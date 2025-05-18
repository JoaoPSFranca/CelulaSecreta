package br.edu.ifsp.pep.bcc.view;

import javax.swing.*;
import java.awt.*;

/**
 * Classe para representar um cartão de jogo
 */
public class GameCard extends JPanel {
    private String cardName;
    private ImageIcon cardImage;
    private boolean isSelected;
    private boolean isFaceUp;
    
    /**
     * Construtor para criar um cartão
     * @param width Largura do cartão
     * @param height Altura do cartão
     */
    public GameCard(int width, int height) {
        this(width, height, null, null);
    }
    
    /**
     * Construtor para criar um cartão com imagem e nome
     * @param width Largura do cartão
     * @param height Altura do cartão
     * @param name Nome do cartão
     * @param image Imagem do cartão
     */
    public GameCard(int width, int height, String name, ImageIcon image) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setLayout(new BorderLayout());
        
        this.cardName = name;
        this.cardImage = image;
        this.isSelected = false;
        this.isFaceUp = true;
        
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenha o fundo do cartão
        if (isFaceUp) {
            // Carta virada para cima
            g2d.setColor(new Color(190, 190, 210));
            
            if (isSelected) {
                // Adiciona um destaque se estiver selecionado
                g2d.setColor(new Color(160, 160, 220));
            }
            
            // Desenha o cartão arredondado
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            
            // Se existir uma imagem, desenha-a
            if (cardImage != null) {
                int imgWidth = cardImage.getIconWidth();
                int imgHeight = cardImage.getIconHeight();
                
                // Centraliza a imagem
                int x = (getWidth() - imgWidth) / 2;
                int y = (getHeight() - imgHeight) / 2 - 10; // Ajuste para o texto
                
                g2d.drawImage(cardImage.getImage(), x, y, null);
            }
            
            // Se existir um nome, desenha-o
            if (cardName != null) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(cardName);
                
                g2d.drawString(cardName, 
                        (getWidth() - textWidth) / 2, 
                        getHeight() - 10);
            }
        } else {
            // Carta virada para baixo (verso)
            g2d.setColor(new Color(80, 80, 160));
            g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            
            // Desenho de padrão para o verso da carta
            g2d.setColor(new Color(60, 60, 120));
            g2d.fillOval(getWidth()/4, getHeight()/4, 
                    getWidth()/2, getHeight()/2);
        }
        
        // Borda do cartão
        g2d.setColor(new Color(100, 100, 150));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        
        g2d.dispose();
    }
    
    // Getters e Setters
    
    public String getCardName() {
        return cardName;
    }
    
    public void setCardName(String cardName) {
        this.cardName = cardName;
        repaint();
    }
    
    public ImageIcon getCardImage() {
        return cardImage;
    }
    
    public void setCardImage(ImageIcon cardImage) {
        this.cardImage = cardImage;
        repaint();
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }
    
    public boolean isFaceUp() {
        return isFaceUp;
    }
    
    public void setFaceUp(boolean faceUp) {
        this.isFaceUp = faceUp;
        repaint();
    }
    
    /**
     * Vira a carta (alterna entre face para cima e para baixo)
     */
    public void flip() {
        this.isFaceUp = !this.isFaceUp;
        repaint();
    }
}
