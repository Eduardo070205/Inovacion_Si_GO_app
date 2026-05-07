package com.example.inovacion_2026.data_api;

import java.util.List;

public class PronosticoResponse {

    private String ciudad;

    private List<PronosticoItem> proximas_horas;

    private List<PronosticoItem> proximos_3_dias;

    public String getCiudad() {
        return ciudad;
    }

    public List<PronosticoItem> getProximas_horas() {
        return proximas_horas;
    }

    public List<PronosticoItem> getProximos_3_dias() {
        return proximos_3_dias;
    }
}