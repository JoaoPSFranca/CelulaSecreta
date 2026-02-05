package br.edu.ifsp.pep.logic;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Pergunta;
import java.util.List;

public interface Opponent {

    AcaoOponente decidirAcao(List<Pergunta> perguntasDisponiveis, List<Carta> cartasPossiveis);

    void processarRespostaDoJogador(Pergunta pergunta, boolean resposta);

    void iniciarNovaPartida(List<Carta> todasAsCartas);
}