package com.example.inovacion_2026.data_api;

public class RegistroRiego {
    private String fecha;
    private String hora;
    private String planta;
    private float litros;
    private float temperatura;

    public String getFecha()       { return fecha; }
    public String getHora()        { return hora; }
    public String getPlanta()      { return planta; }
    public float getLitros()       { return litros; }
    public float getTemperatura()  { return temperatura; }
}