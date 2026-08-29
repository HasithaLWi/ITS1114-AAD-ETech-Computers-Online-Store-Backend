package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.PageResponseDTO;
import lk.ijse.etechbackend.dto.ProductRequestDTO;
import lk.ijse.etechbackend.dto.ProductResponseDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> getAllProducts();

//    PageResponseDTO<ProductResponseDTO> getAllProducts(
//            String category,
//            String brand,
//            String search,
//            BigDecimal minPrice,
//            BigDecimal maxPrice,
//            String badge,
//            int page,
//            int size,
//            String sortBy,
//            String sortDirection
//    );
//
//    ProductResponseDTO getProductById(Long id);
//
//    ProductResponseDTO getProductBySku(String sku);
//
//    ProductResponseDTO createProduct(ProductRequestDTO request);
//
//    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);
//
//    void deleteProduct(Long id);
}
