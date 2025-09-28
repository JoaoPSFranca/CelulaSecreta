package br.edu.ifsp.pep.controller;

import br.edu.ifsp.pep.logic.GameManager;
import br.edu.ifsp.pep.logic.Opponent;
import br.edu.ifsp.pep.logic.OpponentAI;
import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Pergunta;
import br.edu.ifsp.pep.model.Turno;
import br.edu.ifsp.pep.ui.UIManager;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    // --- Módulos Principais ---
    private GameManager gameManager;
    private UIManager uiManager;

    // --- Estado da UI que o Controller precisa saber ---
    private final Button[] cartaBotoes = new Button[20];
    private final boolean[] cartaAtiva = new boolean[20];
    private Button cartaSelecionada;

    @FXML
    public void initialize() {
        Opponent oponenteDoJogo = new OpponentAI();
        this.gameManager = new GameManager(oponenteDoJogo);
        this.uiManager = new UIManager(rootPane, cardGrid, chatBox, chatScroll, suaCartaImage,
                btnPerguntar, btnPalpitar, btnSim, btnNao, zoomContainer, zoomCartaImageView);

        setupInitialBoard();
        uiManager.exibirCartaSecreta(gameManager.getSuaEquipe().getCartaSecreta());
        uiManager.addSystemMessage("Bem-vindo ao Célula Secreta!");
        anunciarTurnoNoChat();

        btnSim.setOnAction(this::onSim);
        btnNao.setOnAction(this::onNao);
        btnPerguntar.setOnAction(this::onPerguntar);
        btnPalpitar.setOnAction(this::onPalpitar);
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

    private void handleCardClick(Button carta, int index) {
        if (gameManager.isModoPalpite() && !cartaAtiva[index]) {
            if (cartaSelecionada != null) cartaSelecionada.getStyleClass().remove("selecionada");
            cartaSelecionada = carta;
            cartaSelecionada.getStyleClass().add("selecionada");

            uiManager.mostrarPainelConfirmacao((ImageView) carta.getGraphic(),
                    () -> { // Ação de Confirmar
                        boolean acertou = gameManager.verificarPalpite((Carta) cartaSelecionada.getUserData());
                        uiManager.exibirFimDeJogo(acertou, gameManager.getEquipeOponente(), () -> System.exit(0));
                    }
            );
        } else if (!gameManager.isModoPalpite()) {
            cartaAtiva[index] = !cartaAtiva[index];
            uiManager.animarCarta(carta, cartaAtiva[index], (Carta) carta.getUserData());
        }
    }

    public void onSim(ActionEvent e) {
        if (!gameManager.isEsperandoRespostaDoJogador()) return;
        uiManager.addChatMessage("Sim", true);
        gameManager.finalizarRespostaDoJogador();
        atualizarInterfaceEAnunciarTurno();
    }

    public void onNao(ActionEvent e) {
        if (!gameManager.isEsperandoRespostaDoJogador()) return;
        uiManager.addChatMessage("Não", true);
        gameManager.finalizarRespostaDoJogador();
        atualizarInterfaceEAnunciarTurno();
    }

    public void onPerguntar(ActionEvent e) {
        uiManager.mostrarPainelDePerguntas(gameManager.getBancoDePerguntas(),
                (perguntaSelecionada) -> { // Ação de Confirmar
                    uiManager.addChatMessage(perguntaSelecionada.getTexto(), true);
                    processarRespostaDoOponente(perguntaSelecionada);
                }
        );
    }

    public void onPalpitar(ActionEvent e) {
        gameManager.alternarModoPalpite();
        // A lógica de atualizar a UI foi movida para o UIManager, mas o Controller precisa disparar
        uiManager.atualizarModoPalpite(gameManager.isModoPalpite(), cartaBotoes, cartaAtiva);
    }

    // --- Orquestração de Turnos ---

    private void processarRespostaDoOponente(Pergunta pergunta) {
        uiManager.atualizarInterfacePorTurno(gameManager.getTurnoAtual(), false); // Trava a UI

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

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            Pergunta pergunta = gameManager.executarTurnoOponente();
            if (pergunta != null) {
                uiManager.addChatMessage(pergunta.getTexto(), false);
            }
            // A UI é atualizada para esperar a resposta do jogador
            uiManager.atualizarInterfacePorTurno(gameManager.getTurnoAtual(), gameManager.isEsperandoRespostaDoJogador());
        });
        delay.play();
    }

    private void anunciarTurnoNoChat() {
        uiManager.addSystemMessage(gameManager.getTurnoAtual() == Turno.JOGADOR ? "Sua vez de jogar!" : "Vez do oponente...");
    }

    private void atualizarInterfaceEAnunciarTurno() {
        uiManager.atualizarInterfacePorTurno(gameManager.getTurnoAtual(), gameManager.isEsperandoRespostaDoJogador());
        anunciarTurnoNoChat();
    }
}