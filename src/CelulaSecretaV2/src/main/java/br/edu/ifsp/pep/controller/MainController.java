package br.edu.ifsp.pep.controller;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Equipe;
import br.edu.ifsp.pep.model.Turno;
import br.edu.ifsp.pep.model.Pergunta;
import br.edu.ifsp.pep.service.PerguntaService;
import br.edu.ifsp.pep.service.CartaService;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
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
    @FXML private TextField codigoSala;
    @FXML private ImageView suaCartaImage;
    @FXML private GridPane cardGrid;
    @FXML private Button btnSim;
    @FXML private Button btnNao;
    @FXML private Button btnPerguntar;
    @FXML private Button btnPalpitar;
    @FXML private ScrollPane chatScroll;
    @FXML private StackPane rootPane;
    @FXML private VBox zoomContainer;
    @FXML private ImageView zoomCartaImageView;

    private CartaService cartaService = new CartaService();
    private PerguntaService perguntaService = new PerguntaService();
    private List<Pergunta> bancoDePerguntas;
    private List<Carta> todasAsCartas;
    private List<Pergunta> perguntasJaFeitas = new ArrayList<>();

    private boolean[] cartaAtiva = new boolean[20];
    private Button[] cartaBotoes = new Button[20];
    private boolean chatLiberado = false;
    private boolean modoPalpite = false;
    private Button cartaSelecionada;
    private Equipe suaEquipe;
    private Equipe equipeOponente;
    private Turno turnoAtual;
    private boolean esperandoRespostaDoJogador = false;

    private Image imagemVerso;

    @FXML
    public void initialize() {
        imagemVerso = new Image(getClass().getResource("/images/verso.png").toExternalForm());

        todasAsCartas = cartaService.carregarCartas();
        // Embaralha as cartas para que a ordem no grid mude a cada jogo
        //Collections.shuffle(todasAsCartas);
        bancoDePerguntas = perguntaService.carregarPerguntas();

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

                        //evita que o 'zoom' apareça fora da tela
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

        List<Carta> baralhoParaSorteio = new ArrayList<>(todasAsCartas);
        Collections.shuffle(baralhoParaSorteio);

        suaEquipe = new Equipe("Sua Equipe");
        equipeOponente = new Equipe("Equipe Oponente");

        suaEquipe.setCartaSecreta(baralhoParaSorteio.removeFirst());
        equipeOponente.setCartaSecreta(baralhoParaSorteio.removeFirst());

        String secretImagePath = "/images/carta_sorteada/" + suaEquipe.getCartaSecreta().getNomeImagem() + ".png";
        Image secretImage = new Image(getClass().getResource(secretImagePath).toExternalForm());
        suaCartaImage.setImage(secretImage);

        btnSim.setOnAction(this::onSim);
        btnNao.setOnAction(this::onNao);
        btnPerguntar.setOnAction(this::onPerguntar);
        btnPalpitar.setOnAction(this::onPalpitar);

        chatBox.setSpacing(8);

        turnoAtual = new Random().nextBoolean() ? Turno.JOGADOR : Turno.OPONENTE;
        atualizarInterfacePorTurno();
        anunciarTurnoNoChat();
//        System.out.println("A carta do oponente é: " + equipeOponente.getCartaSecreta().getNomeImagem());
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

        if(modoPalpite && cartaAtiva[index]) {
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
        if (!esperandoRespostaDoJogador) return;
        addChatMessage("Sim", true);
        finalizarRespostaDoJogador();
    }

    public void onNao(ActionEvent e) {
        if (!esperandoRespostaDoJogador) return;
        addChatMessage("Não", true);
        finalizarRespostaDoJogador();
    }

    private void finalizarRespostaDoJogador() {
        esperandoRespostaDoJogador = false;
        turnoAtual = Turno.JOGADOR;
        atualizarInterfacePorTurno();
        anunciarTurnoNoChat();
    }

    public void onPerguntar(ActionEvent e) {
        mostrarPainelDePerguntas();
    }

    public void onPalpitar(ActionEvent e) {
        // Inverte o estado do modo palpite
        modoPalpite = !modoPalpite;

        if (modoPalpite) {
            // --- ENTRANDO NO MODO PALPITE ---
            // Altera a aparência do botão para indicar que está ativo
            btnPalpitar.setText("Cancelar Palpite");
            btnPalpitar.getStyleClass().add("active");

            // Desabilita o botão de pergunta
            btnPerguntar.setDisable(true);

            // Adiciona o brilho branco em todas as cartas viradas para cima
            for (int i = 0; i < cartaBotoes.length; i++) {
                if (!cartaAtiva[i]) { // Se a carta não está virada
                    cartaBotoes[i].getStyleClass().add("guess-mode-card");
                }
            }
        } else {
            // --- SAINDO DO MODO PALPITE ---
            // Restaura a aparência do botão
            btnPalpitar.setText("Palpitar");
            btnPalpitar.getStyleClass().remove("active");

            // Restaura a interface para o estado normal do turno do jogador
            atualizarInterfacePorTurno();

            // Remove o brilho de todas as cartas
            for (Button cartaBtn : cartaBotoes) {
                cartaBtn.getStyleClass().removeAll("guess-mode-card", "selecionada");
            }
        }
    }

    private void mostrarPainelConfirmacao() {
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);

        VBox confirmacaoPane = new VBox(20);
        confirmacaoPane.setAlignment(Pos.CENTER);
        confirmacaoPane.setStyle("-fx-background-color: #31413d; -fx-padding: 40; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        confirmacaoPane.setMaxSize(450, 400);

        Label msg = new Label("Confirmar este palpite?");
        msg.setStyle("-fx-font-size: 20px; -fx-text-fill: #e0e5e4;");

        ImageView palpiteView = new ImageView(((ImageView) cartaSelecionada.getGraphic()).getImage());
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
            Carta cartaDoPalpite = (Carta) cartaSelecionada.getUserData();
            Carta cartaSecretaOponente = equipeOponente.getCartaSecreta();

            if (cartaDoPalpite.getId() == cartaSecretaOponente.getId()) {
                exibirFimDeJogo(true);
            } else {
                exibirFimDeJogo(false);
            }
            rootPane.getChildren().remove(confirmacaoPane);
        });

        voltar.setOnAction(e -> {
            cardGrid.setDisable(false);
            btnPalpitar.setDisable(false);

            rootPane.getChildren().remove(confirmacaoPane);
        });
    }

    private void exibirFimDeJogo(boolean vitoria) {
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);
        btnPerguntar.setDisable(true);

        VBox fimDeJogoPane = new VBox(20);
        fimDeJogoPane.setAlignment(Pos.CENTER);
        fimDeJogoPane.setStyle("-fx-background-color: #31413d; -fx-padding: 50; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        fimDeJogoPane.setMaxSize(400, 300);

        Label titulo = new Label();
        if (vitoria) {
            titulo.setText("Você Venceu!");
            titulo.setStyle("-fx-font-size: 32px; -fx-text-fill: #a0d4c8; -fx-font-weight: bold;");
        } else {
            titulo.setText("Você Perdeu!");
            titulo.setStyle("-fx-font-size: 32px; -fx-text-fill: #ff8a80; -fx-font-weight: bold;");

            Label info = new Label("A carta secreta era:");
            info.setStyle("-fx-font-size: 16px; -fx-text-fill: #e0e5e4;");

            ImageView cartaCorretaView = new ImageView();
            String path = "/images/carta_maior/" + equipeOponente.getCartaSecreta().getNomeImagem() + ".png";
            cartaCorretaView.setImage(new Image(getClass().getResource(path).toExternalForm()));
            cartaCorretaView.setFitHeight(165);
            cartaCorretaView.setPreserveRatio(true);

            fimDeJogoPane.getChildren().addAll(info, cartaCorretaView);
        }

        Button fechar = new Button("Fechar Jogo");
        fechar.getStyleClass().add("round-button");
        fechar.setOnAction(e -> System.exit(0));

        fimDeJogoPane.getChildren().addAll(titulo, fechar);
        rootPane.getChildren().add(fimDeJogoPane);
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

    private void atualizarInterfacePorTurno() {
        // Se estiver esperando uma resposta do jogador, o estado da UI é especial
        if (esperandoRespostaDoJogador) {
            cardGrid.setDisable(true); // O jogador não pode virar cartas enquanto responde
            btnPerguntar.setDisable(true);
            btnPalpitar.setDisable(true);
            btnSim.setDisable(false); // Habilita os botões de resposta
            btnNao.setDisable(false);
            return; // Interrompe a execução aqui
        }

        // Lógica padrão de início de turno
        boolean isTurnoDoJogador = (turnoAtual == Turno.JOGADOR);

        cardGrid.setDisable(false);
        btnPerguntar.setDisable(!isTurnoDoJogador);
        btnPalpitar.setDisable(!isTurnoDoJogador);
        btnSim.setDisable(true); // Desabilitados por padrão
        btnNao.setDisable(true);

        // Se for a vez do oponente, chama a ação dele
        if (!isTurnoDoJogador) {
            executarTurnoOponente();
        }
    }

    private void anunciarTurnoNoChat() {
        if (turnoAtual == Turno.JOGADOR) {
            addSystemMessage("Sua vez de jogar!");
        } else {
            addSystemMessage("Vez do oponente...");
        }
    }

    private void mostrarPainelDePerguntas() {
        // Desabilita a interface principal
        cardGrid.setDisable(true);
        btnPalpitar.setDisable(true);
        btnPerguntar.setDisable(true);

        // Cria o painel
        VBox painelPerguntas = new VBox(20);
        painelPerguntas.setAlignment(Pos.CENTER);
        painelPerguntas.setStyle("-fx-background-color: #31413d; -fx-padding: 40; -fx-border-color: #a0d4c8; -fx-border-width: 3; -fx-border-radius: 20; -fx-background-radius: 20;");
        painelPerguntas.setMaxSize(600, 500);

        Label titulo = new Label("Escolha sua Pergunta");
        titulo.setStyle("-fx-font-size: 20px; -fx-text-fill: #e0e5e4;");

        // Cria a lista para exibir as perguntas
        ListView<Pergunta> listaDePerguntas = new ListView<>();
        listaDePerguntas.getItems().addAll(bancoDePerguntas);

        listaDePerguntas.setCellFactory(param -> {
            ListCell<Pergunta> cell = new ListCell<>() {
                @Override
                protected void updateItem(Pergunta item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTexto());
                    }
                }
            };
            cell.getStyleClass().add("perguntas-list-cell");
            return cell;
        });

        Button btnConfirmar = new Button("Perguntar");
        Button btnCancelar = new Button("Cancelar");
        btnConfirmar.getStyleClass().add("round-button");
        btnCancelar.getStyleClass().add("round-button");
        btnConfirmar.setDisable(true); // Começa desabilitado

        // Habilita o botão "Perguntar" apenas quando uma pergunta é selecionada
        listaDePerguntas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnConfirmar.setDisable(newSelection == null);
        });

        HBox botoes = new HBox(15, btnConfirmar, btnCancelar);
        botoes.setAlignment(Pos.CENTER);

        painelPerguntas.getChildren().addAll(titulo, listaDePerguntas, botoes);
        rootPane.getChildren().add(painelPerguntas);

        // Ações dos botões
        btnCancelar.setOnAction(e -> {
            rootPane.getChildren().remove(painelPerguntas);
            atualizarInterfacePorTurno();
        });

        btnConfirmar.setOnAction(e -> {
            Pergunta perguntaSelecionada = listaDePerguntas.getSelectionModel().getSelectedItem();
            if (perguntaSelecionada != null) {
                addChatMessage(perguntaSelecionada.getTexto(), true);
                rootPane.getChildren().remove(painelPerguntas);

                processarRespostaDoOponente(perguntaSelecionada);
            }
        });
    }

    private void processarRespostaDoOponente(Pergunta perguntaFeita) {
        // Trava a interface enquanto o oponente "pensa" na resposta
        cardGrid.setDisable(false);
        btnPerguntar.setDisable(true);
        btnPalpitar.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            // O oponente responde à pergunta com base na CARTA DELE
            boolean resposta = perguntaFeita.testar(equipeOponente.getCartaSecreta());

            if (resposta) {
                addChatMessage("Sim", false);
            } else {
                addChatMessage("Não", false);
            }

            turnoAtual = Turno.OPONENTE;
            atualizarInterfacePorTurno();
            anunciarTurnoNoChat();
        });
        delay.play();
    }

//    private void processarTurnoOponente(Pergunta perguntaFeita) {
//        turnoAtual = Turno.OPONENTE;
//        atualizarInterfacePorTurno();
//        anunciarTurnoNoChat();
//
//        PauseTransition delay = new PauseTransition(Duration.seconds(2));
//        delay.setOnFinished(event -> {
//            // Lógica da resposta do oponente
//            // O oponente responde com base na SUA carta secreta
//            boolean resposta = perguntaFeita.testar(equipeOponente.getCartaSecreta());
//
//            if (resposta) {
//                addChatMessage("Sim", false); // 'false' para balão de mensagem recebida
//            } else {
//                addChatMessage("Não", false);
//            }
//
//            // Devolve o turno para o jogador
//            turnoAtual = Turno.JOGADOR;
//            atualizarInterfacePorTurno();
//            anunciarTurnoNoChat();
//        });
//        delay.play();
//    }

    private void addSystemMessage(String message) {
        HBox container = new HBox();
        container.setAlignment(Pos.CENTER);

        Label label = new Label(message);
        label.getStyleClass().add("chat-msg-system");

        container.getChildren().add(label);
        chatBox.getChildren().add(container);

        chatScroll.layout();
        chatScroll.setVvalue(1.0);
    }

    private void processarTurnoOponente(Object o) {

    }

    private void executarTurnoOponente() {
        // Adiciona um delay para simular o oponente pensando
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> {
            // Lógica para escolher uma pergunta
            List<Pergunta> perguntasDisponiveis = new ArrayList<>(bancoDePerguntas);
            perguntasDisponiveis.removeAll(perguntasJaFeitas);

            if (perguntasDisponiveis.isEmpty()) {
                // Se não houver mais perguntas, o oponente passa a vez (ou poderia palpitar)
                turnoAtual = Turno.JOGADOR;
                atualizarInterfacePorTurno();
                anunciarTurnoNoChat();
                return;
            }

            Pergunta perguntaEscolhida = perguntasDisponiveis.get(new Random().nextInt(perguntasDisponiveis.size()));
            perguntasJaFeitas.add(perguntaEscolhida);

            addChatMessage(perguntaEscolhida.getTexto(), false); // Oponente faz a pergunta

            // Prepara a interface para o jogador responder
            esperandoRespostaDoJogador = true;
            atualizarInterfacePorTurno();
        });
        delay.play();
    }
}
