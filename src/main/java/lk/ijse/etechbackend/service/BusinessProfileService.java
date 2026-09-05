package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.profile.BusinessProfileDTO;

public interface BusinessProfileService {
    BusinessProfileDTO getProfile();
    BusinessProfileDTO updateProfile(BusinessProfileDTO request);
}
