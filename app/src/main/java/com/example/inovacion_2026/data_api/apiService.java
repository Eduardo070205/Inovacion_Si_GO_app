package com.example.inovacion_2026.data_api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import com.example.inovacion_2026.data_api.AgregarPlantaRequest;
import com.example.inovacion_2026.data_api.AgregarPlantaResponse;
import com.example.inovacion_2026.data_api.PlantasResponse;

public interface apiService {
    // Para que la App configure el sistema
    @POST("api/configurar")
    Call<Void> enviarConfiguracion(@Body ConfigRequest config);

    // Para que la App jale toda la información y la muestre
    @GET("api/status-total")
    Call<EstadoTotal> obtenerStatusTotal();

    @GET("api/pronostico")
    Call<PronosticoResponse> obtenerPronostico();

    @GET("api/historial")
    Call<HistorialResponse> obtenerHistorial();

    @POST("api/plantas/agregar")
    Call<AgregarPlantaResponse> agregarPlanta(@Body AgregarPlantaRequest request);

    @GET("api/plantas")
    Call<PlantasResponse> listarPlantas();

    @DELETE("api/plantas/{nombre}")
    Call<Void> eliminarPlanta(@Path("nombre") String nombre);

}