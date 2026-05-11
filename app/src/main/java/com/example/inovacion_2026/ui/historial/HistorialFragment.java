package com.example.inovacion_2026.ui.historial;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.inovacion_2026.data_api.HistorialResponse;
import com.example.inovacion_2026.data_api.apiService;
import com.example.inovacion_2026.databinding.FragmentHistorialBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistorialFragment extends Fragment {

    private FragmentHistorialBinding binding;
    private HistorialAdapter adapter;
    private apiService apiService;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistorialBinding.inflate(inflater, container, false);

        // Configurar RecyclerView
        adapter = new HistorialAdapter(new ArrayList<>());
        binding.recyclerHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerHistorial.setAdapter(adapter);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.10:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(apiService.class);

        // Polling cada 30 segundos
        runnable = new Runnable() {
            @Override
            public void run() {
                cargarHistorial();
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(runnable);

        return binding.getRoot();
    }

    private void cargarHistorial() {
        apiService.obtenerHistorial().enqueue(new Callback<HistorialResponse>() {
            @Override
            public void onResponse(Call<HistorialResponse> call, Response<HistorialResponse> response) {
                if (response.isSuccessful() && response.body() != null && binding != null) {
                    var lista = response.body().getHistorial();

                    if (lista == null || lista.isEmpty()) {
                        binding.txtHistorialVacio.setVisibility(View.VISIBLE);
                        binding.recyclerHistorial.setVisibility(View.GONE);
                    } else {
                        binding.txtHistorialVacio.setVisibility(View.GONE);
                        binding.recyclerHistorial.setVisibility(View.VISIBLE);
                        adapter.actualizar(lista);
                    }
                }
            }

            @Override
            public void onFailure(Call<HistorialResponse> call, Throwable t) {
                Log.e("HISTORIAL_ERROR", "Fallo: " + t.getMessage());
            }
        });
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