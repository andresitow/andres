package com.supermercado.inventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 * Clase base de la jerarquía de inventario, mapeada como entidad JPA.
 *
 * <p><b>Herencia (JPA SINGLE_TABLE):</b> todas las subclases se guardan en la
 * tabla {@code productos}, con la columna discriminadora {@code tipo_producto}
 * para reconstruir la subclase correcta al leer.</p>
 *
 * <p><b>Polimorfismo:</b> {@link #calcularPrecioFinal()} y
 * {@link #mostrarDetalle()} son abstractos y cada subclase los sobrescribe;
 * el servicio los invoca sin conocer el tipo concreto.</p>
 */
@Entity
@Table(name = "productos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_producto", discriminatorType = DiscriminatorType.STRING)
public abstract class Producto {

    @Id
    @Column(nullable = false, updatable = false, length = 50)
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "precio_base", nullable = false)
    private double precioBase;

    @Column(nullable = false)
    private int stock;

    @Column(name = "codigo_barras", nullable = false)
    private String codigoBarras;

    protected Producto() {
        // Requerido por JPA.
    }

    protected Producto(String id, String nombre, double precioBase, int stock, String codigoBarras) {
        setId(id);
        setNombre(nombre);
        setPrecioBase(precioBase);
        setStock(stock);
        setCodigoBarras(codigoBarras);
    }

    /**
     * Precio final de una unidad; cada subclase aplica su regla (descuentos,
     * IVA, precio por peso).
     */
    public abstract double calcularPrecioFinal();

    /** Representación legible del producto; cada subclase añade sus atributos. */
    public abstract String mostrarDetalle();

    /** Etiqueta del tipo concreto (PERECEDERO, NO_PERECEDERO, FRUVER). */
    public abstract String tipoProducto();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id no puede estar vacío.");
        }
        this.id = id.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo.");
        }
        this.precioBase = precioBase;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser menor a cero.");
        }
        this.stock = stock;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("El código de barras no puede estar vacío.");
        }
        this.codigoBarras = codigoBarras.trim();
    }

    /** Base común que las subclases reutilizan en {@link #mostrarDetalle()}. */
    protected String datosBase() {
        return String.format("[%s] %s | código: %s | stock: %d | base: $%.2f",
                id, nombre, codigoBarras, stock, precioBase);
    }
}
