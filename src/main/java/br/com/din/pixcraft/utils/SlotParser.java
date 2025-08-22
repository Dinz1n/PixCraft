package br.com.din.pixcraft.utils;

import java.util.ArrayList;
import java.util.List;

public class SlotParser {

    /**
     * Converte uma string como "0-8,23,27" em uma lista de inteiros [0,1,2,3,4,5,6,7,8,23,27].
     *
     * @param input A string de slots no formato "X-Y,Z".
     * @return Lista de inteiros com todos os slots.
     */
    public static List<Integer> parseSlots(String input) {
        List<Integer> slots = new ArrayList<>();

        if (input == null || input.isEmpty()) {
            return slots;
        }

        for (String part : input.split(",")) {
            part = part.trim();
            if (part.isEmpty()) continue;

            if (part.contains("-")) {
                String[] range = part.split("-");
                try {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    if (start > end) {
                        int temp = start;
                        start = end;
                        end = temp;
                    }
                    for (int i = start; i <= end; i++) {
                        slots.add(i);
                    }
                } catch (NumberFormatException ignored) {}
            } else {
                try {
                    slots.add(Integer.parseInt(part));
                } catch (NumberFormatException ignored) {}
            }
        }
        return slots;
    }
}
