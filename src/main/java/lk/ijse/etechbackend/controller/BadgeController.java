package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.badgedto.BadgeRequestDTO;
import lk.ijse.etechbackend.service.BadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> createBadge(@Valid @RequestBody BadgeRequestDTO badgeRequestDTO) {
        log.info("REST: Creating badge - {}", badgeRequestDTO.getName());
        badgeService.createBadge(badgeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Badge created successfully")
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/all")
    public ResponseEntity<CommonResponse> getAllBadges() {
        log.info("REST: Retrieving all badges");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badges retrieved successfully")
                .body(badgeService.findAll())
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/active")
    public ResponseEntity<CommonResponse> getActiveBadges() {
        log.info("REST: Retrieving active badges");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Active badges retrieved successfully")
                .body(badgeService.findActive())
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<CommonResponse> getBadgeById(@PathVariable String id) {
        log.info("REST: Retrieving badge by ID - {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge retrieved successfully")
                .body(badgeService.findById(id))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/slug/{slug}")
    public ResponseEntity<CommonResponse> getBadgeBySlug(@PathVariable String slug) {
        log.info("REST: Retrieving badge by slug - {}", slug);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge retrieved successfully")
                .body(badgeService.findBySlug(slug))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/name/{name}")
    public ResponseEntity<CommonResponse> getBadgeByName(@PathVariable String name) {
        log.info("REST: Retrieving badge by name - {}", name);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge retrieved successfully")
                .body(badgeService.findByName(name))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/filter")
    public ResponseEntity<CommonResponse> filterBadge(@RequestParam String search) {
        log.info("REST: Filtering badges with search - {}", search);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badges filtered successfully")
                .body(badgeService.filterBadge(search))
                .build());
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateBadge(@PathVariable String id, @RequestBody BadgeRequestDTO badgeRequestDTO) {
        log.info("REST: Updating badge - {}", id);
        badgeService.updateBadge(id, badgeRequestDTO);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge updated successfully")
                .build());
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update-status/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateBadgeStatus(@PathVariable String id, @RequestParam String status) {
        log.info("REST: Updating badge status - {} to status: {}", id, status);
        badgeService.updateStatus(id, status);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge status updated successfully")
                .build());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteBadge(@PathVariable String id) {
        log.info("REST: Deleting badge - {}", id);
        badgeService.deleteBadge(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge deleted successfully")
                .build());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/perma-delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public ResponseEntity<CommonResponse> permanentDelete(@PathVariable String id) {
        log.info("REST: Permanently deleting badge - {}", id);
        badgeService.permanentDelete(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badge permanently deleted successfully")
                .build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/auto-assign")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> autoAssignBadges() {
        log.info("REST: Running automated badge assignment rule engine");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Badges auto-assigned successfully")
                .body(badgeService.autoAssignBadges())
                .build());
    }
}
