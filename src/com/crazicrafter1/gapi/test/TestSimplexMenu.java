package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Main;
import com.crazicrafter1.gapi.TriggerComponent;
import com.crazicrafter1.gapi.SimplexMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestSimplexMenu extends SimplexMenu {

    public TestSimplexMenu() {
        super("Test simplex menu", 3,
                ItemBuilder.builder(Material.BLACK_STAINED_GLASS_PANE).name("").toItem());

        this.setComponent(2, 1,
                new TriggerComponent(
                        ItemBuilder.builder(Material.PAPER).name("Test item").toItem()) {
            @Override
            public void onLeftClick(Player p) {
                Main.getInstance().info("Trigger item was clicked!");
            }
        });

        this.setComponent(6, 1, new MySwapComponent());
    }
}
