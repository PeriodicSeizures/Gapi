package com.crazicrafter1.gapi.test;

import com.crazicrafter1.gapi.Result;
import com.crazicrafter1.gapi.SimpleMenu;
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

    void expectedImprovedUsage() {

        // The best approach will be to make this MenuSystem compatible across different Java software, not just Minecraft
        // Usage:
        //  - Gui interface
        //      - methods for onClick

        //new SimpleMenu(col) {
        //    @Override
        //    void onOpen() {

        //    }
        //    onOpen(p ->) // on open is for
        //            .

        //    onClose(p ->Result.PARENT())
        //}
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
