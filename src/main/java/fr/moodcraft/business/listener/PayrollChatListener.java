package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.manager.BusinessBankManager;
import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.PayrollManager;

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

public class PayrollChatListener
        implements Listener {

    private static final Map<UUID, Draft> DRAFTS =
            new HashMap<>();

    public static void start(
            Player p,
            Business business,
            BusinessRole role
    ) {

        if (p == null || business == null || role == null) {
            return;
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

            return;
        }

        DRAFTS.put(
                p.getUniqueId(),
                new Draft(
                        business.getId(),
                        role
                )
        );

        p.closeInventory();

        BusinessMessages.header(
                p,
                "Paie Entreprise"
        );

        p.sendMessage("§fÉcris le salaire mensuel dans le chat.");
        p.sendMessage("§7Entreprise: §e" + business.getName());
        p.sendMessage("§7Rôle: " + role.getDisplayName());
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple: §e4000");
        p.sendMessage("§8• §7Écris §e0 §7pour désactiver le salaire de ce rôle.");
        p.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

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
                    "Paie Entreprise",
                    "Configuration du salaire annulée."
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return;
        }

        double amount;

        try {

            amount =
                    Double.parseDouble(
                            message.replace(",", ".")
                    );

        } catch (Exception ex) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    "Montant invalide. Écris un nombre, exemple: §e4000"
            );

            return;
        }

        if (amount < 0) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    "Le salaire ne peut pas être négatif."
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
                    "Paie Entreprise",
                    "Entreprise introuvable."
            );

            return;
        }

        if (!BusinessBankManager.canConfigurePayroll(
                p,
                business
        )) {

            DRAFTS.remove(
                    p.getUniqueId()
            );

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    "Seul le dirigeant peut configurer les salaires."
            );

            return;
        }

        PayrollManager.PayrollResult result =
                PayrollManager.setSalary(
                        business,
                        draft.role,
                        amount
                );

        DRAFTS.remove(
                p.getUniqueId()
        );

        if (!result.success()) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    result.message()
            );

            return;
        }

        BusinessMessages.success(
                p,
                "Paie Entreprise",
                result.message()
        );

        p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );

        fr.moodcraft.business.gui.BusinessPayrollGUI.open(
                p,
                business
        );
    }

    private static class Draft {

        private final String businessId;
        private final BusinessRole role;

        private Draft(
                String businessId,
                BusinessRole role
        ) {

            this.businessId =
                    businessId;

            this.role =
                    role;
        }
    }
}