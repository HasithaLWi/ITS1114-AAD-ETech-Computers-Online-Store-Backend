package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.order.*;
import lk.ijse.etechbackend.entity.*;
import lk.ijse.etechbackend.enumiration.OrderStatus;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.*;
import lk.ijse.etechbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders(OrderStatus status, String branchId) {
        log.info("Fetching orders (status={}, branchId={})", status, branchId);
        List<Order> orders;

        if (status != null && branchId != null && !branchId.isBlank()) {
            orders = orderRepository.findByStatus(status).stream()
                    .filter(o -> o.getFulfillmentBranch() != null && branchId.equals(o.getFulfillmentBranch().getId()))
                    .collect(Collectors.toList());
        } else if (status != null) {
            orders = orderRepository.findByStatus(status);
        } else if (branchId != null && !branchId.isBlank()) {
            orders = orderRepository.findByFulfillmentBranchId(branchId);
        } else {
            orders = orderRepository.findAll();
        }

        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getMyOrders(String username) {
        log.info("Fetching orders for customer: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderByCode(String orderCode) {
        log.info("Fetching order by code: {}", orderCode);
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
        return toDTO(order);
    }

    @Override
    public OrderResponseDTO createOrder(String currentUsernameOrNull, OrderCreateRequestDTO request) {
        log.info("Placing new order for customer: {}", request.getCustomerName());

        Branch branch = branchRepository.findById(request.getFulfillmentBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Fulfillment branch not found: " + request.getFulfillmentBranchId()));

        User user = null;
        if (currentUsernameOrNull != null && !currentUsernameOrNull.isBlank()) {
            user = userRepository.findByUsername(currentUsernameOrNull).orElse(null);
        }

        String orderCode = "ORD-2026-" + (int)(Math.random() * 9000 + 1000);

        BigDecimal distanceKm = request.getDistanceKm() != null ? request.getDistanceKm() : BigDecimal.valueOf(5.0);
        BigDecimal baseRate = branch.getBaseShippingRate() != null ? branch.getBaseShippingRate() : new BigDecimal("350.00");
        BigDecimal distanceCharge = distanceKm.multiply(BigDecimal.valueOf(15)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shippingFee = baseRate.add(distanceCharge).setScale(2, RoundingMode.HALF_UP);

        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .customerName(request.getCustomerName().trim())
                .customerEmail(request.getCustomerEmail().trim())
                .customerPhone(request.getCustomerPhone().trim())
                .shippingAddress(request.getShippingAddress().trim())
                .city(request.getCity().trim())
                .fulfillmentBranch(branch)
                .distanceKm(distanceKm)
                .subtotal(BigDecimal.ZERO)
                .shippingFee(shippingFee)
                .tax(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.Pending)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "Credit / Debit Card")
                .build();

        for (OrderItemRequestDTO itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemReq.getProductId()));

            int reqQty = itemReq.getQuantity();

            // Check stock in fulfillment branch
            BranchInventory inventory = branchInventoryRepository.findByProductIdAndBranchId(product.getId(), branch.getId())
                    .orElseThrow(() -> new BadRequestException("Product " + product.getName() + " is not available at branch " + branch.getName()));

            if (inventory.getQuantity() < reqQty) {
                throw new BadRequestException("Insufficient stock for " + product.getName() + " at " + branch.getName() +
                        ". Available: " + inventory.getQuantity() + ", Requested: " + reqQty);
            }

            // Deduct stock atomically
            inventory.setQuantity(inventory.getQuantity() - reqQty);
            branchInventoryRepository.save(inventory);

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(reqQty));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .unitPrice(product.getPrice())
                    .quantity(reqQty)
                    .totalPrice(itemTotal)
                    .build();

            orderItems.add(orderItem);
        }

        BigDecimal totalAmount = subtotal.add(shippingFee);

        order.setSubtotal(subtotal);
        order.setTotalAmount(totalAmount);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        log.info("Successfully created order code: {} (total: {})", savedOrder.getOrderCode(), savedOrder.getTotalAmount());
        return toDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatusUpdateDTO request) {
        log.info("Updating order ID {} status to: {}", orderId, request.getStatus());
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (oldStatus == newStatus) {
            return toDTO(order);
        }

        // If transitioning to Cancelled and wasn't previously Cancelled, restore stock
        if (newStatus == OrderStatus.Cancelled && oldStatus != OrderStatus.Cancelled) {
            Branch branch = order.getFulfillmentBranch();
            if (branch != null && order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProduct() != null) {
                        BranchInventory inventory = branchInventoryRepository.findByProductIdAndBranchId(item.getProduct().getId(), branch.getId())
                                .orElseGet(() -> BranchInventory.builder()
                                        .product(item.getProduct())
                                        .branch(branch)
                                        .quantity(0)
                                        .build());
                        inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
                        branchInventoryRepository.save(inventory);
                    }
                }
            }
        }

        order.setStatus(newStatus);
        Order saved = orderRepository.save(order);
        return toDTO(saved);
    }

    private OrderResponseDTO toDTO(Order o) {
        List<OrderItemResponseDTO> itemDTOs = new ArrayList<>();
        if (o.getItems() != null) {
            for (OrderItem item : o.getItems()) {
                String image = null;
                if (item.getProduct() != null && item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
                    image = item.getProduct().getImages().get(0).getImageUrl();
                }
                itemDTOs.add(OrderItemResponseDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .productSku(item.getProductSku())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .image(image)
                        .build());
            }
        }

        return OrderResponseDTO.builder()
                .id(o.getId())
                .orderCode(o.getOrderCode())
                .userId(o.getUser() != null ? o.getUser().getId() : null)
                .customerName(o.getCustomerName())
                .customerEmail(o.getCustomerEmail())
                .customerPhone(o.getCustomerPhone())
                .shippingAddress(o.getShippingAddress())
                .city(o.getCity())
                .fulfillmentBranchId(o.getFulfillmentBranch() != null ? o.getFulfillmentBranch().getId() : null)
                .fulfillmentBranchName(o.getFulfillmentBranch() != null ? o.getFulfillmentBranch().getName() : null)
                .distanceKm(o.getDistanceKm())
                .subtotal(o.getSubtotal())
                .shippingFee(o.getShippingFee())
                .tax(o.getTax())
                .totalAmount(o.getTotalAmount())
                .status(o.getStatus())
                .paymentMethod(o.getPaymentMethod())
                .items(itemDTOs)
                .orderDate(o.getOrderDate())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}
