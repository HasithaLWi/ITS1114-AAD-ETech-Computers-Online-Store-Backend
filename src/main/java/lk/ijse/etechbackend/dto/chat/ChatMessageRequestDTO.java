package lk.ijse.etechbackend.dto.chat;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequestDTO {
    @NotBlank(message = "Message cannot be empty")
    private String message;

    private List<ChatHistoryItemDTO> history;
    private List<Object> cart;
}
