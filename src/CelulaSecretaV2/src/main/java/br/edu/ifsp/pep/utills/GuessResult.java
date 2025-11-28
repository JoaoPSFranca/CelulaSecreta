package br.edu.ifsp.pep.utills;

import br.edu.ifsp.pep.model.Carta;

import java.io.Serializable;

public record GuessResult(
    boolean correto,
    Carta cartaDoJogadorQueRecebeuPalpite,  // Carta do jogador que foi alvo do palpite
    Carta cartaDoJogadorQuePalpitou          // Carta do jogador que fez o palpite
) implements Serializable { }