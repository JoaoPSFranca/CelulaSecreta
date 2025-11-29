package br.edu.ifsp.pep.utills;

import br.edu.ifsp.pep.model.Carta;

import java.io.Serializable;

/**
 * Representa um palpite enviado pela rede com a carta do palpitante
 */
public record GuessWithCard(
    Carta cartaPalpitada,    // A carta que foi palpitada (qual o adversário tem)
    Carta cartaDoPalpitante  // A carta do jogador que está palpitando (para sincronização)
) implements Serializable { }

