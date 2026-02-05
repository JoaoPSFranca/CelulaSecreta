package br.edu.ifsp.pep.service;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Reino;

import java.util.List;
import java.util.ArrayList;

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
                List.of("mitocôndria", "membrana plasmática", "núcleo", "complexo golgiense", "gotículas lipídicas")));

        cartas.add(new Carta(2, "Célula Animal", "Celula_Animal", "Modelo de célula eucarionte animal.", true, 1, animalia,
                List.of("Eucarionte", "Heterótrofo"),
                List.of("Metabolismo", "Divisão celular"),
                List.of("citoesqueleto", "mitocôndria", "lisossomo", "retículo endoplasmático", "membrana celular", "ribossomo", "centríolo", "complexo golgiense", "vacúolo", "nucleolo", "membrana nuclear")));

        cartas.add(new Carta(3, "Célula B Pancreática", "Celula_b_pancreatica", "Célula do pâncreas que produz insulina.", true, 3, animalia,
                List.of("Eucarionte", "Secretora"),
                List.of("Recebe: insulina, glucose", "Produz: ca++, atp, k+", "Ejeta: vdcc"),
                List.of("glut2", "mitocôndria", "núcleo", "retículo endoplasmático", "complexo golgiense", "vesículas de insulina")));

        cartas.add(new Carta(4, "Célula Caliciforme", "Celula_calciforme", "Célula secretora de muco encontrada em epitélios.", true, 2, animalia,
                List.of("Eucarionte", "Secretora"),
                List.of("Produção de muco", "Lubrificação"),
                List.of("microviscosidade", "vesículas secretoras", "retículo endoplasmático", "complexo golgiense", "núcleo")));

        cartas.add(new Carta(5, "Célula de Leydig", "Celula_Leydig", "Célula produtora de testosterona nos testículos.", true, 3, animalia,
                List.of("Eucarionte", "Secretora"),
                List.of("Produção de hormônios"),
                List.of("retículo endoplasmático", "mitocôndria", "gotículas lipídicas", "núcleo", "nucléolo")));

        cartas.add(new Carta(6, "Enterócito", "Enterocito", "Célula do epitélio intestinal.", true, 2, animalia,
                List.of("Eucarionte", "Absortiva"),
                List.of("Absorção de nutrientes"),
                List.of("vacúolo", "citoplasma", "membrana celular", "mitocôndria", "complexo golgiense", "dna", "membrana nuclear", "microvilosidades")));

        cartas.add(new Carta(7, "Macrófago", "Macrofago", "Célula do sistema imune que realiza fagocitose.", true, 2, animalia,
                List.of("Eucarionte", "Fagocitária"),
                List.of("Defesa do organismo", "Fagocitose"),
                List.of("lisossomo", "complexo golgiense", "mitocôndria", "retículo endoplasmático", "ribossomo", "pseudópodes", "núcleo")));

        cartas.add(new Carta(8, "Neurônio", "Neuronio", "Célula do sistema nervoso.", true, 2, animalia,
                List.of("Eucarionte", "Excitável"),
                List.of("Transmissão de impulsos nervosos"),
                List.of("ribossomo", "citoplasma", "membrana celular", "mitocôndria", "membrana nuclear", "dna", "dendritos", "axônio", "núcleo")));

        cartas.add(new Carta(9, "Neutrófilo", "Neutrofilo", "Tipo de leucócito, linha de frente da defesa imune.", true, 2, animalia,
                List.of("Eucarionte", "Fagocitária"),
                List.of("Defesa do organismo", "Fagocitose"),
                List.of("grânulo primário", "grânulo secundário", "mitocôndria", "núcleo")));

        // --- Cartas do Reino Plantae ---
        cartas.add(new Carta(10, "Célula Vegetal", "Celula_Vegetal", "Modelo de célula eucarionte vegetal.", true, 1, plantae,
                List.of("Eucarionte", "Autótrofo", "Parede Celular de Celulose"),
                List.of("Fotossíntese", "Respiração Celular"),
                List.of("complexo golgiense", "lisossomo", "vacúolo", "mitocôndria", "cloroplasto", "ribossomo", "retículo endoplasmático rugoso", "retículo endoplasmático liso", "núcleo", "nucléolo", "parede celular de celulose")));

        cartas.add(new Carta(11, "Célula-Guarda", "Celula_Guarda", "Controla a abertura e fechamento dos estômatos.", true, 2, plantae,
                List.of("Eucarionte", "Autótrofo"),
                List.of("Trocas gasosas", "Controle hídrico"),
                List.of("núcleo", "ostíolo", "epiderme", "cloroplasto", "parede celular de celulose")));

        // --- Cartas do Reino Fungi ---
        cartas.add(new Carta(12, "Candida albicans", "Candida_albicans", "Fungo do tipo levedura, pode ser patogênico.", false, 2, fungi,
                List.of("Eucarionte", "Unicelular", "Parede Celular de Quitina"),
                List.of("Fermentação", "Decomposição"),
                List.of("mitocôndria", "membrana nuclear", "dna", "vacúolo", "ribossomo", "complexo golgiense", "parede celular de quitina", "membrana celular", "brotos", "núcleo")));

        cartas.add(new Carta(13, "Rhizopus sp", "Rhizopus_sp", "Gênero de bolores comuns em pães e frutas.", false, 1, fungi,
                List.of("Eucarionte", "Multicelular", "Parede Celular de Quitina"),
                List.of("Decomposição"),
                List.of("citoplasma", "vacúolo", "dna", "membrana nuclear", "parede celular de quitina", "membrana celular", "mitocôndria", "retículo", "esporos", "núcleo")));

        // --- Cartas do Reino Protista ---
        cartas.add(new Carta(14, "Chlamydomonas sp", "Chlamydomonas_sp", "Gênero de algas verdes unicelulares.", false, 2, protista,
                List.of("Eucarionte", "Unicelular", "Autótrofo", "Móvel"),
                List.of("Fotossíntese", "Locomoção"),
                List.of("flagelos", "mitocôndria", "núcleo", "grão de amido", "pirenoide", "complexo golgiense", "cloroplasto", "vacúolo")));

        cartas.add(new Carta(15, "Paramecium sp", "Paramecio", "Protozoário ciliado de água doce.", false, 2, protista,
                List.of("Eucarionte", "Unicelular", "Móvel"),
                List.of("Locomoção", "Fagocitose"),
                List.of("vacuola contráctil", "canal radial", "micronúcleo", "citoplasma", "vacuolas de alimentos", "poro anal", "boca celular", "apertura bucal", "surco oral", "membrana plasmática", "cílios")));

        cartas.add(new Carta(16, "Plasmodium vivax", "Plasmodium_vivax", "Protozoário causador de uma forma de malária.", false, 3, protista,
                List.of("Eucarionte", "Unicelular", "Parasita"),
                List.of("Ciclo de vida complexo"),
                List.of("roptria", "citoplasma", "mitocôndria", "apicoplasto", "retículo endoplasmático", "núcleo", "complexo golgiense", "grânulos densos")));

        cartas.add(new Carta(17, "Trypanosoma cruzi", "Trypanosoma_cruzi", "Protozoário flagelado causador da Doença de Chagas.", false, 3, protista,
                List.of("Eucarionte", "Unicelular", "Móvel", "Parasita"),
                List.of("Locomoção"),
                List.of("mitocôndria", "ribossomo", "retículo", "membrana ondulatória", "membrana nuclear", "dna", "complexo golgiense", "vacúolo", "flagelo", "núcleo")));

        // --- Cartas do Reino Monera ---
        cartas.add(new Carta(18, "Escherichia coli", "Escherichia_coli", "Bactéria comum no intestino de mamíferos.", false, 1, monera,
                List.of("Procarionte", "Unicelular"),
                List.of("Fermentação", "Respiração anaeróbica"),
                List.of("membrana celular", "parede celular de peptideoglicano", "citoplasma", "ribossomo", "dna", "plasmídio", "fímbria")));

        cartas.add(new Carta(19, "Streptococcus sp", "Streptococcus_sp", "Gênero de bactérias em forma de coco, em cadeias.", false, 1, monera,
                List.of("Procarionte", "Unicelular"),
                List.of("Fermentação"),
                List.of("ribossomo", "citoplasma", "dna", "parede celular de peptideoglicano", "membrana plasmática", "glicocálice")));

        cartas.add(new Carta(20, "Treponema pallidum", "Treponema_pallidum", "Bactéria em forma de espiral, causadora da sífilis.", false, 3, monera,
                List.of("Procarionte", "Unicelular", "Móvel"),
                List.of("Locomoção"),
                List.of("plasmídio", "citoplasma", "dna", "ribossomo", "membrana celular", "parede celular de peptideoglicano", "flagelo axial")));

        return cartas;
    }
}