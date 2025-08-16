package br.com.din.pixcraft.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Embed {
    private String title;
    private String description;
    private String url;
    private int color;
    private String footerText;
    private String footerIcon;
    private String thumbnail;
    private String image;
    private String authorName;
    private String authorUrl;
    private String authorIcon;
    private final List<EmbedField> fields = new ArrayList<>();

    public Embed setTitle(String title) {
        this.title = title;
        return this;
    }

    public Embed setDescription(String description) {
        this.description = description;
        return this;
    }

    public Embed setUrl(String url) {
        this.url = url;
        return this;
    }

    public Embed setColor(int color) {
        this.color = color;
        return this;
    }

    public Embed setFooter(String text, String icon) {
        this.footerText = text;
        this.footerIcon = icon;
        return this;
    }

    public Embed setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public Embed setImage(String image) {
        this.image = image;
        return this;
    }

    public Embed setAuthor(String name, String url, String icon) {
        this.authorName = name;
        this.authorUrl = url;
        this.authorIcon = icon;
        return this;
    }

    public Embed addField(String name, String value, boolean inline) {
        this.fields.add(new EmbedField(name, value, inline));
        return this;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (title != null) json.addProperty("title", title);
        if (description != null) json.addProperty("description", description);
        if (url != null) json.addProperty("url", url);
        if (color != 0) json.addProperty("color", color);

        if (footerText != null) {
            JsonObject footer = new JsonObject();
            footer.addProperty("text", footerText);
            if (footerIcon != null) footer.addProperty("icon_url", footerIcon);
            json.add("footer", footer);
        }

        if (thumbnail != null) {
            JsonObject thumb = new JsonObject();
            thumb.addProperty("url", thumbnail);
            json.add("thumbnail", thumb);
        }

        if (image != null) {
            JsonObject img = new JsonObject();
            img.addProperty("url", image);
            json.add("image", img);
        }

        if (authorName != null) {
            JsonObject author = new JsonObject();
            author.addProperty("name", authorName);
            if (authorUrl != null) author.addProperty("url", authorUrl);
            if (authorIcon != null) author.addProperty("icon_url", authorIcon);
            json.add("author", author);
        }

        if (!fields.isEmpty()) {
            JsonArray fieldArray = new JsonArray();
            for (EmbedField field : fields) {
                fieldArray.add(field.toJson());
            }
            json.add("fields", fieldArray);
        }

        return json;
    }

    public static class EmbedField {
        private final String name;
        private final String value;
        private final boolean inline;

        public EmbedField(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        private JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("name", name);
            json.addProperty("value", value);
            json.addProperty("inline", inline);
            return json;
        }
    }
}