package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.gui.BusinessDashboardGUI;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BusinessCreationChatListener
        implements Listener {

    private static final Set<UUID> WAITING_NAME =
            new HashSet<>();

    public static void start(
            Player p
    ) {

        WAITING_NAME.add(
                p.getUniqueId()
        );

        p.closeInventory();

        BusinessMessages.header(
                p,
                "Bureau des Entreprises"
        );

        p.sendMessage("§fÉcris le nom de ton entreprise dans le chat.");
        p.sendMessage("§7Exemple: §eNordBuild");
        p.sendMessage("");
        p.sendMessage("§8• §7Création directe");
        p.sendMessage("§8• §7Frais progressifs: §e15 000€§7, puis §e30 000€§7...");
        p.sendMessage("§8• §7Nom vérifié automatiquement");
        p.sendMessage("");
        p.sendMessage("§7Tape §cannuler §7pour quitter.");

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

        if (!WAITING_NAME.contains(
                p.getUniqueId()
        )) {
            return;
        }

        e.setCancelled(true);

        String message =
                e.getMessage();

        Bukkit.getScheduler().runTask(
                Main.getInstance(),
                () -> handle(
                        p,
                        message
                )
        );
    }

    private void handle(
            Player p,
            String message
    ) {

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            WAITING_NAME.remove(
                    p.getUniqueId()
            );

            BusinessMessages.info(
                    p,
                    "Bureau des Entreprises",
                    "Création d'entreprise annulée."
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return;
        }

        WAITING_NAME.remove(
                p.getUniqueId()
        );

        BusinessManager.CreationResult result =
                BusinessManager.createBusiness(
                        p,
                        message
                );

        if (!result.success()) {

            BusinessMessages.deny(
                    p,
                    "Bureau des Entreprises",
                    result.message()
            );

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.85f
            );

            return;
        }

        Business business =
                result.business();

        BusinessMessages.header(
                p,
                "Bureau des Entreprises"
        );

        p.sendMessage("§fEntreprise créée avec succès.");
        p.sendMessage("§7Nom: §e" + business.getName());
        p.sendMessage("§7Frais d'enregistrement: §e" + BusinessMessages.money(business.getCreationFee()));
        p.sendMessage("§7Statut: " + business.getStatus().getDisplayName());
        p.sendMessage("");
        p.sendMessage("§a✔ Dossier inscrit au Bureau des Entreprises §aMood§6Craft§a.");

        BusinessMessages.footer(p);

        p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );

        BusinessDashboardGUI.open(
                p,
                business
        );
    }
}