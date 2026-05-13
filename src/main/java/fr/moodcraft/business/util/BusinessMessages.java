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
                        + " §6✦ §8-----"
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
    // • LIGNE SIMPLE
    //

    public static void line(
            CommandSender sender,
            String message
    ) {

        sender.sendMessage(
                "§8• §7" + message
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

        sender.sendMessage("§c✘ §fAction refusée.");
        sender.sendMessage("");
        sender.sendMessage("§7" + reason);

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

        sender.sendMessage("§a✔ §f" + message);

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

        sender.sendMessage("§7" + message);

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

        sender.sendMessage("§6✦ §f" + business.getName());
        sender.sendMessage("");
        sender.sendMessage("§7Dirigeant: §e" + business.getOwnerName());
        sender.sendMessage("§7État: " + business.getStatus().getDisplayName());
        sender.sendMessage("§7Solde: §e" + money(business.getBalance()));
        sender.sendMessage("§7Création n°: §e" + business.getCreationIndex());
        sender.sendMessage("§7Frais: §e" + money(business.getCreationFee()));
        sender.sendMessage("§7Créée: §f" + TimeUtil.formatDate(business.getCreatedAt()));
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
}