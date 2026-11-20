package com.supermercado.inventario.repository;

import com.supermercado.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA del inventario. Spring Data implementa en tiempo de ejecución
 * las operaciones CRUD sobre la tabla {@code productos} en MySQL.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {

    /** Productos con stock estrictamente por debajo del umbral indicado. */
    List<Producto> findByStockLessThan(int umbral);
}
