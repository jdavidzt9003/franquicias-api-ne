package com.franquicias.api.domain.dto;

public record TopStockProductResponseDto(
        Long branchId,
        String branchName,
        Long productId,
        String productName,
        int stock) {
}
