package com.supermercado.inventario.dto;

import com.supermercado.inventario.model.Producto;

/**
 * Representación de salida de un producto para la API. Incluye el
 * {@code precioFinal} calculado polimórficamente y el {@code detalle} legible.
 */
public record ProductoResponse(
        String id,
        String tipo,
        String nombre,
        String codigoBarras,
        double precioBase,
        int stock,
        double precioFinal,
        String detalle) {

    public static ProductoResponse de(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.tipoProducto(),
                p.getNombre(),
                p.getCodigoBarras(),
                p.getPrecioBase(),
                p.getStock(),
                p.calcularPrecioFinal(),
                p.mostrarDetalle());
    }
}
