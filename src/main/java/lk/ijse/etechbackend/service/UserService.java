package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.*;
import lk.ijse.etechbackend.enumiration.UserRole;

import java.util.List;

public interface UserService {

    List<UserDTO> getUsers(String currentUsername, UserRole role, String branch, String search);

    UserDTO getUserById(String currentUsername, Long id);

    UserDTO createUser(String currentUsername, UserCreateRequestDTO request);

    UserDTO updateUser(String currentUsername, Long id, UserUpdateRequestDTO request);

    UserDTO changeUserRole(String currentUsername, Long id, RoleChangeRequestDTO request);

    void deleteUser(String currentUsername, Long id);

    UserDTO updateProfile(String currentUsername, ProfileUpdateRequestDTO request);

    void changePassword(String currentUsername, PasswordChangeRequestDTO request);
}
