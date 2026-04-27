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
    private static volatile boolean broadcasting = false;
    private static Thread broadcasterThread;
    private static Thread listenerThread;

    /**
     * Inicia um servidor que anuncia a sala na rede local
     * @param roomCode O código único da sala
     * @param hostIp O IP do host
     */
    public static void startBroadcastingRoom(String roomCode, String hostIp) {
        broadcasting = true;

        broadcasterThread = new Thread(() -> {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket();
                socket.setBroadcast(true);

                String message = roomCode + ":" + hostIp;
                byte[] data = message.getBytes();

                // Envia o anúncio a cada 2 segundos
                while (broadcasting && !Thread.currentThread().isInterrupted()) {
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
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        // Continua tentando
                    }
                }

                activeRooms.remove(roomCode);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
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

        listenerThread = new Thread(() -> {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(BROADCAST_PORT);
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
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();

        try {
            if (latch.await(10, TimeUnit.SECONDS)) {
                return result.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Garante que a thread será interrompida se o tempo expirar
            if (listenerThread != null && listenerThread.isAlive()) {
                listenerThread.interrupt();
            }
        }
        return null;
    }

    /**
     * Para o anúncio da sala
     */
    public static void stopBroadcastingRoom() {
        broadcasting = false;
        if (broadcasterThread != null && broadcasterThread.isAlive()) {
            broadcasterThread.interrupt();
            try {
                broadcasterThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Para o listener de descoberta de salas
     */
    public static void stopDiscovery() {
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
            try {
                listenerThread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Para o anúncio da sala
     * @param roomCode O código da sala
     */
    public static void stopBroadcasting(String roomCode) {
        activeRooms.remove(roomCode);
        stopBroadcastingRoom();
    }

    /**
     * Encerra todos os recursos do RoomServer
     */
    public static void shutdown() {
        System.out.println("Encerrando RoomServer...");
        broadcasting = false;
        activeRooms.clear();
        stopBroadcastingRoom();
        stopDiscovery();
        System.out.println("RoomServer encerrado.");
    }
}

