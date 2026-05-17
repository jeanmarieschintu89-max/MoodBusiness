package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.BusinessEmployeeManageGUI;
import fr.moodcraft.business.gui.BusinessEmployeesGUI;
import fr.moodcraft.business.gui.BusinessRoleAssignGUI;
import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BusinessEmployeeManageListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();

        if (!title.equals(BusinessEmployeesGUI.TITLE)
                && !title.equals(BusinessEmployeeManageGUI.TITLE)) {
            return;
        }

        if (!(e.getWhoClicked() instanceof Player p)) return;

        ItemStack item = e.getCurrentItem();
        String action = ItemBuilder.getAction(item);
        String target = ItemBuilder.getTarget(item);

        if (action == null || !isAction(action)) return;

        e.setCancelled(true);

        if (action.equals("open_employees")) {
            Business business = BusinessManager.getById(target);
            if (business != null) BusinessEmployeesGUI.open(p, business);
            return;
        }

        if (action.equals("employee_recruit_chat") || action.equals("employee_recruit_help")) {
            Business business = BusinessManager.getById(target);
            if (business == null) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Entreprise introuvable.");
                return;
            }
            if (!BusinessManager.canManageRoles(p, business)) {
                BusinessMessages.deny(p, "Équipe Entreprise", "Votre rôle ne permet pas de recruter.");
                return;
            }
            RecruitmentChatListener.start(p, business);
            return;
        }

        if (target == null || !target.contains(":")) return;

        String[] split = target.split(":");
        if (split.length < 2) return;

        Business business = BusinessManager.getById(split[0]);
        if (business == null) {
            BusinessMessages.deny(p, "Équipe Entreprise", "Entreprise introuvable.");
            return;
        }

        UUID targetUuid;
        try {
            targetUuid = UUID.fromString(split[1]);
        } catch (Exception ex) {
            return;
        }

        if (!BusinessManager.canManageRoles(p, business)) {
            BusinessMessages.deny(p, "Équipe Entreprise", "Votre rôle ne permet pas cette action.");
            return;
        }

        switch (action) {
            case "employee_card" -> BusinessEmployeeManageGUI.open(p, business, targetUuid);

            case "employee_change_role" -> BusinessRoleAssignGUI.open(p, business, targetUuid);

            case "employee_pay_help" -> {
                p.closeInventory();
                String name = business.getMemberName(targetUuid);
                double current = business.getMemberPay(targetUuid);
                BusinessMessages.header(p, "Paye employé");
                p.sendMessage("§e➜ §fModifier la paye prévue d'un membre.");
                p.sendMessage("");
                p.sendMessage("§8• §7Employé : §e" + name);
                p.sendMessage("§8• §7Paye actuelle : §e" + VaultHook.format(current));
                p.sendMessage("");
                p.sendMessage("§8• §7Commande : §e/entreprise paye " + name + " <montant>");
                p.sendMessage("§8• §7Exemple : §e/entreprise paye " + name + " 5000");
                p.sendMessage("§8• §7Mets §e0 §7si aucune paye fixe n'est prévue.");
                BusinessMessages.footer(p);
                p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1.2f);
            }

            case "employee_fire" -> {
                String name = business.getMemberName(targetUuid);
                BusinessManager.ActionResult result = BusinessManager.removeMember(p, business, targetUuid);
                p.closeInventory();

                if (!result.success()) {
                    BusinessMessages.deny(p, "Équipe Entreprise", result.message());
                    p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 0.85f);
                    return;
                }

                BusinessMessages.success(p, "Équipe Entreprise", "Membre licencié: §e" + name);

                OfflinePlayer offline = Bukkit.getOfflinePlayer(targetUuid);
                if (offline.isOnline() && offline.getPlayer() != null) {
                    BusinessMessages.header(offline.getPlayer(), "Équipe Entreprise");
                    offline.getPlayer().sendMessage("§c✘ §fVous avez quitté l'entreprise.");
                    offline.getPlayer().sendMessage("");
                    offline.getPlayer().sendMessage("§7Entreprise: §e" + business.getName());
                    offline.getPlayer().sendMessage("§7Décision: §cLicenciement");
                    BusinessMessages.footer(offline.getPlayer());
                    offline.getPlayer().playSound(offline.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
                }

                BusinessEmployeesGUI.open(p, business);
            }

            default -> {}
        }
    }

    private boolean isAction(String action) {
        return action.equals("employee_card")
                || action.equals("employee_change_role")
                || action.equals("employee_fire")
                || action.equals("employee_pay_help")
                || action.equals("open_employees")
                || action.equals("employee_recruit_chat")
                || action.equals("employee_recruit_help");
    }
}
