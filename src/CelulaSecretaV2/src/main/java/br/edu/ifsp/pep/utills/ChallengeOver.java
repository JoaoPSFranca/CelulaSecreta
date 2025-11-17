package br.edu.ifsp.pep.utills;

import java.io.Serializable;

public record ChallengeOver(
    String mensagemResultado
) implements Serializable { }
