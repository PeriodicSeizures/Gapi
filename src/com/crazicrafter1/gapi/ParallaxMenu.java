package com.crazicrafter1.gapi;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

public class ParallaxMenu extends Menu {

    ParallaxMenu(Player player, String title, HashMap<Integer, Button> buttons, boolean preventClose, Consumer<Player> closeFunction, ItemStack background, int columns) {
        super(player, title, buttons, preventClose, closeFunction, background, columns);
    }

    /**
     * Underlying menu implementation
     */

    // takes in all possible arguments
    // to make the menu and everything


    public static class PBuilder extends Builder {

        public PBuilder() {
            super.columns(6);
        }

        @Override
        public Menu.Builder button(int x, int y, Button.Builder button) {
            Validate.isTrue(x == 0 || x == 8, "x must be 0 or 8 (" + x + ")");
            Validate.isTrue(y == 0 || y == 5, "y must 0 or 5 (" + y + ")");
            return super.button(x, y, button);
        }

        @Override
        public Menu.Builder columns(int columns) {
            throw new UnsupportedOperationException("Setting colunms on ParallaxMenu is not supported");
        }

        @Override
        public Menu.Builder backButton(int x, int y, ItemStack itemStack) {
            Validate.isTrue(x == 0 || x == 8, "x must be 0 or 8 (" + x + ")");
            Validate.isTrue(y == 0 || y == 5, "y must 0 or 5 (" + y + ")");
            return super.backButton(x, y, itemStack);
        }

        @Override
        public ParallaxMenu open(Player player) {
            return new ParallaxMenu();
        }

    }

}
