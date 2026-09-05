package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.profile.LegalPolicyDTO;

import java.util.List;

public interface LegalPolicyService {
    List<LegalPolicyDTO> getAllPolicies();
    LegalPolicyDTO getPolicyBySlug(String slug);
    LegalPolicyDTO updatePolicy(String slug, LegalPolicyDTO request);
}
