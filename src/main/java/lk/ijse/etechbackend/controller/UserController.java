package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.*;
import lk.ijse.etechbackend.enumiration.UserRole;
import lk.ijse.etechbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String search) {
        log.info("REST: Fetching user directory by {} (role={}, branch={}, search={})",
                userDetails.getUsername(), role, branch, search);
        List<UserDTO> users = userService.getUsers(userDetails.getUsername(), role, branch, search);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> getUserById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        log.info("REST: Fetching user ID {} by {}", id, userDetails.getUsername());
        UserDTO user = userService.getUserById(userDetails.getUsername(), id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> createUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDTO request) {
        log.info("REST: Creating user {} with role {} by {}",
                request.getUsername(), request.getRole(), userDetails.getUsername());
        UserDTO createdUser = userService.createUser(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UserDTO request) {
        log.info("REST: Updating user ID {} by {}", id, userDetails.getUsername());
        UserDTO updatedUser = userService.updateUser(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> changeUserRole(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UserDTO request) {
        log.info("REST: Changing role of user ID {} to {} by {}", id, request.getRole(), userDetails.getUsername());
        UserDTO updatedUser = userService.changeUserRole(userDetails.getUsername(), id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getRoles() {
        log.info("REST: Fetching all system user roles");
        return ResponseEntity.ok(userService.getRoles());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<UserDTO> changeUserStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> statusMap) {
        String status = statusMap.get("status");
        log.info("REST: Changing status of user ID {} to {} by {}", id, status, userDetails.getUsername());
        UserDTO statusReq = UserDTO.builder()
                .status(status != null ? lk.ijse.etechbackend.enumiration.Status.valueOf(status.toUpperCase()) : null)
                .build();
        UserDTO updatedUser = userService.updateUserStatus(userDetails.getUsername(), id, statusReq);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse> deleteUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        log.info("REST: Deleting user ID {} by {}", id, userDetails.getUsername());
        userService.deleteUser(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.success("User account removed"));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDTO request) {
        log.info("REST: Self-profile update requested by {}", userDetails.getUsername());
        UserDTO updatedProfile = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDTO request) {
        log.info("REST: Self-password change requested by {}", userDetails.getUsername());
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed"));
    }
}
