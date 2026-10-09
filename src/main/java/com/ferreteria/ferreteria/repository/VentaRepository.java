
package com.ferreteria.ferreteria.repository;

import com.ferreteria.ferreteria.model.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = {"producto"})
    List<Venta> findAllByOrderByFechaDesc();

    @EntityGraph(attributePaths = {"producto"})
    Optional<Venta> findWithProductoById(Long id);
}
