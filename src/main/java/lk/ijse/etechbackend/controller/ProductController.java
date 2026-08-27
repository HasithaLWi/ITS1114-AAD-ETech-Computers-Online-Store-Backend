package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.ApiResponse;
import lk.ijse.etechbackend.dto.PageResponseDTO;
import lk.ijse.etechbackend.dto.ProductRequestDTO;
import lk.ijse.etechbackend.dto.ProductResponseDTO;
import lk.ijse.etechbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductResponseDTO>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String badge,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        log.info("REST: Querying products - category: {}, brand: {}, search: {}, page: {}, size: {}",
                category, brand, search, page, size);

        PageResponseDTO<ProductResponseDTO> response = productService.getAllProducts(
                category, brand, search, minPrice, maxPrice, badge, page, size, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        log.info("REST: Fetching product by ID: {}", id);
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponseDTO> getProductBySku(@PathVariable String sku) {
        log.info("REST: Fetching product by SKU: {}", sku);
        ProductResponseDTO product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        log.info("REST: Creating new product SKU: {}", request.getSku());
        ProductResponseDTO createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        log.info("REST: Updating product ID: {}", id);
        ProductResponseDTO updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        log.info("REST: Deleting product ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product removed"));
    }
}
