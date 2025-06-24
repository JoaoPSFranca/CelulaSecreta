package br.edu.ifsp.pep.controller;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.util.*;

public class MainController {
    @FXML private VBox chatBox;
    @FXML private TextField chatInput;
    @FXML private TextField codigoSala;
    @FXML private ImageView suaCartaImage;
    @FXML private GridPane cardGrid;
    @FXML private Button btnSim;
    @FXML private Button btnNao;
    @FXML private Button btnEnviar;
    @FXML private Button btnPerguntar;
    @FXML private Button btnPalpitar;
    @FXML private ScrollPane chatScroll;
    @FXML private StackPane rootPane;

    private boolean[] cartaAtiva = new boolean[20];
    private Button[] cartaBotoes = new Button[20];
    private Image[] imagensCartas = new Image[14];
    private boolean chatLiberado = false;
    private boolean modoPalpite = false;
    private Button cartaSelecionada;

    @FXML
    public void initialize() {
        for (int i = 0; i < 14; i++) {
            imagensCartas[i] = new Image(getClass().getResource("/images/celulas/Celula" + (i + 1) + ".png").toExternalForm());
        }

        int id = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                Button carta = new Button();
                carta.getStyleClass().add("card-button");
                carta.setPrefSize(110, 165);

                int index = id;
                String classe = "frente-Celula" + ((index % 14) + 1);
                carta.getStyleClass().addAll("card-button", classe);

                carta.setOnAction(e -> alternarCarta(carta, index));

                cartaBotoes[index] = carta;
                cardGrid.add(carta, col, row);
                id++;
            }
        }

        Random rand = new Random();
        int cartaSorteada = rand.nextInt(14);

        suaCartaImage.setImage(imagensCartas[cartaSorteada]);
        suaCartaImage.setFitWidth(225);
        suaCartaImage.setFitHeight(357);
        suaCartaImage.setPreserveRatio(false);

        chatInput.setDisable(true);
        btnSim.setOnAction(this::onSim);
        btnNao.setOnAction(this::onNao);
        btnEnviar.setOnAction(this::onEnviar);
        btnPerguntar.setOnAction(this::onPerguntar);
        btnPalpitar.setOnAction(this::onPalpitar);

        chatBox.setSpacing(8);
    }

    private void alternarCarta(Button carta, int index) {
        if (modoPalpite && !cartaAtiva[index]) {
            if (cartaSelecionada != null) {
                cartaSelecionada.getStyleClass().remove("selecionada");
            }
            cartaSelecionada = carta;
            cartaSelecionada.getStyleClass().add("selecionada");
            mostrarPainelConfirmacao();
            return;
        }

        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), carta);
        rotateOut.setAxis(Rotate.Y_AXIS);
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);
        rotateOut.setInterpolator(Interpolator.EASE_BOTH);

        rotateOut.setOnFinished(e -> {
            if (cartaAtiva[index]) {
                carta.getStyleClass().add("ativa");
            } else {
                carta.getStyleClass().remove("ativa");
            }

            RotateTransition rotateIn = new RotateTransition(Duration.millis(150), carta);
            rotateIn.setAxis(Rotate.Y_AXIS);
            rotateIn.setFromAngle(270);
            rotateIn.setToAngle(360);
            rotateIn.setInterpolator(Interpolator.EASE_BOTH);
            rotateIn.play();
        });

        cartaAtiva[index] = !cartaAtiva[index];
        rotateOut.play();
    }


    public void onSim(ActionEvent e) {
        addChatMessage("Sim", true);
    }

    public void onNao(ActionEvent e) {
        addChatMessage("Não", true);
    }

    public void onPerguntar(ActionEvent e) {
        chatLiberado = true;
        chatInput.setDisable(false);
        chatInput.requestFocus();
        addChatMessage("[Digite sua pergunta]", false);
    }

    public void onEnviar(ActionEvent e) {
        if (!chatLiberado) return;
        String pergunta = chatInput.getText();
        if (!pergunta.isBlank()) {
            addChatMessage(pergunta, true);
            chatInput.clear();
            chatInput.setDisable(true);
            chatLiberado = false;
        }
    }

    public void onPalpitar(ActionEvent e) {
        modoPalpite = true;

        List<Button> visiveis = new ArrayList<>();
        for (int i = 0; i < cartaBotoes.length; i++) {
            if (!cartaAtiva[i]) {
                visiveis.add(cartaBotoes[i]);
            }
        }

        cardGrid.getChildren().clear();
        int index = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                if (index < visiveis.size()) {
                    cardGrid.add(visiveis.get(index), col, row);
                    index++;
                }
            }
        }
    }

    private void mostrarPainelConfirmacao() {
        VBox overlay = new VBox(15);
        overlay.setAlignment(Pos.CENTER);
        overlay.getStyleClass().add("confirmation-pane");

        StackPane.setAlignment(overlay, Pos.CENTER);

        Label msg = new Label("Deseja confirmar escolha?");
        msg.setStyle("-fx-font-size: 16px;");

        Button confirmar = new Button("Confirmar");
        Button cancelar = new Button("Cancelar");
        confirmar.getStyleClass().add("round-button");
        cancelar.getStyleClass().add("round-button");

        HBox botoes = new HBox(10, confirmar, cancelar);
        botoes.setAlignment(Pos.CENTER);

        overlay.getChildren().addAll(cartaSelecionada, msg, botoes);
        rootPane.getChildren().add(overlay);

        confirmar.setOnAction(e -> {
            System.out.println("Palpite confirmado!");
            rootPane.getChildren().remove(overlay);
            modoPalpite = false;
        });

        cancelar.setOnAction(e -> {
            cartaSelecionada.getStyleClass().remove("selecionada");
            rootPane.getChildren().remove(overlay);
            modoPalpite = false;

            cardGrid.getChildren().clear();
            int index = 0;
            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 5; col++) {
                    cartaBotoes[index].setVisible(true);

                    if (cartaAtiva[index]){
                        cartaBotoes[index].getStyleClass().remove("ativa");
                        cartaBotoes[index].getStyleClass().add("ativa");
                    } else {
                        cartaBotoes[index].getStyleClass().remove("ativa");
                    }

                    cardGrid.add(cartaBotoes[index], col, row);
                    index++;
                }
            }

            for (int i = 0; i < cartaBotoes.length; i++) {
                cartaBotoes[i].setVisible(true);
                if (cartaAtiva[i]) {
                    cartaBotoes[i].getStyleClass().add("ativa");
                }
            }
        });
    }

    private void addChatMessage(String message, boolean fromPlayer) {
        HBox container = new HBox();
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(200);
        label.setStyle("-fx-padding: 8 14; -fx-background-radius: 16;");

        if (fromPlayer) {
            container.setAlignment(Pos.CENTER_RIGHT);
            label.getStyleClass().add("chat-msg-player");
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
            label.getStyleClass().add("chat-msg-received");
        }

        container.getChildren().add(label);
        chatBox.getChildren().add(container);

        chatScroll.layout();
        chatScroll.setVvalue(1.0);
    }
}
