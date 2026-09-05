package lk.ijse.etechbackend.service;

import lk.ijse.etechbackend.dto.branch.BranchDTO;
import lk.ijse.etechbackend.dto.branch.BranchNearestRequestDTO;
import lk.ijse.etechbackend.dto.branch.BranchNearestResponseDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getAllBranches(Boolean activeOnly);
    BranchDTO getBranchById(String id);
    BranchDTO createBranch(BranchDTO request);
    BranchDTO updateBranch(String id, BranchDTO request);
    void deleteBranch(String id);
    BranchNearestResponseDTO findNearestBranch(BranchNearestRequestDTO request);
}
