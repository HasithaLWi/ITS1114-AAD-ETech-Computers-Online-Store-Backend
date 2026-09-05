package lk.ijse.etechbackend.dto.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponseDTO {
    private String reply;
    private List<Long> suggestedProducts;
    private LocalDateTime timestamp;
}
