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

        // Sorteia quem começa
        turnoAtual = new Random().nextBoolean() ? Turno.JOGADOR : Turno.OPONENTE;

//        System.out.println("Sua carta: " + suaEquipe.getCartaSecreta().getNome());
//        System.out.println("Carta do Oponente: " + equipeOponente.getCartaSecreta().getNome());
    }

    // --- Getters de Estado (para o Controller ler) ---
    public Turno getTurnoAtual() { return turnoAtual; }
    public boolean isEsperandoRespostaDoJogador() { return esperandoRespostaDoJogador; }
    public boolean isModoPalpite() { return modoPalpite; }
    public List<Carta> getTodasAsCartas() { return todasAsCartas; }
    public List<Pergunta> getBancoDePerguntas() { return bancoDePerguntas; }
    public Equipe getSuaEquipe() { return suaEquipe; }
    public Equipe getEquipeOponente() { return equipeOponente; }

    // --- Ações do Jogo (chamadas pelo Controller) ---

    public void alternarModoPalpite() {
        this.modoPalpite = !this.modoPalpite;
    }

    public void finalizarRespostaDoJogador() {
        this.esperandoRespostaDoJogador = false;
        this.turnoAtual = Turno.JOGADOR;
    }

    public boolean verificarPalpite(Carta cartaDoPalpite) {
        return cartaDoPalpite.getId() == equipeOponente.getCartaSecreta().getId();
    }

    public RespostaOponente processarRespostaDoOponente(Pergunta perguntaFeita) {
        boolean resposta = perguntaFeita.testar(equipeOponente.getCartaSecreta());
        this.turnoAtual = Turno.OPONENTE;
        return new RespostaOponente(resposta);
    }

    public Pergunta executarTurnoOponente() {
        List<Pergunta> perguntasDisponiveis = new ArrayList<>(bancoDePerguntas);
        perguntasDisponiveis.removeAll(perguntasJaFeitas);

        Pergunta perguntaEscolhida = opponent.escolherPergunta(perguntasDisponiveis);

        if (perguntaEscolhida != null) {
            perguntasJaFeitas.add(perguntaEscolhida);
            this.esperandoRespostaDoJogador = true;
        } else {
            this.turnoAtual = Turno.JOGADOR;
        }

        return perguntaEscolhida;
    }

    // Classe auxiliar para encapsular a resposta
    public record RespostaOponente(boolean resposta) {}
}