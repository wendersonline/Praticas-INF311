# Prática 2 - Calculadora imc

Este projeto faz parte da disciplina INF311 e consiste em uma calculadora de IMC desenvolvida em Java + XML para fixação dos conteúdos de navegação entre telas.

## Funcionalidades
* Formulários para coleta dos dados para cálculo do IMC
* Interface customizada com android_layout
* Validação de campos vazios nos campos necessários para cálculo, pesos e alturas nulas e/ou negativas

## Tecnologias
* Java
* Android Studio
* Componentes de Layout XML


## Respostas das observações no comportamento das intents:
c) Modifique a Tela 2 adicionando um botão que possibilite voltar a Tela 1 após a
exibição dos relatório.

i. O que aconteceu quando você tentou voltar para a Tela 1 através do
método de navegação convencional? Você conseguiu visualizar as
informações digitadas anteriormente? O que aconteceu com as
chamadas dos métodos do ciclo de vida da activity Tela 1?

R: Ao tentar voltar para a Tela 1 usando o método padrão, o Android cria uma nova instância da atividade em vez de retomar a anterior. No ciclo de vida, a Tela 2 entra em onPause(), a nova Tela 1 executa o onCreate() e, por fim, a Tela 2 faz o onStop(). O resultado é que os dados digitados anteriormente são perdidos, pois estamos diante de uma tela recém-criada, enquanto a original continua escondida no inicio da pilha.



ii. Experimente adicionar a linha
it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
onde it é o objeto Intent criado para informar os o destino da
navegação, antes do método que consuma a navegação e observe o
que acontecerá com as chamadas dos métodos do ciclo de vida das
activities.
Observação: Existem várias outras flags capazes de alterar o
comportamento do ciclo de vida das activities.

R: Com a adição desta flag, o sistema localiza a instância da Tela 1 que já estava na pilha e a traz para a frente. Nesse cenário, os métodos de criação (como onCreate) não são chamados novamente. A Tela 1 executa apenas o onRestart() e volta ao topo. Como a instância foi reaproveitada, todos os dados inseridos anteriormente permanecem visíveis, garantindo a continuidade da experiência do usuário sem destruir a Tela 2 imediatamente.
