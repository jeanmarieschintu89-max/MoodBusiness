package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;
import fr.moodcraft.business.model.RequestStatus;
import fr.moodcraft.business.model.TransactionType;

import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.ContractStorage;
import fr.moodcraft.business.storage.FinanceStorage;
import fr.moodcraft.business.storage.RequestStorage;

import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ContractManager {

    private ContractManager() {}

    public static ContractResult createFromRequest(Player actor, Business business, BusinessRequest request) {
        if (actor == null || business == null || request == null) return ContractResult.fail("Dossier de prise en charge invalide.");
        if (!business.isActive()) return ContractResult.fail("Votre entreprise n'est pas active.");
        if (!BusinessManager.canManageContracts(actor, business)) return ContractResult.fail("Votre rôle ne permet pas de prendre cette mission.");
        if (!request.getStatus().isOpen() || request.isExpired()) return ContractResult.fail("Cette mission n'est plus disponible.");
        if (request.getCreatorUuid().equals(actor.getUniqueId())) return ContractResult.fail("Vous ne pouvez pas prendre votre propre mission.");

        OfflinePlayer client = Bukkit.getOfflinePlayer(request.getCreatorUuid());
        double gross = request.getBudget();

        if (!VaultHook.has(client, gross)) return ContractResult.fail("Fonds client insuffisants. Montant requis : §e" + VaultHook.format(gross));
        if (!VaultHook.withdraw(client, gross)) return ContractResult.fail("Le blocage de l'argent du client a échoué.");

        double taxRate = Main.getInstance().getConfig().getDouble("economy.tax-rate", 20.0);
        double taxAmount = Math.max(0, gross * (taxRate / 100.0));
        double net = Math.max(0, gross - taxAmount);
        long now = System.currentTimeMillis();
        String id = "C-" + now + "-" + business.getId();

        Contract contract = new Contract(
                id,
                request.getId(),
                "DIRECT",
                request.getCreatorUuid(),
                request.getCreatorName(),
                business.getId(),
                business.getName(),
                actor.getUniqueId(),
                actor.getName(),
                request.getTitle(),
                request.getDescription(),
                gross,
                taxRate,
                taxAmount,
                net,
                gross,
                request.getDueDays(),
                ContractStatus.EN_COURS,
                now,
                now,
                now + TimeUtil.days(request.getDueDays()),
                0,
                0,
                new ArrayList<>()
        );

        contract.addHistory("§8• §6Pris en charge §7par §f" + business.getName() + " §8• §7Responsable : §f" + actor.getName());
        contract.addHistory("§8• §eArgent sécurisé §7Client : §f" + request.getCreatorName() + " §8• §7Montant : §e" + VaultHook.format(gross));
        contract.addHistory("§8• §6Taxe prévue §7Taxe : §c" + VaultHook.format(taxAmount) + " §8(" + taxRate + "%§8)");

        ContractStorage.add(contract);
        request.setAcceptedOfferId("DIRECT:" + business.getId());
        request.setStatus(RequestStatus.TRANSFORMEE_CONTRAT);
        RequestStorage.save();

        AuditLogManager.log(AuditLogType.CONTRACT_CREATED, actor, request.getTitle(), business,
                "Mission prise en charge directement. Brut : " + VaultHook.format(gross) + ", taxe prévue : " + VaultHook.format(taxAmount) + ", net entreprise : " + VaultHook.format(net));

        AlertManager.add(client, AlertType.CONTRACT, "Mission prise en charge",
                business.getName() + " a pris en charge votre mission : " + request.getTitle() + ". Argent sécurisé : " + VaultHook.format(gross) + ".");
        AlertManager.add(business.getOwnerUuid(), business.getOwnerName(), AlertType.CONTRACT, "Nouvelle mission",
                "Votre entreprise a pris en charge : " + request.getTitle() + ".");
        AlertManager.add(actor, AlertType.CONTRACT, "Mission lancée",
                "La mission " + request.getTitle() + " est maintenant prise en charge par votre entreprise.");

        return ContractResult.success(contract, "Mission prise en charge. Argent sécurisé.");
    }

    public static Contract get(String id) {
        Contract contract = ContractStorage.get(id);
        if (contract != null) refreshDelay(contract);
        return contract;
    }

    public static List<Contract> getByClient(Player player) {
        List<Contract> list = new ArrayList<>();
        if (player == null) return list;
        for (Contract contract : ContractStorage.getAll()) {
            refreshDelay(contract);
            if (contract.getClientUuid().equals(player.getUniqueId())) list.add(contract);
        }
        sort(list);
        return list;
    }

    public static List<Contract> getByBusiness(Business business) {
        List<Contract> list = new ArrayList<>();
        if (business == null) return list;
        for (Contract contract : ContractStorage.getAll()) {
            refreshDelay(contract);
            if (contract.getBusinessId().equalsIgnoreCase(business.getId())) list.add(contract);
        }
        sort(list);
        return list;
    }

    public static List<Contract> getLitiges() {
        List<Contract> list = new ArrayList<>();
        for (Contract contract : ContractStorage.getAll()) {
            if (contract.getStatus() == ContractStatus.LITIGE) list.add(contract);
        }
        sort(list);
        return list;
    }

    public static ContractResult complete(Player actor, Contract contract, String comment) {
        if (actor == null || contract == null) return ContractResult.fail("Mission invalide.");
        Business business = BusinessManager.getById(contract.getBusinessId());
        if (business == null) return ContractResult.fail("Entreprise introuvable.");
        if (!BusinessManager.canManageContracts(actor, business)) return ContractResult.fail("Votre rôle ne permet pas de terminer cette mission.");
        if (contract.getStatus() != ContractStatus.EN_COURS && contract.getStatus() != ContractStatus.EN_RETARD) return ContractResult.fail("Cette mission ne peut pas être marquée comme terminée.");

        long now = System.currentTimeMillis();
        int validationHours = Main.getInstance().getConfig().getInt("contracts.validation-delay-hours", 48);
        contract.setStatus(ContractStatus.TERMINE);
        contract.setCompletedAt(now);
        contract.setValidateBefore(now + TimeUtil.hours(validationHours));
        contract.addHistory("§8• §aTerminée §7par §f" + actor.getName() + " §8• §7" + safeComment(comment));
        ContractStorage.save();

        AuditLogManager.log(AuditLogType.CONTRACT_COMPLETED, actor, contract.getClientName(), business,
                "Mission terminée: " + contract.getTitle() + ". Commentaire: " + safeComment(comment));

        OfflinePlayer client = Bukkit.getOfflinePlayer(contract.getClientUuid());
        AlertManager.add(client, AlertType.CONTRACT, "Mission terminée",
                "La mission " + contract.getTitle() + " est terminée. Vous pouvez la valider ou signaler un problème.");

        return ContractResult.success(contract, "Mission marquée comme terminée.");
    }

    public static ContractResult validate(Player client, Contract contract) {
        if (client == null || contract == null) return ContractResult.fail("Mission invalide.");
        if (!contract.getClientUuid().equals(client.getUniqueId())) return ContractResult.fail("Seul le client peut valider cette mission.");
        if (contract.getStatus() != ContractStatus.TERMINE) return ContractResult.fail("Cette mission doit être terminée avant validation.");

        Business business = BusinessManager.getById(contract.getBusinessId());
        if (business == null) return ContractResult.fail("Entreprise introuvable.");

        business.setBalance(business.getBalance() + contract.getNetAmount());
        contract.setEscrowAmount(0);
        contract.setStatus(ContractStatus.VALIDE);
        contract.addHistory("§8• §6Validée §7par §f" + client.getName() + " §8• §7Versement net: §a" + VaultHook.format(contract.getNetAmount()));
        contract.addHistory("§8• §cTaxe §7prélevée: §c" + VaultHook.format(contract.getTaxAmount()) + " §8(" + contract.getTaxRate() + "%§8)");

        BusinessStorage.save();
        ContractStorage.save();
        FinanceStorage.add(business, TransactionType.CONTRAT_VERSEMENT, contract.getNetAmount(), client.getName(), business.getName(), "Versement net de la mission " + contract.getId());
        FinanceStorage.add(business, TransactionType.TAXE, contract.getTaxAmount(), "Bureau des Entreprises", business.getName(), "Taxe " + contract.getTaxRate() + "% sur la mission " + contract.getId());

        AuditLogManager.log(AuditLogType.CONTRACT_VALIDATED, client, business.getName(), business,
                "Mission validée: " + contract.getTitle() + ". Brut: " + VaultHook.format(contract.getGrossAmount()) + ", taxe: " + VaultHook.format(contract.getTaxAmount()) + ", net entreprise: " + VaultHook.format(contract.getNetAmount()));

        AlertManager.add(business.getOwnerUuid(), business.getOwnerName(), AlertType.BANK, "Versement mission",
                "La mission " + contract.getTitle() + " a été validée. Net reçu: " + VaultHook.format(contract.getNetAmount()) + ".");
        OfflinePlayer businessActor = Bukkit.getOfflinePlayer(contract.getBusinessActorUuid());
        AlertManager.add(businessActor, AlertType.CONTRACT, "Mission validée", "Le client a validé la mission " + contract.getTitle() + ".");
        AlertManager.add(client, AlertType.CONTRACT, "Mission validée", "Vous avez validé la mission " + contract.getTitle() + ".");

        return ContractResult.success(contract, "Mission validée. Paiement versé à l'entreprise.");
    }

    public static ContractResult openLitige(Player actor, Contract contract, String reason) {
        if (actor == null || contract == null) return ContractResult.fail("Mission invalide.");
        boolean client = contract.getClientUuid().equals(actor.getUniqueId());
        Business business = BusinessManager.getById(contract.getBusinessId());
        boolean businessActor = business != null && BusinessManager.canManageContracts(actor, business);
        if (!client && !businessActor) return ContractResult.fail("Vous ne pouvez pas signaler un problème sur cette mission.");
        if (contract.getStatus() == ContractStatus.VALIDE || contract.getStatus() == ContractStatus.ANNULE) return ContractResult.fail("Cette mission est déjà clôturée.");

        contract.setStatus(ContractStatus.LITIGE);
        contract.addHistory("§8• §cProblème signalé §7par §f" + actor.getName() + " §8• §7" + safeComment(reason));
        ContractStorage.save();
        AuditLogManager.log(AuditLogType.CONTRACT_LITIGE, actor, contract.getTitle(), business, "Problème signalé. Raison: " + safeComment(reason));

        OfflinePlayer clientPlayer = Bukkit.getOfflinePlayer(contract.getClientUuid());
        AlertManager.add(clientPlayer, AlertType.LITIGE, "Problème signalé", "Un problème a été signalé sur la mission " + contract.getTitle() + ".");
        if (business != null) {
            AlertManager.add(business.getOwnerUuid(), business.getOwnerName(), AlertType.LITIGE, "Problème signalé", "Un problème a été signalé sur la mission " + contract.getTitle() + ".");
        }
        return ContractResult.success(contract, "Problème signalé. L'argent reste sécurisé.");
    }

    public static boolean canView(Player player, Contract contract) {
        if (player == null || contract == null) return false;
        if (contract.getClientUuid().equals(player.getUniqueId())) return true;
        Business business = BusinessManager.getById(contract.getBusinessId());
        return business != null && business.isMember(player.getUniqueId());
    }

    public static boolean canBusinessComplete(Player player, Contract contract) {
        if (player == null || contract == null) return false;
        Business business = BusinessManager.getById(contract.getBusinessId());
        return business != null && BusinessManager.canManageContracts(player, business);
    }

    public static boolean canClientValidate(Player player, Contract contract) {
        return player != null && contract != null && contract.getClientUuid().equals(player.getUniqueId()) && contract.getStatus() == ContractStatus.TERMINE;
    }

    public static void refreshDelay(Contract contract) {
        if (contract == null) return;
        if (contract.getStatus() == ContractStatus.EN_COURS && contract.getDueAt() > 0 && System.currentTimeMillis() > contract.getDueAt()) {
            contract.setStatus(ContractStatus.EN_RETARD);
            contract.addHistory("§8• §cRetard automatique §7Le délai prévu est dépassé.");
            ContractStorage.save();

            Business business = BusinessManager.getById(contract.getBusinessId());
            AuditLogManager.log(AuditLogType.CONTRACT_COMPLETED, "Système", contract.getTitle(), business, "Mission marquée automatiquement en retard.");

            OfflinePlayer client = Bukkit.getOfflinePlayer(contract.getClientUuid());
            AlertManager.add(client, AlertType.CONTRACT, "Mission en retard", "La mission " + contract.getTitle() + " a dépassé son délai.");
            if (business != null) {
                AlertManager.add(business.getOwnerUuid(), business.getOwnerName(), AlertType.CONTRACT, "Mission en retard", "La mission " + contract.getTitle() + " a dépassé son délai.");
            }
        }
    }

    private static String safeComment(String comment) {
        if (comment == null || comment.isBlank()) return "Aucun commentaire.";
        if (comment.length() > 120) return comment.substring(0, 120) + "...";
        return comment;
    }

    private static void sort(List<Contract> list) {
        list.sort(Comparator.comparingLong(Contract::getCreatedAt).reversed());
    }

    public record ContractResult(boolean success, Contract contract, String message) {
        public static ContractResult success(Contract contract, String message) {
            return new ContractResult(true, contract, message);
        }

        public static ContractResult fail(String message) {
            return new ContractResult(false, null, message);
        }
    }
}
