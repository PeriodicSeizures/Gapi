package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.GithubUpdater;
import com.crazicrafter1.gapi.test.CmdTestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public final String prefix = ChatColor.translateAlternateColorCodes('&',
            "&c[&6&lGapi&r&c] ");

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public static boolean debug;
    public static boolean update;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("CRUtils") == null) {
            error(ChatColor.RED + "Required plugin CRUtils not found");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        GithubUpdater.autoUpdate(this, "PeriodicSeizures", "Gapi", "Gapi.jar");

        instance = this;

        this.saveDefaultConfig();

        debug = getConfig().getBoolean("debug");
        update = getConfig().getBoolean("update");

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

    public void debug(String s) {
        if (debug)
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GOLD + s);
    }

}
