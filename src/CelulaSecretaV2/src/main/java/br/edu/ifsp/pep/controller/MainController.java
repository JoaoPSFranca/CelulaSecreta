package br.edu.ifsp.pep.controller;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.service.CartaService;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
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
    @FXML private VBox zoomContainer;
    @FXML private ImageView zoomCartaImageView;

    private CartaService cartaService = new CartaService();
    private List<Carta> todasAsCartas;
    private Carta suaCartaSecreta;

    private boolean[] cartaAtiva = new boolean[20];
    private Button[] cartaBotoes = new Button[20];
    private boolean chatLiberado = false;
    private boolean modoPalpite = false;
    private Button cartaSelecionada;

    private Image imagemVerso;

    @FXML
    public void initialize() {
        imagemVerso = new Image(getClass().getResource("/images/verso.png").toExternalForm());

        todasAsCartas = cartaService.carregarCartas();
        // Embaralha as cartas para que a ordem no grid mude a cada jogo
        //Collections.shuffle(todasAsCartas);

        int id = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 5; col++) {
                if (id < todasAsCartas.size()) {
                    Carta cartaAtual = todasAsCartas.get(id);

                    Button cartaButton = new Button();
                    cartaButton.getStyleClass().add("card-button");
                    cartaButton.setUserData(cartaAtual);

                    cartaButton.setUserData(cartaAtual);

                    String imagePath = "/images/carta_menor/" + cartaAtual.getNomeImagem() + ".png";
                    Image frenteImage = new Image(getClass().getResource(imagePath).toExternalForm());
                    ImageView frenteImageView = new ImageView(frenteImage);
                    frenteImageView.setFitHeight(165);
                    frenteImageView.setFitWidth(110);
                    frenteImageView.setPreserveRatio(true);

                    cartaButton.setGraphic(frenteImageView);

                    int r = row;
                    int index = id;

                    cartaButton.setOnMouseEntered(event -> {
                        if (cartaAtiva[index]) {
                            return;
                        }

                        Carta cartaDoBotao = (Carta) cartaButton.getUserData();
                        String imagemMaiorPath = "/images/carta_maior/" + cartaDoBotao.getNomeImagem() + ".png";
                        Image imagemMaior = new Image(getClass().getResource(imagemMaiorPath).toExternalForm());

                        zoomCartaImageView.setImage(imagemMaior);

                        Bounds boundsDoBotao = cartaButton.localToScene(cartaButton.getBoundsInLocal());

                        double xPos = boundsDoBotao.getMinX() + (boundsDoBotao.getWidth() / 2) - (zoomContainer.getWidth() / 2);

                        double yPos;
                        double offset = 10.0;

                        //evita que o zoom apareça fora da tela
                        if (r <= 1){
                            yPos = boundsDoBotao.getMaxY() + offset;
                            yPos += 165;
                        }
                        else{
                            yPos = boundsDoBotao.getMinY() - zoomContainer.getHeight() - offset;
                            yPos -= 175;
                        }

                        Bounds boundsDoRootPane = rootPane.localToScene(rootPane.getBoundsInLocal());
                        zoomContainer.setTranslateX(xPos - boundsDoRootPane.getMinX());
                        zoomContainer.setTranslateY(yPos - boundsDoRootPane.getMinY());

                        zoomContainer.setVisible(true);
                    });

                    cartaButton.setOnMouseExited(event -> {
                        zoomContainer.setVisible(false);
                    });

                    cartaButton.setOnAction(e -> alternarCarta(cartaButton, index));

                    cartaBotoes[index] = cartaButton;
                    cardGrid.add(cartaButton, col, row);
                    id++;
                }
            }
        }

        Random rand = new Random();
        suaCartaSecreta = todasAsCartas.get(rand.nextInt(todasAsCartas.size()));

        String secretImagePath = "/images/carta_sorteada/" + suaCartaSecreta.getNomeImagem() + ".png";
        Image secretImage = new Image(getClass().getResource(secretImagePath).toExternalForm());
        suaCartaImage.setImage(secretImage);

//        suaCartaImage.setFitWidth(245);
//        suaCartaImage.setFitHeight(368);
//        suaCartaImage.setPreserveRatio(false);

        chatInput.setDisable(true);
        btnSim.setOnAction(this::onSim);
        btnNao.setOnAction(this::onNao);
        btnEnviar.setOnAction(this::onEnviar);
        btnPerguntar.setOnAction(this::onPerguntar);
        btnPalpitar.setOnAction(this::onPalpitar);

        chatBox.setSpacing(8);
    }

    private void alternarCarta(Button carta, int index) {
        if (modoPalpite && cartaAtiva[index]) {
            return;
        }

        Node graphicNode = carta.getGraphic();

        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), graphicNode);
        rotateOut.setAxis(Rotate.Y_AXIS);
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);
        rotateOut.setInterpolator(Interpolator.EASE_BOTH);

        rotateOut.setOnFinished(e -> {
            ImageView newGraphic;
            // CORREÇÃO: Verificamos se a carta AGORA está ativa. Se sim, mostramos o verso.
            if (cartaAtiva[index]) {
                newGraphic = new ImageView(imagemVerso);
            } else { // Se não está ativa, mostramos a frente.
                Carta cartaDoBotao = (Carta) carta.getUserData();
                String imagePath = "/images/carta_menor/" + cartaDoBotao.getNomeImagem() + ".png";
                Image frenteImage = new Image(getClass().getResource(imagePath).toExternalForm());
                newGraphic = new ImageView(frenteImage);
            }

            newGraphic.setFitHeight(165);
            newGraphic.setFitWidth(110);
            newGraphic.setPreserveRatio(true);
            carta.setGraphic(newGraphic);

            RotateTransition rotateIn = new RotateTransition(Duration.millis(150), newGraphic);
            rotateIn.setAxis(Rotate.Y_AXIS);
            rotateIn.setFromAngle(270);
            rotateIn.setToAngle(360);
            rotateIn.setInterpolator(Interpolator.EASE_BOTH);
            rotateIn.play();
        });

        // O estado é invertido ANTES da animação, e a animação reage a esse NOVO estado.
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
