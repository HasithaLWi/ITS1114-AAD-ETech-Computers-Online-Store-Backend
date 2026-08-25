package lk.ijse.etechbackend.service.impl;


import lk.ijse.etechbackend.dto.UserDTO;
import lk.ijse.etechbackend.entity.User;
import lk.ijse.etechbackend.exception.CustomException;
import lk.ijse.etechbackend.repository.UserRepository;
import lk.ijse.etechbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO getUserDetails(String username, String password) {

        Optional<User> optionalUser = userRepository.findByUsernameAndPassword(username,password);
        if(optionalUser.isEmpty())
            throw new CustomException(HttpStatus.NOT_FOUND.value(), "User not found with username: " + username);

        User user = optionalUser.get();
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserRoles()
        );

    }

    @Override
    public void saveUser(UserDTO userDTO) {
        User user = new User();

        user.setName(userDTO.getName());
        user.setEmailAddress(userDTO.getEmailAddress());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setUserRoles(userDTO.getUserRoles());
        user.setStatus(userDTO.getStatus());

        log.info("Saving user: {}", user);

        userRepository.save(user);
    }

    @Override
    public UserDTO getUserDetailsById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty())
            throw new CustomException(HttpStatus.NOT_FOUND.value(), "User not found with id: " + id);

        User user = optionalUser.get();
        return new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getUsername(),
                user.getEmailAddress(),
                user.getPassword(),
                user.getUserRoles(),
                user.getStatus()
        );
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getUsername(),
                user.getEmailAddress(),
                user.getPassword(),
                user.getUserRoles(),
                user.getStatus()
        )).toList();
    }

    @Override
    public List<UserDTO> getUserByName(String name) {
        List<User> users = userRepository.findByName(name);
        return users.stream().map(user -> new UserDTO(
                user.getUserId(),
                user.getName(),
                user.getUsername(),
                user.getEmailAddress(),
                user.getPassword(),
                user.getUserRoles(),
                user.getStatus()
        )).toList();
    }



    @Override
    public void deleteUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty())
            throw new CustomException(HttpStatus.NOT_FOUND.value(), "User not found with id: " + id);

        userRepository.deleteById(id);

    }
}
