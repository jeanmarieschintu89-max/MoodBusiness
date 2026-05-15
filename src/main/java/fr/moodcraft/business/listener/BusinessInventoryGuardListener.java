package fr.moodcraft.business.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;

public class BusinessInventoryGuardListener implements Listener {

    private static final Set<String> CLEAN_TITLES = Set.of(
            "bureau des entreprises",
            "gestion entreprise",
            "gestion entreprises",
            "admin entreprise",
            "mon entreprise",
            "argent entreprise",
            "banque entreprise",
            "paie entreprise",
            "salaires",
            "employes",
            "equipe entreprise",
            "fiche employe",
            "attribuer un role",
            "fermer entreprise",
            "logs",
            "logs financiers",
            "historique entreprise",
            "candidatures",
            "choisir entreprise",
            "type de candidature",
            "mes candidatures",
            "candidatures recues",
            "dossier candidature",
            "demandes",
            "mes demandes",
            "demandes publiques",
            "categorie demande",
            "demande",
            "contrats",
            "mes contrats",
            "contrats entreprise",
            "litiges",
            "contrat",
            "decision litige"
    );

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {

        if (!isBusinessGui(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent event) {

        if (!isBusinessGui(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
    }

    private boolean isBusinessGui(String title) {

        if (title == null || title.isBlank()) {
            return false;
        }

        return CLEAN_TITLES.contains(cleanTitle(title));
    }

    private String cleanTitle(String title) {

        return title
                .replaceAll("§.", "")
                .replace("✦", "")
                .replace("É", "E")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("ù", "u")
                .replace("ç", "c")
                .trim()
                .toLowerCase();
    }
}
