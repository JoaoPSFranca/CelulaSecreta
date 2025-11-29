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
import br.edu.ifsp.pep.utills.ChallengeConfirm;
import br.edu.ifsp.pep.utills.ChallengeOver;
import br.edu.ifsp.pep.utills.ChallengeResult;
import br.edu.ifsp.pep.utills.ChallengeSetup;
import br.edu.ifsp.pep.utills.GuessResult;
import br.edu.ifsp.pep.model.Equipe;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
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
    @FXML private Label labelContador;
    @FXML private GridPane gridOpcoesDesafio;

    // --- Módulos Principais ---
    private GameManager gameManager;
    private UIManager uiManager;
    private NetworkManager networkManager;
    private GameMode gameMode;
    private ChallengeService challengeService;
    private AnimationTimer challengeTimer;

    // --- Estado da UI que o Controller precisa saber ---
    private final Button[] cartaBotoes = new Button[20];
    private final boolean[] cartaAtiva = new boolean[20];
    private Button cartaSelecionada;
    private ChallengeResult myChallengeResult;
    private ChallengeResult opponentChallengeResult;
    private boolean desafioFinalizado = false;
    private ChallengeSetup challengeSetupPendente;
    private boolean clientIsReadyForChallenge = false;
    private boolean resultadosProcessados = false;

    // --- Outras variáveis ---
    private Pergunta perguntaAtualDoOponente;

    // Variáveis para o Desafio (Fase 2)
    private List<ChallengeItem> listaDesafio;
    private int desafioIndexAtual;
    private int desafioAcertos;
    private long desafioStartTime;
    private List<Boolean> desafioRespostas; // NOVO: rastreia acertos/erros

    @FXML
    public void initialize() {

    }

    public void setupGame(GameSetup setup) {
        this.gameMode = setup.mode();
        this.challengeService = new ChallengeService();
        this.uiManager = new UIManager(rootPane, cardGrid, chatBox, chatScroll, suaCartaImage,
                btnPerguntar, btnPalpitar, btnSim, btnNao, zoomContainer, zoomCartaImageView,
                painelDesafio, labelTimer, labelPerguntaDesafio, gridOpcoesDesafio, labelContador);

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

            try { 
                // Envia AMBAS as cartas secretas
                Carta minhaCartaSecreta = gameManager.getSuaEquipe().getCartaSecreta();
                Carta cartaDoOponente = gameManager.getEquipeOponente().getCartaSecreta();
                networkManager.send(new GuessResult(oponenteAcertou, minhaCartaSecreta, cartaDoOponente));
            } catch (Exception e) {
                showAlert("Erro de Rede", "Não foi possível enviar o resultado do palpite.");
            }

            if (oponenteAcertou) { 
                // Oponente ACERTOU = Você PERDEU
                // Deve mostrar a carta do OPONENTE (que ele descobriu)
                Equipe equipeParaExibir = new Equipe("Equipe Oponente");
                equipeParaExibir.setCartaSecreta(gameManager.getEquipeOponente().getCartaSecreta());
                uiManager.exibirFimDeJogo(false, equipeParaExibir, this::iniciarDesafio, this::sairDoJogo);
            } else { 
                // Oponente ERROU = Você GANHOU
                // Não precisa mostrar carta (vitória)
                uiManager.exibirFimDeJogo(true, null, this::iniciarDesafio, this::sairDoJogo);
            }
        } else if (message instanceof GuessResult) {
            GuessResult resultado = (GuessResult) message;
            
            // Atualiza ambas as cartas com os valores reais
            gameManager.getEquipeOponente().setCartaSecreta(resultado.cartaDoJogadorQueRecebeuPalpite());
            gameManager.getSuaEquipe().setCartaSecreta(resultado.cartaDoJogadorQuePalpitou());
            
            if (resultado.correto()) { 
                // Você ACERTOU = Você GANHOU
                // Não precisa mostrar carta (vitória)
                uiManager.exibirFimDeJogo(true, null, this::iniciarDesafio, this::sairDoJogo);
            } else { 
                // Você ERROU = Você PERDEU
                // Deve mostrar a carta CORRETA do oponente (que você não descobriu)
                Equipe equipeParaExibir = new Equipe("Equipe Oponente");
                equipeParaExibir.setCartaSecreta(gameManager.getEquipeOponente().getCartaSecreta());
                uiManager.exibirFimDeJogo(false, equipeParaExibir, this::iniciarDesafio, this::sairDoJogo);
            }
        } else if (message instanceof ChallengeSetup setup) {
            // O Cliente recebeu o convite!
            this.challengeSetupPendente = setup; // Salva o convite
            uiManager.fecharAguardando(); // Fecha o "Aguardando..." se estiver aberto
            if (this.clientIsReadyForChallenge) {
                // Se o cliente já clicou em "jogar", aceita automaticamente.
                aceitarDesafio();
            } else {
                // Se o cliente ainda não clicou, mostra o convite.
                uiManager.exibirConviteDesafio(this::aceitarDesafio, this::recusarDesafio);
            }
        }  else if (message instanceof ChallengeResult resultadoOponente) {
            // --- LÓGICA ATUALIZADA (SÓ O HOST RECEBE ISSO) ---
            this.opponentChallengeResult = resultadoOponente;
            if (this.myChallengeResult != null) {
                // O Host também já terminou, processar resultados
                processarResultados(myChallengeResult, opponentChallengeResult);
            } else {
                // O Host ainda está jogando, apenas anota o resultado
                uiManager.addSystemMessage("Oponente terminou. Continue jogando!");
            }
        } else if (message instanceof ChallengeOver(String mensagemResultado)) {
            uiManager.exibirResultadoFinal(mensagemResultado, this::sairDoJogo);
        } else if (message instanceof ChallengeConfirm(boolean aceito)) {
            // O Host recebeu a resposta do Cliente
            uiManager.fecharAguardando(); // Fecha o painel "Aguardando..."

            if (aceito) {
                // Cliente aceitou! Inicia o jogo para o Host.
                uiManager.addSystemMessage("Cliente aceitou. Começando!");
                prepararEIniciarDesafioUI(this.listaDesafio);
            } else {
                // Cliente recusou.
                showAlert("Desafio Recusado", "O oponente recusou o desafio.");
                // O Host fica na tela de fim de jogo e pode "Sair"
            }
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

        // Lógica do Host: Envia o convite e espera.
        if (gameMode == GameMode.MULTIPLAYER_HOST) {
            uiManager.addSystemMessage("Enviando convite de desafio...");

            // 1. Obter 10 desafios aleatórios do banco de questões
            this.listaDesafio = challengeService.obterDesafiosAleatorios(10);

            // 2. Enviar o "Pacote" de setup para o cliente
            try {
                networkManager.send(new ChallengeSetup(this.listaDesafio));
            } catch (Exception e) {
                showAlert("Erro de Rede", "Não foi possível enviar o convite.");
                e.printStackTrace();
                return;
            }

            // 3. Exibir tela de "Aguardando"
            uiManager.exibirAguardandoOponente("Aguardando resposta do Cliente...");

        } else if (gameMode == GameMode.SINGLE_PLAYER) {
            // Lógica Single Player: Obter 10 desafios aleatórios para a Fase 2
            this.listaDesafio = challengeService.obterDesafiosAleatorios(10);
            prepararEIniciarDesafioUI(this.listaDesafio);
        } else {
            // Lógica do Cliente: Ao clicar, apenas exibe "Aguardando..."
            this.clientIsReadyForChallenge = true;
            uiManager.exibirAguardandoOponente("Aguardando o Host iniciar o desafio...");
        }
    }

    private void aceitarDesafio() {
        this.clientIsReadyForChallenge = false;
        if (challengeSetupPendente == null) return; // Segurança
        try {
            networkManager.send(new ChallengeConfirm(true));
            // Inicia o jogo para o cliente
            prepararEIniciarDesafioUI(challengeSetupPendente.perguntas());
            this.challengeSetupPendente = null; // Limpa o convite
        } catch (Exception e) {
            showAlert("Erro de Rede", "Não foi possível confirmar o desafio.");
        }
    }

    private void recusarDesafio() {
        this.clientIsReadyForChallenge = false;
        try {
            networkManager.send(new ChallengeConfirm(false));
            sairDoJogo(); // Simplesmente sai do jogo
        } catch (Exception e) {
            // Falha ao enviar a recusa, apenas sai
            sairDoJogo();
        }
    }

    private void prepararEIniciarDesafioUI(List<ChallengeItem> desafios) {
        this.listaDesafio = desafios;
        this.desafioIndexAtual = 0;
        this.desafioAcertos = 0;
        this.desafioStartTime = System.currentTimeMillis();
        this.desafioFinalizado = false;
        this.clientIsReadyForChallenge = false;
        this.resultadosProcessados = false;
        this.desafioRespostas = new ArrayList<>(); // NOVO: inicializa a lista

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

            // Atualizar contador de perguntas
            int numeroPergunta = desafioIndexAtual + 1;
            uiManager.atualizarContadorDesafio(numeroPergunta, listaDesafio.size());

            // Pede ao UIManager para mostrar esta pergunta e nos avisa quando uma opção for clicada
            // O UIManager chamará 'onRespostaDesafio' quando um botão for pressionado
            uiManager.exibirPerguntaDesafio(itemAtual, this::onRespostaDesafio);
            gridOpcoesDesafio.setDisable(false);
        } else {
            // Acabaram as perguntas
            finalizarDesafio();
        }
    }

    private void onRespostaDesafio(String respostaSelecionada) {
        gridOpcoesDesafio.setDisable(true);

        // 1. Pega o item da pergunta atual
        ChallengeItem itemAtual = listaDesafio.get(desafioIndexAtual);

        // 2. Verifica se a resposta está correta
        boolean acertou = itemAtual.respostaCorreta().equals(respostaSelecionada);
        if (acertou) {
            this.desafioAcertos++;
        }
        
        // 3. NOVO: Registra o resultado da resposta
        this.desafioRespostas.add(acertou);

        // 4. Avança para a próxima pergunta
        this.desafioIndexAtual++;

        PauseTransition delay = new PauseTransition(Duration.millis(250)); // 0.25 segundos
        delay.setOnFinished(e -> {
            exibirProximaPerguntaDesafio();
        });
        delay.play();
    }

    private void finalizarDesafio() {
        if (desafioFinalizado) {
            return;
        }
        this.desafioFinalizado = true;

        // 1. Parar o cronômetro
        if (challengeTimer != null) {
            challengeTimer.stop();
        }

        // 2. Calcular tempo final
        long elapsedMillis = System.currentTimeMillis() - desafioStartTime;
        double tempoTotalSegundos = elapsedMillis / 1000.0;
        String tempoFormatado = labelTimer.getText().replace("Tempo: ", "");

        // 3. Salvar meu resultado (ATUALIZADO com a lista de respostas)
        this.myChallengeResult = new ChallengeResult(desafioAcertos, tempoTotalSegundos, desafioRespostas);

        // 4. Exibir o placar local (sem botão de fechar)
        uiManager.exibirFimDesafio(desafioAcertos, listaDesafio.size(), tempoFormatado, desafioRespostas);

        // 5. Lógica de Fim de Jogo
        if (gameMode == GameMode.SINGLE_PLAYER) {
            // No single player, o resultado final é imediato
            uiManager.exibirResultadoFinal("Bom trabalho!", this::sairDoJogo);

        } else if (gameMode == GameMode.MULTIPLAYER_CLIENT) {
            // O Cliente envia seu resultado para o Host e espera
            try {
                networkManager.send(myChallengeResult);
                uiManager.addSystemMessage("Você terminou! Aguardando o resultado do oponente...");
            } catch (Exception e) {
                showAlert("Erro de Rede", "Não foi possível enviar seu resultado.");
            }

        } else if (gameMode == GameMode.MULTIPLAYER_HOST) {
            // O Host verifica se o Cliente já terminou
            if (opponentChallengeResult != null) {
                // Sim, o Cliente terminou primeiro. Processar resultados agora.
                processarResultados(myChallengeResult, opponentChallengeResult);
            } else {
                // Não, o Host terminou primeiro. Apenas espera.
                uiManager.addSystemMessage("Você terminou! Aguardando o resultado do oponente...");
            }
        }
    }

    private synchronized void processarResultados(ChallengeResult hostResult, ChallengeResult clientResult) {
        if (resultadosProcessados) {
            return; // Já processamos, não faça nada.
        }
        this.resultadosProcessados = true;

        String msgVencedorHost;
        String msgVencedorClient;

        // Lógica de Vencedor:
        // 1. Mais acertos vence.
        // 2. Se houver empate nos acertos, menor tempo vence.

        if (hostResult.acertos() > clientResult.acertos()) {
            msgVencedorHost = "Você Venceu! (Placar: " + hostResult.acertos() + " a " + clientResult.acertos() + ")";
            msgVencedorClient = "Você Perdeu! (Placar: " + clientResult.acertos() + " a " + hostResult.acertos() + ")";
        } else if (clientResult.acertos() > hostResult.acertos()) {
            msgVencedorHost = "Você Perdeu! (Placar: " + hostResult.acertos() + " a " + clientResult.acertos() + ")";
            msgVencedorClient = "Você Venceu! (Placar: " + clientResult.acertos() + " a " + hostResult.acertos() + ")";
        } else {
            // Empate nos acertos, verificar o tempo
            if (hostResult.tempoTotalSegundos() < clientResult.tempoTotalSegundos()) {
                msgVencedorHost = "Você Venceu! (No tempo)";
                msgVencedorClient = "Você Perdeu! (No tempo)";
            } else if (clientResult.tempoTotalSegundos() < hostResult.tempoTotalSegundos()) {
                msgVencedorHost = "Você Perdeu! (No tempo)";
                msgVencedorClient = "Você Venceu! (No tempo)";
            } else {
                msgVencedorHost = "Empate notável!";
                msgVencedorClient = "Empate notável!";
            }
        }

        // Anunciar o resultado para o Host
        Platform.runLater(() -> {
            uiManager.exibirResultadoFinal(msgVencedorHost, this::sairDoJogo);
        });

        // Enviar o resultado para o Cliente
        try {
            networkManager.send(new ChallengeOver(msgVencedorClient));
        } catch (Exception e) {
            // Esta exceção não pode travar a thread da rede
            e.printStackTrace();
        }
    }

    private void sairDoJogo() {
        System.exit(0);
    }
}
