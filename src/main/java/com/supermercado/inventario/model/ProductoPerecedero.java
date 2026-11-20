package com.supermercado.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Producto con fecha de caducidad.
 *
 * <p><b>Sobrescritura / polimorfismo:</b> redefine {@link #calcularPrecioFinal()}
 * para aplicar un 30% de descuento cuando faltan 3 días o menos para caducar.</p>
 */
@Entity
@DiscriminatorValue("PERECEDERO")
public class ProductoPerecedero extends Producto {

    public static final int DIAS_ALERTA_VENCIMIENTO = 3;
    private static final double DESCUENTO_PROXIMO_VENCIMIENTO = 0.30;

    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @Column(name = "requiere_refrigeracion")
    private boolean requiereRefrigeracion;

    protected ProductoPerecedero() {
    }

    public ProductoPerecedero(String id, String nombre, double precioBase, int stock,
                              String codigoBarras, LocalDate fechaCaducidad,
                              boolean requiereRefrigeracion) {
        super(id, nombre, precioBase, stock, codigoBarras);
        setFechaCaducidad(fechaCaducidad);
        this.requiereRefrigeracion = requiereRefrigeracion;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        if (fechaCaducidad == null) {
            throw new IllegalArgumentException("La fecha de caducidad es obligatoria.");
        }
        this.fechaCaducidad = fechaCaducidad;
    }

    public boolean isRequiereRefrigeracion() {
        return requiereRefrigeracion;
    }

    public void setRequiereRefrigeracion(boolean requiereRefrigeracion) {
        this.requiereRefrigeracion = requiereRefrigeracion;
    }

    public long diasParaCaducar() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaCaducidad);
    }

    public boolean estaProximoAVencer() {
        return diasParaCaducar() <= DIAS_ALERTA_VENCIMIENTO;
    }

    @Override
    public double calcularPrecioFinal() {
        if (estaProximoAVencer()) {
            return getPrecioBase() * (1 - DESCUENTO_PROXIMO_VENCIMIENTO);
        }
        return getPrecioBase();
    }

    @Override
    public String mostrarDetalle() {
        long dias = diasParaCaducar();
        String estado = dias < 0 ? "CADUCADO" : dias + " día(s) para caducar";
        return String.format("%s | PERECEDERO | caduca: %s (%s) | refrigeración: %s | precio final: $%.2f",
                datosBase(), fechaCaducidad, estado,
                requiereRefrigeracion ? "sí" : "no", calcularPrecioFinal());
    }

    @Override
    public String tipoProducto() {
        return "PERECEDERO";
    }
}
