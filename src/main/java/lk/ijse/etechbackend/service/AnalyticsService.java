package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.analytics.AnalyticsOverviewDTO;
import lk.ijse.etechbackend.dto.analytics.BranchRevenueDTO;
import lk.ijse.etechbackend.dto.analytics.TopProductDTO;

import java.util.List;

public interface AnalyticsService {
    AnalyticsOverviewDTO getOverview();
    List<BranchRevenueDTO> getBranchRevenue();
    List<TopProductDTO> getTopProducts(int limit);
}
