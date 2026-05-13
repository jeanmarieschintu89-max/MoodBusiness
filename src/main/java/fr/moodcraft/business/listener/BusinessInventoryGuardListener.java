package fr.moodcraft.business.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Set;

public class BusinessInventoryGuardListener
        implements Listener {

    private static final Set<String> TITLES =
            Set.of(
                    "§6✦ §8Bureau des Entreprises §6✦",
                    "§6✦ §8Gestion Entreprise §6✦",
                    "§6✦ §8Banque Entreprise §6✦",
                    "§6✦ §8Paie Entreprise §6✦",
                    "§6✦ §8Employés §6✦",
                    "§6✦ §8Fiche Employé §6✦",
                    "§6✦ §8Fermer Entreprise §6✦",
                    "§6✦ §8Gestion Entreprises §6✦",
                    "§6✦ §8Admin Entreprise §6✦",
                    "§6✦ §8Logs §6✦",

                    "§6✦ §8Candidatures §6✦",
                    "§6✦ §8Choisir Entreprise §6✦",
                    "§6✦ §8Type de Candidature §6✦",
                    "§6✦ §8Mes Candidatures §6✦",
                    "§6✦ §8Candidatures Reçues §6✦",
                    "§6✦ §8Dossier Candidature §6✦",

                    "§6✦ §8Demandes §6✦",
                    "§6✦ §8Mes demandes §6✦",
                    "§6✦ §8Catégorie Demande §6✦",
                    "§6✦ §8Demande §6✦",
                    "§6✦ §8Offres §6✦",

                    "§6✦ §8Contrats §6✦",
                    "§6✦ §8Mes Contrats §6✦",
                    "§6✦ §8Contrats Entreprise §6✦",
                    "§6✦ §8Litiges §6✦",
                    "§6✦ §8Contrat §6✦",
                    "§6✦ §8Décision Litige §6✦",
                    "§6✦ §8Assigner Mission §6✦",
                    "§6✦ §8Mes Missions §6✦",
                    "§6✦ §8Historique Entreprise §6✦",

                    // anciens titres, pour éviter les trous si un vieux menu existe encore
                    "§8✦ §6Bureau des Entreprises §8✦",
                    "§8✦ §6Gestion Entreprise §8✦",
                    "§8✦ §6Banque Entreprise §8✦",
                    "§8✦ §6Paie Entreprise §8✦",
                    "§8✦ §6Employés §8✦",
                    "§8✦ §6Contrats §8✦",
                    "§8✦ §6Demandes §8✦",
                    "§8✦ §6Candidatures §8✦"
            );

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!isBusinessGui(title)) {
            return;
        }

        //
        // 🔒 Bloque toute prise / déplacement d'item
        // Les autres listeners peuvent quand même lire le clic.
        //

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(
            InventoryDragEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!isBusinessGui(title)) {
            return;
        }

        //
        // 🔒 Bloque les glissements d'items dans les menus
        //

        e.setCancelled(true);
    }

    private boolean isBusinessGui(
            String title
    ) {

        return title != null
                && TITLES.contains(title);
    }
}