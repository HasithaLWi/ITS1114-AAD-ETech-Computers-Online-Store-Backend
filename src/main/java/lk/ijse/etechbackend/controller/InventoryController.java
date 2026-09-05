package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.inventory.InventoryAdjustRequestDTO;
import lk.ijse.etechbackend.dto.inventory.ProductInventorySettingsDTO;
import lk.ijse.etechbackend.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping(value = "/health-report", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> getStockHealthReport(
            @RequestParam(required = false) String branchId) {
        log.info("REST: Fetching stock health report, branch filter: {}", branchId);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Stock health report retrieved successfully")
                .body(inventoryService.getHealthReport(branchId))
                .build());
    }

    @PatchMapping(value = "/{productId}/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateInventorySettings(
            @PathVariable Long productId,
            @Valid @RequestBody ProductInventorySettingsDTO request) {
        log.info("REST: Updating inventory settings for product ID: {}", productId);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Inventory settings updated successfully")
                .body(inventoryService.updateInventorySettings(productId, request))
                .build());
    }

    @PostMapping(value = "/{productId}/adjust", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> adjustInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryAdjustRequestDTO request) {
        log.info("REST: Adjusting inventory for product ID: {} in branch: {}", productId, request.getBranchId());
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Inventory adjusted successfully")
                .body(inventoryService.adjustInventory(productId, request))
                .build());
    }
}
