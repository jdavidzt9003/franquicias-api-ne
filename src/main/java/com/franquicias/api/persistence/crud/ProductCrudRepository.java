package com.franquicias.api.persistence.crud;

import com.franquicias.api.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductCrudRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndBranchIdAndBranchFranchiseId(Long id, Long branchId, Long franchiseId);

    /**
     * Productos con mayor stock de cada sucursal de una franquicia.
     * Si hay empate dentro de una sucursal se devuelven todos los productos empatados.
     */
    @Query("""
            select p from Product p
            join fetch p.branch b
            where b.franchise.id = :franchiseId
              and p.stock = (select max(p2.stock) from Product p2 where p2.branch.id = b.id)
            order by b.name asc, p.name asc
            """)
    List<Product> findTopStockProductPerBranch(@Param("franchiseId") Long franchiseId);
}
