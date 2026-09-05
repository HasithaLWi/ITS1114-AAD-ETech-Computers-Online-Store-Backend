package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.behavior.ProductBehaviorHistoryRequestDTO;
import lk.ijse.etechbackend.service.ProductBehaviorHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductBehaviorHistoryController {

    private final ProductBehaviorHistoryService behaviorHistoryService;

    @GetMapping(value = "/api/v1/product-behavior-history", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> getAllBehaviorHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        log.info("REST: Fetching all product behavior history logs");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Product behavior history retrieved successfully")
                .body(behaviorHistoryService.getGlobalHistory(page, size))
                .build());
    }

    @GetMapping(value = "/api/v1/products/{productId}/behavior-history", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> getProductBehaviorHistory(@PathVariable Long productId) {
        log.info("REST: Fetching behavior history for product ID: {}", productId);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Product behavior history retrieved successfully")
                .body(behaviorHistoryService.getProductHistory(productId))
                .build());
    }

    @PostMapping(value = "/api/v1/product-behavior-history", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> recordBehaviorHistory(@Valid @RequestBody ProductBehaviorHistoryRequestDTO request) {
        log.info("REST: Recording product behavior history for product ID: {}", request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Product behavior history recorded successfully")
                .body(behaviorHistoryService.logEvent(request))
                .build());
    }
}
