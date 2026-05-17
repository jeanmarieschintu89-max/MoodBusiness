package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;
import fr.moodcraft.business.gui.BusinessEmployeesGUI;
import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.VaultHook;
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

public class RecruitmentChatListener implements Listener {

    private static final Map<UUID, Draft> DRAFTS = new HashMap<>();
    private static final Map<UUID, Invite> INVITES = new HashMap<>();
    private static final long TIMEOUT_TICKS = 20L * 60L;
    private static final long INVITE_TIMEOUT_MS = 5L * 60L * 1000L;

    public static void start(Player p, Business business) {
        if (p == null || business == null) return;
        BusinessCreationChatListener.cancel(p);

        if (!BusinessManager.canManageRoles(p, business)) {
            BusinessMessages.deny(p, "Équipe Entreprise", "Votre rôle ne permet pas de recruter.");
            return;
        }

        Draft draft = new Draft(business.getId());
        DRAFTS.put(p.getUniqueId(), draft);
        p.closeInventory();

        BusinessMessages.header(p, "Équipe Entreprise");
        p.sendMessage("§e➜ §fÉcris le pseudo du joueur connecté.");
        p.sendMessage("");
        p.sendMessage("§8• §7Entreprise : §e" + business.getName());
        p.sendMessage("§8• §7Le joueur devra accepter l'invitation.");
        p.sendMessage("§8• §7Exemple : §eSteven2621");
        p.sendMessage("§8• §7Tape §cannuler §7pour quitter");
        p.sendMessage("§8• §7Annulation auto dans §e60 secondes");
        BusinessMessages.footer(p);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            Draft current = DRAFTS.get(p.getUniqueId());
            if (current == null || current != draft) return;
            DRAFTS.remove(p.getUniqueId());
            if (!p.isOnline()) return;
            BusinessMessages.info(p, "Équipe Entreprise", "Recrutement annulé : temps écoulé.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
        }, TIMEOUT_TICKS);
    }

    public static void cancel(Player p) {
        if (p == null) return;
        DRAFTS.remove(p.getUniqueId());
    }

    public static boolean isWaiting(Player p) {
        return p != null && DRAFTS.containsKey(p.getUniqueId());
    }

    public static boolean acceptInvite(Player player) {
        if (player == null) return false;
        Invite invite = INVITES.remove(player.getUniqueId());
        if (invite == null) {
            BusinessMessages.deny(player, "Invitation entreprise", "Vous n'avez aucune invitation en attente.");
            return true;
        }
        if (System.currentTimeMillis() - invite.createdAt() > INVITE_TIMEOUT_MS) {
            BusinessMessages.deny(player, "Invitation entreprise", "Cette invitation a expiré.");
            return true;
        }

        Business business = BusinessManager.getById(invite.businessId());
        if (business == null || !business.isActive()) {
            BusinessMessages.deny(player, "Invitation entreprise", "Cette entreprise n'est plus disponible.");
            return true;
        }
        if (business.isMember(player.getUniqueId())) {
            BusinessMessages.deny(player, "Invitation entreprise", "Vous êtes déjà dans cette entreprise.");
            return true;
        }

        business.addMember(player.getUniqueId(), player.getName(), invite.role());
        business.setMemberPay(player.getUniqueId(), invite.pay());
        BusinessStorage.save();

        BusinessMessages.success(player, "Invitation entreprise",
                "Vous avez rejoint §e" + business.getName() + "§f.",
                "§8• §7Rôle : " + invite.role().getDisplayName(),
                "§8• §7Paye prévue : §e" + VaultHook.format(invite.pay()));
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.1f);

        Player recruiter = Bukkit.getPlayer(invite.recruiterUuid());
        if (recruiter != null && recruiter.isOnline()) {
            BusinessMessages.success(recruiter, "Équipe Entreprise",
                    "§e" + player.getName() + " §fa accepté l'invitation.",
                    "§8• §7Rôle : " + invite.role().getDisplayName(),
                    "§8• §7Paye : §e" + VaultHook.format(invite.pay()));
            recruiter.playSound(recruiter.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1.2f);
        }
        return true;
    }

    public static boolean refuseInvite(Player player) {
        if (player == null) return false;
        Invite invite = INVITES.remove(player.getUniqueId());
        if (invite == null) {
            BusinessMessages.deny(player, "Invitation entreprise", "Vous n'avez aucune invitation en attente.");
            return true;
        }
        BusinessMessages.info(player, "Invitation entreprise", "Invitation refusée.");
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 0.9f);

        Player recruiter = Bukkit.getPlayer(invite.recruiterUuid());
        if (recruiter != null && recruiter.isOnline()) {
            BusinessMessages.info(recruiter, "Équipe Entreprise", player.getName() + " a refusé l'invitation.");
        }
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        DRAFTS.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (BusinessCreationChatListener.isWaiting(p)) return;
        Draft draft = DRAFTS.get(p.getUniqueId());
        if (draft == null) return;

        e.setCancelled(true);
        String message = e.getMessage();
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> handle(p, draft, message));
    }

    private void handle(Player p, Draft draft, String message) {
        if (message.equalsIgnoreCase("annuler") || message.equalsIgnoreCase("cancel")) {
            DRAFTS.remove(p.getUniqueId());
            BusinessMessages.info(p, "Équipe Entreprise", "Recrutement annulé.");
            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
            return;
        }

        Business business = BusinessManager.getById(draft.businessId);
        if (business == null) {
            DRAFTS.remove(p.getUniqueId());
            BusinessMessages.deny(p, "Équipe Entreprise", "Entreprise introuvable.");
            return;
        }
        if (!BusinessManager.canManageRoles(p, business)) {
            DRAFTS.remove(p.getUniqueId());
            BusinessMessages.deny(p, "Équipe Entreprise", "Votre rôle ne permet plus de recruter.");
            return;
        }

        if (draft.step == 0) {
            Player target = Bukkit.getPlayerExact(message.trim());
            if (target == null || !target.isOnline()) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Ce joueur doit être connecté.");
                return;
            }
            if (target.getUniqueId().equals(p.getUniqueId())) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Vous ne pouvez pas vous inviter vous-même.");
                return;
            }
            if (business.isMember(target.getUniqueId())) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Ce joueur fait déjà partie de l'entreprise.");
                return;
            }

            draft.targetUuid = target.getUniqueId();
            draft.targetName = target.getName();
            draft.step = 1;
            BusinessMessages.header(p, "Équipe Entreprise");
            p.sendMessage("§e➜ §fÉcris le rôle proposé.");
            p.sendMessage("");
            p.sendMessage("§8• §7Joueur : §e" + draft.targetName);
            p.sendMessage("§8• §7Rôles : §eEmploye §7ou §eGerant");
            p.sendMessage("§8• §7Tape §cannuler §7pour quitter.");
            BusinessMessages.footer(p);
            return;
        }

        if (draft.step == 1) {
            BusinessRole role = BusinessRole.fromText(message.trim());
            if (role == null) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Rôle inconnu. Exemple : §eemploye§7 ou §egerant§7.");
                return;
            }
            if (role != BusinessRole.EMPLOYE && role != BusinessRole.GERANT) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Rôle simplifié : utilisez §eemploye §7ou §egerant§7.");
                return;
            }
            if (role == BusinessRole.GERANT && business.getRole(p.getUniqueId()) != BusinessRole.DIRIGEANT) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Seul le dirigeant peut proposer le rôle gérant.");
                return;
            }
            draft.role = role;
            draft.step = 2;
            BusinessMessages.header(p, "Équipe Entreprise");
            p.sendMessage("§e➜ §fÉcris la paye prévue pour ce membre.");
            p.sendMessage("");
            p.sendMessage("§8• §7Joueur : §e" + draft.targetName);
            p.sendMessage("§8• §7Rôle : " + draft.role.getDisplayName());
            p.sendMessage("§8• §7Exemple : §e5000");
            p.sendMessage("§8• §7Écris §e0 §7si aucune paye fixe n'est prévue.");
            BusinessMessages.footer(p);
            return;
        }

        if (draft.step == 2) {
            double pay;
            try {
                pay = Double.parseDouble(message.trim().replace(",", "."));
            } catch (Exception e) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Montant invalide. Exemple : §e5000§7.");
                return;
            }
            if (pay < 0) {
                BusinessMessages.deny(p, "Équipe Entreprise", "La paye ne peut pas être négative.");
                return;
            }

            Player target = Bukkit.getPlayer(draft.targetUuid);
            if (target == null || !target.isOnline()) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Le joueur n'est plus connecté.");
                return;
            }

            INVITES.put(target.getUniqueId(), new Invite(business.getId(), p.getUniqueId(), p.getName(), target.getUniqueId(), target.getName(), draft.role, pay, System.currentTimeMillis()));
            DRAFTS.remove(p.getUniqueId());

            sendInvite(target, business, p, draft.role, pay);
            BusinessMessages.success(p, "Équipe Entreprise",
                    "Invitation envoyée à §e" + target.getName() + "§f.",
                    "§8• §7Rôle proposé : " + draft.role.getDisplayName(),
                    "§8• §7Paye prévue : §e" + VaultHook.format(pay));
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
            BusinessEmployeesGUI.open(p, business);
        }
    }

    private void sendInvite(Player target, Business business, Player recruiter, BusinessRole role, double pay) {
        BusinessMessages.header(target, "Invitation entreprise");
        target.sendMessage("§e➜ §fL'entreprise §6" + business.getName() + " §fveut vous recruter.");
        target.sendMessage("§8• §7Proposé par : §e" + recruiter.getName());
        target.sendMessage("§8• §7Rôle proposé : " + role.getDisplayName());
        target.sendMessage("§8• §7Paye prévue : §e" + VaultHook.format(pay));
        target.sendMessage("§8• §7Salaires automatiques : §cDésactivés");
        target.sendMessage("");
        target.sendMessage("§a/entreprise accepter §7pour rejoindre");
        target.sendMessage("§c/entreprise refuser §7pour refuser");
        target.sendMessage("§8• §7Expiration : §e5 minutes");
        BusinessMessages.footer(target);
        target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.9f, 1.2f);
    }

    private static class Draft {
        private final String businessId;
        private int step = 0;
        private UUID targetUuid;
        private String targetName;
        private BusinessRole role;
        private Draft(String businessId) { this.businessId = businessId; }
    }

    private record Invite(String businessId, UUID recruiterUuid, String recruiterName, UUID targetUuid, String targetName, BusinessRole role, double pay, long createdAt) {}
}
