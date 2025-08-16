package br.com.din.pixcraft.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscordWebhook {
    private final String webhookUrl;
    private String content;
    private String username;
    private String avatarUrl;
    private final List<Embed> embeds = new ArrayList<>();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public DiscordWebhook setContent(String content) {
        this.content = content;
        return this;
    }

    public DiscordWebhook setUsername(String username) {
        this.username = username;
        return this;
    }

    public DiscordWebhook setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public DiscordWebhook addEmbed(Embed embed) {
        this.embeds.add(embed);
        return this;
    }

    private JsonObject buildJson() {
        JsonObject json = new JsonObject();
        if (content != null) json.addProperty("content", content);
        if (username != null) json.addProperty("username", username);
        if (avatarUrl != null) json.addProperty("avatar_url", avatarUrl);

        if (!embeds.isEmpty()) {
            JsonArray embedArray = new JsonArray();
            for (Embed embed : embeds) {
                embedArray.add(embed.toJson());
            }
            json.add("embeds", embedArray);
        }
        return json;
    }

    public RequestBody buildRequestBody() {
        return RequestBody.create(JSON, buildJson().toString());
    }

    public void send() throws IOException {
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(buildRequestBody())
                .build();

        Call call = client.newCall(request);
        call.execute();
    }
}