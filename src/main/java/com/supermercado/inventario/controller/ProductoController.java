package com.supermercado.inventario.controller;

import com.supermercado.inventario.dto.OperacionStockRequest;
import com.supermercado.inventario.dto.ProductoRequest;
import com.supermercado.inventario.dto.ProductoResponse;
import com.supermercado.inventario.model.Producto;
import com.supermercado.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * API REST del inventario. Expone el CRUD de productos y consultas de negocio
 * respaldadas por MySQL a través de {@link InventarioService}.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final InventarioService service;

    public ProductoController(InventarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return service.listarInventario().stream().map(ProductoResponse::de).toList();
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable String id) {
        return ProductoResponse.de(service.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
        Producto creado = service.registrar(request.aEntidad());
        return ProductoResponse.de(creado);
    }

    @PostMapping("/{id}/entradas")
    public ProductoResponse registrarEntrada(@PathVariable String id,
                                             @Valid @RequestBody OperacionStockRequest request) {
        return ProductoResponse.de(service.registrarEntrada(id, request.cantidad()));
    }

    @PostMapping("/{id}/salidas")
    public ProductoResponse registrarSalida(@PathVariable String id,
                                            @Valid @RequestBody OperacionStockRequest request) {
        return ProductoResponse.de(service.registrarSalida(id, request.cantidad()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminar(id);
    }

    @GetMapping("/stock-critico")
    public List<ProductoResponse> stockCritico() {
        return service.productosConStockCritico().stream().map(ProductoResponse::de).toList();
    }

    @GetMapping("/proximos-a-vencer")
    public List<ProductoResponse> proximosAVencer() {
        return service.productosProximosAVencer().stream().map(ProductoResponse::de).toList();
    }

    @GetMapping("/valorizacion")
    public ResponseEntity<Map<String, Object>> valorizacion() {
        return ResponseEntity.ok(Map.of(
                "valorizacionTotal", service.calcularValorizacionTotal(),
                "umbralStockCritico", service.umbralStockCritico(),
                "diasAlertaVencimiento", service.diasAlertaVencimiento()));
    }
}
