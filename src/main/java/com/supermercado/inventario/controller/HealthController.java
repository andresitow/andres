package com.supermercado.inventario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Endpoint de salud que confirma que la conexión a MySQL funciona ejecutando
 * una consulta trivial contra la base de datos.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("aplicacion", "UP");
        try {
            Integer uno = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            cuerpo.put("baseDatos", uno != null && uno == 1 ? "UP" : "DOWN");
            cuerpo.put("mysqlVersion", version);
            return ResponseEntity.ok(cuerpo);
        } catch (Exception e) {
            cuerpo.put("baseDatos", "DOWN");
            cuerpo.put("error", e.getMessage());
            return ResponseEntity.status(503).body(cuerpo);
        }
    }
}
