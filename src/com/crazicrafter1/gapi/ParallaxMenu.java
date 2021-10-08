package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import com.crazicrafter1.gapi.Button;
import com.crazicrafter1.gapi.SimpleMenu;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class ParallaxMenu extends SimpleMenu {

    private final ArrayList<Button> orderedButtons;
    private int page = 1;

    private static final int ITEM_X = 1;
    private static final int ITEM_Y = 1;
    private static final int ITEM_W = 7;
    private static final int ITEM_H = 3;

    private static final int ITEM_X2 = ITEM_X + ITEM_W - 1;
    private static final int ITEM_Y2 = ITEM_Y + ITEM_H - 1;
    private static final int SIZE = ITEM_W * ITEM_H;

    private ParallaxMenu(Player player,
                         String inventoryTitle,
                         HashMap<Integer, Button> buttons,
                         boolean preventClose,
                         Function<Player, Button.Result> closeFunction,
                         AbstractMenu.Builder parentMenuBuilder,
                         ItemStack background,
                         ArrayList<Button> orderedButtons) {
        super(player, inventoryTitle, buttons, preventClose, closeFunction, parentMenuBuilder, background, 6);
        this.orderedButtons = orderedButtons;
    }

    @Override
    void openInventory() {
        this.inventory = Bukkit.createInventory(null, 6*9, inventoryTitle);

        // clear all buttons in square

        if (page > 1) {
            // Previous page
            //
            button(0, 5, new Button.Builder()
                    .icon(new ItemBuilder(Material.ARROW).name("&aPrevious Page").lore("&ePage " + (page-1)).toItem())
                    .lmb(interact -> {
                        prevPage();
                        return Button.Result.OK();
                    }).get());

        } else
            delButton(0, 5);

        if (page < getMaxPages()) {
            button(8, 5, new Button.Builder()
                    .icon(new ItemBuilder(Material.ARROW).name("&aNext Page").lore("&ePage " + (page+1)).toItem())
                    .lmb(interact -> {
                        nextPage();
                        return Button.Result.OK();
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

        player.openInventory(inventory);

        super.openInventory();
    }

    private int getMaxPages() {
        return 1 + (orderedButtons.size() - 1) / SIZE;
    }

    private void prevPage() {
        if (page > 1) {
            page--;
            openInventory();
        }
    }

    private void nextPage() {
        if (page < getMaxPages()) {
            page++;
            openInventory();
        }
    }

    public static class PBuilder extends SBuilder {

        private final ArrayList<Button> orderedButtons = new ArrayList<>();

        public PBuilder() {
            super(6);
        }

        /**
         * Add unit to list
         */
        public PBuilder add(Button.Builder button) {
            orderedButtons.add(button.get());
            return this;
        }

        /**
         * Add unit which open a menu on click
         */
        public PBuilder addChild(ItemStack itemStack, Builder menuToOpen) {
            orderedButtons.add(new Button.Builder()
                    .icon(itemStack)
                    .lmb(interact -> Button.Result.open(menuToOpen.parent(this))).get());
            return this;
        }

        public PBuilder action(Consumer<ParallaxMenu.PBuilder> self) {
            // for everytask, do
            self.accept(this);

            return this;
        }

        @Override
        public PBuilder title(String title) {
            return (PBuilder) super.title(title);
        }

        @Override
        public PBuilder childButton(int x, int y, ItemStack itemStack, Builder otherMenu) {
            return (PBuilder) super.childButton(x, y, itemStack, otherMenu);
        }

        @Override
        public PBuilder button(int x, int y, Button.Builder button) {
            // inverse case to show the block, and not block
            Validate.isTrue(!(x >= ITEM_X && x <= ITEM_X2 && y >= ITEM_Y && y <= ITEM_Y2),
                    "x, y must not be within center block (" + x + ", " + y + ")");
            return (PBuilder)super.button(x, y, button);
        }

        @Override
        public PBuilder preventClose() {
            return (PBuilder) super.preventClose();
        }

        @Override
        public PBuilder onClose(Function<Player, Button.Result> closeFunction) {
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
        public PBuilder parentButton(int x, int y, ItemStack itemStack) {
            Validate.isTrue(x == 0 || x == 8, "x must be 0 or 8 (" + x + ")");
            Validate.isTrue(y == 0 || y == 5, "y must 0 or 5 (" + y + ")");
            return (ParallaxMenu.PBuilder)super.parentButton(x, y, itemStack);
        }

        @Override
        public ParallaxMenu open(Player player) {
            ParallaxMenu menu = new ParallaxMenu(player, title, buttons, preventClose, closeFunction, parentMenuBuilder, background, orderedButtons);

            menu.openInventory();

            return menu;
        }

    }

}
