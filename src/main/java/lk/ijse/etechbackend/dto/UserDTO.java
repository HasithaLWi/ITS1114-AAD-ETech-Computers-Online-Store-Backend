package lk.ijse.etechbackend.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.enumiration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {

    private long userId;
    private String name;
    private String username;
    private String emailAddress;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole userRoles;
    @Enumerated(EnumType.STRING)
    private Status status;

    public UserDTO(long userId, String username, String password, UserRole userRoles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.userRoles = userRoles;
    }
}
