package br.edu.ifsp.pep.logic;

import br.edu.ifsp.pep.model.Pergunta;
import java.util.List;

public interface Opponent {

    Pergunta escolherPergunta(List<Pergunta> perguntasDisponiveis);

}