package br.edu.ifsp.pep.network;

import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Gerencia as salas de jogo na rede local.
 * Quando um host cria uma sala, ele anuncia seu IP com um código específico.
 * Clientes podem descobrir o IP do host usando apenas o código.
 */
public class RoomServer {

    private static final int BROADCAST_PORT = 54322;
    private static volatile Map<String, String> activeRooms = new HashMap<>();

    /**
     * Inicia um servidor que anuncia a sala na rede local
     * @param roomCode O código único da sala
     * @param hostIp O IP do host
     */
    public static void startBroadcastingRoom(String roomCode, String hostIp) {
        Thread broadcasterThread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                String message = roomCode + ":" + hostIp;
                byte[] data = message.getBytes();

                // Envia o anúncio a cada 2 segundos
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
                        DatagramPacket packet = new DatagramPacket(
                            data,
                            data.length,
                            broadcastAddress,
                            BROADCAST_PORT
                        );
                        socket.send(packet);

                        activeRooms.put(roomCode, hostIp);

                        Thread.sleep(2000);
                    } catch (Exception e) {
                        // Continua tentando
                    }
                }

                socket.close();
                activeRooms.remove(roomCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        broadcasterThread.setDaemon(true);
        broadcasterThread.start();
    }

    /**
     * Procura por um host específico na rede local usando apenas o código
     * @param roomCode O código da sala
     * @return O IP do host, ou null se não encontrar
     */
    public static String discoverHostByCode(String roomCode) {
        Thread listenerThread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket(BROADCAST_PORT);
                socket.setBroadcast(true);
                socket.setSoTimeout(8000); // Aguarda 8 segundos

                byte[] buffer = new byte[256];

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);

                        String message = new String(packet.getData(), 0, packet.getLength());
                        String[] parts = message.split(":");

                        if (parts.length == 2) {
                            String code = parts[0];
                            String ip = parts[1];
                            activeRooms.put(code, ip);
                        }
                    } catch (SocketTimeoutException e) {
                        break;
                    }
                }

                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        listenerThread.start();

        // Aguarda a descoberta
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 10000) {
            if (activeRooms.containsKey(roomCode)) {
                return activeRooms.get(roomCode);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        try {
            listenerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return activeRooms.getOrDefault(roomCode, null);
    }

    /**
     * Para o anúncio da sala
     * @param roomCode O código da sala
     */
    public static void stopBroadcasting(String roomCode) {
        activeRooms.remove(roomCode);
    }
}

