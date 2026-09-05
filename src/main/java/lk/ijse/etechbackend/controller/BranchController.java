package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.branch.BranchDTO;
import lk.ijse.etechbackend.dto.branch.BranchNearestRequestDTO;
import lk.ijse.etechbackend.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllBranches(@RequestParam(required = false) Boolean activeOnly) {
        log.info("REST: Fetching branches, activeOnly: {}", activeOnly);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Branches retrieved successfully")
                .body(branchService.getAllBranches(activeOnly))
                .build());
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getBranchById(@PathVariable String id) {
        log.info("REST: Fetching branch by ID: {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Branch retrieved successfully")
                .body(branchService.getBranchById(id))
                .build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> createBranch(@Valid @RequestBody BranchDTO branchDTO) {
        log.info("REST: Creating branch - {}", branchDTO.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Branch created successfully")
                .body(branchService.createBranch(branchDTO))
                .build());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateBranch(@PathVariable String id,
                                                       @Valid @RequestBody BranchDTO branchDTO) {
        log.info("REST: Updating branch ID: {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Branch updated successfully")
                .body(branchService.updateBranch(id, branchDTO))
                .build());
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteBranch(@PathVariable String id) {
        log.info("REST: Deleting branch ID: {}", id);
        branchService.deleteBranch(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Branch deleted successfully")
                .build());
    }

    @PostMapping(value = "/nearest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> findNearestBranch(@Valid @RequestBody BranchNearestRequestDTO request) {
        log.info("REST: Finding nearest branch for coords: lat={}, lng={}", request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Nearest branch calculated successfully")
                .body(branchService.findNearestBranch(request))
                .build());
    }
}
