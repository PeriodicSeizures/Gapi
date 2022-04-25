package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.GitUtils;
import com.crazicrafter1.gapi.test.CmdTestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {

    public final String prefix = ChatColor.translateAlternateColorCodes('&',
            "&c[&6&lGapi&r&c] ");

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("CRUtils") == null) {
            getLogger().severe("CRUtils is required");
            getLogger().severe("Install it from here " + ChatColor.UNDERLINE + "https://github.com/PeriodicSeizures/CRUtils/releases");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();
        FileConfiguration config = getConfig();
        boolean update = config.getBoolean("update", false);

        if (update) try {
                StringBuilder outTag = new StringBuilder();
                if (GitUtils.updatePlugin(this, "PeriodicSeizures", "Gapi", "Gapi.jar", outTag)) {
                    getLogger().warning("Updated to " + outTag);
                } else {
                    getLogger().info("Using the latest version");
                }
            } catch (IOException e) {
                getLogger().warning("Error while updating");
                e.printStackTrace();
            }
        else {
            getLogger().warning("Updating is disabled");
            GitUtils.checkForUpdateAsync(this, "PeriodicSeizures", "Gapi", (result, tag) -> { if (result) getLogger().warning("Update " + tag + " is available"); else getLogger().info("Using latest version"); });
        }

        Main.instance = this;

        new EventListener(this);
        new CmdTestMenu(this);
    }

    @Override
    public void onDisable() {
        for (AbstractMenu abstractMenu : AbstractMenu.openMenus.values()) {
            abstractMenu.closeInventory();
        }
    }

    public void info(String s) {
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.DARK_GRAY + s);
    }

    public void important(String s) {
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.DARK_PURPLE + s);
    }

    public void warn(String s) {
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.RED + s);
    }

    public void error(String s) {
        Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.DARK_RED + s);
    }
}
