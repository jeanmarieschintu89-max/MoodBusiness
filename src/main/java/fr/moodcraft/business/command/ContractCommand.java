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
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player p)) {

            sender.sendMessage(
                    "Commande joueur uniquement."
            );

            return true;
        }

        if (args.length == 0) {

            ContractMainGUI.open(p);

            return true;
        }

        String sub =
                args[0].toLowerCase();

        if (sub.equals("info")
                && args.length >= 2) {

            Contract contract =
                    ContractManager.get(args[1]);

            if (contract == null) {

                BusinessMessages.deny(
                        p,
                        "Contrat",
                        "Contrat introuvable."
                );

                return true;
            }

            BusinessMessages.header(
                    p,
                    "Contrat"
            );

            p.sendMessage("§7Titre: §e" + contract.getTitle());
            p.sendMessage("§7Client: §e" + contract.getClientName());
            p.sendMessage("§7Entreprise: §b" + contract.getBusinessName());
            p.sendMessage("§7État: " + contract.getStatus().getDisplayName());
            p.sendMessage("");
            p.sendMessage("§7Brut: §e" + BusinessMessages.money(contract.getGrossAmount()));
            p.sendMessage("§7Taxe: §c" + BusinessMessages.money(contract.getTaxAmount()));
            p.sendMessage("§7Net entreprise: §a" + BusinessMessages.money(contract.getNetAmount()));
            p.sendMessage("§7Argent bloqué: §e" + BusinessMessages.money(contract.getEscrowAmount()));

            BusinessMessages.footer(p);

            return true;
        }

        if (sub.equals("terminer")
                && args.length >= 2) {

            Contract contract =
                    ContractManager.get(args[1]);

            String comment =
                    args.length >= 3
                            ? String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    2,
                                    args.length
                            )
                    )
                            : "Travail terminé.";

            ContractManager.ContractResult result =
                    ContractManager.complete(
                            p,
                            contract,
                            comment
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Contrat",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Contrat",
                    result.message()
            );

            return true;
        }

        if (sub.equals("valider")
                && args.length >= 2) {

            Contract contract =
                    ContractManager.get(args[1]);

            ContractManager.ContractResult result =
                    ContractManager.validate(
                            p,
                            contract
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Contrat",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Contrat",
                    result.message()
            );

            return true;
        }

        if (sub.equals("litige")
                && args.length >= 2) {

            Contract contract =
                    ContractManager.get(args[1]);

            String reason =
                    args.length >= 3
                            ? String.join(
                            " ",
                            Arrays.copyOfRange(
                                    args,
                                    2,
                                    args.length
                            )
                    )
                            : "Litige ouvert sans précision.";

            ContractManager.ContractResult result =
                    ContractManager.openLitige(
                            p,
                            contract,
                            reason
                    );

            if (!result.success()) {

                BusinessMessages.deny(
                        p,
                        "Litige",
                        result.message()
                );

                return true;
            }

            BusinessMessages.success(
                    p,
                    "Litige",
                    result.message()
            );

            return true;
        }

        ContractMainGUI.open(p);

        return true;
    }
}
