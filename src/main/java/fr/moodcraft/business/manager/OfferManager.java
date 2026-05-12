package fr.moodcraft.business.manager;

import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.Offer;
import fr.moodcraft.business.model.OfferStatus;
import fr.moodcraft.business.model.RequestStatus;

import fr.moodcraft.business.storage.OfferStorage;
import fr.moodcraft.business.storage.RequestStorage;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class OfferManager {

    private OfferManager() {}

    public static OfferResult createOffer(
            Player player,
            Business business,
            BusinessRequest request,
            double amount,
            int dueDays,
            String comment
    ) {

        if (player == null || business == null || request == null) {

            return OfferResult.fail(
                    "Dossier d'offre invalide."
            );
        }

        if (!business.isActive()) {

            return OfferResult.fail(
                    "Votre entreprise n'est pas active."
            );
        }

        if (!BusinessManager.canManageContracts(
                player,
                business
        )) {

            return OfferResult.fail(
                    "Votre rôle ne permet pas d'envoyer une offre."
            );
        }

        if (!request.getStatus().isOpen()
                || request.isExpired()) {

            return OfferResult.fail(
                    "Cette demande n'est plus ouverte."
            );
        }

        if (request.getCreatorUuid().equals(
                player.getUniqueId()
        )) {

            return OfferResult.fail(
                    "Vous ne pouvez pas répondre à votre propre demande."
            );
        }

        if (hasActiveOffer(
                request.getId(),
                business.getId()
        )) {

            return OfferResult.fail(
                    "Votre entreprise a déjà une offre active sur cette demande."
            );
        }

        if (amount <= 0) {

            return OfferResult.fail(
                    "Le montant doit être supérieur à zéro."
            );
        }

        if (dueDays <= 0) {

            return OfferResult.fail(
                    "Le délai doit être supérieur à zéro."
            );
        }

        long now =
                System.currentTimeMillis();

        String id =
                now
                        + "-"
                        + business.getId();

        Offer offer =
                new Offer(
                        id,
                        request.getId(),
                        business.getId(),
                        business.getName(),
                        player.getUniqueId(),
                        player.getName(),
                        amount,
                        dueDays,
                        comment != null
                                ? comment
                                : "",
                        OfferStatus.EN_ATTENTE,
                        now,
                        now
                );

        OfferStorage.add(offer);

        if (request.getStatus() == RequestStatus.PUBLIEE) {

            request.setStatus(
                    RequestStatus.OFFRES_RECUES
            );

            RequestStorage.save();
        }

        return OfferResult.success(
                offer,
                "Offre envoyée."
        );
    }

    public static boolean hasActiveOffer(
            String requestId,
            String businessId
    ) {

        for (Offer offer :
                OfferStorage.getAll()) {

            if (offer.getRequestId().equalsIgnoreCase(requestId)
                    && offer.getBusinessId().equalsIgnoreCase(businessId)
                    && offer.getStatus().isActive()) {

                return true;
            }
        }

        return false;
    }

    public static List<Offer> getByRequest(
            String requestId
    ) {

        List<Offer> list =
                new ArrayList<>();

        for (Offer offer :
                OfferStorage.getAll()) {

            if (offer.getRequestId().equalsIgnoreCase(requestId)) {

                list.add(offer);
            }
        }

        list.sort(
                Comparator.comparingLong(
                        Offer::getCreatedAt
                ).reversed()
        );

        return list;
    }

    public static Offer get(
            String id
    ) {

        return OfferStorage.get(id);
    }

    public static OfferResult acceptOffer(
            Player player,
            BusinessRequest request,
            Offer offer
    ) {

        if (player == null || request == null || offer == null) {

            return OfferResult.fail(
                    "Dossier d'offre invalide."
            );
        }

        if (!RequestManager.canManageRequest(
                player,
                request
        )) {

            return OfferResult.fail(
                    "Vous ne pouvez pas accepter cette offre."
            );
        }

        if (!request.getStatus().isOpen()) {

            return OfferResult.fail(
                    "Cette demande n'est plus ouverte."
            );
        }

        if (offer.getStatus() != OfferStatus.EN_ATTENTE) {

            return OfferResult.fail(
                    "Cette offre n'est plus en attente."
            );
        }

        ContractManager.ContractResult contractResult =
                ContractManager.createFromOffer(
                        player,
                        request,
                        offer
                );

        if (!contractResult.success()) {

            return OfferResult.fail(
                    contractResult.message()
            );
        }

        for (Offer other :
                getByRequest(request.getId())) {

            if (other.getId().equals(offer.getId())) {

                other.setStatus(
                        OfferStatus.ACCEPTEE
                );

            } else if (other.getStatus() == OfferStatus.EN_ATTENTE) {

                other.setStatus(
                        OfferStatus.REFUSEE
                );
            }
        }

        request.setAcceptedOfferId(
                offer.getId()
        );

        request.setStatus(
                RequestStatus.TRANSFORMEE_CONTRAT
        );

        OfferStorage.save();
        RequestStorage.save();

        return OfferResult.success(
                offer,
                "Offre acceptée. Contrat sécurisé créé."
        );
    }

    public record OfferResult(
            boolean success,
            Offer offer,
            String message
    ) {

        public static OfferResult success(
                Offer offer,
                String message
        ) {

            return new OfferResult(
                    true,
                    offer,
                    message
            );
        }

        public static OfferResult fail(
                String message
        ) {

            return new OfferResult(
                    false,
                    null,
                    message
            );
        }
    }
}