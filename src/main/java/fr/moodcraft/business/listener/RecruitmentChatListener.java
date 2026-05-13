package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.gui.BusinessEmployeesGUI;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecruitmentChatListener
        implements Listener {

    private static final Map<UUID, Draft> DRAFTS =
            new HashMap<>();

    public static void start(
            Player p,
            Business business
    ) {

        if (p == null || business == null) {
            return;
        }

        if (!BusinessManager.canManageRoles(
                p,
                business
        )) {

            BusinessMessages.deny(
                    p,
                    "Employés Entreprise",
                    "Votre rôle ne permet pas de recruter."
            );

            return;
        }

        DRAFTS.put(
                p.getUniqueId(),
                new Draft(
                        business.getId()
                )
        );

        p.closeInventory();

        BusinessMessages.header(
                p,
                "Employés Entreprise"
        );

        p.sendMessage("§fÉcris le pseudo du joueur connecté.");
        p.sendMessage("");
        p.sendMessage("§7Entreprise: §e" + business.getName());
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple: §eSteven2621");
        p.sendMessage("§8• §7Le joueur doit être connecté");
        p.sendMessage("§8• §7Tape §cannuler §7pour quitter");

        BusinessMessages.footer(p);

        p.playSound(
                p.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.8f,
                1.2f
        );
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player p =
                e.getPlayer();

        Draft draft =
                DRAFTS.get(
                        p.getUniqueId()
                );

        if (draft == null) {
            return;
        }

        e.setCancelled(true);

        String message =
                e.getMessage();

        Bukkit.getScheduler().runTask(
                Main.getInstance(),
                () -> handle(
                        p,
                        draft,
                        message
                )
        );
    }

    private void handle(
            Player p,
            Draft draft,
            String message
    ) {

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            DRAFTS.remove(
                    p.getUniqueId()
            );

            BusinessMessages.info(
                    p,
                    "Employés Entreprise",
                    "Recrutement annulé."
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return;
        }

        Business business =
                BusinessManager.getById(
                        draft.businessId
                );

        if (business == null) {

            DRAFTS.remove(
                    p.getUniqueId()
            );

            BusinessMessages.deny(
                    p,
                    "Employés Entreprise",
                    "Entreprise introuvable."
            );

            return;
        }

        if (!BusinessManager.canManageRoles(
                p,
                business
        )) {

            DRAFTS.remove(
                    p.getUniqueId()
            );

            BusinessMessages.deny(
                    p,
                    "Employés Entreprise",
                    "Votre rôle ne permet plus de recruter."
            );

            return;
        }

        if (draft.step == 0) {

            Player target =
                    Bukkit.getPlayerExact(
                            message.trim()
                    );

            if (target == null || !target.isOnline()) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Ce joueur doit être connecté."
                );

                return;
            }

            if (business.isMember(
                    target.getUniqueId()
            )) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Ce joueur fait déjà partie de l'entreprise."
                );

                return;
            }

            draft.targetUuid =
                    target.getUniqueId();

            draft.targetName =
                    target.getName();

            draft.step =
                    1;

            BusinessMessages.header(
                    p,
                    "Employés Entreprise"
            );

            p.sendMessage("§fÉcris le rôle à donner.");
            p.sendMessage("");
            p.sendMessage("§7Joueur: §e" + draft.targetName);
            p.sendMessage("");
            p.sendMessage("§8• §eStagiaire");
            p.sendMessage("§8• §eApprenti");
            p.sendMessage("§8• §eEmploye");
            p.sendMessage("§8• §eTresorier");
            p.sendMessage("§8• §eResponsable");
            p.sendMessage("§8• §eGerant §7(dirigeant uniquement)");
            p.sendMessage("");
            p.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(p);

            return;
        }

        if (draft.step == 1) {

            BusinessRole role =
                    BusinessRole.fromText(
                            message.trim()
                    );

            if (role == null) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Rôle inconnu. Exemple: §eemploye§7."
                );

                return;
            }

            if (role == BusinessRole.DIRIGEANT) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Le rôle dirigeant ne peut pas être donné ici."
                );

                return;
            }

            BusinessRole actorRole =
                    business.getRole(
                            p.getUniqueId()
                    );

            if (actorRole == BusinessRole.GERANT
                    && role == BusinessRole.GERANT) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Un gérant ne peut pas nommer un autre gérant."
                );

                return;
            }

            Player target =
                    Bukkit.getPlayer(
                            draft.targetUuid
                    );

            if (target == null || !target.isOnline()) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        "Le joueur n'est plus connecté."
                );

                return;
            }

            BusinessManager.ActionResult result =
                    BusinessManager.addMember(
                            p,
                            business,
                            target,
                            role
                    );

            DRAFTS.remove(
                    p.getUniqueId()
            );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Employés Entreprise",
                        result.message()
                );

                return;
            }

            BusinessMessages.success(
                    p,
                    "Employés Entreprise",
                    result.message()
            );

            BusinessMessages.header(
                    target,
                    "Employés Entreprise"
            );

            target.sendMessage("§a✔ §fVous avez rejoint une entreprise.");
            target.sendMessage("");
            target.sendMessage("§7Entreprise: §e" + business.getName());
            target.sendMessage("§7Rôle: " + role.getDisplayName());
            target.sendMessage("");
            BusinessMessages.line(
                    target,
                    "Ouvrez /entreprise pour voir votre espace"
            );

            BusinessMessages.footer(target);

            target.playSound(
                    target.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    0.8f,
                    1.1f
            );

            p.playSound(
                    p.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    0.8f,
                    1.1f
            );

            BusinessEmployeesGUI.open(
                    p,
                    business
            );
        }
    }

    private static class Draft {

        private final String businessId;

        private int step = 0;

        private UUID targetUuid;
        private String targetName;

        private Draft(
                String businessId
        ) {

            this.businessId =
                    businessId;
        }
    }
}