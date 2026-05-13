package fr.moodcraft.business.listener;

import fr.moodcraft.business.gui.ContractAssignGUI;
import fr.moodcraft.business.gui.ContractDetailGUI;
import fr.moodcraft.business.gui.ContractMainGUI;
import fr.moodcraft.business.gui.MyMissionsGUI;

import fr.moodcraft.business.manager.ContractAssignmentManager;
import fr.moodcraft.business.manager.ContractManager;

import fr.moodcraft.business.model.Contract;

import fr.moodcraft.business.util.BusinessMessages;
import fr.moodcraft.business.util.ItemBuilder;

import org.bukkit.Sound;

import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ContractMissionGUIListener
        implements Listener {

    @EventHandler
    public void onClick(
            InventoryClickEvent e
    ) {

        String title =
                e.getView().getTitle();

        if (!title.equals(ContractMainGUI.TITLE)
                && !title.equals(ContractDetailGUI.TITLE)
                && !title.equals(ContractAssignGUI.TITLE)
                && !title.equals(MyMissionsGUI.TITLE)) {
            return;
        }

        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }

        ItemStack item =
                e.getCurrentItem();

        String action =
                ItemBuilder.getAction(item);

        String target =
                ItemBuilder.getTarget(item);

        if (action == null) {
            return;
        }

        if (!isAction(action)) {
            return;
        }

        e.setCancelled(true);

        switch (action) {

            case "contract_assign_open" -> {

                Contract contract =
                        ContractManager.get(target);

                if (contract == null) {

                    BusinessMessages.deny(
                            p,
                            "Contrats",
                            "Contrat introuvable."
                    );

                    return;
                }

                ContractAssignGUI.open(
                        p,
                        contract
                );
            }

            case "contract_assign_member" -> {

                if (target == null || !target.contains(":")) {
                    return;
                }

                String[] split =
                        target.split(":");

                if (split.length < 2) {
                    return;
                }

                Contract contract =
                        ContractManager.get(split[0]);

                if (contract == null) {

                    BusinessMessages.deny(
                            p,
                            "Contrats",
                            "Contrat introuvable."
                    );

                    return;
                }

                UUID memberUuid;

                try {

                    memberUuid =
                            UUID.fromString(split[1]);

                } catch (Exception ex) {

                    return;
                }

                ContractAssignmentManager.AssignResult result =
                        ContractAssignmentManager.assign(
                                p,
                                contract,
                                memberUuid
                        );

                if (!result.success()) {

                    BusinessMessages.deny(
                            p,
                            "Mission",
                            result.message()
                    );

                    p.playSound(
                            p.getLocation(),
                            Sound.ENTITY_VILLAGER_NO,
                            1f,
                            0.85f
                    );

                    return;
                }

                BusinessMessages.success(
                        p,
                        "Mission",
                        result.message()
                );

                p.playSound(
                        p.getLocation(),
                        Sound.UI_TOAST_CHALLENGE_COMPLETE,
                        0.8f,
                        1.1f
                );

                ContractAssignGUI.open(
                        p,
                        contract
                );
            }

            case "contract_my_missions" -> {

                MyMissionsGUI.open(p);
            }

            case "contract_detail" -> {

                Contract contract =
                        ContractManager.get(target);

                if (contract != null) {

                    ContractDetailGUI.open(
                            p,
                            contract
                    );
                }
            }

            case "open_contracts" -> {

                ContractMainGUI.open(p);
            }

            default -> {}
        }
    }

    private boolean isAction(
            String action
    ) {

        return action.equals("contract_assign_open")
                || action.equals("contract_assign_member")
                || action.equals("contract_my_missions")
                || action.equals("contract_detail")
                || action.equals("open_contracts");
    }
}