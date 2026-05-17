package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;
import fr.moodcraft.business.model.AlertType;
import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRequest;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.RequestCategory;
import fr.moodcraft.business.model.RequestStatus;
import fr.moodcraft.business.storage.RequestStorage;
import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.TimeUtil;
import fr.moodcraft.business.util.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class RequestManager {

    private static final Map<UUID, Long> LAST_MISSION_NOTICE = new HashMap<>();
    private static final long NOTICE_COOLDOWN_MS = 5L * 60L * 1000L;

    private RequestManager() {}

    public static RequestResult createRequest(Player player, RequestCategory category, String title, String description, double budget, int dueDays) {
        if (player == null) return RequestResult.fail("Joueur invalide.");

        if (!Main.getInstance().getConfig().getBoolean("requests.enabled", true)) {
            return RequestResult.fail("Les missions sont temporairement fermées.");
        }

        if (category == null) category = RequestCategory.AUTRE;
        if (title == null || title.trim().length() < 3) return RequestResult.fail("Le titre est trop court.");
        if (description == null || description.trim().length() < 10) return RequestResult.fail("La description est trop courte.");
        if (budget <= 0) return RequestResult.fail("Le prix doit être supérieur à zéro.");
        if (dueDays <= 0) return RequestResult.fail("Le délai doit être supérieur à zéro.");

        int max = Main.getInstance().getConfig().getInt("requests.max-active-per-player", 5);
        if (countActiveByPlayer(player.getUniqueId()) >= max) {
            return RequestResult.fail("Vous avez déjà trop de missions actives.");
        }

        long now = System.currentTimeMillis();
        int expireDays = Main.getInstance().getConfig().getInt("requests.expire-after-days", 14);
        String id = now + "-" + player.getUniqueId().toString().substring(0, 8);

        BusinessRequest request = new BusinessRequest(
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

        AuditLogManager.log(
                AuditLogType.REQUEST_CREATED,
                player,
                request.getTitle(),
                null,
                "Mission créée. Prix: " + VaultHook.format(budget) + ", délai: " + dueDays + " jour(s), catégorie: " + category.getDisplayName()
        );

        AlertManager.add(
                player,
                AlertType.REQUEST,
                "Mission publiée",
                "Votre mission " + request.getTitle() + " est visible par les entreprises."
        );

        notifyBusinesses(request);

        return RequestResult.success(request, "Mission publiée.");
    }

    public static BusinessRequest get(String id) {
        BusinessRequest request = RequestStorage.get(id);
        if (request != null) refreshExpiration(request);
        return request;
    }

    public static List<BusinessRequest> getPublicOpen() {
        List<BusinessRequest> list = new ArrayList<>();
        for (BusinessRequest request : RequestStorage.getAll()) {
            refreshExpiration(request);
            if (request.getStatus().isOpen() && !request.isExpired()) list.add(request);
        }
        sort(list);
        return list;
    }

    public static List<BusinessRequest> getByPlayer(UUID uuid) {
        List<BusinessRequest> list = new ArrayList<>();
        for (BusinessRequest request : RequestStorage.getAll()) {
            refreshExpiration(request);
            if (request.getCreatorUuid().equals(uuid)) list.add(request);
        }
        sort(list);
        return list;
    }

    public static int countActiveByPlayer(UUID uuid) {
        int count = 0;
        for (BusinessRequest request : RequestStorage.getAll()) {
            refreshExpiration(request);
            if (request.getCreatorUuid().equals(uuid) && request.getStatus().isOpen() && !request.isExpired()) count++;
        }
        return count;
    }

    public static boolean canManageRequest(Player player, BusinessRequest request) {
        return player != null && request != null && request.getCreatorUuid().equals(player.getUniqueId());
    }

    public static RequestResult cancel(Player player, BusinessRequest request) {
        if (!canManageRequest(player, request)) return RequestResult.fail("Vous ne pouvez pas annuler cette mission.");
        if (!request.getStatus().isOpen()) return RequestResult.fail("Cette mission n'est plus active.");

        request.setStatus(RequestStatus.ANNULEE);
        RequestStorage.save();

        AuditLogManager.log(AuditLogType.REQUEST_CREATED, player, request.getTitle(), null, "Mission annulée.");
        AlertManager.add(player, AlertType.REQUEST, "Mission annulée", "Votre mission " + request.getTitle() + " a été annulée.");
        return RequestResult.success(request, "Mission annulée.");
    }

    public static void refreshExpiration(BusinessRequest request) {
        if (request == null) return;
        if (request.getStatus().isOpen() && request.isExpired()) {
            request.setStatus(RequestStatus.EXPIREE);
            RequestStorage.save();
        }
    }

    private static void notifyBusinesses(BusinessRequest request) {
        long now = System.currentTimeMillis();
        Set<UUID> notified = new HashSet<>();

        for (Business business : BusinessManager.getAll()) {
            if (business == null || !business.isActive()) continue;
            for (Map.Entry<UUID, BusinessRole> entry : business.getMembers().entrySet()) {
                UUID uuid = entry.getKey();
                BusinessRole role = entry.getValue();
                if (uuid == null || role == null || !role.canManageContracts()) continue;
                if (!notified.add(uuid)) continue;
                if (request.getCreatorUuid().equals(uuid)) continue;

                long last = LAST_MISSION_NOTICE.getOrDefault(uuid, 0L);
                if (now - last < NOTICE_COOLDOWN_MS) continue;

                Player target = Bukkit.getPlayer(uuid);
                if (target == null || !target.isOnline()) continue;

                LAST_MISSION_NOTICE.put(uuid, now);
                BusinessMessages.header(target, "Nouvelle mission");
                target.sendMessage("§e➜ §fUne nouvelle mission est disponible.");
                target.sendMessage("§8• §7Mission : §e" + shortText(request.getTitle(), 28));
                target.sendMessage("§8• §7Client : §e" + shortText(request.getCreatorName(), 18));
                target.sendMessage("§8• §7Type : " + request.getCategory().getDisplayName());
                target.sendMessage("§8• §7Prix : §e" + VaultHook.format(request.getBudget()));
                target.sendMessage("§8• §7Délai : §b" + request.getDueDays() + "j");
                target.sendMessage("");
                target.sendMessage("§e/entreprise §7→ §fMon entreprise §7→ §fPrendre une mission");
                target.sendMessage("§8• §7Rappel limité pour éviter le spam.");
                BusinessMessages.footer(target);
                target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.65f, 1.35f);
            }
        }
    }

    private static void sort(List<BusinessRequest> list) {
        list.sort(Comparator.comparingLong(BusinessRequest::getCreatedAt).reversed());
    }

    private static String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    public record RequestResult(boolean success, BusinessRequest request, String message) {
        public static RequestResult success(BusinessRequest request, String message) {
            return new RequestResult(true, request, message);
        }

        public static RequestResult fail(String message) {
            return new RequestResult(false, null, message);
        }
    }
}
