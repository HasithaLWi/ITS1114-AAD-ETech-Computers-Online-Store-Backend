package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.branch.BranchDTO;
import lk.ijse.etechbackend.dto.branch.BranchNearestRequestDTO;
import lk.ijse.etechbackend.dto.branch.BranchNearestResponseDTO;
import lk.ijse.etechbackend.entity.Branch;
import lk.ijse.etechbackend.exception.BadRequestException;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.BranchRepository;
import lk.ijse.etechbackend.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getAllBranches(Boolean activeOnly) {
        log.info("Fetching all branches (activeOnly={})", activeOnly);
        List<Branch> branches = (activeOnly != null && activeOnly)
                ? branchRepository.findByActiveTrue()
                : branchRepository.findAll();

        return branches.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDTO getBranchById(String id) {
        log.info("Fetching branch by ID: {}", id);
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));
        return toDTO(branch);
    }

    @Override
    public BranchDTO createBranch(BranchDTO request) {
        log.info("Creating branch: {}", request.getName());

        String branchId = request.getId() != null && !request.getId().isBlank()
                ? request.getId().trim().toUpperCase()
                : "BR-" + request.getCity().trim().substring(0, Math.min(3, request.getCity().trim().length())).toUpperCase();

        if (branchRepository.existsById(branchId)) {
            throw new BadRequestException("Branch with ID " + branchId + " already exists");
        }

        Branch branch = Branch.builder()
                .id(branchId)
                .name(request.getName().trim())
                .city(request.getCity().trim())
                .address(request.getAddress().trim())
                .phone(request.getPhone().trim())
                .email(request.getEmail().trim())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .baseShippingRate(request.getBaseShippingRate() != null ? request.getBaseShippingRate() : new BigDecimal("350.00"))
                .active(request.getActive() != null ? request.getActive() : true)
                .build();

        Branch saved = branchRepository.save(branch);
        log.info("Successfully created branch with ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Override
    public BranchDTO updateBranch(String id, BranchDTO request) {
        log.info("Updating branch ID: {}", id);
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        if (request.getName() != null) branch.setName(request.getName().trim());
        if (request.getCity() != null) branch.setCity(request.getCity().trim());
        if (request.getAddress() != null) branch.setAddress(request.getAddress().trim());
        if (request.getPhone() != null) branch.setPhone(request.getPhone().trim());
        if (request.getEmail() != null) branch.setEmail(request.getEmail().trim());
        if (request.getLatitude() != null) branch.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) branch.setLongitude(request.getLongitude());
        if (request.getBaseShippingRate() != null) branch.setBaseShippingRate(request.getBaseShippingRate());
        if (request.getActive() != null) branch.setActive(request.getActive());

        Branch saved = branchRepository.save(branch);
        return toDTO(saved);
    }

    @Override
    public void deleteBranch(String id) {
        log.info("Decommissioning branch ID: {}", id);
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with ID: " + id));

        branch.setActive(false);
        branchRepository.save(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchNearestResponseDTO findNearestBranch(BranchNearestRequestDTO request) {
        log.info("Calculating nearest branch for city: {}, lat: {}, lon: {}",
                request.getCity(), request.getLatitude(), request.getLongitude());

        List<Branch> activeBranches = branchRepository.findByActiveTrue();
        if (activeBranches.isEmpty()) {
            throw new ResourceNotFoundException("No active branches found");
        }

        Branch bestBranch = null;
        double minDistance = Double.MAX_VALUE;

        // If coordinates provided, calculate using Haversine
        if (request.getLatitude() != null && request.getLongitude() != null) {
            double userLat = request.getLatitude().doubleValue();
            double userLon = request.getLongitude().doubleValue();

            for (Branch b : activeBranches) {
                double dist = calculateDistanceKm(userLat, userLon, b.getLatitude().doubleValue(), b.getLongitude().doubleValue());
                if (dist < minDistance) {
                    minDistance = dist;
                    bestBranch = b;
                }
            }
        }
        // If city name provided, check city match
        else if (request.getCity() != null && !request.getCity().isBlank()) {
            String targetCity = request.getCity().trim().toLowerCase();
            Optional<Branch> cityMatch = activeBranches.stream()
                    .filter(b -> b.getCity().toLowerCase().contains(targetCity) || targetCity.contains(b.getCity().toLowerCase()))
                    .findFirst();

            if (cityMatch.isPresent()) {
                bestBranch = cityMatch.get();
                minDistance = 3.5; // Nominal in-city distance
            } else {
                bestBranch = activeBranches.get(0); // Colombo hub fallback
                minDistance = 25.0;
            }
        } else {
            bestBranch = activeBranches.get(0);
            minDistance = 5.0;
        }

        BigDecimal distanceKm = BigDecimal.valueOf(minDistance).setScale(1, RoundingMode.HALF_UP);
        BigDecimal baseRate = bestBranch.getBaseShippingRate() != null ? bestBranch.getBaseShippingRate() : new BigDecimal("350.00");
        BigDecimal distanceSurcharge = distanceKm.multiply(BigDecimal.valueOf(15)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal shippingFee = baseRate.add(distanceSurcharge).setScale(2, RoundingMode.HALF_UP);

        return BranchNearestResponseDTO.builder()
                .branch(toDTO(bestBranch))
                .distanceKm(distanceKm)
                .shippingFee(shippingFee)
                .build();
    }

    private double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private BranchDTO toDTO(Branch b) {
        return BranchDTO.builder()
                .id(b.getId())
                .name(b.getName())
                .city(b.getCity())
                .address(b.getAddress())
                .phone(b.getPhone())
                .email(b.getEmail())
                .latitude(b.getLatitude())
                .longitude(b.getLongitude())
                .baseShippingRate(b.getBaseShippingRate())
                .active(b.getActive())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
