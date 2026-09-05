package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.branddto.BrandRequestDTO;
import lk.ijse.etechbackend.dto.branddto.BrandResponseDTO;
import lk.ijse.etechbackend.entity.Brand;
import lk.ijse.etechbackend.entity.Product;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.BrandRepository;
import lk.ijse.etechbackend.repository.ProductRepository;
import lk.ijse.etechbackend.service.BrandService;
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
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> findAll() {
        log.info("Fetching all brands ordered by display order");
        List<Brand> brands = brandRepository.findAllByOrderByDisplayOrderAsc();
        if (brands.isEmpty()) {
            throw new ResourceNotFoundException("No brands found");
        }
        return brands.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> findFeatured() {
        log.info("Fetching all featured active brands");
        List<Brand> brands = brandRepository.findByFeaturedTrueOrderByDisplayOrderAsc();
        return brands.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponseDTO findById(String id) {
        log.info("Fetching brand by ID: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with id " + id + " not found"));
        return toResponseDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponseDTO findBySlug(String slug) {
        log.info("Fetching brand by slug: {}", slug);
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with slug " + slug + " not found"));
        return toResponseDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandResponseDTO findByName(String name) {
        log.info("Fetching brand by name: {}", name);
        Brand brand = brandRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with name " + name + " not found"));
        return toResponseDTO(brand);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BrandResponseDTO> filterBrand(String search) {
        log.info("Filtering brands with search term: {}", search);
        if (search == null || search.isBlank()) {
            return findAll();
        }
        List<Brand> brands = brandRepository.findAllBySearch(search.trim());
        return brands.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BrandResponseDTO> findAllByStatus(String status) {
        log.info("Fetching brands by status: {}", status);
        Status enumStatus;
        try {
            enumStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status value: " + status);
        }
        List<Brand> brands = brandRepository.findAllByStatus(enumStatus);
        return brands.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createBrand(BrandRequestDTO request) {
        log.info("Creating brand with name: {}", request.getName());

        String slug = request.getSlug() != null && !request.getSlug().isBlank()
                ? request.getSlug().trim().toLowerCase()
                : request.getName().trim().toLowerCase().replaceAll("[^a-z0-9]+", "-");

        String brandId = request.getId() != null && !request.getId().isBlank()
                ? request.getId().trim()
                : "brd-" + slug;

        if (brandRepository.existsById(brandId)) {
            throw new BadRequestException("Brand with id " + brandId + " already exists");
        }
        if (brandRepository.existsBySlug(slug)) {
            throw new BadRequestException("Brand with slug " + slug + " already exists");
        }
        if (brandRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("Brand with name " + request.getName() + " already exists");
        }

        Brand brand = Brand.builder()
                .id(brandId)
                .name(request.getName().trim())
                .slug(slug)
                .logoUrl(request.getLogoUrl())
                .country(request.getCountry() != null ? request.getCountry() : "Global")
                .foundedYear(request.getFoundedYear())
                .websiteUrl(request.getWebsiteUrl())
                .tagline(request.getTagline())
                .description(request.getDescription())
                .featured(request.getFeatured() != null ? request.getFeatured() : false)
                .status(request.getStatus() != null ? request.getStatus() : Status.ACTIVE)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();

        brandRepository.save(brand);
        log.info("Successfully created brand with ID: {}", brand.getId());
    }

    @Override
    public void updateBrand(String id, BrandRequestDTO request) {
        log.info("Updating brand with ID: {}", id);

        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with id " + id + " not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            String trimmedName = request.getName().trim();
            if (brandRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
                throw new BadRequestException("Brand with name '" + trimmedName + "' is already in use by another brand");
            }
            brand.setName(trimmedName);
        }

        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            String trimmedSlug = request.getSlug().trim().toLowerCase();
            if (brandRepository.existsBySlugAndIdNot(trimmedSlug, id)) {
                throw new BadRequestException("Brand with slug '" + trimmedSlug + "' is already in use by another brand");
            }
            brand.setSlug(trimmedSlug);
        }

        if (request.getLogoUrl() != null) {
            brand.setLogoUrl(request.getLogoUrl());
        }
        if (request.getCountry() != null) {
            brand.setCountry(request.getCountry());
        }
        if (request.getFoundedYear() != null) {
            brand.setFoundedYear(request.getFoundedYear());
        }
        if (request.getWebsiteUrl() != null) {
            brand.setWebsiteUrl(request.getWebsiteUrl());
        }
        if (request.getTagline() != null) {
            brand.setTagline(request.getTagline());
        }
        if (request.getDescription() != null) {
            brand.setDescription(request.getDescription());
        }
        if (request.getFeatured() != null) {
            brand.setFeatured(request.getFeatured());
        }
        if (request.getStatus() != null) {
            brand.setStatus(request.getStatus());
        }
        if (request.getDisplayOrder() != null) {
            brand.setDisplayOrder(request.getDisplayOrder());
        }

        brandRepository.save(brand);
        log.info("Successfully updated brand with ID: {}", brand.getId());
    }

    @Override
    public void updateStatus(String id, String status) {
        log.info("Updating status for brand ID {} to status: {}", id, status);
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            throw new ResourceNotFoundException("Brand with id " + id + " not found");
        }
        Brand brand = optionalBrand.get();
        try{
            Status newStatus = Status.valueOf(status);
            brand.setStatus(newStatus);
            brandRepository.save(brand);

        }catch (IllegalArgumentException e){

            throw new BadRequestException("Invalid status value: " + status);
        }
    }

    @Override
    public void deleteBrand(String id) {
        log.info("Soft deleting brand ID: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with id " + id + " not found"));
        brand.setStatus(Status.DELETED);
        brandRepository.save(brand);
    }

    @Override
    public void permanentDelete(String id) {
        log.info("Permanently deleting brand ID: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with id " + id + " not found"));

        if (brand.getProducts() != null && !brand.getProducts().isEmpty()) {
            for (Product product : brand.getProducts()) {
                product.setBrand(null);
            }
            productRepository.saveAll(brand.getProducts());
        }

        brandRepository.delete(brand);
        log.info("Permanently deleted brand with ID: {}", id);
    }

    private BrandResponseDTO toResponseDTO(Brand brand) {
        return BrandResponseDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .country(brand.getCountry())
                .foundedYear(brand.getFoundedYear())
                .websiteUrl(brand.getWebsiteUrl())
                .tagline(brand.getTagline())
                .description(brand.getDescription())
                .featured(brand.getFeatured())
                .status(brand.getStatus())
                .displayOrder(brand.getDisplayOrder())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }
}
