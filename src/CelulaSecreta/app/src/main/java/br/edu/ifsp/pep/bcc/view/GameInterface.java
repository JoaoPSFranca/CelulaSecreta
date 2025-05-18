package br.edu.ifsp.pep.bcc.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class GameInterface extends JFrame {
    // Cores do tema
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color LIGHT_PURPLE = new Color(220, 208, 232);
    private static final Color DARK_PURPLE = new Color(90, 80, 112);
    private static final Color GRAY_BUTTON = new Color(180, 180, 180);
    
    // Tamanhos da tela
    private static final int WINDOW_WIDTH = 1366;
    private static final int WINDOW_HEIGHT = 768;
    
    // Chat components
    private JPanel chatPanel;
    private JTextField chatInputField;
    private JButton simButton, naoButton;
    private JPanel messagesPanel;
    private List<String> chatMessages = new ArrayList<>();
    
    // Game components
    private JPanel gamePanel;
    private GameCard[] centralCards = new GameCard[15];
    private GameCard yourCardPanel;
    private JTextField roomCodeField;
    private JButton guessButton, askQuestionButton;

    public GameInterface() {
        setTitle("Card Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Create the left chat panel
        createChatPanel();
        
        // Create the game panel (center and right side)
        createGamePanel();
        
        // Add panels to the main layout
        mainPanel.add(chatPanel, BorderLayout.WEST);
        mainPanel.add(gamePanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void createChatPanel() {
        chatPanel = new JPanel();
        chatPanel.setPreferredSize(new Dimension(350, WINDOW_HEIGHT));
        chatPanel.setBackground(BACKGROUND_COLOR);
        chatPanel.setLayout(new BorderLayout());
        
        // Chat messages area (scrollable)
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(BACKGROUND_COLOR);
        
        // Add sample messages
        addChatMessage("that looks so good!", true);
        addChatMessage("or we could make this?", false);
        addChatMessage("that looks so good!", true);
        addChatMessage("or we could make this?", false);
        addChatMessage("that looks so good!", true);
        addChatMessage("or we could make this?", false);
        addChatMessage("that looks so good!", true);
        addChatMessage("or we could make this?", false);
        addChatMessage("Tr", true);
        
        JScrollPane scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(BACKGROUND_COLOR);
        
        // Add input area with "Sim" and "Não" buttons
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(BACKGROUND_COLOR);
        
        // Input para chat
        chatInputField = new RoundedTextField(296, 41);
        chatInputField.setFont(new Font("Arial", Font.PLAIN, 14));
//        chatInputField.setBorderThickness(2);
        chatInputField.setHorizontalAlignment(JTextField.LEFT);
        chatInputField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        inputPanel.add(chatInputField);
        
        simButton = createRoundedButton("Sim", 140, 55, GRAY_BUTTON, Color.WHITE, 50);
        naoButton = createRoundedButton("Não", 140, 55, GRAY_BUTTON, Color.WHITE, 50);
        
        // Painel adicional para os botões Sim/Não
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.add(simButton);
        buttonsPanel.add(naoButton);
        
        // Painel para organizar campo de entrada e botões
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(inputPanel);
        bottomPanel.add(buttonsPanel);
        
        // Add components to the chat panel
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void addChatMessage(String text, boolean isLeftAligned) {
        JPanel messageWrapper = new JPanel();
        messageWrapper.setLayout(new FlowLayout(isLeftAligned ? FlowLayout.LEFT : FlowLayout.RIGHT));
        messageWrapper.setBackground(BACKGROUND_COLOR);
        
        JLabel message = new JLabel(text);
        message.setOpaque(true);
        message.setBackground(isLeftAligned ? LIGHT_PURPLE : DARK_PURPLE);
        message.setForeground(isLeftAligned ? Color.BLACK : Color.WHITE);
        message.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        // Set rounded corners for the message bubble
        message.putClientProperty("html.disable", Boolean.TRUE);
        
        messageWrapper.add(message);
        messagesPanel.add(messageWrapper);
        messagesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Add to chat history
        chatMessages.add(text);
        
        // Scroll to bottom of chat
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
    
    /**
     * Método público para adicionar mensagens ao chat
     * @param text Texto da mensagem
     * @param isLeftAligned Se a mensagem deve ser alinhada à esquerda
     */
    public void addMessageToChat(String text, boolean isLeftAligned) {
        addChatMessage(text, isLeftAligned);
    }
    
    private void createGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setBackground(BACKGROUND_COLOR);
        gamePanel.setLayout(new BorderLayout());
        
        // Central cards panel
        JPanel centralCardsPanel = new JPanel(new GridLayout(3, 5, 10, 10));
        centralCardsPanel.setBackground(BACKGROUND_COLOR);
        centralCardsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create the 15 central cards (3 rows x 5 columns)
        for (int i = 0; i < 15; i++) {
            centralCards[i] = new GameCard(120, 150);
            centralCards[i].setCardName("Carta " + (i+1));
            
            // Alternar entre face para cima e para baixo nas cartas centrais como exemplo
            if (i % 3 == 0) {
                centralCards[i].setFaceUp(false);
            }
            
            // Adicionar comportamento de clique nas cartas
            final int cardIndex = i;
            centralCards[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    // Exemplo de interação: virar a carta ao clicar
                    centralCards[cardIndex].flip();
                }
            });
            
            centralCardsPanel.add(centralCards[i]);
        }
        
        // Right panel with your card and controls
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(300, WINDOW_HEIGHT));
        rightPanel.setBackground(BACKGROUND_COLOR);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));
        
        // Your card panel
        JPanel yourCardContainer = new JPanel();
        yourCardContainer.setLayout(new BoxLayout(yourCardContainer, BoxLayout.Y_AXIS));
        yourCardContainer.setBackground(BACKGROUND_COLOR);
        yourCardContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel yourCardLabel = new JLabel("Sua Carta");
        yourCardLabel.setForeground(Color.WHITE);
        yourCardLabel.setFont(new Font("Arial", Font.BOLD, 16));
        yourCardLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        yourCardPanel = new GameCard(182, 228, "Jogador", null);
        yourCardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        yourCardContainer.add(yourCardLabel);
        yourCardContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        yourCardContainer.add(yourCardPanel);
        
        // Room code panel
        JPanel roomCodeContainer = new JPanel();
        roomCodeContainer.setLayout(new BoxLayout(roomCodeContainer, BoxLayout.Y_AXIS));
        roomCodeContainer.setBackground(BACKGROUND_COLOR);
        roomCodeContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        roomCodeContainer.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        JLabel roomCodeLabel = new JLabel("Código da Sala");
        roomCodeLabel.setForeground(Color.WHITE);
        roomCodeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        roomCodeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        roomCodeField = new RoundedTextField("123123123", 209, 64);
        roomCodeField.setMaximumSize(new Dimension(209, 64));
        roomCodeField.setHorizontalAlignment(JTextField.CENTER);
        roomCodeField.setFont(new Font("Arial", Font.PLAIN, 18));
        
        roomCodeContainer.add(roomCodeLabel);
        roomCodeContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        roomCodeContainer.add(roomCodeField);
        
        // Action buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(BACKGROUND_COLOR);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        
        guessButton = createRoundedButton("Palpitar", 259, 64, GRAY_BUTTON, Color.WHITE, 50);
        guessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        askQuestionButton = createRoundedButton("Fazer Pergunta", 259, 64, GRAY_BUTTON, Color.WHITE, 50);
        askQuestionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        buttonsPanel.add(guessButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(askQuestionButton);
        
        // Add components to the right panel
        rightPanel.add(yourCardContainer);
        rightPanel.add(roomCodeContainer);
        rightPanel.add(buttonsPanel);
        
        // Add components to the game panel
        gamePanel.add(centralCardsPanel, BorderLayout.CENTER);
        gamePanel.add(rightPanel, BorderLayout.EAST);
    }
    
    private JButton createRoundedButton(String text, int width, int height, Color bgColor, Color textColor, int radius) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(getModel().isPressed() ? bgColor.darker() : bgColor);
                g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
                
                g2d.setColor(textColor);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 2) / 2 - 2);
                
                g2d.dispose();
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // No border painting
            }
        };
        
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        
        return button;
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            GameInterface gameInterface = new GameInterface();
            gameInterface.setVisible(true);
        });
    }
    
    // Getters para acesso aos componentes
    public JButton getSimButton() {
        return simButton;
    }
    
    public JButton getNaoButton() {
        return naoButton;
    }
    
    public JButton getGuessButton() {
        return guessButton;
    }
    
    public JButton getAskQuestionButton() {
        return askQuestionButton;
    }
}