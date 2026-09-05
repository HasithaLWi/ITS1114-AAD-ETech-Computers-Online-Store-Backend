package lk.ijse.etechbackend.controller;

import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> getAnalyticsOverview() {
        log.info("REST: Fetching analytics overview");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Analytics overview retrieved successfully")
                .body(analyticsService.getOverview())
                .build());
    }

    @GetMapping(value = "/branch-revenue", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> getBranchRevenue() {
        log.info("REST: Fetching branch revenue breakdown");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Branch revenue breakdown retrieved successfully")
                .body(analyticsService.getBranchRevenue())
                .build());
    }

    @GetMapping(value = "/top-products", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("REST: Fetching top {} products", limit);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Top products retrieved successfully")
                .body(analyticsService.getTopProducts(limit))
                .build());
    }
}
