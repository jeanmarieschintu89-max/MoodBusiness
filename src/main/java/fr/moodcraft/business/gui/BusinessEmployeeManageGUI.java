package fr.moodcraft.business.gui;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class BusinessEmployeeManageGUI {

    public static final String TITLE =
            "§6✦ §8Fiche Employé §6✦";

    private BusinessEmployeeManageGUI() {}

    public static void open(
            Player p,
            Business business,
            UUID targetUuid
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        27,
                        TITLE
                );

        SafeGUI.fill(inv);

        BusinessRole role =
                business.getRole(targetUuid);

        String name =
                business.getMemberName(targetUuid);

        boolean owner =
                business.isOwner(targetUuid);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.PLAYER_HEAD)
                        .name("§6✦ §f" + shortText(name, 18) + " §6✦")
                        .lore(
                                "§7Fiche du membre.",
                                "",
                                "§7Entreprise: §e" + shortText(business.getName(), 18),
                                "§7Rôle: "
                                        + (role != null
                                        ? role.getDisplayName()
                                        : "§7Aucun"),
                                "",
                                owner
                                        ? "§8• §7Dirigeant officiel"
                                        : "§8• §7Membre de l'entreprise"
                        )
                        .build()
        );

        if (!owner) {

            SafeGUI.set(
                    inv,
                    11,
                    new ItemBuilder(Material.NAME_TAG)
                            .name("§6✦ §fChanger le rôle §6✦")
                            .lore(
                                    "§7Modifier le rôle",
                                    "§7de ce membre.",
                                    "",
                                    "§8• §7Gérant",
                                    "§8• §7Trésorier",
                                    "§8• §7Employé",
                                    "§8• §7Apprenti / Stagiaire",
                                    "",
                                    "§eClique pour choisir"
                            )
                            .action("employee_change_role")
                            .target(
                                    business.getId()
                                            + ":"
                                            + targetUuid
                            )
                            .build()
            );

            SafeGUI.set(
                    inv,
                    15,
                    new ItemBuilder(Material.BARRIER)
                            .name("§c✦ Licencier")
                            .lore(
                                    "§7Retirer ce membre",
                                    "§7de l'entreprise.",
                                    "",
                                    "§cAction sensible",
                                    "",
                                    "§cClique pour licencier"
                            )
                            .action("employee_fire")
                            .target(
                                    business.getId()
                                            + ":"
                                            + targetUuid
                            )
                            .build()
            );
        }

        SafeGUI.set(
                inv,
                22,
                new ItemBuilder(Material.ARROW)
                        .name("§cRetour")
                        .lore(
                                "§7Retour aux employés"
                        )
                        .action("open_employees")
                        .target(business.getId())
                        .build()
        );

        p.openInventory(inv);
    }

    private static String shortText(
            String text,
            int max
    ) {

        if (text == null || text.isBlank()) {
            return "Inconnu";
        }

        String clean =
                text.replaceAll("§.", "")
                        .trim();

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(
                0,
                Math.max(1, max - 3)
        ) + "...";
    }
}