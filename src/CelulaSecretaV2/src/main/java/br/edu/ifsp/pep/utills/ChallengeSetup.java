package br.edu.ifsp.pep.utills;

import br.edu.ifsp.pep.model.ChallengeItem;

import java.io.Serializable;
import java.util.List;

public record ChallengeSetup(
    List<ChallengeItem> perguntas
) implements Serializable { }
