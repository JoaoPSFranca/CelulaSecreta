package br.edu.ifsp.pep.ui;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Equipe;
import br.edu.ifsp.pep.model.Pergunta;
import br.edu.ifsp.pep.model.Turno;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.application.Platform;

import java.util.List;
import java.util.function.Consumer;

public class UIManager {

    private final StackPane rootPane;
    private final GridPane cardGrid;
    private final VBox chatBox;
    private final ScrollPane chatScroll;
    private final ImageView suaCartaImage;
    private final Button btnPerguntar, btnPalpitar, btnSim, btnNao;
    private final VBox zoomContainer;
    private final ImageView zoomCartaImageView;
    private final Image imagemVerso;

    public UIManager(StackPane rootPane, GridPane cardGrid, VBox chatBox, ScrollPane chatScroll,
                     ImageView suaCartaImage, Button btnPerguntar, Button btnPalpitar,
                     Button btnSim, Button btnNao, VBox zoomContainer, ImageView zoomCartaImageView) {
        this.rootPane = rootPane;
        this.cardGrid = cardGrid;
        this.chatBox = chatBox;
        this.chatScroll = chatScroll;
        this.suaCartaImage = suaCartaImage;
        this.btnPerguntar = btnPerguntar;
        this.btnPalpitar = btnPalpitar;
        this.btnSim = btnSim;
        this.btnNao = btnNao;
        this.zoomContainer = zoomContainer;
        this.zoomCartaImageView = zoomCartaImageView;
        this.imagemVerso = new Image(getClass().getResource("/images/verso.png").toExternalForm());

        this.chatBox.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            this.chatScroll.setVvalue(1.0);
        });
    }

    public void exibirCartaSecreta(Carta carta) {
        String path = "/images/carta_sorteada/" + carta.getNomeImagem() + ".png";
        suaCartaImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
    }

    public void atualizarInterfacePorTurno(Turno turnoAtual, boolean esperandoResposta) {
        if (esperandoResposta) {
            cardGrid.setDisable(true);
            btnPerguntar.setDisable(true);
            btnPalpitar.setDisable(true);
            btnSim.setDisable(false);
            btnNao.setDisable(false);
            return;
        }

        boolean isTurnoDoJogador = (turnoAtual == Turno.JOGADOR);
        cardGrid.setDisable(false);
        btnPerguntar.setDisable(!isTurnoDoJogador);
        btnPalpitar.setDisable(!isTurnoDoJogador);
        btnSim.setDisable(true);
        btnNao.setDisable(true);
    }

    public void animarCarta(Button carta, boolean paraVerso, Carta cartaInfo) {
        Node graphicNode = carta.getGraphic();
        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), graphicNode);
        rotateOut.setAxis(Rotate.Y_AXIS);
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);
        rotateOut.setInterpolator(Interpolator.EASE_BOTH);

        rotateOut.setOnFinished(e -> {
            ImageView newGraphic;
            if (paraVerso) {
                newGraphic = new ImageView(imagemVerso);
            } else {
                String imagePath = "/images/carta_menor/" + cartaInfo.getNomeImagem() + ".png";
                newGraphic = new ImageView(new Image(getClass().getResource(imagePath).toExternalForm()));
            }
            newGraphic.setFitHeight(165);
            newGraphic.setFitWidth(110);
            carta.setGraphic(newGraphic);

            RotateTransition rotateIn = new RotateTransition(Duration.millis(150), newGraphic);
            rotateIn.setAxis(Rotate.Y_AXIS);
            rotateIn.setFromAngle(270);
            rotateIn.setToAngle(360);
            rotateIn.setInterpolator(Interpolator.EASE_BOTH);
            rotateIn.play();
        });

        rotateOut.play();
    }

    public void mostrarPainelDePerguntas(List<Pergunta> bancoDePerguntas, Consumer<Pergunta> onConfirmAction) {
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);
        btnPerguntar.setDisable(true);

        VBox painelPerguntas = new VBox(20);
        // ... (código de criação e estilo do painel, como antes)
        painelPerguntas.setAlignment(Pos.CENTER);
        painelPerguntas.setStyle("-fx-background-color: #31413d; -fx-padding: 40; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        painelPerguntas.setMaxSize(600, 500);

        Label titulo = new Label("Escolha sua Pergunta");
        titulo.setStyle("-fx-font-size: 20px; -fx-text-fill: #e0e5e4;");

        ListView<Pergunta> listaDePerguntas = new ListView<>();
        listaDePerguntas.getItems().addAll(bancoDePerguntas);
        listaDePerguntas.setCellFactory(param -> {
            ListCell<Pergunta> cell = new ListCell<>() {
                @Override
                protected void updateItem(Pergunta item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTexto());
                }
            };
            cell.getStyleClass().add("perguntas-list-cell");
            return cell;
        });

        Button btnConfirmar = new Button("Perguntar");
        Button btnCancelar = new Button("Cancelar");
        btnConfirmar.getStyleClass().add("round-button");
        btnCancelar.getStyleClass().add("round-button");
        btnConfirmar.setDisable(true);

        listaDePerguntas.getSelectionModel().selectedItemProperty().addListener((obs, old, aNew) -> btnConfirmar.setDisable(aNew == null));

        HBox botoes = new HBox(15, btnConfirmar, btnCancelar);
        botoes.setAlignment(Pos.CENTER);
        painelPerguntas.getChildren().addAll(titulo, listaDePerguntas, botoes);
        rootPane.getChildren().add(painelPerguntas);

        btnCancelar.setOnAction(e -> {
            rootPane.getChildren().remove(painelPerguntas);
            atualizarInterfacePorTurno(Turno.JOGADOR, false);
        });

        btnConfirmar.setOnAction(e -> {
            Pergunta pergunta = listaDePerguntas.getSelectionModel().getSelectedItem();
            if (pergunta != null) {
                rootPane.getChildren().remove(painelPerguntas);
                onConfirmAction.accept(pergunta); // Executa a ação passada pelo Controller
            }
        });
    }

    public void mostrarPainelConfirmacao(ImageView cartaSelecionadaView, Runnable onConfirmAction) {
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);

        VBox confirmacaoPane = new VBox(20);
        // ... (código de criação e estilo do painel, como antes)
        confirmacaoPane.setAlignment(Pos.CENTER);
        confirmacaoPane.setStyle("-fx-background-color: #31413d; -fx-padding: 40; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        confirmacaoPane.setMaxSize(450, 400);

        Label msg = new Label("Confirmar este palpite?");
        msg.setStyle("-fx-font-size: 20px; -fx-text-fill: #e0e5e4;");

        ImageView palpiteView = new ImageView(cartaSelecionadaView.getImage());
        palpiteView.setFitHeight(165);
        palpiteView.setFitWidth(110);

        Button confirmar = new Button("Confirmar");
        Button voltar = new Button("Voltar");
        confirmar.getStyleClass().add("round-button");
        voltar.getStyleClass().add("round-button");

        HBox botoes = new HBox(15, confirmar, voltar);
        botoes.setAlignment(Pos.CENTER);

        confirmacaoPane.getChildren().addAll(msg, palpiteView, botoes);
        rootPane.getChildren().add(confirmacaoPane);

        confirmar.setOnAction(e -> {
            rootPane.getChildren().remove(confirmacaoPane);
            onConfirmAction.run(); // Executa a ação passada pelo Controller
        });

        voltar.setOnAction(e -> {
            cardGrid.setDisable(false);
            btnPalpitar.setDisable(false);
            rootPane.getChildren().remove(confirmacaoPane);
        });
    }

    public void exibirFimDeJogo(boolean vitoria, Equipe oponente, Runnable onFecharAction) {
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);
        btnPerguntar.setDisable(true);

        VBox fimDeJogoPane = new VBox(20);
        // ... (código de criação e estilo do painel, como antes)
        fimDeJogoPane.setAlignment(Pos.CENTER);
        fimDeJogoPane.setStyle("-fx-background-color: #31413d; -fx-padding: 50; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        fimDeJogoPane.setMaxSize(400, 300);

        Label titulo = new Label(vitoria ? "Você Venceu!" : "Você Perdeu!");
        titulo.setStyle("-fx-font-size: 32px; -fx-text-fill: " + (vitoria ? "#a0d4c8" : "#ff8a80") + "; -fx-font-weight: bold;");

        fimDeJogoPane.getChildren().add(titulo);

        if (!vitoria) {
            Label info = new Label("A carta secreta era:");
            info.setStyle("-fx-font-size: 16px; -fx-text-fill: #e0e5e4;");

            ImageView cartaCorretaView = new ImageView();
            String path = "/images/carta_maior/" + oponente.getCartaSecreta().getNomeImagem() + ".png";
            cartaCorretaView.setImage(new Image(getClass().getResource(path).toExternalForm()));
            cartaCorretaView.setFitHeight(165);
            cartaCorretaView.setPreserveRatio(true);
            fimDeJogoPane.getChildren().addAll(info, cartaCorretaView);
        }

        Button fechar = new Button("Fechar Jogo");
        fechar.getStyleClass().add("round-button");
        fechar.setOnAction(e -> onFecharAction.run());

        fimDeJogoPane.getChildren().add(fechar);
        rootPane.getChildren().add(fimDeJogoPane);
    }

    public void exibirZoom(Button cartaButton, int row) {
        Carta cartaDoBotao = (Carta) cartaButton.getUserData();
        String imagemMaiorPath = "/images/carta_maior/" + cartaDoBotao.getNomeImagem() + ".png";
        zoomCartaImageView.setImage(new Image(getClass().getResource(imagemMaiorPath).toExternalForm()));

        Bounds boundsDoBotao = cartaButton.localToScene(cartaButton.getBoundsInLocal());
        double xPos = boundsDoBotao.getMinX() + (boundsDoBotao.getWidth() / 2) - (zoomContainer.getWidth() / 2);
        double yPos;
        double offset = 10.0;

        if (row <= 1) {
            yPos = boundsDoBotao.getMaxY() + offset;
            yPos += 165;
        } else {
            yPos = boundsDoBotao.getMinY() - zoomContainer.getHeight() - offset;
            yPos -= 175;
        }

        Bounds boundsDoRootPane = rootPane.localToScene(rootPane.getBoundsInLocal());
        zoomContainer.setTranslateX(xPos - boundsDoRootPane.getMinX());
        zoomContainer.setTranslateY(yPos - boundsDoRootPane.getMinY());
        zoomContainer.setVisible(true);
    }

    public void esconderZoom() {
        zoomContainer.setVisible(false);
    }

    public void addChatMessage(String message, boolean fromPlayer) {
        HBox container = new HBox();
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(200);

        if (fromPlayer) {
            container.setAlignment(Pos.CENTER_RIGHT);
            label.getStyleClass().add("chat-msg-player");
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
            label.getStyleClass().add("chat-msg-received");
        }
        container.getChildren().add(label);
        chatBox.getChildren().add(container);
    }

    public void addSystemMessage(String message) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);
        Label label = new Label(message);
        label.getStyleClass().add("chat-msg-system");
        container.getChildren().add(label);
        chatBox.getChildren().add(container);
    }

    public void atualizarModoPalpite(boolean modoPalpite, Button[] cartaBotoes, boolean[] cartaAtiva) {
        if (modoPalpite) {
            btnPalpitar.setText("Cancelar Palpite");
            btnPalpitar.getStyleClass().add("active");

            btnPerguntar.setDisable(true);

            for (int i = 0; i < cartaBotoes.length; i++) {
                if (!cartaAtiva[i]) {
                    cartaBotoes[i].getStyleClass().add("guess-mode-card");
                }
            }
        } else {
            btnPalpitar.setText("Palpitar");
            btnPalpitar.getStyleClass().remove("active");

            atualizarInterfacePorTurno(Turno.JOGADOR, false);

            for (Button cartaBtn : cartaBotoes) {
                cartaBtn.getStyleClass().removeAll("guess-mode-card", "selecionada");
            }
        }
    }
}