package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.BusinessEmployeesGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.BusinessStaffGUI;

import fr.moodcraft.business.manager.BusinessManager;

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

        if (sub.equals("staff")) {

            if (!p.hasPermission("moodbusiness.staff")) {

                BusinessMessages.deny(
                        p,
                        "Gestion Entreprises",
                        "Accès réservé à l'administration économique."
                );

                return true;
            }

            BusinessStaffGUI.open(p);

            return true;
        }

        if (sub.equals("creer")
                || sub.equals("créer")
                || sub.equals("create")) {

            if (args.length < 2) {

                BusinessMessages.header(
                        p,
                        "Registre Économique"
                );

                p.sendMessage("§7Utilisation: §e/entreprise creer <nom>");
                p.sendMessage("§8Exemple: §e/entreprise creer NordBuild");

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
                        "Registre Économique",
                        result.message()
                );

                return true;
            }

            Business business =
                    result.business();

            BusinessMessages.header(
                    p,
                    "Registre Économique"
            );

            p.sendMessage("§fEntreprise créée avec succès.");
            p.sendMessage("§7Nom: §e" + business.getName());
            p.sendMessage(
                    "§7Frais d'enregistrement: §e"
                            + BusinessMessages.money(
                                    business.getCreationFee()
                            )
            );
            p.sendMessage(
                    "§7Statut: "
                            + business.getStatus().getDisplayName()
            );
            p.sendMessage(
                    "§a✔ Dossier inscrit au registre §aMood§6Craft§a."
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
                        "Utilisation: /entreprise recruter <joueur> [role]"
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
                        "Rôle inconnu. Exemple: stagiaire, apprenti, employe, tresorier."
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
                        "Utilisation: /entreprise role <joueur> <role>"
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
                        "Utilisation: /entreprise renvoyer <joueur>"
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
                        "Utilisation: /entreprise suspendre <nom>"
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

            BusinessMessages.success(
                    p,
                    "Gestion Entreprises",
                    "Entreprise suspendue: §e" + business.getName()
            );

            return true;
        }

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
                        "Utilisation: /entreprise reactiver <nom>"
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
}