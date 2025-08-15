package br.com.din.pixcraft.shop.category;

import br.com.din.pixcraft.shop.Button;

import java.util.Map;

public class Category {
    private final String id;
    private final String title;
    private final int size;
    private final Map<Integer, Button> buttons;

    public Category(String id, String title, int size, Map<Integer, Button> buttons) {
        this.id = id;
        this.title = title;
        this.size = size;
        this.buttons = buttons;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public Map<Integer, Button> getButtons() {
        return buttons;
    }
}