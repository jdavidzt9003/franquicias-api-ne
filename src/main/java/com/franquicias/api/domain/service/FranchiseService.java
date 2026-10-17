package com.franquicias.api.domain.service;

import com.franquicias.api.domain.dto.*;
import com.franquicias.api.domain.exception.ResourceNotFoundException;
import com.franquicias.api.domain.repository.BranchRepository;
import com.franquicias.api.domain.repository.FranchiseRepository;
import com.franquicias.api.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public FranchiseService(FranchiseRepository franchiseRepository,
                            BranchRepository branchRepository,
                            ProductRepository productRepository) {
        this.franchiseRepository = franchiseRepository;
        this.branchRepository = branchRepository;
        this.productRepository = productRepository;
    }

    public FranchiseResponseDto createFranchise(NameRequestDto request) {
        return franchiseRepository.save(request);
    }

    public FranchiseResponseDto updateFranchiseName(Long franchiseId, NameRequestDto request) {
        return franchiseRepository.updateName(franchiseId, request);
    }

    public BranchResponseDto addBranch(Long franchiseId, NameRequestDto request) {
        return branchRepository.save(franchiseId, request);
    }

    public BranchResponseDto updateBranchName(Long franchiseId, Long branchId, NameRequestDto request) {
        return branchRepository.updateName(franchiseId, branchId, request);
    }

    public ProductResponseDto addProduct(Long franchiseId, Long branchId, ProductDto request) {
        return productRepository.save(franchiseId, branchId, request);
    }

    public void deleteProduct(Long franchiseId, Long branchId, Long productId) {
        productRepository.delete(franchiseId, branchId, productId);
    }

    public ProductResponseDto updateStock(Long franchiseId, Long branchId, Long productId,
                                          StockRequestDto request) {
        return productRepository.updateStock(franchiseId, branchId, productId, request);
    }

    @Transactional(readOnly = true)
    public List<TopStockProductResponseDto> getTopStockProductPerBranch(Long franchiseId) {
        if (!franchiseRepository.existsById(franchiseId)) {
            throw new ResourceNotFoundException("Franquicia no encontrada: " + franchiseId);
        }
        return productRepository.findTopStockProductPerBranch(franchiseId);
    }
}
