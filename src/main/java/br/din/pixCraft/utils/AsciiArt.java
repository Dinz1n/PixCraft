package br.din.pixCraft.utils;

import br.din.pixCraft.PixCraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class AsciiArt {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN_BRIGHT = "\u001B[92m";
    private static final String CYAN_BRIGHT = "\u001B[96m";
    private static final String RED_BRIGHT = "\u001B[91m";
    private static final String YELLOW = "\u001B[33m";

    public static void printAsciiArt(Logger logger) {
        String caminhoArquivo = "ascii/PixCraftAscii.txt";

        try (InputStream inputStream = PixCraft.class.getClassLoader().getResourceAsStream(caminhoArquivo);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new IOException("Arquivo não encontrado: " + caminhoArquivo);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(color(line));
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private static String color(String mensagem) {
        return mensagem
                .replace("&a", "\u001B[92m")  // Verde claro
                .replace("&b", "\u001B[96m")  // Ciano claro
                .replace("&c", "\u001B[91m")  // Vermelho claro
                .replace("&6", "\u001B[33m")  // Amarelo
                .replace("&r", "\u001B[0m");  // Reset (normal)
    }
}