package com.crazicrafter1.gapi;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class TriggerComponent extends Component {

    public TriggerComponent() {
        super();
    }

    public void onRightClick(Player p) { }

    public void onLeftClick(Player p) { }

    public void onMiddleClick(Player p) { }

}
