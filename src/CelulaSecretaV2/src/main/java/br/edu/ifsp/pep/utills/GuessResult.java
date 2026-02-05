package br.edu.ifsp.pep.utills;

import br.edu.ifsp.pep.model.Carta;

import java.io.Serializable;

public record GuessResult(
    boolean correto,
    Carta suaCartaSecreta,           // A carta secreta de quem recebeu o palpite (HOST)
    Carta cartaPalpitada,            // A carta que foi palpitada pelo CLIENT
    Carta cartaDoOponenteClient      // A carta do CLIENT (para o HOST saber qual era)
) implements Serializable { }