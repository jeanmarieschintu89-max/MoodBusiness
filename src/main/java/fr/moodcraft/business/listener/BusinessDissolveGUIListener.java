package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessDashboardGUI;
import fr.moodcraft.business.gui.BusinessDissolveConfirmGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;

import fr.moodcraft.business.manager.BusinessDissolveManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class BusinessDissolveGUIListener
        implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!title.equals(BusinessDashboardGUI.TITLE)
                && !title.equals(BusinessDissolveConfirmGUI.TITLE)) {
            return;
        }

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }

        ItemStack item =
                e.getCurrentItem();

        String action =
                ItemBuilder.getAction(item);

        String target =
                ItemBuilder.getTarget(item);

        if (action == null) {
            return;
        }

        if (!action.equals("dashboard_dissolve")
                && !action.equals("dissolve_cancel")
                && !action.equals("dissolve_confirm")) {
            return;
        }

        Business business =
                BusinessManager.getById(target);

        if (business == null) {

            BusinessMessages.deny(
                    p,
                    "Dissolution Entreprise",
                    "Entreprise introuvable."
            );

            p.playSound(
                    p.getLocation(),
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.85f
            );

            return;
        }

        //
        // 🧨 OUVRIR CONFIRMATION
        //

        if (action.equals("dashboard_dissolve")) {

            if (!business.isOwner(
                    p.getUniqueId()
            )) {

                BusinessMessages.deny(
                        p,
                        "Dissolution Entreprise",
                        "Seul le dirigeant peut dissoudre l’entreprise."
                );

                p.playSound(
                        p.getLocation(),
                        Sound.ENTITY_VILLAGER_NO,
                        1f,
                        0.85f
                );

                return;
            }

            BusinessDissolveConfirmGUI.open(
                    p,
                    business
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return;
        }

        //
        // ❌ ANNULER
        //

        if (action.equals("dissolve_cancel")) {

            BusinessDashboardGUI.open(
                    p,
                    business
            );

            p.playSound(
                    p.getLocation(),
                    Sound.UI_BUTTON_CLICK,
                    0.8f,
                    1.2f
            );

            return;
        }

        //
        // ✅ CONFIRMER
        //

        if (action.equals("dissolve_confirm")) {

            BusinessDissolveManager.DissolveResult result =
                    BusinessDissolveManager.dissolve(
                            p,
                            business
                    );

            p.closeInventory();

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Dissolution Entreprise",
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

            BusinessMessages.success(
                    p,
                    "Dissolution Entreprise",
                    result.message()
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_BEACON_DEACTIVATE,
                    0.8f,
                    0.8f
            );

            BusinessMainGUI.open(p);
        }
    }
}