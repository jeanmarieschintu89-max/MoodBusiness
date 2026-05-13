package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

public final class BusinessNotifyManager {

    private BusinessNotifyManager() {}

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

            sendRequestMessage(
                    online,
                    request
            );

            AlertManager.add(
                    online,
                    AlertType.REQUEST,
                    "Nouvelle demande publiée",
                    "Une demande "
                            + request.getCategory().getDisplayName()
                            + " est disponible. Budget: "
                            + VaultHook.format(request.getBudget())
            );
        }
    }

    //
    // 💬 MESSAGE DEMANDE
    //

    private static void sendRequestMessage(
            Player player,
            BusinessRequest request
    ) {

        player.sendMessage("");
        player.sendMessage("§8----- §6✦ Bureau des Entreprises ✦ §8-----");
        player.sendMessage("");
        player.sendMessage("§fNouvelle demande publiée.");
        player.sendMessage("");
        player.sendMessage("§7Type: " + request.getCategory().getDisplayName());
        player.sendMessage("§7Budget: §e" + VaultHook.format(request.getBudget()));
        player.sendMessage("§7Délai: §b" + request.getDueDays() + " jours");
        player.sendMessage("");
        player.sendMessage("§8• §7Les entreprises peuvent répondre");
        player.sendMessage("§8• §7Menu: §e/demandes");
        player.sendMessage("");
        player.sendMessage("§8-----------------------------");
        player.sendMessage("");

        player.playSound(
                player.getLocation(),
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                0.6f,
                1.25f
        );
    }
}