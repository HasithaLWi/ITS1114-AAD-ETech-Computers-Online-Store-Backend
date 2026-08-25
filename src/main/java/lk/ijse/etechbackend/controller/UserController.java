package lk.ijse.etechbackend.controller;

import lk.ijse.etechbackend.dto.AuthDTO;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.UserDTO;
import lk.ijse.etechbackend.dto.UserDataDTO;
import lk.ijse.etechbackend.security.JwtUtil;
import lk.ijse.etechbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "save")
    public CommonResponse save(@RequestBody UserDTO userDTO) {
        userService.saveUser(userDTO);
        log.info("User saved successfully: {}", userDTO);
        return new CommonResponse(200,"save success");
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,value = "login")
    public CommonResponse login(@RequestBody AuthDTO authDTO) {
        UserDTO userDetails = userService.getUserDetails(authDTO.getUsername(), authDTO.getPassword());
        System.out.println("API called here");
        String token = jwtUtil.generateToken(userDetails);

        UserDataDTO userDataDTO = new UserDataDTO();
        userDataDTO.setToken(token);
        userDataDTO.setUserId(userDetails.getUserId());
        return new CommonResponse(0,userDataDTO,"JWT Token");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/all")
    public CommonResponse getAllUsers(){
        return new CommonResponse(0,userService.getAllUsers(),"All users");
    }

}
