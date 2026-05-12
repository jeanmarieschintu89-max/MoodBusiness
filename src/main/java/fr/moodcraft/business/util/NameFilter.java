package fr.moodcraft.business.util;

import fr.moodcraft.business.Main;

import java.text.Normalizer;

import java.util.List;
import java.util.Locale;

public final class NameFilter {

    private NameFilter() {}

    public static String clean(
            String input
    ) {

        if (input == null) {
            return "";
        }

        return input
                .replaceAll("§.", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String validate(
            String name
    ) {

        int min =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "business.name.min-length",
                                3
                        );

        int max =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "business.name.max-length",
                                24
                        );

        if (name.length() < min) {

            return "Le nom est trop court. Minimum: §e"
                    + min
                    + " caractères§7.";
        }

        if (name.length() > max) {

            return "Le nom est trop long. Maximum: §e"
                    + max
                    + " caractères§7.";
        }

        String regex =
                Main.getInstance()
                        .getConfig()
                        .getString(
                                "business.name.allowed-regex",
                                "^[A-Za-z0-9_À-ÿ -]+$"
                        );

        if (!name.matches(regex)) {

            return "Le nom contient des caractères interdits.";
        }

        List<String> banned =
                Main.getInstance()
                        .getConfig()
                        .getStringList(
                                "business.name.banned-words"
                        );

        String lower =
                name.toLowerCase(Locale.ROOT);

        for (String word : banned) {

            if (word == null || word.isBlank()) {
                continue;
            }

            if (lower.contains(
                    word.toLowerCase(Locale.ROOT)
            )) {

                return "Ce nom contient un mot interdit.";
            }
        }

        return null;
    }

    public static String toId(
            String input
    ) {

        String clean =
                clean(input);

        String normalized =
                Normalizer.normalize(
                        clean,
                        Normalizer.Form.NFD
                ).replaceAll("\\p{M}", "");

        String id =
                normalized
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-+", "")
                        .replaceAll("-+$", "");

        if (id.isBlank()) {
            return "entreprise";
        }

        return id;
    }
}