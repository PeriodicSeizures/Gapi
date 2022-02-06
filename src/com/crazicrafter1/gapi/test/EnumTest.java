package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import com.crazicrafter1.gapi.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public enum EnumTest {

    SIMPLE(new SimpleMenu.SBuilder(3)
            .title("Simple Menu")
            .background()
            .button(4, 1, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.FEATHER).name("&eHello, World!").toItem())
                    .lmb(interact -> Result.MESSAGE("Hello world!"))
            )
    ),
    NESTED(new SimpleMenu.SBuilder(3)
            .title("Test Nested Menu")
            .background()
            .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                    new SimpleMenu.SBuilder(3)
                            .title("Child menu 1")
                            .background()
                            .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                    new SimpleMenu.SBuilder(3)
                                            .title("Child menu 2")
                                            .background()
                                            .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                                    new SimpleMenu.SBuilder(3)
                                                            .title("Child menu 3")
                                                            .background()

                                                            .parentButton(4, 2))
                                            .parentButton(4, 2))
                            .parentButton(4, 2))
    ),
    TEXT(new SimpleMenu.SBuilder(3)
            .title("parent")
            .childButton(4, 1, () -> new ItemStack(Material.ANVIL),
                    new TextMenu.TBuilder()
                            .title("Text menu")
                            .left(() -> "Default text!")
                            //.preventClose()
                            .onClose((player, reroute) -> Result.BACK())
                            .onComplete((player, s) -> {
                                player.sendMessage("You typed " + s);
                                return null;
                            })
            )),
    TITLES(                new SimpleMenu.SBuilder(3)
            .title("parent menu", true)
            .background()
            .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                    new SimpleMenu.SBuilder(3)
                            .title("child menu 1", true)
                            .background()
                            .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                    new SimpleMenu.SBuilder(3)
                                            .title("child menu 2", true)
                                            .background()
                                            .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                                                    new SimpleMenu.SBuilder(3)
                                                            .title("child menu 3", true)
                                                            .background()

                                                            .parentButton(4, 2))
                                            .parentButton(4, 2))
                            .parentButton(4, 2))
    ),
    PARALLAX(new ParallaxMenu.PBuilder()
            .title(ChatColor.DARK_GRAY + "Test Parallax Menu")
            .addAll((menu) -> {
                ArrayList<Button> result = new ArrayList<>();
                Material values[] = Material.values();
                for (int i = 0; i < 59; i++) {
                    Material material = values[Util.randomRange(0, values.length - 1)];
                    while (!material.isItem()) {
                        material = values[Util.randomRange(0, values.length - 1)];
                    }
                    Material finalMaterial = material;
                    result.add(new Button.Builder()
                            .icon(() -> new ItemBuilder(finalMaterial).toItem())
                            .lmb((interact) -> {
                                interact.player.sendMessage(ChatColor.GOLD + "I'm a " +
                                        interact.clickedItem.getType().name().toLowerCase()
                                                .replaceAll("_", " "));

                                // do nothing else on click
                                return null;
                            }).get()
                    );
                }
                return result;
            })),
    ;

    private final AbstractMenu.Builder builder;

    EnumTest(AbstractMenu.Builder builder) {
        this.builder = builder;
    }

    public AbstractMenu.Builder getMenuBuilder() {
        return this.builder;
    }
}
