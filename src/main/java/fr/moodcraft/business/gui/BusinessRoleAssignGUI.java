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

import java.util.UUID;

public final class BusinessRoleAssignGUI {

    public static final String TITLE =
            "§8✦ §6Attribuer un Rôle §8✦";

    private BusinessRoleAssignGUI() {}

    public static void open(
            Player p,
            Business business,
            UUID targetUuid
    ) {

        Inventory inv =
                Bukkit.createInventory(
                        null,
                        54,
                        TITLE
                );

        SafeGUI.fill(inv);

        String targetName =
                business.getMemberName(targetUuid);

        BusinessRole current =
                business.getRole(targetUuid);

        SafeGUI.set(
                inv,
                4,
                new ItemBuilder(Material.NAME_TAG)
                        .name("§6✦ §f" + targetName + " §6✦")
                        .lore(
                                "§7Rôle actuel: "
                                        + (current != null
                                        ? current.getDisplayName()
                                        : "§7Aucun"),
                                "",
                                "§7Sélectionnez un nouveau rôle.",
                                "§8• §7Le rôle dirigeant est séparé."
                        )
                        .build()
        );

        addRole(
                inv,
                p,
                business,
                targetUuid,
                BusinessRole.GERANT,
                20,
                Material.GOLDEN_HELMET,
                "§7Gestion avancée de l'entreprise."
        );

        addRole(
                inv,
                p,
                business,
                targetUuid,
                BusinessRole.RESPONSABLE_CONTRATS,
                21,
                Material.WRITABLE_BOOK,
                "§7Gère les offres et contrats."
        );

        addRole(
                inv,
                p,
                business,
                targetUuid,
                BusinessRole.TRESORIER,
                22,
                Material.GOLD_INGOT,
                "§7Gère la banque selon autorisation."
        );

        addRole(
                inv,
                p,
                business,
                targetUuid,
                BusinessRole.EMPLOYE,
                23,
                Material.IRON_PICKAXE,
                "§7Membre officiel de l'entreprise."
        );

        addRole(
                inv,
                p,
                business,
                targetUuid,
                BusinessRole.APPRENTI,
                24,
                Material.COPPER_INGOT,
                "§7Formation active encadrée."
        );

        addRole(
                inv,
                p,
                business,
                targetUuid,
                BusinessRole.STAGIAIRE,
                31,
                Material.PAPER,
                "§7Découverte sans accès sensible."
        );

        SafeGUI.set(
                inv,
                40,
                new ItemBuilder(Material.BARRIER)
                        .name("§cRetour")
                        .lore(
                                "§7Revenir à la liste des employés."
                        )
                        .action("open_employees")
                        .target(business.getId())
                        .build()
        );

        p.openInventory(inv);
    }

    private static void addRole(
            Inventory inv,
            Player p,
            Business business,
            UUID targetUuid,
            BusinessRole role,
            int slot,
            Material material,
            String description
    ) {

        boolean allowed =
                BusinessManager.canAssignRole(
                        p,
                        business,
                        targetUuid,
                        role
                );

        SafeGUI.set(
                inv,
                slot,
                new ItemBuilder(
                        allowed
                                ? material
                                : Material.GRAY_DYE
                )
                        .name(
                                "§6✦ "
                                        + role.getDisplayName()
                                        + " §6✦"
                        )
                        .lore(
                                description,
                                "",
                                allowed
                                        ? "§a✔ Attribuer ce rôle"
                                        : "§cAction non autorisée"
                        )
                        .action(
                                allowed
                                        ? "assign_role"
                                        : "coming_soon"
                        )
                        .target(
                                business.getId()
                                        + ":"
                                        + targetUuid
                                        + ":"
                                        + role.name()
                        )
                        .build()
        );
    }
}