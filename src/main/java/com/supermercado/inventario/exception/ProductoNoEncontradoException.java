package com.supermercado.inventario.exception;

/** Excepción lanzada cuando no existe un producto con el id solicitado. */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
