package com.example.inovacion_2026.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.inovacion_2026.data_api.apiService;
import com.example.inovacion_2026.data_api.EstadoTotal;
import com.example.inovacion_2026.data_api.HistorialResponse;
import com.example.inovacion_2026.data_api.RegistroRiego;
import com.example.inovacion_2026.databinding.FragmentHomeBinding;
import com.example.inovacion_2026.util.WeatherEmojiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private apiService apiService;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-si-go.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(apiService.class);

        runnable = new Runnable() {
            @Override
            public void run() {
                obtenerDatosDelCerebro();
                obtenerUltimoRiego();
                handler.postDelayed(this, 10000);
            }
        };

        handler.post(runnable);

        return binding.getRoot();
    }


    private void obtenerDatosDelCerebro() {
        apiService.obtenerStatusTotal().enqueue(new Callback<EstadoTotal>() {
            @Override
            public void onResponse(Call<EstadoTotal> call, Response<EstadoTotal> response) {
                if (response.isSuccessful() && response.body() != null && binding != null) {
                    EstadoTotal data = response.body();

                    // ── Sensores ──────────────────────────────────────────────
                    binding.txtHumedadSensor.setText(String.valueOf(data.lecturas.hum_aire) + " %");
                    binding.txtLluviaSensor.setText(obtenerMensajeLuvia(data.lecturas.lluvia));
                    binding.txtTemperaturaSensor.setText(String.valueOf((int) data.lecturas.temp_aire));
                    binding.txtClima.setText(WeatherEmojiUtils.climaToEmoji(data.analisis.clima_internet));
                    binding.txtClimaDesc.setText(String.valueOf(data.analisis.clima_internet));

                    // ── Alerta ────────────────────────────────────────────────
                    evaluarAlerta(data.analisis.mensaje);

                } else {
                    Log.e("API_ERROR", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<EstadoTotal> call, Throwable t) {
                Log.e("API_ERROR", "Fallo total: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión con la API", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // LÓGICA DE ALERTA — misma que en pronósticos
    // Muestra la card solo si hay una alerta real, la oculta si todo está bien

    private void evaluarAlerta(String mensaje) {
        if (binding == null || mensaje == null) return;

        String m = mensaje.toLowerCase();
        boolean hayAlerta = m.contains("alerta")
                || m.contains("activando riego")
                || m.contains("calor extremo")
                || m.contains("helada")
                || m.contains("seco");

        binding.txtAlerta.setText(mensaje);

        if (hayAlerta) {
            // Alerta activa → fondo amarillo (ya definido en XML)
            binding.cardAlerta.setVisibility(View.VISIBLE);
            binding.cardAlerta.setCardBackgroundColor(android.graphics.Color.parseColor("#FFF3CD"));
        } else {
            // Todo bien → fondo verde suave
            binding.cardAlerta.setVisibility(View.VISIBLE);
            binding.cardAlerta.setCardBackgroundColor(android.graphics.Color.parseColor("#D1FAE5"));
            // Cambiar color del texto a verde oscuro
            binding.txtAlerta.setTextColor(android.graphics.Color.parseColor("#065F46"));
            binding.textView7.setTextColor(android.graphics.Color.parseColor("#065F46"));
        }
    }


    // ÚLTIMO RIEGO — toma el primer elemento del historial (más reciente)

    private void obtenerUltimoRiego() {
        apiService.obtenerHistorial().enqueue(new Callback<HistorialResponse>() {
            @Override
            public void onResponse(Call<HistorialResponse> call, Response<HistorialResponse> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<RegistroRiego> historial = response.body().getHistorial();

                    if (historial != null && !historial.isEmpty()) {
                        // El primero es el más reciente (la API lo devuelve ordenado DESC)
                        RegistroRiego ultimo = historial.get(0);
                        binding.txtUltimoDiaRiego.setText(ultimo.getFecha());
                        binding.txtUltimoHoraRiego.setText(ultimo.getHora());
                    } else {
                        // Sin registros aún
                        binding.txtUltimoDiaRiego.setText("Sin datos");
                        binding.txtUltimoHoraRiego.setText("--:--");
                    }
                }
            }

            @Override
            public void onFailure(Call<HistorialResponse> call, Throwable t) {
                Log.e("API_ERROR", "Error al obtener historial: " + t.getMessage());
            }
        });
    }

    private String obtenerMensajeLuvia(int valorSensor){

        String mensaje;

        if(valorSensor >= 4000){

            mensaje = "Sin Lluvia";

        }else if(valorSensor >= 3000){

            mensaje = "Lluvia Leve";

        }else if(valorSensor >= 2000){

            mensaje = "Lluvia Moderada";

        }else if(valorSensor >= 1000){

            mensaje = "Lluvia Intensa";
        } else{

            mensaje = "Sin Datos";
        }

        return mensaje;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        binding = null;
    }
}