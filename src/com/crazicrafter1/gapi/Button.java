package com.crazicrafter1.gapi;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;
import java.util.function.Supplier;

public class Button {

    public static class Interact {
        public Player player;
        public ItemStack heldItem;
        public ItemStack clickedItem;
        public boolean shift;
        public int numberKeySlot;

        public Interact(Player player,
                        ItemStack heldItem,
                        ItemStack clickedItem,
                        boolean shift,
                        int numberKeySlot) {
            this.player = player;
            this.heldItem = heldItem;
            this.clickedItem = clickedItem;
            this.shift = shift;
            this.numberKeySlot = numberKeySlot;
        }

        @Override
        public String toString() {
            return "Interact{" +
                    "player=" + player.getName() +
                    ", heldItem=" + heldItem +
                    ", clickedItem=" + clickedItem +
                    ", shift=" + shift +
                    ", numberKeySlot=" + numberKeySlot +
                    '}';
        }
    }

    Supplier<ItemStack> getItemStackFunction;
    final Function<Interact, Result> leftClickFunction;
    final Function<Interact, Result> middleClickFunction;
    final Function<Interact, Result> rightClickFunction;
    final Function<Interact, Result> numberKeyFunction;

    Button(Supplier<ItemStack> getItemStackFunction,
           Function<Interact, Result> leftClickFunction,
           Function<Interact, Result> middleClickFunction,
           Function<Interact, Result> rightClickFunction,
           Function<Interact, Result> numberKeyFunction) {
        this.getItemStackFunction = getItemStackFunction;
        this.leftClickFunction = leftClickFunction;
        this.middleClickFunction = middleClickFunction;
        this.rightClickFunction = rightClickFunction;
        this.numberKeyFunction = numberKeyFunction;
    }

    public static class Builder {
        Supplier<ItemStack> getItemStackFunction;
        Function<Interact, Result> leftClickFunction;
        Function<Interact, Result> middleClickFunction;
        Function<Interact, Result> rightClickFunction;
        Function<Interact, Result> numberKeyFunction;

        public Builder icon(Supplier<ItemStack> getItemStackFunction) {
            this.getItemStackFunction = getItemStackFunction;
            return this;
        }

        public Builder lmb(Function<Interact, Result> leftClickFunction) {
            this.leftClickFunction = leftClickFunction;
            return this;
        }

        public Builder mmb(Function<Interact, Result> middleClickFunction) {
            this.middleClickFunction = middleClickFunction;
            return this;
        }

        public Builder rmb(Function<Interact, Result> rightClickFunction) {
            this.rightClickFunction = rightClickFunction;
            return this;
        }

        public Builder num(Function<Interact, Result> numberKeyFunction) {
            this.numberKeyFunction = numberKeyFunction;
            return this;
        }

        /**
         * Attach an {@link AbstractMenu.Builder} to this button to open on
         * the specified {@link EnumPress}
         * @param menuToOpen the menu to open
         * @param press which press
         * @return this
         */
        public Builder bind(AbstractMenu.Builder menuToOpen,
                            EnumPress press) {
            Validate.notNull(menuToOpen, "Supplied menu must not be null");

            return this.append(press, (interact) -> Result.OPEN(menuToOpen));
        }

        /**
         * Bind a menu to LMB, and assign parent menu
         * @param parentBuilder the parent menu
         * @param menuToOpen the menu to open
         * @return this
         */
        public Builder child(AbstractMenu.Builder parentBuilder, AbstractMenu.Builder menuToOpen) {
            Validate.notNull(parentBuilder);

            menuToOpen.parent(parentBuilder);
            return bind(menuToOpen, EnumPress.LMB);
        }

        public Builder child(AbstractMenu.Builder parentBuilder,
                             AbstractMenu.Builder menuToOpen,
                             Function<Interact, Result> rightClickListener) {

            Validate.notNull(parentBuilder);

            menuToOpen.parent(parentBuilder);
            append(EnumPress.RMB, rightClickListener);
            return bind(menuToOpen, EnumPress.LMB);
        }

        /**
         * Combine button presses to listeners
         * @param press
         * @param func
         * @return
         */
        public Builder append(EnumPress press, Function<Interact, Result> func) {
            if (press != null)
                switch (press) {
                    case LMB: return lmb(func);
                    case MMB: return mmb(func);
                    case RMB: return rmb(func);
                    case NUM: return num(func);
                }

            throw new NullPointerException("Supplied EnumPress must not be null");
        }

        public Button get() {
            return new Button(getItemStackFunction,
                              leftClickFunction,
                              middleClickFunction,
                              rightClickFunction,
                              numberKeyFunction);
        }
    }
}
