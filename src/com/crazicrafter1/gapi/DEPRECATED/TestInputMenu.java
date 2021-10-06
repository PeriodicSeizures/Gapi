package com.crazicrafter1.gapi.DEPRECATED;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.InputMenu;
import com.crazicrafter1.gapi.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestInputMenu extends InputMenu {

    public TestInputMenu() {
        super("Test Input Menu", new ItemBuilder(Material.NAME_TAG).toItem());
    }

    @Override
    protected void onInputClick(ItemStack output) {
        Main.getInstance().info("Got: " + output.getItemMeta().getDisplayName());
    }
}
