package com.example.inovacion_2026.ui.historial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inovacion_2026.R;
import com.example.inovacion_2026.data_api.RegistroRiego;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<RegistroRiego> registros;

    public HistorialAdapter(List<RegistroRiego> registros) {
        this.registros = registros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial_riego, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RegistroRiego r = registros.get(position);
        holder.txtFecha.setText(r.getFecha());
        holder.txtHora.setText(r.getHora());
        holder.txtPlanta.setText(r.getPlanta());
        holder.txtLitros.setText(r.getLitros() + " L");
        holder.txtTemperatura.setText(Math.round(r.getTemperatura()) + "°");
    }

    @Override
    public int getItemCount() { return registros.size(); }

    // Actualizar la lista desde el Fragment
    public void actualizar(List<RegistroRiego> nuevos) {
        this.registros = nuevos;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtHora, txtPlanta, txtLitros, txtTemperatura;

        ViewHolder(View itemView) {
            super(itemView);
            txtFecha       = itemView.findViewById(R.id.txt_item_fecha);
            txtHora        = itemView.findViewById(R.id.txt_item_hora);
            txtPlanta      = itemView.findViewById(R.id.txt_item_planta);
            txtLitros      = itemView.findViewById(R.id.txt_item_litros);
            txtTemperatura = itemView.findViewById(R.id.txt_item_temperatura);
        }
    }
}