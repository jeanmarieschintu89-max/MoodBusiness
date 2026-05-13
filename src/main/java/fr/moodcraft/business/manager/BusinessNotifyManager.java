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

    //
    // 🏢 CRÉATION ENTREPRISE
    //

    public static void businessCreated(
            Player owner,
            Business business
    ) {

        if (owner == null || business == null) {
            return;
        }

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§a✔ §fNouvelle entreprise créée.");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7Entreprise: §e" + business.getName());
        Bukkit.broadcastMessage("§7Dirigeant: §e" + owner.getName());
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8• §7Elle rejoint l'économie de §aMood§6Craft");
        Bukkit.broadcastMessage("§8• §7Menu: §e/entreprise");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§8-----------------------------");
        Bukkit.broadcastMessage("");
    }

    //
    // 📋 NOUVELLE DEMANDE PUBLIÉE
    //

    public static void notifyRequestCreated(
            BusinessRequest request
    ) {

        if (request == null) {
            return;
        }

        for (Player online :
                Bukkit.getOnlinePlayers()) {

            Business business =
                    BusinessManager.getMemberBusiness(
                            online.getUniqueId()
                    );

            if (business == null) {
                continue;
            }

            BusinessRole role =
                    business.getRole(
                            online.getUniqueId()
                    );

            if (role == null
                    || !role.canManageContracts()) {
                continue;
            }

            online.sendMessage("");
            online.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
            online.sendMessage("");
            online.sendMessage("§fNouvelle demande publiée.");
            online.sendMessage("");
            online.sendMessage("§7Type: " + request.getCategory().getDisplayName());
            online.sendMessage("§7Budget: §e" + VaultHook.format(request.getBudget()));
            online.sendMessage("§7Délai: §b" + request.getDueDays() + " jours");
            online.sendMessage("");
            online.sendMessage("§8• §7Les entreprises peuvent répondre");
            online.sendMessage("§8• §7Menu: §e/demandes");
            online.sendMessage("");
            online.sendMessage("§8-----------------------------");
            online.sendMessage("");

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
                    "Une demande est disponible. Budget: "
                            + VaultHook.format(request.getBudget())
            );
        }
    }

    //
    // 👥 MEMBRE AJOUTÉ
    //

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
                "Entreprise: "
                        + business.getName()
                        + " - Rôle: "
                        + role.name()
        );

        if (target.isOnline()
                && target.getPlayer() != null) {

            Player p =
                    target.getPlayer();

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§a✔ §fVous avez rejoint une entreprise.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + business.getName());
            p.sendMessage("§7Rôle: " + role.getDisplayName());
            p.sendMessage("");
            p.sendMessage("§8• §7Menu: §e/entreprise");
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.UI_TOAST_CHALLENGE_COMPLETE,
                    0.8f,
                    1.1f
            );
        }
    }

    //
    // 🏷 RÔLE MODIFIÉ
    //

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
                "Entreprise: "
                        + business.getName()
                        + " - Nouveau rôle: "
                        + role.name()
        );

        if (target.isOnline()
                && target.getPlayer() != null) {

            Player p =
                    target.getPlayer();

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§a✔ §fVotre rôle a changé.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + business.getName());
            p.sendMessage("§7Nouveau rôle: " + role.getDisplayName());
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    0.8f,
                    1.2f
            );
        }
    }

    //
    // ❌ MEMBRE RETIRÉ
    //

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

        if (target.isOnline()
                && target.getPlayer() != null) {

            Player p =
                    target.getPlayer();

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§c✘ §fVous avez quitté l'entreprise.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + business.getName());
            p.sendMessage("§7Décision: §cLicenciement");
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );
        }
    }

    //
    // 🎁 PRIME REÇUE
    //

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
                "Entreprise: "
                        + business.getName()
                        + " - Montant: "
                        + VaultHook.format(amount)
        );

        if (target.isOnline()
                && target.getPlayer() != null) {

            Player p =
                    target.getPlayer();

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Paie Entreprise ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§a✔ §fPrime reçue.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + business.getName());
            p.sendMessage("§7Montant: §e" + VaultHook.format(amount));
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    0.8f,
                    1.2f
            );
        }
    }

    //
    // 💰 SALAIRE REÇU
    //

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
                "Entreprise: "
                        + business.getName()
                        + " - Montant: "
                        + VaultHook.format(amount)
        );

        if (target.isOnline()
                && target.getPlayer() != null) {

            Player p =
                    target.getPlayer();

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Paie Entreprise ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§a✔ §fSalaire reçu.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + business.getName());
            p.sendMessage("§7Montant: §e" + VaultHook.format(amount));
            p.sendMessage("");
            p.sendMessage("§8• §7Paie mensuelle versée");
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    0.8f,
                    1.2f
            );
        }
    }

    //
    // ⚠ PAIE BLOQUÉE
    //

    public static void payrollBlocked(
            Business business,
            double required
    ) {

        if (business == null) {
            return;
        }

        OfflinePlayer owner =
                Bukkit.getOfflinePlayer(
                        business.getOwnerUuid()
                );

        AlertManager.add(
                owner,
                AlertType.PAYROLL,
                "Paie bloquée",
                "Fonds insuffisants. Requis: "
                        + VaultHook.format(required)
        );

        if (owner.isOnline()
                && owner.getPlayer() != null) {

            Player p =
                    owner.getPlayer();

            p.sendMessage("");
            p.sendMessage("§8----- §6✦ Paie Entreprise ✦ §8-----");
            p.sendMessage("");
            p.sendMessage("§c✘ §fPaie bloquée.");
            p.sendMessage("");
            p.sendMessage("§7Entreprise: §e" + business.getName());
            p.sendMessage("§7Requis: §e" + VaultHook.format(required));
            p.sendMessage("");
            p.sendMessage("§8• §7Déposez de l'argent");
            p.sendMessage("§8• §7dans la banque entreprise");
            p.sendMessage("");
            p.sendMessage("§8-----------------------------");
            p.sendMessage("");
        }
    }
}