package br.edu.ifsp.pep.network;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    public static String getOutboundIPv4() {
        try (DatagramSocket socket = new DatagramSocket()) {
            // endereço público qualquer - não será enviado nada
            socket.connect(InetAddress.getByName("8.8.8.8"), 53);
            InetAddress local = socket.getLocalAddress();
            if (local instanceof Inet4Address) {
                String ip = local.getHostAddress();
                if (!ip.equals("0.0.0.0")) return ip;
            }
        } catch (Exception ignored) {
            // pode falhar se máquina não tiver rota para 8.8.8.8
        }
        return null;
    }

    public static String getBestLocalIPv4Fallback() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            List<String> candidates = new ArrayList<>();
            while (nets.hasMoreElements()) {
                NetworkInterface netIf = nets.nextElement();
                try {
                    if (!netIf.isUp() || netIf.isLoopback()) continue;
                } catch (Exception e) {
                    continue;
                }

                String name = netIf.getName().toLowerCase();
                String display = (netIf.getDisplayName() == null ? "" : netIf.getDisplayName().toLowerCase());

                // filtros por nome/descrição que normalmente indicam adaptadores virtuais/VM/docker/VPN
                if (name.contains("vbox") || name.contains("virtual") || name.contains("vmware")
                        || name.contains("docker") || name.contains("br-") || name.contains("veth")
                        || name.contains("tun") || name.contains("tap") || display.contains("virtual")
                        || display.contains("vmware") || display.contains("virtualbox") || display.contains("hyper-v")) {
                    continue;
                }

                Enumeration<InetAddress> addrs = netIf.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();
                        // prefira IPs privados (10., 172.16-31., 192.168.)
                        if (isPrivateIPv4(ip)) {
                            return ip; // melhor candidato imediato
                        } else {
                            candidates.add(ip);
                        }
                    }
                }
            }
            // se não encontrou privado, retorna primeiro candidato não-virtual
            if (!candidates.isEmpty()) return candidates.get(0);
        } catch (SocketException ignored) {}
        return null;
    }

    private static boolean isPrivateIPv4(String ip) {
        if (ip == null) return false;
        // checa faixas privadas
        if (ip.startsWith("10.")) return true;
        if (ip.startsWith("192.168.")) return true;
        // 172.16.0.0 — 172.31.255.255
        if (ip.startsWith("172.")) {
            String[] parts = ip.split("\\.");
            if (parts.length >= 2) {
                try {
                    int second = Integer.parseInt(parts[1]);
                    return second >= 16 && second <= 31;
                } catch (NumberFormatException ignored) {}
            }
        }
        return false;
    }

    public static String detectLocalIPv4() {
        String ip = getOutboundIPv4();
        if (ip != null && !ip.isEmpty()) return ip;

        ip = getBestLocalIPv4Fallback();
        if (ip != null && !ip.isEmpty()) return ip;

        return "127.0.0.1";
    }

    public static List<String> listAllLocalIPv4() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface netIf = nets.nextElement();
                try {
                    if (!netIf.isUp() || netIf.isLoopback()) continue;
                } catch (Exception e) { continue; }

                Enumeration<InetAddress> addrs = netIf.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        ips.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException ignored) {}
        return ips;
    }
}
