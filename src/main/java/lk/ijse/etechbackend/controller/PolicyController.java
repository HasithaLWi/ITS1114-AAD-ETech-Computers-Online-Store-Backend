package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.profile.LegalPolicyDTO;
import lk.ijse.etechbackend.service.LegalPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final LegalPolicyService legalPolicyService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getAllPolicies() {
        log.info("REST: Fetching all legal policies");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Legal policies retrieved successfully")
                .body(legalPolicyService.getAllPolicies())
                .build());
    }

    @GetMapping(value = "/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse> getPolicyBySlug(@PathVariable String slug) {
        log.info("REST: Fetching legal policy by slug: {}", slug);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Legal policy retrieved successfully")
                .body(legalPolicyService.getPolicyBySlug(slug))
                .build());
    }

    @PutMapping(value = "/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updatePolicy(@PathVariable String slug,
                                                       @Valid @RequestBody LegalPolicyDTO request) {
        log.info("REST: Updating legal policy with slug: {}", slug);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Legal policy updated successfully")
                .body(legalPolicyService.updatePolicy(slug, request))
                .build());
    }
}
