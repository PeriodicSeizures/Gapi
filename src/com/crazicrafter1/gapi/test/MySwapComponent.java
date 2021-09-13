package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MySwapComponent extends Component {

    public MySwapComponent() {
        super();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.PAPER).name("Swap item").toItem();
    }
}
