package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.AuthDTO;
import lk.ijse.etechbackend.dto.AuthResponseDTO;
import lk.ijse.etechbackend.dto.UserDTO;
import lk.ijse.etechbackend.entity.User;
import lk.ijse.etechbackend.enumiration.UserRole;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.exception.UnauthorizedException;
import lk.ijse.etechbackend.repository.UserRepository;
import lk.ijse.etechbackend.security.JwtUtil;
import lk.ijse.etechbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO login(AuthDTO request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Password mismatch for username: {}", request.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }

        UserDTO userDTO = mapToDTO(user, null);
        String token = jwtUtil.generateToken(userDTO);

        log.info("User {} successfully authenticated with role {}", user.getUsername(), user.getRole());
        return AuthResponseDTO.builder()
                .token(token)
                .user(userDTO)
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO register(UserDTO request) {
        log.info("Registering customer account with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .assignedBranch(null)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Registered new user ID: {} with role: {}", savedUser.getId(), savedUser.getRole());

        UserDTO userDTO = mapToDTO(savedUser, null);
        String token = jwtUtil.generateToken(userDTO);

        return AuthResponseDTO.builder()
                .token(token)
                .user(userDTO)
                .build();
    }

    @Override
    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToDTO(user, null);
    }

    private UserDTO mapToDTO(User user, Boolean canManage) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .assignedBranch(user.getAssignedBranch() != null ? user.getAssignedBranch().getId() : null)
                .canManage(canManage)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
