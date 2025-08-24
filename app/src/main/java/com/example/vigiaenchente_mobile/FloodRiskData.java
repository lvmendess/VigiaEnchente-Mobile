package com.example.vigiaenchente_mobile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FloodRiskData {
    private static final MutableLiveData<String> riscoLiveData = new MutableLiveData<>();

    public static LiveData<String> getRisco() {
        return riscoLiveData;
    }

    public static void setRisco(String risco) {
        riscoLiveData.postValue(risco);
    }
}
