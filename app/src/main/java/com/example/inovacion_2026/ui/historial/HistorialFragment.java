package com.example.inovacion_2026.ui.historial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.inovacion_2026.R;
import com.example.inovacion_2026.data_api.ConfigRequest;
import com.example.inovacion_2026.data_api.HistorialResponse;
import com.example.inovacion_2026.data_api.PlantaItem;
import com.example.inovacion_2026.data_api.PlantasResponse;
import com.example.inovacion_2026.data_api.RegistroRiego;
import com.example.inovacion_2026.data_api.apiService;
import com.example.inovacion_2026.databinding.FragmentHistorialBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HistorialFragment extends Fragment {

    // ── SharedPreferences keys ────────────────────────────────────────────────
    private static final String PREFS_NAME     = "sigo_prefs";
    private static final String KEY_PLANTA     = "planta_activa";
    private static final String KEY_CIUDAD     = "ciudad_activa";
    private static final String DEFAULT_CIUDAD = "Jerez De Garcia Salinas";

    private FragmentHistorialBinding binding;
    private HistorialAdapter adapter;
    private apiService apiService;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    private List<String> nombresPlantas     = new ArrayList<>();
    private String plantaSeleccionada       = null;
    private boolean spinnerInicializado     = false;
    private boolean spinnerFechaInicializado = false;

    // Días a filtrar: 7, 14 o 30
    private int diasFiltro = 7;

    // Todos los registros sin filtrar (para re-filtrar al cambiar periodo)
    private List<RegistroRiego> todosLosRegistros = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistorialBinding.inflate(inflater, container, false);

        if (plantaSeleccionada != null) {
            cambiarPlantaActiva(plantaSeleccionada);
        }

        // RecyclerView
        adapter = new HistorialAdapter(new ArrayList<>());
        binding.recyclerHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerHistorial.setAdapter(adapter);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-si-go.onrender.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(apiService.class);

        // Leer planta guardada
        plantaSeleccionada = leerPlantaGuardada();

        // Configurar spinner de periodo
        configurarSpinnerFecha();

        // Cargar plantas
        cargarPlantas();

        // Polling cada 30 segundos
        runnable = new Runnable() {
            @Override
            public void run() {
                if (plantaSeleccionada != null) cargarHistorial();
                handler.postDelayed(this, 30000);
            }
        };
        handler.post(runnable);

        return binding.getRoot();
    }


    // SPINNER DE PERIODO (7, 14, 30 días)

    private void configurarSpinnerFecha() {
        List<String> periodos = new ArrayList<>();
        periodos.add("Últimos 7 días");
        periodos.add("Últimos 14 días");
        periodos.add("Este mes");
        periodos.add("Todo el tiempo");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_custom,
                periodos
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        binding.spinnerFecha.setAdapter(adapter);

        spinnerFechaInicializado = false;
        binding.spinnerFecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: diasFiltro = 7;  break;
                    case 1: diasFiltro = 14; break;
                    case 2: diasFiltro = 30; break;
                    case 3: diasFiltro = -1; break;
                }
                if (!spinnerFechaInicializado) {
                    spinnerFechaInicializado = true;
                    return;
                }
                // Re-filtrar con los datos ya descargados
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // SHARED PREFERENCES

    private String leerPlantaGuardada() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PLANTA, null);
    }

    private void guardarPlanta(String planta) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PLANTA, planta).apply();
    }

    private String leerCiudadGuardada() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_CIUDAD, DEFAULT_CIUDAD);
    }


    // CARGAR PLANTAS EN EL SPINNER

    private void cargarPlantas() {
        apiService.listarPlantas().enqueue(new Callback<PlantasResponse>() {
            @Override
            public void onResponse(Call<PlantasResponse> call, Response<PlantasResponse> response) {
                if (binding == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<PlantaItem> plantas = response.body().getPlantas();

                    if (plantas == null || plantas.isEmpty()) {
                        Toast.makeText(getContext(), "No hay plantas registradas", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    nombresPlantas.clear();
                    for (PlantaItem p : plantas) {
                        nombresPlantas.add(p.getNombre());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.spinner_item_custom,
                            nombresPlantas
                    );
                    spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
                    binding.spinnerPlantas.setAdapter(spinnerAdapter);

                    spinnerInicializado = false;
                    if (plantaSeleccionada != null) {
                        int index = nombresPlantas.indexOf(plantaSeleccionada);
                        if (index >= 0) binding.spinnerPlantas.setSelection(index, false);
                    }

                    binding.spinnerPlantas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            String seleccionada = nombresPlantas.get(position);

                            if (!spinnerInicializado) {
                                spinnerInicializado = true;
                                plantaSeleccionada  = seleccionada;
                                cargarHistorial();
                                return;
                            }

                            plantaSeleccionada = seleccionada;
                            guardarPlanta(plantaSeleccionada);
                            cambiarPlantaActiva(plantaSeleccionada);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<PlantasResponse> call, Throwable t) {
                Log.e("HISTORIAL_ERROR", "Error al cargar plantas: " + t.getMessage());
            }
        });
    }


    // CAMBIAR PLANTA ACTIVA EN LA API

    private void cambiarPlantaActiva(String nombrePlanta) {
        String ciudad = leerCiudadGuardada();
        ConfigRequest config = new ConfigRequest(ciudad, nombrePlanta);

        apiService.enviarConfiguracion(config).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "✅ Planta activa: " + nombrePlanta, Toast.LENGTH_SHORT).show();
                    cargarHistorial();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("HISTORIAL_ERROR", "Error al cambiar planta: " + t.getMessage());
                Toast.makeText(getContext(), "Error al cambiar planta activa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // CARGAR HISTORIAL COMPLETO Y FILTRAR

    private void cargarHistorial() {
        apiService.obtenerHistorial().enqueue(new Callback<HistorialResponse>() {
            @Override
            public void onResponse(Call<HistorialResponse> call, Response<HistorialResponse> response) {
                if (response.isSuccessful() && response.body() != null && binding != null) {
                    todosLosRegistros = response.body().getHistorial();
                    if (todosLosRegistros == null) todosLosRegistros = new ArrayList<>();
                    aplicarFiltros();
                }
            }

            @Override
            public void onFailure(Call<HistorialResponse> call, Throwable t) {
                Log.e("HISTORIAL_ERROR", "Fallo: " + t.getMessage());
            }
        });
    }


    // APLICAR FILTROS: planta + periodo de fechas

    private void aplicarFiltros() {
        if (binding == null) return;

        // Fecha límite según el periodo seleccionado
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -diasFiltro);
        Date fechaLimite = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        List<RegistroRiego> filtrados = new ArrayList<>();

        for (RegistroRiego r : todosLosRegistros) {
            // Filtrar por planta
            if (r.getPlanta() == null || !r.getPlanta().equalsIgnoreCase(plantaSeleccionada)) continue;

            if (diasFiltro == -1) {
                filtrados.add(r);
                continue;
            }

            // Filtrar por fecha
            try {
                Date fechaRegistro = sdf.parse(r.getFecha());
                if (fechaRegistro != null && !fechaRegistro.before(fechaLimite)) {
                    filtrados.add(r);
                }
            } catch (ParseException e) {
                // Si no se puede parsear la fecha, incluirlo igual
                filtrados.add(r);
            }
        }

        // Actualizar contador
        binding.txtContadorRiegos.setText("💧 Riegos en el periodo: " + filtrados.size());

        if (filtrados.isEmpty()) {
            binding.txtHistorialVacio.setVisibility(View.VISIBLE);
            binding.recyclerHistorial.setVisibility(View.GONE);
        } else {
            binding.txtHistorialVacio.setVisibility(View.GONE);
            binding.recyclerHistorial.setVisibility(View.VISIBLE);
            adapter.actualizar(filtrados);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && runnable != null) handler.removeCallbacks(runnable);
        binding = null;
    }
}