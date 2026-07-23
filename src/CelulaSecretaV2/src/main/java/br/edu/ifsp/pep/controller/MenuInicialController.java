package br.edu.ifsp.pep.controller;

import br.edu.ifsp.pep.network.*;
import br.edu.ifsp.pep.ui.UIAlert;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class MenuInicialController {

    @FXML private StackPane rootPane;
    @FXML private VBox menuPrincipal;
    @FXML private VBox menuMultiplayer;
    @FXML private VBox menuCarregando;
    @FXML private Button btnJogarSozinho;
    @FXML private Button btnMultiplayer;
    @FXML private Button btnEntrarSala;
    @FXML private Button btnCriarSala;
    @FXML private Button btnVoltarMultiplayer;
    @FXML private Button btnCancelarCarregamento;
    @FXML private Button btnSairAplicacao;
    @FXML private TextField ipInput;
    @FXML private Label labelCarregando;
    @FXML private javafx.scene.image.ImageView logoImage;
    @FXML private TextField labelCodigoSala;
    @FXML private Button btnCopiarCodigo;
    @FXML private HBox codigoSalaContainer;

    private GameSetupListener setupListener;
    private Task<?> connectionTask;

    public interface GameSetupListener {
        void onGameSetupComplete(GameSetup setup);
    }

    public void initialize() {
        setupButtonActions();
    }

    public void setGameSetupListener(GameSetupListener listener) {
        this.setupListener = listener;
    }

    private void setupButtonActions() {
        // Menu Principal
        btnJogarSozinho.setOnAction(e -> startSinglePlayer());
        btnMultiplayer.setOnAction(e -> showMultiplayerMenu());
        btnSairAplicacao.setOnAction(e -> sairAplicacao());

        // Menu Multiplayer
        btnEntrarSala.setOnAction(e -> joinGame());
        btnCriarSala.setOnAction(e -> hostGame());
        btnVoltarMultiplayer.setOnAction(e -> showMainMenu());

        // Enable/Disable botão de entrar quando IP é digitado
        ipInput.textProperty().addListener((obs, oldVal, newVal) ->
            btnEntrarSala.setDisable(newVal.trim().isEmpty())
        );

        // Cancelar carregamento
        btnCancelarCarregamento.setOnAction(e -> cancelConnection());
        
        // Copiar código de sala
        btnCopiarCodigo.setOnAction(e -> copyRoomCodeToClipboard());
    }

    private void showMainMenu() {
        logoImage.setVisible(true);
        logoImage.setManaged(true);
        menuPrincipal.setVisible(true);
        menuPrincipal.setManaged(true);
        menuMultiplayer.setVisible(false);
        menuMultiplayer.setManaged(false);
        menuCarregando.setVisible(false);
        menuCarregando.setManaged(false);
        if (codigoSalaContainer != null) {
            codigoSalaContainer.setVisible(false);
            codigoSalaContainer.setManaged(false);
        }
    }

    private void showMultiplayerMenu() {
        logoImage.setVisible(false);
        logoImage.setManaged(false);
        menuPrincipal.setVisible(false);
        menuPrincipal.setManaged(false);
        menuMultiplayer.setVisible(true);
        menuMultiplayer.setManaged(true);
        menuCarregando.setVisible(false);
        menuCarregando.setManaged(false);
        ipInput.clear();
        btnEntrarSala.setDisable(true);
        if (codigoSalaContainer != null) {
            codigoSalaContainer.setVisible(false);
            codigoSalaContainer.setManaged(false);
        }
    }

    private void showLoadingMenu(String message) {
        logoImage.setVisible(false);
        logoImage.setManaged(false);
        menuPrincipal.setVisible(false);
        menuPrincipal.setManaged(false);
        menuMultiplayer.setVisible(false);
        menuMultiplayer.setManaged(false);
        menuCarregando.setVisible(true);
        menuCarregando.setManaged(true);
        labelCarregando.setText(message);
    }

    private void showLoadingMenuWithCode(String message, String roomCode) {
        showLoadingMenu(message);
        // Exibir o código de sala para o host copiar
        if (labelCodigoSala != null && codigoSalaContainer != null) {
            labelCodigoSala.setText(roomCode);
            codigoSalaContainer.setVisible(true);
            codigoSalaContainer.setManaged(true);
        }
    }

    private void startSinglePlayer() {
        GameSetup setup = new GameSetup(GameMode.SINGLE_PLAYER, null, null);
        if (setupListener != null) {
            setupListener.onGameSetupComplete(setup);
        }
    }

    private void joinGame() {
        String roomCode = ipInput.getText().trim().toUpperCase();
        if (roomCode.isEmpty()) {
            showError("Digite o código da sala");
            return;
        }

        // Valida se o código tem o tamanho correto
        if (roomCode.length() != 6 || !roomCode.matches("[A-Z0-9]{6}")) {
            showError("Código inválido!\nDeve ter 6 caracteres (ex: AB3X7K)");
            return;
        }

        showLoadingMenu("Procurando pela sala " + roomCode + "...");

        connectionTask = new Task<GameSetup>() {
            @Override
            protected GameSetup call() throws Exception {
                // Procura o host usando o código
                String hostIp = RoomServer.discoverHostByCode(roomCode);

                if (hostIp == null) {
                    throw new Exception("Sala não encontrada na rede");
                }

                return new GameSetup(GameMode.MULTIPLAYER_CLIENT, hostIp, roomCode);
            }
        };

        connectionTask.setOnSucceeded(e -> {
            if (setupListener != null) {
                setupListener.onGameSetupComplete((GameSetup) connectionTask.getValue());
            }
        });

        connectionTask.setOnFailed(e -> {
            showError("Sala não encontrada!\nVerifique o código e tente novamente.\n\n" +
                     connectionTask.getException().getMessage());
            showMultiplayerMenu();
        });

        new Thread(connectionTask).start();
    }

    private void hostGame() {
        showLoadingMenu("Criando sala de jogo...");

        connectionTask = new Task<GameSetup>() {
            @Override
            protected GameSetup call() {
                String localIp = NetworkUtils.detectLocalIPv4();
                String roomCode = RoomCodeGenerator.generateRoomCode();
                
                Platform.runLater(() -> showLoadingMenuWithCode("Aguardando jogador...", roomCode));

                // Inicia o anúncio da sala na rede
                RoomServer.startBroadcastingRoom(roomCode, localIp);

                // Retorna imediatamente — a espera real pela conexão TCP
                // acontece no MainController.setupNetworkGame() via serverSocket.accept()
                return new GameSetup(GameMode.MULTIPLAYER_HOST, localIp, roomCode);
            }
        };

        connectionTask.setOnSucceeded(e -> {
            if (setupListener != null) {
                setupListener.onGameSetupComplete((GameSetup) connectionTask.getValue());
            }
        });

        connectionTask.setOnFailed(e -> {
            showError("Falha ao criar sala: " + connectionTask.getException().getMessage());
            showMultiplayerMenu();
        });

        new Thread(connectionTask).start();
    }

    private void cancelConnection() {
        if (connectionTask != null && connectionTask.isRunning()) {
            connectionTask.cancel();
        }
        showMainMenu();
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            UIAlert.mostrarAlerta(rootPane, "Erro", message);
        });
    }

    private void copyRoomCodeToClipboard() {
        String roomCode = labelCodigoSala.getText();
        if (roomCode != null && !roomCode.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(roomCode);
            clipboard.setContent(content);
            
            // Mostrar feedback visual
            String originalText = btnCopiarCodigo.getText();
            btnCopiarCodigo.setText("Copiado!");
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> btnCopiarCodigo.setText(originalText));
                } catch (InterruptedException e) {
                    Platform.runLater(() -> btnCopiarCodigo.setText(originalText));
                }
            }).start();
        }
    }

    private void sairAplicacao() {
        System.out.println("Saindo da aplicação...");
        Platform.exit();
        System.exit(0);
    }
}

