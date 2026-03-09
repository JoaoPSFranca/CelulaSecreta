package br.edu.ifsp.pep.service;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Pergunta;
import java.util.ArrayList;
import java.util.List;

public class PerguntaService {
    public List<Pergunta> carregarPerguntas() {
        List<Pergunta> perguntas = new ArrayList<>();
        int id = 1;

        // --- Perguntas Gerais e de Reino ---
        perguntas.add(new Pergunta(id++, "É um organismo Eucarionte?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("Eucarionte"))));
        perguntas.add(new Pergunta(id++, "É um organismo Procarionte?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("Procarionte"))));
        perguntas.add(new Pergunta(id++, "Pertence ao Reino Fungi?",
                carta -> carta.getReino().getNome().equalsIgnoreCase("Fungi")));
        perguntas.add(new Pergunta(id++, "Pertence ao Reino Monera (Bactérias)?",
                carta -> carta.getReino().getNome().equalsIgnoreCase("Monera")));
        perguntas.add(new Pergunta(id++, "Pertence ao Reino Plantae?",
                carta -> carta.getReino().getNome().equalsIgnoreCase("Plantae")));

        // --- Perguntas sobre Organelas e Estruturas ---
        perguntas.add(new Pergunta(id++, "Possui Núcleo definido? (Núcleo e/ou Membrana Nuclear)",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("nucleo") || o.contains("membrana_nuclear"))));
        perguntas.add(new Pergunta(id++, "Possui Mitocôndria?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("mitocondria"))));
        perguntas.add(new Pergunta(id++, "Possui Ribossomos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("ribossomo"))));
        perguntas.add(new Pergunta(id++, "Possui Complexo Golgiense?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("complexo_golgiense"))));
        perguntas.add(new Pergunta(id++, "Possui Membrana Plasmática?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("membrana_plasmatica"))));
        perguntas.add(new Pergunta(id++, "Possui Membrana Celular?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("membrana_celular"))));
        perguntas.add(new Pergunta(id++, "Possui Parede Celular?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("parede_celular"))));
        perguntas.add(new Pergunta(id++, "Possui DNA aparente? (Está representado na imagem?)",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("dna"))));
        perguntas.add(new Pergunta(id++, "Possui Retículo representado?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("reticulo"))));
        perguntas.add(new Pergunta(id++, "Possui Vacúolo?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("vacuolo"))));
        perguntas.add(new Pergunta(id++, "Possui Cloroplasto (para fotossíntese)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("cloroplasto"))));
        perguntas.add(new Pergunta(id++, "Possui Lisossomos (para digestão celular)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("lisossomo"))));
        perguntas.add(new Pergunta(id++, "Possui nucleolo?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("nucleolo"))));
        perguntas.add(new Pergunta(id++, "Possui Microvilosidades?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("microvilosidades"))));
        perguntas.add(new Pergunta(id++, "Possui Plasmídio?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("plasmidio"))));

        // --- Perguntas específicas ---

        // Adipócito
        perguntas.add(new Pergunta(id++, "A Célula possui Reserva de Gordura?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("reserva_de_gordura"))));

        // Candida albicans
        perguntas.add(new Pergunta(id++, "A Célula possui Broto?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("broto"))));

        // Célula Animal
        perguntas.add(new Pergunta(id++, "A Célula possui Centríolo e Citoesqueleto representados?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("centriolo") && o.contains("citoesqueleto"))));

        // Célula b Pancreática
        perguntas.add(new Pergunta(id++, "A Célula produz Insulina?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("insulina"))));

        // Célula Calciforme
        perguntas.add(new Pergunta(id++, "A Célula possui Vesícula Secretora?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("vesicula_secretora"))));

        // Celula de Leydig
        perguntas.add(new Pergunta(id++, "A Célula possui Gotículas Lipídicas?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("goticulas_lipidicas"))));

        // Célula Guarda
        perguntas.add(new Pergunta(id++, "A Célula possui Ostíolo e Epiderme?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("ostiolo") && o.contains("epiderme"))));

        // Chlamydomonas sp
        perguntas.add(new Pergunta(id++, "A Célula possui Pirenoide e Grãos de Amido?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("pirenoide") && o.contains("graos_de_amido"))));

        // Escherichia coli
        perguntas.add(new Pergunta(id++, "A Célula possui Fímbria?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("fimbria"))));

        // Macrófago
        perguntas.add(new Pergunta(id++, "A Célula possui Pseudópode?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("pseudopode"))));

        // Neurônio
        perguntas.add(new Pergunta(id++, "A Célula possui Axônio e Dendritos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("axonio") && o.contains("dendritos"))));

        // Neutrofilo
        perguntas.add(new Pergunta(id++, "A Célula possui um núcleo em formato de ferradura?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("nucleo_ferradura"))));

        // Paramecio
        perguntas.add(new Pergunta(id++, "A Célula possui Cílios?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("cilios"))));

        // Plasmodium Vivax
        perguntas.add(new Pergunta(id++, "A Célula possui Roptria?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("roptria"))));

        // Rhizopus sp
        perguntas.add(new Pergunta(id++, "A Célula possui Esporos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("esporos"))));

        // Streptococcus sp
        perguntas.add(new Pergunta(id++, "A Célula possui glicocalice?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("glicocalice"))));

        // Treponema paliddum
        perguntas.add(new Pergunta(id++, "A Célula possui flagelo axial?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("flagelo_axial"))));

        // Trypanosoma cruzi
        perguntas.add(new Pergunta(id++, "A Célula possui Membrana Ondulatória?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("membrana_ondulatoria"))));

        return perguntas;
    }
}