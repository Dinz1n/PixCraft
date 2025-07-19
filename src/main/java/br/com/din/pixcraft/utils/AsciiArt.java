package br.com.din.pixcraft.utils;

import br.com.din.pixcraft.PixCraft;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class AsciiArt {
    public static void printAsciiArt(Logger logger) {
        String caminhoArquivo = "pixcraft-ascii-art.txt";

        try (InputStream inputStream = PixCraft.class.getClassLoader().getResourceAsStream(caminhoArquivo);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new IOException("Arquivo não encontrado: " + caminhoArquivo);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                logger.info(color(line.replace("{version}", PixCraft.getInstance().getDescription().getVersion())));
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    private static String color(String mensagem) {
        return mensagem
                .replace("&a", "\u001B[32m")  // Verde
                .replace("&b", "\u001B[36m")  // Ciano
                .replace("&c", "\u001B[31m")  // Vermelho
                .replace("&6", "\u001B[33m")  // Amarelo
                .replace("&r", "\u001B[0m");  // Reset
    }
}