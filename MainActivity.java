package com.espapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editIp, editPort;
    private Button btnToggle;
    private TextView txtStatus;
    private boolean ledDurum = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editIp = findViewById(R.id.editIp);
        editPort = findViewById(R.id.editPort);
        btnToggle = findViewById(R.id.btnToggle);
        txtStatus = findViewById(R.id.txtStatus);

        btnToggle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String ip = editIp.getText().toString().trim();
					String port = editPort.getText().toString().trim();

					if (ip.isEmpty() || port.isEmpty()) {
						Toast.makeText(MainActivity.this, "IP ve Port girin!", Toast.LENGTH_SHORT).show();
						return;
					}

					ledDurum = !ledDurum;
					String komut = ledDurum ? "ON" : "OFF";
					new ESPRequest().execute(ip, port, komut);
				}
			});
    }

    private class ESPRequest {
        void execute(String ip, String port, String komut) {
            txtStatus.setText("Durum: Bağlanıyor...");

            try {
                String urlStr = "http://" + ip + ":" + port + "/led?state=" + komut;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);

                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                conn.disconnect();

                txtStatus.setText("Durum: LED " + (ledDurum ? "AÇIK" : "KAPALI"));
                btnToggle.setText(ledDurum ? "LED KAPAT" : "LED AÇ");

            } catch (Exception e) {
                txtStatus.setText("Hata: " + e.getMessage());
                Toast.makeText(MainActivity.this, "Bağlantı başarısız!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
