package com.franquicias.api.domain.repository;

import com.franquicias.api.domain.dto.ProductDto;
import com.franquicias.api.domain.dto.ProductResponseDto;
import com.franquicias.api.domain.dto.StockRequestDto;
import com.franquicias.api.domain.dto.TopStockProductResponseDto;

import java.util.List;

public interface ProductRepository {

    ProductResponseDto save(Long franchiseId, Long branchId, ProductDto request);

    void delete(Long franchiseId, Long branchId, Long productId);

    ProductResponseDto updateStock(Long franchiseId, Long branchId, Long productId, StockRequestDto request);

    List<TopStockProductResponseDto> findTopStockProductPerBranch(Long franchiseId);
}
