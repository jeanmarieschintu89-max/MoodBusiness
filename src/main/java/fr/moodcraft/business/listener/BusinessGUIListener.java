package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.ApplicationBusinessSelectGUI;
import fr.moodcraft.business.gui.ApplicationListGUI;
import fr.moodcraft.business.gui.ApplicationMainGUI;
import fr.moodcraft.business.gui.ApplicationReviewGUI;
import fr.moodcraft.business.gui.ApplicationTypeGUI;

import fr.moodcraft.business.gui.AuditLogGUI;

import fr.moodcraft.business.gui.BusinessEmployeesGUI;
import fr.moodcraft.business.gui.BusinessListGUI;
import fr.moodcraft.business.gui.BusinessMainGUI;
import fr.moodcraft.business.gui.BusinessRoleAssignGUI;
import fr.moodcraft.business.gui.BusinessStaffGUI;

import fr.moodcraft.business.gui.ContractListGUI;

import fr.moodcraft.business.manager.ApplicationManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Application;
import fr.moodcraft.business.model.ApplicationType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

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

            //
            // 🏢 CREATION
            //

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

            //
            // 🛡 STAFF
            //

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

            case "audit_logs" -> {

                if (!p.hasPermission("moodbusiness.staff")) {

                    BusinessMessages.deny(
                            p,
                            "Historique Administratif",
                            "Accès réservé à l'administration économique."
                    );

                    return;
                }

                AuditLogGUI.open(p);
            }

            case "contract_litige_list" -> {

                if (!p.hasPermission("moodbusiness.staff.litige")) {

                    BusinessMessages.deny(
                            p,
                            "Litiges Économiques",
                            "Accès réservé à l'administration économique."
                    );

                    return;
                }

                ContractListGUI.openLitiges(p);
            }

            //
            // 📚 LISTES ENTREPRISES
            //

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

            //
            // 📄 DOSSIER ENTREPRISE
            //

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

            //
            // 👥 EMPLOYES
            //

            case "open_employees" -> {

                Business business =
                        BusinessManager.getByName(
                                target
                        );

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Employés Entreprise",
                            "Entreprise introuvable."
                    );

                    return;
                }

                if (!BusinessManager.canSeeEmployees(
                        p,
                        business
                )) {

                    BusinessMessages.deny(
                            p,
                            "Employés Entreprise",
                            "Vous n'appartenez pas à cette entreprise."
                    );

                    return;
                }

                BusinessEmployeesGUI.open(
                        p,
                        business
                );
            }

            case "employee_manage" ->
                    openRoleManager(
                            p,
                            target
                    );

            case "assign_role" ->
                    assignRole(
                            p,
                            target
                    );

            case "employee_recruit_help" -> {

                p.closeInventory();

                BusinessMessages.header(
                        p,
                        "Employés Entreprise"
                );

                p.sendMessage("§fRecruter un membre.");
                p.sendMessage("§7Commande: §e/entreprise recruter <joueur> [role]");
                p.sendMessage("");
                p.sendMessage("§7Rôles possibles:");
                p.sendMessage("§8• §eStagiaire");
                p.sendMessage("§8• §eApprenti");
                p.sendMessage("§8• §eEmploye");
                p.sendMessage("§8• §eTresorier");
                p.sendMessage("§8• §eResponsable");
                p.sendMessage("§8• §eGerant §7(dirigeant seulement)");

                BusinessMessages.footer(p);
            }

            //
            // 📨 CANDIDATURES
            //

            case "open_applications" ->
                    ApplicationMainGUI.open(p);

            case "application_choose_business" ->
                    ApplicationBusinessSelectGUI.open(p);

            case "application_my_list" ->
                    ApplicationListGUI.openMy(p);

            case "application_received_list" -> {

                if (target == null || target.isBlank()) {

                    Business own =
                            BusinessManager.getMemberBusiness(
                                    p.getUniqueId()
                            );

                    if (own == null) {
                        return;
                    }

                    ApplicationListGUI.openReceived(
                            p,
                            own.getId()
                    );

                    return;
                }

                ApplicationListGUI.openReceived(
                        p,
                        target
                );
            }

            case "application_select_business" -> {

                Business business =
                        BusinessManager.getByName(
                                target
                        );

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            "Entreprise introuvable."
                    );

                    return;
                }

                ApplicationTypeGUI.open(
                        p,
                        business
                );
            }

            case "application_start" -> {

                if (target == null || !target.contains(":")) {
                    return;
                }

                String[] split =
                        target.split(":");

                if (split.length < 2) {
                    return;
                }

                Business business =
                        BusinessManager.getByName(
                                split[0]
                        );

                if (business == null) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            "Entreprise introuvable."
                    );

                    return;
                }

                ApplicationType type;

                try {

                    type =
                            ApplicationType.valueOf(
                                    split[1]
                            );

                } catch (Exception ex) {

                    return;
                }

                ApplicationChatListener.start(
                        p,
                        business,
                        type
                );
            }

            case "application_review" -> {

                Application application =
                        ApplicationManager.get(
                                target
                        );

                if (application == null) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            "Candidature introuvable."
                    );

                    return;
                }

                ApplicationReviewGUI.open(
                        p,
                        application
                );
            }

            case "application_interview" -> {

                Application application =
                        ApplicationManager.get(
                                target
                        );

                if (application == null) {
                    return;
                }

                ApplicationManager.ApplicationResult result =
                        ApplicationManager.requestInterview(
                                p,
                                application
                        );

                p.closeInventory();

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Candidature " + BusinessMessages.brand(),
                        "Entretien demandé pour §e"
                                + application.getApplicantName()
                );
            }case "application_refuse" -> {

                Application application =
                        ApplicationManager.get(
                                target
                        );

                if (application == null) {
                    return;
                }

                ApplicationManager.ApplicationResult result =
                        ApplicationManager.refuse(
                                p,
                                application,
                                "Refusée par l'entreprise"
                        );

                p.closeInventory();

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Candidature " + BusinessMessages.brand(),
                        "Candidature refusée."
                );
            }

            case "application_accept_stage" -> {

                Application application =
                        ApplicationManager.get(
                                target
                        );

                if (application == null) {
                    return;
                }

                ApplicationManager.ApplicationResult result =
                        ApplicationManager.accept(
                                p,
                                application,
                                BusinessRole.STAGIAIRE
                        );

                p.closeInventory();

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Candidature " + BusinessMessages.brand(),
                        "Joueur accepté comme stagiaire."
                );
            }

            case "application_accept_apprentice" -> {

                Application application =
                        ApplicationManager.get(
                                target
                        );

                if (application == null) {
                    return;
                }

                ApplicationManager.ApplicationResult result =
                        ApplicationManager.accept(
                                p,
                                application,
                                BusinessRole.APPRENTI
                        );

                p.closeInventory();

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Candidature " + BusinessMessages.brand(),
                            result.message()
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Candidature " + BusinessMessages.brand(),
                        "Joueur accepté comme apprenti."
                );
            }

            //
            // 📄 PAGES
            //

            case "list_prev", "list_next" ->
                    openListTarget(
                            p,
                            target
                    );

            //
            // ↩ RETOURS
            //

            case "back_main" ->
                    BusinessMainGUI.open(p);

            case "back_staff" ->
                    BusinessStaffGUI.open(p);

            //
            // 💤 PLACEHOLDER
            //

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
                || title.equals(BusinessListGUI.TITLE_SUSPENDED)
                || title.equals(BusinessEmployeesGUI.TITLE)
                || title.equals(BusinessRoleAssignGUI.TITLE)
                || title.equals(ApplicationMainGUI.TITLE)
                || title.equals(ApplicationBusinessSelectGUI.TITLE)
                || title.equals(ApplicationTypeGUI.TITLE)
                || title.equals(ApplicationListGUI.TITLE_MY)
                || title.equals(ApplicationListGUI.TITLE_RECEIVED)
                || title.equals(ApplicationReviewGUI.TITLE)
                || title.equals(AuditLogGUI.TITLE);
    }

    private void openRoleManager(
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

        Business business =
                BusinessManager.getByName(
                        split[0]
                );

        if (business == null) {
            return;
        }

        UUID targetUuid;

        try {

            targetUuid =
                    UUID.fromString(
                            split[1]
                    );

        } catch (Exception e) {

            return;
        }

        if (!BusinessManager.canManageRoles(
                p,
                business
        )) {

            BusinessMessages.deny(
                    p,
                    "Rôles Entreprise",
                    "Vous ne pouvez pas modifier les rôles."
            );

            return;
        }

        BusinessRoleAssignGUI.open(
                p,
                business,
                targetUuid
        );
    }

    private void assignRole(
            Player p,
            String target
    ) {

        if (target == null || !target.contains(":")) {
            return;
        }

        String[] split =
                target.split(":");

        if (split.length < 3) {
            return;
        }

        Business business =
                BusinessManager.getByName(
                        split[0]
                );

        if (business == null) {
            return;
        }

        UUID targetUuid;

        try {

            targetUuid =
                    UUID.fromString(
                            split[1]
                    );

        } catch (Exception e) {

            return;
        }

        BusinessRole role;

        try {

            role =
                    BusinessRole.valueOf(
                            split[2]
                    );

        } catch (Exception e) {

            return;
        }

        BusinessManager.ActionResult result =
                BusinessManager.assignRole(
                        p,
                        business,
                        targetUuid,
                        role
                );

        if (!result.success()) {

            BusinessMessages.deny(
                    p,
                    "Rôles Entreprise",
                    result.message()
            );

            return;
        }

        p.closeInventory();

        BusinessMessages.success(
                p,
                "Rôles Entreprise",
                result.message()
        );

        BusinessEmployeesGUI.open(
                p,
                business
        );
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
                    Integer.parseInt(
                            split[1]
                    );

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