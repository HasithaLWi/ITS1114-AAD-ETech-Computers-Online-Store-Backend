package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.transfer.StockTransferRequestDTO;
import lk.ijse.etechbackend.dto.transfer.TransferStatusUpdateDTO;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;
import lk.ijse.etechbackend.service.TransferService;
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
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> getAllTransfers(
            @RequestParam(required = false) StockTransferStatus status,
            @RequestParam(required = false) String branchId) {
        log.info("REST: Fetching transfers with status: {}, branch: {}", status, branchId);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Stock transfers retrieved successfully")
                .body(transferService.getTransfers(status, branchId))
                .build());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> getTransferById(@PathVariable String id) {
        log.info("REST: Fetching transfer by ID: {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Stock transfer retrieved successfully")
                .body(transferService.getTransferById(id))
                .build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> createTransfer(@Valid @RequestBody StockTransferRequestDTO request,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Creating stock transfer by user {} from {} to {}", username, request.getFromBranchId(), request.getToBranchId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Stock transfer created successfully")
                .body(transferService.initiateTransfer(username, request))
                .build());
    }

    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> updateTransferStatus(
            @PathVariable String id,
            @Valid @RequestBody TransferStatusUpdateDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        log.info("REST: Updating transfer ID {} status to {} by user {}", id, request.getStatus(), username);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Stock transfer status updated successfully")
                .body(transferService.updateTransferStatus(username, id, request))
                .build());
    }

    @GetMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> getTransferMetrics() {
        log.info("REST: Fetching stock transfer metrics");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Transfer metrics retrieved successfully")
                .body(transferService.getTransferMetrics())
                .build());
    }
}
