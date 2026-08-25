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
    private UserRole role;
    private String assignedBranch;
    private Boolean canManage;
    private LocalDateTime createdAt;
}
