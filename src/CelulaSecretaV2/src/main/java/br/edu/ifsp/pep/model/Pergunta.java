package br.edu.ifsp.pep.model;

import java.io.Serializable;
import java.util.function.Predicate;

public class Pergunta implements Serializable {
    private final int id;
    private final String texto;

    private transient Predicate<Carta> condicao;

    public Pergunta(int id, String texto, Predicate<Carta> condicao) {
        this.id = id;
        this.texto = texto;
        this.condicao = condicao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getTexto() { return texto; }
    public Predicate<Carta> getCondicao() { return condicao; }

    // Setter para reinicializar a condição
    public void setCondicao(Predicate<Carta> condicao) { this.condicao = condicao; }

    public boolean testar(Carta carta) {
        if (condicao == null) {
            // Se a condição for nula (pode acontecer após ser recebida pela rede),
            // a lógica precisará ser reinjetada. Por enquanto, retornamos false para evitar erros.
            System.err.println("Atenção: A condição da pergunta '" + texto + "' não foi inicializada.");
            return false;
        }
        return condicao.test(carta);
    }
}
