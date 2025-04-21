package br.din.pixCraft.utils;

import okhttp3.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;

public class DiscordWebhook {
    private static final OkHttpClient client = new OkHttpClient();

    public static void sendEmbed(ConfigurationSection discordConfig, Player player, String product, double price, String date) {
        if (!discordConfig.getBoolean("notifications.enabled")) {
            return;
        }

        String url = discordConfig.getString("notifications.webhook-url");
        if (url == null || url.isEmpty()) {
            System.out.println("Webhook URL não configurado.");
            return;
        }

        String title = discordConfig.getString("notifications.embed.title", "Nova venda efetuada!")
                .replace("{player}", player.getName())
                .replace("{product}", product)
                .replace("{price}", String.format("%.2f", price))
                .replace("{date}", date);
        String description = discordConfig.getString("notifications.embed.description", "Comprador: {player}")
                .replace("{player}", player.getName())
                .replace("{product}", product)
                .replace("{price}", String.format("%.2f", price))
                .replace("{date}", date);
        String hexColor = discordConfig.getString("notifications.embed.color", "#1aff00");
        int color = Color.decode(hexColor).getRGB() & 0xFFFFFF;

        boolean showPlayerHead = discordConfig.getBoolean("notifications.embed.player-head-icon", true);
        String playerHeadUrl = showPlayerHead
                ? "https://mc-heads.net/avatar/" + player.getName()
                : "";

        boolean fieldEnabled = discordConfig.getBoolean("notifications.embed.field.enabled", true);
        String fieldName = discordConfig.getString("notifications.embed.field.title", "Produto: {product} | Valor pago: R${price}")
                .replace("{player}", player.getName())
                .replace("{product}", product)
                .replace("{price}", String.format("%.2f", price))
                .replace("{date}", date);
        String fieldValue = discordConfig.getString("notifications.embed.field.description", "Data da compra: {date}")
                .replace("{player}", player.getName())
                .replace("{product}", product)
                .replace("{price}", String.format("%.2f", price))
                .replace("{date}", date);

        String jsonPayload = "{\n" +
                "    \"embeds\": [\n" +
                "        {\n" +
                "            \"title\": \"" + title + "\",\n" +
                "            \"description\": \"" + description + "\",\n" +
                "            \"color\": " + color + ",\n" +
                "            \"thumbnail\": { \"url\": \"" + playerHeadUrl + "\" }\n" +
                "            " + (fieldEnabled ? ",\n" +
                "                \"fields\": [\n" +
                "                    {\n" +
                "                        \"name\": \"" + fieldName + "\",\n" +
                "                        \"value\": \"" + fieldValue + "\",\n" +
                "                        \"inline\": true\n" +
                "                    }\n" +
                "                ]\n" : "") +
                "        }\n" +
                "    ]\n" +
                "}";

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}