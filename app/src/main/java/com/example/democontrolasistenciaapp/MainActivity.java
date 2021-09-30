package com.example.democontrolasistenciaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private String estadoActual = "Personal fuera";
    private String estadoStrBtn = "entrada";
    private String dniUsuario = "";
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txt_msg = findViewById(R.id.txt_msg);
        Button btn_marcar = findViewById(R.id.btn_marcar);
        Button btn_obtenerMAC = findViewById(R.id.btn_obtenerMAC);
        MaterialTextView txt_estado = findViewById(R.id.txt_estado);
        btn_marcar.setText("Marcar " + estadoStrBtn);
        ImageView imgEstado = findViewById(R.id.img_estado);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        TextView txtResult = findViewById(R.id.txt_result);

        EditText edtDniUsuario = findViewById(R.id.edt_dniUsuario);

        btn_obtenerMAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //txtResult.setText(getMACAddress("wlan0"));
                dniUsuario = edtDniUsuario.getText().toString();

// Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                //String url ="http://www.google.com";
                String url ="http:172.26.115.240/ws/usuariosApi.php?dni=" + dniUsuario;

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //String dniUsuario = response.getString("dniUsuario");
                                //txtResult.setText(dniUsuario);
                                txtResult.setText(response.toString());

                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        txtResult.setText(error.toString());
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);


            }
        });

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

                if (estadoActual.equals("Personal fuera")) {
                    estadoActual = "Personal dentro";
                    imgEstado.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
                } else {
                    estadoActual = "Personal fuera";
                    imgEstado.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
                }

                btn_marcar.setText("Marcar " + estadoStrBtn);
                //RESULT
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

    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { }
        return "";
    }


}