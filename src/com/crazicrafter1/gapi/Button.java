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

    /*
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

        //**
        // * To be called by Button return to do
        // * something simple at end of click
        // *
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
     */

    //ItemStack itemStack;
    Supplier<ItemStack> getItemStackFunction;
    final Function<Interact, Object> leftClickFunction;
    final Function<Interact, Object> middleClickFunction;
    final Function<Interact, Object> rightClickFunction;
    final Function<Interact, Object> numberKeyFunction;

    Button(//ItemStack itemStack,
           Supplier<ItemStack> getItemStackFunction,
           Function<Interact, Object> leftClickFunction,
           Function<Interact, Object> middleClickFunction,
           Function<Interact, Object> rightClickFunction,
           Function<Interact, Object> numberKeyFunction) {
        //this.itemStack = itemStack;
        this.getItemStackFunction = getItemStackFunction;
        this.leftClickFunction = leftClickFunction;
        this.middleClickFunction = middleClickFunction;
        this.rightClickFunction = rightClickFunction;
        this.numberKeyFunction = numberKeyFunction;
    }

    public static class Builder {
        //private ItemStack itemStack;
        private Supplier<ItemStack> getItemStackFunction;
        private Function<Interact, Object> leftClickFunction;
        private Function<Interact, Object> middleClickFunction;
        private Function<Interact, Object> rightClickFunction;
        private Function<Interact, Object> numberKeyFunction;

        //public Builder icon(ItemStack itemStack) {
        //    this.itemStack = itemStack;
        //    return this;
        //}



        public Builder icon(Supplier<ItemStack> getItemStackFunction) {
            this.getItemStackFunction = getItemStackFunction;
            return this;
        }

        public Builder lmb(Function<Interact, Object> leftClickFunction) {
            this.leftClickFunction = leftClickFunction;
            return this;
        }

        public Builder mmb(Function<Interact, Object> middleClickFunction) {
            this.middleClickFunction = middleClickFunction;
            return this;
        }

        public Builder rmb(Function<Interact, Object> rightClickFunction) {
            this.rightClickFunction = rightClickFunction;
            return this;
        }

        public Builder num(Function<Interact, Object> numberKeyFunction) {
            this.numberKeyFunction = numberKeyFunction;
            return this;
        }

        public Builder bind(AbstractMenu.Builder menuToOpen,
                            EnumPress press) {

            Validate.notNull(menuToOpen, "Supplied menu must not be null");

            return this.append(press, (interact) -> EnumResult.OPEN(menuToOpen));
        }

        public Builder append(EnumPress press, Function<Interact, Object> func) {

            if (press != null)
                return switch (press) {
                    case LMB -> lmb(func);
                    case MMB -> mmb(func);
                    case RMB -> rmb(func);
                    case NUM -> num(func);
                };
            throw new NullPointerException("Supplied EnumPress must not be null");
        }

        public Button get() {
            return new Button(//itemStack,
                              getItemStackFunction,
                              leftClickFunction,
                              middleClickFunction,
                              rightClickFunction,
                              numberKeyFunction);
        }


    }
}
