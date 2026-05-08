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
import com.example.inovacion_2026.databinding.FragmentHomeBinding;
import com.example.inovacion_2026.util.WeatherEmojiUtils;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private apiService apiService;

    // 1. Definimos el Handler y el Runnable
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);



        // Configuración de Retrofit (se hace una sola vez)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.20:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(apiService.class);

        // 2. Definimos la tarea que se repetirá
        runnable = new Runnable() {
            @Override
            public void run() {
                obtenerDatosDelCerebro();
                // Programamos la siguiente ejecución en 10 segundos
                handler.postDelayed(this, 10000);
            }
        };

        // 3. Iniciamos el ciclo por primera vez
        handler.post(runnable);




        return binding.getRoot();
    }

    private void obtenerDatosDelCerebro() {
        apiService.obtenerStatusTotal().enqueue(new Callback<EstadoTotal>() {
            @Override
            public void onResponse(Call<EstadoTotal> call, Response<EstadoTotal> response) {
                // Verificamos que el binding no sea nulo antes de actualizar la UI
                if (response.isSuccessful() && response.body() != null && binding != null) {
                    EstadoTotal data = response.body();
                    binding.txtAlerta.setText(String.valueOf(data.analisis.mensaje));
                    binding.txtHumedadSensor.setText(String.valueOf(data.lecturas.hum_aire));
                    binding.txtLluviaSensor.setText(String.valueOf(data.lecturas.lluvia));
                    binding.txtTemperaturaSensor.setText(String.valueOf((int)data.lecturas.temp_aire));
                    binding.txtClima.setText(WeatherEmojiUtils.climaToEmoji(data.analisis.clima_internet));
                    binding.txtClimaDesc.setText(String.valueOf(data.analisis.clima_internet));
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 4. IMPORTANTE: Detenemos el Handler para evitar fugas de memoria
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        binding = null;
    }
}