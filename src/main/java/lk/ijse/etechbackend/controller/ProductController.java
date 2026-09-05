package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.*;
import lk.ijse.etechbackend.dto.productsdto.ProductRequestDTO;
import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lk.ijse.etechbackend.dto.productsdto.UpdateInventory;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

        private final ProductService productService;

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/all")
        public ResponseEntity<CommonResponse> getAllProducts() {
                List<ProductResponseDTO> productResponseDTOS = productService.getAllProducts();
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Products retrieved successfully")
                                .body(productResponseDTOS)
                                .build());
        }

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/filter")
        public ResponseEntity<CommonResponse> getAllProducts(
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

                List<ProductResponseDTO> response = productService.getFilteredProducts(
                                category, brand, search, minPrice, maxPrice, badge, page, size, sortBy, sortDir);

                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Products retrieved successfully")
                                .body(response)
                                .build());
        }

        @GetMapping("/{id}")
        public ResponseEntity<CommonResponse> getProductById(@PathVariable Long id) {
                log.info("REST: Fetching product by ID: {}", id);
                ProductResponseDTO product = productService.getProductById(id);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Product retrieved by Id, successfully")
                                .body(product)
                                .build());
        }

        @GetMapping("/sku/{sku}")
        public ResponseEntity<CommonResponse> getProductBySku(@PathVariable String sku) {
                log.info("REST: Fetching product by SKU: {}", sku);
                ProductResponseDTO product = productService.getProductBySku(sku);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Product retrieved by SKU, successfully")
                                .body(product)
                                .build());
        }

        @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/status")
        public ResponseEntity<CommonResponse> getProductsByStatus(@RequestParam String status) {
                log.info("REST: Fetching products by status: {}", status);
                List<ProductResponseDTO> products = productService.getByProductStatus(status);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Products retrieved by status, successfully")
                                .body(products)
                                .build());
        }

        @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/create")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
        public ResponseEntity<CommonResponse> createProduct(@Valid @RequestBody ProductRequestDTO request) {
                log.info("REST: Creating new product SKU: {}", request.getSku());
                ProductResponseDTO createdProduct = productService.createProduct(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Product created successfully")
                                .build());
        }

        @PutMapping("/update/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
        public ResponseEntity<CommonResponse> updateProduct(
                        @PathVariable Long id,
                        @RequestBody ProductRequestDTO request) {
                log.info("REST: Updating product ID: {}", id);
                ProductResponseDTO updatedProduct = productService.updateProduct(id, request);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Product updated successfully")
                                .body(updatedProduct)
                                .build());
        }

        @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update-inventory")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
        public ResponseEntity<CommonResponse> updateBranchInventory(@RequestBody UpdateInventory updateInventory) {
                log.info("REST: Updating branch inventory for product ID: {}", updateInventory.getProductId());
                Map<String, Integer> updatedInventory = productService.updateBranchInventory(updateInventory);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Branch inventory updated successfully")
                                .body(updatedInventory)
                                .build());
        }

        @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update-status/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
        public ResponseEntity<CommonResponse> updateProductStatus(
                        @PathVariable Long id,
                        @RequestParam Status status) {
                productService.updateProductStatus(id, status);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Product status updated successfully")
                                .build());
        }

        @DeleteMapping("/delete/{id}")
        @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
        public ResponseEntity<CommonResponse> deleteProduct(@PathVariable Long id) {
                log.info("REST: Deleting product ID: {}", id);
                productService.deleteProduct(id);
                return ResponseEntity.ok(CommonResponse.builder()
                                .status(HttpStatus.OK.value())
                                .message("Product removed")
                                .build());
        }
}
