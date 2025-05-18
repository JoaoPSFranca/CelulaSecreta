package br.edu.ifsp.pep.bcc.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente personalizado para mensagens de chat com visuais melhorados
 */
public class ChatBubble extends JPanel {
    private static final Color LIGHT_PURPLE = new Color(220, 208, 232);
    private static final Color DARK_PURPLE = new Color(90, 80, 112);
    private static final int CORNER_RADIUS = 15;
    
    private String message;
    private boolean isLeftAligned;
    private final Font messageFont = new Font("Arial", Font.PLAIN, 14);
    
    /**
     * Construtor para criar uma bolha de chat
     * @param message Texto da mensagem
     * @param isLeftAligned Se a mensagem deve ser alinhada à esquerda
     */
    public ChatBubble(String message, boolean isLeftAligned) {
        this.message = message;
        this.isLeftAligned = isLeftAligned;
        
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
        
        // Ajusta automaticamente o tamanho baseado no conteúdo
        adjustSize();
    }
    
    /**
     * Ajusta o tamanho do componente baseado no conteúdo da mensagem
     */
    private void adjustSize() {
        // Cria um FontMetrics temporário para calcular o tamanho do texto
        FontMetrics fm = getFontMetrics(messageFont);
        
        // Calcula a largura do texto com padding
        int textWidth = fm.stringWidth(message) + 30;
        
        // Limita a largura máxima da bolha
        int bubbleWidth = Math.min(textWidth, 250);
        
        // Calcula a altura necessária baseada na largura disponível
        int lines = (int) Math.ceil((double) textWidth / bubbleWidth);
        int bubbleHeight = fm.getHeight() * lines + 20;
        
        // Define o tamanho do componente
        setPreferredSize(new Dimension(bubbleWidth, bubbleHeight));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Configurações de alta qualidade para renderização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Define a cor baseada no alinhamento
        Color bubbleColor = isLeftAligned ? LIGHT_PURPLE : DARK_PURPLE;
        g2d.setColor(bubbleColor);
        
        // Forma da bolha com cantos arredondados
        RoundRectangle2D.Float bubble = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        
        // Preenche e desenha a bolha
        g2d.fill(bubble);
        
        // Configura o texto
        g2d.setColor(isLeftAligned ? Color.BLACK : Color.WHITE);
        g2d.setFont(messageFont);
        
        // Calcula posição e desenha o texto
        FontMetrics fm = g2d.getFontMetrics();
        
        // Gerencia quebras de linha para textos longos
        int textX = 10;
        int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        
        // Para textos longos que precisam ser quebrados
        if (fm.stringWidth(message) > getWidth() - 20) {
            drawMultilineText(g2d, fm);
        } else {
            g2d.drawString(message, textX, textY);
        }
        
        g2d.dispose();
    }
    
    /**
     * Desenha texto com múltiplas linhas
     */
    private void drawMultilineText(Graphics2D g2d, FontMetrics fm) {
        int availableWidth = getWidth() - 20;
        int x = 10;
        int y = 15 + fm.getAscent();
        
        // Algoritmo simples de quebra de linha
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.toString() + word + " ";
            int testWidth = fm.stringWidth(testLine.trim());
            
            if (testWidth > availableWidth) {
                // Desenha a linha atual e começa uma nova
                g2d.drawString(currentLine.toString().trim(), x, y);
                y += fm.getHeight();
                currentLine = new StringBuilder(word + " ");
            } else {
                // Adiciona a palavra à linha atual
                currentLine.append(word).append(" ");
            }
        }
        
        // Desenha a última linha
        if (currentLine.length() > 0) {
            g2d.drawString(currentLine.toString().trim(), x, y);
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        // Se o tamanho não foi ainda calculado, faz isso agora
        if (super.getPreferredSize().width == 0) {
            adjustSize();
        }
        return super.getPreferredSize();
    }
    
    // Getters e Setters
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
        adjustSize(); // Recalcula o tamanho
        repaint();
    }
    
    public boolean isLeftAligned() {
        return isLeftAligned;
    }
    
    public void setLeftAligned(boolean leftAligned) {
        this.isLeftAligned = leftAligned;
        repaint();
    }
}