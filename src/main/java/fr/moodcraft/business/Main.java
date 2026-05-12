package fr.moodcraft.business;

import fr.moodcraft.business.command.BusinessCommand;
import fr.moodcraft.business.command.ContractCommand;
import fr.moodcraft.business.command.RequestsCommand;

import fr.moodcraft.business.listener.AlertJoinListener;
import fr.moodcraft.business.listener.ApplicationChatListener;
import fr.moodcraft.business.listener.BankGUIListener;
import fr.moodcraft.business.listener.BusinessActionGUIListener;
import fr.moodcraft.business.listener.BusinessCreationChatListener;
import fr.moodcraft.business.listener.BusinessGUIListener;
import fr.moodcraft.business.listener.ContractChatListener;
import fr.moodcraft.business.listener.ContractGUIListener;
import fr.moodcraft.business.listener.RequestChatListener;
import fr.moodcraft.business.listener.RequestGUIListener;

import fr.moodcraft.business.manager.AuditLogManager;
import fr.moodcraft.business.manager.BusinessManager;
import fr.moodcraft.business.manager.PayrollManager;

import fr.moodcraft.business.storage.AlertStorage;
import fr.moodcraft.business.storage.ApplicationStorage;
import fr.moodcraft.business.storage.AuditLogStorage;
import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.ContractStorage;
import fr.moodcraft.business.storage.FinanceStorage;
import fr.moodcraft.business.storage.OfferStorage;
import fr.moodcraft.business.storage.PayrollStorage;
import fr.moodcraft.business.storage.RequestStorage;

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
        ApplicationStorage.init();
        RequestStorage.init();
        OfferStorage.init();
        ContractStorage.init();
        FinanceStorage.init();
        PayrollStorage.init();
        AuditLogStorage.init();
        AlertStorage.init();

        BusinessManager.init();

        registerCommands();
        registerListeners();

        PayrollManager.startTask();

        AuditLogManager.system(
                "MoodBusiness chargé."
        );

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(
                "§8----- §6✦ §aMood§6Business §6✦ §8-----"
        );
        Bukkit.getConsoleSender().sendMessage(
                "§a✔ §fBureau des Entreprises chargé."
        );
        Bukkit.getConsoleSender().sendMessage(
                "§7Service officiel de §aMood§6Craft§7."
        );
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {

        AuditLogManager.system(
                "MoodBusiness arrêté."
        );

        BusinessStorage.save();
        ApplicationStorage.save();
        RequestStorage.save();
        OfferStorage.save();
        ContractStorage.save();
        FinanceStorage.save();
        PayrollStorage.save();
        AuditLogStorage.save();
        AlertStorage.save();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(
                "§8----- §6✦ §aMood§6Business §6✦ §8-----"
        );
        Bukkit.getConsoleSender().sendMessage(
                "§c✘ §fBureau des Entreprises arrêté."
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

        Bukkit.getPluginManager().registerEvents(
                new BusinessActionGUIListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new BusinessCreationChatListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new ApplicationChatListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new RequestGUIListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new RequestChatListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new ContractGUIListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new ContractChatListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new BankGUIListener(),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                new AlertJoinListener(),
                this
        );
    }

    public static Main getInstance() {

        return instance;
    }
}