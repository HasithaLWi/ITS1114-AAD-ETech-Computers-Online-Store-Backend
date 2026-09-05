package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.productsdto.ProductRequestDTO;
import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lk.ijse.etechbackend.dto.productsdto.UpdateInventory;
import lk.ijse.etechbackend.enumiration.Status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {

    List<ProductResponseDTO> getAllProducts();
    List<ProductResponseDTO> getFilteredProducts(String category,
                                                 String brand,
                                                 String search,
                                                 BigDecimal minPrice,
                                                 BigDecimal maxPrice,
                                                 String badge,
                                                 int page,
                                                 int size,
                                                 String sortBy,
                                                 String sortDirection);

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO getProductBySku(String sku);

    ProductResponseDTO createProduct(ProductRequestDTO request);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO request);

    Map<String, Integer> updateBranchInventory(UpdateInventory updateInventory);

    void updateProductStatus(Long id, Status status);

    void deleteProduct(Long id);
}
