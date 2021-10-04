package com.crazicrafter1.gapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TriggerComponent extends Component {

    public TriggerComponent() {
        super();
    }

    public void onRightClick(Player p, boolean shift) { }

    public void onLeftClick(Player p, boolean shift) { }

    public void onMiddleClick(Player p) { }

}
