package br.edu.ifsp.pep.utills;

import java.io.Serializable;

public record ChallengeResult(
    int acertos,
    double tempoTotalSegundos
) implements Serializable { }
