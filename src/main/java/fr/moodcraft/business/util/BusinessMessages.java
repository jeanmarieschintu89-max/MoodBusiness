package fr.moodcraft.business.util;

import fr.moodcraft.business.model.Business;

import org.bukkit.command.CommandSender;

public final class BusinessMessages {

    public static final String FOOTER = "§8----------- §6✦ §8-----------";

    private BusinessMessages() {}

    public static String brand() {
        return "§aMood§6Craft";
    }

    public static String guiTitle(String title) {
        return "§6✦ §8§l" + cleanTitle(title) + " §6✦";
    }

    public static void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + cleanTitle(title) + " ✦ §8-----");
    }

    public static void footer(CommandSender sender) {
        sender.sendMessage(FOOTER);
    }

    public static void infoLine(CommandSender sender, String message) {
        sender.sendMessage("§e➜ §f" + cleanPrefix(message));
    }

    public static void line(CommandSender sender, String message) {
        sender.sendMessage("§8• §7" + cleanPrefix(message));
    }

    public static void deny(CommandSender sender, String title, String reason, String... details) {
        header(sender, title);
        sender.sendMessage("§c✖ §fAction refusée.");
        line(sender, reason);
        sendDetails(sender, details);
        footer(sender);
    }

    public static void success(CommandSender sender, String title, String message, String... details) {
        header(sender, title);
        sender.sendMessage("§a✔ §f" + cleanPrefix(message));
        sendDetails(sender, details);
        footer(sender);
    }

    public static void info(CommandSender sender, String title, String message, String... details) {
        header(sender, title);
        sender.sendMessage("§e➜ §f" + cleanPrefix(message));
        sendDetails(sender, details);
        footer(sender);
    }

    public static void businessInfo(CommandSender sender, Business business) {
        header(sender, "Dossier Entreprise");
        sender.sendMessage("§e➜ §fEntreprise : §6" + business.getName());
        line(sender, "Dirigeant : §e" + business.getOwnerName());
        line(sender, "État : " + business.getStatus().getDisplayName());
        line(sender, "Banque : §e" + money(business.getBalance()));
        line(sender, "Création n° : §e" + business.getCreationIndex());
        line(sender, "Frais : §e" + money(business.getCreationFee()));
        line(sender, "Créée : §f" + TimeUtil.formatDate(business.getCreatedAt()));
        line(sender, "Service officiel de " + brand());
        footer(sender);
    }

    public static String money(double amount) {
        return VaultHook.format(amount);
    }

    private static void sendDetails(CommandSender sender, String... details) {
        if (details == null) return;

        for (String detail : details) {
            if (detail == null || detail.isBlank()) continue;

            String clean = detail.trim();

            if (clean.startsWith("§8•")
                    || clean.startsWith("§e➜")
                    || clean.startsWith("§a✔")
                    || clean.startsWith("§c✖")) {
                sender.sendMessage(clean);
                continue;
            }

            line(sender, clean);
        }
    }

    private static String cleanTitle(String title) {
        if (title == null || title.isBlank()) {
            return "Bureau des Entreprises";
        }

        return title
                .replace("§f", "")
                .replace("§6", "")
                .replace("§a", "")
                .replace("§c", "")
                .replace("§7", "")
                .replace("§8", "")
                .replace("§l", "")
                .replace("✦", "")
                .trim();
    }

    private static String cleanPrefix(String text) {
        if (text == null) return "";

        return text
                .replaceFirst("^§[0-9a-fk-or]", "")
                .replaceFirst("^➜\\s*", "")
                .replaceFirst("^✔\\s*", "")
                .replaceFirst("^✘\\s*", "")
                .replaceFirst("^✖\\s*", "")
                .replaceFirst("^•\\s*", "")
                .trim();
    }
}
