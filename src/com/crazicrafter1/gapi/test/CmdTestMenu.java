package com.crazicrafter1.gapi.test;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Locale;

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
            p.sendMessage(ChatColor.RED + "" + Arrays.toString(EnumTest.values()));
            return true;
        }

        try {
            EnumTest enumTest = EnumTest.valueOf(args[0].toLowerCase(Locale.ROOT));
            enumTest.getMenuBuilder().open(p);
        } catch (Exception e) {
            p.sendMessage(ChatColor.RED + "" + Arrays.toString(EnumTest.values()));
        }

        return true;
    }
}
