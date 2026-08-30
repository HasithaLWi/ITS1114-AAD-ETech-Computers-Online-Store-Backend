package lk.ijse.etechbackend.service.impl;

import jakarta.persistence.criteria.Predicate;
import lk.ijse.etechbackend.dto.PageResponseDTO;
import lk.ijse.etechbackend.dto.ProductRequestDTO;
import lk.ijse.etechbackend.dto.ProductResponseDTO;
import lk.ijse.etechbackend.entity.*;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.*;
import lk.ijse.etechbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final BranchInventoryRepository branchInventoryRepository;
    private final BranchRepository branchRepository;
    private final CategoryRepository categoryRepository;
    private final SpecsRepository specsRepository;
    private final FeaturesRepository featuresRepository;
    private final BadgeRepository badgeRepository;
    private final BrandRepository brandRepository;

    private final int MAX_GALLERY_IMAGES = 5;


//    @Override
//    @Transactional(readOnly = true)
//    public PageResponseDTO<ProductResponseDTO> getAllProducts(
//            String category,
//            String brand,
//            String search,
//            BigDecimal minPrice,
//            BigDecimal maxPrice,
//            String badge,
//            int page,
//            int size,
//            String sortBy,
//            String sortDirection) {
//
//        log.debug("Fetching products with filters - cat: {}, brand: {}, search: {}, min: {}, max: {}, badge: {}, page: {}, size: {}",
//                category, brand, search, minPrice, maxPrice, badge, page, size);
//
//        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
//        String sortProperty = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
//
//        Specification<Product> spec = (root, query, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (category != null && !category.isBlank()) {
//                predicates.add(cb.equal(cb.lower(root.get("categorySlug")), category.trim().toLowerCase()));
//            }
//
//            if (brand != null && !brand.isBlank()) {
//                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
//            }
//
//            if (badge != null && !badge.isBlank()) {
//                predicates.add(cb.equal(cb.lower(root.get("badge")), badge.trim().toLowerCase()));
//            }
//
//            if (minPrice != null) {
//                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
//            }
//
//            if (maxPrice != null) {
//                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
//            }
//
//            if (search != null && !search.isBlank()) {
//                String searchPattern = "%" + search.trim().toLowerCase() + "%";
//                Predicate nameMatch = cb.like(cb.lower(root.get("name")), searchPattern);
//                Predicate descMatch = cb.like(cb.lower(root.get("description")), searchPattern);
//                Predicate brandMatch = cb.like(cb.lower(root.get("brand")), searchPattern);
//                Predicate skuMatch = cb.like(cb.lower(root.get("sku")), searchPattern);
//                predicates.add(cb.or(nameMatch, descMatch, brandMatch, skuMatch));
//            }
//
//            return cb.and(predicates.toArray(new Predicate[0]));
//        };
//
//        Page<Product> productPage = productRepository.findAll(spec, pageable);
//
//        List<ProductResponseDTO> content = productPage.getContent().stream()
//                .map(this::mapToResponseDTO)
//                .collect(Collectors.toList());
//
//        return PageResponseDTO.of(productPage, content);
//    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getFilteredProducts(String category,
            String brand,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String badge,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));

        Page<Product> productPage = productRepository.findProductsWithOptionalFilter(
                category != null && !category.isBlank() ? category.trim().toLowerCase() : null,
                brand != null && !brand.isBlank() ? brand.trim().toLowerCase() : null,
                search != null && !search.isBlank() ? search.trim().toLowerCase() : null,
                minPrice,
                maxPrice,
                badge != null && !badge.isBlank() ? badge.trim().toLowerCase() : null,
                pageable
        );

        log.info("Fetched {} products with filters - category: {}, brand: {}, search: {}, minPrice: {}, " +
                "maxPrice: {}, badge: {}, page: {}, size: {}", productPage.getNumberOfElements(),
                category, brand, search, minPrice, maxPrice, badge, page, size);


        return  productPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {

        log.debug("Fetching all products");

        List<Product> productPage = productRepository.findAll();

        List<ProductResponseDTO> content = productPage.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return content;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public ProductResponseDTO getProductById(Long id) {
//        log.debug("Fetching product by ID: {}", id);
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
//        return mapToResponseDTO(product);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ProductResponseDTO getProductBySku(String sku) {
//        log.debug("Fetching product by SKU: {}", sku);
//        Product product = productRepository.findBySku(sku)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
//        return mapToResponseDTO(product);
//    }
//
//    @Override
//    public ProductResponseDTO createProduct(ProductRequestDTO request) {
//        log.info("Creating new product with SKU: {}", request.getSku());
//
//        if (productRepository.existsBySku(request.getSku())) {
//            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
//        }
//
//        Product product = Product.builder()
//                .name(request.getName().trim())
//                .categorySlug(request.getCategorySlug().trim().toLowerCase())
//                .brand(request.getBrand() != null ? request.getBrand().trim() : "")
//                .price(request.getPrice())
//                .originalPrice(request.getOriginalPrice())
//                .rating(new BigDecimal("5.0"))
//                .reviewsCount(0)
//                .imageUrl(request.getImageUrl().trim())
//                .description(request.getDescription())
//                .fullDescription(request.getFullDescription())
//                .sku(request.getSku().trim())
//                .badge(request.getBadge() != null ? request.getBadge().trim() : "")
//                .warranty(request.getWarranty() != null ? request.getWarranty().trim() : "2-Year Warranty")
//                .alertEnabled(request.getAlertEnabled() != null ? request.getAlertEnabled() : true)
//                .lowStockMargin(request.getLowStockMargin() != null ? request.getLowStockMargin() : 5)
//                .specs(request.getSpecs() != null ? request.getSpecs() : new HashMap<>())
//                .features(request.getFeatures() != null ? request.getFeatures() : new ArrayList<>())
//                .build();
//
//        // Attach gallery images (max 5)
//        if (request.getImages() != null && !request.getImages().isEmpty()) {
//            int order = 0;
//            for (String imgUrl : request.getImages()) {
//                if (imgUrl != null && !imgUrl.isBlank() && order < 5) {
//                    product.addImage(ProductImage.builder()
//                            .imageUrl(imgUrl.trim())
//                            .displayOrder(order++)
//                            .build());
//                }
//            }
//        }
//
//        // Attach branch inventories
//        if (request.getBranchStock() != null && !request.getBranchStock().isEmpty()) {
//            for (Map.Entry<String, Integer> entry : request.getBranchStock().entrySet()) {
//                String branchId = entry.getKey();
//                Integer quantity = entry.getValue() != null ? entry.getValue() : 0;
//                Branch branch = branchRepository.findById(branchId)
//                        .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));
//
//                product.addBranchInventory(BranchInventory.builder()
//                        .branch(branch)
//                        .quantity(Math.max(0, quantity))
//                        .build());
//            }
//        }
//
//        Product savedProduct = productRepository.save(product);
//        log.info("Successfully created product with ID: {} and SKU: {}", savedProduct.getId(), savedProduct.getSku());
//        return mapToResponseDTO(savedProduct);
//    }
//
//    @Override
//    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
//        log.info("Updating product ID: {}", id);
//
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
//
//        if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
//            throw new BadRequestException("Product SKU '" + request.getSku() + "' is already in use by another product");
//        }
//
//        product.setName(request.getName().trim());
//        product.setCategorySlug(request.getCategorySlug().trim().toLowerCase());
//        product.setBrand(request.getBrand() != null ? request.getBrand().trim() : "");
//        product.setPrice(request.getPrice());
//        product.setOriginalPrice(request.getOriginalPrice());
//        product.setImageUrl(request.getImageUrl().trim());
//        product.setDescription(request.getDescription());
//        product.setFullDescription(request.getFullDescription());
//        product.setSku(request.getSku().trim());
//        product.setBadge(request.getBadge() != null ? request.getBadge().trim() : "");
//        product.setWarranty(request.getWarranty() != null ? request.getWarranty().trim() : "2-Year Warranty");
//        product.setAlertEnabled(request.getAlertEnabled() != null ? request.getAlertEnabled() : true);
//        product.setLowStockMargin(request.getLowStockMargin() != null ? request.getLowStockMargin() : 5);
//        product.setSpecs(request.getSpecs() != null ? request.getSpecs() : new HashMap<>());
//        product.setFeatures(request.getFeatures() != null ? request.getFeatures() : new ArrayList<>());
//
//        // Update gallery images
//        if (request.getImages() != null) {
//            product.getImages().clear();
//            int order = 0;
//            for (String imgUrl : request.getImages()) {
//                if (imgUrl != null && !imgUrl.isBlank() && order < 5) {
//                    product.addImage(ProductImage.builder()
//                            .imageUrl(imgUrl.trim())
//                            .displayOrder(order++)
//                            .build());
//                }
//            }
//        }
//
//        // Update branch stock
//        if (request.getBranchStock() != null) {
//            Map<String, BranchInventory> existingBranchMap = product.getBranchInventories().stream()
//                    .collect(Collectors.toMap(bi -> bi.getBranch().getId(), bi -> bi));
//
//            for (Map.Entry<String, Integer> entry : request.getBranchStock().entrySet()) {
//                String branchId = entry.getKey();
//                int qty = Math.max(0, entry.getValue() != null ? entry.getValue() : 0);
//
//                if (existingBranchMap.containsKey(branchId)) {
//                    existingBranchMap.get(branchId).setQuantity(qty);
//                } else {
//                    Branch branch = branchRepository.findById(branchId)
//                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));
//                    product.addBranchInventory(BranchInventory.builder()
//                            .branch(branch)
//                            .quantity(qty)
//                            .build());
//                }
//            }
//        }
//
//        Product updatedProduct = productRepository.save(product);
//        log.info("Successfully updated product ID: {}", updatedProduct.getId());
//        return mapToResponseDTO(updatedProduct);
//    }
//
//    @Override
//    public void deleteProduct(Long id) {
//        log.info("Deleting product ID: {}", id);
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
//        productRepository.delete(product);
//        log.info("Successfully deleted product ID: {}", id);
//    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        List<String> imageUrls = new ArrayList<>();
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            imageUrls = product.getImages().stream()
                    .sorted(Comparator.comparing(ProductImage::getDisplayOrder))
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
        }

        Map<String, Integer> branchStock = new HashMap<>();
        int totalStock = 0;
        if (product.getBranchInventories() != null && !product.getBranchInventories().isEmpty()) {
            for (BranchInventory bi : product.getBranchInventories()) {
                if (bi.getBranch() != null) {
                    branchStock.put(bi.getBranch().getId(), bi.getQuantity());
                    totalStock += (bi.getQuantity() != null ? bi.getQuantity() : 0);
                }
            }
        }

        Map<String, String> specs = new HashMap<>();
        if (product.getSpecs() != null && !product.getSpecs().isEmpty()) {

            for (Specs spec : product.getSpecs()) {
                specs.put(spec.getName(), spec.getDescription());
            }
        }

        List<String> features = new ArrayList<>();
        if (product.getFeatures() != null && !product.getFeatures().isEmpty()) {
            for (Features feature : product.getFeatures()) {
                features.add(feature.getFeatureName());
            }
        }

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .categoryId(product.getCategory().getId())
                .brandId(product.getBrand().getId())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .rating(product.getRating())
                .reviewsCount(product.getReviewsCount())
                .description(product.getDescription())
                .fullDescription(product.getFullDescription())
                .sku(product.getSku())
                .badgeId(product.getBadge().getId())
                .warranty(product.getWarranty())
                .alertEnabled(product.getAlertEnabled())
                .lowStockMargin(product.getLowStockMargin())
                .specs(specs)
                .features(features)
                .images(imageUrls)
                .branchStock(branchStock)
                .totalStock(totalStock)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
