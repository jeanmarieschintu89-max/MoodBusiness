package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.RequestCategory;
import fr.moodcraft.business.model.RequestStatus;

import fr.moodcraft.business.storage.RequestStorage;

import fr.moodcraft.business.util.TimeUtil;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class RequestManager {

    private RequestManager() {}

    public static RequestResult createRequest(
            Player player,
            RequestCategory category,
            String title,
            String description,
            double budget,
            int dueDays
    ) {

        if (player == null) {

            return RequestResult.fail(
                    "Joueur invalide."
            );
        }

        if (!Main.getInstance()
                .getConfig()
                .getBoolean(
                        "requests.enabled",
                        true
                )) {

            return RequestResult.fail(
                    "Les demandes économiques sont temporairement fermées."
            );
        }

        if (category == null) {
            category = RequestCategory.AUTRE;
        }

        if (title == null || title.trim().length() < 3) {

            return RequestResult.fail(
                    "Le titre est trop court."
            );
        }

        if (description == null || description.trim().length() < 10) {

            return RequestResult.fail(
                    "La description est trop courte."
            );
        }

        if (budget <= 0) {

            return RequestResult.fail(
                    "Le budget doit être supérieur à zéro."
            );
        }

        if (dueDays <= 0) {

            return RequestResult.fail(
                    "Le délai doit être supérieur à zéro."
            );
        }

        int max =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "requests.max-active-per-player",
                                5
                        );

        if (countActiveByPlayer(
                player.getUniqueId()
        ) >= max) {

            return RequestResult.fail(
                    "Vous avez déjà trop de demandes actives."
            );
        }

        long now =
                System.currentTimeMillis();

        int expireDays =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "requests.expire-after-days",
                                14
                        );

        String id =
                now
                        + "-"
                        + player.getUniqueId()
                        .toString()
                        .substring(0, 8);

        BusinessRequest request =
                new BusinessRequest(
                        id,
                        player.getUniqueId(),
                        player.getName(),
                        title.trim(),
                        description.trim(),
                        budget,
                        dueDays,
                        category,
                        RequestStatus.PUBLIEE,
                        now,
                        now,
                        now + TimeUtil.days(expireDays),
                        ""
                );

        RequestStorage.add(request);

        return RequestResult.success(
                request,
                "Demande publiée."
        );
    }

    public static BusinessRequest get(
            String id
    ) {

        BusinessRequest request =
                RequestStorage.get(id);

        if (request != null) {
            refreshExpiration(request);
        }

        return request;
    }

    public static List<BusinessRequest> getPublicOpen() {

        List<BusinessRequest> list =
                new ArrayList<>();

        for (BusinessRequest request :
                RequestStorage.getAll()) {

            refreshExpiration(request);

            if (request.getStatus().isOpen()
                    && !request.isExpired()) {

                list.add(request);
            }
        }

        sort(list);

        return list;
    }

    public static List<BusinessRequest> getByPlayer(
            UUID uuid
    ) {

        List<BusinessRequest> list =
                new ArrayList<>();

        for (BusinessRequest request :
                RequestStorage.getAll()) {

            refreshExpiration(request);

            if (request.getCreatorUuid().equals(uuid)) {

                list.add(request);
            }
        }

        sort(list);

        return list;
    }

    public static int countActiveByPlayer(
            UUID uuid
    ) {

        int count = 0;

        for (BusinessRequest request :
                RequestStorage.getAll()) {

            refreshExpiration(request);

            if (request.getCreatorUuid().equals(uuid)
                    && request.getStatus().isOpen()
                    && !request.isExpired()) {

                count++;
            }
        }

        return count;
    }

    public static boolean canManageRequest(
            Player player,
            BusinessRequest request
    ) {

        return player != null
                && request != null
                && request.getCreatorUuid().equals(
                player.getUniqueId()
        );
    }

    public static RequestResult cancel(
            Player player,
            BusinessRequest request
    ) {

        if (!canManageRequest(
                player,
                request
        )) {

            return RequestResult.fail(
                    "Vous ne pouvez pas annuler cette demande."
            );
        }

        if (!request.getStatus().isOpen()) {

            return RequestResult.fail(
                    "Cette demande n'est plus active."
            );
        }

        request.setStatus(
                RequestStatus.ANNULEE
        );

        RequestStorage.save();

        return RequestResult.success(
                request,
                "Demande annulée."
        );
    }

    public static void refreshExpiration(
            BusinessRequest request
    ) {

        if (request == null) {
            return;
        }

        if (request.getStatus().isOpen()
                && request.isExpired()) {

            request.setStatus(
                    RequestStatus.EXPIREE
            );

            RequestStorage.save();
        }
    }

    private static void sort(
            List<BusinessRequest> list
    ) {

        list.sort(
                Comparator.comparingLong(
                        BusinessRequest::getCreatedAt
                ).reversed()
        );
    }

    public record RequestResult(
            boolean success,
            BusinessRequest request,
            String message
    ) {

        public static RequestResult success(
                BusinessRequest request,
                String message
        ) {

            return new RequestResult(
                    true,
                    request,
                    message
            );
        }

        public static RequestResult fail(
                String message
        ) {

            return new RequestResult(
                    false,
                    null,
                    message
            );
        }
    }
}