package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ReflectionUtil;
import com.crazicrafter1.crutils.Updater;
import com.crazicrafter1.gapi.test.CmdTestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public final String prefix = ChatColor.translateAlternateColorCodes('&',
            "&c[&6&lGapi&r&c] ");

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    public boolean debug;
    public boolean update;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.debug = getConfig().getBoolean("debug");
        this.update = getConfig().getBoolean("update");

        new Updater(this, "PeriodicSeizures", "Gapi", update);

        new EventListener(this);
        new CmdTestMenu(this);
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
