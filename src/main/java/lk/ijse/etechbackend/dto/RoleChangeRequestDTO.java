package lk.ijse.etechbackend.dto;

import jakarta.validation.constraints.NotNull;
import lk.ijse.etechbackend.enumiration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleChangeRequestDTO {

    @NotNull(message = "Role is required")
    private UserRole role;

    private String assignedBranch;
}
