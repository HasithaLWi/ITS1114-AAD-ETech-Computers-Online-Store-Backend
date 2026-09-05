package lk.ijse.etechbackend.controller;

import jakarta.validation.Valid;
import lk.ijse.etechbackend.dto.CommonResponse;
import lk.ijse.etechbackend.dto.branddto.BrandRequestDTO;
import lk.ijse.etechbackend.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/create")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> createBrand(@Valid @RequestBody BrandRequestDTO brandRequestDTO) {
        log.info("REST: Creating brand - {}", brandRequestDTO.getName());
        brandService.createBrand(brandRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message("Brand created successfully")
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/all")
    public ResponseEntity<CommonResponse> getAllBrands() {
        log.info("REST: Retrieving all brands");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brands retrieved successfully")
                .body(brandService.findAll())
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/featured")
    public ResponseEntity<CommonResponse> getFeaturedBrands() {
        log.info("REST: Retrieving featured brands");
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Featured brands retrieved successfully")
                .body(brandService.findFeatured())
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/{id}")
    public ResponseEntity<CommonResponse> getBrandById(@PathVariable String id) {
        log.info("REST: Retrieving brand by ID - {}", id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand retrieved successfully")
                .body(brandService.findById(id))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/slug/{slug}")
    public ResponseEntity<CommonResponse> getBrandBySlug(@PathVariable String slug) {
        log.info("REST: Retrieving brand by slug - {}", slug);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand retrieved successfully")
                .body(brandService.findBySlug(slug))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/name/{name}")
    public ResponseEntity<CommonResponse> getBrandByName(@PathVariable String name) {
        log.info("REST: Retrieving brand by name - {}", name);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand retrieved successfully")
                .body(brandService.findByName(name))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/filter")
    public ResponseEntity<CommonResponse> filterBrand(@RequestParam String search) {
        log.info("REST: Filtering brands with search - {}", search);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brands filtered successfully")
                .body(brandService.filterBrand(search))
                .build());
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/status/{status}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> getBrandsByStatus(@PathVariable String status) {
        log.info("REST: Retrieving brands by status - {}", status);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brands retrieved successfully")
                .body(brandService.findAllByStatus(status))
                .build());
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateBrand(@PathVariable String id, @RequestBody BrandRequestDTO brandRequestDTO) {
        log.info("REST: Updating brand - {}", id);
        brandService.updateBrand(id, brandRequestDTO);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand updated successfully")
                .build());
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/update-status/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> updateBrandStatus(@PathVariable String id, @RequestParam String status) {
        log.info("REST: Updating brand status - {} to status: {}", id, status);
        brandService.updateStatus(id, status);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand status updated successfully")
                .build());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'ADMIN')")
    public ResponseEntity<CommonResponse> deleteBrand(@PathVariable String id) {
        log.info("REST: Deleting brand - {}", id);
        brandService.deleteBrand(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand deleted successfully")
                .build());
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE, value = "/perma-delete/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN')")
    public ResponseEntity<CommonResponse> permanentDelete(@PathVariable String id) {
        log.info("REST: Permanently deleting brand - {}", id);
        brandService.permanentDelete(id);
        return ResponseEntity.ok(CommonResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Brand permanently deleted successfully")
                .build());
    }
}
