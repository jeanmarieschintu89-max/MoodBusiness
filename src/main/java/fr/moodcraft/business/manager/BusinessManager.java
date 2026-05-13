package fr.moodcraft.business.manager;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.model.AuditLogType;
import fr.moodcraft.business.model.Business;
import fr.moodcraft.business.model.BusinessRole;
import fr.moodcraft.business.model.BusinessStatus;

import fr.moodcraft.business.storage.BusinessStorage;

import fr.moodcraft.business.util.NameFilter;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class BusinessManager {

    private BusinessManager() {}

    public static void init() {}

    public static CreationResult createBusiness(
            Player player,
            String rawName
    ) {

        if (!Main.getInstance()
                .getConfig()
                .getBoolean(
                        "business.creation.enabled",
                        true
                )) {

            return CreationResult.fail(
                    "La création d'entreprise est temporairement fermée."
            );
        }

        if (!VaultHook.isReady()) {

            return CreationResult.fail(
                    "Le service économique n'est pas disponible."
            );
        }

        if (!player.hasPermission("moodbusiness.create")) {

            return CreationResult.fail(
                    "Vous ne pouvez pas créer d'entreprise."
            );
        }

        UUID uuid =
                player.getUniqueId();

        if (BusinessStorage.isRegisterSuspended(uuid)
                && !player.hasPermission("moodbusiness.bypass")) {

            return CreationResult.fail(
                    "Vous êtes suspendu du Bureau des Entreprises."
            );
        }

        long cooldown =
                BusinessStorage.getCooldownUntil(uuid);

        if (cooldown > System.currentTimeMillis()
                && !player.hasPermission("moodbusiness.bypass")) {

            long seconds =
                    Math.max(
                            1,
                            (cooldown - System.currentTimeMillis()) / 1000
                    );

            return CreationResult.fail(
                    "Vous devez attendre encore §e"
                            + seconds
                            + "s §7avant une nouvelle création."
            );
        }

        int maxActive =
                Main.getInstance()
                        .getConfig()
                        .getInt(
                                "business.creation.max-active-owned",
                                1
                        );

        if (!player.hasPermission("moodbusiness.bypass")
                && countActiveOwned(uuid) >= maxActive) {

            return CreationResult.fail(
                    "Vous avez déjà une entreprise active."
            );
        }

        String name =
                NameFilter.clean(rawName);

        String error =
                NameFilter.validate(name);

        if (error != null) {

            return CreationResult.fail(error);
        }

        //
        // 🔒 NOM DÉJÀ UTILISÉ
        // Une entreprise ACTIVE ou SUSPENDUE bloque le nom.
        // Une entreprise ARCHIVÉE garde son historique, mais le nom peut être réutilisé.
        //

        Business activeSameName =
                getActiveByName(name);

        if (activeSameName != null) {

            return CreationResult.fail(
                    "Ce nom d'entreprise est déjà utilisé."
            );
        }

        String baseId =
                NameFilter.toId(name);

        String id =
                getAvailableBusinessId(
                        baseId,
                        uuid
                );

        double price =
                getCreationPrice(uuid);

        if (!player.hasPermission("moodbusiness.bypass")) {

            if (!VaultHook.has(
                    player,
                    price
            )) {

                return CreationResult.fail(
                        "Fonds insuffisants. Frais requis: §e"
                                + VaultHook.format(price)
                );
            }

            if (!VaultHook.withdraw(
                    player,
                    price
            )) {

                return CreationResult.fail(
                        "Le paiement des frais d'enregistrement a échoué."
                );
            }
        }

        int creationIndex =
                BusinessStorage.getCreatedCount(uuid) + 1;

        Business business =
                new Business(
                        id,
                        name,
                        uuid,
                        player.getName(),
                        BusinessStatus.ACTIVE,
                        Main.getInstance()
                                .getConfig()
                                .getDouble(
                                        "bank.starting-balance",
                                        0
                                ),
                        creationIndex,
                        price,
                        System.currentTimeMillis()
                );

        BusinessStorage.addBusiness(business);

        BusinessStorage.setCreatedCount(
                uuid,
                creationIndex
        );

        AuditLogManager.log(
                AuditLogType.BUSINESS_CREATED,
                player,
                business.getName(),
                business,
                "Entreprise créée avec frais d'enregistrement: "
                        + VaultHook.format(price)
        );

        BusinessNotifyManager.businessCreated(
                player,
                business
        );

        return CreationResult.success(
                business,
                "Entreprise créée."
        );
    }

    public static double getCreationPrice(
            UUID uuid
    ) {

        double base =
                Main.getInstance()
                        .getConfig()
                        .getDouble(
                                "business.creation.base-price",
                                15000
                        );

        double step =
                Main.getInstance()
                        .getConfig()
                        .getDouble(
                                "business.creation.price-step",
                                15000
                        );

        int alreadyCreated =
                BusinessStorage.getCreatedCount(uuid);

        return base + (alreadyCreated * step);
    }

    public static int countActiveOwned(
            UUID uuid
    ) {

        int count = 0;

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.getOwnerUuid().equals(uuid)
                    && business.getStatus() == BusinessStatus.ACTIVE) {

                count++;
            }
        }

        return count;
    }

    public static Business getOwnedBusiness(
            UUID uuid
    ) {

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.getOwnerUuid().equals(uuid)
                    && business.getStatus() == BusinessStatus.ACTIVE) {

                return business;
            }
        }

        return null;
    }

    public static Business getMemberBusiness(
            UUID uuid
    ) {

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.isMember(uuid)
                    && business.getStatus() == BusinessStatus.ACTIVE) {

                return business;
            }
        }

        return null;
    }

    //
    // 🔎 RECHERCHE PUBLIQUE / ADMIN
    // Priorité à une entreprise non archivée.
    // Si seule une archive existe, on peut quand même la retrouver pour consultation admin.
    //

    public static Business getByName(
            String name
    ) {

        String clean =
                NameFilter.clean(name);

        String id =
                NameFilter.toId(clean);

        Business direct =
                BusinessStorage.getBusiness(id);

        if (direct != null
                && direct.getStatus() != BusinessStatus.ARCHIVEE) {

            return direct;
        }

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.getName().equalsIgnoreCase(clean)
                    && business.getStatus() != BusinessStatus.ARCHIVEE) {

                return business;
            }
        }

        if (direct != null) {
            return direct;
        }

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.getName().equalsIgnoreCase(clean)) {

                return business;
            }
        }

        return null;
    }

    //
    // 🔎 RECHERCHE NOM ACTIF
    // Utilisé pour bloquer uniquement les noms encore actifs/suspendus.
    //

    public static Business getActiveByName(
            String name
    ) {

        String clean =
                NameFilter.clean(name);

        String id =
                NameFilter.toId(clean);

        Business direct =
                BusinessStorage.getBusiness(id);

        if (direct != null
                && direct.getStatus() != BusinessStatus.ARCHIVEE) {

            return direct;
        }

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.getName().equalsIgnoreCase(clean)
                    && business.getStatus() != BusinessStatus.ARCHIVEE) {

                return business;
            }
        }

        return null;
    }

    public static Business getById(
            String id
    ) {

        return BusinessStorage.getBusiness(id);
    }

    public static List<Business> getAll() {

        return new ArrayList<>(
                BusinessStorage.getBusinesses()
        );
    }

    public static List<Business> getByStatus(
            BusinessStatus status
    ) {

        List<Business> list =
                new ArrayList<>();

        for (Business business :
                BusinessStorage.getBusinesses()) {

            if (business.getStatus() == status) {

                list.add(business);
            }
        }

        list.sort(
                Comparator.comparingLong(
                        Business::getCreatedAt
                ).reversed()
        );

        return list;
    }

    public static List<Business> getRecent() {

        List<Business> list =
                getAll();

        list.sort(
                Comparator.comparingLong(
                        Business::getCreatedAt
                ).reversed()
        );

        return list;
    }

    public static void setStatus(
            Business business,
            BusinessStatus status
    ) {

        business.setStatus(status);
        BusinessStorage.save();
    }

    public static BusinessRole getRole(
            Player player,
            Business business
    ) {

        if (player == null || business == null) {
            return null;
        }

        return business.getRole(
                player.getUniqueId()
        );
    }

    public static boolean canSeeEmployees(
            Player player,
            Business business
    ) {

        return player != null
                && business != null
                && business.isMember(player.getUniqueId());
    }

    public static boolean canManageRoles(
            Player player,
            Business business
    ) {

        BusinessRole role =
                getRole(
                        player,
                        business
                );

        return role != null
                && role.canManageRoles();
    }

    public static boolean canManageContracts(
            Player player,
            Business business
    ) {

        BusinessRole role =
                getRole(
                        player,
                        business
                );

        return role != null
                && role.canManageContracts();
    }

    public static boolean canAssignRole(
            Player actor,
            Business business,
            UUID targetUuid,
            BusinessRole newRole
    ) {

        if (actor == null
                || business == null
                || targetUuid == null
                || newRole == null) {

            return false;
        }

        UUID actorUuid =
                actor.getUniqueId();

        if (!business.isMember(actorUuid)) {
            return false;
        }

        if (!business.isMember(targetUuid)) {
            return false;
        }

        if (actorUuid.equals(targetUuid)) {
            return false;
        }

        if (business.isOwner(targetUuid)) {
            return false;
        }

        if (newRole == BusinessRole.DIRIGEANT) {
            return false;
        }

        BusinessRole actorRole =
                business.getRole(actorUuid);

        BusinessRole targetRole =
                business.getRole(targetUuid);

        if (actorRole == null || targetRole == null) {
            return false;
        }

        if (actorRole == BusinessRole.DIRIGEANT) {
            return true;
        }

        if (actorRole == BusinessRole.GERANT) {

            if (targetRole == BusinessRole.GERANT
                    || targetRole == BusinessRole.DIRIGEANT) {

                return false;
            }

            return newRole != BusinessRole.GERANT
                    && newRole != BusinessRole.DIRIGEANT;
        }

        return false;
    }

    public static ActionResult addMember(
            Player actor,
            Business business,
            OfflinePlayer target,
            BusinessRole role
    ) {

        if (actor == null || business == null || target == null) {

            return ActionResult.fail(
                    "Dossier invalide."
            );
        }

        if (!canManageRoles(actor, business)) {

            return ActionResult.fail(
                    "Vous ne pouvez pas recruter dans cette entreprise."
            );
        }

        if (role == null) {
            role = BusinessRole.STAGIAIRE;
        }

        if (role == BusinessRole.DIRIGEANT) {

            return ActionResult.fail(
                    "Le rôle dirigeant ne peut pas être attribué ici."
            );
        }

        UUID targetUuid =
                target.getUniqueId();

        if (business.isMember(targetUuid)) {

            return ActionResult.fail(
                    "Ce joueur fait déjà partie de l'entreprise."
            );
        }

        String name =
                target.getName() != null
                        ? target.getName()
                        : "Inconnu";

        business.addMember(
                targetUuid,
                name,
                role
        );

        BusinessStorage.save();

        AuditLogManager.log(
                AuditLogType.MEMBER_ADDED,
                actor,
                name,
                business,
                "Membre recruté avec rôle: "
                        + role.getDisplayName()
        );

        BusinessNotifyManager.memberJoined(
                target,
                business,
                role
        );

        return ActionResult.success(
                "Joueur recruté: §e"
                        + name
                        + " §7en tant que "
                        + role.getDisplayName()
        );
    }

    public static ActionResult assignRole(
            Player actor,
            Business business,
            UUID targetUuid,
            BusinessRole role
    ) {

        if (!canAssignRole(
                actor,
                business,
                targetUuid,
                role
        )) {

            return ActionResult.fail(
                    "Vous ne pouvez pas attribuer ce rôle."
            );
        }

        business.setRole(
                targetUuid,
                role
        );

        OfflinePlayer offline =
                Bukkit.getOfflinePlayer(targetUuid);

        if (offline.getName() != null) {

            business.setMemberName(
                    targetUuid,
                    offline.getName()
            );
        }

        BusinessStorage.save();

        AuditLogManager.log(
                AuditLogType.ROLE_CHANGED,
                actor,
                business.getMemberName(targetUuid),
                business,
                "Nouveau rôle: "
                        + role.getDisplayName()
        );

        BusinessNotifyManager.roleChanged(
                offline,
                business,
                role
        );

        return ActionResult.success(
                "Rôle mis à jour pour §e"
                        + business.getMemberName(targetUuid)
                        + "§7: "
                        + role.getDisplayName()
        );
    }

    public static ActionResult removeMember(
            Player actor,
            Business business,
            UUID targetUuid
    ) {

        if (actor == null || business == null || targetUuid == null) {

            return ActionResult.fail(
                    "Dossier invalide."
            );
        }

        if (!canManageRoles(actor, business)) {

            return ActionResult.fail(
                    "Vous ne pouvez pas renvoyer de membre."
            );
        }

        if (!business.isMember(targetUuid)) {

            return ActionResult.fail(
                    "Ce joueur n'est pas dans l'entreprise."
            );
        }

        if (business.isOwner(targetUuid)) {

            return ActionResult.fail(
                    "Le dirigeant ne peut pas être renvoyé."
            );
        }

        BusinessRole actorRole =
                business.getRole(actor.getUniqueId());

        BusinessRole targetRole =
                business.getRole(targetUuid);

        if (actorRole == BusinessRole.GERANT
                && targetRole != null
                && targetRole.getPower() >= BusinessRole.GERANT.getPower()) {

            return ActionResult.fail(
                    "Un gérant ne peut pas renvoyer un rôle équivalent ou supérieur."
            );
        }

        String name =
                business.getMemberName(targetUuid);

        OfflinePlayer offline =
                Bukkit.getOfflinePlayer(targetUuid);

        business.removeMember(targetUuid);

        BusinessStorage.save();

        AuditLogManager.log(
                AuditLogType.MEMBER_REMOVED,
                actor,
                name,
                business,
                "Membre retiré de l'entreprise."
        );

        BusinessNotifyManager.memberRemoved(
                offline,
                business
        );

        return ActionResult.success(
                "Membre retiré de l'entreprise: §e" + name
        );
    }

    public static UUID getMemberUuidByName(
            Business business,
            String name
    ) {

        if (business == null || name == null) {
            return null;
        }

        for (UUID uuid : business.getMembers().keySet()) {

            if (business.getMemberName(uuid)
                    .equalsIgnoreCase(name)) {

                return uuid;
            }
        }

        return null;
    }

    //
    // 🔁 ID DISPONIBLE
    //

    private static String getAvailableBusinessId(
            String baseId,
            UUID ownerUuid
    ) {

        if (!BusinessStorage.exists(baseId)) {
            return baseId;
        }

        int created =
                BusinessStorage.getCreatedCount(ownerUuid) + 1;

        String id =
                baseId + "-" + created;

        int tries =
                1;

        while (BusinessStorage.exists(id)) {

            tries++;

    