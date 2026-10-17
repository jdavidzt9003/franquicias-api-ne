package com.franquicias.api.domain.repository;

import com.franquicias.api.domain.dto.FranchiseResponseDto;
import com.franquicias.api.domain.dto.NameRequestDto;

public interface FranchiseRepository {

    FranchiseResponseDto save(NameRequestDto request);

    FranchiseResponseDto updateName(Long franchiseId, NameRequestDto request);

    boolean existsById(Long franchiseId);
}
