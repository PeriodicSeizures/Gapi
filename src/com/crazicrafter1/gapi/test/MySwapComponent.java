package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Main;
import com.crazicrafter1.gapi.SwapComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MySwapComponent extends SwapComponent {

    public MySwapComponent() {
        super(ItemBuilder.builder(Material.PAPER).name("Swap item").toItem());
    }

    @Override
    public void onInsert(Player player, ItemStack itemStack) {
        Main.getInstance().info("inserted!");
    }

    @Override
    public void onRemoval(Player player, ItemStack itemStack) {
        Main.getInstance().info("removed!");
    }

}
