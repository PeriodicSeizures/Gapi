package com.crazicrafter1.gapi.temp_ignore;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.Button;
import com.crazicrafter1.gapi.Main;
import com.crazicrafter1.gapi.SimpleMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.function.Consumer;

public class EditMenu extends SimpleMenu {

    // have a menu which allows the user to insert an item and perform changes on

    //private ItemStack current;

    private Button current;

    private EditMenu(Player player,
                     String title,
                     HashMap<Integer, Button> buttons,
                     boolean preventClose,
                     Consumer<Player> closeFunction,
                     ItemStack background,
                     ItemStack def) {
        super(player, title, buttons, preventClose, closeFunction, background, 5);
    }

    @Override
    void openInventory() {

        Button surround = new Button.Builder()
                .icon(new ItemBuilder(Material.RED_STAINED_GLASS).name("&eSet to").toItem())
                .get();

        // set the border
        button(2, 1, surround);
        button(3, 2, surround);
        button(2, 3, surround);
        button(1, 2, surround);

        // set the item
        button(2, 2, current);

        button(2, 2,
                new Button.Builder().lmb(interact -> {
                    new AnvilGUI.Builder()
                            .title("Edit item name")
                            .itemLeft(current.itemStack)
                            .onClose(player -> {

                            })
                            .onComplete((player, s) -> {
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        //Main.getInstance().info("Runnable was called!");

                                    }
                                }.runTaskLater(Main.getInstance(), 0);

                                return AnvilGUI.Response.close();
                            });
                    return Button.Result.OK();
                }).get());

        //new AnvilGUI.Builder().
        //super.openInventory();
    }

    // this menu will be

}
