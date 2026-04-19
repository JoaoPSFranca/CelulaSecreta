package br.edu.ifsp.pep.network;

import java.net.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Gerencia as salas de jogo na rede local.
 * Quando um host cria uma sala, ele anuncia seu IP com um código específico.
 * Clientes podem descobrir o IP do host usando apenas o código.
 */
public class RoomServer {

    private static final int BROADCAST_PORT = 54322;
    private static final Map<String, String> activeRooms = new ConcurrentHashMap<>();
    private static volatile boolean broadcasting = true;
    private static Thread broadcasterThread;

    /**
     * Inicia um servidor que anuncia a sala na rede local
     * @param roomCode O código único da sala
     * @param hostIp O IP do host
     */
    public static void startBroadcastingRoom(String roomCode, String hostIp) {
        broadcasterThread = new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                String message = roomCode + ":" + hostIp;
                byte[] data = message.getBytes();

                // Envia o anúncio a cada 2 segundos
                while (broadcasting) {
                    try {
                        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                        NetworkInterface networkInterface = null;

                        while (interfaces.hasMoreElements()) {
                            networkInterface = interfaces.nextElement();

                            if (!networkInterface.isUp() || networkInterface.isLoopback()) continue;

                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                InetAddress broadcast = interfaceAddress.getBroadcast();
                                if (broadcast == null) continue;

                                DatagramPacket packet = new DatagramPacket(
                                    data,
                                    data.length,
                                    broadcast,
                                    BROADCAST_PORT
                                );

                                socket.send(packet);
                            }
                        }

                        activeRooms.put(roomCode, hostIp);
                        Thread.sleep(2000);
                        stopBroadcastingRoom();
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
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> result = new AtomicReference<>();

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

                        if (parts.length == 2 && parts[0].matches("[A-Z0-9]{6}")) {
                            String code = parts[0];
                            String ip = parts[1];

                            activeRooms.put(code, ip);

                            if (code.equals(roomCode)) {
                                result.set(ip);
                                latch.countDown();
                            }
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

        try {
            if (latch.await(10, TimeUnit.SECONDS)) {
                return result.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
        
    }

    public static void stopBroadcastingRoom() {
        if (broadcasterThread != null && broadcasterThread.isAlive()) {
            broadcasterThread.interrupt();
        }
    }

    /**
     * Para o anúncio da sala
     * @param roomCode O código da sala
     */
    public static void stopBroadcasting(String roomCode) {
        activeRooms.remove(roomCode);
    }
}

