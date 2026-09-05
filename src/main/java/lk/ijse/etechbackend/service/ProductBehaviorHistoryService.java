package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.behavior.ProductBehaviorHistoryDTO;
import lk.ijse.etechbackend.dto.behavior.ProductBehaviorHistoryRequestDTO;

import java.util.List;

public interface ProductBehaviorHistoryService {
    List<ProductBehaviorHistoryDTO> getGlobalHistory(int page, int size);
    List<ProductBehaviorHistoryDTO> getProductHistory(Long productId);
    ProductBehaviorHistoryDTO logEvent(ProductBehaviorHistoryRequestDTO request);
}
