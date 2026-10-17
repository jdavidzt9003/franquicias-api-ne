package com.franquicias.api.persistence.crud;

import com.franquicias.api.persistence.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseCrudRepository extends JpaRepository<Franchise, Long> {
}
