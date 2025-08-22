package com.example.vigiaenchente_mobile;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private ImageView shieldIcon;
    private LinearLayout statusCard;
    private static final String TAG = "EnchenteApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        shieldIcon = findViewById(R.id.shieldIcon);
        statusCard = findViewById(R.id.statusCard);

        new Thread(() -> {
            String resultado = calcularRiscoEnchente();
            runOnUiThread(() -> atualizarUI(resultado));
        }).start();
    }

    private String calcularRiscoEnchente() {
        double latitude = -19.8949;
        double longitude = -43.8148;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String endDate = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, -2);
        String startDate = sdf.format(cal.getTime());

        String urlString = "https://flood-api.open-meteo.com/v1/flood?latitude="
                + latitude + "&longitude=" + longitude
                + "&daily=river_discharge&models=forecast_v4"
                + "&start_date=" + startDate + "&end_date=" + endDate;

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        String resposta = tentarRequisicao(client, urlString, 3);
        if (resposta == null) return "Erro";

        try {
            JSONObject json = new JSONObject(resposta);
            JSONArray discharge = json.getJSONObject("daily").optJSONArray("river_discharge");
            if (discharge == null || discharge.length() < 3) return "Erro";

            double d0 = discharge.getDouble(0);
            double d1 = discharge.getDouble(1);
            double d2 = discharge.getDouble(2);

            double media = (d0 + d1 + d2) / 3.0;
            double variacao = d2 - d0;

            if (media < 5) return (variacao > 3) ? "Médio" : "Baixo";
            else if (media < 10) return (variacao > 3) ? "Alto" : "Médio";
            else return "Alto";

        } catch (Exception e) {
            return "Erro";
        }
    }

    private String tentarRequisicao(OkHttpClient client, String url, int tentativas) {
        for (int i = 0; i < tentativas; i++) {
            try {
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) return response.body().string();
                }
            } catch (Exception ignored) {}
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
        return null;
    }


    private void atualizarUI(String risco) {
        switch (risco) {
            case "Baixo":
                statusText.setText("Não há risco de enchente");
                statusCard.setBackgroundResource(R.drawable.green_background);
                shieldIcon.setImageResource(R.drawable.shield_green);
                break;
            case "Médio":
                statusText.setText("Risco Médio de Enchente");
                statusCard.setBackgroundResource(R.drawable.yellow_background);
                shieldIcon.setImageResource(R.drawable.shield_yellow);
                break;
            case "Alto":
                statusText.setText("Risco Alto de Enchente");
                statusCard.setBackgroundResource(R.drawable.red_background);
                shieldIcon.setImageResource(R.drawable.shield_red);
                break;
            default:
                statusText.setText("Erro ao calcular risco");
                statusCard.setBackgroundResource(R.drawable.card_background);
                shieldIcon.setImageResource(R.drawable.shield_gray);
                break;
        }
    }
}
