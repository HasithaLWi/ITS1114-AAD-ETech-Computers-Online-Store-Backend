package lk.ijse.etechbackend.dto.chat;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryItemDTO {
    private String sender; // "user" or "assistant" / "bot"
    private String text;
}
