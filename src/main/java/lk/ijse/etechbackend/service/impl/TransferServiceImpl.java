package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.transfer.StockTransferRequestDTO;
import lk.ijse.etechbackend.dto.transfer.StockTransferResponseDTO;
import lk.ijse.etechbackend.dto.transfer.TransferMetricsDTO;
import lk.ijse.etechbackend.dto.transfer.TransferStatusUpdateDTO;
import lk.ijse.etechbackend.entity.Branch;
import lk.ijse.etechbackend.entity.BranchInventory;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.entity.StockTransfer;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.BranchInventoryRepository;
import lk.ijse.etechbackend.repository.BranchRepository;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.repository.StockTransferRepository;
import lk.ijse.etechbackend.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TransferServiceImpl implements TransferService {

    private final StockTransferRepository transferRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final BranchInventoryRepository branchInventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockTransferResponseDTO> getTransfers(StockTransferStatus status, String branchId) {
        log.info("Fetching transfers with status: {}, branch: {}", status, branchId);
        List<StockTransfer> list;

        if (status != null && branchId != null && !branchId.isBlank()) {
            list = transferRepository.findByStatus(status).stream()
                    .filter(t -> branchId.equals(t.getFromBranch().getId()) || branchId.equals(t.getToBranch().getId()))
                    .collect(Collectors.toList());
        } else if (status != null) {
            list = transferRepository.findByStatus(status);
        } else if (branchId != null && !branchId.isBlank()) {
            list = transferRepository.findByFromBranchIdOrToBranchId(branchId, branchId);
        } else {
            list = transferRepository.findAll();
        }

        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StockTransferResponseDTO getTransferById(String id) {
        log.info("Fetching transfer by ID: {}", id);
        StockTransfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with ID: " + id));
        return toDTO(transfer);
    }

    @Override
    public StockTransferResponseDTO initiateTransfer(String username, StockTransferRequestDTO request) {
        log.info("Initiating transfer by user {}: Product {}, From {}, To {}, Qty {}",
                username, request.getProductId(), request.getFromBranchId(), request.getToBranchId(), request.getQuantity());

        if (request.getFromBranchId().equalsIgnoreCase(request.getToBranchId())) {
            throw new BadRequestException("Source and destination branches cannot be the same");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        Branch fromBranch = branchRepository.findById(request.getFromBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Source branch not found: " + request.getFromBranchId()));

        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination branch not found: " + request.getToBranchId()));

        BranchInventory fromInventory = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), fromBranch.getId())
                .orElseThrow(() -> new BadRequestException("No inventory record for product in branch " + fromBranch.getId()));

        if (fromInventory.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Insufficient stock in source branch " + fromBranch.getId() +
                    ". Available: " + fromInventory.getQuantity() + ", Requested: " + request.getQuantity());
        }

        String transferId = "TRF-2026-" + String.format("%03d", (transferRepository.count() + 1));

        StockTransfer transfer = StockTransfer.builder()
                .id(transferId)
                .product(product)
                .fromBranch(fromBranch)
                .toBranch(toBranch)
                .quantity(request.getQuantity())
                .status(StockTransferStatus.PENDING)
                .reason(request.getReason())
                .initiatedBy(username != null ? username : "System")
                .notes(request.getNotes())
                .build();

        StockTransfer saved = transferRepository.save(transfer);
        log.info("Successfully created transfer ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public StockTransferResponseDTO updateTransferStatus(String username, String transferId, TransferStatusUpdateDTO request) {
        log.info("Updating transfer {} status to {} by {}", transferId, request.getStatus(), username);
        StockTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found with ID: " + transferId));

        StockTransferStatus oldStatus = transfer.getStatus();
        StockTransferStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return toDTO(transfer);
        }

        if (oldStatus == StockTransferStatus.RECEIVED || oldStatus == StockTransferStatus.CANCELLED) {
            throw new BadRequestException("Cannot change status of a " + oldStatus + " transfer");
        }

        Product product = transfer.getProduct();
        Branch fromBranch = transfer.getFromBranch();
        Branch toBranch = transfer.getToBranch();
        int qty = transfer.getQuantity();

        // State Machine transitions:
        // 1. PENDING -> IN_TRANSIT: Deduct from fromBranch
        if (oldStatus == StockTransferStatus.PENDING && newStatus == StockTransferStatus.IN_TRANSIT) {
            BranchInventory fromInv = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), fromBranch.getId())
                    .orElseThrow(() -> new BadRequestException("Source inventory not found"));
            if (fromInv.getQuantity() < qty) {
                throw new BadRequestException("Insufficient stock in source branch to dispatch. Available: " + fromInv.getQuantity());
            }
            fromInv.setQuantity(fromInv.getQuantity() - qty);
            branchInventoryRepository.save(fromInv);
            transfer.setDispatchedAt(LocalDateTime.now());
        }
        // 2. IN_TRANSIT -> RECEIVED: Add to toBranch
        else if (oldStatus == StockTransferStatus.IN_TRANSIT && newStatus == StockTransferStatus.RECEIVED) {
            BranchInventory toInv = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), toBranch.getId())
                    .orElseGet(() -> BranchInventory.builder()
                            .product(product)
                            .branch(toBranch)
                            .quantity(0)
                            .build());
            toInv.setQuantity(toInv.getQuantity() + qty);
            branchInventoryRepository.save(toInv);
            transfer.setReceivedAt(LocalDateTime.now());
        }
        // 3. PENDING -> RECEIVED: Direct atomic transfer (fromBranch -> toBranch)
        else if (oldStatus == StockTransferStatus.PENDING && newStatus == StockTransferStatus.RECEIVED) {
            BranchInventory fromInv = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), fromBranch.getId())
                    .orElseThrow(() -> new BadRequestException("Source inventory not found"));
            if (fromInv.getQuantity() < qty) {
                throw new BadRequestException("Insufficient stock in source branch to receive. Available: " + fromInv.getQuantity());
            }
            fromInv.setQuantity(fromInv.getQuantity() - qty);
            branchInventoryRepository.save(fromInv);

            BranchInventory toInv = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), toBranch.getId())
                    .orElseGet(() -> BranchInventory.builder()
                            .product(product)
                            .branch(toBranch)
                            .quantity(0)
                            .build());
            toInv.setQuantity(toInv.getQuantity() + qty);
            branchInventoryRepository.save(toInv);

            transfer.setDispatchedAt(LocalDateTime.now());
            transfer.setReceivedAt(LocalDateTime.now());
        }
        // 4. IN_TRANSIT -> CANCELLED: Restore back to fromBranch
        else if (oldStatus == StockTransferStatus.IN_TRANSIT && newStatus == StockTransferStatus.CANCELLED) {
            BranchInventory fromInv = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), fromBranch.getId())
                    .orElseGet(() -> BranchInventory.builder()
                            .product(product)
                            .branch(fromBranch)
                            .quantity(0)
                            .build());
            fromInv.setQuantity(fromInv.getQuantity() + qty);
            branchInventoryRepository.save(fromInv);
            transfer.setCancelledAt(LocalDateTime.now());
        }
        // 5. PENDING -> CANCELLED: Simple cancel
        else if (oldStatus == StockTransferStatus.PENDING && newStatus == StockTransferStatus.CANCELLED) {
            transfer.setCancelledAt(LocalDateTime.now());
        }

        transfer.setStatus(newStatus);
        StockTransfer saved = transferRepository.save(transfer);
        log.info("Transfer {} updated to {}", transferId, newStatus);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferMetricsDTO getTransferMetrics() {
        long pending = transferRepository.countByStatus(StockTransferStatus.PENDING);
        long inTransit = transferRepository.countByStatus(StockTransferStatus.IN_TRANSIT);
        long received = transferRepository.countByStatus(StockTransferStatus.RECEIVED);
        Long totalUnits = transferRepository.calculateTotalUnitsMoved();

        return TransferMetricsDTO.builder()
                .pendingCount(pending)
                .inTransitCount(inTransit)
                .receivedCount(received)
                .totalUnitsMoved(totalUnits != null ? totalUnits : 0L)
                .build();
    }

    private StockTransferResponseDTO toDTO(StockTransfer t) {
        return StockTransferResponseDTO.builder()
                .id(t.getId())
                .productId(t.getProduct() != null ? t.getProduct().getId() : null)
                .productName(t.getProduct() != null ? t.getProduct().getName() : null)
                .productSku(t.getProduct() != null ? t.getProduct().getSku() : null)
                .fromBranchId(t.getFromBranch() != null ? t.getFromBranch().getId() : null)
                .fromBranchName(t.getFromBranch() != null ? t.getFromBranch().getName() : null)
                .toBranchId(t.getToBranch() != null ? t.getToBranch().getId() : null)
                .toBranchName(t.getToBranch() != null ? t.getToBranch().getName() : null)
                .quantity(t.getQuantity())
                .status(t.getStatus())
                .reason(t.getReason())
                .initiatedBy(t.getInitiatedBy())
                .notes(t.getNotes())
                .createdAt(t.getCreatedAt())
                .dispatchedAt(t.getDispatchedAt())
                .receivedAt(t.getReceivedAt())
                .cancelledAt(t.getCancelledAt())
                .build();
    }
}
