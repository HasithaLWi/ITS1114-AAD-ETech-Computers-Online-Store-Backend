package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.promotion.*;
import lk.ijse.etechbackend.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    // --- Hot Deals ---

    @GetMapping(value = "/hot-deals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllHotDeals(@RequestParam(required = false) Boolean activeOnly) {
        log.info("REST: Fetching hot deals, activeOnly: {}", activeOnly);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Hot deals retrieved successfully")
                .body(promotionService.getHotDeals(activeOnly))
                .build());
    }

    @PostMapping(value = "/hot-deals", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> createHotDeal(@Valid @RequestBody HotDealRequestDTO request) {
        log.info("REST: Creating hot deal for product ID: {}", request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Hot deal created successfully")
                .body(promotionService.createHotDeal(request))
                .build());
    }

    @PutMapping(value = "/hot-deals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateHotDeal(@PathVariable Long id,
                                                        @Valid @RequestBody HotDealRequestDTO request) {
        log.info("REST: Updating hot deal ID: {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Hot deal updated successfully")
                .body(promotionService.updateHotDeal(id, request))
                .build());
    }

    @DeleteMapping(value = "/hot-deals/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteHotDeal(@PathVariable Long id) {
        log.info("REST: Deleting hot deal ID: {}", id);
        promotionService.deleteHotDeal(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Hot deal deleted successfully")
                .build());
    }

    // --- Home Deal Banner ---

    @GetMapping(value = "/home-banner", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getHomeDealBanner() {
        log.info("REST: Fetching home deal banner");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Home deal banner retrieved successfully")
                .body(promotionService.getHomeBanner())
                .build());
    }

    @PutMapping(value = "/home-banner", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateHomeDealBanner(@Valid @RequestBody HomeDealBannerDTO request) {
        log.info("REST: Updating home deal banner");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Home deal banner updated successfully")
                .body(promotionService.updateHomeBanner(request))
                .build());
    }

    // --- Deal Bundles ---

    @GetMapping(value = "/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllBundles(@RequestParam(required = false) Boolean activeOnly) {
        log.info("REST: Fetching all deal bundles, activeOnly: {}", activeOnly);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deal bundles retrieved successfully")
                .body(promotionService.getBundles(activeOnly))
                .build());
    }

    @GetMapping(value = "/bundles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getBundleById(@PathVariable Long id) {
        log.info("REST: Fetching bundle by ID: {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deal bundle retrieved successfully")
                .body(promotionService.getBundleById(id))
                .build());
    }

    @PostMapping(value = "/bundles", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> createBundle(@Valid @RequestBody DealBundleRequestDTO request) {
        log.info("REST: Creating deal bundle - {}", request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Deal bundle created successfully")
                .body(promotionService.createBundle(request))
                .build());
    }

    @PutMapping(value = "/bundles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateBundle(@PathVariable Long id,
                                                       @Valid @RequestBody DealBundleRequestDTO request) {
        log.info("REST: Updating deal bundle ID: {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deal bundle updated successfully")
                .body(promotionService.updateBundle(id, request))
                .build());
    }

    @DeleteMapping(value = "/bundles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteBundle(@PathVariable Long id) {
        log.info("REST: Deleting deal bundle ID: {}", id);
        promotionService.deleteBundle(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Deal bundle deleted successfully")
                .build());
    }
}
