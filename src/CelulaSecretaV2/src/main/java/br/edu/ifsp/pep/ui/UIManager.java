package br.edu.ifsp.pep.ui;

import br.edu.ifsp.pep.model.*;
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
    private VBox fimDesafioPane;

    private final VBox painelDesafio;
    private final Label labelTimer;
    private final Label labelPerguntaDesafio;
    private final Label labelContador;
    private final GridPane gridOpcoesDesafio;

    public UIManager(StackPane rootPane, GridPane cardGrid, VBox chatBox, ScrollPane chatScroll,
                     ImageView suaCartaImage, Button btnPerguntar, Button btnPalpitar,
                     Button btnSim, Button btnNao, VBox zoomContainer, ImageView zoomCartaImageView, VBox painelDesafio, Label labelTimer, Label labelPerguntaDesafio, GridPane gridOpcoesDesafio, Label labelContador) {
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
        this.painelDesafio = painelDesafio;
        this.labelTimer = labelTimer;
        this.labelPerguntaDesafio = labelPerguntaDesafio;
        this.gridOpcoesDesafio = gridOpcoesDesafio;
        this.labelContador = labelContador;
        this.imagemVerso = new Image(getClass().getResource("/images/verso.png").toExternalForm());

        this.chatBox.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            this.chatScroll.setVvalue(1.0);
        });
    }

    public void exibirCartaSecreta(Carta carta) {
        String path = "/images/carta_sorteada/" + carta.getNomeImagem() + ".png";
        suaCartaImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
    }

    public void updateUIForTurnState(boolean isMyTurn, boolean isAwaitingPlayerResponse) {
        if (!isMyTurn) {
            cardGrid.setDisable(false);
            btnPerguntar.setDisable(true);
            btnPalpitar.setDisable(true);
            btnSim.setDisable(true);
            btnNao.setDisable(true);
        } else {
            if (isAwaitingPlayerResponse) { // Vez de Reponder Sim ou Não
                cardGrid.setDisable(true);
                btnPerguntar.setDisable(true);
                btnPalpitar.setDisable(true);
                btnSim.setDisable(false); // Habilita Sim
                btnNao.setDisable(false); // Habilita Não
            } else { // Vez de Palpitar / Perguntar
                cardGrid.setDisable(false);
                btnPerguntar.setDisable(false); // Habilita Perguntar
                btnPalpitar.setDisable(false); // Habilita Palpitar
                btnSim.setDisable(true);
                btnNao.setDisable(true);
            }
        }
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
            updateUIForTurnState(true, false);
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

    public void exibirFimDeJogo(boolean vitoria, Equipe oponente, Runnable onChallengeAction, Runnable onExitAction) {
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);
        btnPerguntar.setDisable(true);

        VBox fimDeJogoPane = new VBox(20);
        fimDeJogoPane.setAlignment(Pos.CENTER);
        fimDeJogoPane.setStyle("-fx-background-color: #31413d; -fx-padding: 50; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        fimDeJogoPane.setMaxSize(450, 450); // Aumentei um pouco a altura para os novos botões

        Label titulo = new Label(vitoria ? "Você Venceu!" : "Você Perdeu!");
        titulo.setStyle("-fx-font-size: 32px; -fx-text-fill: " + (vitoria ? "#a0d4c8" : "#ff8a80") + "; -fx-font-weight: bold;");

        fimDeJogoPane.getChildren().add(titulo);

        // Só mostra a carta se for DERROTA e a equipe não for nula
        if (!vitoria && oponente != null && oponente.getCartaSecreta() != null) {
            Label info = new Label("A carta secreta era:");
            info.setStyle("-fx-font-size: 16px; -fx-text-fill: #e0e5e4;");

            ImageView cartaCorretaView = new ImageView();
            String path = "/images/carta_maior/" + oponente.getCartaSecreta().getNomeImagem() + ".png";
            cartaCorretaView.setImage(new Image(getClass().getResource(path).toExternalForm()));
            cartaCorretaView.setFitHeight(165);
            cartaCorretaView.setPreserveRatio(true);
            fimDeJogoPane.getChildren().addAll(info, cartaCorretaView);
        }

        Button btnJogarDesafio = new Button("Jogar Desafio (Fase 2)");
        btnJogarDesafio.getStyleClass().add("round-button");
        btnJogarDesafio.setOnAction(e -> {
            rootPane.getChildren().remove(fimDeJogoPane); // Fecha o painel
            onChallengeAction.run(); // Executa a ação do desafio
        });

        Button btnSair = new Button("Sair do Jogo");
        btnSair.getStyleClass().add("round-button");
        btnSair.setOnAction(e -> onExitAction.run()); // Executa a ação de sair

        VBox.setMargin(btnJogarDesafio, new javafx.geometry.Insets(20, 0, 0, 0)); // Adiciona espaço
        fimDeJogoPane.getChildren().addAll(btnJogarDesafio, btnSair);
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

            updateUIForTurnState(true, false);

            for (Button cartaBtn : cartaBotoes) {
                cartaBtn.getStyleClass().removeAll("guess-mode-card", "selecionada");
            }
        }
    }

    public void transicionarParaDesafio() {
        rootPane.getChildren().removeIf(node -> node.getStyle().contains("-fx-background-color: #31413d;"));
        rootPane.getChildren().removeIf(node -> "fimDesafioPane".equals(node.getId()));
        this.fimDesafioPane = null;

        // Esconde a Fase 1
        cardGrid.setVisible(false);
        cardGrid.setManaged(false);
        chatBox.getParent().setVisible(false); // Esconde o VBox pai do chat
        chatBox.getParent().setManaged(false);
        suaCartaImage.getParent().getParent().setVisible(false); // Esconde o VBox pai dos botões da direita
        suaCartaImage.getParent().getParent().setManaged(false);

        // Mostra a Fase 2
        painelDesafio.setVisible(true);
        painelDesafio.setManaged(true);
    }

    public void exibirPerguntaDesafio(ChallengeItem item, Consumer<String> onOpcaoSelecionada) {
        labelPerguntaDesafio.setText(item.pergunta());
        gridOpcoesDesafio.getChildren().clear(); // Limpa opções anteriores

        if (item.opcoes() == null || item.opcoes().size() != 4) {
            labelPerguntaDesafio.setText("Erro: O item do desafio está mal formatado.");
            return;
        }

        int index = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                String opcaoTexto = item.opcoes().get(index++);
                Button opcaoBtn = new Button(opcaoTexto);
                opcaoBtn.getStyleClass().add("challenge-option-button"); // (Podemos estilizar isso no CSS depois)
                opcaoBtn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                // Define a ação do botão para chamar o callback
                opcaoBtn.setOnAction(e -> onOpcaoSelecionada.accept(opcaoTexto));

                gridOpcoesDesafio.add(opcaoBtn, col, row);
            }
        }
    }

    public void atualizarTimer(String segundosFormatados) {
        labelTimer.setText("Tempo: " + segundosFormatados);
    }

    public void atualizarContadorDesafio(int perguntaAtual, int totalPerguntas) {
        labelContador.setText(perguntaAtual + "/" + totalPerguntas);
    }

    public void exibirFimDesafio(int acertos, int totalPerguntas, String tempoFinal, List<Boolean> respostas) {
        if (this.fimDesafioPane != null && rootPane.getChildren().contains(this.fimDesafioPane)) {
            return; // O painel já está sendo exibido, não faça nada.
        }

        // Desabilita o painel de perguntas
        painelDesafio.setDisable(true);

        this.fimDesafioPane = new VBox(20);
        this.fimDesafioPane.setId("fimDesafioPane");

        fimDesafioPane.setAlignment(Pos.CENTER);
        fimDesafioPane.setStyle("-fx-background-color: #31413d; -fx-padding: 50; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        fimDesafioPane.setMaxSize(500, 400);

        Label titulo = new Label("Desafio Concluído!");
        titulo.setStyle("-fx-font-size: 32px; -fx-text-fill: #a0d4c8; -fx-font-weight: bold;");

        Label placarLabel = new Label("Seu placar: " + acertos + " / " + totalPerguntas);
        placarLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #e0e5e4;");

        Label tempoLabel = new Label("Tempo final: " + tempoFinal);
        tempoLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #e0e5e4;");

        // NOVO: Criar feedback visual das respostas
        HBox feedbackBox = new HBox(10);
        feedbackBox.setAlignment(Pos.CENTER);
        
        for (Boolean acertou : respostas) {
            Label iconLabel = new Label(acertou ? "✅" : "❌");
            iconLabel.setStyle("-fx-font-size: 24px;");
            feedbackBox.getChildren().add(iconLabel);
        }

        fimDesafioPane.getChildren().addAll(titulo, placarLabel, tempoLabel, feedbackBox);

        // Adiciona o painel de fim de desafio sobre o painel de desafio
        rootPane.getChildren().add(fimDesafioPane);
    }

    public void exibirResultadoFinal(String mensagemResultado, Runnable onFechar) {
        // Encontra o painel 'fimDesafioPane' que já está na tela
        if (this.fimDesafioPane == null) {
            System.err.println("Erro: O painel 'fimDesafioPane' é nulo. Não é possível exibir o resultado final.");
            return; // Painel não foi criado, não faz nada
        }

        Label vencedorLabel = new Label(mensagemResultado);
        vencedorLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: #e0e5e4; -fx-font-weight: bold;");
        VBox.setMargin(vencedorLabel, new javafx.geometry.Insets(15, 0, 0, 0));

        Button fecharBtn = new Button("Fechar Jogo");
        fecharBtn.getStyleClass().add("round-button");
        fecharBtn.setOnAction(e -> onFechar.run());
        VBox.setMargin(fecharBtn, new javafx.geometry.Insets(15, 0, 0, 0));

        fimDesafioPane.getChildren().addAll(vencedorLabel, fecharBtn);
    }

    public void exibirAguardandoOponente(String mensagem) {
        VBox aguardandoPane = new VBox(20);
        aguardandoPane.setId("aguardandoPane"); // ID para podermos fechar depois
        aguardandoPane.setAlignment(Pos.CENTER);
        aguardandoPane.setStyle("-fx-background-color: rgba(40, 40, 40, 0.85); -fx-padding: 50;");

        Label label = new Label(mensagem);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");
        ProgressIndicator pi = new ProgressIndicator();

        aguardandoPane.getChildren().addAll(label, pi);
        rootPane.getChildren().add(aguardandoPane);
    }

    public void fecharAguardando() {
        rootPane.getChildren().removeIf(node -> "aguardandoPane".equals(node.getId()));
    }

    public void exibirConviteDesafio(Runnable onAceitar, Runnable onRecusar) {
        // Primeiro, remove o painel de fim de jogo da Fase 1
        rootPane.getChildren().removeIf(node -> node.getStyle().contains("-fx-background-color: #31413d;"));

        VBox convitePane = new VBox(20);
        convitePane.setAlignment(Pos.CENTER);
        convitePane.setStyle("-fx-background-color: #31413d; -fx-padding: 50; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        convitePane.setMaxSize(450, 450);

        Label titulo = new Label("Convite para Desafio!");
        titulo.setStyle("-fx-font-size: 24px; -fx-text-fill: #a0d4c8; -fx-font-weight: bold;");
        Label sub = new Label("O Host convidou você para a Fase 2.");
        sub.setStyle("-fx-font-size: 16px; -fx-text-fill: #e0e5e4;");

        Button btnAceitar = new Button("Aceitar");
        btnAceitar.getStyleClass().add("round-button");
        btnAceitar.setOnAction(e -> {
            rootPane.getChildren().remove(convitePane);
            onAceitar.run();
        });

        Button btnRecusar = new Button("Recusar");
        btnRecusar.getStyleClass().add("round-button");
        btnRecusar.setOnAction(e -> {
            rootPane.getChildren().remove(convitePane);
            onRecusar.run();
        });

        HBox botoes = new HBox(15, btnAceitar, btnRecusar);
        botoes.setAlignment(Pos.CENTER);

        convitePane.getChildren().addAll(titulo, sub, botoes);
        rootPane.getChildren().add(convitePane);
    }
}
