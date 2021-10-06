package com.crazicrafter1.gapi.DEPRECATED;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestSimplexMenu extends SimplexMenu {

    public TestSimplexMenu() {
        super("Test simplex menu", 3,
                new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name("").toItem());

        this.setComponent(2, 1, new TriggerComponent() {
            @Override
            public void onLeftClick(Player p, boolean shift) {
                Main.getInstance().info("Trigger item was clicked!");
            }

            @Override
            public ItemStack getIcon() {
                return new ItemBuilder(Material.PAPER).name("Test item").toItem();
            }
        });

        this.setComponent(6, 1, new MySwapComponent());
    }
}
