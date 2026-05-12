package fr.moodcraft.business.command;

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

        BusinessMessages.header(
                p,
                "Demandes " + "§aMood§6Craft"
        );

        p.sendMessage("§6✦ §fCréer une demande §6✦");
        p.sendMessage("§7Publier une recherche de service,");
        p.sendMessage("§7construction, livraison ou commerce.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fMes demandes §6✦");
        p.sendMessage("§7Consulter vos demandes actives.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fDemandes publiques §6✦");
        p.sendMessage("§7Voir les besoins ouverts aux entreprises.");
        p.sendMessage("");

        p.sendMessage("§8• §7Module complet dans un prochain pack.");

        BusinessMessages.footer(p);

        return true;
    }
}