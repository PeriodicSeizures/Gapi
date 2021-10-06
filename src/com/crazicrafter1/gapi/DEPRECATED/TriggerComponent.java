package com.crazicrafter1.gapi.DEPRECATED;

import com.crazicrafter1.gapi.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class TriggerComponent extends Button {

    public class Interact {
        public Player player;
        public ClickType click;
        public ItemStack heldItem;
    }



    public TriggerComponent() {
        super();
    }

    public void onRightClick(Player p, boolean shift) {}

    public void onLeftClick(Player p, boolean shift) {}

    public void onMiddleClick(Player p) {}

}
