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
        stage.show();
    }

    private void startMainGame(GameSetup setup) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
            scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
            scene.getRoot().setStyle("-fx-background-color: #394a46;");

            MainController controller = fxmlLoader.getController();
            controller.setupGame(setup);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
