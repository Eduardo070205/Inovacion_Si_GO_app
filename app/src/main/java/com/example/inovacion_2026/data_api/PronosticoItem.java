package com.example.inovacion_2026.data_api;

public class PronosticoItem {

    private String fecha;
    private double temperatura;
    private String descripcion;

    private double temp_min;
    private double temp_max;

    public double getTemp_min() {
        return temp_min;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public String getFecha() {
        return fecha;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public String getDescripcion() {
        return descripcion;
    }
}