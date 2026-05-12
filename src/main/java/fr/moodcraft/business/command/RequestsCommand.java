package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.RequestCategoryGUI;
import fr.moodcraft.business.gui.RequestListGUI;
import fr.moodcraft.business.gui.RequestMainGUI;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class RequestsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {

            sender.sendMessage(
                    "Commande joueur uniquement."
            );

            return true;
        }

        if (args.length == 0) {

            RequestMainGUI.open(p);

            return true;
        }

        String sub =
                args[0].toLowerCase();

        if (sub.equals("creer")
                || sub.equals("créer")
                || sub.equals("create")) {

            RequestCategoryGUI.open(p);

            return true;
        }

        if (sub.equals("mes")
                || sub.equals("mine")) {

            RequestListGUI.openMy(p);

            return true;
        }

        if (sub.equals("publiques")
                || sub.equals("public")
                || sub.equals("liste")) {

            RequestListGUI.openPublic(p);

            return true;
        }

        BusinessMessages.header(
                p,
                "Demandes " + BusinessMessages.brand()
        );

        p.sendMessage("§7Utilisation:");
        p.sendMessage("§8• §e/demandes");
        p.sendMessage("§8• §e/demandes creer");
        p.sendMessage("§8• §e/demandes mes");
        p.sendMessage("§8• §e/demandes publiques");

        BusinessMessages.footer(p);

        return true;
    }
}