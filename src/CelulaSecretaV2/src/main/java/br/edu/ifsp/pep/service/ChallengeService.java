package br.edu.ifsp.pep.service;

import br.edu.ifsp.pep.model.ChallengeItem;

import java.util.ArrayList;
import java.util.List;

public class ChallengeService {
    public List<ChallengeItem> carregarDesafios() {
        List<ChallengeItem> desafios = new ArrayList<>();

        desafios.add(new ChallengeItem(1,
                "Produz a maior parte do ATP (energia) da célula.",
                "Mitocôndria",
                List.of("Mitocôndria", "Lisossomo", "Cloroplasto", "Núcleo")
        ));

        desafios.add(new ChallengeItem(2,
                "Controla todas as atividades celulares e armazena o DNA.",
                "Núcleo",
                List.of("Ribossomo", "Núcleo", "Vacúolo", "Membrana Plasmática")
        ));

        desafios.add(new ChallengeItem(3,
                "Realiza a fotossíntese (exclusivo de plantas e algas).",
                "Cloroplasto",
                List.of("Parede Celular", "Complexo Golgiense", "Cloroplasto", "Mitocôndria")
        ));

        desafios.add(new ChallengeItem(4,
                "Responsável pela síntese de proteínas.",
                "Ribossomo",
                List.of("Ribossomo", "Retículo Endoplasmático", "Lisossomo", "Centríolo")
        ));

        desafios.add(new ChallengeItem(5,
                "Realiza a digestão de substâncias dentro da célula.",
                "Lisossomo",
                List.of("Vacúolo", "Lisossomo", "Peroxissomo", "Complexo Golgiense")
        ));

        return desafios;
    }
}
