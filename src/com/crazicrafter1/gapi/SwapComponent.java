package com.crazicrafter1.gapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class SwapComponent extends Component {

    public SwapComponent(ItemStack itemStack) {
        super(itemStack);
    }

    public void onInsert(Player player, ItemStack itemStack) { }

    public void onRemoval(Player player, ItemStack itemStack) { }
}
