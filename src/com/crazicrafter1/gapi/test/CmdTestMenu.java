package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.Button;
import com.crazicrafter1.gapi.Menu;
import com.crazicrafter1.gapi.ParallaxMenu;
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
                new Menu.Builder()
                        .title("Test Builder Menu")
                        .columns(3)
                        .openButton(4, 1, new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                new Menu.Builder()
                                        .title("Child menu")
                                        .columns(3)
                                        .background()
                                        .backButton(4, 1))
                        .open(p);
                break;
            } case "locked": {
                new Menu.Builder()
                        .title("Test Lockable Menu")
                        .preventClose()
                        .columns(1)
                        .button(4, 0,
                                new Button.Builder()
                                        .icon(new ItemBuilder(Material.IRON_DOOR).name("Unlock menu").toItem())
                                        .lmb(interact -> Button.Result.close()))
                        .open(p);

                //new ParallaxMenu.PBuilder()

                break;
            }
            default:
                return false;
        }

        return true;
    }
}
