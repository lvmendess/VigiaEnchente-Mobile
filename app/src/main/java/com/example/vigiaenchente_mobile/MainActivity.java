package com.example.vigiaenchente_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    /*private static final String IPINFO_TOKEN = "";
    private static final String OPENWHEATHER_KEY = "";
    private static final String CIDADE_DEFAULT = "Sabará";
    private TextView clima;*/
    private ImageView escudo;
    private ImageButton button;
    private LinearLayout statusCard;
    private TextView statusText;
    private int escudos[] = {R.drawable.shield_green, R.drawable.shield_yellow, R.drawable.shield_red};
    private int statusCards[] = {R.drawable.green_background, R.drawable.yellow_background, R.drawable.red_background};
    private String statusTexts[] = {"Não há risco de enchente", "Risco médio de enchente", "Risco alto de enchente"};
    private int escudoAtual = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        escudo = findViewById(R.id.shieldIcon);
        button = findViewById(R.id.button4);
        statusCard = findViewById(R.id.statusCard);
        statusText = findViewById(R.id.statusText);
        //clima = findViewById(R.id.main);
        //buscarCidadePorIp();
    }

    public void mudarEscudo(View v) {
        if (escudoAtual >= escudos.length - 1) {
            escudoAtual = 0;
        } else {
            escudoAtual++;
        }
        escudo.setImageResource(escudos[escudoAtual]);
        statusCard.setBackgroundResource(statusCards[escudoAtual]);
        statusText.setText(statusTexts[escudoAtual]);
    }
    /*
    private void buscarCidadePorIp(){
        IpInfoService ipService = ApiClient.getIpClient().create(IpInfoService.class);
        ipService.getCidade(IPINFO_TOKEN).enqueue(new Callback<IpInfoResponse>() {
            @Override
            public void onResponse(Call<IpInfoResponse> call, Response<IpInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCidade() != null) {
                    String cidade = response.body().getCidade();
                    Log.d("API", "Cidade detectada: " + cidade);
                    buscarClima(cidade);
                } else {
                    buscarClima(CIDADE_DEFAULT);
                }
            }

            @Override
            public void onFailure(Call<IpInfoResponse> call, Throwable t) {
                Log.e("API", "Erro ao buscar cidade", t);
                buscarClima(CIDADE_DEFAULT);
            }
        });
    }

    private void buscarClima(String cidade) {
        ClimaService weatherService = ApiClient.getClimaClient().create(ClimaService.class);
        weatherService.getClima(cidade + ",br", "metric", "pt_br", OPENWHEATHER_KEY)
                .enqueue(new Callback<ClimaResponse>() {
                    @Override
                    public void onResponse(Call<ClimaResponse> call, Response<ClimaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            float temp = response.body().getMain().getTemp();
                            String name = response.body().getCidade();
                            clima.setText("Clima em " + name + ": " + temp + "°C");
                        } else {
                            clima.setText("Não foi possível obter o clima.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ClimaResponse> call, Throwable t) {
                        clima.setText("Erro ao buscar clima.");
                        Log.e("API", "Erro ao buscar clima", t);
                    }
                });
    }
     */
}