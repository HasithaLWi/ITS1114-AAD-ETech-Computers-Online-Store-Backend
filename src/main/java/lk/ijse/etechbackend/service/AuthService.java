package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.AuthDTO;
import lk.ijse.etechbackend.dto.AuthResponseDTO;
import lk.ijse.etechbackend.dto.UserDTO;

public interface AuthService {

    AuthResponseDTO login(AuthDTO request);

    AuthResponseDTO register(UserDTO request);

    UserDTO getCurrentUser(String username);
}
