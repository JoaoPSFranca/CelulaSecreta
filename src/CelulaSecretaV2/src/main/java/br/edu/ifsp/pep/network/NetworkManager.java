package br.edu.ifsp.pep.network;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.function.Consumer;

public class NetworkManager {
    private static final int PORT = 54321; // Porta de comunicação

    private Socket socket;
    private ServerSocket serverSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread listeningThread;
    private volatile boolean isConnected = false;

    private Consumer<Object> onMessageReceived;

    public void setOnMessageReceived(Consumer<Object> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    // Metodo para o Host: cria um servidor e espera o cliente
    public void hostGame() throws Exception {
        System.out.println("Modo Host: Aguardando conexão do cliente...");
        serverSocket = new ServerSocket(PORT);
        this.socket = serverSocket.accept(); // Linha bloqueante: espera a conexão
        System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());
        isConnected = true;
        initializeStreams();
    }

    // Metodo para o Cliente: conecta a um servidor existente
    public void joinGame(String ipAddress) throws Exception {
        System.out.println("Modo Cliente: Conectando ao host " + ipAddress + "...");
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(ipAddress, PORT), 5000);
        System.out.println("Conectado com sucesso!");
        isConnected = true;
        initializeStreams();
    }

    private void initializeStreams() throws Exception {
        // Importante: O OutputStream deve ser criado PRIMEIRO
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Object object) throws Exception {
        if (out != null) {
            out.writeObject(object);
            out.flush();
        }
    }

    public void startListening() {
        Task<Void> listeningTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (in == null) return null;

                try {
                    while (isConnected && socket != null && socket.isConnected()) {
                        Object receivedObject = in.readObject();

                        // Usa o callback para notificar o MainController
                        // Platform.runLater garante que o código do callback rode na UI thread
                        if (onMessageReceived != null) {
                            Platform.runLater(() -> onMessageReceived.accept(receivedObject));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Conexão perdida: " + e.getMessage());
                    // Podería usar o callback para notificar a UI sobre a desconexão
                } finally {
                    isConnected = false;
                }
                return null;
            }
        };

        listeningThread = new Thread(listeningTask);
        listeningThread.setDaemon(true);
        listeningThread.start();
    }

    /**
     * Encerra a conexão de rede e fecha todos os recursos
     */
    public void shutdown() {
        System.out.println("Encerrando conexão de rede...");
        isConnected = false;

        // Fechar thread de escuta
        if (listeningThread != null && listeningThread.isAlive()) {
            listeningThread.interrupt();
            try {
                listeningThread.join(2000); // Aguarda até 2 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Fechar streams
        try {
            if (in != null) in.close();
        } catch (Exception e) {
            System.err.println("Erro ao fechar InputStream: " + e.getMessage());
        }

        try {
            if (out != null) out.close();
        } catch (Exception e) {
            System.err.println("Erro ao fechar OutputStream: " + e.getMessage());
        }

        // Fechar socket cliente
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar Socket: " + e.getMessage());
        }

        // Fechar server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (Exception e) {
            System.err.println("Erro ao fechar ServerSocket: " + e.getMessage());
        }

        System.out.println("Conexão de rede encerrada.");
    }

    /**
     * Verifica se está conectado
     */
    public boolean isConnected() {
        return isConnected && socket != null && socket.isConnected();
    }
}
