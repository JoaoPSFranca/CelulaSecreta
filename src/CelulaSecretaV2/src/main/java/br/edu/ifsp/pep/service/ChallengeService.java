package br.edu.ifsp.pep.service;

import br.edu.ifsp.pep.model.ChallengeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChallengeService {

    /**
     * Carrega todos os 40 desafios disponíveis no banco de questões.
     * @return Lista contendo todos os desafios disponíveis
     */
    public List<ChallengeItem> carregarDesafios() {
        List<ChallengeItem> desafios = new ArrayList<>();

        // ============ MEMBRANA PLASMÁTICA ============
        desafios.add(new ChallengeItem(1,
                "Qual estrutura é responsável pela delimitação e proteção celular?",
                "Membrana Plasmática",
                List.of("Membrana Plasmática", "Parede Celular", "Citoesqueleto", "Cápsula")
        ));

        desafios.add(new ChallengeItem(2,
                "Qual função NÃO é realizada pela membrana plasmática?",
                "Síntese de proteínas",
                List.of("Transporte seletivo", "Recepção de estímulos", "Síntese de proteínas", "Reconhecimento celular")
        ));

        // ============ COMPLEXO DE GOLGI ============
        desafios.add(new ChallengeItem(3,
                "Qual organela é responsável pela modificação de proteínas através da glicosilação?",
                "Complexo de Golgi",
                List.of("Retículo Endoplasmático", "Complexo de Golgi", "Ribossomo", "Lisossomo")
        ));

        desafios.add(new ChallengeItem(4,
                "Qual organela sintetiza lipídeos como glicolipídios e esfingomielina?",
                "Complexo de Golgi",
                List.of("Mitocôndria", "Peroxissomo", "Complexo de Golgi", "Retículo Endoplasmático Liso")
        ));

        // ============ RETÍCULO ENDOPLASMÁTICO ============
        desafios.add(new ChallengeItem(5,
                "Qual estrutura é responsável pela síntese de proteínas e seu dobramento correto?",
                "Retículo Endoplasmático Rugoso",
                List.of("Retículo Endoplasmático Liso", "Retículo Endoplasmático Rugoso", "Complexo de Golgi", "Ribossomo")
        ));

        desafios.add(new ChallengeItem(6,
                "Onde ocorre a síntese de fosfolipídeos, esteroides e gorduras?",
                "Retículo Endoplasmático Liso",
                List.of("Retículo Endoplasmático Rugoso", "Complexo de Golgi", "Retículo Endoplasmático Liso", "Peroxissomo")
        ));

        desafios.add(new ChallengeItem(7,
                "Qual organela atua na detoxificação do organismo, convertendo substâncias tóxicas em produtos solúveis?",
                "Retículo Endoplasmático Liso",
                List.of("Lisossomo", "Peroxissomo", "Retículo Endoplasmático Liso", "Complexo de Golgi")
        ));

        desafios.add(new ChallengeItem(8,
                "Em células musculares, qual estrutura armazena cálcio para as contrações?",
                "Retículo Endoplasmático Liso",
                List.of("Mitocôndria", "Retículo Endoplasmático Liso", "Sarcômero", "Vacúolo")
        ));

        // ============ RIBOSSOMO ============
        desafios.add(new ChallengeItem(9,
                "Qual organela funciona como 'fábrica' que lê o RNA mensageiro para formar proteínas?",
                "Ribossomo",
                List.of("Ribossomo", "Complexo de Golgi", "Núcleo", "Retículo Endoplasmático")
        ));

        desafios.add(new ChallengeItem(10,
                "O processo de união de aminoácidos para formar cadeias de proteínas é chamado de:",
                "Tradução",
                List.of("Transcrição", "Replicação", "Tradução", "Glicosilação")
        ));

        // ============ LISOSSOMO ============
        desafios.add(new ChallengeItem(11,
                "Qual organela realiza a digestão intracelular de materiais externos e componentes envelhecidos?",
                "Lisossomo",
                List.of("Peroxissomo", "Lisossomo", "Vacúolo", "Complexo de Golgi")
        ));

        desafios.add(new ChallengeItem(12,
                "A digestão de componentes celulares envelhecidos ou danificados é chamada de:",
                "Autofagia",
                List.of("Heterofagia", "Autofagia", "Apoptose", "Fagocitose")
        ));

        // ============ PEROXISSOMO ============
        desafios.add(new ChallengeItem(13,
                "Qual organela é responsável pela degradação de peróxido de hidrogênio?",
                "Peroxissomo",
                List.of("Lisossomo", "Mitocôndria", "Peroxissomo", "Vacúolo")
        ));

        desafios.add(new ChallengeItem(14,
                "A metabolização do álcool (etanol) em acetaldeído ocorre principalmente em qual organela?",
                "Peroxissomo",
                List.of("Lisossomo", "Retículo Endoplasmático Liso", "Mitocôndria", "Peroxissomo")
        ));

        desafios.add(new ChallengeItem(15,
                "Além do retículo endoplasmático, qual organela possui enzimas para síntese de colesterol?",
                "Peroxissomo",
                List.of("Complexo de Golgi", "Mitocôndria", "Peroxissomo", "Lisossomo")
        ));

        // ============ MITOCÔNDRIA ============
        desafios.add(new ChallengeItem(16,
                "Qual organela é conhecida como a 'usina de energia' da célula?",
                "Mitocôndria",
                List.of("Cloroplasto", "Mitocôndria", "Núcleo", "Peroxissomo")
        ));

        desafios.add(new ChallengeItem(17,
                "Além da produção de ATP, qual função a mitocôndria NÃO realiza?",
                "Síntese de proteínas",
                List.of("Homeostase do cálcio", "Apoptose", "Síntese de proteínas", "Geração de espécies reativas de oxigênio")
        ));

        // ============ CITOESQUELETO ============
        desafios.add(new ChallengeItem(18,
                "Quais são os três componentes principais do citoesqueleto?",
                "Filamentos de actina, microtúbulos e filamentos intermediários",
                List.of("Actina, miosina e tubulina", "Filamentos de actina, microtúbulos e filamentos intermediários", "Microfilamentos, macrofilamentos e nanofilamentos", "Actina, colágeno e elastina")
        ));

        desafios.add(new ChallengeItem(19,
                "Qual componente do citoesqueleto é responsável por transmitir força mecânica e manter a forma da célula?",
                "Filamentos Intermediários",
                List.of("Filamentos de actina", "Microtúbulos", "Filamentos Intermediários", "Microfilamentos")
        ));

        desafios.add(new ChallengeItem(20,
                "Qual estrutura forma o fuso mitótico durante a divisão celular?",
                "Microtúbulos",
                List.of("Filamentos de actina", "Filamentos intermediários", "Microtúbulos", "Centríolos")
        ));

        desafios.add(new ChallengeItem(21,
                "As proteínas motoras cinesinas e dineínas transportam vesículas através de qual estrutura?",
                "Microtúbulos",
                List.of("Filamentos de actina", "Microtúbulos", "Filamentos intermediários", "Retículo Endoplasmático")
        ));

        // ============ NÚCLEO ============
        desafios.add(new ChallengeItem(22,
                "Qual estrutura abriga a maior parte do DNA celular?",
                "Núcleo",
                List.of("Mitocôndria", "Núcleo", "Ribossomo", "Cloroplasto")
        ));

        desafios.add(new ChallengeItem(23,
                "Onde ocorre a transcrição do DNA em RNA?",
                "Núcleo",
                List.of("Ribossomo", "Citoplasma", "Núcleo", "Complexo de Golgi")
        ));

        desafios.add(new ChallengeItem(24,
                "Qual estrutura é responsável pela formação dos ribossomos?",
                "Núcleo",
                List.of("Nucléolo", "Núcleo", "Retículo Endoplasmático", "Complexo de Golgi")
        ));

        // ============ JUNÇÕES CELULARES ============
        desafios.add(new ChallengeItem(25,
                "Qual tipo de junção funciona como barreira seletivamente permeável entre células epiteliais?",
                "Tight junctions (Junções estreitas)",
                List.of("Desmossomos", "Tight junctions (Junções estreitas)", "Junções comunicantes", "Plasmodesmos")
        ));

        desafios.add(new ChallengeItem(26,
                "Qual estrutura permite a troca citoplasmática entre células vegetais vizinhas?",
                "Plasmodesmos",
                List.of("Junções comunicantes", "Desmossomos", "Tight junctions", "Plasmodesmos")
        ));

        desafios.add(new ChallengeItem(27,
                "Qual junção liga células adjacentes garantindo que órgãos como pele e coração permaneçam conectados?",
                "Desmossomos",
                List.of("Tight junctions", "Junções comunicantes", "Desmossomos", "Plasmodesmos")
        ));

        desafios.add(new ChallengeItem(28,
                "Qual estrutura forma canais entre células animais permitindo transporte de íons e água?",
                "Junções comunicantes",
                List.of("Tight junctions", "Desmossomos", "Junções comunicantes", "Plasmodesmos")
        ));

        // ============ SINAPSE E JUNÇÃO NEUROMUSCULAR ============
        desafios.add(new ChallengeItem(29,
                "Qual estrutura permite a comunicação entre neurônios através de sinais químicos?",
                "Sinapse",
                List.of("Axônio", "Dendrito", "Sinapse", "Junção neuromuscular")
        ));

        desafios.add(new ChallengeItem(30,
                "Qual junção transmite impulsos nervosos de um neurônio motor para uma fibra muscular?",
                "Junção Neuromuscular",
                List.of("Sinapse", "Junção Neuromuscular", "Desmossomo", "Junção comunicante")
        ));

        // ============ SARCÔMERO ============
        desafios.add(new ChallengeItem(31,
                "Qual é a unidade contrátil básica do tecido muscular?",
                "Sarcômero",
                List.of("Miofibrilha", "Sarcômero", "Actina", "Miosina")
        ));

        desafios.add(new ChallengeItem(32,
                "O sarcômero converte energia química em trabalho mecânico utilizando qual molécula?",
                "ATP",
                List.of("ADP", "ATP", "GTP", "NADH")
        ));

        // ============ CLOROPLASTO ============
        desafios.add(new ChallengeItem(33,
                "Qual organela é exclusiva de células vegetais e realiza a fotossíntese?",
                "Cloroplasto",
                List.of("Mitocôndria", "Cloroplasto", "Vacúolo", "Peroxissomo")
        ));

        desafios.add(new ChallengeItem(34,
                "A fotossíntese converte energia luminosa em qual produto energético principal?",
                "Glicose",
                List.of("ATP", "Glicose", "Amido", "Oxigênio")
        ));

        desafios.add(new ChallengeItem(35,
                "Além da fotossíntese, o cloroplasto também é local para biossíntese de:",
                "Ácidos graxos e aminoácidos",
                List.of("Proteínas e lipídeos", "Ácidos graxos e aminoácidos", "DNA e RNA", "Carboidratos e vitaminas")
        ));

        // ============ VACÚOLO ============
        desafios.add(new ChallengeItem(36,
                "Qual estrutura é responsável pela manutenção da pressão de turgor em células vegetais?",
                "Vacúolo",
                List.of("Cloroplasto", "Parede Celular", "Vacúolo", "Citoplasma")
        ));

        desafios.add(new ChallengeItem(37,
                "Qual função NÃO é realizada pelo vacúolo?",
                "Síntese de ATP",
                List.of("Armazenamento de água", "Regulação do pH", "Síntese de ATP", "Digestão de macromoléculas")
        ));

        // ============ CÉLULA GUARDA ============
        desafios.add(new ChallengeItem(38,
                "Qual tipo de célula controla a abertura e fechamento dos estômatos nas plantas?",
                "Célula guarda",
                List.of("Célula parenquimática", "Célula guarda", "Célula epidérmica", "Tricoma")
        ));

        // ============ HEMIFUSOMA (DESCOBERTA RECENTE) ============
        desafios.add(new ChallengeItem(39,
                "Qual organela descoberta em 2025 pode ser responsável pela reciclagem de proteínas e membranas?",
                "Hemifusoma",
                List.of("Autofagossomo", "Hemifusoma", "Endossomo", "Fagossomo")
        ));

        // ============ QUESTÃO GERAL ============
        desafios.add(new ChallengeItem(40,
                "Qual processo celular programado está relacionado com a função dos lisossomos?",
                "Apoptose",
                List.of("Mitose", "Meiose", "Apoptose", "Citocinese")
        ));

        return desafios;
    }

    /**
     * Obtém um número específico de desafios aleatórios do banco de questões.
     * Embaralha a lista completa e retorna um subconjunto.
     *
     * @param quantidade Número de desafios a retornar (padrão: 10 para a Fase 2)
     * @return Lista contendo os desafios aleatórios selecionados
     */
    public List<ChallengeItem> obterDesafiosAleatorios(int quantidade) {
        List<ChallengeItem> desafios = carregarDesafios();
        Collections.shuffle(desafios);
        return new ArrayList<>(desafios.subList(0, Math.min(quantidade, desafios.size())));
    }

    /**
     * Obtém 10 desafios aleatórios para a Fase 2 do jogo.
     * Conveniência para iniciar a segunda fase com 10 questões embaralhadas.
     *
     * @return Lista contendo 10 desafios aleatórios
     */
    public List<ChallengeItem> obterDesafiosFase2() {
        return obterDesafiosAleatorios(10);
    }
}