package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.wishlist.MoveToCartRequestDTO;
import lk.ijse.etechbackend.dto.wishlist.MoveToCartResponseDTO;
import lk.ijse.etechbackend.dto.wishlist.WishlistActionResponseDTO;
import lk.ijse.etechbackend.dto.wishlist.WishlistResponseDTO;

public interface WishlistService {
    WishlistResponseDTO getWishlist(String username);
    WishlistActionResponseDTO toggleWishlist(String username, Long productId);
    WishlistActionResponseDTO addToWishlist(String username, Long productId);
    WishlistActionResponseDTO removeFromWishlist(String username, Long productId);
    WishlistActionResponseDTO clearWishlist(String username);
    MoveToCartResponseDTO moveToCart(String username, MoveToCartRequestDTO request);
}
