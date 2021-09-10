package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.gapi.Component;
import com.crazicrafter1.gapi.Main;
import com.crazicrafter1.gapi.ParallaxMenu;
import com.crazicrafter1.gapi.TriggerComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TestParallaxMenu extends ParallaxMenu {

    public TestParallaxMenu() {
        super("Test parallax menu");

        Material[] materials = Material.values();
        // insert a bunch of random items
        for (int i=0; i < 62; i++) {
            int index = (int)(Math.random() * (double)(materials.length));

            addItem(new Component(ItemBuilder.builder(materials[index]).toItem()));
        }
    }
}
