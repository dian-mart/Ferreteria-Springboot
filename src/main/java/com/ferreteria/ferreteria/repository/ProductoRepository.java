package com.ferreteria.ferreteria.repository;

import com.ferreteria.ferreteria.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
