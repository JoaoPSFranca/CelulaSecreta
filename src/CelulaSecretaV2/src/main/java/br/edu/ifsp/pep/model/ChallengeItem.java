package br.edu.ifsp.pep.model;

import java.io.Serializable;
import java.util.List;

public record ChallengeItem(
        int id,
        String pergunta,
        String respostaCorreta,
        List<String> opcoes
) implements Serializable { }
