package lk.ijse.etechbackend.config;

import lk.ijse.etechbackend.entity.*;
import lk.ijse.etechbackend.enumiration.BadgeRuleType;
import lk.ijse.etechbackend.enumiration.Status;
import lk.ijse.etechbackend.enumiration.UserRole;
import lk.ijse.etechbackend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final BadgeRepository badgeRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("--- DATA INITIALIZER IS RUNNING! ---");
        seedBranches();
        seedUsers();
        seedCategories();
        seedBrands();
        seedBadges();
        seedProducts();
        log.info("--- DATA INITIALIZER FINISHED! ---");
    }

    private void seedBranches() {
        if (branchRepository.count() == 0) {
            log.info("Seeding default branch warehouse hubs...");

            List<Branch> branches = List.of(
                    Branch.builder()
                            .id("BR-COL")
                            .name("Colombo Main Hub")
                            .city("Colombo")
                            .address("450 Galle Road, Colombo 03")
                            .phone("+94 11 234 5678")
                            .email("colombo@etech.com")
                            .latitude(new BigDecimal("6.92710000"))
                            .longitude(new BigDecimal("79.86120000"))
                            .baseShippingRate(new BigDecimal("350.00"))
                            .active(true)
                            .build(),
                    Branch.builder()
                            .id("BR-GAL")
                            .name("Galle Tech Hub")
                            .city("Galle")
                            .address("12 Wakwella Road, Galle")
                            .phone("+94 91 223 4567")
                            .email("galle@etech.com")
                            .latitude(new BigDecimal("6.05350000"))
                            .longitude(new BigDecimal("80.22100000"))
                            .baseShippingRate(new BigDecimal("450.00"))
                            .active(true)
                            .build(),
                    Branch.builder()
                            .id("BR-MAT")
                            .name("Matara Regional Hub")
                            .city("Matara")
                            .address("88 Anagarika Dharmapala Mawatha, Matara")
                            .phone("+94 41 222 3456")
                            .email("matara@etech.com")
                            .latitude(new BigDecimal("5.95490000"))
                            .longitude(new BigDecimal("80.55500000"))
                            .baseShippingRate(new BigDecimal("500.00"))
                            .active(true)
                            .build(),
                    Branch.builder()
                            .id("BR-KAN")
                            .name("Kandy Central Hub")
                            .city("Kandy")
                            .address("102 Dalada Veediya, Kandy")
                            .phone("+94 81 220 1234")
                            .email("kandy@etech.com")
                            .latitude(new BigDecimal("7.29060000"))
                            .longitude(new BigDecimal("80.63370000"))
                            .baseShippingRate(new BigDecimal("450.00"))
                            .active(true)
                            .build()
            );

            branchRepository.saveAll(branches);
            log.info("Successfully seeded {} branches", branches.size());
        }
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("superadmin")) {
            log.info("Seeding Super Admin root account...");
            User superAdmin = User.builder()
                    .name("System Owner & Super Admin")
                    .username("superadmin")
                    .email("superadmin@etech.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(UserRole.SUPERADMIN)
                    .assignedBranch(null)
                    .build();
            userRepository.save(superAdmin);
        }

        if (!userRepository.existsByUsername("admin")) {
            log.info("Seeding Store Administrator account...");
            User admin = User.builder()
                    .name("Store Administrator")
                    .username("admin")
                    .email("admin@etech.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .assignedBranch(null)
                    .build();
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("staff_colombo")) {
            log.info("Seeding Branch Staff account...");
            Branch colomboBranch = branchRepository.findById("BR-COL").orElse(null);
            User staff = User.builder()
                    .name("Colombo Branch Operations")
                    .username("staff_colombo")
                    .email("staff.colombo@etech.com")
                    .passwordHash(passwordEncoder.encode("staff123"))
                    .role(UserRole.STAFF)
                    .assignedBranch(colomboBranch)
                    .build();
            userRepository.save(staff);
        }

        if (!userRepository.existsByUsername("kasun")) {
            log.info("Seeding Customer account...");
            User customer = User.builder()
                    .name("Kasun Perera")
                    .username("kasun")
                    .email("kasun.p@gmail.com")
                    .passwordHash(passwordEncoder.encode("customer123"))
                    .role(UserRole.CUSTOMER)
                    .assignedBranch(null)
                    .build();
            userRepository.save(customer);
        }
    }

    private void seedCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Seeding catalog categories...");

            List<Category> categories = List.of(
                    Category.builder()
                            .id("cat-laptops")
                            .name("Laptops & Notebooks")
                            .slug("laptops")
                            .icon("💻")
                            .description("High-performance gaming, ultrabooks, and professional workstations")
                            .featured(true)
                            .displayOrder(1)
                            .build(),
                    Category.builder()
                            .id("cat-components")
                            .name("PC Components")
                            .slug("components")
                            .icon("⚙️")
                            .description("Processors, GPUs, motherboards, RAM, power supplies, and cases")
                            .featured(true)
                            .displayOrder(2)
                            .build(),
                    Category.builder()
                            .id("cat-peripherals")
                            .name("Peripherals & Accessories")
                            .slug("peripherals")
                            .icon("🖱️")
                            .description("Gaming mice, mechanical keyboards, audio headsets, and streaming gear")
                            .featured(true)
                            .displayOrder(3)
                            .build(),
                    Category.builder()
                            .id("cat-monitors")
                            .name("Monitors & Displays")
                            .slug("monitors")
                            .icon("🖥️")
                            .description("High refresh rate gaming monitors, 4K OLED displays, and ultrawide panels")
                            .featured(true)
                            .displayOrder(4)
                            .build(),
                    Category.builder()
                            .id("cat-storage")
                            .name("Storage & Memory")
                            .slug("storage")
                            .icon("💾")
                            .description("Gen4/Gen5 NVMe SSDs, high-capacity HDDs, and DDR5 RAM kits")
                            .featured(false)
                            .displayOrder(5)
                            .build(),
                    Category.builder()
                            .id("cat-networking")
                            .name("Networking Gear")
                            .slug("networking")
                            .icon("🌐")
                            .description("Wi-Fi 7 gaming routers, mesh network systems, and Gigabit switches")
                            .featured(false)
                            .displayOrder(6)
                            .build()
            );

            categoryRepository.saveAll(categories);
            log.info("Successfully seeded {} categories", categories.size());
        }
    }

    private void seedBrands() {
        if (brandRepository.count() == 0) {
            log.info("Seeding official manufacturer partner brands...");

            List<Brand> brands = List.of(
                    Brand.builder()
                            .id("brd-asus")
                            .name("ASUS")
                            .slug("asus")
                            .logoUrl("https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=200&auto=format&fit=crop&q=80")
                            .country("Taiwan")
                            .foundedYear("1989")
                            .websiteUrl("https://www.asus.com")
                            .tagline("In Search of Incredible")
                            .description("Leading provider of ROG gaming hardware, laptops, motherboards, and displays.")
                            .featured(true)
                            .status(Status.ACTIVE)
                            .displayOrder(1)
                            .build(),
                    Brand.builder()
                            .id("brd-msi")
                            .name("MSI")
                            .slug("msi")
                            .logoUrl("https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=200&auto=format&fit=crop&q=80")
                            .country("Taiwan")
                            .foundedYear("1986")
                            .websiteUrl("https://www.msi.com")
                            .tagline("True Gaming")
                            .description("World leader in AI PCs, gaming laptops, graphics cards, and enthusiast components.")
                            .featured(true)
                            .status(Status.ACTIVE)
                            .displayOrder(2)
                            .build(),
                    Brand.builder()
                            .id("brd-corsair")
                            .name("Corsair")
                            .slug("corsair")
                            .logoUrl("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=200&auto=format&fit=crop&q=80")
                            .country("USA")
                            .foundedYear("1994")
                            .websiteUrl("https://www.corsair.com")
                            .tagline("Game On")
                            .description("High-performance gaming gear, liquid cooling, power supplies, and iCUE ecosystem.")
                            .featured(true)
                            .status(Status.ACTIVE)
                            .displayOrder(3)
                            .build(),
                    Brand.builder()
                            .id("brd-intel")
                            .name("Intel")
                            .slug("intel")
                            .logoUrl("https://images.unsplash.com/photo-1518770660439-4636190af475?w=200&auto=format&fit=crop&q=80")
                            .country("USA")
                            .foundedYear("1968")
                            .websiteUrl("https://www.intel.com")
                            .tagline("Do More")
                            .description("Cutting-edge Core Ultra processors and advanced semiconductor innovation.")
                            .featured(true)
                            .status(Status.ACTIVE)
                            .displayOrder(4)
                            .build(),
                    Brand.builder()
                            .id("brd-logitech")
                            .name("Logitech")
                            .slug("logitech")
                            .logoUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=200&auto=format&fit=crop&q=80")
                            .country("Switzerland")
                            .foundedYear("1981")
                            .websiteUrl("https://www.logitechg.com")
                            .tagline("Defy Logic")
                            .description("Premier gaming mice, keyboards, simulation wheels, and professional audio gear.")
                            .featured(true)
                            .status(Status.ACTIVE)
                            .displayOrder(5)
                            .build(),
                    Brand.builder()
                            .id("brd-razer")
                            .name("Razer")
                            .slug("razer")
                            .logoUrl("https://images.unsplash.com/photo-1542751371-adc38448a05e?w=200&auto=format&fit=crop&q=80")
                            .country("USA")
                            .foundedYear("2005")
                            .websiteUrl("https://www.razer.com")
                            .tagline("For Gamers. By Gamers.")
                            .description("Global gaming lifestyle brand renowned for Chroma RGB peripherals and Blade laptops.")
                            .featured(true)
                            .status(Status.ACTIVE)
                            .displayOrder(6)
                            .build()
            );

            brandRepository.saveAll(brands);
            log.info("Successfully seeded {} brands", brands.size());
        }
    }

    private void seedBadges() {
        if (badgeRepository.count() == 0) {
            log.info("Seeding system badges and rules...");

            List<Badge> badges = List.of(
                    Badge.builder()
                            .id("bdg-hotdeal")
                            .name("Hot Deal")
                            .slug("hotdeal")
                            .colorKey("rose")
                            .colorHex("#e11d48")
                            .purpose("Active promotional discount campaign")
                            .standardDescription("Discounted hardware with active countdown timer")
                            .ruleType(BadgeRuleType.system)
                            .criteria("promo_active")
                            .priority(1)
                            .isSystemDefault(true)
                            .canEdit(false)
                            .canDelete(false)
                            .status(Status.ACTIVE)
                            .build(),
                    Badge.builder()
                            .id("bdg-bestseller")
                            .name("Bestseller")
                            .slug("bestseller")
                            .colorKey("amber")
                            .colorHex("#d97706")
                            .purpose("High sales volume leader")
                            .standardDescription("Top selling product based on customer order volume")
                            .ruleType(BadgeRuleType.automatic)
                            .criteria("sales_gt_50")
                            .priority(2)
                            .isSystemDefault(true)
                            .canEdit(true)
                            .canDelete(false)
                            .status(Status.ACTIVE)
                            .build(),
                    Badge.builder()
                            .id("bdg-toprated")
                            .name("Top Rated")
                            .slug("toprated")
                            .colorKey("emerald")
                            .colorHex("#059669")
                            .purpose("Customer rating average >= 4.8")
                            .standardDescription("Highly rated product with excellent user feedback")
                            .ruleType(BadgeRuleType.automatic)
                            .criteria("rating_gte_4.8")
                            .priority(3)
                            .isSystemDefault(true)
                            .canEdit(true)
                            .canDelete(false)
                            .status(Status.ACTIVE)
                            .build(),
                    Badge.builder()
                            .id("bdg-new")
                            .name("New Arrival")
                            .slug("new")
                            .colorKey("sky")
                            .colorHex("#0284c7")
                            .purpose("Recently added hardware")
                            .standardDescription("Latest generation hardware catalog addition")
                            .ruleType(BadgeRuleType.manual)
                            .criteria("created_within_30d")
                            .priority(4)
                            .isSystemDefault(true)
                            .canEdit(true)
                            .canDelete(false)
                            .status(Status.ACTIVE)
                            .build()
            );

            badgeRepository.saveAll(badges);
            log.info("Successfully seeded {} badges", badges.size());
        }
    }

    private void seedProducts() {
        if (productRepository.count() == 0) {
            log.info("Seeding catalog products with specs, gallery, and branch warehouse inventory...");

            Branch colombo = branchRepository.findById("BR-COL").orElse(null);
            Branch galle = branchRepository.findById("BR-GAL").orElse(null);
            Branch matara = branchRepository.findById("BR-MAT").orElse(null);
            Branch kandy = branchRepository.findById("BR-KAN").orElse(null);

            // 1. ROG Strix SCAR 18
            Product p1 = Product.builder()
                    .name("ROG Strix SCAR 18 (2026)")
                    .category(categoryRepository.findBySlug("laptops").orElse(null))
                    .brand(brandRepository.findBySlug("asus").orElse(null))
                    .badge(badgeRepository.findBySlug("toprated").orElse(null))
                    .price(new BigDecimal("849999.00"))
                    .originalPrice(new BigDecimal("899999.00"))
                    .rating(new BigDecimal("4.9"))
                    .reviewsCount(48)
                    .description("Flagship 18-inch Mini-LED gaming laptop powered by Intel Core Ultra 9 & NVIDIA RTX 4090.")
                    .fullDescription("Dominate Windows 11 gaming with the 2026 ROG Strix SCAR 18. Equipped with an Intel Core Ultra 9 185H processor, NVIDIA GeForce RTX 4090 Laptop GPU with 175W max TGP, 64GB DDR5 memory, and lightning-fast 4TB PCIe 4.0 NVMe RAID 0 storage. Features Conductonaut Extreme liquid metal and Tri-Fan Technology.")
                    .sku("ETC-LAP-001")
                    .warranty("3-Year Official Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(3)
                    .build();

            p1.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1603302576837-37561b2e2302?w=800&auto=format&fit=crop&q=80").displayOrder(0).build());
            p1.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1541807084-5c52b6b3adef?w=800&auto=format&fit=crop&q=80").displayOrder(1).build());
            p1.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1593642702821-c8da6771f0c6?w=800&auto=format&fit=crop&q=80").displayOrder(2).build());

            p1.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p1.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p1.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p1.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());
            p1.addSpec(Specs.builder().name("Display").description("18.0\" QHD+ (2560x1600) 240Hz Mini-LED HDR 1100").build());

            p1.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p1.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p1.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(6).build());
            if (galle != null) p1.addBranchInventory(BranchInventory.builder().branch(galle).quantity(3).build());
            if (matara != null) p1.addBranchInventory(BranchInventory.builder().branch(matara).quantity(2).build());
            if (kandy != null) p1.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(2).build());

            // 2. MSI Titan 18 HX
            Product p2 = Product.builder()
                    .name("MSI Titan 18 HX Dragon Edition")
                    .category(categoryRepository.findBySlug("laptops").orElse(null))
                    .brand(brandRepository.findBySlug("msi").orElse(null))
                    .badge(badgeRepository.findBySlug("toprated").orElse(null))
                    .price(new BigDecimal("799999.00"))
                    .originalPrice(new BigDecimal("849999.00"))
                    .rating(new BigDecimal("4.8"))
                    .reviewsCount(32)
                    .description("Extreme desktop replacement with mechanical Cherry MX keyboard and vapor chamber cooling.")
                    .fullDescription("MSI Titan 18 HX combines extreme desktop-grade computing with unmatched portability. Driven by Intel 14th Gen Core i9-14900HX, RTX 4090 GPU, 18-inch 4K 120Hz Mini-LED display, and world's first seamless RGB haptic touchpad.")
                    .sku("ETC-LAP-002")

                    .warranty("2-Year International Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(2)
                    .build();

            p2.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80").displayOrder(0).build());
            p2.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1593642634367-d91a135587b5?w=800&auto=format&fit=crop&q=80").displayOrder(1).build());

            p2.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p2.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p2.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p2.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());
            p2.addSpec(Specs.builder().name("Display").description("18.0\" QHD+ (2560x1600) 240Hz Mini-LED HDR 1100").build());

            p2.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p2.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p2.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(4).build());
            if (galle != null) p2.addBranchInventory(BranchInventory.builder().branch(galle).quantity(2).build());
            if (kandy != null) p2.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(1).build());

            // 3. ASUS TUF Gaming GeForce RTX 4070 Ti Super
            Product p3 = Product.builder()
                    .name("ASUS TUF Gaming GeForce RTX 4070 Ti SUPER 16GB")
                    .category(categoryRepository.findBySlug("components").orElse(null))
                    .brand(brandRepository.findBySlug("asus").orElse(null))
                    .badge(badgeRepository.findBySlug("hotdeal").orElse(null))
                    .price(new BigDecimal("325000.00"))
                    .originalPrice(new BigDecimal("345000.00"))
                    .rating(new BigDecimal("4.9"))
                    .reviewsCount(85)
                    .description("Military-grade durability, dual ball fan bearings, and robust heatsink for 4K ray tracing.")
                    .fullDescription("Built with auto-extreme automated manufacturing, TUF capacitors rated for 20,000 hours at 105C, and axial-tech fans scaled up for 21% more airflow.")
                    .sku("ETC-GPU-001")
                    .warranty("3-Year Replacement Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(5)
                    .build();

            p3.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1587202372775-e229f172b9d7?w=800&auto=format&fit=crop&q=80").displayOrder(0).build());

            p3.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p3.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p3.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p3.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());


            p3.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p3.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p3.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(12).build());
            if (galle != null) p3.addBranchInventory(BranchInventory.builder().branch(galle).quantity(6).build());
            if (matara != null) p3.addBranchInventory(BranchInventory.builder().branch(matara).quantity(4).build());
            if (kandy != null) p3.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(5).build());

            // 4. Corsair Dominator Titanium DDR5
            Product p4 = Product.builder()
                    .name("Corsair Dominator Titanium DDR5 64GB (2x32GB) 6600MHz")
                    .category(categoryRepository.findBySlug("components").orElse(null))
                    .brand(brandRepository.findBySlug("corsair").orElse(null))
                    .badge(badgeRepository.findBySlug("new").orElse(null))
                    .price(new BigDecimal("115000.00"))
                    .originalPrice(new BigDecimal("125000.00"))
                    .rating(new BigDecimal("5.0"))
                    .reviewsCount(21)
                    .description("Premium forged aluminum styling, patented DHX cooling, and 11 vibrant CAPELLIX RGB LEDs.")
                    .fullDescription("Corsair Dominator Titanium combines clean forged aluminum styling with precision memory performance and customizable top bar architecture.")
                    .sku("ETC-RAM-001")
                    .warranty("Lifetime Limited Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(4)
                    .build();

            p4.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80").displayOrder(0).build());

            p4.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p4.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p4.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p4.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());
            p4.addSpec(Specs.builder().name("Display").description("18.0\" QHD+ (2560x1600) 240Hz Mini-LED HDR 1100").build());

            p4.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p4.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p4.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(15).build());
            if (galle != null) p4.addBranchInventory(BranchInventory.builder().branch(galle).quantity(8).build());
            if (matara != null) p4.addBranchInventory(BranchInventory.builder().branch(matara).quantity(6).build());
            if (kandy != null) p4.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(7).build());

            // 5. Logitech G PRO X SUPERLIGHT 2
            Product p5 = Product.builder()
                    .name("Logitech G PRO X SUPERLIGHT 2 Wireless Gaming Mouse")
                    .category(categoryRepository.findBySlug("peripherals").orElse(null))
                    .brand(brandRepository.findBySlug("logitech").orElse(null))
                    .badge(badgeRepository.findBySlug("bestseller").orElse(null))
                    .price(new BigDecimal("49500.00"))
                    .originalPrice(new BigDecimal("55000.00"))
                    .rating(new BigDecimal("4.9"))
                    .reviewsCount(142)
                    .description("Ultra-lightweight 60g wireless esports mouse with LIGHTFORCE hybrid optical-mechanical switches.")
                    .fullDescription("Engineered with the world's leading esports professionals. Features the HERO 2 sensor with sub-micron tracking up to 32,000 DPI and true 4,000Hz wireless polling rate via LIGHTSPEED.")
                    .sku("ETC-MOU-001")
                    .warranty("2-Year Official Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(10)
                    .build();

            p5.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800&auto=format&fit=crop&q=80").displayOrder(0).build());

            p5.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p5.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p5.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p5.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());
            p5.addSpec(Specs.builder().name("Display").description("18.0\" QHD+ (2560x1600) 240Hz Mini-LED HDR 1100").build());

            p5.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p5.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p5.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(25).build());
            if (galle != null) p5.addBranchInventory(BranchInventory.builder().branch(galle).quantity(14).build());
            if (matara != null) p5.addBranchInventory(BranchInventory.builder().branch(matara).quantity(10).build());
            if (kandy != null) p5.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(12).build());

            // 6. Razer Huntsman V3 Pro
            Product p6 = Product.builder()
                    .name("Razer Huntsman V3 Pro Analog Gaming Keyboard")
                    .category(categoryRepository.findBySlug("peripherals").orElse(null))
                    .brand(brandRepository.findBySlug("razer").orElse(null))
                    .badge(badgeRepository.findBySlug("toprated").orElse(null))
                    .price(new BigDecimal("78000.00"))
                    .originalPrice(new BigDecimal("85000.00"))
                    .rating(new BigDecimal("4.8"))
                    .reviewsCount(59)
                    .description("Gen-2 Analog Optical switches with Rapid Trigger and adjustable actuation from 0.1 to 4.0 mm.")
                    .fullDescription("Maximize responsiveness for competitive FPS gaming. Rapid Trigger mode allows instant keystroke reset without physical rebound for ultra-fast counter-strafing.")
                    .sku("ETC-KEY-001")
                    .warranty("2-Year Official Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(5)
                    .build();

            p6.addImage(ProductImage.builder().imageUrl("https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop&q=80").displayOrder(0).build());

            p6.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p6.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p6.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p6.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());
            p6.addSpec(Specs.builder().name("Display").description("18.0\" QHD+ (2560x1600) 240Hz Mini-LED HDR 1100").build());

            p6.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p6.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p6.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(18).build());
            if (galle != null) p6.addBranchInventory(BranchInventory.builder().branch(galle).quantity(9).build());
            if (matara != null) p6.addBranchInventory(BranchInventory.builder().branch(matara).quantity(5).build());
            if (kandy != null) p6.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(8).build());

            // 7. ASUS ROG Swift OLED PG32UCDM
            Product p7 = Product.builder()
                    .name("ASUS ROG Swift OLED PG32UCDM 32\" 4K 240Hz")
                    .category(categoryRepository.findBySlug("monitors").orElse(null))
                    .brand(brandRepository.findBySlug("asus").orElse(null))
                    .badge(badgeRepository.findBySlug("hotdeal").orElse(null))
                    .price(new BigDecimal("420000.00"))
                    .originalPrice(new BigDecimal("450000.00"))
                    .rating(new BigDecimal("5.0"))
                    .reviewsCount(18)
                    .description("32-inch 4K QD-OLED gaming panel with 240Hz refresh rate and 0.03ms response time.")
                    .fullDescription("Featuring 3rd Gen QD-OLED technology, custom graphene heatsink, DisplayPort 1.4 (DSC), HDMI 2.1, and 90W USB-C Power Delivery.")
                    .sku("ETC-MON-001")
                    .warranty("3-Year OLED Burn-in Warranty")
                    .alertEnabled(true)
                    .lowStockMargin(3)
                    .build();

            p7.addImage(ProductImage.builder()
                    .imageUrl("https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800&auto=format&fit=crop&q=80")
                    .displayOrder(0)
                    .build());

            p7.addSpec(Specs.builder().name("Processor").description("Intel Core Ultra 9 185H (24 Cores, up to 5.8GHz)").build());
            p7.addSpec(Specs.builder().name("Graphics").description("NVIDIA GeForce RTX 4090 16GB GDDR6 (175W)").build());
            p7.addSpec(Specs.builder().name("Memory").description("64GB DDR5 5600MHz Dual-Channel").build());
            p7.addSpec(Specs.builder().name("Storage").description("4TB NVMe PCIe 4.0 SSD (2TB x 2 RAID 0)").build());
            p7.addSpec(Specs.builder().name("Display").description("18.0\" QHD+ (2560x1600) 240Hz Mini-LED HDR 1100").build());

            p7.addFeature(Features.builder().featureName("Conductonaut Extreme Liquid Metal on CPU & GPU").build());
            p7.addFeature(Features.builder().featureName("Tri-Fan Cooling with Anti-Dust Technology").build());

            if (colombo != null) p7.addBranchInventory(BranchInventory.builder().branch(colombo).quantity(5).build());
            if (galle != null) p7.addBranchInventory(BranchInventory.builder().branch(galle).quantity(2).build());
            if (kandy != null) p7.addBranchInventory(BranchInventory.builder().branch(kandy).quantity(2).build());

            productRepository.saveAll(List.of(p1, p2, p3, p4, p5, p6, p7));
            log.info("Successfully seeded 7 sample products with branch inventories and gallery images!");
        }
    }
}
