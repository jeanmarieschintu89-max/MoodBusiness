package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.RequestCategoryGUI;
import fr.moodcraft.business.gui.RequestListGUI;
import fr.moodcraft.business.gui.RequestMainGUI;

import fr.moodcraft.business.manager.RequestManager;

import fr.moodcraft.business.model.BusinessRequest;

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
                    "§c✖ §fCommande joueur uniquement."
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

        if (sub.equals("annuler")
                || sub.equals("cancel")
                || sub.equals("supprimer")) {

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Demandes",
                        "Indiquez l'identifiant de la demande."
                );

                return true;
            }

            BusinessRequest request =
                    RequestManager.get(args[1]);

            if (request == null) {

                BusinessMessages.deny(
                        p,
                        "Demandes",
                        "Demande introuvable."
                );

                return true;
            }

            RequestManager.RequestResult result =
                    RequestManager.cancel(
                            p,
                            request
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Demandes",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Demandes",
                    result.message()
            );

            RequestListGUI.openMy(p);

            return true;
        }

        BusinessMessages.header(
                p,
                "Demandes"
        );

        p.sendMessage("§e➜ §fMenu des demandes économiques.");
        p.sendMessage("§8• §7/demandes §eouvrir le menu");
        p.sendMessage("§8• §7/demandes creer §ecréer une demande");
        p.sendMessage("§8• §7/demandes mes §evoir vos demandes");
        p.sendMessage("§8• §7/demandes publiques §evoir les besoins");
        p.sendMessage("§8• §7/demandes annuler <id> §eannuler une demande");

        BusinessMessages.footer(p);

        return true;
    }
}
