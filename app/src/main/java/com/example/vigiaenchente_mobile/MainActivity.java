package com.example.vigiaenchente_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;
    private ImageView shieldIcon;
    private LinearLayout statusCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        shieldIcon = findViewById(R.id.shieldIcon);
        statusCard = findViewById(R.id.statusCard);

        observarRisco();
        agendarFloodWorker(); // dispara a primeira execução
    }

    private void observarRisco() {
        FloodRiskData.getRisco().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String risco) {
                atualizarUI(risco);
            }
        });
    }

    private void agendarFloodWorker() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(FloodRiskWorker.class)
                .setInitialDelay(0, TimeUnit.SECONDS) // roda imediatamente
                .build();
        WorkManager.getInstance(this).enqueue(work);
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
