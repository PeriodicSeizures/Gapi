package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ReflectionUtil;
import com.crazicrafter1.gapi.test.CmdTestMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private String prefix = ChatColor.GOLD + this.getName() + " : ";
    private FileConfiguration config = null;

    public static boolean debug;

    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        String v = Bukkit.getVersion();

        if (ReflectionUtil.isOldVersion()) {
            error("Works only on 1.17+");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.saveDefaultConfig();
        this.config = this.getConfig();

        debug = config.getBoolean("debug");

        new EventListener(this);

        this.info("Loaded successfully.");

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
