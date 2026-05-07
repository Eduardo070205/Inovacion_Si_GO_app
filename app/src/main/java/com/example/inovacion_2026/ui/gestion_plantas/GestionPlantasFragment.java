package com.example.inovacion_2026.ui.gestion_plantas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.inovacion_2026.databinding.FragmentGestionPlantasBinding;

public class GestionPlantasFragment extends Fragment{

    private FragmentGestionPlantasBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_configuration, container, false);

        GestionPlantasViewModel configViewModel = new ViewModelProvider(this).get(GestionPlantasViewModel.class);

        binding = FragmentGestionPlantasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textConfig;
        configViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return  root;

    }

    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }


}
