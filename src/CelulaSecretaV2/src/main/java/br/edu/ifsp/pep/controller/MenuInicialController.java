package br.edu.ifsp.pep.controller;

import br.edu.ifsp.pep.network.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class MenuInicialController {

    @FXML private VBox menuPrincipal;
    @FXML private VBox menuMultiplayer;
    @FXML private VBox menuCarregando;
    @FXML private Button btnJogarSozinho;
    @FXML private Button btnMultiplayer;
    @FXML private Button btnEntrarSala;
    @FXML private Button btnCriarSala;
    @FXML private Button btnVoltarMultiplayer;
    @FXML private Button btnCancelarCarregamento;
    @FXML private TextField ipInput;
    @FXML private Label labelCarregando;
    @FXML private javafx.scene.image.ImageView logoImage;

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
                try {
                    String localIp = NetworkUtils.detectLocalIPv4();
                    String roomCode = RoomCodeGenerator.generateRoomCode();

                    // Inicia o anúncio da sala na rede
                    RoomServer.startBroadcastingRoom(roomCode, localIp);

//                    Platform.runLater(() -> {
//                        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
//                        infoAlert.setTitle("Sala Criada!");
//                        infoAlert.setHeaderText("Compartilhe este código com o outro jogador:");
//                        infoAlert.setContentText(roomCode);
//                        infoAlert.showAndWait();
//                    });

                    return new GameSetup(GameMode.MULTIPLAYER_HOST, localIp, roomCode);
                } catch (Exception e) {
                    return new GameSetup(GameMode.MULTIPLAYER_HOST, "127.0.0.1", "XXXXXX");
                }
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}

