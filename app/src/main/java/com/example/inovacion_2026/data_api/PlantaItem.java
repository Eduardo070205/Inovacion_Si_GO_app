// ── PlantaItem.java ──────────────────────────────────────────────────────────
package com.example.inovacion_2026.data_api;

public class PlantaItem {
    private int id;
    private String nombre;
    private int umbral_riego;
    private float temp_min_recomendada;
    private float temp_max_recomendada;

    public int getId()                    { return id; }
    public String getNombre()             { return nombre; }
    public int getUmbral_riego()          { return umbral_riego; }
    public float getTemp_min_recomendada(){ return temp_min_recomendada; }
    public float getTemp_max_recomendada(){ return temp_max_recomendada; }
}