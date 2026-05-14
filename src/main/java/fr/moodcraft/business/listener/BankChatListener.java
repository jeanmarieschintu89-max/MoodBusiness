package fr.moodcraft.business.listener;

import fr.moodcraft.business.Main;

import fr.moodcraft.business.gui.BusinessBankGUI;

import fr.moodcraft.business.manager.BusinessBankManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.model.Business;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BankChatListener
        implements Listener {

    private static final Map<UUID, Draft> DRAFTS =
            new HashMap<>();

    public static void startDeposit(
            Player p,
            Business business
    ) {

        start(
                p,
                business,
                Mode.DEPOSIT
        );
    }

    public static void startWithdraw(
            Player p,
            Business business
    ) {

        start(
                p,
                business,
                Mode.WITHDRAW
        );
    }

    public static void startPrime(
            Player p,
            Business business
    ) {

        start(
                p,
                business,
                Mode.PRIME_TARGET
        );
    }

    private static void start(
            Player p,
            Business business,
            Mode mode
    ) {

        if (p == null || business == null || mode == null) {
            return;
        }

        DRAFTS.put(
                p.getUniqueId(),
                new Draft(
                        business.getId(),
                        mode
                )
        );

        p.closeInventory();

        BusinessMessages.header(
                p,
                "Banque Entreprise"
        );

        if (mode == Mode.DEPOSIT) {

            p.sendMessage("§e➜ §fÉcris le montant à déposer.");
            p.sendMessage("§8• §7Entreprise : §e" + business.getName());
            p.sendMessage("");
            p.sendMessage("§8• §7Exemple : §e5000");
            p.sendMessage("§8• §7Les fonds seront retirés de votre argent personnel.");

        } else if (mode == Mode.WITHDRAW) {

            p.sendMessage("§e➜ §fÉcris le montant à retirer.");
            p.sendMessage("§8• §7Entreprise : §e" + business.getName());
            p.sendMessage("");
            p.sendMessage("§8• §7Exemple : §e5000");
            p.sendMessage("§8• §7Les fonds seront versés sur votre compte personnel.");

        } else {

            p.sendMessage("§e➜ §fÉcris le pseudo du membre à primer.");
            p.sendMessage("§8• §7Entreprise : §e" + business.getName());
            p.sendMessage("");
            p.sendMessage("§8• §7Exemple : §eSteven2621");
            p.sendMessage("§8• §7Le joueur doit être membre de l'entreprise.");
        }

        p.sendMessage("");
        p.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(p);

        p.playSound(
                p.getLocation(),
                Sound.UI_BUTTON_CLICK,
                0.8f,
                1.2f
        );
    }

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent e
    ) {

        Player p =
                e.getPlayer();

        Draft draft =
                DRAFTS.get(
                        p.getUniqueId()
                );

        if (draft == null) {
            return;
        }

        e.setCancelled(true);

        String message =
                e.getMessage();

        Bukkit.getScheduler().runTask(
                Main.getInstance(),
                () -> handle(
                        p,
                        draft,
                        message
                )
        );
    }

    private void handle(
            Player p,
            Draft draft,
            String message
    ) {

        if (message.equalsIgnoreCase("annuler")
                || message.equalsIgnoreCase("cancel")) {

            DRAFTS.remove(
                    p.getUniqueId()
            );

            BusinessMessages.info(
                    p,
                    "Banque Entreprise",
                    "Opération bancaire annulée."
            );

            p.playSound(
                    p.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_BASS,
                    0.8f,
                    0.8f
            );

            return;
        }

        Business business =
                BusinessManager.getById(
                        draft.businessId
                );

        if (business == null) {

            DRAFTS.remove(
                    p.getUniqueId()
            );

            BusinessMessages.deny(
                    p,
                    "Banque Entreprise",
                    "Entreprise introuvable."
            );

            return;
        }

        switch (draft.mode) {

            case DEPOSIT ->
                    handleDeposit(
                            p,
                            business,
                            message
                    );

            case WITHDRAW ->
                    handleWithdraw(
                            p,
                            business,
                            message
                    );

            case PRIME_TARGET ->
                    handlePrimeTarget(
                            p,
                            business,
                            draft,
                            message
                    );

            case PRIME_AMOUNT ->
                    handlePrimeAmount(
                            p,
                            business,
                            draft,
                            message
                    );

            case PRIME_CONFIRM ->
                    handlePrimeConfirm(
                            p,
                            business,
                            draft,
                            message
                    );
        }
    }

    private void handleDeposit(
            Player p,
            Business business,
            String message
    ) {

        double amount =
                parseAmount(message);

        if (amount <= 0) {

            BusinessMessages.deny(
                    p,
                    "Banque Entreprise",
                    "Montant invalide. Exemple : §e5000"
            );

            return;
        }

        BusinessBankManager.BankResult result =
                BusinessBankManager.deposit(
                        p,
                        business,
                        amount
                );

        DRAFTS.remove(
                p.getUniqueId()
        );

        if (!result.success()) {

            BusinessMessages.deny(
                    p,
                    "Banque Entreprise",
                    result.message()
            );

            return;
        }

        BusinessMessages.success(
                p,
                "Banque Entreprise",
                result.message()
        );

        p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );

        BusinessBankGUI.open(
                p,
                business
        );
    }

    private void handleWithdraw(
            Player p,
            Business business,
            String message
    ) {

        double amount =
                parseAmount(message);

        if (amount <= 0) {

            BusinessMessages.deny(
                    p,
                    "Banque Entreprise",
                    "Montant invalide. Exemple : §e5000"
            );

            return;
        }

        BusinessBankManager.BankResult result =
                BusinessBankManager.withdraw(
                        p,
                        business,
                        amount
                );

        DRAFTS.remove(
                p.getUniqueId()
        );

        if (!result.success()) {

            BusinessMessages.deny(
                    p,
                    "Banque Entreprise",
                    result.message()
            );

            return;
        }

        BusinessMessages.success(
                p,
                "Banque Entreprise",
                result.message()
        );

        p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );

        BusinessBankGUI.open(
                p,
                business
        );
    }

    private void handlePrimeTarget(
            Player p,
            Business business,
            Draft draft,
            String message
    ) {

        UUID targetUuid =
                BusinessManager.getMemberUuidByName(
                        business,
                        message.trim()
                );

        if (targetUuid == null) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    "Ce joueur n'est pas membre de votre entreprise."
            );

            return;
        }

        draft.targetUuid =
                targetUuid;

        draft.mode =
                Mode.PRIME_AMOUNT;

        BusinessMessages.header(
                p,
                "Paie Entreprise"
        );

        p.sendMessage("§e➜ §fÉcris le montant de la prime.");
        p.sendMessage("§8• §7Joueur : §e" + business.getMemberName(targetUuid));
        p.sendMessage("§8• §7Entreprise : §e" + business.getName());
        p.sendMessage("");
        p.sendMessage("§8• §7Exemple : §e2500");
        p.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

        BusinessMessages.footer(p);
    }

    private void handlePrimeAmount(
            Player p,
            Business business,
            Draft draft,
            String message
    ) {

        double amount =
                parseAmount(message);

        if (amount <= 0) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    "Montant invalide. Exemple : §e2500"
            );

            return;
        }

        draft.amount =
                amount;

        double maxWithoutConfirmation =
                Main.getInstance()
                        .getConfig()
                        .getDouble(
                                "payroll.max-bonus-without-confirmation",
                                10000
                        );

        if (amount > maxWithoutConfirmation) {

            draft.mode =
                    Mode.PRIME_CONFIRM;

            BusinessMessages.header(
                    p,
                    "Paie Entreprise"
            );

            p.sendMessage("§e➜ §fPrime élevée détectée.");
            p.sendMessage("§8• §7Joueur : §e" + business.getMemberName(draft.targetUuid));
            p.sendMessage("§8• §7Montant : §e" + VaultHook.format(amount));
            p.sendMessage("");
            p.sendMessage("§8• §7Écris §aconfirmer §7pour valider.");
            p.sendMessage("§8• §7Tape §cannuler §7pour quitter.");

            BusinessMessages.footer(p);

            return;
        }

        processPrime(
                p,
                business,
                draft,
                false
        );
    }

    private void handlePrimeConfirm(
            Player p,
            Business business,
            Draft draft,
            String message
    ) {

        if (!message.equalsIgnoreCase("confirmer")
                && !message.equalsIgnoreCase("oui")
                && !message.equalsIgnoreCase("yes")) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    "Confirmation invalide. Écris §aconfirmer §7ou §cannuler§7."
            );

            return;
        }

        processPrime(
                p,
                business,
                draft,
                true
        );
    }

    private void processPrime(
            Player p,
            Business business,
            Draft draft,
            boolean confirmed
    ) {

        BusinessBankManager.BankResult result =
                BusinessBankManager.bonus(
                        p,
                        business,
                        draft.targetUuid,
                        draft.amount,
                        confirmed
                );

        DRAFTS.remove(
                p.getUniqueId()
        );

        if (!result.success()) {

            BusinessMessages.deny(
                    p,
                    "Paie Entreprise",
                    result.message()
            );

            return;
        }

        BusinessMessages.success(
                p,
                "Paie Entreprise",
                result.message()
        );

        p.playSound(
                p.getLocation(),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                0.8f,
                1.1f
        );

        BusinessBankGUI.open(
                p,
                business
        );
    }

    private double parseAmount(
            String text
    ) {

        try {

            return Double.parseDouble(
                    text.replace(",", ".")
            );

        } catch (Exception e) {

            return -1;
        }
    }

    private enum Mode {

        DEPOSIT,
        WITHDRAW,
        PRIME_TARGET,
        PRIME_AMOUNT,
        PRIME_CONFIRM
    }

    private static class Draft {

        private final String businessId;
        private Mode mode;

        private UUID targetUuid;
        private double amount;

        private Draft(
                String businessId,
                Mode mode
        ) {

            this.businessId =
                    businessId;

            this.mode =
                    mode;
        }
    }
}