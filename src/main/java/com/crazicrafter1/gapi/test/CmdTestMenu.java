package com.crazicrafter1.gapi.test;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class CmdTestMenu implements CommandExecutor {

    public CmdTestMenu(JavaPlugin plugin) {
        plugin.getCommand("testmenu").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player p = (Player) sender;

        /*
         * How to reference this parent menu
         * in a sub menu that this creates
         */

        if (args.length == 0) {
            p.sendMessage(ChatColor.RED + "" + Arrays.toString(com.crazicrafter1.gapi.test.EnumTest.values()));
            return true;
        }

        try {
            com.crazicrafter1.gapi.test.EnumTest enumTest = com.crazicrafter1.gapi.test.EnumTest.valueOf(args[0].toUpperCase());
            enumTest.getMenuBuilder().open(p);
        } catch (IllegalArgumentException e) {
            p.sendMessage(ChatColor.RED + "" + Arrays.toString(com.crazicrafter1.gapi.test.EnumTest.values()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
