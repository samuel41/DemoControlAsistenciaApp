package com.example.democontrolasistenciaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String estadoActual = "Personal fuera";
    private String estadoStrBtn = "entrada";
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt_msg = findViewById(R.id.txt_msg);
        Button btn_marcar = findViewById(R.id.btn_marcar);
        MaterialTextView txt_estado = findViewById(R.id.txt_estado);
        btn_marcar.setText("Marcar " + estadoStrBtn);
        ImageView imgEstado = findViewById(R.id.img_estado);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        TextView txtResult = findViewById(R.id.txt_result);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                /*Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();*/
                vibrator.vibrate(500);

                txtResult.setText(dbSendData(estadoStrBtn));

                if (estadoStrBtn.equals("entrada")) {
                    estadoStrBtn = "salida";
                } else {
                    estadoStrBtn = "entrada";
                }

                if (estadoActual.equals("Usuario fuera")) {
                    estadoActual = "Personal dentro";
                    imgEstado.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                } else {
                    estadoActual = "Personal fuera";
                    imgEstado.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
                }

                btn_marcar.setText("Marcar " + estadoStrBtn);
                txt_estado.setText(estadoActual);


            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Marcar Asistencia")
                .setDescription("Coloque el dedo para marcar entrada o salida")
                .setNegativeButtonText("Usar contraseña del dispositivo")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        Button biometricLoginButton = findViewById(R.id.btn_marcar);
        biometricLoginButton.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });
    }

    private String dbSendData(String estadoActual) {
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String result = "código: " + "\n" +
                "nombres: SAMUEL RAY" + "\n" +
                "apellidos: NÚÑEZ MAMANI" + "\n" +
                "..." + "\n" +
                "fecha: " + formatterDate.format(date) + "\n" +
                "hora: " + formatterTime.format(date) + "\n" +
                "estado: " + estadoActual;
        return result;
    }

}