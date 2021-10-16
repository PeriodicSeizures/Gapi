package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParallaxMenu extends SimpleMenu {

    private static final int ITEM_X = 1;
    private static final int ITEM_Y = 1;
    private static final int ITEM_W = 7;
    private static final int ITEM_H = 3;

    private static final int ITEM_X2 = ITEM_X + ITEM_W - 1;
    private static final int ITEM_Y2 = ITEM_Y + ITEM_H - 1;
    private static final int SIZE = ITEM_W * ITEM_H;

    //private final ArrayList<Button> orderedButtons;
    private int page = 1;

    //private Consumer<ParallaxMenu.PBuilder> action;
    private Function<Builder, ArrayList<Button>> orderedButtonsFunc;

    private ParallaxMenu(Player player,
                         String inventoryTitle,
                         HashMap<Integer, Button> buttons,
                         //boolean preventClose,
                         Function<Player, EnumResult> closeFunction,
                         Builder parentBuilder,
                         Builder thisBuilder,
                         ItemStack background,
                         //ArrayList<Button> orderedButtons
                         Function<Builder, ArrayList<Button>> orderedButtonsFunc
    ) {
        super(player, inventoryTitle, buttons/*, preventClose*/, closeFunction, parentBuilder, thisBuilder, background, 6);
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
                    .icon(() -> new ItemBuilder(Material.ARROW).name("&aPrevious Page").lore("&ePage " + (page-1)).toItem())
                    .lmb(interact -> {
                        prevPage();
                        return EnumResult.OK;
                    }).get());

        } else
            delButton(0, 5);

        if (page < getMaxPages(orderedButtons.size())) {
            button(8, 5, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.ARROW).name("&aNext Page").lore("&ePage " + (page+1)).toItem())
                    .lmb(interact -> {
                        nextPage(orderedButtons.size());
                        return EnumResult.OK;
                    }).get());
        } else
            delButton(8, 5);

        // now assign center block items

        final int size = orderedButtons.size();

        int startIndex = (page-1) * SIZE;
        final int endIndex = Util.clamp(
                startIndex + Util.clamp(size - startIndex, 0, SIZE),
                0, size-1);

        loop:
        for (int y = ITEM_Y; y < ITEM_Y2 + 1; y++) {
            for (int x = ITEM_X; x < ITEM_X2 + 1; x++) {
                // Delete old blocked button

                //Main.getInstance().info("" + startIndex + " " + endIndex);
                if (startIndex > endIndex) {
                    delButton(x, y);
                    //break loop;
                } else {

                    button(x, y, orderedButtons.get(startIndex++));
                }
            }
        }

        //player.openInventory(inventory);

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

        //private final ArrayList<Button.Builder> orderedButtons = new ArrayList<>();

        private Function<Builder, ArrayList<Button>> orderedButtonsFunc;

        public PBuilder() {
            super(6);
        }

        public PBuilder action(Function<Builder, ArrayList<Button>> orderedButtonsFunc) {
            Validate.notNull(orderedButtonsFunc);
            this.orderedButtonsFunc = orderedButtonsFunc;

            return this;
        }

        @Override
        public PBuilder title(String title) {
            return (PBuilder) super.title(title);
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

        //@Override
        //public PBuilder preventClose() {
        //    return (PBuilder) super.preventClose();
        //}

        @Override
        public PBuilder onClose(Function<Player, EnumResult> closeFunction) {
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

        //@Override
        //protected SBuilder clone() {
        //    PBuilder builder = new PBuilder();
        //    builder.title = title;
        //    builder.buttons = (HashMap<Integer, Button.Builder>) buttons.clone();
        //    builder.closeFunction = closeFunction;
        //    builder.parentMenuBuilder = parentMenuBuilder;
        //    builder.background = background; //.clone();
        //    builder.orderedButtonsFunc = orderedButtonsFunc;
        //    return builder;
        //}

        @Override
        void validate() {
            Validate.notNull(orderedButtonsFunc);
            super.validate();
        }

        @Override
        public ParallaxMenu open(Player player) {
            Validate.notNull(player, "Player cannot be null");

            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            ParallaxMenu menu = new ParallaxMenu(player,
                                                 title,
                                                 btns,
                                                 //preventClose,
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
