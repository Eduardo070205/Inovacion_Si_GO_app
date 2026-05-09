package com.example.inovacion_2026.data_api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

}