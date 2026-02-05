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

        // --- Perguntas sobre Organelas e Estruturas Comuns ---
        perguntas.add(new Pergunta(id++, "Possui Núcleo definido?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("núcleo") || o.equalsIgnoreCase("membrana nuclear"))));
        perguntas.add(new Pergunta(id++, "Possui Mitocôndria (para respiração celular)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("mitocôndria"))));
        perguntas.add(new Pergunta(id++, "Possui Ribossomos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("ribossomo"))));
        perguntas.add(new Pergunta(id++, "Possui Complexo Golgiense?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("complexo golgiense"))));
        perguntas.add(new Pergunta(id++, "Possui Membrana Plasmática/Celular?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("membrana"))));

        // --- Perguntas sobre Parede Celular (MODIFICADAS E NOVAS) ---
        perguntas.add(new Pergunta(id++, "Possui Parede Celular?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("parede celular"))));

        perguntas.add(new Pergunta(id++, "Possui parede celular de quitina?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("parede celular de quitina"))));

        perguntas.add(new Pergunta(id++, "Possui parede celular de celulose?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("parede celular de celulose"))));

        perguntas.add(new Pergunta(id++, "Possui parede celular de peptideoglicano?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("parede celular de peptideoglicano"))));

        // --- Perguntas sobre Estruturas Específicas e Diferenciadoras ---
        perguntas.add(new Pergunta(id++, "Possui Cloroplasto (para fotossíntese)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("cloroplasto"))));
        perguntas.add(new Pergunta(id++, "Possui Lisossomos (para digestão celular)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("lisossomo"))));
        perguntas.add(new Pergunta(id++, "Possui Flagelos ou Cílios para locomoção?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("flagelos") || o.equalsIgnoreCase("flagelo") || o.equalsIgnoreCase("cílios"))));
        perguntas.add(new Pergunta(id++, "É um organismo parasita?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("parasita"))));
        perguntas.add(new Pergunta(id++, "Realiza fagocitose (é uma célula 'fagocitária')?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("fagocitária"))));
        perguntas.add(new Pergunta(id++, "Seu material genético (DNA) fica em um Nucleoide?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("nucleoide"))));

        // --- NOVAS PERGUNTAS ESPECÍFICAS POR CÉLULA ---

        // Célula β pancreática
        perguntas.add(new Pergunta(id++, "Apresenta transportador de glicose (GLUT2)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("glut2"))));

        perguntas.add(new Pergunta(id++, "Realiza secreção de insulina?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("insulina"))));

        // Adipócito
        perguntas.add(new Pergunta(id++, "Armazena gordura?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("gotículas lipídicas")) ||
                        carta.getFuncoes().stream().anyMatch(f -> f.toLowerCase().contains("armazenamento de energia"))));

        // Célula Guarda
        perguntas.add(new Pergunta(id++, "Apresenta ostíolo?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("ostíolo"))));

        // Neurônio
        perguntas.add(new Pergunta(id++, "Possui dendritos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("dendritos"))));

        perguntas.add(new Pergunta(id++, "Possui axônio?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("axônio"))));

        // Célula de Leydig
        perguntas.add(new Pergunta(id++, "Apresenta pequenas gotículas lipídicas?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("gotículas lipídicas"))));

        // Rhizopus sp
        perguntas.add(new Pergunta(id++, "Apresenta esporos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("esporos"))));

        // Célula Caliciforme
        perguntas.add(new Pergunta(id++, "Possui vesícula secretora?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("vesículas secretoras"))));

        // Enterócito
        perguntas.add(new Pergunta(id++, "Possui Microvilosidades?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("microvilosidades"))));

        // Neutrófilo
        perguntas.add(new Pergunta(id++, "Apresenta grânulo primário?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("grânulo primário"))));

        // Célula Vegetal
        perguntas.add(new Pergunta(id++, "Apresenta vacúolo bem desenvolvido?",
                carta -> carta.getNome().equalsIgnoreCase("Célula Vegetal") &&
                        carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("vacúolo"))));

        // Paramecium sp
        perguntas.add(new Pergunta(id++, "Presença de vacúolo contrátil?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("vacuola contráctil"))));

        perguntas.add(new Pergunta(id++, "Presença de cílios?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("cílios"))));

        // Plasmodium vivax
        perguntas.add(new Pergunta(id++, "Apresenta apicoplasto?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("apicoplasto"))));

        // Chlamydomonas sp
        perguntas.add(new Pergunta(id++, "Presença de pirenoide?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("pirenoide"))));

        perguntas.add(new Pergunta(id++, "Presença de grão de amido?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("grão de amido"))));

        // Trypanosoma cruzi
        perguntas.add(new Pergunta(id++, "Apresenta membrana ondulante?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.toLowerCase().contains("membrana ondulatória"))));

        // Escherichia coli
        perguntas.add(new Pergunta(id++, "Presença de fímbria?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("fímbria"))));

        // Streptococcus sp
        perguntas.add(new Pergunta(id++, "Apresenta glicocálice?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("glicocálice"))));

        // Treponema pallidum
        perguntas.add(new Pergunta(id++, "Apresenta flagelo axial?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("flagelo axial"))));

        // Macrófago
        perguntas.add(new Pergunta(id++, "Apresenta pseudópodes?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("pseudópodes"))));

        // Candida albicans
        perguntas.add(new Pergunta(id++, "Apresenta brotos?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("brotos"))));

        return perguntas;
    }
}