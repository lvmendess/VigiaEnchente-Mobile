package com.example.vigiaenchente_mobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FloodRiskWorker extends Worker {

    private static final String TAG = "FloodRiskWorker";
    private static final String CHANNEL_ID = "flood_alerts";

    public FloodRiskWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // 1. Calcular risco
        String risco = calcularRiscoEnchente();

        // 2. Atualizar LiveData
        FloodRiskData.setRisco(risco);

        // 3. Enviar notificação
        enviarNotificacao(risco);

        // 4. Reagendar próxima execução em 3 minutos
        agendarProximo(getApplicationContext());

        return Result.success();
    }

    // Reagendar Worker único
    private void agendarProximo(Context context) {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FloodRiskWorker.class)
                .setInitialDelay(3, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                "FloodRiskWorker",
                ExistingWorkPolicy.REPLACE, // Substitui worker antigo
                work
        );
    }

    // Calcula risco de enchente (mesmo código)
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

    // Tentar requisição até 3 vezes
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

    // Enviar notificação (mesmo código)
    private void enviarNotificacao(String risco) {
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Alertas de Enchente",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        String mensagem;
        switch (risco) {
            case "Baixo": mensagem = "Não há risco de enchente"; break;
            case "Médio": mensagem = "Risco Médio de Enchente"; break;
            case "Alto": mensagem = "Risco Alto de Enchente"; break;
            default: mensagem = "Erro ao calcular risco"; break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Vigia Enchente")
                .setContentText(mensagem)
                .setSmallIcon(R.drawable.shield_gray)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // ID fixo evita múltiplas notificações empilhadas
        manager.notify(1, builder.build());
    }
}
