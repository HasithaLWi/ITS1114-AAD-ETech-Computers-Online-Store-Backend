package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.profile.BusinessProfileDTO;
import lk.ijse.etechbackend.entity.BusinessProfile;
import lk.ijse.etechbackend.repository.BusinessProfileRepository;
import lk.ijse.etechbackend.service.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessProfileServiceImpl implements BusinessProfileService {

    private final BusinessProfileRepository profileRepository;

    @Override
    @Transactional(readOnly = true)
    public BusinessProfileDTO getProfile() {
        log.info("Fetching business profile");
        BusinessProfile profile = profileRepository.findById(1).orElseGet(() -> {
            BusinessProfile defaultProfile = BusinessProfile.builder()
                    .id(1)
                    .storeName("ETech Computers (Pvt) Ltd")
                    .tagline("Sri Lanka's Premier Next-Gen High Performance Computing & Gaming Destination")
                    .registrationNo("PV-00289412")
                    .taxId("VAT-100293841-7000")
                    .isoCert("ISO 9001:2015 Quality Management Certified")
                    .supportEmail("support@etech.com")
                    .hotline("+94 11 234 5678")
                    .headquarters("Level 14, ETech Tower, 450 Galle Road, Colombo 03, Sri Lanka")
                    .workingHours("Monday – Saturday: 9:00 AM – 7:30 PM | Sunday: 10:00 AM – 4:00 PM")
                    .missionStatement("To empower gamers, creative professionals, and high-performance computing enthusiasts with cutting-edge authentic technology, rapid island-wide logistics, and exceptional customer guarantee.")
                    .companyStory("Founded with a vision to redefine computer hardware retail in Sri Lanka, ETech Computers has grown to become the benchmark in enthusiast PC hardware.")
                    .build();
            return profileRepository.save(defaultProfile);
        });

        return toDTO(profile);
    }

    @Override
    public BusinessProfileDTO updateProfile(BusinessProfileDTO request) {
        log.info("Updating business profile");
        BusinessProfile profile = profileRepository.findById(1).orElseGet(() -> BusinessProfile.builder().id(1).build());

        if (request.getStoreName() != null) profile.setStoreName(request.getStoreName().trim());
        if (request.getTagline() != null) profile.setTagline(request.getTagline().trim());
        if (request.getRegistrationNo() != null) profile.setRegistrationNo(request.getRegistrationNo().trim());
        if (request.getTaxId() != null) profile.setTaxId(request.getTaxId().trim());
        if (request.getIsoCert() != null) profile.setIsoCert(request.getIsoCert().trim());
        if (request.getSupportEmail() != null) profile.setSupportEmail(request.getSupportEmail().trim());
        if (request.getHotline() != null) profile.setHotline(request.getHotline().trim());
        if (request.getHeadquarters() != null) profile.setHeadquarters(request.getHeadquarters().trim());
        if (request.getWorkingHours() != null) profile.setWorkingHours(request.getWorkingHours().trim());
        if (request.getMissionStatement() != null) profile.setMissionStatement(request.getMissionStatement().trim());
        if (request.getCompanyStory() != null) profile.setCompanyStory(request.getCompanyStory().trim());

        BusinessProfile saved = profileRepository.save(profile);
        return toDTO(saved);
    }

    private BusinessProfileDTO toDTO(BusinessProfile p) {
        return BusinessProfileDTO.builder()
                .id(p.getId())
                .storeName(p.getStoreName())
                .tagline(p.getTagline())
                .registrationNo(p.getRegistrationNo())
                .taxId(p.getTaxId())
                .isoCert(p.getIsoCert())
                .supportEmail(p.getSupportEmail())
                .hotline(p.getHotline())
                .headquarters(p.getHeadquarters())
                .workingHours(p.getWorkingHours())
                .missionStatement(p.getMissionStatement())
                .companyStory(p.getCompanyStory())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
