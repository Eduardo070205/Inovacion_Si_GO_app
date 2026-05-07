package com.example.inovacion_2026.data_api;

public class EstadoTotal {
    public Configuracion configuracion;
    public Lecturas lecturas;
    public Analisis analisis;

    public class Configuracion {
        public String ciudad;
        public String planta;
    }

    public class Lecturas {
        public int humedad_suelo;
        public int lluvia;
        public float temp_aire;
        public float hum_aire;
    }

    public class Analisis {
        public String mensaje;
        public String clima_internet;
    }
}
