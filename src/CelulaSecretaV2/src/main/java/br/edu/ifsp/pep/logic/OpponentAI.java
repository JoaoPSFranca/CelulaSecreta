package br.edu.ifsp.pep.logic;

import br.edu.ifsp.pep.model.Carta;
import br.edu.ifsp.pep.model.Pergunta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class OpponentAI implements Opponent {

    private List<Carta> cartasCandidatas;

    @Override
    public void iniciarNovaPartida(List<Carta> todasAsCartas) {
        this.cartasCandidatas = new ArrayList<>(todasAsCartas);
    }

    @Override
    public AcaoOponente decidirAcao(List<Pergunta> perguntasDisponiveis, List<Carta> cartasAtivasNoTabuleiro) {
        // Lógica de escolher pergunta
        List<Pergunta> perguntasUteis = new ArrayList<>();
        if (cartasCandidatas.size() > 1) { // Só faz sentido perguntar se há mais de 1 candidato
            for (Pergunta p : perguntasDisponiveis) {
                long countSim = this.cartasCandidatas.stream().filter(p::testar).count();
                if (countSim > 0 && countSim < this.cartasCandidatas.size()) {
                    perguntasUteis.add(p);
                }
            }
        }

        // Se encontrar perguntas úteis, sorteia uma e cria uma AÇÃO de pergunta.
        if (!perguntasUteis.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(perguntasUteis.size());
            return AcaoOponente.criarAcaoDePergunta(perguntasUteis.get(index));
        }

        // Se NÃO há perguntas úteis, ou se só resta 1 carta, partimos para o palpite.
        if (!cartasCandidatas.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(cartasCandidatas.size());
            Carta cartaParaPalpite = cartasCandidatas.get(index);
            return AcaoOponente.criarAcaoDePalpite(cartaParaPalpite);
        }

        // Caso de falha (não deveria acontecer), não faz nada.
        System.out.println("Falhou");
        return null;
    }

    @Override
    public void processarRespostaDoJogador(Pergunta pergunta, boolean resposta) {
        if (this.cartasCandidatas == null || this.cartasCandidatas.isEmpty()) {
            return;
        }

        this.cartasCandidatas = cartasCandidatas.stream()
                .filter(carta -> pergunta.testar(carta) == resposta)
                .collect(Collectors.toList());

        System.out.println("Oponente agora acha que restam " + cartasCandidatas.size() + " cartas candidatas.");
        cartasCandidatas.forEach(c -> System.out.println("- " + c.getNome())); // Para depuração
    }
}