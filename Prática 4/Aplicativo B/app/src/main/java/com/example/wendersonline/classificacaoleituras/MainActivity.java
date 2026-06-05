package com.example.wendersonline.classificacaoleituras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnDevolver = findViewById(R.id.btnDevolver);

        // Recupera a Intent que veio do App A
        Intent intentRecebida = getIntent();

        // Extrai os valores dos sensores (usando 0.0 como valor padrão caso falhe)
        float valorLuz = intentRecebida.getFloatExtra("VALOR_LUZ", 0f);
        float valorProximidade = intentRecebida.getFloatExtra("VALOR_PROXIMIDADE", 0f);

        // Log para conferir os valores que estão sendo capturados...
        Log.d("Valor Luz: ", "" + valorLuz);
        Log.d("Valor Proximidade: ", "" + valorProximidade);

        btnDevolver.setOnClickListener(v -> {
            // Classificação da Luz (Inferior a 20.0 lx liga a lanterna)
            boolean ligarLanterna = (valorLuz < 20.0f);

            // Classificação da Proximidade (Mais de 3 cm liga a vibração)
            boolean ligarVibracao = (valorProximidade > 3.0f);

            // Cria uma Intent de resposta para empacotar os resultados
            Intent intentResposta = new Intent();
            intentResposta.putExtra("RESULTADO_LUZ", ligarLanterna);
            intentResposta.putExtra("RESULTADO_PROXIMIDADE", ligarVibracao);

            // Define o resultado como OK e anexa os dados de retorno
            setResult(RESULT_OK, intentResposta);

            // Fecha o App B e volta automaticamente para o App A
            finish();
        });
    }
}