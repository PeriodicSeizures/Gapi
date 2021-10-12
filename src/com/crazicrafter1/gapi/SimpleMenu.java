package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleMenu extends AbstractMenu {
    private final ItemStack background;
    private final int columns;

    SimpleMenu(Player player,
               String inventoryTitle,
               HashMap<Integer, Button> buttons,
               boolean preventClose,
               Function<Player, EnumResult> closeFunction,
               Builder parentBuilder,
               Builder thisBuilder,
               ItemStack background,
               int columns) {
        super(player, inventoryTitle, buttons, preventClose, closeFunction, parentBuilder, thisBuilder);
        this.background = background;
        this.columns = columns;
    }

    @Override
    void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Object o = invokeButtonAt(event);
        Main.getInstance().debug("Result: " + o.getClass().getSimpleName());
        invokeResult(event, o);
    }

    void button(int x, int y, Button button) {
        buttons.put(y*9 + x, button);
    }

    void delButton(int x, int y) {
        buttons.remove(y*9 + x);
    }

    @Override
    void openInventory(boolean sendOpenPacket) {
        if (sendOpenPacket) {
            this.inventory = Bukkit.createInventory(null, columns * 9, inventoryTitle);
            player.openInventory(inventory);
        }

        if (background != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        //if (sendOpenPacket)
        // Will push event closeInventory if an inventory is opened


        super.openInventory(sendOpenPacket);
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
        public SBuilder onClose(Function<Player, EnumResult> closeFunction) {
            return (SBuilder) super.onClose(closeFunction);
        }

        /**
         * Set the menu to open with default LMB bound button
         * @param x horizontal position
         * @param y vertical position
         * @param getItemStackFunction button icon
         * @param menuToOpen
         * @return this
         */
        public SBuilder childButton(int x, int y,
                                    Supplier<ItemStack> getItemStackFunction, Builder menuToOpen) {

            // print before and after for debug
            Main.getInstance().debug("Got Builder: " + menuToOpen);
            Validate.notNull(menuToOpen);

            menuToOpen.parent(this);

            return this.bind(x, y, EnumPress.LMB, getItemStackFunction, menuToOpen);
        }

        public SBuilder childButton(int x, int y,
                                    Supplier<ItemStack> getItemStackFunction, Builder menuToOpen,
                                    Function<Button.Interact, Object> rightClickListener) {

            menuToOpen.parent(this);

            return this.button(x, y, new Button.Builder()
                    .icon(getItemStackFunction)
                    .bind(menuToOpen, EnumPress.LMB)
                    .rmb(rightClickListener));
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
            return this.parentButton(x, y, () -> PREV_1);
        }

        public SBuilder parentButton(int x, int y,
                                     Supplier<ItemStack> getItemStackFunction) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");

            //this.parentMenuBuilder =

            return button(x, y, new Button.Builder()
                    .icon(getItemStackFunction)
                    .lmb(interact -> EnumResult.BACK));

            //return this.bind(x, y, EnumPress.LMB, itemStack, parentMenuBuilder);
        }

        /**
         * Bind a menu to a button at location, upon {@link EnumPress} being invoked
         * @param x horizontal position
         * @param y vertical position
         * @param press bind to which event
         * @param getItemStackFunction button icon lambda
         * @param menuToOpen the menu to open on press
         * @return this
         */
        public SBuilder bind(int x, int y,
                             EnumPress press,
                             Supplier<ItemStack> getItemStackFunction, Builder menuToOpen) {
            //menuToOpen.parent(this);
            //this.parent(menuToOpen);

            this.getOrMakeButton(x, y, getItemStackFunction)
                    .bind(menuToOpen, press);
            return this;
        }

        final Button.Builder getOrMakeButton(int x, int y, Supplier<ItemStack> getItemStackFunction) {
            return super.getOrMakeButton(y*9 + x, getItemStackFunction);
        }

        public SimpleMenu open(Player player) {
            Validate.notNull(player, "Player cannot be null");

            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            SimpleMenu menu = new SimpleMenu(player,
                                             title,
                                             btns,
                                             preventClose,
                                             closeFunction,
                                             parentMenuBuilder,
                                             this,
                                             background,
                                             columns);

            menu.openInventory(true);

            return menu;
        }
    }
}
