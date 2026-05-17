# Prática 3 - GPS Location

Este projeto faz parte da disciplina INF311 e consiste em um aplicativo de localização geográfica desenvolvido em Java + XML para fixação dos conteúdos de mapas, GPS, banco de dados local e navegação entre telas.

## Funcionalidades

* Menu principal com opções de localização e acesso ao relatório de logs
* Mapa interativo com três marcadores fixos (Cidade Natal, Casa em Viçosa e DPI/UFV)
* Navegação entre telas com passagem de parâmetros via Intent
* Centralização do mapa no ponto escolhido pelo usuário com diferentes níveis de zoom
* Marcador azul indicando a localização atual do usuário via GPS
* Cálculo e exibição da distância em linha reta entre a posição atual e o ponto visível no mapa
* Banco de dados SQLite com as tabelas Location e Logs
* Registro automático de log a cada opção clicada no menu, com timestamp e chave estrangeira para o local
* Tela de relatório listando todos os logs registrados
* Exibição de latitude e longitude do local associado ao log via INNER JOIN ao clicar em um item da lista

## Tecnologias

* Java
* Android Studio
* Google Maps SDK para Android
* Google Play Services Location (FusedLocationProviderClient)
* SQLite (SQLiteOpenHelper)
* Componentes de Layout XML
