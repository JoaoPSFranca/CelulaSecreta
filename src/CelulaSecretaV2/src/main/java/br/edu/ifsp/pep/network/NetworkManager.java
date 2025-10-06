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
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Consumer<Object> onMessageReceived;

    public void setOnMessageReceived(Consumer<Object> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    // Metodo para o Host: cria um servidor e espera o cliente
    public void hostGame() throws Exception {
        System.out.println("Modo Host: Aguardando conexão do cliente...");
        ServerSocket serverSocket = new ServerSocket(PORT);
        this.socket = serverSocket.accept(); // Linha bloqueante: espera a conexão
        System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());
        initializeStreams();
    }

    // Método para o Cliente: conecta a um servidor existente
    public void joinGame(String ipAddress) throws Exception {
        System.out.println("Modo Cliente: Conectando ao host " + ipAddress + "...");
        this.socket = new Socket(ipAddress, PORT);
        System.out.println("Conectado com sucesso!");
        initializeStreams();
    }

    private void initializeStreams() throws Exception {
        // Importante: O OutputStream deve ser criado PRIMEIRO
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    // Futuramente: métodos para enviar e receber objetos
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
                    while (socket.isConnected()) {
                        Object receivedObject = in.readObject();

                        // Usa o callback para notificar o MainController
                        // Platform.runLater garante que o código do callback rode na UI thread
                        if (onMessageReceived != null) {
                            Platform.runLater(() -> onMessageReceived.accept(receivedObject));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Conexão perdida.");
                    // Podería usar o callback para notificar a UI sobre a desconexão
                }
                return null;
            }
        };

        new Thread(listeningTask).start();
    }
}
