package br.edu.ifsp.pep.bcc.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Componente personalizado para botões com cantos arredondados e aparência moderna
 */
public class RoundedButton extends JButton {
    private Color backgroundColor;
    private Color textColor;
    private Color hoverColor;
    private Color pressedColor;
    private int cornerRadius;
    
    /**
     * Construtor para criar um botão com cantos arredondados
     * @param text Texto do botão
     * @param width Largura do botão
     * @param height Altura do botão
     * @param bgColor Cor de fundo
     * @param textColor Cor do texto
     * @param cornerRadius Raio dos cantos arredondados
     */
    public RoundedButton(String text, int width, int height, Color bgColor, Color textColor, int cornerRadius) {
        super(text);
        
        this.backgroundColor = bgColor;
        this.textColor = textColor;
        this.hoverColor = bgColor.brighter();
        this.pressedColor = bgColor.darker();
        this.cornerRadius = cornerRadius;
        
        // Configurações básicas
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        
        // Define o tamanho do botão
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        
        // Define fonte
        setFont(new Font("Arial", Font.BOLD, 16));
        
        // Adiciona o listener para os efeitos de hover
        getModel().addChangeListener(e -> repaint());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Define a cor baseada no estado do botão
        if (getModel().isPressed()) {
            g2d.setColor(pressedColor);
        } else if (getModel().isRollover()) {
            g2d.setColor(hoverColor);
        } else {
            g2d.setColor(backgroundColor);
        }
        
        // Cria a forma do botão com cantos arredondados
        RoundRectangle2D.Float buttonShape = 
            new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        
        // Preenche o botão
        g2d.fill(buttonShape);
        
        // Adiciona uma borda sutil para melhorar a aparência
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1f));
        g2d.draw(buttonShape);
        
        // Desenha o texto
        g2d.setColor(textColor);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getHeight();
        
        g2d.drawString(getText(), 
                (getWidth() - textWidth) / 2, 
                (getHeight() + textHeight / 2) / 2 - 2);
        
        g2d.dispose();
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // Não desenha a borda padrão
    }
    
    @Override
    public boolean contains(int x, int y) {
        // Detecção de mouse sobre o formato do botão
        return new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius).contains(x, y);
    }
    
    // Getters e Setters personalizados
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        repaint();
    }
    
    public void setButtonTextColor(Color color) {
        this.textColor = color;
        repaint();
    }
    
    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }
}