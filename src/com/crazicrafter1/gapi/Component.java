package com.crazicrafter1.gapi;

import org.bukkit.inventory.ItemStack;

public class Component {

    private final ItemStack itemStack;

    public Component(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getIcon() {
        return this.itemStack;
    }
}
