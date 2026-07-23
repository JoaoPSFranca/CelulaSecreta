package br.edu.ifsp.pep.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UIAlert {

    public static void mostrarAlerta(StackPane rootPane, String titulo, String mensagem) {
        mostrarAlerta(rootPane, titulo, mensagem, null);
    }

    public static void mostrarAlerta(StackPane rootPane, String titulo, String mensagem, Runnable onFechar) {
        VBox alertPane = new VBox(20);
        alertPane.setId("alertPane");
        alertPane.setAlignment(Pos.CENTER);
        alertPane.setStyle("-fx-background-color: #31413d; -fx-padding: 40; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        alertPane.setMaxSize(500, 300);

        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #a0d4c8; -fx-font-weight: bold;");

        Label conteudoLabel = new Label(mensagem);
        conteudoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e0e5e4;");
        conteudoLabel.setWrapText(true);
        conteudoLabel.setMaxWidth(450);

        Button btnFechar = new Button("OK");
        btnFechar.getStyleClass().add("round-button");
        btnFechar.setStyle("-fx-padding: 10 40 10 40;");
        btnFechar.setOnAction(e -> {
            rootPane.getChildren().remove(alertPane);
            if (onFechar != null) {
                onFechar.run();
            }
        });

        VBox conteudoBox = new VBox(15);
        conteudoBox.setAlignment(Pos.CENTER);
        conteudoBox.getChildren().addAll(tituloLabel, conteudoLabel);

        alertPane.getChildren().addAll(conteudoBox, btnFechar);
        rootPane.getChildren().add(alertPane);
    }

    public static void mostrarAlertaComOpcoes(StackPane rootPane, String titulo, String mensagem, 
                                              String opcao1, String opcao2, 
                                              Runnable onOpcao1, Runnable onOpcao2) {
        VBox alertPane = new VBox(20);
        alertPane.setId("alertPane");
        alertPane.setAlignment(Pos.CENTER);
        alertPane.setStyle("-fx-background-color: #31413d; -fx-padding: 40; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        alertPane.setMaxSize(500, 300);

        Label tituloLabel = new Label(titulo);
        tituloLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #a0d4c8; -fx-font-weight: bold;");

        Label conteudoLabel = new Label(mensagem);
        conteudoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e0e5e4;");
        conteudoLabel.setWrapText(true);
        conteudoLabel.setMaxWidth(450);

        Button btn1 = new Button(opcao1);
        btn1.getStyleClass().add("round-button");
        btn1.setStyle("-fx-padding: 10 30 10 30;");
        btn1.setOnAction(e -> {
            rootPane.getChildren().remove(alertPane);
            onOpcao1.run();
        });

        Button btn2 = new Button(opcao2);
        btn2.getStyleClass().add("round-button");
        btn2.setStyle("-fx-padding: 10 30 10 30;");
        btn2.setOnAction(e -> {
            rootPane.getChildren().remove(alertPane);
            onOpcao2.run();
        });

        HBox botoes = new HBox(15, btn1, btn2);
        botoes.setAlignment(Pos.CENTER);

        VBox conteudoBox = new VBox(15);
        conteudoBox.setAlignment(Pos.CENTER);
        conteudoBox.getChildren().addAll(tituloLabel, conteudoLabel);

        alertPane.getChildren().addAll(conteudoBox, botoes);
        rootPane.getChildren().add(alertPane);
    }
}
