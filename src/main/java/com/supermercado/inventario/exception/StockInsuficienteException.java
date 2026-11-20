package com.supermercado.inventario.exception;

/**
 * Excepción de negocio lanzada cuando una venta solicita más unidades de las
 * disponibles en inventario.
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
