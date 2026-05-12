package fr.moodcraft.business;

import fr.moodcraft.business.command.BusinessCommand;
import fr.moodcraft.business.command.ContractCommand;
import fr.moodcraft.business.command.RequestsCommand;

import fr.moodcraft.business.listener.BusinessGUIListener;

import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.storage.BusinessStorage;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;

import org.bukkit.command.PluginCommand;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        VaultHook.setup();

        BusinessStorage.init();
        BusinessManager.init();

        registerCommands();
        registerListeners();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(
                "§8----- §6✦ §aMood§6Business §6✦ §8-----"
        );
        Bukkit.getConsoleSender().sendMessage(
                "§a✔ §fRegistre économique chargé."
        );
        Bukkit.getConsoleSender().sendMessage(
                "§7Service officiel de §aMood§6Craft§7."
        );
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {

        BusinessStorage.save();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(
                "§8----- §6✦ §aMood§6Business §6✦ §8-----"
        );
        Bukkit.getConsoleSender().sendMessage(
                "§c✘ §fRegistre économique arrêté."
        );
        Bukkit.getConsoleSender().sendMessage("");
    }

    private void registerCommands() {

        PluginCommand entreprise =
                getCommand("entreprise");

        if (entreprise != null) {

            entreprise.setExecutor(
                    new BusinessCommand()
            );
        }

        PluginCommand demandes =
                getCommand("demandes");

        if (demandes != null) {

            demandes.setExecutor(
                    new RequestsCommand()
            );
        }

        PluginCommand contrat =
                getCommand("contrat");

        if (contrat != null) {

            contrat.setExecutor(
                    new ContractCommand()
            );
        }
    }

    private void registerListeners() {

        Bukkit.getPluginManager().registerEvents(
                new BusinessGUIListener(),
                this
        );
    }

    public static Main getInstance() {

        return instance;
    }
}