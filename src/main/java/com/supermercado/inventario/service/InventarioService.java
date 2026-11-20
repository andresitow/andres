package com.supermercado.inventario.service;

import com.supermercado.inventario.exception.ProductoNoEncontradoException;
import com.supermercado.inventario.exception.StockInsuficienteException;
import com.supermercado.inventario.model.Producto;
import com.supermercado.inventario.model.ProductoPerecedero;
import com.supermercado.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio del inventario. Persiste en MySQL a través de
 * {@link ProductoRepository} (inyectado por Spring).
 *
 * <p><b>Polimorfismo:</b> {@link #calcularValorizacionTotal()} invoca
 * {@code calcularPrecioFinal()} de cada subclase sin conocer el tipo concreto.</p>
 */
@Service
public class InventarioService {

    private static final int STOCK_CRITICO = 5;

    private final ProductoRepository repositorio;

    public InventarioService(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public Producto registrar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        if (repositorio.existsById(producto.getId())) {
            throw new IllegalArgumentException("Ya existe un producto con id " + producto.getId() + ".");
        }
        return repositorio.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(String id) {
        return repositorio.findById(id).orElseThrow(() ->
                new ProductoNoEncontradoException("No existe un producto con id " + id + "."));
    }

    @Transactional(readOnly = true)
    public List<Producto> listarInventario() {
        return repositorio.findAll();
    }

    @Transactional
    public Producto registrarEntrada(String id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de entrada debe ser mayor a cero.");
        }
        Producto p = buscarPorId(id);
        p.setStock(p.getStock() + cantidad);
        return repositorio.save(p);
    }

    @Transactional
    public Producto registrarSalida(String id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de salida debe ser mayor a cero.");
        }
        Producto p = buscarPorId(id);
        if (cantidad > p.getStock()) {
            throw new StockInsuficienteException(String.format(
                    "Stock insuficiente para '%s': disponible %d, solicitado %d.",
                    p.getNombre(), p.getStock(), cantidad));
        }
        p.setStock(p.getStock() - cantidad);
        return repositorio.save(p);
    }

    @Transactional
    public void eliminar(String id) {
        if (!repositorio.existsById(id)) {
            throw new ProductoNoEncontradoException("No existe un producto con id " + id + ".");
        }
        repositorio.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Producto> productosConStockCritico() {
        return repositorio.findByStockLessThan(STOCK_CRITICO);
    }

    @Transactional(readOnly = true)
    public List<ProductoPerecedero> productosProximosAVencer() {
        return repositorio.findAll().stream()
                .filter(p -> p instanceof ProductoPerecedero)
                .map(p -> (ProductoPerecedero) p)
                .filter(ProductoPerecedero::estaProximoAVencer)
                .toList();
    }

    @Transactional(readOnly = true)
    public double calcularValorizacionTotal() {
        return repositorio.findAll().stream()
                .mapToDouble(p -> p.calcularPrecioFinal() * p.getStock())
                .sum();
    }

    public int umbralStockCritico() {
        return STOCK_CRITICO;
    }

    public int diasAlertaVencimiento() {
        return ProductoPerecedero.DIAS_ALERTA_VENCIMIENTO;
    }
}
