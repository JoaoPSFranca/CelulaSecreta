package br.edu.ifsp.pep.utills;

import java.io.Serializable;

/**
 * Mensagem para notificar que um jogador saiu/abandonou a partida
 */
public record GameAborted(String reason) implements Serializable {

    public static final long serialVersionUID = 1L;
}

