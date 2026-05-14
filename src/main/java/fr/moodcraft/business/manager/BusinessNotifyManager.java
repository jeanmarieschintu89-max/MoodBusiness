package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

public final class BusinessNotifyManager {

    private BusinessNotifyManager() {}

    public static void businessCreated(
            Player owner,
            Business business
    ) {

        if (owner == null || business == null) {
            return;
        }

        broadcast(
                "Bureau des Entreprises",
                "§a✔ §fNouvelle entreprise créée.",
                "§e➜ §7Entreprise : §e" + business.getName(),
                "§e➜ §7Dirigeant : §e" + owner.getName(),
                "§e➜ §7Elle rejoint l'économie de §aMood§6Craft",
                "§e➜ §7Menu : §e/entreprise"
        );
    }

    public static void notifyRequestCreated(
            BusinessRequest request
    ) {

        if (request == null) {
            return;
        }

        for (Player online : Bukkit.getOnlinePlayers()) {

            Business business = BusinessManager.getMemberBusiness(online.getUniqueId());

            if (business == null) {
                continue;
            }

            BusinessRole role = business.getRole(online.getUniqueId());

            if (role == null || !role.canManageContracts()) {
                continue;
            }

            send(
                    online,
                    "Bureau des Entreprises",
                    "§e➜ §7Nouvelle demande publiée.",
                    "§e➜ §7Type : " + request.getCategory().getDisplayName(),
                    "§e➜ §7Budget : §e" + VaultHook.format(request.getBudget()),
                    "§e➜ §7Délai : §b" + request.getDueDays() + " jours",
                    "§e➜ §7Les entreprises peuvent répondre",
                    "§e➜ §7Menu : §e/demandes"
            );

            online.playSound(
                    online.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    0.6f,
                    1.25f
            );

            AlertManager.add(
                    online,
                    AlertType.REQUEST,
                    "Nouvelle demande publiée",
                    "Une demande est disponible. Budget: " + VaultHook.format(request.getBudget())
            );
        }
    }

    public static void memberJoined(
            OfflinePlayer target,
            Business business,
            BusinessRole role
    ) {

        if (target == null || business == null || role == null) {
            return;
        }

        AlertManager.add(
                target,
                AlertType.BUSINESS,
                "Vous avez rejoint une entreprise",
                "Entreprise: " + business.getName() + " - Rôle: " + role.name()
        );

        if (target.isOnline() && target.getPlayer() != null) {

            Player p = target.getPlayer();

            send(
                    p,
                    "Bureau des Entreprises",
                    "§a✔ §fVous avez rejoint une entreprise.",
                    "§e➜ §7Entreprise : §e" + business.getName(),
                    "§e➜ §7Rôle : " + role.getDisplayName(),
                    "§e➜ §7Menu : §e/entreprise"
            );

            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.1f);
        }
    }

    public static void roleChanged(
            OfflinePlayer target,
            Business business,
            BusinessRole role
    ) {

        if (target == null || business == null || role == null) {
            return;
        }

        AlertManager.add(
                target,
                AlertType.BUSINESS,
                "Votre rôle a changé",
                "Entreprise: " + business.getName() + " - Nouveau rôle: " + role.name()
        );

        if (target.isOnline() && target.getPlayer() != null) {

            Player p = target.getPlayer();

            send(
                    p,
                    "Bureau des Entreprises",
                    "§a✔ §fVotre rôle a changé.",
                    "§e➜ §7Entreprise : §e" + business.getName(),
                    "§e➜ §7Nouveau rôle : " + role.getDisplayName()
            );

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.8f, 1.2f);
        }
    }

    public static void memberRemoved(
            OfflinePlayer target,
            Business business
    ) {

        if (target == null || business == null) {
            return;
        }

        AlertManager.add(
                target,
                AlertType.BUSINESS,
                "Vous avez quitté une entreprise",
                "Entreprise: " + business.getName()
        );

        if (target.isOnline() && target.getPlayer() != null) {

            Player p = target.getPlayer();

            send(
                    p,
                    "Bureau des Entreprises",
                    "§c✖ §fVous avez quitté l'entreprise.",
                    "§e➜ §7Entreprise : §e" + business.getName(),
                    "§e➜ §7Décision : §cLicenciement"
            );

            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
        }
    }

    public static void bonusPaid(
            OfflinePlayer target,
            Business business,
            double amount
    ) {

        if (target == null || business == null) {
            return;
        }

        AlertManager.add(
                target,
                AlertType.PAYROLL,
                "Prime reçue",
                "Entreprise: " + business.getName() + " - Montant: " + VaultHook.format(amount)
        );

        if (target.isOnline() && target.getPlayer() != null) {

            Player p = target.getPlayer();

            send(
                    p,
                    "Paie Entreprise",
                    "§a✔ §fPrime reçue.",
                    "§e➜ §7Entreprise : §e" + business.getName(),
                    "§e➜ §7Montant : §e" + VaultHook.format(amount)
            );

            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }
    }

    public static void payrollPaid(
            OfflinePlayer target,
            Business business,
            double amount
    ) {

        if (target == null || business == null) {
            return;
        }

        AlertManager.add(
                target,
                AlertType.PAYROLL,
                "Salaire reçu",
                "Entreprise: " + business.getName() + " - Montant: " + VaultHook.format(amount)
        );

        if (target.isOnline() && target.getPlayer() != null) {

            Player p = target.getPlayer();

            send(
                    p,
                    "Paie Entreprise",
                    "§a✔ §fSalaire reçu.",
                    "§e➜ §7Entreprise : §e" + business.getName(),
                    "§e➜ §7Montant : §e" + VaultHook.format(amount),
                    "§e➜ §7Paie mensuelle versée"
            );

            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 1.2f);
        }
    }

    public static void payrollBlocked(
            Business business,
            double required
    ) {

        if (business == null) {
            return;
        }

        OfflinePlayer owner = Bukkit.getOfflinePlayer(business.getOwnerUuid());

        AlertManager.add(
                owner,
                AlertType.PAYROLL,
                "Paie bloquée",
                "Fonds insuffisants. Requis: " + VaultHook.format(required)
        );

        if (owner.isOnline() && owner.getPlayer() != null) {

            Player p = owner.getPlayer();

            send(
                    p,
                    "Paie Entreprise",
                    "§c✖ §fPaie bloquée.",
                    "§e➜ §7Entreprise : §e" + business.getName(),
                    "§e➜ §7Requis : §e" + VaultHook.format(required),
                    "§e➜ §7Déposez de l'argent dans la banque entreprise"
            );
        }
    }

    private static void send(
            Player player,
            String title,
            String... lines
    ) {

        player.sendMessage("");
        player.sendMessage("§8----- §6✦ " + title + " ✦ §8-----");

        for (String line : lines) {
            player.sendMessage(line);
        }

        player.sendMessage("§8-----------------------------");
    }

    private static void broadcast(
            String title,
            String... lines
    ) {

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8----- §6✦ " + title + " ✦ §8-----");

        for (String line : lines) {
            Bukkit.broadcastMessage(line);
        }

        Bukkit.broadcastMessage("§8-----------------------------");
    }
}
