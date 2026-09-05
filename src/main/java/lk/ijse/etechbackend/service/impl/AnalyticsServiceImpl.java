package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.analytics.AnalyticsOverviewDTO;
import lk.ijse.etechbackend.dto.analytics.BranchRevenueDTO;
import lk.ijse.etechbackend.dto.analytics.TopProductDTO;
import lk.ijse.etechbackend.entity.Branch;
import lk.ijse.etechbackend.entity.Order;
import lk.ijse.etechbackend.entity.OrderItem;
import lk.ijse.etechbackend.enumiration.OrderStatus;
import lk.ijse.etechbackend.repository.BranchRepository;
import lk.ijse.etechbackend.repository.OrderItemRepository;
import lk.ijse.etechbackend.repository.OrderRepository;
import lk.ijse.etechbackend.repository.UserRepository;
import lk.ijse.etechbackend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;

    @Override
    public AnalyticsOverviewDTO getOverview() {
        log.info("Calculating financial analytics overview");
        BigDecimal grossRevenue = orderRepository.calculateGrossRevenue();
        if (grossRevenue == null) grossRevenue = BigDecimal.ZERO;

        Long totalOrdersCount = orderRepository.countValidOrders();
        long totalOrders = totalOrdersCount != null ? totalOrdersCount : 0L;

        BigDecimal avgOrderValue = totalOrders > 0
                ? grossRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long activeUsers = userRepository.count();

        return AnalyticsOverviewDTO.builder()
                .grossRevenue(grossRevenue)
                .totalOrders(totalOrders)
                .avgOrderValue(avgOrderValue)
                .activeUsers(activeUsers)
                .build();
    }

    @Override
    public List<BranchRevenueDTO> getBranchRevenue() {
        log.info("Calculating branch revenue breakdown");
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() != OrderStatus.Cancelled)
                .collect(Collectors.toList());

        List<Branch> branches = branchRepository.findAll();
        BigDecimal totalGross = orderRepository.calculateGrossRevenue();
        if (totalGross == null || totalGross.compareTo(BigDecimal.ZERO) == 0) {
            totalGross = BigDecimal.ONE; // prevent div by zero
        }

        Map<String, Long> countMap = new HashMap<>();
        Map<String, BigDecimal> revMap = new HashMap<>();

        for (Branch b : branches) {
            countMap.put(b.getId(), 0L);
            revMap.put(b.getId(), BigDecimal.ZERO);
        }

        for (Order o : orders) {
            if (o.getFulfillmentBranch() != null) {
                String bid = o.getFulfillmentBranch().getId();
                countMap.put(bid, countMap.getOrDefault(bid, 0L) + 1);
                revMap.put(bid, revMap.getOrDefault(bid, BigDecimal.ZERO).add(o.getTotalAmount()));
            }
        }

        List<BranchRevenueDTO> result = new ArrayList<>();
        for (Branch b : branches) {
            BigDecimal rev = revMap.getOrDefault(b.getId(), BigDecimal.ZERO);
            long count = countMap.getOrDefault(b.getId(), 0L);
            BigDecimal pct = rev.multiply(BigDecimal.valueOf(100)).divide(totalGross, 1, RoundingMode.HALF_UP);

            result.add(BranchRevenueDTO.builder()
                    .branchId(b.getId())
                    .branchName(b.getName())
                    .orderCount(count)
                    .revenue(rev)
                    .percentage(pct)
                    .build());
        }

        return result;
    }

    @Override
    public List<TopProductDTO> getTopProducts(int limit) {
        log.info("Fetching top selling products (limit={})", limit);
        List<OrderItem> items = orderItemRepository.findAll();

        Map<Long, String> nameMap = new HashMap<>();
        Map<Long, Long> unitsMap = new HashMap<>();
        Map<Long, BigDecimal> revMap = new HashMap<>();

        for (OrderItem item : items) {
            if (item.getProduct() != null && item.getOrder() != null && item.getOrder().getStatus() != OrderStatus.Cancelled) {
                Long pid = item.getProduct().getId();
                nameMap.put(pid, item.getProductName());
                unitsMap.put(pid, unitsMap.getOrDefault(pid, 0L) + item.getQuantity());
                revMap.put(pid, revMap.getOrDefault(pid, BigDecimal.ZERO).add(item.getTotalPrice()));
            }
        }

        return unitsMap.entrySet().stream()
                .map(e -> TopProductDTO.builder()
                        .productId(e.getKey())
                        .name(nameMap.get(e.getKey()))
                        .unitsSold(e.getValue())
                        .revenue(revMap.getOrDefault(e.getKey(), BigDecimal.ZERO))
                        .build())
                .sorted(Comparator.comparing(TopProductDTO::getUnitsSold).reversed())
                .limit(limit > 0 ? limit : 5)
                .collect(Collectors.toList());
    }
}
