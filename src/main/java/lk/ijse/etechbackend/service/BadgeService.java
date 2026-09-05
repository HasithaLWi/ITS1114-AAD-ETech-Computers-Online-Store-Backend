package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.badgedto.BadgeRequestDTO;
import lk.ijse.etechbackend.dto.badgedto.BadgeResponseDTO;
import lk.ijse.etechbackend.enumiration.Status;

import java.util.List;

public interface BadgeService {

    List<BadgeResponseDTO> findAll();

    List<BadgeResponseDTO> findActive();

    BadgeResponseDTO findById(String id);

    BadgeResponseDTO findBySlug(String slug);

    BadgeResponseDTO findByName(String name);

    List<BadgeResponseDTO> filterBadge(String search);

    List<BadgeResponseDTO> filterBadgeByStatus(String status);

    void createBadge(BadgeRequestDTO badgeRequestDTO);

    void updateBadge(String id, BadgeRequestDTO badgeRequestDTO);

    void updateStatus(String id, String status);

    void deleteBadge(String id);

    void permanentDelete(String id);
}
