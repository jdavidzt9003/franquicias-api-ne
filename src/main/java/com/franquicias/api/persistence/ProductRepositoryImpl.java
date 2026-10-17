package com.franquicias.api.persistence;

import com.franquicias.api.domain.dto.ProductDto;
import com.franquicias.api.domain.dto.ProductResponseDto;
import com.franquicias.api.domain.dto.StockRequestDto;
import com.franquicias.api.domain.dto.TopStockProductResponseDto;
import com.franquicias.api.domain.exception.ResourceNotFoundException;
import com.franquicias.api.domain.repository.ProductRepository;
import com.franquicias.api.persistence.crud.BranchCrudRepository;
import com.franquicias.api.persistence.crud.ProductCrudRepository;
import com.franquicias.api.persistence.entity.Branch;
import com.franquicias.api.persistence.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductCrudRepository crud;
    private final BranchCrudRepository branchCrud;

    public ProductRepositoryImpl(ProductCrudRepository crud, BranchCrudRepository branchCrud) {
        this.crud = crud;
        this.branchCrud = branchCrud;
    }

    @Override
    public ProductResponseDto save(Long franchiseId, Long branchId, ProductDto request) {
        Branch branch = branchCrud.findByIdAndFranchiseId(branchId, franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal " + branchId + " no encontrada en la franquicia " + franchiseId));
        Product saved = crud.save(new Product(request.name().trim(), request.stock(), branch));
        return toDto(saved);
    }

    @Override
    public void delete(Long franchiseId, Long branchId, Long productId) {
        crud.delete(find(franchiseId, branchId, productId));
    }

    @Override
    public ProductResponseDto updateStock(Long franchiseId, Long branchId, Long productId,
                                          StockRequestDto request) {
        Product product = find(franchiseId, branchId, productId);
        product.setStock(request.stock());
        return toDto(crud.save(product));
    }

    @Override
    public List<TopStockProductResponseDto> findTopStockProductPerBranch(Long franchiseId) {
        return crud.findTopStockProductPerBranch(franchiseId).stream()
                .map(p -> new TopStockProductResponseDto(
                        p.getBranch().getId(),
                        p.getBranch().getName(),
                        p.getId(),
                        p.getName(),
                        p.getStock()))
                .toList();
    }

    private Product find(Long franchiseId, Long branchId, Long productId) {
        return crud.findByIdAndBranchIdAndBranchFranchiseId(productId, branchId, franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto " + productId + " no encontrado en la sucursal " + branchId));
    }

    private ProductResponseDto toDto(Product p) {
        return new ProductResponseDto(p.getId(), p.getName(), p.getStock(), p.getBranch().getId());
    }
}
