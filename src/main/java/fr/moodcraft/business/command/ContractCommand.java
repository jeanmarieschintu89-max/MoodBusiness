package fr.moodcraft.business.command;

import fr.moodcraft.business.gui.ContractMainGUI;
import fr.moodcraft.business.manager.ContractManager;
import fr.moodcraft.business.model.Contract;
import fr.moodcraft.business.util.BusinessMessages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ContractCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Commande joueur uniquement.");
            return true;
        }

        String cmd = command.getName().toLowerCase();

        if (cmd.equals("valider")) {
            if (args.length < 1) {
                BusinessMessages.deny(p, "Validation mission", "Utilisation : §e/valider <idMission>");
                return true;
            }
            return validateDirect(p, args[0]);
        }

        if (cmd.equals("refuser") || cmd.equals("litige")) {
            if (args.length < 1) {
                BusinessMessages.deny(p, "Litige mission", "Utilisation : §e/" + cmd + " <idMission> [raison]");
                return true;
            }
            String reason = args.length >= 2 ? String.join(" ", Arrays.copyOfRange(args, 1, args.length)) : "Problème signalé sans précision.";
            return litigeDirect(p, args[0], reason);
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

        if (sub.equals("valider") && args.length >= 2) {
            return validateDirect(p, args[1]);
        }

        if ((sub.equals("litige") || sub.equals("refuser")) && args.length >= 2) {
            String reason = args.length >= 3 ? String.join(" ", Arrays.copyOfRange(args, 2, args.length)) : "Problème signalé sans précision.";
            return litigeDirect(p, args[1], reason);
        }

        ContractMainGUI.open(p);
        return true;
    }

    private boolean validateDirect(Player p, String id) {
        Contract contract = ContractManager.get(id);
        ContractManager.ContractResult result = ContractManager.validate(p, contract);
        if (!result.success()) {
            BusinessMessages.deny(p, "Validation mission", result.message());
            return true;
        }
        BusinessMessages.success(p, "Validation mission", result.message());
        return true;
    }

    private boolean litigeDirect(Player p, String id, String reason) {
        Contract contract = ContractManager.get(id);
        ContractManager.ContractResult result = ContractManager.openLitige(p, contract, reason);
        if (!result.success()) {
            BusinessMessages.deny(p, "Litige mission", result.message());
            return true;
        }
        BusinessMessages.success(p, "Litige mission", result.message());
        return true;
    }
}
