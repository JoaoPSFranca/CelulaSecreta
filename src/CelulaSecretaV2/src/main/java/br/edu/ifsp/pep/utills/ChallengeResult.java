package br.edu.ifsp.pep.utills;

import java.io.Serializable;
import java.util.List;

public record ChallengeResult(
        int acertos,
        double tempoTotalSegundos,
        List<Boolean> respostas  // true = acertou, false = errou
) implements Serializable { }
