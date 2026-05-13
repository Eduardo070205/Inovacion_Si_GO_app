package com.example.inovacion_2026.ui.pronostico;

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

import com.example.inovacion_2026.data_api.PronosticoItem;
import com.example.inovacion_2026.data_api.PronosticoResponse;
import com.example.inovacion_2026.data_api.apiService;
import com.example.inovacion_2026.databinding.FragmentPronosticoBinding;
import com.example.inovacion_2026.util.WeatherEmojiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PronosticoFragment extends Fragment {

    private FragmentPronosticoBinding binding;

    private apiService apiService;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPronosticoBinding.inflate(inflater, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-si-go.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(apiService.class);

        runnable = new Runnable() {
            @Override
            public void run() {
                obtenerPronostico();

                handler.postDelayed(this, (3600*1000));
            }
        };



        handler.post(runnable);

        return binding.getRoot();
    }

    private void obtenerPronostico() {
        apiService.obtenerPronostico().enqueue(new Callback<PronosticoResponse>() {

            @Override
            public void onResponse(Call<PronosticoResponse> call, Response<PronosticoResponse> response) {

                if (response.isSuccessful() && response.body() != null && binding != null) {
                    PronosticoResponse datos = response.body();

                    PronosticoItem primeraHora = datos.getProximas_horas().get(0);
                    PronosticoItem segundaHora = datos.getProximas_horas().get(1);
                    PronosticoItem terceraHora = datos.getProximas_horas().get(2);
                    PronosticoItem cuartaHora = datos.getProximas_horas().get(3);
                    PronosticoItem quintaHora = datos.getProximas_horas().get(4);

                    binding.txtHora1.setText(String.valueOf(primeraHora.getFecha()));
                    binding.txtHora2.setText(String.valueOf(segundaHora.getFecha()));
                    binding.txtHora3.setText(String.valueOf(terceraHora.getFecha()));
                    binding.txtHora4.setText(String.valueOf(cuartaHora.getFecha()));
                    binding.txtHora5.setText(String.valueOf(quintaHora.getFecha()));

                    binding.txtClmH1.setText(WeatherEmojiUtils.climaToEmoji(primeraHora.getDescripcion()));
                    binding.txtClmH2.setText(WeatherEmojiUtils.climaToEmoji(segundaHora.getDescripcion()));
                    binding.txtClmH3.setText(WeatherEmojiUtils.climaToEmoji(terceraHora.getDescripcion()));
                    binding.txtClmH4.setText(WeatherEmojiUtils.climaToEmoji(cuartaHora.getDescripcion()));
                    binding.txtClmH5.setText(WeatherEmojiUtils.climaToEmoji(quintaHora.getDescripcion()));

                    binding.txtTmpH1.setText(String.valueOf(Math.round(primeraHora.getTemperatura())) + " °");
                    binding.txtTmpH2.setText(String.valueOf(Math.round(segundaHora.getTemperatura())) + " °");
                    binding.txtTmpH3.setText(String.valueOf(Math.round(terceraHora.getTemperatura())) + " °");
                    binding.txtTmpH4.setText(String.valueOf(Math.round(cuartaHora.getTemperatura())) + " °");
                    binding.txtTmpH5.setText(String.valueOf(Math.round(quintaHora.getTemperatura())) + " °");


                    PronosticoItem primerDia = datos.getProximos_3_dias().get(0);
                    PronosticoItem segundoDia = datos.getProximos_3_dias().get(1);
                    PronosticoItem tercerDia = datos.getProximos_3_dias().get(2);

                    binding.txtFechaDia1.setText(String.valueOf(primerDia.getFecha()));
                    binding.txtFechaDia2.setText(String.valueOf(segundoDia.getFecha()));
                    binding.txtFechaDia3.setText(String.valueOf(tercerDia.getFecha()));

                    binding.txtMaxDia1.setText(String.valueOf(Math.round(primerDia.getTemp_max())) + " °");
                    binding.txtMaxDia2.setText(String.valueOf(Math.round(segundoDia.getTemp_max())) + " °");
                    binding.txtMaxDia3.setText(String.valueOf(Math.round(tercerDia.getTemp_max())) + " °");

                    binding.txtMinDia1.setText(String.valueOf(Math.round(primerDia.getTemp_min())) + " °");
                    binding.txtMinDia2.setText(String.valueOf(Math.round(segundoDia.getTemp_min())) + " °");
                    binding.txtMinDia3.setText(String.valueOf(Math.round(tercerDia.getTemp_min())) + " °");

                    binding.txtClmDia1.setText(WeatherEmojiUtils.climaToEmoji(primerDia.getDescripcion()));
                    binding.txtClmDia2.setText(WeatherEmojiUtils.climaToEmoji(segundoDia.getDescripcion()));
                    binding.txtClmDia3.setText(WeatherEmojiUtils.climaToEmoji(tercerDia.getDescripcion()));

                    evaluarAlertaPronostico(primerDia, segundoDia, tercerDia);


                } else {
                    Log.e("API_ERROR", "Error en la respuesta: " + response.code());
                }

            }

            @Override
            public void onFailure(Call<PronosticoResponse> call, Throwable t) {

                Log.e("API_ERROR", "Fallo total: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión con la API", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void evaluarAlertaPronostico(PronosticoItem dia1, PronosticoItem dia2, PronosticoItem dia3) {

        StringBuilder alerta = new StringBuilder();

        // Umbrales configurables
        float TEMP_CALOR    = 35f;   // °C — calor extremo
        float TEMP_HELADA   = 5f;    // °C — riesgo de helada
        boolean hayAlerta   = false;

        PronosticoItem[] dias = { dia1, dia2, dia3 };
        String[] nombres = { "Mañana", "Pasado mañana", "En 3 días" };

        for (int i = 0; i < dias.length; i++) {
            float maxima = (float) dias[i].getTemp_max();
            float minima = (float) dias[i].getTemp_min();
            String fecha = dias[i].getFecha();

            if (maxima >= TEMP_CALOR) {
                alerta.append("🌡️ Calor extremo el ").append(fecha)
                        .append(" (").append(Math.round(maxima)).append("°). Riega temprano.\n");
                hayAlerta = true;
            }

            if (minima <= TEMP_HELADA) {
                alerta.append("❄️ Riesgo de helada el ").append(fecha)
                        .append(" (").append(Math.round(minima)).append("°). Protege tus plantas.\n");
                hayAlerta = true;
            }
        }

        if (binding == null) return;

        if (hayAlerta) {
            // Quitar el último salto de línea
            String mensaje = alerta.toString().trim();
            binding.txtAlertaPronostico.setText(mensaje);
            binding.cardAlertaPronostico.setVisibility(View.VISIBLE);
        } else {
            binding.cardAlertaPronostico.setVisibility(View.GONE);
        }
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