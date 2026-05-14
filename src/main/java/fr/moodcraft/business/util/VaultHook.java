package fr.moodcraft.business.util;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import org.bukkit.entity.Player;

import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private static Economy economy;

    private VaultHook() {}

    public static void setup() {

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {

            Bukkit.getConsoleSender().sendMessage(
                    "§c[MoodBusiness] Vault introuvable."
            );

            return;
        }

        RegisteredServiceProvider<Economy> provider =
                Bukkit.getServicesManager().getRegistration(
                        Economy.class
                );

        if (provider == null) {

            Bukkit.getConsoleSender().sendMessage(
                    "§c[MoodBusiness] Aucun fournisseur économie Vault."
            );

            return;
        }

        economy =
                provider.getProvider();

        Bukkit.getConsoleSender().sendMessage(
                "§a[MoodBusiness] Vault connecté."
        );
    }

    public static boolean isReady() {

        return economy != null;
    }

    public static boolean has(
            Player player,
            double amount
    ) {

        return economy != null
                && economy.has(
                player,
                amount
        );
    }

    public static boolean has(
            OfflinePlayer player,
            double amount
    ) {

        return economy != null
                && player != null
                && economy.has(
                player,
                amount
        );
    }

    public static boolean withdraw(
            Player player,
            double amount
    ) {

        if (economy == null) {
            return false;
        }

        EconomyResponse response =
                economy.withdrawPlayer(
                        player,
                        amount
                );

        return response.transactionSuccess();
    }

    public static boolean withdraw(
            OfflinePlayer player,
            double amount
    ) {

        if (economy == null || player == null) {
            return false;
        }

        EconomyResponse response =
                economy.withdrawPlayer(
                        player,
                        amount
                );

        return response.transactionSuccess();
    }

    public static boolean deposit(
            Player player,
            double amount
    ) {

        if (economy == null) {
            return false;
        }

        EconomyResponse response =
                economy.depositPlayer(
                        player,
                        amount
                );

        return response.transactionSuccess();
    }

    public static boolean deposit(
            OfflinePlayer player,
            double amount
    ) {

        if (economy == null || player == null) {
            return false;
        }

        EconomyResponse response =
                economy.depositPlayer(
                        player,
                        amount
                );

        return response.transactionSuccess();
    }

    public static String format(
            double amount
    ) {

        if (economy != null) {

            return economy.format(amount);
        }

        return String.format(
                "%,.0f€",
                amount
        ).replace(",", " ");
    }
}