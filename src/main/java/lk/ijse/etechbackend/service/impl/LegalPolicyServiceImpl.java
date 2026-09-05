package lk.ijse.etechbackend.service.impl;

import lk.ijse.etechbackend.dto.profile.LegalPolicyDTO;
import lk.ijse.etechbackend.entity.LegalPolicy;
import lk.ijse.etechbackend.entity.PolicySections;
import lk.ijse.etechbackend.exception.ResourceNotFoundException;
import lk.ijse.etechbackend.repository.LegalPolicyRepository;
import lk.ijse.etechbackend.repository.PolicySectionRepository;
import lk.ijse.etechbackend.service.LegalPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LegalPolicyServiceImpl implements LegalPolicyService {

    private final LegalPolicyRepository policyRepository;
    private final PolicySectionRepository policySectionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LegalPolicyDTO> getAllPolicies() {
        log.info("Fetching all legal policies");
        return policyRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LegalPolicyDTO getPolicyBySlug(String slug) {
        log.info("Fetching legal policy by slug/ID: {}", slug);
        LegalPolicy policy = policyRepository.findById(slug.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Policy document not found for: " + slug));
        return toDTO(policy);
    }

    @Override
    public LegalPolicyDTO updatePolicy(String slug, LegalPolicyDTO request) {
        log.info("Updating legal policy for: {}", slug);
        LegalPolicy policy = policyRepository.findById(slug.toLowerCase())
                .orElseGet(() -> LegalPolicy.builder().id(slug.toLowerCase()).build());

        if (request.getTitle() != null) policy.setTitle(request.getTitle().trim());
        if (request.getSubtitle() != null) policy.setSubtitle(request.getSubtitle().trim());
        if (request.getLastUpdated() != null) policy.setLastUpdated(request.getLastUpdated().trim());

        if (request.getPolicySections() != null && !request.getPolicySections().isEmpty()) {
            policy.getPolicySections().clear();
            for (Map.Entry<String, String> entry : request.getPolicySections().entrySet()) {
                String title = entry.getKey();
                String section = entry.getValue();

                PolicySections policySection = PolicySections.builder()
                        .id(slug.toLowerCase() + "-" + title.toLowerCase().replaceAll("\\s+", "-"))
                        .sectionTitle(title)
                        .sectionContent(section)
                        .legalPolicy(policy)
                        .build();

                policy.addPolicySection(policySectionRepository.save(policySection));
            }

        }

        LegalPolicy saved = policyRepository.save(policy);
        return toDTO(saved);
    }

    private LegalPolicyDTO toDTO(LegalPolicy p) {

        Map<String, String> sections = new HashMap<>();
        if (p.getPolicySections() != null && !p.getPolicySections().isEmpty()) {
            p.getPolicySections().forEach(section -> sections.put(
                    section.getSectionTitle(),
                    section.getSectionContent()
            ));
        }
        return LegalPolicyDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .subtitle(p.getSubtitle())
                .lastUpdated(p.getLastUpdated())
                .policySections(sections)
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
