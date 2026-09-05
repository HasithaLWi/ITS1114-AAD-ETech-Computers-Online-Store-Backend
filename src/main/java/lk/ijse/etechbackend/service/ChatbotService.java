package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.chat.ChatMessageRequestDTO;
import lk.ijse.etechbackend.dto.chat.ChatMessageResponseDTO;

public interface ChatbotService {
    ChatMessageResponseDTO processMessage(ChatMessageRequestDTO request);
}
