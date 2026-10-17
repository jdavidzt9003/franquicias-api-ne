package com.franquicias.api.persistence;

import com.franquicias.api.domain.dto.BranchResponseDto;
import com.franquicias.api.domain.dto.NameRequestDto;
import com.franquicias.api.domain.exception.ResourceNotFoundException;
import com.franquicias.api.domain.repository.BranchRepository;
import com.franquicias.api.persistence.crud.BranchCrudRepository;
import com.franquicias.api.persistence.crud.FranchiseCrudRepository;
import com.franquicias.api.persistence.entity.Branch;
import com.franquicias.api.persistence.entity.Franchise;
import org.springframework.stereotype.Repository;

@Repository
public class BranchRepositoryImpl implements BranchRepository {

    private final BranchCrudRepository crud;
    private final FranchiseCrudRepository franchiseCrud;

    public BranchRepositoryImpl(BranchCrudRepository crud, FranchiseCrudRepository franchiseCrud) {
        this.crud = crud;
        this.franchiseCrud = franchiseCrud;
    }

    @Override
    public BranchResponseDto save(Long franchiseId, NameRequestDto request) {
        Franchise franchise = franchiseCrud.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franquicia no encontrada: " + franchiseId));
        return toDto(crud.save(new Branch(request.name().trim(), franchise)));
    }

    @Override
    public BranchResponseDto updateName(Long franchiseId, Long branchId, NameRequestDto request) {
        Branch branch = crud.findByIdAndFranchiseId(branchId, franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sucursal " + branchId + " no encontrada en la franquicia " + franchiseId));
        branch.setName(request.name().trim());
        return toDto(crud.save(branch));
    }

    private BranchResponseDto toDto(Branch b) {
        return new BranchResponseDto(b.getId(), b.getName(), b.getFranchise().getId());
    }
}
