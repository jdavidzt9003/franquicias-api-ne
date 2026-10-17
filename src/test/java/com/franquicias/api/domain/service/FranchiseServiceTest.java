package com.franquicias.api.domain.service;

import com.franquicias.api.domain.dto.*;
import com.franquicias.api.domain.exception.ResourceNotFoundException;
import com.franquicias.api.domain.repository.BranchRepository;
import com.franquicias.api.domain.repository.FranchiseRepository;
import com.franquicias.api.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    FranchiseRepository franchiseRepository;
    @Mock
    BranchRepository branchRepository;
    @Mock
    ProductRepository productRepository;

    @InjectMocks
    FranchiseService service;

    @Test
    void createFranchise_delegaEnElRepositorio() {
        NameRequestDto request = new NameRequestDto("Burger House");
        when(franchiseRepository.save(request)).thenReturn(new FranchiseResponseDto(1L, "Burger House"));

        FranchiseResponseDto response = service.createFranchise(request);

        assertEquals(1L, response.id());
        assertEquals("Burger House", response.name());
    }

    @Test
    void updateFranchiseName_delegaEnElRepositorio() {
        NameRequestDto request = new NameRequestDto("Nuevo");
        when(franchiseRepository.updateName(1L, request)).thenReturn(new FranchiseResponseDto(1L, "Nuevo"));

        assertEquals("Nuevo", service.updateFranchiseName(1L, request).name());
    }

    @Test
    void addBranch_delegaEnElRepositorio() {
        NameRequestDto request = new NameRequestDto("Centro");
        when(branchRepository.save(1L, request)).thenReturn(new BranchResponseDto(2L, "Centro", 1L));

        BranchResponseDto response = service.addBranch(1L, request);

        assertEquals(2L, response.id());
        assertEquals(1L, response.franchiseId());
    }

    @Test
    void updateBranchName_delegaEnElRepositorio() {
        NameRequestDto request = new NameRequestDto("Chapinero");
        when(branchRepository.updateName(1L, 2L, request)).thenReturn(new BranchResponseDto(2L, "Chapinero", 1L));

        assertEquals("Chapinero", service.updateBranchName(1L, 2L, request).name());
    }

    @Test
    void addProduct_delegaEnElRepositorio() {
        ProductDto request = new ProductDto("Pizza", 10);
        when(productRepository.save(1L, 2L, request)).thenReturn(new ProductResponseDto(3L, "Pizza", 10, 2L));

        ProductResponseDto response = service.addProduct(1L, 2L, request);

        assertEquals("Pizza", response.name());
        assertEquals(10, response.stock());
    }

    @Test
    void updateStock_delegaEnElRepositorio() {
        StockRequestDto request = new StockRequestDto(42);
        when(productRepository.updateStock(1L, 2L, 3L, request))
                .thenReturn(new ProductResponseDto(3L, "Pizza", 42, 2L));

        assertEquals(42, service.updateStock(1L, 2L, 3L, request).stock());
    }

    @Test
    void deleteProduct_delegaEnElRepositorio() {
        service.deleteProduct(1L, 2L, 3L);

        verify(productRepository).delete(1L, 2L, 3L);
    }

    @Test
    void getTopStockProductPerBranch_franquiciaInexistente_lanzaNotFound() {
        when(franchiseRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.getTopStockProductPerBranch(99L));
        verify(productRepository, never()).findTopStockProductPerBranch(anyLong());
    }

    @Test
    void getTopStockProductPerBranch_retornaLaLista() {
        List<TopStockProductResponseDto> expected =
                List.of(new TopStockProductResponseDto(2L, "Centro", 3L, "Pizza", 50));
        when(franchiseRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findTopStockProductPerBranch(1L)).thenReturn(expected);

        assertEquals(expected, service.getTopStockProductPerBranch(1L));
    }
}
