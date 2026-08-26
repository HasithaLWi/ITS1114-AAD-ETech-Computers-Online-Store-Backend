package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.AuthRequestDTO;
import lk.ijse.etechbackend.dto.AuthResponseDTO;
import lk.ijse.etechbackend.dto.RegisterRequestDTO;
import lk.ijse.etechbackend.dto.UserDTO;

public interface AuthService {

    AuthResponseDTO login(AuthRequestDTO request);

    AuthResponseDTO register(RegisterRequestDTO request);

    UserDTO getCurrentUser(String username);
}
