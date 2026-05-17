package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;
import fr.moodcraft.business.util.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class BusinessEmployeeManageGUI {

    public static final String TITLE = GuiTitle.of("Fiche Employé");

    private BusinessEmployeeManageGUI() {}

    public static void open(Player p, Business business, UUID targetUuid) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        SafeGUI.fill(inv);

        BusinessRole role = business.getRole(targetUuid);
        String name = business.getMemberName(targetUuid);
        boolean owner = business.isOwner(targetUuid);

        SafeGUI.set(inv, 4, new ItemBuilder(Material.PLAYER_HEAD)
                .name("§6✦ §f" + shortText(name, 18) + " §6✦")
                .lore(
                        "§8• §7Entreprise : §e" + shortText(business.getName(), 18),
                        "§8• §7Rôle : " + (role != null ? role.getDisplayName() : "§7Aucun"),
                        owner ? "§8• §7Dirigeant officiel" : "§8• §7Paye prévue : §e" + VaultHook.format(business.getMemberPay(targetUuid)),
                        "",
                        owner ? "§e➜ §fFiche en lecture seule" : "§e➜ §fChoisis une action"
                )
                .build());

        if (!owner) {
            SafeGUI.set(inv, 10, new ItemBuilder(Material.NAME_TAG)
                    .name("§6✦ §fChanger le rôle §6✦")
                    .lore(
                            "§8• §7Employé",
                            "§8• §7Gérant",
                            "",
                            "§e➜ §fChoisir un rôle"
                    )
                    .action("employee_change_role")
                    .target(business.getId() + ":" + targetUuid)
                    .build());

            SafeGUI.set(inv, 13, new ItemBuilder(Material.GOLD_INGOT)
                    .name("§6✦ §fModifier la paye §6✦")
                    .lore(
                            "§8• §7Paye actuelle : §e" + VaultHook.format(business.getMemberPay(targetUuid)),
                            "§8• §7Modifie le montant prévu",
                            "§8• §7Commande guidée dans le chat",
                            "",
                            "§e➜ §fVoir la commande"
                    )
                    .action("employee_pay_help")
                    .target(business.getId() + ":" + targetUuid)
                    .build());

            SafeGUI.set(inv, 16, new ItemBuilder(Material.REDSTONE_BLOCK)
                    .name("§c✦ §fLicencier §c✦")
                    .lore(
                            "§8• §7Retire ce membre",
                            "§8• §7de l'entreprise",
                            "§8• §7Logs gardés",
                            "",
                            "§c✖ §fAction sensible"
                    )
                    .action("employee_fire")
                    .target(business.getId() + ":" + targetUuid)
                    .build());
        }

        SafeGUI.set(inv, 22, new ItemBuilder(Material.BARRIER)
                .name("§6✦ §fRetour §6✦")
                .lore("§8• §7Retour à l'équipe")
                .action("open_employees")
                .target(business.getId())
                .build());

        p.openInventory(inv);
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
