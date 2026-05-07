package com.example.inovacion_2026.ui.pronostico;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PronosticoViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public PronosticoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}