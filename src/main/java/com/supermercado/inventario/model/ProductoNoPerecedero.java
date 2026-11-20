package com.supermercado.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Producto de larga duración (enlatados, granos, aseo).
 *
 * <p><b>Sobrescritura / polimorfismo:</b> redefine {@link #calcularPrecioFinal()}
 * para sumar el IVA estándar del 19%.</p>
 */
@Entity
@DiscriminatorValue("NO_PERECEDERO")
public class ProductoNoPerecedero extends Producto {

    private static final double IVA = 0.19;

    @Column(name = "categoria_pasillo")
    private String categoriaPasillo;

    @Column(name = "peso_neto_kg")
    private double pesoNetoKg;

    protected ProductoNoPerecedero() {
    }

    public ProductoNoPerecedero(String id, String nombre, double precioBase, int stock,
                                String codigoBarras, String categoriaPasillo, double pesoNetoKg) {
        super(id, nombre, precioBase, stock, codigoBarras);
        setCategoriaPasillo(categoriaPasillo);
        setPesoNetoKg(pesoNetoKg);
    }

    public String getCategoriaPasillo() {
        return categoriaPasillo;
    }

    public void setCategoriaPasillo(String categoriaPasillo) {
        if (categoriaPasillo == null || categoriaPasillo.isBlank()) {
            throw new IllegalArgumentException("La categoría de pasillo no puede estar vacía.");
        }
        this.categoriaPasillo = categoriaPasillo.trim();
    }

    public double getPesoNetoKg() {
        return pesoNetoKg;
    }

    public void setPesoNetoKg(double pesoNetoKg) {
        if (pesoNetoKg <= 0) {
            throw new IllegalArgumentException("El peso neto debe ser mayor a cero.");
        }
        this.pesoNetoKg = pesoNetoKg;
    }

    @Override
    public double calcularPrecioFinal() {
        return getPrecioBase() * (1 + IVA);
    }

    @Override
    public String mostrarDetalle() {
        return String.format("%s | NO PERECEDERO | pasillo: %s | peso neto: %.3f kg | precio final (IVA 19%%): $%.2f",
                datosBase(), categoriaPasillo, pesoNetoKg, calcularPrecioFinal());
    }

    @Override
    public String tipoProducto() {
        return "NO_PERECEDERO";
    }
}
