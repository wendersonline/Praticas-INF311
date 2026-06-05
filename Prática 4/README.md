# INF311 - Programação para Dispositivos Móveis
## Atividade Prática 04 - Integração de Aplicativos e Sensores no Android

Este repositório contém a resolução da **Prática 04**, cujo objetivo é aplicar os conhecimentos sobre a captura de dados em tempo real utilizando os sensores do dispositivo e a comunicação bidirecional entre diferentes aplicações utilizando `Intents` explícitas por ação e contratos de resultado (`ActivityResultLauncher`).

O projeto é dividido em dois aplicativos independentes que cooperam entre si: **Aplicativo A** (Leitura e Atuação) e **Aplicativo B** (Regras de Negócio e Classificação).

---

## Estrutura dos Aplicativos

### 1. Aplicativo A: Leituras dos Sensores (`leiturasensores`)
Este aplicativo funciona como a interface de hardware com o utilizador.
* **Monitorização em Tempo Real:** Captura de forma contínua os dados do **Sensor de Luminosidade (`Sensor.TYPE_PROXIMITY`)** e do **Sensor de Proximidade (`Sensor.TYPE_LIGHT`)**.
* **Comunicação de Saída:** Quando o utilizador clica em *"Classificar leituras"*, o app envia os valores mais recentes capturados para o Aplicativo B através de uma `Intent` com a ação personalizada `com.example.ACAO_CLASSIFICAR_SENSORES`.
* **Atuação de Hardware:** Aguarda o retorno do Aplicativo B. Dependendo da classificação lógica recebida, ele ativa ou desativa visualmente os `SwitchMaterial` no topo da e aciona fisicamente a **Lanterna (Flash)** e o **Motor de Vibração**.
* **Gestão de Ciclo de Vida:** Intercepta o comportamento do botão "Voltar" para forçar o encerramento da atividade (`finish()`). Garante no método `onDestroy()` o desligamento completo de todos os atuadores para preservar o hardware e a bateria do dispositivo.

### 2. Aplicativo B: Classificação das Leituras (`classificacaoleituras`)
Este aplicativo funciona como o processador lógico isolado do sistema.
* **Filtro de Intent:** Configurado no `AndroidManifest.xml` com um `<intent-filter>` para responder unicamente à ação disparada pelo App A.
* **Processamento de Regras:** Ao ser aberto, extrai os valores numéricos recebidos e, ao clicar em *"Devolver classificações"*, aplica as seguintes regras estipuladas:
  * **Intensidade de Luz < 20.0 lx:** Retorna `true` para ligar a lanterna; caso contrário, `false`.
  * **Distância de Obstáculo > 3.0 cm:** Retorna `true` para ligar o motor de vibração; caso contrário, `false`.
* **Comunicação de Retorno:** Devolve as respostas booleanas (`RESULTADO_LUZ` e `RESULTADO_PROXIMIDADE`) com o código `RESULT_OK` e finaliza-se automaticamente, devolvendo o controlo ao App A.

---

## Tecnologias e Configurações Utilizadas

* **Linguagem:** Java
* **Ambiente de Desenvolvimento:** Android Studio
* **API Mínima Suportada:** API 23 (Android 6.0 Marshmallow)
* **Arquitetura de Layout:** `ConstraintLayout` com componentes do `Google Material Design` (`SwitchMaterial`).
* **Hardware Utilizado:** `CameraManager` (Controle de Flash) e `Vibrator` (Controle de Motor).
