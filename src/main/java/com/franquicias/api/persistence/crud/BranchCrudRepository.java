package com.franquicias.api.persistence.crud;

import com.franquicias.api.persistence.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchCrudRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByIdAndFranchiseId(Long id, Long franchiseId);
}
