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
        perguntas.add(new Pergunta(id++, "É Unicelular?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("Unicelular"))));
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
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.contains("membrana")))); // Pega "membrana celular" e "membrana plasmática"


        // --- Perguntas sobre Estruturas Específicas e Diferenciadoras ---
        perguntas.add(new Pergunta(id++, "Possui Parede Celular?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("parede celular"))));
        perguntas.add(new Pergunta(id++, "Possui Cloroplasto (para fotossíntese)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("cloroplasto"))));
        perguntas.add(new Pergunta(id++, "Possui Lisossomos (para digestão celular)?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("lisossomo"))));
        perguntas.add(new Pergunta(id++, "Possui Flagelos ou Cílios para locomoção?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("flagelos") || o.equalsIgnoreCase("cílios"))));
        perguntas.add(new Pergunta(id++, "É um organismo parasita?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("parasita"))));
        perguntas.add(new Pergunta(id++, "Realiza fagocitose (é uma célula 'fagocitária')?",
                carta -> carta.getPropriedades().stream().anyMatch(p -> p.equalsIgnoreCase("fagocitária"))));
        perguntas.add(new Pergunta(id++, "Possui Microvilosidades?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("microvilosidades"))));
        perguntas.add(new Pergunta(id++, "Seu material genético (DNA) fica em um Nucleoide?",
                carta -> carta.getOrganelas().stream().anyMatch(o -> o.equalsIgnoreCase("nucleoide"))));


        return perguntas;
    }
}