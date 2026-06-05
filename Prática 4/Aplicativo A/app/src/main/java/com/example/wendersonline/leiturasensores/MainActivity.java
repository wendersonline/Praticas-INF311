package com.example.wendersonline.leiturasensores;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensorLuz;
    private Sensor sensorProximidade;

    private float valorLuzAtual = 0f;
    private float valorProximidadeAtual = 0f;

    private SwitchMaterial switchLanterna;
    private SwitchMaterial switchVibracao;
    private Button btnClassificar;

    private LanternaHelper lanternaHelper;
    private MotorHelper motorHelper;

    // Launcher para abrir o App B esperando um resultado de volta
    private final ActivityResultLauncher<Intent> abrirAppBLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();

                    // Recupera as classificações calculadas pelo App B
                    boolean ligarLanterna = data.getBooleanExtra("RESULTADO_LUZ", false);
                    boolean ligarVibracao = data.getBooleanExtra("RESULTADO_PROXIMIDADE", false);

                    // Atualiza a Lanterna e o Switch correspondente
                    switchLanterna.setChecked(ligarLanterna);
                    if (ligarLanterna) lanternaHelper.ligar(); else lanternaHelper.desligar();

                    // Atualiza o Motor e o Switch correspondente
                    switchVibracao.setChecked(ligarVibracao);
                    if (ligarVibracao) motorHelper.iniciarVibracao(); else motorHelper.pararVibracao();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa as views
        switchLanterna = findViewById(R.id.switchLanterna);
        switchVibracao = findViewById(R.id.switchVibracao);
        btnClassificar = findViewById(R.id.btnClassificar);

        // Inicializa os Helpers fornecidos
        lanternaHelper = new LanternaHelper(this);
        motorHelper = new MotorHelper(this);

        // Inicializa o gerenciador de sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            sensorProximidade = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        // Configura o clique do botão
        btnClassificar.setOnClickListener(v -> {
            // Cria a Intent com uma AÇÃO personalizada que o App B vai filtrar
            Intent intent = new Intent("com.example.ACAO_CLASSIFICAR_SENSORES");

            // Passa as últimas leituras salvas
            intent.putExtra("VALOR_LUZ", valorLuzAtual);
            intent.putExtra("VALOR_PROXIMIDADE", valorProximidadeAtual);

            // Dispara aguardando resposta
            abrirAppBLauncher.launch(intent);
        });

        // Ao fechar a tela pelo botão voltar ele "mata" a tela
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Força o encerramento definitivo desta tela
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Registra os listeners para começar a ler os sensores em tempo real
        if (sensorManager != null) {
            if (sensorLuz != null) {
                sensorManager.registerListener(this, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL);
            }
            if (sensorProximidade != null) {
                sensorManager.registerListener(this, sensorProximidade, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistra para economizar bateria enquanto o app não está visível
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        // Garante desligar tudo ao fechar definitivamente o app
        lanternaHelper.desligar();
        motorHelper.pararVibracao();
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Atualiza as variáveis locais com os valores mais recentes coletados pelos sensores
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            valorLuzAtual = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            valorProximidadeAtual = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Não precisamos tratar alteração de precisão
    }
}