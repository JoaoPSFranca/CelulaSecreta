package br.edu.ifsp.pep.logic;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Equipe;
import br.edu.ifsp.pep.model.Pergunta;
import br.edu.ifsp.pep.model.Turno;
import br.edu.ifsp.pep.service.CartaService;
import br.edu.ifsp.pep.service.PerguntaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {

    // --- Atributos de Estado e Dados ---
    private Equipe suaEquipe;
    private Equipe equipeOponente;
    private Turno turnoAtual;
    private boolean esperandoRespostaDoJogador = false;
    private boolean modoPalpite = false;
    private boolean jogoFinalizado = false;
    private boolean isMyTurn = false;
    private final List<Carta> todasAsCartas;
    private final List<Pergunta> bancoDePerguntas;
    private List<Pergunta> perguntasJaFeitas = new ArrayList<>();

    // --- Lógica do Oponente (Abstraída) ---
    private final Opponent opponent;

    public GameManager(Opponent opponent) {
        // Serviços
        CartaService cartaService = new CartaService();
        PerguntaService perguntaService = new PerguntaService();
        this.todasAsCartas = cartaService.carregarCartas();
        this.bancoDePerguntas = perguntaService.carregarPerguntas();

        // Armazena a implementação do oponente (seja IA, rede, etc.)
        this.opponent = opponent;

        iniciarNovoJogo();
    }

    public void iniciarNovoJogo() {
        // Sorteia as cartas
        List<Carta> baralhoParaSorteio = new ArrayList<>(todasAsCartas);
        Collections.shuffle(baralhoParaSorteio);

        suaEquipe = new Equipe("Sua Equipe");
        equipeOponente = new Equipe("Equipe Oponente");
        suaEquipe.setCartaSecreta(baralhoParaSorteio.removeFirst());
        equipeOponente.setCartaSecreta(baralhoParaSorteio.removeFirst());

        // Só tenta iniciar o oponente se ele não for nulo (ou seja, se for o modo vs. IA).
        if (this.opponent != null) {
            opponent.iniciarNovaPartida(todasAsCartas);
        }

        // Sorteia quem começa (isso precisa ser adaptado para a rede depois)
        turnoAtual = new Random().nextBoolean() ? Turno.JOGADOR : Turno.OPONENTE;

//        System.out.println("Sua carta: " + suaEquipe.getCartaSecreta().getNome());
        System.out.println("Carta do Oponente: " + equipeOponente.getCartaSecreta().getNome());
    }

    // --- Getters de Estado (para o Controller ler) ---
    public Turno getTurnoAtual() { return turnoAtual; }
    public boolean isEsperandoRespostaDoJogador() { return esperandoRespostaDoJogador; }
    public boolean isModoPalpite() { return modoPalpite; }
    public List<Carta> getTodasAsCartas() { return todasAsCartas; }
    public List<Pergunta> getBancoDePerguntas() { return bancoDePerguntas; }
    public Equipe getSuaEquipe() { return suaEquipe; }
    public Equipe getEquipeOponente() { return equipeOponente; }
    public boolean isJogoFinalizado() { return jogoFinalizado; }
    public boolean isMyTurn() { return isMyTurn; }
    public void setMyTurn(boolean myTurn) { isMyTurn = myTurn; }

    // --- Ações do Jogo (chamadas pelo Controller) ---

    public void alternarModoPalpite() {
        this.modoPalpite = !this.modoPalpite;
    }

    public void finalizarRespostaDoJogador() {
        this.esperandoRespostaDoJogador = false;
        this.turnoAtual = Turno.JOGADOR;
    }

    public boolean verificarPalpite(Carta cartaDoPalpite, boolean isMyGuess) {
        if (isMyGuess)
            return cartaDoPalpite.getId() == equipeOponente.getCartaSecreta().getId();
        else
            return cartaDoPalpite.getId() == suaEquipe.getCartaSecreta().getId();
    }

    public RespostaOponente processarRespostaDoOponente(Pergunta perguntaFeita) {
        boolean resposta = perguntaFeita.testar(equipeOponente.getCartaSecreta());
        this.turnoAtual = Turno.OPONENTE;
        return new RespostaOponente(resposta);
    }

    // Ação chamada pelo Controller quando o jogador responde à IA
    public void processarRespostaDoJogador(Pergunta perguntaFeitaPeloOponente, boolean resposta) {
        opponent.processarRespostaDoJogador(perguntaFeitaPeloOponente, resposta);
        this.esperandoRespostaDoJogador = false;
        this.turnoAtual = Turno.JOGADOR; // Volta o turno para o jogador
    }

    public ResultadoTurnoOponente executarTurnoOponente() {
        List<Pergunta> perguntasDisponiveis = new ArrayList<>(bancoDePerguntas);
        perguntasDisponiveis.removeAll(perguntasJaFeitas);

        AcaoOponente acao = opponent.decidirAcao(perguntasDisponiveis, new ArrayList<>());

        if (acao == null) {
            return new ResultadoTurnoOponente(null, null); // Nenhuma ação
        }

        if (acao.isPergunta()) {
            Pergunta perguntaEscolhida = acao.getPergunta();
            perguntasJaFeitas.add(perguntaEscolhida);
            this.esperandoRespostaDoJogador = true;
            return new ResultadoTurnoOponente(perguntaEscolhida, null);
        }

        if (acao.isPalpite()) {
            Carta palpite = acao.getPalpite();
            boolean acertou = palpite.getId() == suaEquipe.getCartaSecreta().getId();
            this.jogoFinalizado = true; // O jogo acaba aqui!
            return new ResultadoTurnoOponente(null, new PalpiteOponente(palpite, acertou));
        }

        return new ResultadoTurnoOponente(null, null);
    }

    // Classe auxiliar para encapsular a resposta
    public record RespostaOponente(boolean resposta) {}

    // Classe interna para encapsular o resultado do turno do oponente
    public record ResultadoTurnoOponente(Pergunta pergunta, PalpiteOponente palpite) {}
    public record PalpiteOponente(Carta carta, boolean acertou) {}
}