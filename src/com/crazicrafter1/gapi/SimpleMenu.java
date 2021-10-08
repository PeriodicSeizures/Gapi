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
import java.util.function.Function;

public class SimpleMenu extends AbstractMenu {
    private final ItemStack background;
    private final int columns;

    SimpleMenu(Player player,
               String inventoryTitle,
               HashMap<Integer, Button> buttons,
               boolean preventClose,
               Function<Player, Button.Result> closeFunction,
               AbstractMenu.Builder parentMenuBuilder,
               ItemStack background,
               int columns) {
        super(player, inventoryTitle, buttons, preventClose, closeFunction, parentMenuBuilder);
        this.background = background;
        this.columns = columns;
    }

    @Override
    void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        invokeResult(event, invokeButtonAt(event));
    }

    void button(int x, int y, Button button) {
        buttons.put(y*9 + x, button);
    }

    void delButton(int x, int y) {
        buttons.remove(y*9 + x);
    }

    void openInventory() {
        // this causes the mouse reset,
        // but ensures that
        // the prev menu was correctly closed
        //player.closeInventory();

        this.inventory = Bukkit.createInventory(null, columns*9, inventoryTitle);

        if (background != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                //if (inventory.getItem(i) == null)
                inventory.setItem(i, background);
            }
        }

        player.openInventory(inventory);

        super.openInventory();

        //new BukkitRunnable() {
        //    @Override
        //    public void run() {
                //player.openInventory(inventory);
                //openMenus.put(player.getUniqueId(), self);
                //open = true;
        //    }
        //}.runTaskLater(Main.getInstance(), 0);
    }

    public static class SBuilder extends Builder {
        private final static ItemStack BACKGROUND_1 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").toItem();

        ItemStack background;
        private final int columns;

        public SBuilder(int columns) {
            Validate.isTrue(columns >= 1, "columns must be greater or equal to 0 (" + columns + ")");
            Validate.isTrue(columns <= 6, "columns must be less or equal to 6 (" + columns + ")");
            this.columns = columns;
        }

        @Override
        public SBuilder title(String title) {
            return (SBuilder) super.title(title);
        }

        @Override
        public SBuilder preventClose() {
            return (SBuilder) super.preventClose();
        }

        @Override
        public SBuilder onClose(Function<Player, Button.Result> closeFunction) {
            return (SBuilder) super.onClose(closeFunction);
        }

        /**
         * The LMB will always be bound to the child menu
         * Heavy customization is the aim of this plugin
         * So, how to denote these overrides and customization?
         * childButtonAndRMB
         * @param x
         * @param y
         * @param itemStack
         * @param menuToOpen
         * @param rightClickListener
         * @return
         */
        public SBuilder childButton(int x, int y, ItemStack itemStack, Builder menuToOpen, Function<Button.Interact, Button.Result> rightClickListener) {
            return this.button(x, y, new Button.Builder()
                    .icon(itemStack)
                    .lmb(interact -> Button.Result.open(menuToOpen.parent(this)))
                    .rmb(rightClickListener));
        }

        public SBuilder childButton(int x, int y, ItemStack itemStack, Builder menuToOpen) {
            return this.button(x, y, new Button.Builder()
                    .icon(itemStack)
                    .lmb(interact -> Button.Result.open(menuToOpen.parent(this))));
        }

        public SBuilder button(int x, int y, Button.Builder button) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");

            return (SBuilder) super.button(y*9 + x, button);
        }

        public SBuilder background() {
            return this.background(BACKGROUND_1);
        }

        public SBuilder background(ItemStack itemStack) {
            Validate.notNull(itemStack, "itemstack must not be null");
            Validate.isTrue(itemStack.getType() != Material.AIR, "itemstack must not be air");
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
            Validate.notNull(player, "Player cannot be null");

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
