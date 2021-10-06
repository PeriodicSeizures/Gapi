package com.crazicrafter1.gapi.DEPRECATED;

import com.crazicrafter1.gapi.Button;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class RemovableComponent extends Button {

    ItemStack currentItem;

    public RemovableComponent(ItemStack currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public final ItemStack getIcon() {
        return currentItem;
    }
}
