package com.github.idimabr.raphasignrepair;

import com.github.idimabr.raphasignrepair.listeners.SignListener;
import com.github.idimabr.raphasignrepair.utils.ConfigUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class RaphaSignRepair extends JavaPlugin {

    private static RaphaSignRepair instance;
    private ConfigUtil config;
    private static Economy econ = null;

    public static RaphaSignRepair getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        if (!setupEconomy()) {
            getLogger().info("O Vault não foi encontrado, desabilitando o plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        config = new ConfigUtil(null, "config.yml", false);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);
        config.saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public ConfigUtil getConfig() {
        return config;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public Economy getVault() {
        return econ;
    }
}
