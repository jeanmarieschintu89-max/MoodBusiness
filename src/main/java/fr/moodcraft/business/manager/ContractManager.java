package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;
import fr.moodcraft.business.model.Offer;

import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.ContractStorage;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ContractManager {

    private ContractManager() {}

    public static ContractResult createFromOffer(
            Player client,
            BusinessRequest request,
            Offer offer
    ) {

        if (client == null || request == null || offer == null) {

            return ContractResult.fail(
                    "Dossier de contrat invalide."
            );
        }

        if (!request.getCreatorUuid().equals(
                client.getUniqueId()
        )) {

            return ContractResult.fail(
                    "Vous ne pouvez pas accepter cette offre."
            );
        }

        Business business =
                BusinessManager.getById(
                        offer.getBusinessId()
                );

        if (business == null) {

            return ContractResult.fail(
                    "Entreprise introuvable."
            );
        }

        if (!business.isActive()) {

            return ContractResult.fail(
                    "Cette entreprise n'est pas active."
            );
        }

        double gross =
                offer.getAmount();

        if (!VaultHook.has(
                client,
                gross
        )) {

            return ContractResult.fail(
                    "Fonds insuffisants. Montant requis: §e"
                            + VaultHook.format(gross)
            );
        }

        if (!VaultHook.withdraw(
                client,
                gross
        )) {

            return ContractResult.fail(
                    "Le blocage des fonds a échoué."
            );
        }

        double taxRate =
                Main.getInstance()
                        .getConfig()
                        .getDouble(
                                "economy.tax-rate",
                                20.0
                        );

        double taxAmount =
                Math.max(
                        0,
                        gross * (taxRate / 100.0)
                );

        double net =
                Math.max(
                        0,
                        gross - taxAmount
                );

        long now =
                System.currentTimeMillis();

        String id =
                "C-"
                        + now
                        + "-"
                        + business.getId();

        Contract contract =
                new Contract(
                        id,
                        request.getId(),
                        offer.getId(),
                        client.getUniqueId(),
                        client.getName(),
                        business.getId(),
                        business.getName(),
                        offer.getSenderUuid(),
                        offer.getSenderName(),
                        request.getTitle(),
                        offer.getComment(),
                        gross,
                        taxRate,
                        taxAmount,
                        net,
                        gross,
                        offer.getDueDays(),
                        ContractStatus.EN_COURS,
                        now,
                        now,
                        now + TimeUtil.days(offer.getDueDays()),
                        0,
                        0,
                        new ArrayList<>()
                );

        contract.addHistory(
                "§8• §eCréé §7par §f"
                        + client.getName()
                        + " §8• §7Fonds bloqués: §e"
                        + VaultHook.format(gross)
        );

        contract.addHistory(
                "§8• §bOffre acceptée §7Entreprise: §f"
                        + business.getName()
                        + " §8• §7Délai: §b"
                        + offer.getDueDays()
                        + " jours"
        );

        contract.addHistory(
                "§8• §6Fiscalité prévue §7Taxe: §c"
                        + VaultHook.format(taxAmount)
                        + " §8("
                        + taxRate
                        + "%§8)"
        );

        ContractStorage.add(contract);

        return ContractResult.success(
                contract,
                "Contrat sécurisé créé."
        );
    }

    public static Contract get(
            String id
    ) {

        Contract contract =
                ContractStorage.get(id);

        if (contract != null) {

            refreshDelay(contract);
        }

        return contract;
    }

    public static List<Contract> getByClient(
            Player player
    ) {

        List<Contract> list =
                new ArrayList<>();

        for (Contract contract :
                ContractStorage.getAll()) {

            refreshDelay(contract);

            if (contract.getClientUuid().equals(
                    player.getUniqueId()
            )) {

                list.add(contract);
            }
        }

        sort(list);

        return list;
    }

    public static List<Contract> getByBusiness(
            Business business
    ) {

        List<Contract> list =
                new ArrayList<>();

        if (business == null) {
            return list;
        }

        for (Contract contract :
                ContractStorage.getAll()) {

            refreshDelay(contract);

            if (contract.getBusinessId().equalsIgnoreCase(
                    business.getId()
            )) {

                list.add(contract);
            }
        }

        sort(list);

        return list;
    }

    public static List<Contract> getLitiges() {

        List<Contract> list =
                new ArrayList<>();

        for (Contract contract :
                ContractStorage.getAll()) {

            if (contract.getStatus() == ContractStatus.LITIGE) {

                list.add(contract);
            }
        }

        sort(list);

        return list;
    }

    public static ContractResult complete(
            Player actor,
            Contract contract,
            String comment
    ) {

        if (actor == null || contract == null) {

            return ContractResult.fail(
                    "Contrat invalide."
            );
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        if (business == null) {

            return ContractResult.fail(
                    "Entreprise introuvable."
            );
        }

        if (!BusinessManager.canManageContracts(
                actor,
                business
        )) {

            return ContractResult.fail(
                    "Votre rôle ne permet pas de terminer ce contrat."
            );
        }

        if (contract.getStatus() != ContractStatus.EN_COURS
                && contract.getStatus() != ContractStatus.EN_RETARD) {

            return ContractResult.fail(
                    "Ce contrat ne peut pas être marqué comme terminé."
            );
        }

        long now =
                System.currentTimeMillis();

        int validationHours =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "contracts.validation-delay-hours",
                                48
                        );

        contract.setStatus(
                ContractStatus.TERMINE
        );

        contract.setCompletedAt(now);
        contract.setValidateBefore(
                now + TimeUtil.hours(validationHours)
        );

        contract.addHistory(
                "§8• §aTerminé §7par §f"
                        + actor.getName()
                        + " §8• §7"
                        + safeComment(comment)
        );

        ContractStorage.save();

        return ContractResult.success(
                contract,
                "Contrat marqué comme terminé."
        );
    }

    public static ContractResult validate(
            Player client,
            Contract contract
    ) {

        if (client == null || contract == null) {

            return ContractResult.fail(
                    "Contrat invalide."
            );
        }

        if (!contract.getClientUuid().equals(
                client.getUniqueId()
        )) {

            return ContractResult.fail(
                    "Seul le client peut valider ce contrat."
            );
        }

        if (contract.getStatus() != ContractStatus.TERMINE) {

            return ContractResult.fail(
                    "Ce contrat doit être terminé avant validation."
            );
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        if (business == null) {

            return ContractResult.fail(
                    "Entreprise introuvable."
            );
        }

        business.setBalance(
                business.getBalance()
                        + contract.getNetAmount()
        );

        contract.setEscrowAmount(0);
        contract.setStatus(
                ContractStatus.VALIDE
        );

        contract.addHistory(
                "§8• §6Validé §7par §f"
                        + client.getName()
                        + " §8• §7Versement net: §a"
                        + VaultHook.format(contract.getNetAmount())
        );

        contract.addHistory(
                "§8• §cTaxe économique §7prélevée: §c"
                        + VaultHook.format(contract.getTaxAmount())
                        + " §8("
                        + contract.getTaxRate()
                        + "%§8)"
        );

        BusinessStorage.save();
        ContractStorage.save();

        return ContractResult.success(
                contract,
                "Contrat validé. Paiement versé à l'entreprise."
        );
    }

    public static ContractResult openLitige(
            Player actor,
            Contract contract,
            String reason
    ) {

        if (actor == null || contract == null) {

            return ContractResult.fail(
                    "Contrat invalide."
            );
        }

        boolean client =
                contract.getClientUuid().equals(
                        actor.getUniqueId()
                );

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        boolean businessActor =
                business != null
                        && BusinessManager.canManageContracts(
                        actor,
                        business
                );

        if (!client && !businessActor) {

            return ContractResult.fail(
                    "Vous ne pouvez pas ouvrir un litige sur ce contrat."
            );
        }

        if (contract.getStatus() == ContractStatus.VALIDE
                || contract.getStatus() == ContractStatus.ANNULE) {

            return ContractResult.fail(
                    "Ce contrat est déjà clôturé."
            );
        }

        contract.setStatus(
                ContractStatus.LITIGE
        );

        contract.addHistory(
                "§8• §cLitige ouvert §7par §f"
                        + actor.getName()
                        + " §8• §7"
                        + safeComment(reason)
        );

        ContractStorage.save();

        return ContractResult.success(
                contract,
                "Litige ouvert. Les fonds restent bloqués."
        );
    }

    public static boolean canView(
            Player player,
            Contract contract
    ) {

        if (player == null || contract == null) {
            return false;
        }

        if (contract.getClientUuid().equals(
                player.getUniqueId()
        )) {

            return true;
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        return business != null
                && business.isMember(
                player.getUniqueId()
        );
    }

    public static boolean canBusinessComplete(
            Player player,
            Contract contract
    ) {

        if (player == null || contract == null) {
            return false;
        }

        Business business =
                BusinessManager.getById(
                        contract.getBusinessId()
                );

        return business != null
                && BusinessManager.canManageContracts(
                player,
                business
        );
    }

    public static boolean canClientValidate(
            Player player,
            Contract contract
    ) {

        return player != null
                && contract != null
                && contract.getClientUuid().equals(
                player.getUniqueId()
        )
                && contract.getStatus() == ContractStatus.TERMINE;
    }

    public static void refreshDelay(
            Contract contract
    ) {

        if (contract == null) {
            return;
        }

        if (contract.getStatus() == ContractStatus.EN_COURS
                && contract.getDueAt() > 0
                && System.currentTimeMillis() > contract.getDueAt()) {

            contract.setStatus(
                    ContractStatus.EN_RETARD
            );

            contract.addHistory(
                    "§8• §cRetard automatique §7Le délai prévu est dépassé."
            );

            ContractStorage.save();
        }
    }

    private static String safeComment(
            String comment
    ) {

        if (comment == null || comment.isBlank()) {
            return "Aucun commentaire.";
        }

        if (comment.length() > 120) {
            return comment.substring(0, 120) + "...";
        }

        return comment;
    }

    private static void sort(
            List<Contract> list
    ) {

        list.sort(
                Comparator.comparingLong(
                        Contract::getCreatedAt
                ).reversed()
        );
    }

    public record ContractResult(
            boolean success,
            Contract contract,
            String message
    ) {

        public static ContractResult success(
                Contract contract,
                String message
        ) {

            return new ContractResult(
                    true,
                    contract,
                    message
            );
        }

        public static ContractResult fail(
                String message
        ) {

            return new ContractResult(
                    false,
                    null,
                    message
            );
        }
    }
}