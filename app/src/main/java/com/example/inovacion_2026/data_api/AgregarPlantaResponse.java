package com.example.inovacion_2026.data_api;

public class AgregarPlantaResponse {
    private String status;
    private String planta;
    private String mensaje;
    private boolean info_real;

    public String getStatus()    { return status; }
    public String getPlanta()    { return planta; }
    public String getMensaje()   { return mensaje; }
    public boolean isInfo_real() { return info_real; }
}