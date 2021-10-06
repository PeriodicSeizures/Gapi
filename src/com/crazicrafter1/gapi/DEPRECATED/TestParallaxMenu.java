package com.crazicrafter1.gapi.DEPRECATED;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.Button;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestParallaxMenu extends ParallaxMenu {

    public TestParallaxMenu() {
        super("Test parallax menu");

        Material[] materials = Material.values();
        // insert a bunch of random items
        for (int i=0; i < 62; i++) {
            int index = (int)(Math.random() * (double)(materials.length));

            addItem(new Button() {
                @Override
                public ItemStack getIcon() {
                    return new ItemBuilder(materials[index]).toItem();
                }
            });
        }
    }
}
