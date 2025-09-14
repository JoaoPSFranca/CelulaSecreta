package br.edu.ifsp.pep.service;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Reino;

import java.util.ArrayList;
import java.util.List;

public class CartaService {
    public List<Carta> carregarCartas() {
        List<Carta> cartas = new ArrayList<>();

        Reino animalia = new Reino(1, "Animalia", "Reino dos animais.");
        Reino plantae = new Reino(2, "Plantae", "Reino das plantas.");
        Reino fungi = new Reino(3, "Fungi", "Reino dos fungos.");
        Reino protista = new Reino(4, "Protista", "Reino dos protozoários e algas.");
        Reino monera = new Reino(5, "Monera", "Reino das bactérias.");

        // --- Cartas do Reino Animalia ---
        cartas.add(new Carta(1, "Adipócito", "Adipocito", "Célula que armazena gordura.", true, 1, animalia,
                List.of("Eucarionte", "Heterótrofo"),
                List.of("Armazenamento de energia", "Proteção térmica"),
                List.of("Núcleo", "Mitocôndria", "Complexo Golgiense", "Membrana Plasmática", "Citoplasma")));

        cartas.add(new Carta(2, "Célula Animal", "Celula_Animal", "Modelo de célula eucarionte animal.", true, 1, animalia,
                List.of("Eucarionte", "Heterótrofo"),
                List.of("Metabolismo", "Divisão celular"),
                List.of("Núcleo", "Mitocôndria", "Complexo Golgiense", "Lisossomo", "Ribossomos", "Retículo Endoplasmático")));

        cartas.add(new Carta(3, "Célula B Pancreática", "Celula_b_pancreatica", "Célula do pâncreas que produz insulina.", true, 3, animalia,
                List.of("Eucarionte", "Secretora"),
                List.of("Produção de hormônios"),
                List.of("Núcleo", "Mitocôndria", "Retículo Endoplasmático Rugoso", "Complexo Golgiense")));

        cartas.add(new Carta(4, "Célula Caliciforme", "Celula_calciforme", "Célula secretora de muco encontrada em epitélios.", true, 2, animalia,
                List.of("Eucarionte", "Secretora"),
                List.of("Produção de muco", "Lubrificação"),
                List.of("Núcleo", "Complexo Golgiense", "Retículo Endoplasmático Rugoso")));

        cartas.add(new Carta(5, "Célula de Leydig", "Celula_Leydig", "Célula produtora de testosterona nos testículos.", true, 3, animalia,
                List.of("Eucarionte", "Secretora"),
                List.of("Produção de hormônios"),
                List.of("Núcleo", "Retículo Endoplasmático Liso", "Mitocôndria")));

        cartas.add(new Carta(6, "Enterócito", "Enterocito", "Célula do epitélio intestinal.", true, 2, animalia,
                List.of("Eucarionte", "Absortiva"),
                List.of("Absorção de nutrientes"),
                List.of("Núcleo", "Mitocôndria", "Microvilosidades")));

        cartas.add(new Carta(7, "Macrófago", "Macrofago", "Célula do sistema imune que realiza fagocitose.", true, 2, animalia,
                List.of("Eucarionte", "Fagocitária"),
                List.of("Defesa do organismo", "Fagocitose"),
                List.of("Núcleo", "Lisossomos", "Fagossomos")));

        cartas.add(new Carta(8, "Neurônio", "Neuronio", "Célula do sistema nervoso.", true, 2, animalia,
                List.of("Eucarionte", "Excitável"),
                List.of("Transmissão de impulsos nervosos"),
                List.of("Núcleo", "Mitocôndria", "Corpos de Nissl", "Axônio", "Dendritos")));

        cartas.add(new Carta(9, "Neutrófilo", "Neutrofilo", "Tipo de leucócito, linha de frente da defesa imune.", true, 2, animalia,
                List.of("Eucarionte", "Fagocitária"),
                List.of("Defesa do organismo", "Fagocitose"),
                List.of("Núcleo multilobulado", "Lisossomos", "Grânulos")));

        // --- Cartas do Reino Plantae ---
        cartas.add(new Carta(10, "Célula Vegetal", "Celula_Vegetal", "Modelo de célula eucarionte vegetal.", true, 1, plantae,
                List.of("Eucarionte", "Autótrofo", "Parede Celular de Celulose"),
                List.of("Fotossíntese", "Respiração Celular"),
                List.of("Núcleo", "Cloroplasto", "Mitocôndria", "Vácuolo", "Parede Celular")));

        cartas.add(new Carta(11, "Célula-Guarda", "Celula_Guarda", "Controla a abertura e fechamento dos estômatos.", true, 2, plantae,
                List.of("Eucarionte", "Autótrofo"),
                List.of("Trocas gasosas", "Controle hídrico"),
                List.of("Núcleo", "Cloroplasto", "Vácuolo", "Parede Celular")));

        // --- Cartas do Reino Fungi ---
        cartas.add(new Carta(12, "Candida albicans", "Candida_albicans", "Fungo do tipo levedura, pode ser patogênico.", false, 2, fungi,
                List.of("Eucarionte", "Unicelular", "Parede Celular de Quitina"),
                List.of("Fermentação", "Decomposição"),
                List.of("Núcleo", "Mitocôndria", "Parede Celular")));

        cartas.add(new Carta(13, "Rhizopus sp", "Rhizopus_sp", "Gênero de bolores comuns em pães e frutas.", false, 1, fungi,
                List.of("Eucarionte", "Multicelular", "Parede Celular de Quitina"),
                List.of("Decomposição"),
                List.of("Núcleo", "Mitocôndria", "Parede Celular", "Hifas")));

        // --- Cartas do Reino Protista ---
        cartas.add(new Carta(14, "Chlamydomonas sp", "Chlamydomonas_sp", "Gênero de algas verdes unicelulares.", false, 2, protista,
                List.of("Eucarionte", "Unicelular", "Autótrofo", "Móvel"),
                List.of("Fotossíntese", "Locomoção"),
                List.of("Núcleo", "Cloroplasto", "Flagelos")));

        cartas.add(new Carta(15, "Paramecium sp", "Paramecio", "Protozoário ciliado de água doce.", false, 2, protista,
                List.of("Eucarionte", "Unicelular", "Móvel"),
                List.of("Locomoção", "Fagocitose"),
                List.of("Núcleo", "Vácuolo Contátil", "Cílios")));

        cartas.add(new Carta(16, "Plasmodium vivax", "Plasmodium_vivax", "Protozoário causador de uma forma de malária.", false, 3, protista,
                List.of("Eucarionte", "Unicelular", "Parasita"),
                List.of("Ciclo de vida complexo"),
                List.of("Núcleo", "Complexo apical")));

        cartas.add(new Carta(17, "Trypanosoma cruzi", "Trypanosoma_Cruzi", "Protozoário flagelado causador da Doença de Chagas.", false, 3, protista,
                List.of("Eucarionte", "Unicelular", "Móvel", "Parasita"),
                List.of("Locomoção"),
                List.of("Núcleo", "Flagelo", "Cinetoplasto")));

        // --- Cartas do Reino Monera ---
        cartas.add(new Carta(18, "Escherichia coli", "Escherichia_coli", "Bactéria comum no intestino de mamíferos.", false, 1, monera,
                List.of("Procarionte", "Unicelular"),
                List.of("Fermentação", "Respiração anaeróbica"),
                List.of("Nucleoide", "Ribossomos", "Membrana Plasmática", "Parede Celular")));

        cartas.add(new Carta(19, "Streptococcus sp", "Streptococcus_sp", "Gênero de bactérias em forma de coco, em cadeias.", false, 1, monera,
                List.of("Procarionte", "Unicelular"),
                List.of("Fermentação"),
                List.of("Nucleoide", "Ribossomos", "Parede Celular")));

        cartas.add(new Carta(20, "Treponema pallidum", "Treponema_pallidum", "Bactéria em forma de espiral, causadora da sífilis.", false, 3, monera,
                List.of("Procarionte", "Unicelular", "Móvel"),
                List.of("Locomoção"),
                List.of("Nucleoide", "Ribossomos", "Endoflagelos")));

        return cartas;
    }
}