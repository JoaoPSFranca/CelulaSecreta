package br.edu.ifsp.pep.logic;

import br.edu.ifsp.pep.model.Pergunta;
import java.util.List;
import java.util.Random;

public class OpponentAI implements Opponent {

    @Override
    public Pergunta escolherPergunta(List<Pergunta> perguntasDisponiveis) {
        if (perguntasDisponiveis == null || perguntasDisponiveis.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int index = random.nextInt(perguntasDisponiveis.size());
        return perguntasDisponiveis.get(index);
    }
}