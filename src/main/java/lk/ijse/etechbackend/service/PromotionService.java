package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.promotion.*;

import java.util.List;

public interface PromotionService {
    // Hot Deals
    List<HotDealResponseDTO> getHotDeals(Boolean activeOnly);
    HotDealResponseDTO getHotDealById(Long id);
    HotDealResponseDTO createHotDeal(HotDealRequestDTO request);
    HotDealResponseDTO updateHotDeal(Long id, HotDealRequestDTO request);
    void deleteHotDeal(Long id);

    // Home Deal Banner
    HomeDealBannerDTO getHomeBanner();
    HomeDealBannerDTO updateHomeBanner(HomeDealBannerDTO request);

    // Deal Bundles
    List<DealBundleResponseDTO> getBundles(Boolean activeOnly);
    DealBundleResponseDTO getBundleById(Long id);
    DealBundleResponseDTO createBundle(DealBundleRequestDTO request);
    DealBundleResponseDTO updateBundle(Long id, DealBundleRequestDTO request);
    void deleteBundle(Long id);
}
