package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.AuthDTO;
import lk.ijse.etechbackend.dto.AuthResponseDTO;
import lk.ijse.etechbackend.dto.UserDTO;
import lk.ijse.etechbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthDTO request) {
        log.info("REST: Login request for user: {}", request.getUsername());
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserDTO request) {
        log.info("REST: Register request for user: {}", request.getUsername());
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST: Get current user profile for: {}", userDetails.getUsername());
        UserDTO userDTO = authService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(userDTO);
    }
}
