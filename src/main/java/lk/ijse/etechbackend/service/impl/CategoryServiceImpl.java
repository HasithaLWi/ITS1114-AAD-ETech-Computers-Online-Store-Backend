package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.dto.categorydto.CategoryRequestDTO;
import lk.ijse.etechbackend.dto.categorydto.CategoryResponseDTO;
import lk.ijse.etechbackend.entity.Category;
import lk.ijse.etechbackend.repository.CategoryRepository;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponseDTO> findAll() {
        List<CategoryResponseDTO> categories = categoryRepository.findAllCategory();
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("No categories found");
        }
        return categories;
    }

    @Override
    public CategoryResponseDTO findById(String id) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        return toResponseDTO(optCategory);
    }

    @Override
    public CategoryResponseDTO findBySlug(String slug) {
        Optional<Category> optCategory = categoryRepository.findBySlug(slug);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with slug " + slug + " not found");
        }
        return toResponseDTO(optCategory);
    }

    @Override
    public CategoryResponseDTO findByName(String name) {
        Optional<Category> optCategory = categoryRepository.findByName(name);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with name " + name + " not found");
        }
        return toResponseDTO(optCategory);
    }

    @Override
    public List<CategoryResponseDTO> filterCategory(String search) {
        return categoryRepository.findAllBySearch(search);
    }

    @Override
    public List<CategoryResponseDTO> getByStatus(String status) {
        log.debug("Fetching categories by status: {}", status);
        return categoryRepository.findAllByStatus(status);
    }


    @Override
    public void createCategory(CategoryRequestDTO categoryRequestDTO) {
        boolean isExist = categoryRepository.existsBySlug(categoryRequestDTO.getSlug());
        if (isExist) {
            throw new BadRequestException("Category with slug " + categoryRequestDTO.getSlug() + " already exists");
        }
        isExist = categoryRepository.existsByName(categoryRequestDTO.getName());
        if (isExist) {
            throw new BadRequestException("Category with name " + categoryRequestDTO.getName() + " already exists");
        }
        Optional<Category> optCategory = categoryRepository.findById(categoryRequestDTO.getId());
        if (optCategory.isPresent()) {
            throw new BadRequestException("Category with id " + categoryRequestDTO.getId() + " already exists");
        }
        log.info("superCategoryId: {}", categoryRequestDTO.getSuperCategoryId());
        Category superCategory = null;
        if (categoryRequestDTO.getSuperCategoryId() != null && !categoryRequestDTO.getSuperCategoryId().isEmpty()) {
            Optional<Category> superCategoryOpt = categoryRepository.findById(categoryRequestDTO.getSuperCategoryId());
            if (superCategoryOpt.isEmpty()) {
                throw new BadRequestException("Super category with id " + categoryRequestDTO.getSuperCategoryId() + " does not exist");
            }
            superCategory = superCategoryOpt.get();
            log.info("Super category with id {} found", categoryRequestDTO.getSuperCategoryId());

        }


        Category category = Category.builder()
                .id(categoryRequestDTO.getId())
                .superCategory(superCategory)
                .name(categoryRequestDTO.getName())
                .slug(categoryRequestDTO.getSlug())
                .icon(categoryRequestDTO.getIcon())
                .description(categoryRequestDTO.getDescription())
                .featured(categoryRequestDTO.getFeatured())
                .displayOrder(categoryRequestDTO.getDisplayOrder())
                .build();
        categoryRepository.save(category);
//        CategoryResponseDTO.builder()
//                .id(category.getId())
//                .superCategoryId(category.getSuperCategory() != null ? category.getSuperCategory().getId() : null)
//                .name(category.getName())
//                .slug(category.getSlug())
//                .icon(category.getIcon())
//                .description(category.getDescription())
//                .featured(category.getFeatured())
//                .displayOrder(category.getDisplayOrder())
//                .categoryStatus(category.getCategoryStatus())
//                .createdAt(category.getCreatedAt())
//                .updatedAt(category.getUpdatedAt())
//                .build();
    }

    @Override
    public void updateCategory(String id, CategoryRequestDTO categoryRequestDTO) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        Category category = optCategory.get();
        if (categoryRequestDTO.getName() != null && !categoryRequestDTO.getName().isEmpty()) {
            category.setName(categoryRequestDTO.getName());
        }
        if (categoryRequestDTO.getSlug() != null && !categoryRequestDTO.getSlug().isEmpty()) {
            category.setSlug(categoryRequestDTO.getSlug());
        }
        if (categoryRequestDTO.getIcon() != null && !categoryRequestDTO.getIcon().isEmpty()) {
            category.setIcon(categoryRequestDTO.getIcon());
        }
        if (categoryRequestDTO.getDescription() != null && !categoryRequestDTO.getDescription().isEmpty()) {
            category.setDescription(categoryRequestDTO.getDescription());
        }
        if (categoryRequestDTO.getFeatured() != null) {
            category.setFeatured(categoryRequestDTO.getFeatured());
        }
        if (categoryRequestDTO.getDisplayOrder() != null) {
            category.setDisplayOrder(categoryRequestDTO.getDisplayOrder());
        }
        if (categoryRequestDTO.getCategoryStatus() != null) {
            category.setCategoryStatus(categoryRequestDTO.getCategoryStatus());
        }
        if (categoryRequestDTO.getSuperCategoryId() != null && !categoryRequestDTO.getSuperCategoryId().isEmpty()) {
            Optional<Category> superCategoryOpt = categoryRepository.findById(categoryRequestDTO.getSuperCategoryId());
            if (superCategoryOpt.isEmpty()) {
                throw new BadRequestException("Super category with id " + categoryRequestDTO.getSuperCategoryId() + " does not exist");
            }
            category.setSuperCategory(superCategoryOpt.get());
        } else {
            category.setSuperCategory(null);
        }

        categoryRepository.save(category);
//        return CategoryResponseDTO.builder()
//                .id(category.getId())
//                .superCategoryId(category.getSuperCategory() != null ? category.getSuperCategory().getId() : null)
//                .name(category.getName())
//                .slug(category.getSlug())
//                .icon(category.getIcon())
//                .description(category.getDescription())
//                .featured(category.getFeatured())
//                .displayOrder(category.getDisplayOrder())
//                .categoryStatus(category.getCategoryStatus())
//                .createdAt(category.getCreatedAt())
//                .updatedAt(category.getUpdatedAt())
//                .build();
    }

    @Override
    public void updateStatus(String id, String status) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        Category category = optCategory.get();
        try {
            Status newStatus = Status.valueOf(status.toUpperCase());
            category.setCategoryStatus(newStatus);
            categoryRepository.save(category);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value: " + status);
        }
    }

    @Override
    public void deleteCategory(String id) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        Category category = optCategory.get();
        category.setCategoryStatus(Status.DELETED);
        categoryRepository.save(category);
    }

    @Override
    public void permanentDelete(String id) {
        Optional<Category> optCategory = categoryRepository.findById(id);
        if (optCategory.isEmpty()) {
            throw new ResourceNotFoundException("Category with id " + id + " not found");
        }
        Category category = optCategory.get();
        List<Product> products = productRepository.findByCategory(category);
        if (!products.isEmpty()) {
            for (Product product : products) {
                product.setCategory(null);
            }
            productRepository.saveAll(products);
        }
        List<Category> subCategories = categoryRepository.findCategoryBySuperCategory(category);
        if (!subCategories.isEmpty()) {
            for (Category subCategory : subCategories) {
                subCategory.setSuperCategory(null);
            }
            categoryRepository.saveAll(subCategories);
        }
//        categoryRepository.delete(category);
    }

    private CategoryResponseDTO toResponseDTO(Optional<Category> optCategory) {
        Category category = optCategory.get();
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .superCategoryId(category.getSuperCategory() != null ? category.getSuperCategory().getId() : null)
                .name(category.getName())
                .slug(category.getSlug())
                .icon(category.getIcon())
                .description(category.getDescription())
                .featured(category.getFeatured())
                .displayOrder(category.getDisplayOrder())
                .categoryStatus(category.getCategoryStatus())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
