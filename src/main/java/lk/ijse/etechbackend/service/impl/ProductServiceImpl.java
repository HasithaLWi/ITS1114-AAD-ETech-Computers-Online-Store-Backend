package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.productsdto.ProductRequestDTO;
import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;
import lk.ijse.etechbackend.dto.productsdto.UpdateInventory;
import lk.ijse.etechbackend.entity.*;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.*;
import lk.ijse.etechbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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


        return productPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {

        log.debug("Fetching all products");

        List<Product> productPage = productRepository.findAllProducts();

        List<ProductResponseDTO> content = productPage.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return content;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        log.debug("Fetching product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
        if(product.getProductStatus() == Status.DELETED) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        return mapToResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductBySku(String sku) {
        log.debug("Fetching product by SKU: {}", sku);
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
        if(product.getProductStatus() == Status.DELETED) {
            throw new ResourceNotFoundException("Product not found with SKU: " + sku);
        }
        return mapToResponseDTO(product);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        log.info("Creating new product with SKU: {}", request.getSku());

        if (productRepository.existsBySku(request.getSku())) {
            throw new BadRequestException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId());
        }
        Category category = categoryOpt.get();

        Optional<Brand> brandOpt = brandRepository.findById(request.getBrandId());
        if (brandOpt.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with ID: " + request.getBrandId());
        }
        Brand brand = brandOpt.get();

        Optional<Badge> badgeOpt = badgeRepository.findById(request.getBadgeId());
        if (request.getBadgeId() != null && badgeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Badge not found with ID: " + request.getBadgeId());
        }
        Badge badge = badgeOpt.orElse(null);


        Product product = Product.builder()
                .name(request.getName().trim())
                .category(category)
                .brand(brand)
                .price(request.getPrice())
                .originalPrice(request.getOriginalPrice())
                .rating(new BigDecimal("5.0"))
                .reviewsCount(0)
                .description(request.getDescription())
                .fullDescription(request.getFullDescription())
                .sku(request.getSku().trim())
                .badge(badge)
                .warranty(request.getWarranty() != null ? request.getWarranty().trim() : "2-Year Warranty")
                .alertEnabled(request.getAlertEnabled() != null ? request.getAlertEnabled() : true)
                .lowStockMargin(request.getLowStockMargin() != null ? request.getLowStockMargin() : 5)
                .productStatus(request.getProductStatus() != null ? request.getProductStatus() : Status.ACTIVE)
                .build();

        // Attach gallery images (max 5)
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int order = 0;
            for (String imgUrl : request.getImages()) {
                if (imgUrl != null && !imgUrl.isBlank() && order < 5) {
                    product.addImage(ProductImage.builder()
                            .imageUrl(imgUrl.trim())
                            .displayOrder(order++)
                            .build());
                }
            }
        }

        // Attach branch inventories
        if (request.getBranchStock() != null && !request.getBranchStock().isEmpty()) {
            for (Map.Entry<String, Integer> entry : request.getBranchStock().entrySet()) {
                String branchId = entry.getKey();
                Integer quantity = entry.getValue() != null ? entry.getValue() : 0;
                Branch branch = branchRepository.findById(branchId)
                        .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));

                product.addBranchInventory(BranchInventory.builder()
                        .branch(branch)
                        .quantity(Math.max(0, quantity))
                        .build());
            }
        }

        if (request.getSpecs() != null && !request.getSpecs().isEmpty()) {
            for (Map.Entry<String, String> entry : request.getSpecs().entrySet()) {
                String specName = entry.getKey();
                String specDescription = entry.getValue();
                Specs spec = Specs.builder()
                        .name(specName)
                        .description(specDescription)
                        .build();
                product.addSpec(spec);
            }
        }

        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            for (String featureName : request.getFeatures()) {
                Features feature = Features.builder()
                        .featureName(featureName)
                        .build();
                product.addFeature(feature);
            }
        }

        Product savedProduct = productRepository.save(product);
        log.info("Successfully created product with ID: {} and SKU: {}", savedProduct.getId(), savedProduct.getSku());
        return mapToResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO request) {
        log.info("Updating product ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new BadRequestException("Product SKU '" + request.getSku() + "' is already in use by another product");
        }

        Optional<Category> categoryOpt = categoryRepository.findById(request.getCategoryId());
        if (categoryOpt.isEmpty()) {
            throw new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId());
        }
        Category category = categoryOpt.get();

        Optional<Brand> brandOpt = brandRepository.findById(request.getBrandId());
        if (brandOpt.isEmpty()) {
            throw new ResourceNotFoundException("Brand not found with ID: " + request.getBrandId());
        }
        Brand brand = brandOpt.get();

        Optional<Badge> badgeOpt = badgeRepository.findById(request.getBadgeId());
        if (request.getBadgeId() != null && badgeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Badge not found with ID: " + request.getBadgeId());
        }
        Badge badge = badgeOpt.orElse(null);

        product.setName(request.getName().trim());
        product.setCategory(category);
        product.setBrand(brand);
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setDescription(request.getDescription());
        product.setFullDescription(request.getFullDescription());
        product.setSku(request.getSku().trim());
        product.setBadge(badge);
        product.setWarranty(request.getWarranty() != null ? request.getWarranty().trim() : "No Warranty");
        product.setAlertEnabled(request.getAlertEnabled() != null ? request.getAlertEnabled() : true);
        product.setLowStockMargin(request.getLowStockMargin() != null ? request.getLowStockMargin() : 5);
        product.setProductStatus(request.getProductStatus() != null ? request.getProductStatus() : product.getProductStatus());
//        List<Specs> existingSpecs = product.getSpecs();
//        specsRepository.deleteAll(existingSpecs);

        if (request.getSpecs() != null && !request.getSpecs().isEmpty()) {
            product.getSpecs().clear();
            for (Map.Entry<String, String> entry : request.getSpecs().entrySet()) {
                String specName = entry.getKey();
                String specDescription = entry.getValue();
                Specs spec = Specs.builder()
                        .name(specName)
                        .description(specDescription)
                        .build();
                product.addSpec(spec);
            }
        }

        if (request.getFeatures() != null && !request.getFeatures().isEmpty()) {
            product.getFeatures().clear();
            for (String featureName : request.getFeatures()) {
                Features feature = Features.builder()
                        .featureName(featureName)
                        .build();
                product.addFeature(feature);
            }
        }

        // Update gallery images
        if (request.getImages() != null) {
            product.getImages().clear();
            int order = 0;
            for (String imgUrl : request.getImages()) {
                if (imgUrl != null && !imgUrl.isBlank() && order < 5) {
                    product.addImage(ProductImage.builder()
                            .imageUrl(imgUrl.trim())
                            .displayOrder(order++)
                            .build());
                }
            }
        }

        // Update branch stock
        if (request.getBranchStock() != null) {
            Map<String, BranchInventory> existingBranchMap = product.getBranchInventories().stream()
                    .collect(Collectors.toMap(bi -> bi.getBranch().getId(), bi -> bi));

            for (Map.Entry<String, Integer> entry : request.getBranchStock().entrySet()) {
                String branchId = entry.getKey();
                int qty = Math.max(0, entry.getValue() != null ? entry.getValue() : 0);

                if (existingBranchMap.containsKey(branchId)) {
                    existingBranchMap.get(branchId).setQuantity(qty);
                } else {
                    Branch branch = branchRepository.findById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));
                    product.addBranchInventory(BranchInventory.builder()
                            .branch(branch)
                            .quantity(qty)
                            .build());
                }
            }
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Successfully updated product ID: {}", updatedProduct.getId());
        return mapToResponseDTO(updatedProduct);
    }

    @Override
    public Map<String, Integer> updateBranchInventory(UpdateInventory updateInventory) {

        Optional<Product> productOpt = productRepository.findById(updateInventory.getProductId());
        if (productOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + updateInventory.getProductId());
        }
        Product product = productOpt.get();

        // Update branch stock
        if (updateInventory.getBranchStock() != null) {
            Map<String, BranchInventory> existingBranchMap = product.getBranchInventories().stream()
                    .collect(Collectors.toMap(bi -> bi.getBranch().getId(), bi -> bi));

            for (Map.Entry<String, Integer> entry : updateInventory.getBranchStock().entrySet()) {
                String branchId = entry.getKey();
                int qty = Math.max(0, entry.getValue() != null ? entry.getValue() : 0);

                if (existingBranchMap.containsKey(branchId)) {
                    existingBranchMap.get(branchId).setQuantity(qty);
                } else {
                    Branch branch = branchRepository.findById(branchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + branchId));
                    product.addBranchInventory(BranchInventory.builder()
                            .branch(branch)
                            .quantity(qty)
                            .build());
                }
            }
        }
        Map<String, Integer> updatedBranchStock = product.getBranchInventories().stream()
                .collect(Collectors.toMap(bi -> bi.getBranch().getId(), BranchInventory::getQuantity));
        productRepository.save(product);

        log.info("Successfully updated branch inventory for product ID: {}", product.getId());

        return updatedBranchStock;
    }

    @Override
    public void updateProductStatus(Long id, Status status) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        Product product = productOpt.get();
        product.setProductStatus(status);
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product ID: {}", id);
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        Product product = productOpt.get();

        product.setProductStatus(Status.DELETED);

        productRepository.save(product);
        log.info("Successfully deleted product ID: {}", id);
    }

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
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
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
                .productStatus(product.getProductStatus())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
