package com.example.democontrolasistenciaapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private void guardarArchivos(Activity activity, String estadoActual, String estadoStrBtn) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("estadoActual", estadoActual);
        editor.putString("estadoStrBtn", estadoStrBtn);
        editor.commit();
    }
}
