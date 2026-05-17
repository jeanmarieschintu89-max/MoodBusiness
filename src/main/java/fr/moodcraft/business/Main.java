package fr.moodcraft.business;

import fr.moodcraft.business.command.BusinessAdminCommand;
import fr.moodcraft.business.command.BusinessCommand;
import fr.moodcraft.business.command.BusinessStaffCommand;
import fr.moodcraft.business.command.ContractCommand;
import fr.moodcraft.business.command.RequestAdminCommand;
import fr.moodcraft.business.command.RequestsCommand;

import fr.moodcraft.business.listener.AlertJoinListener;
import fr.moodcraft.business.listener.BankChatListener;
import fr.moodcraft.business.listener.BankGUIListener;
import fr.moodcraft.business.listener.BusinessAdminGUIListener;
import fr.moodcraft.business.listener.BusinessCreationChatListener;
import fr.moodcraft.business.listener.BusinessDissolveGUIListener;
import fr.moodcraft.business.listener.BusinessEmployeeManageListener;
import fr.moodcraft.business.listener.BusinessGUIListener;
import fr.moodcraft.business.listener.BusinessInventoryGuardListener;
import fr.moodcraft.business.listener.ContractChatListener;
import fr.moodcraft.business.listener.ContractGUIListener;
import fr.moodcraft.business.listener.RequestChatListener;
import fr.moodcraft.business.listener.RequestGUIListener;

import fr.moodcraft.business.manager.AuditLogManager;
import fr.moodcraft.business.manager.BusinessManager;

import fr.moodcraft.business.storage.AlertStorage;
import fr.moodcraft.business.storage.AuditLogStorage;
import fr.moodcraft.business.storage.BusinessStorage;
import fr.moodcraft.business.storage.ContractStorage;
import fr.moodcraft.business.storage.FinanceStorage;
import fr.moodcraft.business.storage.RequestStorage;

import fr.moodcraft.business.util.VaultHook;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        VaultHook.setup();

        BusinessStorage.init();
        RequestStorage.init();
        ContractStorage.init();
        FinanceStorage.init();
        AuditLogStorage.init();
        AlertStorage.init();

        BusinessManager.init();
        registerCommands();
        registerListeners();
        AuditLogManager.system("MoodBusiness chargé.");

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§8----- §6✦ §aMood§6Business §6✦ §8-----");
        Bukkit.getConsoleSender().sendMessage("§a✔ §fBureau des Entreprises chargé.");
        Bukkit.getConsoleSender().sendMessage("§e➜ §7Service officiel de §aMood§6Craft§7.");
        Bukkit.getConsoleSender().sendMessage("§8----------- §6✦ §8-----------");
    }

    @Override
    public void onDisable() {
        AuditLogManager.system("MoodBusiness arrêté.");

        BusinessStorage.save();
        RequestStorage.save();
        ContractStorage.save();
        FinanceStorage.save();
        AuditLogStorage.save();
        AlertStorage.save();

        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§8----- §6✦ §aMood§6Business §6✦ §8-----");
        Bukkit.getConsoleSender().sendMessage("§c✖ §fBureau des Entreprises arrêté.");
        Bukkit.getConsoleSender().sendMessage("§8----------- §6✦ §8-----------");
    }

    private void registerCommands() {
        registerCommand("entreprise", new BusinessCommand());
        registerCommand("entrepriseadmin", new BusinessAdminCommand());
        registerCommand("businessstaff", new BusinessStaffCommand());
        registerCommand("demandes", new RequestsCommand());
        registerCommand("demandesadmin", new RequestAdminCommand());
        ContractCommand contractCommand = new ContractCommand();
        registerCommand("contrat", contractCommand);
        registerCommand("valider", contractCommand);
        registerCommand("refuser", contractCommand);
        registerCommand("litige", contractCommand);
    }

    private void registerCommand(String name, org.bukkit.command.CommandExecutor executor) {
        PluginCommand command = getCommand(name);
        if (command != null) {
            command.setExecutor(executor);
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BusinessInventoryGuardListener(), this);
        Bukkit.getPluginManager().registerEvents(new BusinessGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new BusinessCreationChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new BusinessDissolveGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new BusinessAdminGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new BusinessEmployeeManageListener(), this);
        Bukkit.getPluginManager().registerEvents(new RequestGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new RequestChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new ContractGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new ContractChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new BankGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new BankChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new AlertJoinListener(), this);
    }
}
