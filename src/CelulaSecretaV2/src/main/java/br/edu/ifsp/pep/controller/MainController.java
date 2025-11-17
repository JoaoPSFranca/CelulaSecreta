package br.edu.ifsp.pep.controller;

import br.edu.ifsp.pep.logic.GameManager;
import br.edu.ifsp.pep.logic.OpponentAI;
import br.edu.ifsp.pep.logic.Opponent;
import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.ChallengeItem;
import br.edu.ifsp.pep.model.Pergunta;
import br.edu.ifsp.pep.model.Turno;
import br.edu.ifsp.pep.network.*;
import br.edu.ifsp.pep.service.ChallengeService;
import br.edu.ifsp.pep.ui.UIManager;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class MainController {

    // --- FXML Components ---
    @FXML private VBox chatBox;
    @FXML private ImageView suaCartaImage;
    @FXML private GridPane cardGrid;
    @FXML private Button btnSim, btnNao, btnPerguntar, btnPalpitar;
    @FXML private ScrollPane chatScroll;
    @FXML private StackPane rootPane;
    @FXML private VBox zoomContainer;
    @FXML private ImageView zoomCartaImageView;
    @FXML private VBox loadingOverlay;
    @FXML private Label loadingLabel;
    @FXML private VBox painelDesafio;
    @FXML private Label labelTimer;
    @FXML private Label labelPerguntaDesafio;
    @FXML private GridPane gridOpcoesDesafio;

    // --- Módulos Principais ---
    private GameManager gameManager;
    private UIManager uiManager;
    private NetworkManager networkManager;
    private GameMode gameMode;
    private Opponent opponentAI;
    private ChallengeService challengeService;
    private AnimationTimer challengeTimer;

    // --- Estado da UI que o Controller precisa saber ---
    private final Button[] cartaBotoes = new Button[20];
    private final boolean[] cartaAtiva = new boolean[20];
    private Button cartaSelecionada;

    // --- Outras variáveis ---
    private Pergunta perguntaAtualDoOponente;

    // Variáveis para o Desafio (Fase 2)
    private List<ChallengeItem> listaDesafio;
    private int desafioIndexAtual;
    private int desafioAcertos;
    private long desafioStartTime;

    @FXML
    public void initialize() {

    }

    public void setupGame(GameSetup setup) {
        this.gameMode = setup.mode();
        this.challengeService = new ChallengeService();
        this.uiManager = new UIManager(rootPane, cardGrid, chatBox, chatScroll, suaCartaImage,
                btnPerguntar, btnPalpitar, btnSim, btnNao, zoomContainer, zoomCartaImageView,
                painelDesafio, labelTimer, labelPerguntaDesafio, gridOpcoesDesafio);

        switch (gameMode) {
            case SINGLE_PLAYER:
                // Se for single-player, configura o jogo localmente com a IA.
                loadingOverlay.setVisible(false); // Garante que a tela de carregamento não apareça.

                Opponent oponenteDoJogo = new OpponentAI();
                this.gameManager = new GameManager(oponenteDoJogo);

                // Inicia a interface do jogo imediatamente.
                initializeGameUI();
                break;

            case MULTIPLAYER_HOST:
            case MULTIPLAYER_CLIENT:
                // Se for multiplayer, inicia a rotina de conexão de rede.
                setupNetworkGame(setup);
                break;
        }

        btnSim.setOnAction(this::onSim);
        btnNao.setOnAction(this::onNao);
        btnPerguntar.setOnAction(this::onPerguntar);
        btnPalpitar.setOnAction(this::onPalpitar);
    }

    private void setupNetworkGame(GameSetup setup) {
        this.networkManager = new NetworkManager();
        loadingOverlay.setVisible(true); // Mostra o overlay de carregamento.

        // Cria a tarefa que rodará em segundo plano para não travar a UI.
        Task<Boolean> connectionTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    if (setup.mode() == GameMode.MULTIPLAYER_HOST) {
                        updateMessage("Aguardando conexão no IP: " + setup.ipAddress());
                        networkManager.hostGame();
                    } else {
                        updateMessage("Conectando ao host: " + setup.ipAddress());
                        networkManager.joinGame(setup.ipAddress());
                    }
                    return Boolean.TRUE; // Sucesso
                } catch (Exception e) {
                    updateMessage("Falha na conexão: " + e.getMessage());
                    e.printStackTrace();
                    return Boolean.FALSE; // Falha
                }
            }
        };

        // Vincula a mensagem da tarefa ao Label na tela de carregamento.
        loadingLabel.textProperty().bind(connectionTask.messageProperty());

        // Define o que fazer quando a tarefa terminar com sucesso.
        connectionTask.setOnSucceeded(event -> {
            loadingOverlay.setVisible(false); // Esconde o overlay.
            boolean success = connectionTask.getValue();

            if (success) {
                // A conexão foi bem-sucedida.
                networkManager.setOnMessageReceived(this::processReceivedMessage);
                networkManager.startListening();

                // No modo de rede, o oponente é gerenciado pela rede (null no GameManager).
                this.gameManager = new GameManager(null);
                initializeGameUI(); // Inicia a ‘interface’ do jogo.

                // Lógica inicial de turno para rede (ex: host começa).
                if (setup.mode() == GameMode.MULTIPLAYER_HOST) {
                    gameManager.setMyTurn(true);
                    uiManager.addSystemMessage("Você começa.");
                } else {
                    gameManager.setMyTurn(false);
                    uiManager.addSystemMessage("Vez do oponente.");
                }

                uiManager.updateUIForTurnState(gameManager.isMyTurn(), false);
            } else {
                showAlert("Erro de Conexão", "Não foi possível conectar. Verifique o IP e se o host está esperando.");
            }
        });

        // Define o que fazer se a tarefa falhar com uma exceção inesperada.
        connectionTask.setOnFailed(event -> {
            loadingOverlay.setVisible(false);
            showAlert("Erro Crítico de Rede", "Ocorreu um erro inesperado durante a conexão.");
            connectionTask.getException().printStackTrace();
        });

        // Inicia a tarefa numa nova thread.
        new Thread(connectionTask).start();
    }

    private void initializeGameUI() {
        setupInitialBoard();
        uiManager.exibirCartaSecreta(gameManager.getSuaEquipe().getCartaSecreta());

        // A lógica de início de turno é diferente para single-player.
        if (gameMode == GameMode.SINGLE_PLAYER) {
            if (gameManager.getTurnoAtual() == Turno.OPONENTE) {
                executarTurnoOponente();
            } else {
                anunciarTurnoNoChat();
            }
        }
    }

    private void processReceivedMessage(Object message) {
        if (message instanceof Pergunta) {
            Pergunta perguntaRecebida = (Pergunta) message;
            this.perguntaAtualDoOponente = perguntaRecebida;

            uiManager.addChatMessage(perguntaRecebida.getTexto(), false);
            uiManager.addSystemMessage("Sua vez de responder!");

            // Agora é a vez do player de responder
            gameManager.setMyTurn(true);
            uiManager.updateUIForTurnState(true, true); // Habilita Sim/Não
        }
        else if (message instanceof Boolean) {
            Boolean resposta = (Boolean) message;
            uiManager.addChatMessage(resposta ? "Sim" : "Não", false);
            uiManager.addSystemMessage("Aguardando a jogada do oponente...");

            // O oponente respondeu, agora a vez é dele.
            gameManager.setMyTurn(false);
            uiManager.updateUIForTurnState(false, false); // Desabilita tudo
        } else if (message instanceof Carta) {
            // Recebe Palpite do Oponente
            Carta palpiteRecebido = (Carta) message;
            uiManager.addSystemMessage("Oponente palpitou: " + palpiteRecebido.getNome());

            // Verifica se o palpite está correto
            boolean oponenteAcertou = gameManager.verificarPalpite(palpiteRecebido, false);

            try { // envia resposta
                networkManager.send(oponenteAcertou ? "CORRETO" : "INCORRETO");
            } catch (Exception e) {
                showAlert("Erro de Rede", "Não foi possível enviar o resultado do palpite.");
            }

            if (oponenteAcertou) { // sobrou nada pro beta
                uiManager.exibirFimDeJogo(false, gameManager.getEquipeOponente(), this::iniciarDesafio, this::sairDoJogo);
            } else { // ganhamo sigma
                uiManager.exibirFimDeJogo(true, gameManager.getEquipeOponente(), this::iniciarDesafio, this::sairDoJogo);
            }
        } else if (message instanceof String) { // Resultado do palpite
            String resultado = (String) message;
            if (resultado.equals("CORRETO")) { // ganhamo sigma
                uiManager.exibirFimDeJogo(true, gameManager.getEquipeOponente(), this::iniciarDesafio, this::sairDoJogo);
            } else { // sobrou nada pro beta
                uiManager.exibirFimDeJogo(false, gameManager.getEquipeOponente(), this::iniciarDesafio, this::sairDoJogo);
            }
        } else if (message instanceof ChallengeSetup setup) {
            // O Cliente recebeu o pacote de início de desafio do Host
            uiManager.addSystemMessage("Desafio recebido do Host. Começando!");
            prepararEIniciarDesafioUI(setup.perguntas());
        }
        else if (message instanceof ChallengeResult resultadoOponente) {
            // O Host recebeu o resultado do Cliente
            // **A FAZER (Próximos Passos):** Lógica para comparar os resultados e declarar o vencedor
            System.out.println("Recebido resultado do oponente: " + resultadoOponente.acertos() + " acertos em " + resultadoOponente.tempoTotalSegundos() + "s");
        }
    }

    // Metodo auxiliar para exibir alertas
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void setupInitialBoard() {
        for (int i = 0; i < gameManager.getTodasAsCartas().size(); i++) {
            int row = i / 5;
            int col = i % 5;
            Carta cartaAtual = gameManager.getTodasAsCartas().get(i);

            Button cartaButton = new Button();
            cartaButton.getStyleClass().add("card-button");
            cartaButton.setUserData(cartaAtual);

            String imagePath = "/images/carta_menor/" + cartaAtual.getNomeImagem() + ".png";
            ImageView frenteImageView = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
            frenteImageView.setFitHeight(165);
            frenteImageView.setFitWidth(110);
            cartaButton.setGraphic(frenteImageView);

            // Event Handlers
            final int index = i;
            final int finalRow = row;
            cartaButton.setOnAction(e -> handleCardClick(cartaButton, index));
            cartaButton.setOnMouseEntered(event -> { if (!cartaAtiva[index]) uiManager.exibirZoom(cartaButton, finalRow); });
            cartaButton.setOnMouseExited(event -> uiManager.esconderZoom());

            cartaBotoes[i] = cartaButton;
            cardGrid.add(cartaButton, col, row);
        }
    }

    // --- Event Handlers (Delegam para os Módulos) ---

    private void handleCardClick(Button cartaButton, int index) {
        if (gameManager.isModoPalpite() && !cartaAtiva[index]) {
            if (cartaSelecionada != null) cartaSelecionada.getStyleClass().remove("selecionada");
            cartaSelecionada = cartaButton;
            cartaSelecionada.getStyleClass().add("selecionada");

            uiManager.mostrarPainelConfirmacao((ImageView) cartaButton.getGraphic(),
                    () -> { // Ação de Confirmar o Palpite
                        Carta palpite = (Carta) cartaSelecionada.getUserData();

                        if (gameMode == GameMode.SINGLE_PLAYER) {
                            // Lógica para Single-Player (vs. IA)
                            boolean acertou = gameManager.verificarPalpite(palpite, true);
                            uiManager.exibirFimDeJogo(acertou, gameManager.getEquipeOponente(), this::iniciarDesafio, this::sairDoJogo);
                        } else {
                            // Lógica para Multiplayer (Rede)
                            try {
                                networkManager.send(palpite);
                                uiManager.addSystemMessage("Palpite enviado! Aguardando resultado...");
                                uiManager.addSystemMessage("Você palpitou: " + palpite.getNome());
                            } catch (Exception ex) {
                                showAlert("Erro de Rede", "Não foi possível enviar o palpite.");
                                ex.printStackTrace();
                            }
                        }
                    }
            );
        } else if (!gameManager.isModoPalpite()) {
            cartaAtiva[index] = !cartaAtiva[index];
            uiManager.animarCarta(cartaButton, cartaAtiva[index], (Carta) cartaButton.getUserData());
        }
    }

    public void onSim(ActionEvent e) {
        if (gameMode == GameMode.SINGLE_PLAYER) {
            // Lógica para Single-Player (vs. IA)
            if (!gameManager.isEsperandoRespostaDoJogador()) return;
            uiManager.addChatMessage("Sim", true);
            gameManager.processarRespostaDoJogador(perguntaAtualDoOponente, true);
            atualizarInterfaceEAnunciarTurno();
        } else {
            // Lógica para Multiplayer (Rede)
            uiManager.addChatMessage("Sim", true);
            try {
                networkManager.send(Boolean.TRUE);
                uiManager.addSystemMessage("Sua vez");
                gameManager.setMyTurn(true); // Continuar jogando
                uiManager.updateUIForTurnState(true, false);
            } catch (Exception ex) {
                showAlert("Erro de Rede", "Não foi possível enviar a resposta.");
                ex.printStackTrace();
            }
        }
    }

    public void onNao(ActionEvent e) {
        if (gameMode == GameMode.SINGLE_PLAYER) {
            // Lógica para Single-Player (vs. IA)
            if (!gameManager.isEsperandoRespostaDoJogador()) return;
            uiManager.addChatMessage("Não", true);
            gameManager.processarRespostaDoJogador(perguntaAtualDoOponente, false);
            atualizarInterfaceEAnunciarTurno();
        } else {
            // Lógica para Multiplayer (Rede)
            uiManager.addChatMessage("Não", true);
            try {
                networkManager.send(Boolean.FALSE);
                uiManager.addSystemMessage("Sua vez");
                gameManager.setMyTurn(true); // Continua jogando
                uiManager.updateUIForTurnState(true, false);
            } catch (Exception ex) {
                showAlert("Erro de Rede", "Não foi possível enviar a resposta.");
                ex.printStackTrace();
            }
        }
    }

    public void onPerguntar(ActionEvent e) {
        uiManager.mostrarPainelDePerguntas(gameManager.getBancoDePerguntas(),
                (perguntaSelecionada) -> { // Ação a ser executada ao confirmar a pergunta
                    uiManager.addChatMessage(perguntaSelecionada.getTexto(), true);
                    if (gameMode == GameMode.SINGLE_PLAYER) {
                        processarRespostaDoOponente(perguntaSelecionada);
                    } else {
                        try {
                            networkManager.send(perguntaSelecionada);
                            uiManager.addSystemMessage("Aguardando a resposta do oponente...");
                            gameManager.setMyTurn(false); // Passa a vez
                            uiManager.updateUIForTurnState(false, false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert("Erro de Rede", "Não foi possível enviar a pergunta.");
                        }
                    }
                }
        );
    }

    public void onPalpitar(ActionEvent e) {
        gameManager.alternarModoPalpite();
        uiManager.atualizarModoPalpite(gameManager.isModoPalpite(), cartaBotoes, cartaAtiva);
    }

    // --- Orquestração de Turnos ---

    private void processarRespostaDoOponente(Pergunta pergunta) {
        uiManager.updateUIForTurnState(gameManager.isMyTurn(), false); // Trava a UI

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            GameManager.RespostaOponente resp = gameManager.processarRespostaDoOponente(pergunta);
            uiManager.addChatMessage(resp.resposta() ? "Sim" : "Não", false);
            executarTurnoOponente();
        });
        delay.play();
    }

    private void executarTurnoOponente() {
        anunciarTurnoNoChat();

        // Desabilita a interface enquanto o oponente "pensa"
        uiManager.updateUIForTurnState(gameManager.isMyTurn(), false);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            if(gameManager.isJogoFinalizado()) return;

            GameManager.ResultadoTurnoOponente resultado = gameManager.executarTurnoOponente();

            // Se o oponente fez uma pergunta
            if (resultado.pergunta() != null) {
                this.perguntaAtualDoOponente = resultado.pergunta();
                uiManager.addChatMessage(resultado.pergunta().getTexto(), false);
                // Prepara a UI para a resposta do jogador
                gameManager.setMyTurn(true);
                uiManager.updateUIForTurnState(gameManager.isMyTurn(), true);
            }
            // Se o oponente fez um palpite
            else if (resultado.palpite() != null) {
                GameManager.PalpiteOponente palpite = resultado.palpite();
                String msg = "Meu palpite é... " + palpite.carta().getNome() + "!";
                uiManager.addChatMessage(msg, false);

                // Adiciona um pequeno delay para o jogador ler o palpite antes do fim de jogo
                PauseTransition fimDeJogoDelay = new PauseTransition(Duration.seconds(2));
                fimDeJogoDelay.setOnFinished(e -> {
                    // O resultado é invertido: se o oponente acertou (true), o jogador perdeu (vitoria = false).
                    boolean jogadorVenceu = !palpite.acertou();
                    uiManager.exibirFimDeJogo(jogadorVenceu, gameManager.getEquipeOponente(), this::iniciarDesafio, this::sairDoJogo);
                });
                fimDeJogoDelay.play();
            }
            // Se o oponente não fez nada (passou a vez)
            else {
                gameManager.finalizarRespostaDoJogador(); // Simula uma "passagem de turno"
                atualizarInterfaceEAnunciarTurno();
            }
        });
        delay.play();
    }

    private void anunciarTurnoNoChat() {
        uiManager.addSystemMessage(gameManager.getTurnoAtual() == Turno.JOGADOR ? "Sua vez de jogar!" : "Vez do oponente...");
    }

    private void atualizarInterfaceEAnunciarTurno() {
        uiManager.updateUIForTurnState(gameManager.isMyTurn(), gameManager.isEsperandoRespostaDoJogador());
        anunciarTurnoNoChat();
    }

    // segunda fase

    private void iniciarDesafio() {
        System.out.println("Iniciando Desafio (Fase 2)...");
        uiManager.addSystemMessage("Iniciando Desafio de Velocidade!");

        // Lógica de Rede: Apenas o HOST prepara e envia as perguntas.
        if (gameMode == GameMode.MULTIPLAYER_HOST) {
            // 1. Carregar e embaralhar as perguntas
            List<ChallengeItem> desafios = challengeService.carregarDesafios();
            Collections.shuffle(desafios);

            // 2. Pegar apenas 10 (ou o número que quisermos)
            this.listaDesafio = desafios.subList(0, Math.min(desafios.size(), 10));

            // 3. Enviar o "Pacote" de setup para o cliente
            try {
                networkManager.send(new ChallengeSetup(this.listaDesafio));
            } catch (Exception e) {
                showAlert("Erro de Rede", "Não foi possível iniciar o desafio para o oponente.");
                e.printStackTrace();
                return;
            }

            // 4. Iniciar o desafio para si mesmo (Host)
            prepararEIniciarDesafioUI(this.listaDesafio);

        } else if (gameMode == GameMode.SINGLE_PLAYER) {
            // Lógica para Single Player
            List<ChallengeItem> desafios = challengeService.carregarDesafios();
            Collections.shuffle(desafios);
            this.listaDesafio = desafios.subList(0, Math.min(desafios.size(), 10));

            // Inicia o desafio localmente
            prepararEIniciarDesafioUI(this.listaDesafio);
        }

        // Se for CLIENT, não faz nada. Apenas espera o Host enviar o ChallengeSetup
        // (Isso será tratado no 'processReceivedMessage')
    }

    private void prepararEIniciarDesafioUI(List<ChallengeItem> desafios) {
        this.listaDesafio = desafios;
        this.desafioIndexAtual = 0;
        this.desafioAcertos = 0;
        this.desafioStartTime = System.currentTimeMillis(); // Inicia o cronômetro

        // 1. Chamar o UIManager para esconder a Fase 1 (grid, chat)
        uiManager.transicionarParaDesafio();

        // 2. Iniciar o cronômetro visual
        iniciarCronometroDesafio();

        // 3. Exibir a primeira pergunta
        exibirProximaPerguntaDesafio();
    }

    private void iniciarCronometroDesafio() {
        if (challengeTimer != null) {
            challengeTimer.stop();
        }
        challengeTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calcula o tempo decorrido
                long elapsedMillis = System.currentTimeMillis() - desafioStartTime;
                long elapsedSeconds = elapsedMillis / 1000;
                long minutes = elapsedSeconds / 60;
                long seconds = elapsedSeconds % 60;

                // Atualiza o UIManager
                uiManager.atualizarTimer(String.format("%02d:%02d", minutes, seconds));
            }
        };
        challengeTimer.start();
    }

    private void exibirProximaPerguntaDesafio() {
        if (desafioIndexAtual < listaDesafio.size()) {
            // Ainda há perguntas
            ChallengeItem itemAtual = listaDesafio.get(desafioIndexAtual);

            // Pede ao UIManager para mostrar esta pergunta e nos avisa quando uma opção for clicada
            // O UIManager chamará 'onRespostaDesafio' quando um botão for pressionado
            uiManager.exibirPerguntaDesafio(itemAtual, this::onRespostaDesafio);
        } else {
            // Acabaram as perguntas
            finalizarDesafio();
        }
    }

    private void onRespostaDesafio(String respostaSelecionada) {
        // 1. Pega o item da pergunta atual
        ChallengeItem itemAtual = listaDesafio.get(desafioIndexAtual);

        // 2. Verifica se a resposta está correta
        if (itemAtual.respostaCorreta().equals(respostaSelecionada)) {
            this.desafioAcertos++;
            // (Opcional: dar feedback visual de acerto)
        } else {
            // (Opcional: dar feedback visual de erro)
        }

        // 3. Avança para a próxima pergunta
        this.desafioIndexAtual++;
        exibirProximaPerguntaDesafio();
    }

    private void finalizarDesafio() {
        // 1. Parar o cronômetro
        if (challengeTimer != null) {
            challengeTimer.stop();
        }

        // 2. Calcular tempo final
        long elapsedMillis = System.currentTimeMillis() - desafioStartTime;
        double tempoTotalSegundos = elapsedMillis / 1000.0;
        String tempoFormatado = labelTimer.getText().replace("Tempo: ", ""); // Pega o tempo final da UI

        // 3. Lógica de Fim de Jogo (SINGLE PLAYER)
        if (gameMode == GameMode.SINGLE_PLAYER) {
            uiManager.exibirFimDesafio(desafioAcertos, listaDesafio.size(), tempoFormatado, this::sairDoJogo);
        }
        // 4. Lógica de Fim de Jogo (MULTIPLAYER)
        else {
            // (A FAZER NO PRÓXIMO PASSO: Lógica de rede para enviar resultados)

            // Por enquanto, vamos apenas exibir o resultado localmente
            uiManager.exibirFimDesafio(desafioAcertos, listaDesafio.size(), tempoFormatado, this::sairDoJogo);

            // (Na próxima etapa, substituiremos a linha acima pela lógica de
            // envio de resultados e espera pelo oponente)
        }
    }

    private void sairDoJogo() {
        System.exit(0);
    }

    public record ChallengeSetup(
            List<ChallengeItem> perguntas
    ) implements Serializable { }

    public record ChallengeResult(
            int acertos,
            double tempoTotalSegundos
    ) implements Serializable { }
}