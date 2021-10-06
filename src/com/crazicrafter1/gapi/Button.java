package com.crazicrafter1.gapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Button {

    public static class Interact {
        public Player player;
        public ItemStack heldItem;
        public ItemStack clickedItem;
        public boolean shift;

        public Interact(Player player, ItemStack heldItem, ItemStack clickedItem, boolean shift) {
            this.player = player;
            this.heldItem = heldItem;
            this.clickedItem = clickedItem;
            this.shift = shift;
        }
    }

    public static class Result {
        private boolean allowTake;
        private Menu.Builder builder;
        private boolean close;

        public Result(boolean allowTake, Menu.Builder builder, boolean close) {
            this.allowTake = allowTake;
            this.builder = builder;
            this.close = close;
        }

        public boolean isCancelled() {
            return !allowTake;
        }

        public Menu.Builder getBuilder() {
            return builder;
        }

        public boolean doClose() {
            return close;
        }

        public static Result take() {
            return new Result(true, null, false);
        }

        public static Result open(Menu.Builder builder) {
            return new Result(false, builder, false);
        }

        public static Result close() {
            return new Result(false, null, true);
        }
    }

    final ItemStack itemStack;
    final Function<Interact, Result> lmb;
    //Consumer<Player> mmb;
    final Function<Interact, Result> rmb;

    private Button(ItemStack itemStack,
                   Function<Interact, Result> leftClickListener,
                   Function<Interact, Result> rightClickListener) {
        this.itemStack = itemStack;
        this.lmb = leftClickListener;
        this.rmb = rightClickListener;
    }

    public static class Builder {
        private ItemStack itemStack;
        private Function<Interact, Result> leftClickListener;
        private Function<Interact, Result> rightClickListener;

        public Builder icon(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        // now if
        public Builder lmb(Function<Interact, Result> leftClickListener) {
            this.leftClickListener = leftClickListener;
            return this;
        }

        public Builder rmb(Function<Interact, Result> rightClickListener) {
            this.rightClickListener = rightClickListener;
            return this;
        }

        public Button get() {
            return new Button(itemStack, leftClickListener, rightClickListener);
        }
    }
}
