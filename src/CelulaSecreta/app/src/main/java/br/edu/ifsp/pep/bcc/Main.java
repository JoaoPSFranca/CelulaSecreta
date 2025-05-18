package br.edu.ifsp.pep.bcc;

import br.edu.ifsp.pep.bcc.view.GameInterface;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Configura o look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Inicia a interface gráfica no Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            GameInterface gameInterface = new GameInterface();
            gameInterface.setVisible(true);
            
            // Adiciona alguns ouvintes de eventos para demonstração
            setupEventListeners(gameInterface);
        });
    }
    
    /**
     * Configura ouvintes de eventos para os componentes da interface
     * @param game A interface do jogo
     */
    private static void setupEventListeners(GameInterface game) {
        // Exemplo de como você pode adicionar funcionalidades aos botões
        game.getGuessButton().addActionListener(e -> {
            JOptionPane.showMessageDialog(game, 
                "Funcionalidade de palpite será implementada aqui!", 
                "Palpitar", JOptionPane.INFORMATION_MESSAGE);
        });
        
        game.getAskQuestionButton().addActionListener(e -> {
            String question = JOptionPane.showInputDialog(game,
                "Digite sua pergunta:",
                "Fazer Pergunta",
                JOptionPane.QUESTION_MESSAGE);
                
            if (question != null && !question.trim().isEmpty()) {
                game.addMessageToChat(question, true);
                
                // Simulação de resposta
                Timer timer = new Timer(1000, event -> {
                    game.addMessageToChat("Resposta para: " + question, false);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        
        game.getSimButton().addActionListener(e -> {
            game.addMessageToChat("Sim, concordo!", true);
            
            // Simulação de resposta
            Timer timer = new Timer(1000, event -> {
                game.addMessageToChat("Ótimo! Vamos continuar.", false);
            });
            timer.setRepeats(false);
            timer.start();
        });
        
        game.getNaoButton().addActionListener(e -> {
            game.addMessageToChat("Não, discordo!", true);
            
            // Simulação de resposta
            Timer timer = new Timer(1000, event -> {
                game.addMessageToChat("Ok, vamos tentar outra coisa então.", false);
            });
            timer.setRepeats(false);
            timer.start();
        });
        
        game.getSimButton().addActionListener(e -> {
            game.addMessageToChat("Sim, concordo!", true);
        });
        
        game.getNaoButton().addActionListener(e -> {
            game.addMessageToChat("Não, discordo!", true);
        });
    }
}
