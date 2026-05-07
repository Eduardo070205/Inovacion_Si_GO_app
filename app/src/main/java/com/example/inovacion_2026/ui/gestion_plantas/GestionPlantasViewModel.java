package com.example.inovacion_2026.ui.gestion_plantas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GestionPlantasViewModel extends ViewModel{

    private final MutableLiveData<String> mText;

    public GestionPlantasViewModel() {
        this.mText = new MutableLiveData<>();
        mText.setValue("Pantalla de configuración");
    }

    public LiveData<String> getText(){

        return mText;
    }

}
