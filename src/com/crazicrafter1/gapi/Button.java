package com.crazicrafter1.gapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class Button {

    public static class Interact {
        public Player player;
        public ItemStack heldItem;
        public ItemStack clickedItem;
        boolean shift;
        int numberKeySlot;

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

        public boolean isShift() {
            return shift;
        }

        public int getPressedNumberKey() {
            return numberKeySlot;
        }
    }

    public static class Result {
        private boolean allowTake;
        private AbstractMenu.Builder builder;
        private boolean close;
        private boolean back;
        private boolean refresh;

        public Result(boolean allowTake, AbstractMenu.Builder builder, boolean close, boolean back, boolean refresh) {
            this.allowTake = allowTake;
            this.builder = builder;
            this.close = close;
            this.back = back;
            this.refresh = refresh;
        }

        public boolean allowsTake() {
            return allowTake;
        }

        public AbstractMenu.Builder getBuilder() {
            return builder;
        }

        public boolean doClose() {
            return close;
        }

        public boolean goBack() {
            return back;
        }

        //public boolean doRefresh() {
        //    return refresh;
        //}

        /**
         * To be called by Button return to do
         * something simple at end of click
         */
        public static Result take() {
            return new Result(true, null, false, false, false);
        }

        public static Result open(AbstractMenu.Builder builder) {
            return new Result(false, builder, false, false, false);
        }

        public static Result close() {
            return new Result(false, null, true, false, false);
        }

        public static Result back() {
            return new Result(false, null, false, true, false);
        }

        public static Result OK() {
            return new Result(false, null, false, false, false);
        }

        public static Result refresh() {
            return new Result(false, null, false, false, true);
        }
    }

    ItemStack itemStack;
    final Function<Interact, Result> leftClickFunction;
    final Function<Interact, Result> middleClickFunction;
    final Function<Interact, Result> rightClickFunction;
    final Function<Interact, Result> numberKeyFunction;

    Button(ItemStack itemStack,
           Function<Interact, Result> leftClickFunction,
           Function<Interact, Result> middleClickFunction,
           Function<Interact, Result> rightClickFunction,
           Function<Interact, Result> numberKeyFunction) {
        this.itemStack = itemStack;
        this.leftClickFunction = leftClickFunction;
        this.middleClickFunction = middleClickFunction;
        this.rightClickFunction = rightClickFunction;
        this.numberKeyFunction = numberKeyFunction;
    }

    public static class Builder {
        private ItemStack itemStack;
        private Function<Interact, Result> leftClickFunction;
        private Function<Interact, Result> middleClickFunction;
        private Function<Interact, Result> rightClickFunction;
        private Function<Interact, Result> numberKeyFunction;

        public Builder icon(ItemStack itemStack) {
            this.itemStack = itemStack;
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

        public Button get() {
            return new Button(itemStack,
                              leftClickFunction,
                              middleClickFunction,
                              rightClickFunction,
                              numberKeyFunction);
        }
    }
}
