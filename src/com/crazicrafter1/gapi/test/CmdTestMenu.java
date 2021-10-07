package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import com.crazicrafter1.gapi.Button;
import com.crazicrafter1.gapi.Main;
import com.crazicrafter1.gapi.ParallaxMenu;
import com.crazicrafter1.gapi.SimpleMenu;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

            /*case "test": {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Main.getInstance().info("Runnable was called!");
                    }
                }.runTaskLater(Main.getInstance(), 0);

                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }

                Main.getInstance().info("Outside runnable was called!");
                break;
            }*/

            case "simple": {
                new SimpleMenu.SBuilder(3)
                        .title("Simple Menu")
                        .background()
                        .button(4, 1, new Button.Builder()
                                        .icon(new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem()))
                        .open(p);

                break;
            } case "hierarchy": {
                new SimpleMenu.SBuilder(3)
                        .title("Test Builder Menu")
                        .childButton(4, 1, new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                new SimpleMenu.SBuilder(3)
                                        .title("Child menu")
                                        .background()
                                        .parentButton(4, 1))
                        .open(p);
                break;
            } case "locked": {
                new SimpleMenu.SBuilder(1)
                        .title("Test Lockable Menu")
                        .preventClose()
                        .button(4, 0,
                                new Button.Builder()
                                        .icon(new ItemBuilder(Material.IRON_DOOR).name("Unlock menu").toItem())
                                        .lmb(interact -> Button.Result.close()))
                        .open(p);
                break;
            } case "parallax": {
                ParallaxMenu.PBuilder builder = new ParallaxMenu.PBuilder()
                        .title("Test Parallax Menu");

                Material values[] = Material.values();
                for (int i = 0; i < 59; i++) {
                    builder.add(new Button.Builder()
                            .icon(new ItemBuilder(values[Util.randomRange(0, values.length - 1)]).toItem()));
                }

                builder.open(p);
                break;
            }
            default:
                return false;
        }

        return true;
    }
}
