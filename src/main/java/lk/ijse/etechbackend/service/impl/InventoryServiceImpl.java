package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.inventory.*;
import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lk.ijse.etechbackend.entity.Branch;
import lk.ijse.etechbackend.entity.BranchInventory;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.BranchInventoryRepository;
import lk.ijse.etechbackend.repository.BranchRepository;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.service.InventoryService;
import lk.ijse.etechbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final ProductService productService;

    @Override
    @Transactional(readOnly = true)
    public StockHealthReportDTO getHealthReport(String branchId) {
        log.info("Generating inventory health report (branchId={})", branchId);

        List<Product> products = productRepository.findAll();
        List<Branch> branches = branchRepository.findAll();

        long totalMonitored = 0;
        long depletedCount = 0;
        long lowStockCount = 0;
        List<StockAlertDTO> alerts = new ArrayList<>();

        Map<String, BranchHealthDTO> branchHealthMap = new HashMap<>();
        for (Branch b : branches) {
            branchHealthMap.put(b.getId(), BranchHealthDTO.builder()
                    .branchId(b.getId())
                    .branchName(b.getName())
                    .totalUnits(0)
                    .lowStockCount(0)
                    .depletedCount(0)
                    .healthScore(BigDecimal.valueOf(100.0))
                    .build());
        }

        for (Product p : products) {
            if (p.getProductStatus() == Status.DELETED) continue;
            totalMonitored++;

            Map<String, Integer> stockMap = new HashMap<>();
            int totalProductStock = 0;

            if (p.getBranchInventories() != null) {
                for (BranchInventory bi : p.getBranchInventories()) {
                    int qty = bi.getQuantity() != null ? bi.getQuantity() : 0;
                    stockMap.put(bi.getBranch().getId(), qty);
                    totalProductStock += qty;

                    BranchHealthDTO bh = branchHealthMap.get(bi.getBranch().getId());
                    if (bh != null) {
                        bh.setTotalUnits(bh.getTotalUnits() + qty);
                        if (qty == 0) {
                            bh.setDepletedCount(bh.getDepletedCount() + 1);
                        } else if (p.getAlertEnabled() && qty <= p.getLowStockMargin()) {
                            bh.setLowStockCount(bh.getLowStockCount() + 1);
                        }
                    }
                }
            }

            int margin = p.getLowStockMargin() != null ? p.getLowStockMargin() : 5;
            boolean isMonitored = Boolean.TRUE.equals(p.getAlertEnabled());

            // If filtering by branch, evaluate for that branch
            int effectiveStock = (branchId != null && !branchId.isBlank())
                    ? stockMap.getOrDefault(branchId, 0)
                    : totalProductStock;

            if (effectiveStock == 0) {
                depletedCount++;
                alerts.add(StockAlertDTO.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .productSku(p.getSku())
                        .category(p.getCategory() != null ? p.getCategory().getName() : "")
                        .alertType("DEPLETED")
                        .totalStock(effectiveStock)
                        .lowStockMargin(margin)
                        .branchStock(stockMap)
                        .build());
            } else if (isMonitored && effectiveStock <= margin) {
                lowStockCount++;
                alerts.add(StockAlertDTO.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .productSku(p.getSku())
                        .category(p.getCategory() != null ? p.getCategory().getName() : "")
                        .alertType("LOW_STOCK")
                        .totalStock(effectiveStock)
                        .lowStockMargin(margin)
                        .branchStock(stockMap)
                        .build());
            }
        }

        // Calculate health scores per branch
        for (BranchHealthDTO bh : branchHealthMap.values()) {
            if (totalMonitored > 0) {
                double healthyRatio = (double)(totalMonitored - bh.getDepletedCount() - (bh.getLowStockCount() * 0.5)) / totalMonitored;
                double score = Math.max(0.0, Math.min(100.0, healthyRatio * 100.0));
                bh.setHealthScore(BigDecimal.valueOf(score).setScale(1, RoundingMode.HALF_UP));
            }
        }

        return StockHealthReportDTO.builder()
                .totalMonitored(totalMonitored)
                .depletedCount(depletedCount)
                .lowStockCount(lowStockCount)
                .branchHealth(branchHealthMap)
                .alerts(alerts)
                .build();
    }

    @Override
    public ProductResponseDTO updateInventorySettings(Long productId, ProductInventorySettingsDTO request) {
        log.info("Updating inventory settings for product ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        if (request.getAlertEnabled() != null) {
            product.setAlertEnabled(request.getAlertEnabled());
        }
        if (request.getLowStockMargin() != null) {
            product.setLowStockMargin(request.getLowStockMargin());
        }

        productRepository.save(product);
        return productService.getProductById(productId);
    }

    @Override
    public Map<String, Integer> adjustInventory(Long productId, InventoryAdjustRequestDTO request) {
        log.info("Adjusting inventory for product ID {} in branch {} with delta: {}",
                productId, request.getBranchId(), request.getQuantityDelta());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + request.getBranchId()));

        BranchInventory inventory = branchInventoryRepository.findByProductIdAndBranchId(productId, request.getBranchId())
                .orElseGet(() -> BranchInventory.builder()
                        .product(product)
                        .branch(branch)
                        .quantity(0)
                        .build());

        int newQuantity = inventory.getQuantity() + request.getQuantityDelta();
        if (newQuantity < 0) {
            throw new BadRequestException("Inventory quantity cannot be reduced below 0. Current: " + inventory.getQuantity() + ", Delta: " + request.getQuantityDelta());
        }

        inventory.setQuantity(newQuantity);
        branchInventoryRepository.save(inventory);

        // Fetch refreshed stock map
        Map<String, Integer> stockMap = new HashMap<>();
        List<BranchInventory> allInventories = branchInventoryRepository.findByProductId(productId);
        for (BranchInventory bi : allInventories) {
            stockMap.put(bi.getBranch().getId(), bi.getQuantity());
        }

        log.info("Product ID {} inventory in {} updated to {}", productId, request.getBranchId(), newQuantity);
        return stockMap;
    }
}
