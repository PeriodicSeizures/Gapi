package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ParallaxMenu extends SimpleMenu {

    private static final int ITEM_X = 1;
    private static final int ITEM_Y = 1;
    private static final int ITEM_W = 7;
    private static final int ITEM_H = 3;

    private static final int ITEM_X2 = ITEM_X + ITEM_W - 1;
    private static final int ITEM_Y2 = ITEM_Y + ITEM_H - 1;
    private static final int SIZE = ITEM_W * ITEM_H;

    private int page = 1;

    private final Function<Builder, ArrayList<Button>> orderedButtonsFunc;

    private ParallaxMenu(Player player,
                         String inventoryTitle,
                         HashMap<Integer, Button> buttons,
                         Runnable openRunnable,
                         BiFunction<Player, Boolean, Result> closeFunction,
                         Builder parentBuilder,
                         Builder thisBuilder,
                         ItemStack background,
                         //ArrayList<Button> orderedButtons
                         Function<Builder, ArrayList<Button>> orderedButtonsFunc
    ) {
        super(player, inventoryTitle, buttons, openRunnable, closeFunction, parentBuilder, thisBuilder, background, 6);
        //this.orderedButtons = orderedButtons;
        this.orderedButtonsFunc = orderedButtonsFunc;
    }

    @Override
    void openInventory(boolean sendOpenPacket) {
        //this.inventory = Bukkit.createInventory(null, 6*9, inventoryTitle);

        // clear all buttons in square

        ArrayList<Button> orderedButtons = orderedButtonsFunc.apply(thisBuilder);

        if (page > 1) {
            // Previous page
            //
            button(0, 5, new Button.Builder()
                    .icon(() -> ItemBuilder.copyOf(Material.ARROW).name("&aPrevious Page").lore("&ePage " + (page-1)).build())
                    .lmb((interact) -> {
                        prevPage();
                        return null;
                    }).get());

        } else
            delButton(0, 5);

        if (page < getMaxPages(orderedButtons.size())) {
            button(8, 5, new Button.Builder()
                    .icon(() -> ItemBuilder.copyOf(Material.ARROW).name("&aNext Page").lore("&ePage " + (page+1)).build())
                    .lmb((interact) -> {
                        nextPage(orderedButtons.size());
                        return null;
                    }).get());
        } else
            delButton(8, 5);

        // now assign center block items

        final int size = orderedButtons.size();

        int startIndex = (page-1) * SIZE;
        final int endIndex = Util.clamp(
                startIndex + Util.clamp(size - startIndex, 0, SIZE),
                0, size-1);

        for (int y = ITEM_Y; y < ITEM_Y2 + 1; y++) {
            for (int x = ITEM_X; x < ITEM_X2 + 1; x++) {
                if (startIndex > endIndex) {
                    delButton(x, y);
                } else {
                    button(x, y, orderedButtons.get(startIndex++));
                }
            }
        }

        super.openInventory(sendOpenPacket);
    }

    private int getMaxPages(int size) {
        return 1 + (size - 1) / SIZE;
    }

    private void prevPage() {
        if (page > 1) {
            page--;
            openInventory(true);
        }
    }

    private void nextPage(int size) {
        if (page < getMaxPages(size)) {
            page++;
            openInventory(true);
        }
    }

    public static class PBuilder extends SBuilder {
        private Function<Builder, ArrayList<Button>> orderedButtonsFunc;

        public PBuilder() {
            super(6);
        }

        public PBuilder addAll(Function<Builder, ArrayList<Button>> orderedButtonsFunc) {
            Validate.notNull(orderedButtonsFunc);
            this.orderedButtonsFunc = orderedButtonsFunc;

            return this;
        }

        @Override
        public PBuilder title(String title) {
            return (PBuilder) super.title(title);
        }

        @Override
        public PBuilder title(String title, boolean recursiveTitle) {
            return (PBuilder) super.title(title, recursiveTitle);
        }

        @Override
        public PBuilder childButton(int x, int y, Supplier<ItemStack> getItemStackFunction, Builder otherMenu) {
            Validate.isTrue(!(x >= ITEM_X && x <= ITEM_X2 && y >= ITEM_Y && y <= ITEM_Y2),
                    "x, y must not be within center block (" + x + ", " + y + ")");
            Validate.isTrue(!((x == 0 || x == 8) && y == 5), "button must not overlap page buttons");
            return (PBuilder) super.childButton(x, y, getItemStackFunction, otherMenu);
        }

        @Override
        public PBuilder button(int x, int y, Button.Builder button) {
            // inverse case to show the block, and not block
            Validate.isTrue(!(x >= ITEM_X && x <= ITEM_X2 && y >= ITEM_Y && y <= ITEM_Y2),
                    "x, y must not be within center block (" + x + ", " + y + ")");
            Validate.isTrue(!((x == 0 || x == 8) && y == 5), "button must not overlap page buttons");
            return (PBuilder) super.button(x, y, button);
        }

        @Override
        public PBuilder onOpen(Runnable openRunnable) {
            return (PBuilder) super.onOpen(openRunnable);
        }

        @Override
        public PBuilder onClose(BiFunction<Player, Boolean, Result> closeFunction) {
            return (PBuilder) super.onClose(closeFunction);
        }

        @Override
        public PBuilder background() {
            return (PBuilder) super.background();
        }

        @Override
        public PBuilder background(ItemStack itemStack) {
            return (PBuilder) super.background(itemStack);
        }

        @Override
        public PBuilder parentButton(int x, int y) {
            return (PBuilder) super.parentButton(x, y);
        }

        @Override
        public PBuilder parentButton(int x, int y, Supplier<ItemStack> getItemStackFunction) {
            Validate.isTrue(!(x >= ITEM_X && x <= ITEM_X2 && y >= ITEM_Y && y <= ITEM_Y2),
                    "x, y must not be within center block (" + x + ", " + y + ")");
            return (ParallaxMenu.PBuilder) super.parentButton(x, y, getItemStackFunction);
        }

        @Override
        public PBuilder bind(int x, int y, EnumPress press, Supplier<ItemStack> getItemStackFunction, Builder menuToOpen) {
            return (PBuilder) super.bind(x, y, press, getItemStackFunction, menuToOpen);
        }

        @Override
        public ParallaxMenu open(Player player) {
            Validate.notNull(player, "Player cannot be null");

            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            ParallaxMenu menu = new ParallaxMenu(player,
                                                 getTitle(),
                                                 btns,
                                                 openRunnable,
                                                 closeFunction,
                                                 parentMenuBuilder,
                                                 this,
                                                 background,
                                                 orderedButtonsFunc);

            menu.openInventory(true);

            return menu;
        }

    }

}
