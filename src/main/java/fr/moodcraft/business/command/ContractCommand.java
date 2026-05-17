package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.ContractMainGUI;
import fr.moodcraft.business.manager.ContractManager;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.model.ContractStatus;
import fr.moodcraft.business.util.BusinessMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ContractCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Commande joueur uniquement.");
            return true;
        }

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("valider")) {
            Contract contract = resolvePending(p, args.length >= 1 ? args[0] : null, true);
            if (contract == null) return true;
            return validateDirect(p, contract);
        }

        if (cmd.equals("refuser") || cmd.equals("litige")) {
            Decision decision = resolveDecision(p, args, cmd);
            if (decision == null || decision.contract() == null) return true;
            return litigeDirect(p, decision.contract(), decision.reason());
        }

        if (args.length == 0) {
            ContractMainGUI.open(p);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("info") && args.length >= 2) {
            Contract contract = ContractManager.get(args[1]);
            if (contract == null) {
                BusinessMessages.deny(p, "Mission", "Mission introuvable.");
                return true;
            }
            showInfo(p, contract);
            return true;
        }

        if (sub.equals("terminer") && args.length >= 2) {
            Contract contract = ContractManager.get(args[1]);
            String comment = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Travail terminé.";
            ContractManager.ContractResult result = ContractManager.complete(p, contract, comment);
            if (!result.success()) {
                BusinessMessages.deny(p, "Mission", result.message());
                return true;
            }
            BusinessMessages.success(p, "Mission", result.message());
            return true;
        }

        if (sub.equals("valider")) {
            Contract contract = resolvePending(p, args.length >= 2 ? args[1] : null, true);
            if (contract == null) return true;
            return validateDirect(p, contract);
        }

        if (sub.equals("litige") || sub.equals("refuser")) {
            String[] decisionArgs = args.length >= 2 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
            Decision decision = resolveDecision(p, decisionArgs, sub);
            if (decision == null || decision.contract() == null) return true;
            return litigeDirect(p, decision.contract(), decision.reason());
        }

        ContractMainGUI.open(p);
        return true;
    }

    private Contract resolvePending(Player player, String token, boolean showHelp) {
        List<Contract> pending = pendingFor(player);

        if (token != null && !token.isBlank()) {
            if (isInteger(token)) {
                int index = Integer.parseInt(token) - 1;
                if (index < 0 || index >= pending.size()) {
                    BusinessMessages.deny(player, "Mission", "Numéro invalide. Utilise §e/valider §7pour voir la liste.");
                    return null;
                }
                return pending.get(index);
            }
            Contract direct = ContractManager.get(token);
            if (direct == null) {
                BusinessMessages.deny(player, "Mission", "Mission introuvable.");
                return null;
            }
            return direct;
        }

        if (pending.isEmpty()) {
            BusinessMessages.deny(player, "Mission", "Aucune mission terminée à valider.");
            return null;
        }

        if (pending.size() == 1) return pending.get(0);

        if (showHelp) showPendingList(player, pending);
        return null;
    }

    private Decision resolveDecision(Player player, String[] args, String commandName) {
        List<Contract> pending = pendingFor(player);
        Contract contract;
        String reason;

        if (args.length >= 1 && isInteger(args[0])) {
            int index = Integer.parseInt(args[0]) - 1;
            if (index < 0 || index >= pending.size()) {
                BusinessMessages.deny(player, "Mission", "Numéro invalide. Utilise §e/" + commandName + " §7pour voir la liste.");
                return null;
            }
            contract = pending.get(index);
            reason = args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Problème signalé sans précision.";
            return new Decision(contract, reason);
        }

        if (args.length >= 1 && args[0].startsWith("C-")) {
            contract = ContractManager.get(args[0]);
            if (contract == null) {
                BusinessMessages.deny(player, "Mission", "Mission introuvable.");
                return null;
            }
            reason = args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Problème signalé sans précision.";
            return new Decision(contract, reason);
        }

        if (pending.isEmpty()) {
            BusinessMessages.deny(player, "Mission", "Aucune mission terminée à refuser ou signaler.");
            return null;
        }

        if (pending.size() > 1) {
            showPendingList(player, pending);
            return null;
        }

        contract = pending.get(0);
        reason = args.length >= 1 ? String.join(" ", args) : "Problème signalé sans précision.";
        return new Decision(contract, reason);
    }

    private List<Contract> pendingFor(Player player) {
        return ContractManager.getByClient(player).stream()
                .filter(contract -> contract.getStatus() == ContractStatus.TERMINE)
                .toList();
    }

    private void showPendingList(Player player, List<Contract> pending) {
        BusinessMessages.header(player, "Missions à valider");
        player.sendMessage("§fVous avez plusieurs missions terminées.");
        player.sendMessage("");
        for (int i = 0; i < pending.size(); i++) {
            Contract contract = pending.get(i);
            player.sendMessage("§e" + (i + 1) + ". §f" + shortText(contract.getTitle(), 24)
                    + " §8• §7Entreprise : §b" + shortText(contract.getBusinessName(), 14)
                    + " §8• §7Prix : §e" + BusinessMessages.money(contract.getGrossAmount()));
        }
        player.sendMessage("");
        player.sendMessage("§a/valider <numéro> §7pour payer l'entreprise");
        player.sendMessage("§c/refuser <numéro> <raison> §7pour ouvrir un litige");
        player.sendMessage("§c/litige <numéro> <raison> §7pour signaler un problème");
        BusinessMessages.footer(player);
    }

    private void showInfo(Player p, Contract contract) {
        BusinessMessages.header(p, "Mission");
        p.sendMessage("§7Titre: §e" + contract.getTitle());
        p.sendMessage("§7Client: §e" + contract.getClientName());
        p.sendMessage("§7Entreprise: §b" + contract.getBusinessName());
        p.sendMessage("§7État: " + contract.getStatus().getDisplayName());
        p.sendMessage("");
        p.sendMessage("§7Prix: §e" + BusinessMessages.money(contract.getGrossAmount()));
        p.sendMessage("§7Taxe: §c" + BusinessMessages.money(contract.getTaxAmount()));
        p.sendMessage("§7Net entreprise: §a" + BusinessMessages.money(contract.getNetAmount()));
        p.sendMessage("§7Argent sécurisé: §e" + BusinessMessages.money(contract.getEscrowAmount()));
        BusinessMessages.footer(p);
    }

    private boolean validateDirect(Player p, Contract contract) {
        ContractManager.ContractResult result = ContractManager.validate(p, contract);
        if (!result.success()) {
            BusinessMessages.deny(p, "Validation mission", result.message());
            return true;
        }
        BusinessMessages.success(p, "Validation mission", result.message());
        return true;
    }

    private boolean litigeDirect(Player p, Contract contract, String reason) {
        ContractManager.ContractResult result = ContractManager.openLitige(p, contract, reason);
        if (!result.success()) {
            BusinessMessages.deny(p, "Litige mission", result.message());
            return true;
        }
        BusinessMessages.success(p, "Litige mission", result.message());
        return true;
    }

    private boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private String shortText(String text, int max) {
        if (text == null || text.isBlank()) return "Inconnu";
        String clean = text.replaceAll("§.", "").trim();
        if (clean.length() <= max) return clean;
        return clean.substring(0, Math.max(1, max - 3)) + "...";
    }

    private record Decision(Contract contract, String reason) {}
}
