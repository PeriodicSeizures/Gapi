package com.crazicrafter1.gapi.DEPRECATED;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.Button;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MySwapComponent extends Button {

    public MySwapComponent() {
        super();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.PAPER).name("Swap item").toItem();
    }
}
