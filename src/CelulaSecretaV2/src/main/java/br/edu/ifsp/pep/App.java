package br.edu.ifsp.pep;

import br.edu.ifsp.pep.controller.MainController;
import br.edu.ifsp.pep.network.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.Optional;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Optional<GameSetup> setup = showGameSetupDialog();

        if (setup.isEmpty()) {
            Platform.exit();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
        scene.getRoot().setStyle("-fx-background-color: #394a46;");

        MainController controller = fxmlLoader.getController();
        controller.setupGame(setup.get());

        stage.setTitle("Célula Secreta");
        stage.setScene(scene);

        stage.setWidth(1366);
        stage.setHeight(768);
        stage.setResizable(false);

        stage.show();
    }

    private Optional<GameSetup> showGameSetupDialog() {
        Dialog<GameSetup> dialog = new Dialog<>();
        dialog.setTitle("Célula Secreta");
        dialog.setHeaderText("Bem-vindo! Como você gostaria de jogar?");

        ButtonType singlePlayerButton = new ButtonType("Jogar Sozinho (vs. IA)", ButtonBar.ButtonData.OK_DONE);
        ButtonType hostButton = new ButtonType("Hostear Jogo (Rede)", ButtonBar.ButtonData.OK_DONE);
        ButtonType joinButton = new ButtonType("Entrar em Jogo (Rede)", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(singlePlayerButton, hostButton, joinButton, ButtonType.CANCEL);

        // Layout para a opção de entrar no jogo (cliente)
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField ipAddressField = new TextField();
        ipAddressField.setPromptText("192.168.0.1");
        grid.add(new Label("Endereço IP do Host:"), 0, 0);
        grid.add(ipAddressField, 1, 0);

        // Desabilita o botão "Entrar" se o IP não for digitado
        dialog.getDialogPane().lookupButton(joinButton).setDisable(true);
        ipAddressField.textProperty().addListener((observable, oldValue, newValue) -> {
            dialog.getDialogPane().lookupButton(joinButton).setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Converte o resultado do diálogo para o nosso objeto ConnectionResult
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == singlePlayerButton) {
                return new GameSetup(GameMode.SINGLE_PLAYER, null);
            }
            if (dialogButton == hostButton) {
                try {
                    String localIp = NetworkUtils.detectLocalIPv4();

                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setTitle("Você é o Host!");
                    infoAlert.setHeaderText("Aguardando a conexão do outro jogador...");
                    infoAlert.setContentText("Por favor, compartilhe este endereço IP com ele: " + localIp);
                    infoAlert.showAndWait();

                    return new GameSetup(GameMode.MULTIPLAYER_HOST, localIp);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new GameSetup(GameMode.MULTIPLAYER_HOST, "127.0.0.1");
                }
            }
            if (dialogButton == joinButton) {
                return new GameSetup(GameMode.MULTIPLAYER_CLIENT, ipAddressField.getText());
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
