package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.BusinessMessages;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContractChatListener implements Listener {

    private static final Map<UUID, Draft> drafts =
            new HashMap<>();

    public static void startComplete(
            Player player,
            Contract contract
    ) {

        drafts.put(
                player.getUniqueId(),
                new Draft(
                        contract.getId(),
                        Mode.COMPLETE
                )
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                "Contrat Officiel"
        );

        player.sendMessage("§fÉcris un commentaire de fin.");
        player.sendMessage("§7Exemple: Construction terminée avec intérieur complet.");
        player.sendMessage("§7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(player);
    }

    public static void startLitige(
            Player player,
            Contract contract
    ) {

        drafts.put(
                player.getUniqueId(),
                new Draft(
                        contract.getId(),
                        Mode.LITIGE
                )
        );

        player.closeInventory();

        BusinessMessages.header(
                player,
                "Litige Économique"
        );

        player.sendMessage("§fExplique la raison du litige.");
        player.sendMessage("§7Les fonds resteront bloqués.");
        player.sendMessage("§7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(player);
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player player =
                e.getPlayer();

        Draft draft =
                drafts.get(
                        player.getUniqueId()
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
                        player,
                        draft,
                        message
                )
        );
    }

    private void handle(
            Player player,
            Draft draft,
            String message
    ) {

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            drafts.remove(
                    player.getUniqueId()
            );

            BusinessMessages.info(
                    player,
                    "Contrat Officiel",
                    "Saisie annulée."
            );

            return;
        }

        Contract contract =
                ContractManager.get(
                        draft.contractId
                );

        drafts.remove(
                player.getUniqueId()
        );

        if (contract == null) {

            BusinessMessages.deny(
                    player,
                    "Contrat Officiel",
                    "Contrat introuvable."
            );

            return;
        }

        ContractManager.ContractResult result;

        if (draft.mode == Mode.COMPLETE) {

            result =
                    ContractManager.complete(
                            player,
                            contract,
                            message
                    );

        } else {

            result =
                    ContractManager.openLitige(
                            player,
                            contract,
                            message
                    );
        }

        if (!result.success()) {

            BusinessMessages.deny(
                    player,
                    draft.mode == Mode.COMPLETE
                            ? "Contrat Officiel"
                            : "Litige Économique",
                    result.message()
            );

            return;
        }

        BusinessMessages.success(
                player,
                draft.mode == Mode.COMPLETE
                        ? "Contrat Officiel"
                        : "Litige Économique",
                result.message()
        );
    }

    private enum Mode {

        COMPLETE,
        LITIGE
    }

    private static class Draft {

        private final String contractId;
        private final Mode mode;

        private Draft(
                String contractId,
                Mode mode
        ) {

            this.contractId = contractId;
            this.mode = mode;
        }
    }
}