package br.edu.ifsp.pep.logic;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Pergunta;

public class AcaoOponente {

    private final Pergunta pergunta;
    private final Carta palpite;

    private AcaoOponente(Pergunta pergunta, Carta palpite) {
        this.pergunta = pergunta;
        this.palpite = palpite;
    }

    public static AcaoOponente criarAcaoDePergunta(Pergunta pergunta) {
        return new AcaoOponente(pergunta, null);
    }

    public static AcaoOponente criarAcaoDePalpite(Carta palpite) {
        return new AcaoOponente(null, palpite);
    }

    public boolean isPergunta() {
        return pergunta != null;
    }

    public boolean isPalpite() {
        return palpite != null;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public Carta getPalpite() {
        return palpite;
    }
}