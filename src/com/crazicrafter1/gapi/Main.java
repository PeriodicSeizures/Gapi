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

    @Override
    public void onEnable() {
        instance = this;

        if (ReflectionUtil.isOldVersion()) {
            error("only MC 1.17+ is supported (Java 16)");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        ConfigurationSerialization.registerClass(Data.class);

        this.saveDefaultConfig();

        new Updater(this, "PeriodicSeizures", "Crutils", Data.update);

        new EventListener(this);
        new CmdTestMenu(this);
    }

    @Override
    public void reloadConfig() {
        try {
            super.reloadConfig();
        } catch (Exception e) {
            error("Couldn't load config");
            e.printStackTrace();
        }
    }

    @Override
    public void saveConfig() {
        this.getConfig().set("data", new Data());
        super.saveConfig();
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
        if (Data.debug)
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.GOLD + s);
    }

}
