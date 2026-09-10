package lk.ijse.etechbackend.controller;

import lk.ijse.etechbackend.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/ping")
    public ResponseEntity<CommonResponse> ping() {
        return ResponseEntity.ok(CommonResponse.builder()
                .status(200)
                .message("Ping successful")
                .body("Server Connect Successful")
                .build()
        );
    }
}
