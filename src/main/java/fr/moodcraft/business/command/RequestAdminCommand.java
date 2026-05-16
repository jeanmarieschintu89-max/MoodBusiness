package fr.moodcraft.business.command;

import fr.moodcraft.business.manager.AuditLogManager;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.storage.RequestStorage;
import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RequestAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission("moodbusiness.staff")) {
            BusinessMessages.deny(
                    sender,
                    "Admin Demandes",
                    "Accès réservé au staff."
            );
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "list", "liste", "voir" -> list(sender, args);
            case "clear", "delete", "supprimer", "reset" -> clear(sender, args);
            default -> help(sender);
        }

        return true;
    }

    private void list(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/demandesadmin list <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        List<BusinessRequest> requests = RequestStorage.getByCreator(target.getUniqueId());

        BusinessMessages.header(sender, "Admin Demandes");
        sender.sendMessage("§e➜ §fDemandes du joueur : §e" + safeName(target));
        sender.sendMessage("§8• §7Total : §e" + requests.size());
        sender.sendMessage("");

        if (requests.isEmpty()) {
            sender.sendMessage("§8• §7Aucune demande trouvée.");
        } else {
            for (BusinessRequest request : requests) {
                sender.sendMessage("§8• §e" + request.getTitle()
                        + " §8• §7" + request.getStatus().name()
                        + " §8• §7ID: §f" + request.getId());
            }
        }

        BusinessMessages.footer(sender);
    }

    private void clear(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/demandesadmin clear <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        int removed = RequestStorage.removeByCreator(target.getUniqueId());

        AuditLogManager.log(
                AuditLogType.STAFF_ACTION,
                sender,
                safeName(target),
                null,
                "Demandes joueur supprimées par commande admin: " + removed
        );

        BusinessMessages.success(
                sender,
                "Admin Demandes",
                "Demandes supprimées pour §e" + safeName(target) + " §8• §e" + removed
        );
    }

    private void help(CommandSender sender) {
        BusinessMessages.header(sender, "Admin Demandes");
        sender.sendMessage("§fGestion staff des demandes joueurs.");
        sender.sendMessage("");
        sender.sendMessage("§8• §e/demandesadmin list <joueur>");
        sender.sendMessage("§8• §e/demandesadmin clear <joueur>");
        sender.sendMessage("");
        sender.sendMessage("§8• §7Supprime uniquement les demandes du joueur.");
        sender.sendMessage("§8• §7Ne touche pas aux entreprises existantes.");
        BusinessMessages.footer(sender);
    }

    private void usage(CommandSender sender, String usage) {
        BusinessMessages.header(sender, "Admin Demandes");
        sender.sendMessage("§c✖ §fCommande incomplète.");
        sender.sendMessage("§e➜ §fUtilisation : §e" + usage);
        BusinessMessages.footer(sender);
    }

    private String safeName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : "Inconnu";
    }
}
