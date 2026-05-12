package fr.moodcraft.business.util;

import fr.moodcraft.business.model.Business;

import org.bukkit.command.CommandSender;

public final class BusinessMessages {

    private BusinessMessages() {}

    public static void header(
            CommandSender sender,
            String title
    ) {

        sender.sendMessage("");
        sender.sendMessage(
                "§8----- §6✦ "
                        + title
                        + " §6✦ §8-----"
        );
    }

    public static void footer(
            CommandSender sender
    ) {

        sender.sendMessage("");
        sender.sendMessage(
                "§8-----------------------------"
        );
        sender.sendMessage("");
    }

    public static void deny(
            CommandSender sender,
            String title,
            String reason
    ) {

        header(
                sender,
                title
        );

        sender.sendMessage("§cAccès refusé.");
        sender.sendMessage("§7" + reason);

        footer(sender);
    }

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

    public static void businessInfo(
            CommandSender sender,
            Business business
    ) {

        header(
                sender,
                "Dossier Entreprise"
        );

        sender.sendMessage("§6✦ §f" + business.getName() + " §6✦");
        sender.sendMessage("");
        sender.sendMessage("§7Dirigeant: §e" + business.getOwnerName());
        sender.sendMessage("§7Statut: " + business.getStatus().getDisplayName());
        sender.sendMessage("§7Solde entreprise: §e" + money(business.getBalance()));
        sender.sendMessage("§7Création n°: §e" + business.getCreationIndex());
        sender.sendMessage("§7Frais d'enregistrement: §e" + money(business.getCreationFee()));
        sender.sendMessage("§7Créée le: §f" + TimeUtil.formatDate(business.getCreatedAt()));
        sender.sendMessage("");
        sender.sendMessage("§8• §7Service officiel de §aMood§6Craft§7.");

        footer(sender);
    }

    public static String money(
            double amount
    ) {

        return VaultHook.format(amount);
    }

    public static String brand() {

        return "§aMood§6Craft";
    }
}