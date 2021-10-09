package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import com.crazicrafter1.gapi.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

        /*
         * How to reference this parent menu
         * in a sub menu that this creates
         */

        if (args.length == 0)
            return false;

        switch (args[0].toLowerCase()) {
            case "simple": {
                new SimpleMenu.SBuilder(3)
                        .title("Simple Menu")
                        .background()
                        .button(4, 1, new Button.Builder()
                                        .icon(new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem()))
                        .open(p);

                break;
            } case "nested": {
                new SimpleMenu.SBuilder(3)
                        .title("Test Nested Menu")
                        .background()
                        .childButton(4, 1, new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                new SimpleMenu.SBuilder(3)
                                        .title("Child menu 1")
                                        .background()
                                        .childButton(4, 1, new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                                new SimpleMenu.SBuilder(3)
                                                        .title("Child menu 2")
                                                        .background()
                                                        .childButton(4, 1, new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                                                new SimpleMenu.SBuilder(3)
                                                                        .title("Child menu 3")
                                                                        .background()

                                                                        .parentButton(4, 2))
                                                        .parentButton(4, 2))
                                        .parentButton(4, 2))
                        .open(p);
                break;
            } case "locked": {
                new SimpleMenu.SBuilder(1)
                        .title("Test Lockable Menu")
                        .preventClose()
                        .button(4, 0,
                                new Button.Builder()
                                        .icon(new ItemBuilder(Material.IRON_DOOR).name("Unlock menu").toItem())
                                        .lmb(interact -> EnumResult.CLOSE))
                        .open(p);
                break;
            } case "parallax": {
                new ParallaxMenu.PBuilder()
                        .title(ChatColor.DARK_GRAY + "Test Parallax Menu")
                        .action(self -> {
                            Material values[] = Material.values();
                            for (int i = 0; i < 59; i++) {
                                Material material = values[Util.randomRange(0, values.length - 1)];
                                while (!material.isItem()) {
                                    material = values[Util.randomRange(0, values.length - 1)];
                                }
                                self.append(new Button.Builder()
                                        .icon(new ItemBuilder(material).toItem())
                                        .lmb(interact -> {
                                            interact.player.sendMessage(ChatColor.GOLD + "I'm a " +
                                                    interact.clickedItem.getType().name().toLowerCase().replaceAll("_", " "));

                                            // do nothing else on click
                                            return EnumResult.OK;
                                        })
                                );
                            }
                        })
                        .open(p);

                break;
            } case "text": {
                new TextMenu.TBuilder()
                        .title("Text menu")
                        .text("Default text!")
                        .onComplete((player, s) -> {
                            p.sendMessage("You typed " + s);
                            return EnumResult.OK;
                        })
                        .open(p);
                break;
            }
            default:
                return false;
        }

        return true;
    }
}
