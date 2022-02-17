package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.ReflectionUtil;
import com.crazicrafter1.crutils.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.*;

public class SimpleMenu extends AbstractMenu {
    private final ItemStack background;
    private final int columns;

    SimpleMenu(Player player,
               String inventoryTitle,
               HashMap<Integer, Button> buttons,
               Runnable openRunnable,
               BiFunction<Player, Boolean, Result> closeFunction,
               Builder parentBuilder,
               Builder thisBuilder,
               ItemStack background,
               int columns) {
        super(player, inventoryTitle, buttons, openRunnable, closeFunction, parentBuilder, thisBuilder);
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

    @Override
    void openInventory(boolean sendOpenPacket) {
        if (openRunnable != null) {
            openRunnable.run();
        }

        if (sendOpenPacket) {
            this.inventory = Bukkit.createInventory(null, columns * 9, inventoryTitle);
            player.openInventory(inventory);
        }

        if (background != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }
        }

        super.openInventory(sendOpenPacket);
    }

    public static class SBuilder extends Builder {
        final static ItemStack PREV_1 = ItemBuilder.copyOf(Material.ARROW).name("&cBack").build();
        private static final ItemStack BACKGROUND_1 = ItemBuilder.of("BLACK_STAINED_GLASS_PANE").name(" ").build();
        //private static final ItemStack BACKGROUND_1 = ItemBuilder.copyOf(ReflectionUtil.isAtLeastVersion("1_16") ?
        //    new ItemStack(Material.BLACK_STAINED_GLASS_PANE) :
        //    new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short) 15)).name(" ").build();

        ItemStack background;
        private final int columns;

        public SBuilder(int columns) {
            Validate.isTrue(columns >= 1, "columns must be greater or equal to 1 (" + columns + ")");
            Validate.isTrue(columns <= 6, "columns must be less or equal to 6 (" + columns + ")");
            this.columns = columns;
        }

        @Override
        public SBuilder title(String title) {
            return (SBuilder) super.title(title);
        }

        @Override
        public SBuilder title(String title, boolean recursiveTitle) {
            return (SBuilder) super.title(title, recursiveTitle);
        }

        @Override
        public SBuilder onOpen(Runnable openRunnable) {
            return (SBuilder) super.onOpen(openRunnable);
        }

        @Override
        public SBuilder onClose(BiFunction<Player, Boolean, Result> closeFunction) {
            return (SBuilder) super.onClose(closeFunction);
        }

        /**
         * Bind a sub menu with LMB as the default button
         *
         * @param x horizontal position
         * @param y vertical position
         * @param getItemStackFunction button icon
         * @param builder the menu to eventually open
         * @return this
         */
        public SBuilder childButton(int x, int y,
                                    Supplier<ItemStack> getItemStackFunction, Builder builder) {
            builder.parent(this);

            return this.bind(x, y, EnumPress.LMB, getItemStackFunction, builder);
        }

        public SBuilder childButton(int x, int y,
                                    Supplier<ItemStack> getItemStackFunction, Builder menuToOpen,
                                    Function<Button.Interact, Result> rightClickListener) {

            menuToOpen.parent(this);

            return this.button(x, y, new Button.Builder()
                    .icon(getItemStackFunction)
                    .bind(menuToOpen, EnumPress.LMB)
                    .rmb(rightClickListener));
        }

        public SBuilder childButton(int x, int y,
                                    Supplier<ItemStack> getItemStackFunction, Builder builder, Supplier<Boolean> keepCondition) {
            if (keepCondition.get()) {
                builder.parent(this);

                return this.bind(x, y, EnumPress.LMB, getItemStackFunction, builder);
            }
            return this;
        }

        public SBuilder childButton(int x, int y,
                                    Supplier<ItemStack> getItemStackFunction, Builder menuToOpen,
                                    Function<Button.Interact, Result> rightClickListener, Supplier<Boolean> keepCondition) {

            if (keepCondition.get()) {
                menuToOpen.parent(this);

                return this.button(x, y, new Button.Builder()
                        .icon(getItemStackFunction)
                        .bind(menuToOpen, EnumPress.LMB)
                        .rmb(rightClickListener));
            }
            return this;
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
            Validate.notNull(itemStack);
            Validate.isTrue(itemStack.getType() != Material.AIR, "itemstack must not be air");
            this.background = itemStack;
            return this;
        }

        public SBuilder parentButton(int x, int y) {
            return this.parentButton(x, y, () -> PREV_1);
        }

        public SBuilder parentButton(int x, int y,
                                     Supplier<ItemStack> getItemStackFunction) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");

            return button(x, y, new Button.Builder()
                    .icon(getItemStackFunction)
                    .lmb((interact) -> Result.BACK()));
        }

        /**
         * Attach a menu to a button on {@link EnumPress} being invoked
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
            menuToOpen.parent(this);

            this.getOrMakeButton(x, y, getItemStackFunction)
                    .bind(menuToOpen, press);
            return this;
        }

        final Button.Builder getOrMakeButton(int x, int y, Supplier<ItemStack> getItemStackFunction) {
            return super.getOrMakeButton(y*9 + x, getItemStackFunction);
        }

        public SimpleMenu open(Player player) {
            Validate.notNull(player);

            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            SimpleMenu menu = new SimpleMenu(player,
                                             getTitle(),
                                             btns,
                                             openRunnable,
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
