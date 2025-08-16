package br.com.din.pixcraft.discord;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

public class WebhookConfigLoader {
    public static DiscordWebhook fromConfig(String webhookUrl, ConfigurationSection section, Map<String, String> placeholders) {
        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

        if (section.contains("content")) {
            webhook.setContent(applyPlaceholders(section.getString("content"), placeholders));
        }

        if (section.contains("username")) {
            webhook.setUsername(applyPlaceholders(section.getString("username"), placeholders));
        }

        if (section.contains("avatar_url")) {
            String avatarRaw = section.getString("avatar_url");
            String resolved = AvatarResolver.resolve(applyPlaceholders(avatarRaw, placeholders),
                    placeholders.getOrDefault("player_name", "Steve"));
            webhook.setAvatarUrl(resolved);
        }

        if (section.isList("embeds")) {
            List<?> embeds = section.getList("embeds");
            for (Object obj : embeds) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> embedMap = (Map<String, Object>) obj;
                    webhook.addEmbed(parseEmbed(embedMap, placeholders));
                }
            }
        }

        return webhook;
    }

    private static Embed parseEmbed(Map<String, Object> map, Map<String, String> placeholders) {
        Embed embed = new Embed();

        if (map.containsKey("title")) embed.setTitle(applyPlaceholders(map.get("title").toString(), placeholders));
        if (map.containsKey("description")) embed.setDescription(applyPlaceholders(map.get("description").toString(), placeholders));
        if (map.containsKey("url")) embed.setUrl(map.get("url").toString());
        if (map.containsKey("color")) embed.setColor((Integer) map.get("color"));

        if (map.containsKey("footer")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> footer = (Map<String, Object>) map.get("footer");
            embed.setFooter(
                    applyPlaceholders((String) footer.get("text"), placeholders),
                    (String) footer.get("icon_url")
            );
        }

        if (map.containsKey("thumbnail")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> thumb = (Map<String, Object>) map.get("thumbnail");
            String thumbUrl = (String) thumb.get("url");
            if (thumbUrl != null) {
                String resolvedThumb = AvatarResolver.resolve(applyPlaceholders(thumbUrl, placeholders),
                        placeholders.getOrDefault("player_name", "Steve"));
                embed.setThumbnail(resolvedThumb);
            }
        }

        if (map.containsKey("image")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> img = (Map<String, Object>) map.get("image");
            if (img.get("url") != null) {
                embed.setImage(applyPlaceholders((String) img.get("url"), placeholders));
            }
        }

        if (map.containsKey("author")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> author = (Map<String, Object>) map.get("author");
            embed.setAuthor(
                    applyPlaceholders((String) author.get("name"), placeholders),
                    (String) author.get("url"),
                    (String) author.get("icon_url")
            );
        }

        if (map.containsKey("fields")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> fields = (List<Map<String, Object>>) map.get("fields");
            for (Map<String, Object> f : fields) {
                embed.addField(
                        applyPlaceholders((String) f.get("name"), placeholders),
                        applyPlaceholders((String) f.get("value"), placeholders),
                        f.containsKey("inline") && (Boolean) f.get("inline")
                );
            }
        }
        return embed;
    }

    private static String applyPlaceholders(String text, Map<String, String> placeholders) {
        if (text == null) return null;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    private static class AvatarResolver {
        public static String resolve(String avatarConfig, String playerName) {
            if (avatarConfig == null) return null;

            if (avatarConfig.startsWith("player:")) {
                String nick = avatarConfig.replace("player:", "");
                if (nick.contains("{player_name}")) {
                    nick = nick.replace("{player_name}", playerName);
                }
                return "https://minotar.net/avatar/" + nick + "/128.png";
            }

            return avatarConfig;
        }
    }
}