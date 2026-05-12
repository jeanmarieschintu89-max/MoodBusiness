package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessListGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.BusinessStaffGUI;

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

public class BusinessGUIListener implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!isBusinessTitle(title)) {
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

        switch (action) {

            case "main_create" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Registre Économique"
                );

                p.sendMessage("§fCréation d'entreprise.");
                p.sendMessage("§7Utilisation: §e/entreprise creer <nom>");
                p.sendMessage("§8Exemple: §e/entreprise creer NordBuild");

                BusinessMessages.footer(p);
            }

            case "open_staff" -> {

                if (!p.hasPermission("moodbusiness.staff")) {

                    BusinessMessages.deny(
                            p,
                            "Gestion Entreprises",
                            "Accès réservé à l'administration économique."
                    );

                    return;
                }

                BusinessStaffGUI.open(p);
            }

            case "open_public_active", "staff_active" ->
                    BusinessListGUI.openActive(
                            p,
                            1
                    );

            case "staff_recent" ->
                    BusinessListGUI.openRecent(
                            p,
                            1
                    );

            case "staff_suspended" ->
                    BusinessListGUI.openSuspended(
                            p,
                            1
                    );

            case "owned_info", "business_info" -> {

                Business business =
                        BusinessManager.getByName(
                                target
                        );

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Dossier Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                p.closeInventory();

                BusinessMessages.businessInfo(
                        p,
                        business
                );
            }

            case "list_prev", "list_next" ->
                    openListTarget(
                            p,
                            target
                    );

            case "back_main" ->
                    BusinessMainGUI.open(p);

            case "back_staff" ->
                    BusinessStaffGUI.open(p);

            case "coming_soon" -> {

                p.playSound(
                        p.getLocation(),
                        Sound.UI_BUTTON_CLICK,
                        0.8f,
                        1.4f
                );

                BusinessMessages.header(
                        p,
                        "Registre Économique"
                );

                p.sendMessage("§eModule en préparation.");
                p.sendMessage("§7Cette partie arrive dans un prochain pack.");

                BusinessMessages.footer(p);
            }

            default -> {}
        }
    }

    private boolean isBusinessTitle(
            String title
    ) {

        return title.equals(BusinessMainGUI.TITLE)
                || title.equals(BusinessStaffGUI.TITLE)
                || title.equals(BusinessListGUI.TITLE_ACTIVE)
                || title.equals(BusinessListGUI.TITLE_RECENT)
                || title.equals(BusinessListGUI.TITLE_SUSPENDED);
    }

    private void openListTarget(
            Player p,
            String target
    ) {

        if (target == null || !target.contains(":")) {
            return;
        }

        String[] split =
                target.split(":");

        if (split.length < 2) {
            return;
        }

        String type =
                split[0];

        int page;

        try {

            page =
                    Integer.parseInt(split[1]);

        } catch (Exception e) {

            page = 1;
        }

        switch (type) {

            case "ACTIVE" ->
                    BusinessListGUI.openActive(
                            p,
                            page
                    );

            case "RECENT" ->
                    BusinessListGUI.openRecent(
                            p,
                            page
                    );

            case "SUSPENDUE" ->
                    BusinessListGUI.openSuspended(
                            p,
                            page
                    );

            default -> {}
        }
    }
}