package fr.moodcraft.business.gui;

import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.ItemBuilder;
import fr.moodcraft.business.util.SafeGUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BusinessEmployeesGUI {

    public static final String TITLE =
            "§8✦ §6Employés Entreprise §8✦";

    private static final int[] SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private BusinessEmployeesGUI() {}

    public static void open(
            Player p,
            Business business
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        boolean canManage =
                BusinessManager.canManageRoles(
                        p,
                        business
                );

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.BOOK)
                        .name("§6✦ §f" + business.getName() + " §6✦")
                        .lore(
                                "§7Membres enregistrés: §e"
                                        + business.getMembers().size(),
                                "§7Gestion: "
                                        + (canManage
                                        ? "§aAutorisée"
                                        : "§cLecture seule"),
                                "",
                                "§8• §7Service officiel de §aMood§6Craft§7."
                        )
                        .build()
        );

        List<Map.Entry<UUID, BusinessRole>> members =
                new ArrayList<>(
                        business.getMembers().entrySet()
                );

        int index = 0;

        for (Map.Entry<UUID, BusinessRole> entry : members) {

            if (index >= SLOTS.length) {
                break;
            }

            UUID uuid =
                    entry.getKey();

            BusinessRole role =
                    entry.getValue();

            Material icon =
                    switch (role) {

                        case DIRIGEANT -> Material.NETHER_STAR;
                        case GERANT -> Material.GOLDEN_HELMET;
                        case RESPONSABLE_CONTRATS -> Material.WRITABLE_BOOK;
                        case TRESORIER -> Material.GOLD_INGOT;
                        case EMPLOYE -> Material.IRON_PICKAXE;
                        case APPRENTI -> Material.COPPER_INGOT;
                        case STAGIAIRE -> Material.PAPER;
                    };

            List<String> lore =
                    new ArrayList<>();

            lore.add("§7Rôle actuel: " + role.getDisplayName());
            lore.add("§7UUID: §8" + uuid);
            lore.add("");

            if (business.isOwner(uuid)) {

                lore.add("§8• §7Dirigeant officiel de l'entreprise.");

            } else if (canManage) {

                lore.add("§eClique pour modifier le rôle.");

            } else {

                lore.add("§8• §7Consultation uniquement.");
            }

            SafeGUI.set(
                    inv,
                    SLOTS[index],
                    new ItemBuilder(icon)
                            .name(
                                    "§6✦ §f"
                                            + business.getMemberName(uuid)
                                            + " §6✦"
                            )
                            .lore(
                                    lore.toArray(new String[0])
                            )
                            .action("employee_manage")
                            .target(
                                    business.getId()
                                            + ":"
                                            + uuid
                            )
                            .build()
            );

            index++;
        }

        SafeGUI.set(
                inv,
                45,
                new ItemBuilder(Material.EMERALD)
                        .name("§6✦ §fRecruter §6✦")
                        .lore(
                                "§7Ajouter un joueur dans l'entreprise.",
                                "",
                                "§eCommande:",
                                "§f/entreprise recruter <joueur> [role]",
                                "",
                                "§8• §7Par défaut: §eStagiaire"
                        )
                        .action("employee_recruit_help")
                        .target(business.getId())
                        .build()
        );

        SafeGUI.set(
                inv,
                49,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir au menu entreprise."
                        )
                        .action("back_main")
                        .build()
        );

        p.openInventory(inv);
    }
}