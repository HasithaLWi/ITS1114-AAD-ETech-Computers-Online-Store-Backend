package lk.ijse.etechbackend.controller;

import lk.ijse.etechbackend.dto.categorydto.CategoryRequestDTO;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        log.info("REST: Creating category - {}", categoryRequestDTO.getId());
        categoryService.createCategory(categoryRequestDTO);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category created successfully")
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/all")
    public ResponseEntity<CommonResponse> getAllCategories() {
        log.info("REST: Retrieving all categories");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Categories retrieved successfully")
                .body(categoryService.findAll())
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<CommonResponse> getCategoryById(@PathVariable String id) {
        log.info("REST: Retrieving category by ID - {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category retrieved successfully")
                .body(categoryService.findById(id))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/slug/{slug}")
    public ResponseEntity<CommonResponse> getCategoryBySlug(@PathVariable String slug) {
        log.info("REST: Retrieving category by slug - {}", slug);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category retrieved successfully")
                .body(categoryService.findBySlug(slug))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/name/{name}")
    public ResponseEntity<CommonResponse> getCategoryByName(@PathVariable String name) {
        log.info("REST: Retrieving category by name - {}", name);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category retrieved successfully")
                .body(categoryService.findByName(name))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/filter")
//    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<CommonResponse> filterCategory(@RequestParam String search) {
        log.info("REST: Filtering categories with search - {}", search);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Categories filtered successfully")
                .body(categoryService.filterCategory(search))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/status")
    public ResponseEntity<CommonResponse> getCategoriesByStatus(@RequestParam String status) {
        log.info("REST: Retrieving categories by status - {}", status);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Categories retrieved successfully")
                .body(categoryService.getByStatus(status))
                .build());
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateCategory(@PathVariable String id, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        log.info("REST: Updating category - {}", id);
        categoryService.updateCategory(id, categoryRequestDTO);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category updated successfully")
                .build());
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update-status/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateCategoryStatus(@PathVariable String id, @RequestParam String status) {
        log.info("REST: Updating category status - {} to {}", id, status);
        categoryService.updateStatus(id, status);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category status updated successfully")
                .build());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteCategory(@PathVariable String id) {
        log.info("REST: Deleting category - {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category deleted successfully")
                .build());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/perma-delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public ResponseEntity<CommonResponse> permanentDelete(@PathVariable String id) {
        log.info("REST: Permanently deleting category - {}", id);
        categoryService.permanentDelete(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Category permanently deleted successfully")
                .build());
    }
}
