package com.crazicrafter1.gapi;

import org.bukkit.inventory.ItemStack;

public class RemovableComponent extends Component {

    ItemStack currentItem;

    public RemovableComponent(ItemStack currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public final ItemStack getIcon() {
        return currentItem;
    }
}
