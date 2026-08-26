package lk.ijse.etechbackend.config;

import lk.ijse.etechbackend.entity.Branch;
import lk.ijse.etechbackend.entity.User;
import lk.ijse.etechbackend.enumiration.UserRole;
import lk.ijse.etechbackend.repository.BranchRepository;
import lk.ijse.etechbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedBranches();
        seedUsers();
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
}
