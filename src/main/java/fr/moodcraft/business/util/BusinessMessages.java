package fr.moodcraft.business.util;

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

    public static String money(
            double amount
    ) {

        return VaultHook.format(amount);
    }

    public static String brand() {

        return "§aMood§6Craft";
    }
}