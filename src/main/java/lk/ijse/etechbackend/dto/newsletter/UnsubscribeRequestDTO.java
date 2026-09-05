package lk.ijse.etechbackend.dto.newsletter;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnsubscribeRequestDTO {
    @NotBlank(message = "Identifier (email) is required")
    private String identifier;

    private String email;

    public String getEmail() {
        return email != null && !email.isBlank() ? email : identifier;
    }
}
