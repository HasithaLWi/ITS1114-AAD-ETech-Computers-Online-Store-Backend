package lk.ijse.etechbackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lk.ijse.etechbackend.enumiration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private String assignedBranch;
    private Boolean canManage;
    private LocalDateTime createdAt;


    private String currentPassword;
    private String newPassword;

    public UserDTO(Long id, String username, String name, String email, UserRole role, String assignedBranch, Boolean canManage, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.assignedBranch = assignedBranch;
        this.canManage = canManage;
        this.createdAt = createdAt;
    }

    public UserDTO(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public UserDTO(String username, String name, String email) {
        this.username = username;
        this.name = name;
        this.email = email;
    }

    public UserDTO(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserDTO(UserRole role, String assignedBranch) {
        this.role = role;
        this.assignedBranch = assignedBranch;
    }

    public UserDTO(String username, String name, String email, String password, UserRole role, String assignedBranch) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.assignedBranch = assignedBranch;
    }
}
