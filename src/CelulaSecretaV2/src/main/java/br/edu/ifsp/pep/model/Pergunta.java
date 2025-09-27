package br.edu.ifsp.pep.model;

import java.util.function.Predicate;

public class Pergunta {
    private final int id;
    private final String texto;
    private final Predicate<Carta> condicao;

    public Pergunta(int id, String texto, Predicate<Carta> condicao) {
        this.id = id;
        this.texto = texto;
        this.condicao = condicao;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    public boolean testar(Carta carta) {
        return condicao.test(carta);
    }
}
