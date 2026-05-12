package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessAdminManageGUI;
import fr.moodcraft.business.gui.BusinessListGUI;
import fr.moodcraft.business.gui.BusinessStaffGUI;

import fr.moodcraft.business.manager.BusinessDissolveManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessStatus;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

public class BusinessAdminGUIListener
        implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!title.equals(BusinessListGUI.TITLE_ACTIVE)
                && !title.equals(BusinessListGUI.TITLE_RECENT)
                && !title.equals(BusinessListGUI.TITLE_SUSPENDED)
                && !title.equals(BusinessAdminManageGUI.TITLE)) {
            return;
        }

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

        if (!isAdminAction(action)) {
            return;
        }

        e.setCancelled(true);

        if (!p.hasPermission("moodbusiness.staff")) {

            BusinessMessages.deny(
                    p,
                    "Gestion Entreprises",
                    "Accès réservé à l'administration économique."
            );

            return;
        }

        if (action.equals("open_staff")) {

            BusinessStaffGUI.open(p);

            return;
        }

        Business business =
                BusinessManager.getById(target);

        if (business == null) {

            BusinessMessages.deny(
                    p,
                    "Gestion Entreprises",
                    "Entreprise introuvable."
            );

            return;
        }

        switch (action) {

            case "admin_manage_business" -> {

                BusinessAdminManageGUI.open(
                        p,
                        business
                );
            }

            case "admin_info_business" -> {

                p.closeInventory();

                BusinessMessages.businessInfo(
                        p,
                        business
                );
            }

            case "admin_suspend_business" -> {

                if (!p.hasPermission("moodbusiness.staff.suspend")) {

                    BusinessMessages.deny(
                            p,
                            "Gestion Entreprises",
                            "Vous ne pouvez pas suspendre une entreprise."
                    );

                    return;
                }

                BusinessManager.setStatus(
                        business,
                        BusinessStatus.SUSPENDUE
                );

                BusinessMessages.success(
                        p,
                        "Gestion Entreprises",
                        "Entreprise suspendue: §e" + business.getName()
                );

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_BEACON_DEACTIVATE,
                        0.8f,
                        0.9f
                );

                BusinessAdminManageGUI.open(
                        p,
                        business
                );
            }

            case "admin_reactivate_business" -> {

                if (!p.hasPermission("moodbusiness.staff.suspend")) {

                    BusinessMessages.deny(
                            p,
                            "Gestion Entreprises",
                            "Vous ne pouvez pas réactiver une entreprise."
                    );

                    return;
                }

                BusinessManager.setStatus(
                        business,
                        BusinessStatus.ACTIVE
                );

                BusinessMessages.success(
                        p,
                        "Gestion Entreprises",
                        "Entreprise réactivée: §e" + business.getName()
                );

                p.playSound(
                        p.getLocation(),
                        Sound.BLOCK_BEACON_ACTIVATE,
                        0.8f,
                        1.1f
                );

                BusinessAdminManageGUI.open(
                        p,
                        business
                );
            }

            case "admin_dissolve_business" -> {

                if (!p.hasPermission("moodbusiness.staff.suspend")) {

                    BusinessMessages.deny(
                            p,
                            "Gestion Entreprises",
                            "Vous ne pouvez pas archiver une entreprise."
                    );

                    return;
                }

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
            }

            default -> {}
        }
    }

    private boolean isAdminAction(
            String action
    ) {

        return action.equals("admin_manage_business")
                || action.equals("admin_info_business")
                || action.equals("admin_suspend_business")
                || action.equals("admin_reactivate_business")
                || action.equals("admin_dissolve_business")
                || action.equals("open_staff");
    }
}