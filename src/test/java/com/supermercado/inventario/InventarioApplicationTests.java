package com.supermercado.inventario;

import com.supermercado.inventario.model.ProductoFruver;
import com.supermercado.inventario.model.ProductoNoPerecedero;
import com.supermercado.inventario.model.ProductoPerecedero;
import com.supermercado.inventario.service.InventarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class InventarioApplicationTests {

    @Autowired
    InventarioService service;

    @Test
    void contextoArranca() {
        assertThat(service).isNotNull();
    }

    @Test
    void persisteYRecuperaPreciosPolimorficos() {
        service.registrar(new ProductoNoPerecedero("T-N", "Atún", 1000, 10, "100", "Enlatados", 0.2));
        service.registrar(new ProductoFruver("T-F", "Tomate", 2000, 10, "200", 2.0, true));
        service.registrar(new ProductoPerecedero("T-P", "Leche", 3000, 2, "300",
                LocalDate.now().plusDays(1), true));

        // No perecedero: +19% IVA
        assertThat(service.buscarPorId("T-N").calcularPrecioFinal()).isEqualTo(1190.0);
        // Fruver orgánico: 2000 * 2.0 * 1.10
        assertThat(service.buscarPorId("T-F").calcularPrecioFinal()).isEqualTo(4400.0);
        // Perecedero próximo a vencer: -30%
        assertThat(service.buscarPorId("T-P").calcularPrecioFinal()).isEqualTo(2100.0);
    }

    @Test
    void stockInsuficienteLanzaExcepcion() {
        service.registrar(new ProductoFruver("T-X", "Banano", 1000, 5, "400", 1.0, false));
        assertThatThrownBy(() -> service.registrarSalida("T-X", 10))
                .hasMessageContaining("Stock insuficiente");
    }
}
