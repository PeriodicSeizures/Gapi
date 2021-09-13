package com.crazicrafter1.gapi;

import org.bukkit.inventory.ItemStack;

public class RemovableComponent extends Component {

    ItemStack currentItem;

    public RemovableComponent() {

    }

    @Override
    public final ItemStack getIcon() {
        return currentItem;
    }
}
