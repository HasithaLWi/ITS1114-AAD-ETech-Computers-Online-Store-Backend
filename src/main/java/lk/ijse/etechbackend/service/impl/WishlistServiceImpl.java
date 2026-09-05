package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.wishlist.*;
import lk.ijse.etechbackend.entity.BranchInventory;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.entity.ProductImage;
import lk.ijse.etechbackend.entity.User;
import lk.ijse.etechbackend.entity.WishlistItem;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.repository.UserRepository;
import lk.ijse.etechbackend.repository.WishlistRepository;
import lk.ijse.etechbackend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public WishlistResponseDTO getWishlist(String username) {
        log.info("Fetching wishlist for user: {}", username);
        User user = getUser(username);
        List<WishlistItem> items = wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<WishlistItemDTO> dtoList = items.stream().map(this::toDTO).collect(Collectors.toList());

        return WishlistResponseDTO.builder()
                .success(true)
                .total(dtoList.size())
                .items(dtoList)
                .build();
    }

    @Override
    public WishlistActionResponseDTO toggleWishlist(String username, Long productId) {
        log.info("Toggling wishlist item {} for user: {}", productId, username);
        User user = getUser(username);
        Product product = getProduct(productId);

        Optional<WishlistItem> existing = wishlistRepository.findByUserIdAndProductId(user.getId(), product.getId());
        boolean added;
        if (existing.isPresent()) {
            wishlistRepository.delete(existing.get());
            added = false;
        } else {
            WishlistItem item = WishlistItem.builder()
                    .user(user)
                    .product(product)
                    .build();
            wishlistRepository.save(item);
            added = true;
        }

        long count = wishlistRepository.countByUserId(user.getId());
        return WishlistActionResponseDTO.builder()
                .success(true)
                .added(added)
                .productId(productId)
                .message(added ? "Added to wishlist" : "Removed from wishlist")
                .wishlistCount(count)
                .build();
    }

    @Override
    public WishlistActionResponseDTO addToWishlist(String username, Long productId) {
        log.info("Adding product {} to wishlist for user: {}", productId, username);
        User user = getUser(username);
        Product product = getProduct(productId);

        if (!wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId())) {
            WishlistItem item = WishlistItem.builder()
                    .user(user)
                    .product(product)
                    .build();
            wishlistRepository.save(item);
        }

        long count = wishlistRepository.countByUserId(user.getId());
        return WishlistActionResponseDTO.builder()
                .success(true)
                .added(true)
                .productId(productId)
                .message("Added to wishlist")
                .wishlistCount(count)
                .build();
    }

    @Override
    public WishlistActionResponseDTO removeFromWishlist(String username, Long productId) {
        log.info("Removing product {} from wishlist for user: {}", productId, username);
        User user = getUser(username);
        wishlistRepository.deleteByUserIdAndProductId(user.getId(), productId);

        long count = wishlistRepository.countByUserId(user.getId());
        return WishlistActionResponseDTO.builder()
                .success(true)
                .added(false)
                .productId(productId)
                .message("Removed from wishlist")
                .wishlistCount(count)
                .build();
    }

    @Override
    public WishlistActionResponseDTO clearWishlist(String username) {
        log.info("Clearing wishlist for user: {}", username);
        User user = getUser(username);
        wishlistRepository.deleteByUserId(user.getId());

        return WishlistActionResponseDTO.builder()
                .success(true)
                .message("Wishlist cleared")
                .wishlistCount(0)
                .build();
    }

    @Override
    public MoveToCartResponseDTO moveToCart(String username, MoveToCartRequestDTO request) {
        log.info("Moving items to cart for user: {}", username);
        User user = getUser(username);
        List<WishlistItem> items = wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        List<WishlistItem> targetItems;
        if (request != null && request.getProductIds() != null && !request.getProductIds().isEmpty()) {
            targetItems = items.stream()
                    .filter(item -> request.getProductIds().contains(item.getProduct().getId()))
                    .collect(Collectors.toList());
        } else {
            targetItems = items;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (WishlistItem item : targetItems) {
            total = total.add(item.getProduct().getPrice());
            wishlistRepository.delete(item);
        }

        return MoveToCartResponseDTO.builder()
                .success(true)
                .movedCount(targetItems.size())
                .cartTotal(total)
                .build();
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    private WishlistItemDTO toDTO(WishlistItem item) {
        Product p = item.getProduct();
        int totalStock = p.getBranchInventories() != null
                ? p.getBranchInventories().stream().mapToInt(BranchInventory::getQuantity).sum()
                : 0;

        String firstImage = p.getImages() != null && !p.getImages().isEmpty()
                ? p.getImages().get(0).getImageUrl()
                : null;

        String categoryName = p.getCategory() != null ? p.getCategory().getName() : "";

        return WishlistItemDTO.builder()
                .id(item.getId())
                .productId(p.getId())
                .name(p.getName())
                .sku(p.getSku())
                .price(p.getPrice())
                .originalPrice(p.getOriginalPrice())
                .image(firstImage)
                .category(categoryName)
                .inStock(totalStock > 0)
                .totalStock(totalStock)
                .savedAt(item.getCreatedAt())
                .build();
    }
}
