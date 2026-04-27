package br.edu.ifsp.pep;

import br.edu.ifsp.pep.controller.MainController;
import br.edu.ifsp.pep.controller.MenuInicialController;
import br.edu.ifsp.pep.network.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private Stage primaryStage;
    private MainController mainController;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // Load menu inicial
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/fxml/menu_inicial.fxml"));
        Scene menuScene = new Scene(menuLoader.load(), 1366, 768);
        menuScene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());

        MenuInicialController menuController = menuLoader.getController();
        menuController.setGameSetupListener(new MenuInicialController.GameSetupListener() {
            @Override
            public void onGameSetupComplete(GameSetup setup) {
                startMainGame(setup);
            }
        });

        stage.setTitle("Célula Secreta");
        stage.setScene(menuScene);
        stage.setWidth(1366);
        stage.setHeight(768);
        stage.setResizable(false);

        // Adiciona handler para quando a janela for fechada
        stage.setOnCloseRequest(event -> {
            System.out.println("Janela sendo fechada - limpando recursos...");
            shutdownApplication();
        });

        stage.show();
    }

    private void startMainGame(GameSetup setup) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
            scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
            scene.getRoot().setStyle("-fx-background-color: #394a46;");

            mainController = fxmlLoader.getController();
            mainController.setupGame(setup);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    /**
     * Encerra a aplicação de forma segura, limpando todos os recursos
     */
    private void shutdownApplication() {
        System.out.println("Iniciando encerramento seguro da aplicação...");

        try {
            // Se o MainController está ativo, encerra a conexão de rede
            if (mainController != null) {
                mainController.cleanup();
            }

            // Encerra o RoomServer
            RoomServer.shutdown();

            // Aguarda um pouco para todas as threads finalizarem
            Thread.sleep(500);

        } catch (Exception e) {
            System.err.println("Erro durante encerramento: " + e.getMessage());
            e.printStackTrace();
        }

        // Finalmente, sai da aplicação
        System.out.println("Encerramento completo. Saindo...");
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
