package com.example.inovacion_2026.ui.pronostico;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.inovacion_2026.data_api.EstadoTotal;
import com.example.inovacion_2026.data_api.PronosticoItem;
import com.example.inovacion_2026.data_api.PronosticoResponse;
import com.example.inovacion_2026.data_api.apiService;
import com.example.inovacion_2026.databinding.FragmentPronosticoBinding;

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
                .baseUrl("http://192.168.0.20:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(apiService.class);

        obtenerPronostico();

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

                    binding.txtClmH1.setText(String.valueOf(primeraHora.getDescripcion()));
                    binding.txtClmH2.setText(String.valueOf(segundaHora.getDescripcion()));
                    binding.txtClmH3.setText(String.valueOf(terceraHora.getDescripcion()));
                    binding.txtClmH4.setText(String.valueOf(cuartaHora.getDescripcion()));
                    binding.txtClmH5.setText(String.valueOf(quintaHora.getDescripcion()));

                    binding.txtTmpH1.setText(String.valueOf(primeraHora.getTemperatura()));
                    binding.txtTmpH2.setText(String.valueOf(segundaHora.getTemperatura()));
                    binding.txtTmpH3.setText(String.valueOf(terceraHora.getTemperatura()));
                    binding.txtTmpH4.setText(String.valueOf(cuartaHora.getTemperatura()));
                    binding.txtTmpH5.setText(String.valueOf(quintaHora.getTemperatura()));


                    PronosticoItem primerDia = datos.getProximos_3_dias().get(0);
                    PronosticoItem segundoDia = datos.getProximos_3_dias().get(1);
                    PronosticoItem tercerDia = datos.getProximos_3_dias().get(2);

                    binding.txtFechaDia1.setText(String.valueOf(primerDia.getFecha()));
                    binding.txtFechaDia2.setText(String.valueOf(segundoDia.getFecha()));
                    binding.txtFechaDia3.setText(String.valueOf(tercerDia.getFecha()));

                    binding.txtMaxDia1.setText(String.valueOf(primerDia.getTemp_max()));
                    binding.txtMaxDia2.setText(String.valueOf(segundoDia.getTemp_max()));
                    binding.txtMaxDia3.setText(String.valueOf(tercerDia.getTemp_max()));

                    binding.txtMinDia1.setText(String.valueOf(primerDia.getTemp_min()));
                    binding.txtMinDia2.setText(String.valueOf(segundoDia.getTemp_min()));
                    binding.txtMinDia3.setText(String.valueOf(tercerDia.getTemp_min()));

                    binding.txtClmDia1.setText(String.valueOf(primerDia.getDescripcion()));
                    binding.txtClmDia2.setText(String.valueOf(segundoDia.getDescripcion()));
                    binding.txtClmDia3.setText(String.valueOf(tercerDia.getDescripcion()));


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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}