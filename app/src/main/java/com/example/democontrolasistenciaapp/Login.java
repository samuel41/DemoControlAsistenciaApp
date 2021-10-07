package com.example.democontrolasistenciaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class Login extends AppCompatActivity {
    private EditText edtDniUsuario, edtPassword;
    private TextView txtResult;
    private String dniUsuario, password;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtDniUsuario = findViewById(R.id.edt_dniUsuario);
        edtPassword = findViewById(R.id.edt_password);
        txtResult = findViewById(R.id.txt_resultLogin);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dniUsuario = edtDniUsuario.getText().toString();
                password = edtPassword.getText().toString();

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                //String url ="http://www.google.com";
                //String url ="http://172.26.115.240/ws/usuariosApi.php" + "?pass=" + password + "&dni=" + dniUsuario;
                String url ="http://172.26.115.240/ws/usuariosApi.php" +
                        "?pass=" + password +
                        "&dni=" + dniUsuario +
                        "&mac=" + getMACAddress("wlan0");

                //txtResult.setText(url);

                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //String dniUsuario = response.getString("dniUsuario");
                                //txtResult.setText(dniUsuario);

                                Gson gson = new Gson();
                                Usuario usuario = gson.fromJson(response.toString(), Usuario.class);

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.putExtra("idUsuario", usuario.idUsuario );
                                intent.putExtra("dniUsuario", usuario.dniUsuario);
                                intent.putExtra("nombreUsuario", usuario.nombreUsuario);
                                intent.putExtra("apellidoUsuario", usuario.apellidoUsuario);
                                startActivity(intent);

                                //txtResult.setText(response.toString());

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