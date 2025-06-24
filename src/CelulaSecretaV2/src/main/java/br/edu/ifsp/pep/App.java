package br.edu.ifsp.pep;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1366, 768);
        scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
        scene.getRoot().setStyle("-fx-background-color: #f8f8f8;");
        stage.setTitle("Célula Secreta");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
