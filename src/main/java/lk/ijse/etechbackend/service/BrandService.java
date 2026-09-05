package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.branddto.BrandRequestDTO;
import lk.ijse.etechbackend.dto.branddto.BrandResponseDTO;

import java.util.List;

public interface BrandService {

    List<BrandResponseDTO> findAll();

    List<BrandResponseDTO> findFeatured();

    BrandResponseDTO findById(String id);

    BrandResponseDTO findBySlug(String slug);

    BrandResponseDTO findByName(String name);

    List<BrandResponseDTO> filterBrand(String search);

    List<BrandResponseDTO> findAllByStatus(String status);

    void createBrand(BrandRequestDTO brandRequestDTO);

    void updateBrand(String id, BrandRequestDTO brandRequestDTO);

    void updateStatus(String id, String status);

    void deleteBrand(String id);

    void permanentDelete(String id);
}
