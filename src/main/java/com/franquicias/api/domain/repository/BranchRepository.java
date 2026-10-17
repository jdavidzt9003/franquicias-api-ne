package com.franquicias.api.domain.repository;

import com.franquicias.api.domain.dto.BranchResponseDto;
import com.franquicias.api.domain.dto.NameRequestDto;

public interface BranchRepository {

    BranchResponseDto save(Long franchiseId, NameRequestDto request);

    BranchResponseDto updateName(Long franchiseId, Long branchId, NameRequestDto request);
}
