package com.supermercado.inventario.dto;

import com.supermercado.inventario.model.Producto;
import com.supermercado.inventario.model.ProductoFruver;
import com.supermercado.inventario.model.ProductoNoPerecedero;
import com.supermercado.inventario.model.ProductoPerecedero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

/**
 * Datos de entrada para crear un producto. El campo {@code tipo} decide qué
 * subclase se instancia; los campos específicos se validan en
 * {@link #aEntidad()} según ese tipo.
 */
public class ProductoRequest {

    public enum Tipo { PERECEDERO, NO_PERECEDERO, FRUVER }

    @NotNull(message = "El tipo es obligatorio (PERECEDERO, NO_PERECEDERO o FRUVER).")
    private Tipo tipo;

    @NotBlank(message = "El id es obligatorio.")
    private String id;

    @NotBlank(message = "El nombre es obligatorio.")
    private String nombre;

    @PositiveOrZero(message = "El precio base no puede ser negativo.")
    private double precioBase;

    @PositiveOrZero(message = "El stock no puede ser negativo.")
    private int stock;

    @NotBlank(message = "El código de barras es obligatorio.")
    private String codigoBarras;

    // Perecedero
    private LocalDate fechaCaducidad;
    private boolean requiereRefrigeracion;

    // No perecedero
    private String categoriaPasillo;
    private double pesoNetoKg;

    // Fruver
    private double pesoEnKilos;
    private boolean esOrganico;

    /** Construye la subclase concreta a partir del tipo indicado. */
    public Producto aEntidad() {
        return switch (tipo) {
            case PERECEDERO -> new ProductoPerecedero(id, nombre, precioBase, stock, codigoBarras,
                    fechaCaducidad, requiereRefrigeracion);
            case NO_PERECEDERO -> new ProductoNoPerecedero(id, nombre, precioBase, stock, codigoBarras,
                    categoriaPasillo, pesoNetoKg);
            case FRUVER -> new ProductoFruver(id, nombre, precioBase, stock, codigoBarras,
                    pesoEnKilos, esOrganico);
        };
    }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(LocalDate fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }

    public boolean isRequiereRefrigeracion() { return requiereRefrigeracion; }
    public void setRequiereRefrigeracion(boolean requiereRefrigeracion) { this.requiereRefrigeracion = requiereRefrigeracion; }

    public String getCategoriaPasillo() { return categoriaPasillo; }
    public void setCategoriaPasillo(String categoriaPasillo) { this.categoriaPasillo = categoriaPasillo; }

    public double getPesoNetoKg() { return pesoNetoKg; }
    public void setPesoNetoKg(double pesoNetoKg) { this.pesoNetoKg = pesoNetoKg; }

    public double getPesoEnKilos() { return pesoEnKilos; }
    public void setPesoEnKilos(double pesoEnKilos) { this.pesoEnKilos = pesoEnKilos; }

    public boolean isEsOrganico() { return esOrganico; }
    public void setEsOrganico(boolean esOrganico) { this.esOrganico = esOrganico; }
}
