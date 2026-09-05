package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.inventory.InventoryAdjustRequestDTO;
import lk.ijse.etechbackend.dto.inventory.ProductInventorySettingsDTO;
import lk.ijse.etechbackend.dto.inventory.StockHealthReportDTO;
import lk.ijse.etechbackend.dto.productsdto.ProductResponseDTO;

import java.util.Map;

public interface InventoryService {
    StockHealthReportDTO getHealthReport(String branchId);
    ProductResponseDTO updateInventorySettings(Long productId, ProductInventorySettingsDTO request);
    Map<String, Integer> adjustInventory(Long productId, InventoryAdjustRequestDTO request);
}
