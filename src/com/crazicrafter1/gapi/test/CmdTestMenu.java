package com.crazicrafter1.gapi.test;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CmdTestMenu implements CommandExecutor {

    public CmdTestMenu(JavaPlugin plugin) {
        plugin.getCommand("testmenu").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p))
            return false;

        if (args.length != 1) {
            return false;
        }

        switch (args[0]) {
            case "simplex" -> new TestSimplexMenu().show(p);
            case "parallax" -> new TestParallaxMenu().show(p);
            default -> {
                return false;
            }
        }
        return true;
    }
}
