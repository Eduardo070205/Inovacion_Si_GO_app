package com.example.inovacion_2026.ui.gestion_plantas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.inovacion_2026.R;
import com.example.inovacion_2026.data_api.AgregarPlantaRequest;
import com.example.inovacion_2026.data_api.AgregarPlantaResponse;
import com.example.inovacion_2026.data_api.PlantaItem;
import com.example.inovacion_2026.data_api.PlantasResponse;
import com.example.inovacion_2026.data_api.apiService;
import com.example.inovacion_2026.databinding.FragmentGestionPlantasBinding;
import com.example.inovacion_2026.ui.gestion_plantas.PlantasAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GestionPlantasFragment extends Fragment {

    private FragmentGestionPlantasBinding binding;
    private apiService apiService;
    private PlantasAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGestionPlantasBinding.inflate(inflater, container, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.10:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(apiService.class);

        // RecyclerView
        adapter = new PlantasAdapter(new java.util.ArrayList<>(),
                this::mostrarDialogoEliminar,
                this::mostrarDialogoDetalle);
        binding.recyclerPlantas.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerPlantas.setAdapter(adapter);

        // Botón agregar
        binding.btnAgregarPlanta.setOnClickListener(v -> mostrarDialogoAgregar());

        cargarPlantas();

        return binding.getRoot();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSULTA — cargar lista
    // ═══════════════════════════════════════════════════════════════════════════
    private void cargarPlantas() {
        binding.progressPlantas.setVisibility(View.VISIBLE);

        apiService.listarPlantas().enqueue(new Callback<PlantasResponse>() {
            @Override
            public void onResponse(Call<PlantasResponse> call, Response<PlantasResponse> response) {
                if (binding == null) return;
                binding.progressPlantas.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    var lista = response.body().getPlantas();
                    if (lista == null || lista.isEmpty()) {
                        binding.txtPlantasVacio.setVisibility(View.VISIBLE);
                        binding.recyclerPlantas.setVisibility(View.GONE);
                    } else {
                        binding.txtPlantasVacio.setVisibility(View.GONE);
                        binding.recyclerPlantas.setVisibility(View.VISIBLE);
                        adapter.actualizar(lista);
                    }
                }
            }

            @Override
            public void onFailure(Call<PlantasResponse> call, Throwable t) {
                if (binding == null) return;
                binding.progressPlantas.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ALTA — diálogo con solo nombre, IA llena el resto
    // ═══════════════════════════════════════════════════════════════════════════
    private void mostrarDialogoAgregar() {
        // Input del nombre
        EditText input = new EditText(getContext());
        input.setHint("Ej: Pepino, Zanahoria, Albahaca...");
        input.setPadding(48, 32, 48, 16);

        // Loading label
        TextView txtCargando = new TextView(getContext());
        txtCargando.setText("🤖 La IA está buscando información...");
        txtCargando.setTextSize(13);
        txtCargando.setPadding(48, 0, 48, 16);
        txtCargando.setVisibility(View.GONE);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        layout.addView(txtCargando);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("🌱 Agregar planta")
                .setMessage("Escribe el nombre y la IA obtendrá los datos automáticamente")
                .setView(layout)
                .setPositiveButton("Agregar", null) // null para manejar manualmente
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String nombre = input.getText().toString().trim();
                if (nombre.isEmpty()) {
                    input.setError("Ingresa el nombre de la planta");
                    return;
                }

                // Mostrar loading y deshabilitar botón
                txtCargando.setVisibility(View.VISIBLE);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                input.setEnabled(false);

                apiService.agregarPlanta(new AgregarPlantaRequest(nombre))
                        .enqueue(new Callback<AgregarPlantaResponse>() {
                            @Override
                            public void onResponse(Call<AgregarPlantaResponse> call, Response<AgregarPlantaResponse> response) {
                                dialog.dismiss();
                                if (response.isSuccessful() && response.body() != null) {
                                    AgregarPlantaResponse res = response.body();
                                    String icono = res.isInfo_real() ? "✅" : "⚠️";
                                    // Mostrar resultado
                                    new AlertDialog.Builder(getContext())
                                            .setTitle(icono + " " + res.getPlanta() + " agregada")
                                            .setMessage(res.getMensaje())
                                            .setPositiveButton("OK", null)
                                            .show();
                                    cargarPlantas(); // Refrescar lista
                                } else if (response.code() == 400) {
                                    Toast.makeText(getContext(), "Esa planta ya existe", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<AgregarPlantaResponse> call, Throwable t) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        });

        dialog.show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSULTA DETALLE — ver datos de una planta
    // ═══════════════════════════════════════════════════════════════════════════
    private void mostrarDialogoDetalle(PlantaItem planta) {
        String info = "🌿 Nombre: " + planta.getNombre() + "\n\n" +
                "💧 Umbral de riego: " + planta.getUmbral_riego() + " ADC\n\n" +
                "🌡️ Temp. mínima recomendada: " + planta.getTemp_min_recomendada() + " °C\n\n" +
                "🔥 Temp. máxima recomendada: " + planta.getTemp_max_recomendada() + " °C";

        new AlertDialog.Builder(getContext())
                .setTitle(planta.getNombre())
                .setMessage(info)
                .setPositiveButton("Cerrar", null)
                .setNeutralButton("🗑️ Eliminar", (d, w) -> mostrarDialogoEliminar(planta))
                .show();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BAJA — confirmar y eliminar
    // ═══════════════════════════════════════════════════════════════════════════
    private void mostrarDialogoEliminar(PlantaItem planta) {
        new AlertDialog.Builder(getContext())
                .setTitle("⚠️ Eliminar planta")
                .setMessage("¿Estás seguro de eliminar \"" + planta.getNombre() + "\"?\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (d, w) -> eliminarPlanta(planta.getNombre()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarPlanta(String nombre) {
        apiService.eliminarPlanta(nombre).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "✅ Planta eliminada", Toast.LENGTH_SHORT).show();
                    cargarPlantas();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}