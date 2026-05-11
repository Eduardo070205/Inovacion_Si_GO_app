package com.example.inovacion_2026.ui.gestion_plantas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inovacion_2026.R;
import com.example.inovacion_2026.data_api.PlantaItem;

import java.util.List;

public class PlantasAdapter extends RecyclerView.Adapter<PlantasAdapter.ViewHolder> {

    public interface OnEliminarListener { void onEliminar(PlantaItem planta); }
    public interface OnDetalleListener  { void onDetalle(PlantaItem planta); }

    private List<PlantaItem> plantas;
    private final OnEliminarListener eliminarListener;
    private final OnDetalleListener  detalleListener;

    public PlantasAdapter(List<PlantaItem> plantas,
                          OnEliminarListener eliminarListener,
                          OnDetalleListener detalleListener) {
        this.plantas          = plantas;
        this.eliminarListener = eliminarListener;
        this.detalleListener  = detalleListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlantaItem p = plantas.get(position);

        holder.txtNombre.setText(p.getNombre());
        holder.txtUmbral.setText(String.valueOf(p.getUmbral_riego()));
        holder.txtTmpMin.setText(p.getTemp_min_recomendada() + "°");
        holder.txtTmpMax.setText(p.getTemp_max_recomendada() + "°");

        // Click en la fila → ver detalle
        holder.itemView.setOnClickListener(v -> detalleListener.onDetalle(p));

        // Click en el botón eliminar
        holder.btnEliminar.setOnClickListener(v -> eliminarListener.onEliminar(p));
    }

    @Override
    public int getItemCount() { return plantas.size(); }

    public void actualizar(List<PlantaItem> nuevas) {
        this.plantas = nuevas;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtUmbral, txtTmpMin, txtTmpMax, btnEliminar;

        ViewHolder(View itemView) {
            super(itemView);
            txtNombre   = itemView.findViewById(R.id.txt_planta_nombre);
            txtUmbral   = itemView.findViewById(R.id.txt_planta_umbral);
            txtTmpMin   = itemView.findViewById(R.id.txt_planta_tmp_min);
            txtTmpMax   = itemView.findViewById(R.id.txt_planta_tmp_max);
            btnEliminar = itemView.findViewById(R.id.btn_planta_eliminar);
        }
    }
}