package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.transfer.StockTransferRequestDTO;
import lk.ijse.etechbackend.dto.transfer.StockTransferResponseDTO;
import lk.ijse.etechbackend.dto.transfer.TransferMetricsDTO;
import lk.ijse.etechbackend.dto.transfer.TransferStatusUpdateDTO;
import lk.ijse.etechbackend.enumiration.StockTransferStatus;

import java.util.List;

public interface TransferService {
    List<StockTransferResponseDTO> getTransfers(StockTransferStatus status, String branchId);
    StockTransferResponseDTO getTransferById(String id);
    StockTransferResponseDTO initiateTransfer(String username, StockTransferRequestDTO request);
    StockTransferResponseDTO updateTransferStatus(String username, String transferId, TransferStatusUpdateDTO request);
    TransferMetricsDTO getTransferMetrics();
}
