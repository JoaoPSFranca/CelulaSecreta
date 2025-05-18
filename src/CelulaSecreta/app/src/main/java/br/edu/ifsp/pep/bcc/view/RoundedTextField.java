package br.edu.ifsp.pep.bcc.view;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionListener; 

/**
 * Componente personalizado para entrada de texto com cantos arredondados
 */
public class RoundedTextField extends JTextField {
    private static final int CORNER_RADIUS = 8;
    private Color borderColor = new Color(190, 190, 210);
    private int borderThickness = 1;
    
    /**
     * Construtor para criar um campo de texto com cantos arredondados
     * @param width Largura do campo
     * @param height Altura do campo
     */
    public RoundedTextField(int width, int height) {
        this("", width, height);
    }
    
    /**
     * Construtor para criar um campo de texto com texto inicial e cantos arredondados
     * @param text Texto inicial
     * @param width Largura do campo
     * @param height Altura do campo
     */
    public RoundedTextField(String text, int width, int height) {
        super(text);
        
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        
        // Centraliza o texto
        setHorizontalAlignment(JTextField.CENTER);
        setFont(new Font("Arial", Font.PLAIN, 16));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Pinta o fundo
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
        
        // Desenha a borda
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(borderThickness));
        g2d.drawRoundRect(borderThickness / 2, borderThickness / 2, 
                getWidth() - borderThickness, getHeight() - borderThickness, 
                CORNER_RADIUS, CORNER_RADIUS);
        
        g2d.dispose();
        
        // Chama a implementação padrão para desenhar o texto
        super.paintComponent(g);
    }
    
    @Override
    protected void paintBorder(Graphics g) {
        // Não desenha a borda padrão
    }
    
    /**
     * Define a cor da borda
     * @param color Cor da borda
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    /**
     * Define a espessura da borda
     * @param thickness Espessura da borda
     */
    public void setBorderThickness(int thickness) {
        this.borderThickness = thickness;
        repaint();
    }
}