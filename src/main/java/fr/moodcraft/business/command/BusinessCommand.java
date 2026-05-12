package fr.moodcraft.business.command;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

public class BusinessCommand implements CommandExecutor {

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

        if (args.length >= 1
                && args[0].equalsIgnoreCase("staff")) {

            if (!p.hasPermission("moodbusiness.staff")) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Accès réservé à l'administration économique."
                );

                return true;
            }

            BusinessMessages.header(
                    p,
                    "Gestion Entreprises"
            );

            p.sendMessage("§7Service officiel de §aMood§6Craft§7.");
            p.sendMessage("");

            p.sendMessage("§6✦ §fEntreprises actives §6✦");
            p.sendMessage("§7Consulter les entreprises enregistrées.");
            p.sendMessage("");

            p.sendMessage("§6✦ §fEntreprises récentes §6✦");
            p.sendMessage("§7Surveiller les nouvelles créations.");
            p.sendMessage("");

            p.sendMessage("§6✦ §fEntreprises suspendues §6✦");
            p.sendMessage("§7Consulter les sanctions économiques.");
            p.sendMessage("");

            p.sendMessage("§6✦ §fLitiges économiques §6✦");
            p.sendMessage("§7Gérer les conflits et fonds bloqués.");
            p.sendMessage("");

            p.sendMessage("§8• §7Les GUI arrivent avec le Pack 2.");

            BusinessMessages.footer(p);

            return true;
        }

        BusinessMessages.header(
                p,
                "Registre Économique"
        );

        p.sendMessage("§7Service officiel de §aMood§6Craft§7.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fEntreprises §6✦");
        p.sendMessage("§7Créer et gérer une entreprise.");
        p.sendMessage("§8• §7Création: §e15 000€§7, puis §e+15 000€ §7par création.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fEmployés et rôles §6✦");
        p.sendMessage("§7Dirigeant, gérant, trésorier, employé,");
        p.sendMessage("§7apprenti et stagiaire.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fCandidatures §6✦");
        p.sendMessage("§7Stage, apprentissage ou emploi.");
        p.sendMessage("");

        p.sendMessage("§6✦ §fContrats officiels §6✦");
        p.sendMessage("§7Fonds bloqués, taxe 20%, validation et litiges.");

        BusinessMessages.footer(p);

        return true;
    }
}