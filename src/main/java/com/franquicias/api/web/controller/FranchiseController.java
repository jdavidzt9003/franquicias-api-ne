package com.franquicias.api.web.controller;

import com.franquicias.api.domain.dto.*;
import com.franquicias.api.domain.service.FranchiseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/franchises")
@Tag(name = "Franquicias", description = "Gestión de franquicias, sucursales y productos")
public class FranchiseController {

    private final FranchiseService service;

    public FranchiseController(FranchiseService service) {
        this.service = service;
    }

    @Operation(summary = "Crear una franquicia")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FranchiseResponseDto createFranchise(@Valid @RequestBody NameRequestDto request) {
        return service.createFranchise(request);
    }

    @Operation(summary = "Actualizar el nombre de una franquicia")
    @PatchMapping("/{franchiseId}")
    public FranchiseResponseDto updateFranchise(@PathVariable Long franchiseId,
                                                @Valid @RequestBody NameRequestDto request) {
        return service.updateFranchiseName(franchiseId, request);
    }

    @Operation(summary = "Agregar una sucursal a una franquicia")
    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public BranchResponseDto addBranch(@PathVariable Long franchiseId,
                                       @Valid @RequestBody NameRequestDto request) {
        return service.addBranch(franchiseId, request);
    }

    @Operation(summary = "Actualizar el nombre de una sucursal")
    @PatchMapping("/{franchiseId}/branches/{branchId}")
    public BranchResponseDto updateBranch(@PathVariable Long franchiseId,
                                          @PathVariable Long branchId,
                                          @Valid @RequestBody NameRequestDto request) {
        return service.updateBranchName(franchiseId, branchId, request);
    }

    @Operation(summary = "Agregar un producto a una sucursal")
    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto addProduct(@PathVariable Long franchiseId,
                                         @PathVariable Long branchId,
                                         @Valid @RequestBody ProductDto request) {
        return service.addProduct(franchiseId, branchId, request);
    }

    @Operation(summary = "Eliminar un producto de una sucursal")
    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long franchiseId,
                              @PathVariable Long branchId,
                              @PathVariable Long productId) {
        service.deleteProduct(franchiseId, branchId, productId);
    }

    @Operation(summary = "Modificar el stock de un producto")
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public ProductResponseDto updateStock(@PathVariable Long franchiseId,
                                          @PathVariable Long branchId,
                                          @PathVariable Long productId,
                                          @Valid @RequestBody StockRequestDto request) {
        return service.updateStock(franchiseId, branchId, productId, request);
    }

    @Operation(summary = "Producto con más stock por sucursal de una franquicia")
    @GetMapping("/{franchiseId}/top-stock-products")
    public List<TopStockProductResponseDto> topStockProducts(@PathVariable Long franchiseId) {
        return service.getTopStockProductPerBranch(franchiseId);
    }
}
