package fr.moodcraft.business.util;

import fr.moodcraft.business.model.Contract;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.HashMap;
import java.util.Map;

public final class ContractBookUtil {

    private ContractBookUtil() {}

    public static void giveProofBooks(
            Contract contract
    ) {

        if (contract == null) {
            return;
        }

        Player client =
                Bukkit.getPlayer(
                        contract.getClientUuid()
                );

        Player businessActor =
                Bukkit.getPlayer(
                        contract.getBusinessActorUuid()
                );

        if (client != null && client.isOnline()) {
            giveBook(
                    client,
                    contract,
                    "Client"
            );
        }

        if (businessActor != null && businessActor.isOnline()) {
            giveBook(
                    businessActor,
                    contract,
                    "Entreprise"
            );
        }
    }

    private static void giveBook(
            Player player,
            Contract contract,
            String role
    ) {

        ItemStack book =
                createBook(
                        contract,
                        role
                );

        Map<Integer, ItemStack> remaining =
                player.getInventory().addItem(book);

        if (!remaining.isEmpty()) {
            for (ItemStack item : remaining.values()) {
                player.getWorld().dropItemNaturally(
                        player.getLocation(),
                        item
                );
            }
        }

        BusinessMessages.success(
                player,
                "Contrat",
                "Livre de preuve reçu.",
                "§8• §7Gardez-le comme trace du contrat."
        );
    }

    private static ItemStack createBook(
            Contract contract,
            String role
    ) {

        ItemStack item =
                new ItemStack(Material.WRITTEN_BOOK);

        BookMeta meta =
                (BookMeta) item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setTitle("Contrat Business");
        meta.setAuthor("MoodCraft");
        meta.setDisplayName("§6✦ §fContrat Business §6✦");
        meta.setLore(
                java.util.List.of(
                        "§8• §7Preuve de contrat",
                        "§8• §7Rôle : §e" + role,
                        "§8• §7ID : §8" + shortText(contract.getId(), 20)
                )
        );

        meta.addPage(
                "§6Contrat MoodCraft\n\n"
                        + "§0Rôle: " + role + "\n"
                        + "§0ID: " + contract.getId() + "\n\n"
                        + "§0Projet:\n"
                        + shortText(contract.getTitle(), 120)
        );

        meta.addPage(
                "§6Parties\n\n"
                        + "§0Client:\n"
                        + safe(contract.getClientName()) + "\n\n"
                        + "§0Entreprise:\n"
                        + safe(contract.getBusinessName()) + "\n\n"
                        + "§0Responsable:\n"
                        + safe(contract.getBusinessActorName())
        );

        meta.addPage(
                "§6Montants\n\n"
                        + "§0Budget bloque:\n"
                        + VaultHook.format(contract.getGrossAmount()) + "\n\n"
                        + "§0Taxe prevue:\n"
                        + VaultHook.format(contract.getTaxAmount()) + "\n\n"
                        + "§0Net entreprise:\n"
                        + VaultHook.format(contract.getNetAmount())
        );

        meta.addPage(
                "§6Conditions\n\n"
                        + "§0Delai:\n"
                        + contract.getDueDays() + " jour(s)\n\n"
                        + "§0Etat initial:\n"
                        + strip(contract.getStatus().getDisplayName()) + "\n\n"
                        + "§0Validation finale:\n"
                        + "client ou litige staff."
        );

        meta.addPage(
                "§6Description\n\n"
                        + "§0"
                        + shortText(contract.getDescription(), 220)
        );

        item.setItemMeta(meta);
        return item;
    }

    private static String safe(
            String text
    ) {

        if (text == null || text.isBlank()) {
            return "Non renseigne";
        }

        return strip(text);
    }

    private static String strip(
            String text
    ) {

        if (text == null) {
            return "";
        }

        return text.replaceAll("§.", "")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("ù", "u")
                .replace("ç", "c");
    }

    private static String shortText(
            String text,
            int max
    ) {

        String clean =
                safe(text);

        if (clean.length() <= max) {
            return clean;
        }

        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }
}
