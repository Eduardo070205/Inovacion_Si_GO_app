// ── AgregarPlantaRequest.java ─────────────────────────────────────────────────
package com.example.inovacion_2026.data_api;

public class AgregarPlantaRequest {
    private String nombre;

    public AgregarPlantaRequest(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { return nombre; }
}