package lk.ijse.etechbackend.service;


import lk.ijse.etechbackend.dto.UserDTO;

import java.util.List;

public interface UserService {


    UserDTO getUserDetails(String username, String password);
    void saveUser(UserDTO userDTO);
    UserDTO getUserDetailsById(long id);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUserByName(String name);
    void deleteUser(long id);
}
