package br.edu.ifsp.pep.bcc.view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel de chat melhorado que utiliza a classe ChatBubble
 * para exibir mensagens no formato de bolhas de chat
 */
public class ImprovedChatPanel extends JPanel {
    // Cores do tema
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color LIGHT_PURPLE = new Color(220, 208, 232);
    private static final Color DARK_PURPLE = new Color(90, 80, 112);
    private static final Color GRAY_BUTTON = new Color(180, 180, 180);
    
    // Componentes do chat
    private RoundedTextField chatInputField;
    private JButton simButton, naoButton;
    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private List<String> chatMessages = new ArrayList<>();
    
    /**
     * Construtor para o painel de chat melhorado
     */
    public ImprovedChatPanel() {
        setPreferredSize(new Dimension(350, 768)); // Altura da janela principal
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());
        
        // Inicializa os componentes
        initComponents();
    }
    
    /**
     * Inicializa os componentes do painel de chat
     */
    private void initComponents() {
        // Área de mensagens (rolável)
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(BACKGROUND_COLOR);
        messagesPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Adiciona espaçamento uniforme
        
        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        // Configura a barra de rolagem para ter uma aparência mais moderna
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Rolagem mais suave
        
        // Painel para organizar os componentes de entrada
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        
        // Painel para o campo de texto
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        // Campo de entrada de texto
        chatInputField = new RoundedTextField(296, 41);
        chatInputField.setFont(new Font("Arial", Font.PLAIN, 14));
        chatInputField.setHorizontalAlignment(JTextField.LEFT);
        chatInputField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 10));
        
        // Adiciona comportamento de envio quando Enter é pressionado
        chatInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        inputPanel.add(chatInputField);
        
        // Painel para os botões Sim/Não
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        
        // Cria botões arredondados com a nova aparência
        simButton = createRoundedButton("Sim", 140, 45, GRAY_BUTTON, Color.WHITE);
        naoButton = createRoundedButton("Não", 140, 45, GRAY_BUTTON, Color.WHITE);
        
        // Adiciona comportamentos aos botões
        simButton.addActionListener(e -> {
            addChatMessage("Sim", false);
            scrollToBottom();
        });
        
        naoButton.addActionListener(e -> {
            addChatMessage("Não", false);
            scrollToBottom();
        });
        
        // Adiciona botões ao painel
        buttonsPanel.add(simButton);
        buttonsPanel.add(naoButton);
        
        // Adiciona os painéis ao painel inferior
        bottomPanel.add(inputPanel);
        bottomPanel.add(buttonsPanel);
        
        // Adiciona os componentes ao painel principal
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Envia a mensagem do campo de texto
     */
    private void sendMessage() {
        String text = chatInputField.getText().trim();
        if (!text.isEmpty()) {
            addChatMessage(text, false);
            chatInputField.setText("");
            scrollToBottom();
        }
    }
    
    /**
     * Adiciona uma mensagem ao chat
     * @param text Texto da mensagem
     * @param isLeftAligned Se a mensagem deve ser alinhada à esquerda (mensagem recebida)
     */
    public void addChatMessage(String text, boolean isLeftAligned) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setLayout(new FlowLayout(isLeftAligned ? FlowLayout.LEFT : FlowLayout.RIGHT, 0, 0));
        messageWrapper.setBackground(BACKGROUND_COLOR);
        
        // Usa ChatBubble personalizado
        ChatBubble chatBubble = new ChatBubble(text, isLeftAligned);
        messageWrapper.add(chatBubble);
        
        messagesPanel.add(messageWrapper);
        messagesPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Espaçamento consistente entre mensagens
        
        // Adiciona ao histórico de chat
        chatMessages.add(text);
        
        // Atualiza o painel
        messagesPanel.revalidate();
        messagesPanel.repaint();
        
        // Rola para a nova mensagem
        scrollToBottom();
    }
    
    /**
     * Rola para o final do chat para mostrar a mensagem mais recente
     */
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    /**
     * Cria um botão com cantos arredondados com aparência moderna
     */
    private JButton createRoundedButton(String text, int width, int height, Color bgColor, Color textColor) {
        JButton button = new JButton(text) {
            // Raio de arredondamento mais suave para combinar com o protótipo
            private final int radius = 25;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Efeito hover e pressionado
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                // Desenha o botão com cantos completamente arredondados
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                
                // Desenha o texto
                g2d.setColor(textColor);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                
                // Centraliza o texto
                g2d.drawString(getText(), 
                        (getWidth() - textWidth) / 2, 
                        (getHeight() + textHeight / 2) / 2 - 2);
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Sem borda
            }
            
            // Forma para detecção de cliques
            @Override
            public boolean contains(int x, int y) {
                return new java.awt.geom.RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), radius, radius).contains(x, y);
            }
        };
        
        // Configurações do botão
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        
        // Adiciona efeito de hover
        button.getModel().addChangeListener(e -> button.repaint());
        
        return button;
    }
    
    // Getters para acesso aos componentes
    public JButton getSimButton() {
        return simButton;
    }
    
    public JButton getNaoButton() {
        return naoButton;
    }
    
    public RoundedTextField getChatInputField() {
        return chatInputField;
    }
    
    /**
     * Classe interna para criar uma barra de rolagem moderna
     */
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private final int THUMB_SIZE = 8;
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            // Fundo transparente
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Cor da barra
            g2.setColor(new Color(160, 160, 160, 120));
            
            // Centraliza a barra
            int x = thumbBounds.x + (thumbBounds.width - THUMB_SIZE) / 2;
            
            // Desenha barra arredondada
            g2.fillRoundRect(x, thumbBounds.y, THUMB_SIZE, thumbBounds.height, THUMB_SIZE, THUMB_SIZE);
            
            g2.dispose();
        }
    }
}