package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.wishlist.MoveToCartRequestDTO;
import lk.ijse.etechbackend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> getWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Retrieving wishlist for user - {}", username);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Wishlist retrieved successfully")
                .body(wishlistService.getWishlist(username))
                .build());
    }

    @PostMapping(value = "/toggle/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> toggleWishlist(@PathVariable Long productId,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Toggling product {} in wishlist for user - {}", productId, username);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Wishlist item toggled successfully")
                .body(wishlistService.toggleWishlist(username, productId))
                .build());
    }

    @PostMapping(value = "/add/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> addWishlistItem(@PathVariable Long productId,
                                                          @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Adding product {} to wishlist for user - {}", productId, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Item added to wishlist successfully")
                .body(wishlistService.addToWishlist(username, productId))
                .build());
    }

    @DeleteMapping(value = "/remove/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> removeWishlistItem(@PathVariable Long productId,
                                                             @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Removing product {} from wishlist for user - {}", productId, username);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Item removed from wishlist successfully")
                .body(wishlistService.removeFromWishlist(username, productId))
                .build());
    }

    @DeleteMapping(value = "/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> clearWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Clearing wishlist for user - {}", username);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Wishlist cleared successfully")
                .body(wishlistService.clearWishlist(username))
                .build());
    }

    @PostMapping(value = "/move-to-cart", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse> moveToCart(@Valid @RequestBody MoveToCartRequestDTO request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Moving items to cart for user - {}", username);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Items moved to cart successfully")
                .body(wishlistService.moveToCart(username, request))
                .build());
    }
}
