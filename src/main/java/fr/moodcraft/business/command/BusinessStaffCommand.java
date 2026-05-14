package fr.moodcraft.business.command;

import fr.moodcraft.business.manager.AuditLogManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.AuditLogEntry;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.storage.AuditLogStorage;
import fr.moodcraft.business.storage.BusinessStorage;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BusinessStaffCommand implements CommandExecutor {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("moodbusiness.staff")) {
            error(sender, "Accès réservé au staff entreprises.");
            return true;
        }

        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "help", "aide" -> help(sender);
            case "status", "etat", "état" -> status(sender);
            case "audit" -> audit(sender, args);
            case "membres", "members" -> members(sender, args);
            case "addmember", "ajouter" -> addMember(sender, args);
            case "delmember", "remove", "retirer" -> removeMember(sender, args);
            case "setrole", "role" -> setRole(sender, args);
            case "finance", "money" -> finance(sender, args);
            case "save" -> save(sender);
            case "reload" -> reload(sender);
            default -> help(sender);
        }

        return true;
    }

    private void status(CommandSender sender) {
        header(sender, "Staff Entreprises");
        sender.sendMessage("§e➜ §7Entreprises chargées : §e" + BusinessManager.getAll().size());
        sender.sendMessage("§e➜ §7Logs audit : §e" + AuditLogStorage.getAll().size());
        sender.sendMessage("§e➜ §7Vault : " + (VaultHook.isReady() ? "§aOK" : "§cIndisponible"));
        footer(sender);
    }

    private void audit(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/businessstaff audit <entreprise>");
            return;
        }

        Business business = getBusiness(args, 1);
        if (business == null) {
            error(sender, "Entreprise introuvable.");
            return;
        }

        header(sender, "Audit Entreprise");
        sender.sendMessage("§e➜ §7Entreprise : §e" + business.getName());

        int shown = 0;
        for (AuditLogEntry entry : AuditLogStorage.getByBusiness(business.getId(), 8)) {
            sender.sendMessage("§e➜ §7" + dateFormat.format(new Date(entry.getCreatedAt()))
                    + " §8• §e" + entry.getType().name()
                    + " §8• §7" + entry.getActorName()
                    + " §8→ §f" + entry.getMessage());
            shown++;
        }

        if (shown == 0) {
            sender.sendMessage("§e➜ §7Aucun log pour cette entreprise.");
        }

        footer(sender);
    }

    private void members(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/businessstaff membres <entreprise>");
            return;
        }

        Business business = getBusiness(args, 1);
        if (business == null) {
            error(sender, "Entreprise introuvable.");
            return;
        }

        header(sender, "Membres Entreprise");
        sender.sendMessage("§e➜ §7Entreprise : §e" + business.getName());

        for (UUID uuid : business.getMembers().keySet()) {
            sender.sendMessage("§e➜ §7" + business.getMemberName(uuid)
                    + " §8• " + business.getRole(uuid).getDisplayName()
                    + (business.isOwner(uuid) ? " §8• §6Dirigeant légal" : ""));
        }

        footer(sender);
    }

    private void addMember(CommandSender sender, String[] args) {
        if (args.length < 4) {
            usage(sender, "/businessstaff addmember <entreprise> <joueur> <role>");
            return;
        }

        BusinessRole role = BusinessRole.fromText(args[args.length - 1]);
        if (role == null) {
            error(sender, "Rôle invalide.");
            return;
        }

        if (role == BusinessRole.DIRIGEANT) {
            error(sender, "Le rôle dirigeant ne peut pas être ajouté par cette commande.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[args.length - 2]);
        Business business = getBusiness(args, 1, args.length - 2);

        if (business == null) {
            error(sender, "Entreprise introuvable.");
            return;
        }

        if (business.isMember(target.getUniqueId())) {
            error(sender, "Ce joueur est déjà membre de l'entreprise.");
            return;
        }

        business.addMember(target.getUniqueId(), safeName(target), role);
        BusinessStorage.save();

        AuditLogManager.log(
                AuditLogType.MEMBER_ADDED,
                sender,
                safeName(target),
                business,
                "Membre ajouté par commande staff avec rôle: " + role.getDisplayName()
        );

        success(sender, "Membre ajouté : §e" + safeName(target) + " §8• " + role.getDisplayName());
    }

    private void removeMember(CommandSender sender, String[] args) {
        if (args.length < 3) {
            usage(sender, "/businessstaff delmember <entreprise> <joueur>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[args.length - 1]);
        Business business = getBusiness(args, 1, args.length - 1);

        if (business == null) {
            error(sender, "Entreprise introuvable.");
            return;
        }

        if (!business.isMember(target.getUniqueId())) {
            error(sender, "Ce joueur n'est pas membre de l'entreprise.");
            return;
        }

        if (business.isOwner(target.getUniqueId())) {
            error(sender, "Impossible de retirer le dirigeant légal.");
            return;
        }

        String name = business.getMemberName(target.getUniqueId());
        business.removeMember(target.getUniqueId());
        BusinessStorage.save();

        AuditLogManager.log(
                AuditLogType.MEMBER_REMOVED,
                sender,
                name,
                business,
                "Membre retiré par commande staff."
        );

        success(sender, "Membre retiré : §e" + name);
    }

    private void setRole(CommandSender sender, String[] args) {
        if (args.length < 4) {
            usage(sender, "/businessstaff setrole <entreprise> <joueur> <role>");
            return;
        }

        BusinessRole role = BusinessRole.fromText(args[args.length - 1]);
        if (role == null) {
            error(sender, "Rôle invalide.");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[args.length - 2]);
        Business business = getBusiness(args, 1, args.length - 2);

        if (business == null) {
            error(sender, "Entreprise introuvable.");
            return;
        }

        if (!business.isMember(target.getUniqueId())) {
            error(sender, "Ce joueur n'est pas membre de l'entreprise.");
            return;
        }

        if (business.isOwner(target.getUniqueId()) && role != BusinessRole.DIRIGEANT) {
            error(sender, "Le dirigeant légal doit garder le rôle dirigeant.");
            return;
        }

        business.setRole(target.getUniqueId(), role);
        business.setMemberName(target.getUniqueId(), safeName(target));
        BusinessStorage.save();

        AuditLogManager.log(
                AuditLogType.ROLE_CHANGED,
                sender,
                safeName(target),
                business,
                "Rôle défini par commande staff: " + role.getDisplayName()
        );

        success(sender, "Rôle modifié : §e" + safeName(target) + " §8• " + role.getDisplayName());
    }

    private void finance(CommandSender sender, String[] args) {
        if (args.length < 2) {
            usage(sender, "/businessstaff finance <entreprise>");
            return;
        }

        Business business = getBusiness(args, 1);
        if (business == null) {
            error(sender, "Entreprise introuvable.");
            return;
        }

        header(sender, "Finance Entreprise");
        sender.sendMessage("§e➜ §7Entreprise : §e" + business.getName());
        sender.sendMessage("§e➜ §7Solde : §e" + VaultHook.format(business.getBalance()));
        sender.sendMessage("§e➜ §7Frais création : §e" + VaultHook.format(business.getCreationFee()));
        sender.sendMessage("§e➜ §7Index création : §e" + business.getCreationIndex());
        footer(sender);
    }

    private void save(CommandSender sender) {
        BusinessStorage.save();
        AuditLogStorage.save();
        success(sender, "Données entreprises sauvegardées.");
    }

    private void reload(CommandSender sender) {
        BusinessStorage.init();
        AuditLogStorage.init();
        success(sender, "Données entreprises rechargées.");
    }

    private Business getBusiness(String[] args, int start) {
        return getBusiness(args, start, args.length);
    }

    private Business getBusiness(String[] args, int start, int end) {
        if (end <= start) {
            return null;
        }
        return BusinessManager.getByName(String.join(" ", java.util.Arrays.copyOfRange(args, start, end)));
    }

    private String safeName(OfflinePlayer player) {
        return player.getName() != null ? player.getName() : "Inconnu";
    }

    private void help(CommandSender sender) {
        header(sender, "Staff Entreprises");
        sender.sendMessage("§e➜ §7/businessstaff status");
        sender.sendMessage("§e➜ §7/businessstaff audit <entreprise>");
        sender.sendMessage("§e➜ §7/businessstaff membres <entreprise>");
        sender.sendMessage("§e➜ §7/businessstaff addmember <entreprise> <joueur> <role>");
        sender.sendMessage("§e➜ §7/businessstaff delmember <entreprise> <joueur>");
        sender.sendMessage("§e➜ §7/businessstaff setrole <entreprise> <joueur> <role>");
        sender.sendMessage("§e➜ §7/businessstaff finance <entreprise>");
        sender.sendMessage("§e➜ §7/businessstaff save");
        sender.sendMessage("§e➜ §7/businessstaff reload");
        footer(sender);
    }

    private void usage(CommandSender sender, String usage) {
        header(sender, "Staff Entreprises");
        sender.sendMessage("§c✖ §fCommande incomplète.");
        sender.sendMessage("§e➜ §7Utilisation : §e" + usage);
        footer(sender);
    }

    private void success(CommandSender sender, String message) {
        header(sender, "Staff Entreprises");
        sender.sendMessage("§a✔ §f" + message);
        footer(sender);
    }

    private void error(CommandSender sender, String message) {
        header(sender, "Staff Entreprises");
        sender.sendMessage("§c✖ §f" + message);
        footer(sender);
    }

    private void header(CommandSender sender, String title) {
        sender.sendMessage("");
        sender.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");
    }

    private void footer(CommandSender sender) {
        sender.sendMessage("§8-----------------------------");
    }
}
