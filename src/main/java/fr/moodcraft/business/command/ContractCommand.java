package fr.moodcraft.business.command;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class ContractCommand implements CommandExecutor {

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
                "Contrats Officiels"
        );

        p.sendMessage("§6✦ §fMes contrats §6✦");
        p.sendMessage("§7Suivre vos contrats personnels.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fContrats d'entreprise §6✦");
        p.sendMessage("§7Voir les contrats liés à votre entreprise.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fSécurité des fonds §6✦");
        p.sendMessage("§7Les paiements seront bloqués");
        p.sendMessage("§7jusqu'à validation finale.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fTaxe économique §6✦");
        p.sendMessage("§7Taxe prévue: §c20% §7sur les contrats validés.");
        p.sendMessage("");

        p.sendMessage("§8• §7Module complet dans un prochain pack.");

        BusinessMessages.footer(p);

        return true;
    }
}