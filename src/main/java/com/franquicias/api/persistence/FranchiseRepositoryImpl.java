package com.franquicias.api.persistence;

import com.franquicias.api.domain.dto.FranchiseResponseDto;
import com.franquicias.api.domain.dto.NameRequestDto;
import com.franquicias.api.domain.exception.ResourceNotFoundException;
import com.franquicias.api.domain.repository.FranchiseRepository;
import com.franquicias.api.persistence.crud.FranchiseCrudRepository;
import com.franquicias.api.persistence.entity.Franchise;
import org.springframework.stereotype.Repository;

@Repository
public class FranchiseRepositoryImpl implements FranchiseRepository {

    private final FranchiseCrudRepository crud;

    public FranchiseRepositoryImpl(FranchiseCrudRepository crud) {
        this.crud = crud;
    }

    @Override
    public FranchiseResponseDto save(NameRequestDto request) {
        Franchise saved = crud.save(new Franchise(request.name().trim()));
        return toDto(saved);
    }

    @Override
    public FranchiseResponseDto updateName(Long franchiseId, NameRequestDto request) {
        Franchise franchise = crud.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franquicia no encontrada: " + franchiseId));
        franchise.setName(request.name().trim());
        return toDto(crud.save(franchise));
    }

    @Override
    public boolean existsById(Long franchiseId) {
        return crud.existsById(franchiseId);
    }

    private FranchiseResponseDto toDto(Franchise f) {
        return new FranchiseResponseDto(f.getId(), f.getName());
    }
}
