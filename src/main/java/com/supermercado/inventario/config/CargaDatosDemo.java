package com.supermercado.inventario.config;

import com.supermercado.inventario.model.ProductoFruver;
import com.supermercado.inventario.model.ProductoNoPerecedero;
import com.supermercado.inventario.model.ProductoPerecedero;
import com.supermercado.inventario.repository.ProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Siembra 6 productos de ejemplo al arrancar si la tabla está vacía. Así se
 * comprueba de inmediato que la escritura y lectura contra MySQL funcionan.
 * Se desactiva con el perfil {@code test}.
 */
@Component
@Profile("!test")
public class CargaDatosDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(CargaDatosDemo.class);

    private final ProductoRepository repositorio;

    public CargaDatosDemo(ProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            log.info("La tabla 'productos' ya tiene datos ({} filas); no se cargan datos demo.",
                    repositorio.count());
            return;
        }

        LocalDate hoy = LocalDate.now();
        repositorio.saveAll(List.of(
                new ProductoPerecedero("P01", "Leche entera 1L", 3800, 12, "7701001", hoy.plusDays(2), true),
                new ProductoPerecedero("P02", "Yogur natural", 2500, 3, "7701002", hoy.plusDays(20), true),
                new ProductoNoPerecedero("N01", "Atún en lata", 6500, 40, "7702001", "Enlatados", 0.170),
                new ProductoNoPerecedero("N02", "Arroz blanco 1kg", 4200, 4, "7702002", "Granos", 1.0),
                new ProductoFruver("F01", "Banano", 2600, 80, "7703001", 1.5, false),
                new ProductoFruver("F02", "Tomate orgánico", 5200, 25, "7703002", 2.0, true)));

        log.info("Datos demo cargados en MySQL: {} productos.", repositorio.count());
    }
}
