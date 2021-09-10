package com.crazicrafter1.gapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TriggerComponent extends Component {

    public TriggerComponent(ItemStack itemStack) {
        super(itemStack);
    }

    public void onRightClick(Player p) { }

    public void onLeftClick(Player p) { }

    public void onMiddleClick(Player p) { }

}
