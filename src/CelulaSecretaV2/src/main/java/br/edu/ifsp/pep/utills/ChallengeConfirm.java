package br.edu.ifsp.pep.utills;

import java.io.Serializable;

public record ChallengeConfirm(
        boolean aceito
) implements Serializable { }
