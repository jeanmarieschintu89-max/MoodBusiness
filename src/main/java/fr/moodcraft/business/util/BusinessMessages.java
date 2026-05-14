package fr.moodcraft.business.util;

import fr.moodcraft.business.model.Business;

import org.bukkit.command.CommandSender;

public final class BusinessMessages {

    private BusinessMessages() {}

    //
    // 🟢 BRAND
    //

    public static String brand() {

        return "§aMood§6Craft";
    }

    //
    // 🎨 HEADER PREMIUM
    //

    public static void header(
            CommandSender sender,
            String title
    ) {

        sender.sendMessage("");
        sender.sendMessage(
                "§8----- §6✦ "
                        + cleanTitle(title)
                        + " ✦ §8-----"
        );
        sender.sendMessage("");
    }

    //
    // 🎨 FOOTER
    //

    public static void footer(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage(
                "§8-----------------------------"
        );
        sender.sendMessage("");
    }

    //
    // ➜ INFO
    //

    public static void infoLine(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage(
                "§e➜ §f" + cleanPrefix(message)
        );
    }

    //
    // • DÉTAIL
    //

    public static void line(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage(
                "§8• §7" + cleanPrefix(message)
        );
    }

    //
    // ❌ REFUS
    //

    public static void deny(
            CommandSender sender,
            String title,
            String reason
    ) {

        header(
                sender,
                title
        );

        sender.sendMessage("§c✖ §fAction refusée.");
        sender.sendMessage("");
        line(sender, reason);

        footer(sender);
    }

    //
    // ✅ SUCCÈS
    //

    public static void success(
            CommandSender sender,
            String title,
            String message
    ) {

        header(
                sender,
                title
        );

        sender.sendMessage("§a✔ §f" + cleanPrefix(message));

        footer(sender);
    }

    //
    // ℹ INFO
    //

    public static void info(
            CommandSender sender,
            String title,
            String message
    ) {

        header(
                sender,
                title
        );

        sender.sendMessage("§e➜ §f" + cleanPrefix(message));

        footer(sender);
    }

    //
    // 🏢 DOSSIER ENTREPRISE
    //

    public static void businessInfo(
            CommandSender sender,
            Business business
    ) {

        header(
                sender,
                "Dossier Entreprise"
        );

        sender.sendMessage("§e➜ §fEntreprise : §6" + business.getName());
        sender.sendMessage("");
        line(sender, "Dirigeant : §e" + business.getOwnerName());
        line(sender, "État : " + business.getStatus().getDisplayName());
        line(sender, "Banque : §e" + money(business.getBalance()));
        line(sender, "Création n° : §e" + business.getCreationIndex());
        line(sender, "Frais : §e" + money(business.getCreationFee()));
        line(sender, "Créée : §f" + TimeUtil.formatDate(business.getCreatedAt()));
        sender.sendMessage("");
        line(
                sender,
                "Service officiel de " + brand()
        );

        footer(sender);
    }

    //
    // 💶 MONEY
    //

    public static String money(
            double amount
    ) {

        return VaultHook.format(amount);
    }

    //
    // 🧼 TITLE CLEANER
    //

    private static String cleanTitle(
            String title
    ) {

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
                .replace("✦", "")
                .trim();
    }

    private static String cleanPrefix(
            String text
    ) {

        if (text == null) {
            return "";
        }

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
