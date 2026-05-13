package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.ApplicationMainGUI;
import fr.moodcraft.business.gui.BusinessBankGUI;
import fr.moodcraft.business.manager.AuditLogManager;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.gui.BusinessDissolveConfirmGUI;
import fr.moodcraft.business.gui.BusinessEmployeesGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.BusinessStaffGUI;

import fr.moodcraft.business.manager.BusinessBankManager;
import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.PayrollManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.BusinessStatus;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

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

        if (args.length == 0) {

            BusinessMainGUI.open(p);

            return true;
        }

        String sub =
                args[0].toLowerCase();

        //
        // 🔔 ALERTES
        //

        if (sub.equals("alertes")
                || sub.equals("alerts")
                || sub.equals("notifications")) {

            fr.moodcraft.business.manager.AlertManager.showAlertHistory(p);

            return true;
        }

        //
        // 🛡 STAFF
        //

        if (sub.equals("staff")) {

            if (!p.hasPermission("moodbusiness.staff")) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Accès réservé au staff."
                );

                return true;
            }

            BusinessStaffGUI.open(p);

            return true;
        }

        //
        // 🏢 CREATION ENTREPRISE
        //

        if (sub.equals("creer")
                || sub.equals("créer")
                || sub.equals("create")) {

            if (args.length < 2) {

                BusinessMessages.header(
                        p,
                        "Bureau des Entreprises"
                );

                p.sendMessage("§fCréer une entreprise.");
                p.sendMessage("");
                p.sendMessage("§7Utilisation:");
                p.sendMessage("§e/entreprise creer <nom>");
                p.sendMessage("");
                p.sendMessage("§8• §7Exemple: §e/entreprise creer NordBuild");

                BusinessMessages.footer(p);

                return true;
            }

            String name =
                    String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    1,
                                    args.length
                            )
                    );

            BusinessManager.CreationResult result =
                    BusinessManager.createBusiness(
                            p,
                            name
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Bureau des Entreprises",
                        result.message()
                );

                return true;
            }

            Business business =
                    result.business();

            BusinessMessages.header(
                    p,
                    "Bureau des Entreprises"
            );

            p.sendMessage("§a✔ §fEntreprise créée.");
            p.sendMessage("");
            p.sendMessage("§7Nom: §e" + business.getName());
            p.sendMessage("§7Frais: §e" + BusinessMessages.money(business.getCreationFee()));
            p.sendMessage("§7État: " + business.getStatus().getDisplayName());
            p.sendMessage("");
            BusinessMessages.line(
                    p,
                    "Dossier inscrit au Bureau des Entreprises"
            );

            BusinessMessages.footer(p);

            p.playSound(
                    p.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    0.8f,
                    1.1f
            );

            BusinessMainGUI.open(p);

            return true;
        }

        //
        // 📄 INFO ENTREPRISE
        //

        if (sub.equals("info")) {

            Business business;

            if (args.length >= 2) {

                String name =
                        String.join(
                                " ",
                                Arrays.copyOfRange(
                                        args,
                                        1,
                                        args.length
                                )
                        );

                business =
                        BusinessManager.getByName(name);

            } else {

                business =
                        BusinessManager.getOwnedBusiness(
                                p.getUniqueId()
                        );
            }

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Dossier Entreprise",
                        "Aucune entreprise trouvée."
                );

                return true;
            }

            BusinessMessages.businessInfo(
                    p,
                    business
            );

            return true;
        }

        //
        // 👥 EMPLOYES
        //

        if (sub.equals("employes")
                || sub.equals("employés")
                || sub.equals("employees")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            BusinessEmployeesGUI.open(
                    p,
                    business
            );

            return true;
        }

        //
        // 📨 CANDIDATURES
        //

        if (sub.equals("candidatures")
                || sub.equals("candidature")
                || sub.equals("postuler")) {

            ApplicationMainGUI.open(p);

            return true;
        }

        //
        // 💰 BANQUE
        //

        if (sub.equals("banque")
                || sub.equals("bank")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            BusinessBankGUI.open(
                    p,
                    business
            );

            return true;
        }

        //
        // 💰 DEPOT
        //

        if (sub.equals("depot")
                || sub.equals("dépôt")
                || sub.equals("deposit")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        "Montant manquant."
                );

                return true;
            }

            double amount =
                    parseDouble(args[1]);

            BusinessBankManager.BankResult result =
                    BusinessBankManager.deposit(
                            p,
                            business,
                            amount
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Banque Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // 💸 RETRAIT
        //

        if (sub.equals("retrait")
                || sub.equals("withdraw")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        "Montant manquant."
                );

                return true;
            }

            double amount =
                    parseDouble(args[1]);

            BusinessBankManager.BankResult result =
                    BusinessBankManager.withdraw(
                            p,
                            business,
                            amount
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Banque Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Banque Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // 🎁 PRIME
        //

        if (sub.equals("prime")
                || sub.equals("bonus")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (args.length < 3) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Indiquez le joueur et le montant."
                );

                return true;
            }

            UUID targetUuid =
                    BusinessManager.getMemberUuidByName(
                            business,
                            args[1]
                    );

            double amount =
                    parseDouble(args[2]);

            boolean confirmed =
                    args.length >= 4
                            && args[3].equalsIgnoreCase("confirmer");

            BusinessBankManager.BankResult result =
                    BusinessBankManager.bonus(
                            p,
                            business,
                            targetUuid,
                            amount,
                            confirmed
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Paie Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // 🧾 SALAIRE
        //

        if (sub.equals("salaire")
                || sub.equals("salary")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (!BusinessBankManager.canConfigurePayroll(
                    p,
                    business
            )) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Seul le dirigeant peut configurer les salaires."
                );

                return true;
            }

            if (args.length < 3) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Indiquez le rôle et le montant."
                );

                return true;
            }

            BusinessRole role =
                    BusinessRole.fromText(args[1]);

            if (role == null) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Rôle inconnu."
                );

                return true;
            }

            double amount =
                    parseDouble(args[2]);

            PayrollManager.PayrollResult result =
                    PayrollManager.setSalary(
                            business,
                            role,
                            amount
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Paie Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // 📆 PAIE MENSUELLE MANUELLE
        //

        if (sub.equals("paie")
                || sub.equals("payroll")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (!BusinessBankManager.canConfigurePayroll(
                    p,
                    business
            )) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        "Seul le dirigeant peut lancer une paie manuelle."
                );

                return true;
            }

            PayrollManager.PayrollResult result =
                    PayrollManager.payBusiness(
                            business,
                            p.getName(),
                            true
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Paie Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Paie Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // ➕ RECRUTER
        //

        if (sub.equals("recruter")
                || sub.equals("inviter")
                || sub.equals("hire")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Indiquez le joueur à recruter."
                );

                return true;
            }

            OfflinePlayer target =
                    Bukkit.getOfflinePlayer(args[1]);

            BusinessRole role =
                    args.length >= 3
                            ? BusinessRole.fromText(args[2])
                            : BusinessRole.STAGIAIRE;

            if (role == null) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Rôle inconnu."
                );

                return true;
            }

            BusinessManager.ActionResult result =
                    BusinessManager.addMember(
                            p,
                            business,
                            target,
                            role
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Employés Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // 🏷 ROLE
        //

        if (sub.equals("role")
                || sub.equals("rang")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Rôles Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (args.length < 3) {

                BusinessMessages.deny(
                        p,
                        "Rôles Entreprise",
                        "Indiquez le joueur et le rôle."
                );

                return true;
            }

            UUID targetUuid =
                    BusinessManager.getMemberUuidByName(
                            business,
                            args[1]
                    );

            if (targetUuid == null) {

                BusinessMessages.deny(
                        p,
                        "Rôles Entreprise",
                        "Ce joueur n'est pas dans votre entreprise."
                );

                return true;
            }

            BusinessRole role =
                    BusinessRole.fromText(args[2]);

            if (role == null) {

                BusinessMessages.deny(
                        p,
                        "Rôles Entreprise",
                        "Rôle inconnu."
                );

                return true;
            }

            BusinessManager.ActionResult result =
                    BusinessManager.assignRole(
                            p,
                            business,
                            targetUuid,
                            role
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Rôles Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Rôles Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // ➖ RENVOYER
        //

        if (sub.equals("renvoyer")
                || sub.equals("kick")
                || sub.equals("retirer")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Indiquez le joueur à licencier."
                );

                return true;
            }

            UUID targetUuid =
                    BusinessManager.getMemberUuidByName(
                            business,
                            args[1]
                    );

            if (targetUuid == null) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Ce joueur n'est pas dans votre entreprise."
                );

                return true;
            }

            BusinessManager.ActionResult result =
                    BusinessManager.removeMember(
                            p,
                            business,
                            targetUuid
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Employés Entreprise",
                    result.message()
            );

            return true;
        }

        //
        // 🧨 FERMER ENTREPRISE
        //

        if (sub.equals("dissoudre")
                || sub.equals("dissolution")
                || sub.equals("supprimer")
                || sub.equals("delete")
                || sub.equals("fermer")) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            p.getUniqueId()
                    );

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Fermer Entreprise",
                        "Vous n'appartenez à aucune entreprise active."
                );

                return true;
            }

            if (!business.isOwner(
                    p.getUniqueId()
            )) {

                BusinessMessages.deny(
                        p,
                        "Fermer Entreprise",
                        "Seul le dirigeant peut fermer l'entreprise."
                );

                return true;
            }

            BusinessDissolveConfirmGUI.open(
                    p,
                    business
            );

            return true;
        }

        //
        // ⛔ SUSPENDRE
        //

        if (sub.equals("suspendre")) {

            if (!p.hasPermission("moodbusiness.staff.suspend")) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Vous ne pouvez pas suspendre une entreprise."
                );

                return true;
            }

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Indiquez l'entreprise à suspendre."
                );

                return true;
            }

            String name =
                    String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    1,
                                    args.length
                            )
                    );

            Business business =
                    BusinessManager.getByName(name);

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Entreprise introuvable."
                );

                return true;
            }

            BusinessManager.setStatus(
                    business,
                    BusinessStatus.SUSPENDUE
            );

            AuditLogManager.log(
                    AuditLogType.BUSINESS_SUSPENDED,
                    p,
                    business.getName(),
                    business,
                    "Entreprise suspendue par l'administration."
            );

            BusinessMessages.success(
                    p,
                    "Gestion Entreprises",
                    "Entreprise suspendue: §e" + business.getName()
            );

            return true;
        }

        //
        // ✅ REACTIVER
        //

        if (sub.equals("reactiver")
                || sub.equals("réactiver")) {

            if (!p.hasPermission("moodbusiness.staff.suspend")) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Vous ne pouvez pas réactiver une entreprise."
                );

                return true;
            }

            if (args.length < 2) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Indiquez l'entreprise à réactiver."
                );

                return true;
            }

            String name =
                    String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    1,
                                    args.length
                            )
                    );

            Business business =
                    BusinessManager.getByName(name);

            if (business == null) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Entreprise introuvable."
                );

                return true;
            }

            BusinessManager.setStatus(
                    business,
                    BusinessStatus.ACTIVE
            );

            AuditLogManager.log(
                    AuditLogType.BUSINESS_REACTIVATED,
                    p,
                    business.getName(),
                    business,
                    "Entreprise réactivée par l'administration."
            );

            BusinessMessages.success(
                    p,
                    "Gestion Entreprises",
                    "Entreprise réactivée: §e" + business.getName()
            );

            return true;
        }

        BusinessMainGUI.open(p);

        return true;
    }

    private double parseDouble(
            String text
    ) {

        try {

            return Double.parseDouble(
                    text.replace(",", ".")
            );

        } catch (Exception e) {

            return -1;
        }
    }
}
