package fr.moodcraft.business.command;

import fr.moodcraft.business.manager.AuditLogManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessStatus;

import fr.moodcraft.business.storage.BusinessStorage;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class BusinessAdminCommand
        implements CommandExecutor {

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
                    "Admin Entreprises",
                    "Accès réservé au staff."
            );

            return true;
        }

        if (args.length == 0) {

            sendHelp(sender);

            return true;
        }

        String sub =
                args[0].toLowerCase();

        switch (sub) {

            //
            // 📘 AIDE
            //

            case "aide", "help" -> {

                sendHelp(sender);

                return true;
            }

            //
            // 📋 LISTE
            //

            case "liste", "list" -> {

                listBusinesses(sender);

                return true;
            }

            //
            // ℹ INFO
            //

            case "info" -> {

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin info <entreprise>"
                    );

                    return true;
                }

                Business business =
                        getBusinessFromArgs(
                                args,
                                1
                        );

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                BusinessMessages.businessInfo(
                        sender,
                        business
                );

                return true;
            }

            //
            // ⛔ SUSPENDRE
            //

            case "suspendre", "suspend" -> {

                if (!sender.hasPermission("moodbusiness.staff.suspend")) {

                    error(
                            sender,
                            "Vous ne pouvez pas suspendre une entreprise."
                    );

                    return true;
                }

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin suspendre <entreprise>"
                    );

                    return true;
                }

                Business business =
                        getBusinessFromArgs(
                                args,
                                1
                        );

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                BusinessManager.setStatus(
                        business,
                        BusinessStatus.SUSPENDUE
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        business.getName(),
                        business,
                        "Entreprise suspendue par commande admin."
                );

                success(
                        sender,
                        "Entreprise suspendue: §e" + business.getName()
                );

                return true;
            }

            //
            // ✅ RÉACTIVER
            //

            case "reactiver", "réactiver", "active" -> {

                if (!sender.hasPermission("moodbusiness.staff.suspend")) {

                    error(
                            sender,
                            "Vous ne pouvez pas réactiver une entreprise."
                    );

                    return true;
                }

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin reactiver <entreprise>"
                    );

                    return true;
                }

                Business business =
                        getBusinessFromArgs(
                                args,
                                1
                        );

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                BusinessManager.setStatus(
                        business,
                        BusinessStatus.ACTIVE
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        business.getName(),
                        business,
                        "Entreprise réactivée par commande admin."
                );

                success(
                        sender,
                        "Entreprise réactivée: §e" + business.getName()
                );

                return true;
            }

            //
            // 📦 ARCHIVER / FERMER
            //

            case "archiver", "fermer", "close" -> {

                if (!sender.hasPermission("moodbusiness.staff.suspend")) {

                    error(
                            sender,
                            "Vous ne pouvez pas archiver une entreprise."
                    );

                    return true;
                }

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin archiver <entreprise>"
                    );

                    return true;
                }

                Business business =
                        getBusinessFromArgs(
                                args,
                                1
                        );

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                BusinessManager.setStatus(
                        business,
                        BusinessStatus.ARCHIVEE
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        business.getName(),
                        business,
                        "Entreprise archivée par commande admin."
                );

                success(
                        sender,
                        "Entreprise archivée: §e" + business.getName()
                );

                return true;
            }

            //
            // ⏳ RESET DÉLAI CRÉATION
            //

            case "delaireset", "resetdelai", "cooldownreset" -> {

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin delaireset <joueur>"
                    );

                    return true;
                }

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(
                                args[1]
                        );

                BusinessStorage.setCooldownUntil(
                        target.getUniqueId(),
                        0L
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        safeName(target),
                        null,
                        "Délai de création entreprise réinitialisé."
                );

                success(
                        sender,
                        "Délai de création réinitialisé pour §e"
                                + safeName(target)
                );

                return true;
            }

            //
            // 🔢 SET COMPTEUR CRÉATION
            //

            case "compteur", "setcompteur" -> {

                if (args.length < 3) {

                    usage(
                            sender,
                            "/entrepriseadmin compteur <joueur> <nombre>"
                    );

                    return true;
                }

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(
                                args[1]
                        );

                int count;

                try {

                    count =
                            Integer.parseInt(args[2]);

                } catch (Exception e) {

                    error(
                            sender,
                            "Nombre invalide."
                    );

                    return true;
                }

                if (count < 0) {
                    count = 0;
                }

                BusinessStorage.setCreatedCount(
                        target.getUniqueId(),
                        count
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        safeName(target),
                        null,
                        "Compteur de créations entreprise défini à "
                                + count
                );

                success(
                        sender,
                        "Compteur de créations défini à §e"
                                + count
                                + " §7pour §e"
                                + safeName(target)
                );

                return true;
            }

            //
            // 🔄 RESET COMPTEUR CRÉATION
            //

            case "compteurreset", "resetcompteur" -> {

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin compteurreset <joueur>"
                    );

                    return true;
                }

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(
                                args[1]
                        );

                BusinessStorage.setCreatedCount(
                        target.getUniqueId(),
                        0
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        safeName(target),
                        null,
                        "Compteur de créations entreprise remis à zéro."
                );

                success(
                        sender,
                        "Compteur de créations remis à zéro pour §e"
                                + safeName(target)
                );

                return true;
            }

            //
            // 🚫 BLOQUER JOUEUR DU BUREAU
            //

            case "bloquer", "suspendjoueur" -> {

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin bloquer <joueur>"
                    );

                    return true;
                }

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(
                                args[1]
                        );

                BusinessStorage.setRegisterSuspended(
                        target.getUniqueId(),
                        true
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        safeName(target),
                        null,
                        "Joueur bloqué du Bureau des Entreprises."
                );

                success(
                        sender,
                        "Joueur bloqué du Bureau des Entreprises: §e"
                                + safeName(target)
                );

                return true;
            }//
            // ✅ DÉBLOQUER JOUEUR DU BUREAU
            //

            case "debloquer", "débloquer", "unsuspendjoueur" -> {

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin debloquer <joueur>"
                    );

                    return true;
                }

                OfflinePlayer target =
                        Bukkit.getOfflinePlayer(
                                args[1]
                        );

                BusinessStorage.setRegisterSuspended(
                        target.getUniqueId(),
                        false
                );

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        safeName(target),
                        null,
                        "Joueur débloqué du Bureau des Entreprises."
                );

                success(
                        sender,
                        "Joueur débloqué: §e"
                                + safeName(target)
                );

                return true;
            }

            //
            // 💰 SOLDE ENTREPRISE
            //

            case "solde", "banque" -> {

                if (args.length < 2) {

                    usage(
                            sender,
                            "/entrepriseadmin solde <entreprise>"
                    );

                    return true;
                }

                Business business =
                        getBusinessFromArgs(
                                args,
                                1
                        );

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                BusinessMessages.header(
                        sender,
                        "Banque Entreprise"
                );

                sender.sendMessage("§7Entreprise: §e" + business.getName());
                sender.sendMessage("§7Solde: §e" + VaultHook.format(business.getBalance()));

                BusinessMessages.footer(sender);

                return true;
            }

            //
            // 💰 SET SOLDE
            //

            case "setsolde" -> {

                if (args.length < 3) {

                    usage(
                            sender,
                            "/entrepriseadmin setsolde <entreprise> <montant>"
                    );

                    return true;
                }

                double amount =
                        parseLastAmount(
                                sender,
                                args
                        );

                if (amount < 0) {
                    return true;
                }

                String name =
                        joinArgs(
                                args,
                                1,
                                args.length - 1
                        );

                Business business =
                        BusinessManager.getByName(name);

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                business.setBalance(amount);

                BusinessStorage.save();

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        business.getName(),
                        business,
                        "Solde entreprise défini à "
                                + VaultHook.format(amount)
                );

                success(
                        sender,
                        "Solde défini: §e"
                                + business.getName()
                                + " §8» §e"
                                + VaultHook.format(amount)
                );

                return true;
            }

            //
            // ➕ AJOUTER AU SOLDE
            //

            case "ajouter", "addsolde" -> {

                if (args.length < 3) {

                    usage(
                            sender,
                            "/entrepriseadmin ajouter <entreprise> <montant>"
                    );

                    return true;
                }

                double amount =
                        parseLastAmount(
                                sender,
                                args
                        );

                if (amount <= 0) {
                    return true;
                }

                String name =
                        joinArgs(
                                args,
                                1,
                                args.length - 1
                        );

                Business business =
                        BusinessManager.getByName(name);

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                business.setBalance(
                        business.getBalance() + amount
                );

                BusinessStorage.save();

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        business.getName(),
                        business,
                        "Ajout banque entreprise: "
                                + VaultHook.format(amount)
                );

                success(
                        sender,
                        "Ajout effectué: §e"
                                + VaultHook.format(amount)
                                + " §7à §e"
                                + business.getName()
                );

                return true;
            }

            //
            // ➖ RETIRER DU SOLDE
            //

            case "retirer", "removesolde" -> {

                if (args.length < 3) {

                    usage(
                            sender,
                            "/entrepriseadmin retirer <entreprise> <montant>"
                    );

                    return true;
                }

                double amount =
                        parseLastAmount(
                                sender,
                                args
                        );

                if (amount <= 0) {
                    return true;
                }

                String name =
                        joinArgs(
                                args,
                                1,
                                args.length - 1
                        );

                Business business =
                        BusinessManager.getByName(name);

                if (business == null) {

                    error(
                            sender,
                            "Entreprise introuvable."
                    );

                    return true;
                }

                business.setBalance(
                        Math.max(
                                0,
                                business.getBalance() - amount
                        )
                );

                BusinessStorage.save();

                AuditLogManager.log(
                        AuditLogType.STAFF_ACTION,
                        sender,
                        business.getName(),
                        business,
                        "Retrait banque entreprise: "
                                + VaultHook.format(amount)
                );

                success(
                        sender,
                        "Retrait effectué: §e"
                                + VaultHook.format(amount)
                                + " §7à §e"
                                + business.getName()
                );

                return true;
            }

            default -> {

                sendHelp(sender);

                return true;
            }
        }
    }

    //
    // 📘 AIDE
    //

    private void sendHelp(
            CommandSender sender
    ) {

        BusinessMessages.header(
                sender,
                "Admin Entreprises"
        );

        sender.sendMessage("§fCommandes staff.");
        sender.sendMessage("");
        sender.sendMessage("§8• §e/entrepriseadmin liste");
        sender.sendMessage("§8• §e/entrepriseadmin info <entreprise>");
        sender.sendMessage("§8• §e/entrepriseadmin suspendre <entreprise>");
        sender.sendMessage("§8• §e/entrepriseadmin reactiver <entreprise>");
        sender.sendMessage("§8• §e/entrepriseadmin archiver <entreprise>");
        sender.sendMessage("");
        sender.sendMessage("§8• §e/entrepriseadmin delaireset <joueur>");
        sender.sendMessage("§8• §e/entrepriseadmin compteur <joueur> <nombre>");
        sender.sendMessage("§8• §e/entrepriseadmin compteurreset <joueur>");
        sender.sendMessage("§8• §e/entrepriseadmin bloquer <joueur>");
        sender.sendMessage("§8• §e/entrepriseadmin debloquer <joueur>");
        sender.sendMessage("");
        sender.sendMessage("§8• §e/entrepriseadmin solde <entreprise>");
        sender.sendMessage("§8• §e/entrepriseadmin setsolde <entreprise> <montant>");
        sender.sendMessage("§8• §e/entrepriseadmin ajouter <entreprise> <montant>");
        sender.sendMessage("§8• §e/entrepriseadmin retirer <entreprise> <montant>");

        BusinessMessages.footer(sender);
    }

    //
    // 📋 LISTE
    //

    private void listBusinesses(
            CommandSender sender
    ) {

        BusinessMessages.header(
                sender,
                "Admin Entreprises"
        );

        int count = 0;

        for (Business business :
                BusinessManager.getAll()) {

            sender.sendMessage(
                    "§8• §e"
                            + business.getName()
                            + " §8» "
                            + business.getStatus().getDisplayName()
            );

            count++;
        }

        if (count == 0) {

            sender.sendMessage("§7Aucune entreprise.");
        }

        BusinessMessages.footer(sender);
    }

    //
    // 🔎 BUSINESS FROM ARGS
    //

    private Business getBusinessFromArgs(
            String[] args,
            int start
    ) {

        String name =
                joinArgs(
                        args,
                        start,
                        args.length
                );

        return BusinessManager.getByName(name);
    }

    //
    // 🔤 JOIN ARGS
    //

    private String joinArgs(
            String[] args,
            int start,
            int end
    ) {

        return String.join(
                " ",
                Arrays.copyOfRange(
                        args,
                        start,
                        end
                )
        );
    }

    //
    // 🔢 MONTANT FINAL
    //

    private double parseLastAmount(
            CommandSender sender,
            String[] args
    ) {

        try {

            return Double.parseDouble(
                    args[args.length - 1]
                            .replace(",", ".")
            );

        } catch (Exception e) {

            error(
                    sender,
                    "Montant invalide."
            );

            return -1;
        }
    }

    //
    // 👤 NOM
    //

    private String safeName(
            OfflinePlayer player
    ) {

        return player.getName() != null
                ? player.getName()
                : "Inconnu";
    }

    //
    // 📘 USAGE
    //

    private void usage(
            CommandSender sender,
            String usage
    ) {

        BusinessMessages.header(
                sender,
                "Admin Entreprises"
        );

        sender.sendMessage("§fCommande incomplète.");
        sender.sendMessage("");
        sender.sendMessage("§7Utilisation:");
        sender.sendMessage("§e" + usage);

        BusinessMessages.footer(sender);
    }

    //
    // ✅ SUCCESS
    //

    private void success(
            CommandSender sender,
            String message
    ) {

        BusinessMessages.success(
                sender,
                "Admin Entreprises",
                message
        );
    }

    //
    // ❌ ERROR
    //

    private void error(
            CommandSender sender,
            String message
    ) {

        BusinessMessages.deny(
                sender,
                "Admin Entreprises",
                message
        );
    }
}