package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.categorydto.CategoryRequestDTO;
import lk.ijse.etechbackend.dto.categorydto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> findAll();

    CategoryResponseDTO findById(String id);

    CategoryResponseDTO findBySlug(String slug);

    CategoryResponseDTO findByName(String name);

    List<CategoryResponseDTO> filterCategory(String search);

    void createCategory(CategoryRequestDTO categoryRequestDTO);

    void updateCategory(String id, CategoryRequestDTO categoryRequestDTO);

    void updateStatus(String id, String status);

    void deleteCategory(String id);

    void permanentDelete(String id);


}
