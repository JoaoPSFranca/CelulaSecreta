package br.edu.ifsp.pep.network;

import java.util.Random;

/**
 * Gera códigos de sala únicos e fáceis de lembrar.
 * Formato: 6 caracteres alfanuméricos (A-Z, 0-9)
 * Exemplos: AB3X7K, Z9M2Q5, K7N1P8
 */
public class RoomCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random random = new Random();

    /**
     * Gera um código de sala aleatório
     * @return Um código de 6 caracteres (ex: "AB3X7K")
     */
    public static String generateRoomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}

