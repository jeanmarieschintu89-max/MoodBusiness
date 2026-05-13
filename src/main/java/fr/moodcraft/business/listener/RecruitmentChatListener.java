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
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RecruitmentChatListener
        implements Listener {

    private static final Map<UUID, Draft> DRAFTS =
            new HashMap<>();

    //
    // ⏳ 60 secondes avant annulation auto
    //

    private static final long TIMEOUT_TICKS =
            20L * 60L;

    public static void start(
            Player p,
            Business business
    ) {

        if (p == null || business == null) {
            return;
        }

        //
        // 🔒 Nettoie les autres saisies chat
        //

        BusinessCreationChatListener.cancel(p);

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

        Draft draft =
                new Draft(
                        business.getId()
                );

        DRAFTS.put(
                p.getUniqueId(),
                draft
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
        p.sendMessage("§8• §7Annulation auto dans §e60 secondes");

        BusinessMessages.footer(p);

        p.playSound(
                p.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.8f,
                1.2f
        );

        //
        // ⏳ Annulation automatique si le joueur ne répond pas
        //

        Bukkit.getScheduler().runTaskLater(
                Main.getInstance(),
                () -> {

                    Draft current =
                            DRAFTS.get(
                                    p.getUniqueId()
                            );

                    if (current == null) {
                        return;
                    }

                    if (current != draft) {
                        return;
                    }

                    DRAFTS.remove(
                            p.getUniqueId()
                    );

                    if (!p.isOnline()) {
                        return;
                    }

                    BusinessMessages.info(
                            p,
                            "Employés Entreprise",
                            "Recrutement annulé : temps écoulé."
                    );

                    p.playSound(
                            p.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_BASS,
                            0.8f,
                            0.8f
                    );
                },
                TIMEOUT_TICKS
        );
    }

    public static void cancel(
            Player p
    ) {

        if (p == null) {
            return;
        }

        DRAFTS.remove(
                p.getUniqueId()
        );
    }

    public static boolean isWaiting(
            Player p
    ) {

        return p != null
                && DRAFTS.containsKey(
                p.getUniqueId()
        );
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent e
    ) {

        DRAFTS.remove(
                e.getPlayer().getUniqueId()
        );
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player p =
                e.getPlayer();

        //
        // Si une création d'entreprise est active,
        // on laisse l'autre listener gérer le message.
        //

        if (BusinessCreationChatListener.isWaiting(p)) {
            return;
        }

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

        //
        // ÉTAPE 1 : PSEUDO
        //

        if (draft.step == 0) {

            Player target =
                    Bukkit.getPlayerExact(
                            message.trim()
                    );

            if (target == null || !target.isOnline()) {

                BusinessMessages.header(
                        p,
                        "Employés Entreprise"
                );

                p.sendMessage("§c✘ §fJoueur introuvable.");
                p.sendMessage("");
                p.sendMessage("§7Ce joueur doit être connecté.");
                p.sendMessage("");
                p.sendMessage("§8• §7Écris un autre pseudo");
                p.sendMessage("§8• §7ou tape §cannuler §7pour quitter");

                BusinessMessages.footer(p);

                p.playSound(
                        p.getLocation(),
                        Sound.ENTITY_VILLAGER_NO,
                        1f,
                        0.85f
                );

                return;
            }

            if (business.isMember(
                    target.getUniqueId()
            )) {

                BusinessMessages.header(
                        p,
                        "Employés Entreprise"
                );

                p.sendMessage("§c✘ §fJoueur déjà membre.");
                p.sendMessage("");
                p.sendMessage("§7Ce joueur fait déjà");
                p.sendMessage("§7partie de l'entreprise.");
                p.sendMessage("");
                p.sendMessage("§8• §7Écris un autre pseudo");
                p.sendMessage("§8• §7ou tape §cannuler §7pour quitter");

                BusinessMessages.footer(p);

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
            p.sendMessage("§8• §eGerant §7dirigeant uniquement");
            p.sendMessage("");
            p.sendMessage("§7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(p);

            return;
        }

        //
        // ÉTAPE 2 : RÔLE
        //

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