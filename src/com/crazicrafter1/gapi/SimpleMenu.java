package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleMenu extends AbstractMenu {
    private final ItemStack background;
    private final int columns;

    SimpleMenu(Player player,
               String inventoryTitle,
               HashMap<Integer, Button> buttons,
               boolean preventClose,
               Consumer<Player> closeFunction,
               AbstractMenu.Builder parentMenuBuilder,
               ItemStack background,
               int columns) {
        super(player, inventoryTitle, buttons, preventClose, closeFunction, parentMenuBuilder);
        this.background = background;
        this.columns = columns;
    }

    void button(int x, int y, Button button) {
        buttons.put(y*9 + x, button);
    }

    void delButton(int x, int y) {
        buttons.remove(y*9 + x);
    }

    void openInventory() {
        SimpleMenu instance = this;
        this.inventory = Bukkit.createInventory(null, columns*9, inventoryTitle);

        if (background != null)
            for (int i =0; i < inventory.getSize(); i++) {
                //if (inventory.getItem(i) == null)
                    inventory.setItem(i, background);
            }

        super.openInventory();

        player.openInventory(inventory);
    }

    public static class SBuilder extends Builder {
        private final static ItemStack BACKGROUND_1 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").toItem();

        ItemStack background;
        private int columns;

        // ref prev button
        private Button.Builder backButton;
        private int backButtonX;
        private int backButtonY;

        public SBuilder(int columns) {
            Validate.isTrue(columns >= 1, "columns must be greater or equal to 0 (" + columns + ")");
            Validate.isTrue(columns <= 6, "columns must be less or equal to 6 (" + columns + ")");
            this.columns = columns;
        }

        @Override
        public SBuilder title(String title) {
            return (SBuilder) super.title(title);
        }

        public SBuilder childButton(int x, int y, ItemStack itemStack, Builder menuToOpen) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");

            return (SBuilder) super.childButton(y*9 + x, itemStack, menuToOpen);
        }

        public SBuilder button(int x, int y, Button.Builder button) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");

            return (SBuilder) super.button(y*9 + x, button);
        }

        public SBuilder preventClose() {
            return (SBuilder) super.preventClose();
        }

        public SBuilder onClose(Consumer<Player> closeFunction) {
            return (SBuilder) super.onClose(closeFunction);
        }

        public SBuilder background() {
            return this.background(BACKGROUND_1);
        }

        public SBuilder background(ItemStack itemStack) {
            this.background = itemStack;
            return this;
        }

        /**
         * Set the prev button target button
         * @param x
         * @param y
         * @return
         */
        public SBuilder parentButton(int x, int y) {
            return this.parentButton(x, y, PREV_1);
        }

        public SBuilder parentButton(int x, int y, ItemStack itemStack) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");

            return button(x, y, new Button.Builder().icon(itemStack).lmb(
                interact -> Button.Result.open(parentMenuBuilder)
            ));
        }

        public SimpleMenu open(Player player) {
            SimpleMenu menu = new SimpleMenu(player,
                                             title,
                                             buttons,
                                             preventClose,
                                             closeFunction,
                                             parentMenuBuilder,
                                             background,
                                             columns);

            menu.openInventory();

            return menu;
        }
    }
}
