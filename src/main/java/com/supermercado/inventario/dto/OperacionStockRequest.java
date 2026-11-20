package com.supermercado.inventario.dto;

import jakarta.validation.constraints.Positive;

/** Cantidad para una operación de entrada o salida de stock. */
public record OperacionStockRequest(
        @Positive(message = "La cantidad debe ser mayor a cero.") int cantidad) {
}
