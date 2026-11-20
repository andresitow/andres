package com.supermercado.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Frutas y verduras vendidas por peso. Aquí {@code precioBase} se interpreta
 * como precio por kilo.
 *
 * <p><b>Sobrescritura / polimorfismo:</b> redefine {@link #calcularPrecioFinal()}
 * multiplicando precio por kilo por el peso y sumando 10% si es orgánico.</p>
 */
@Entity
@DiscriminatorValue("FRUVER")
public class ProductoFruver extends Producto {

    private static final double RECARGO_ORGANICO = 0.10;

    @Column(name = "peso_en_kilos")
    private double pesoEnKilos;

    @Column(name = "es_organico")
    private boolean esOrganico;

    protected ProductoFruver() {
    }

    public ProductoFruver(String id, String nombre, double precioPorKilo, int stock,
                          String codigoBarras, double pesoEnKilos, boolean esOrganico) {
        super(id, nombre, precioPorKilo, stock, codigoBarras);
        setPesoEnKilos(pesoEnKilos);
        this.esOrganico = esOrganico;
    }

    public double getPesoEnKilos() {
        return pesoEnKilos;
    }

    public void setPesoEnKilos(double pesoEnKilos) {
        if (pesoEnKilos <= 0) {
            throw new IllegalArgumentException("El peso en kilos debe ser mayor a cero.");
        }
        this.pesoEnKilos = pesoEnKilos;
    }

    public boolean isEsOrganico() {
        return esOrganico;
    }

    public void setEsOrganico(boolean esOrganico) {
        this.esOrganico = esOrganico;
    }

    @Override
    public double calcularPrecioFinal() {
        double precio = getPrecioBase() * pesoEnKilos;
        if (esOrganico) {
            precio *= (1 + RECARGO_ORGANICO);
        }
        return precio;
    }

    @Override
    public String mostrarDetalle() {
        return String.format("%s | FRUVER | $%.2f/kg | peso: %.3f kg | orgánico: %s | precio final: $%.2f",
                datosBase(), getPrecioBase(), pesoEnKilos, esOrganico ? "sí (+10%)" : "no", calcularPrecioFinal());
    }

    @Override
    public String tipoProducto() {
        return "FRUVER";
    }
}
