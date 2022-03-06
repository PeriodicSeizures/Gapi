package com.crazicrafter1.gapi.test;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.MathUtil;
import com.crazicrafter1.crutils.ProbabilityUtil;
import com.crazicrafter1.crutils.Util;
import com.crazicrafter1.gapi.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public enum EnumTest {

    SIMPLE(new SimpleMenu.SBuilder(3)
            .title(p -> "Simple Menu")
            .background()
            .button(4, 1, new Button.Builder()
                    .icon((p) -> ItemBuilder.copyOf(Material.FEATHER).name("&eHello, World!").build())
                    .lmb(interact -> Result.MESSAGE("Hello world!"))
            )
    ),
    NESTED(new SimpleMenu.SBuilder(3)
            .title(p -> "Test Nested Menu")
            .background()
            .childButton(4, 1, (p) -> ItemBuilder.copyOf(Material.FEATHER).name("&8Next menu").build(),
                    new SimpleMenu.SBuilder(3)
                            .title(p -> "Child menu 1")
                            .background()
                            .childButton(4, 1, (p) -> ItemBuilder.copyOf(Material.FEATHER).name("&8Next menu").build(),
                                    new SimpleMenu.SBuilder(3)
                                            .title(p -> "Child menu 2")
                                            .background()
                                            .childButton(4, 1, (p) -> ItemBuilder.copyOf(Material.FEATHER).name("&8Next menu").build(),
                                                    new SimpleMenu.SBuilder(3)
                                                            .title(p -> "Child menu 3")
                                                            .background()

                                                            .parentButton(4, 2))
                                            .parentButton(4, 2))
                            .parentButton(4, 2))
    ),
    TEXT(new SimpleMenu.SBuilder(3)
            .title(p -> "parent")
            .childButton(4, 1, (p) -> new ItemStack(Material.ANVIL),
                    new TextMenu.TBuilder()
                            .title(p -> "Text menu")
                            .leftRaw(p ->  "Default text!")
                            //.preventClose()
                            .onClose((player, reroute) -> Result.PARENT())
                            .onComplete((player, s, builder) -> {
                                player.sendMessage("You typed " + s);
                                return null;
                            })
            )),
    TITLES(                new SimpleMenu.SBuilder(3)
            .title(p -> "parent menu")
            .background()
            .childButton(4, 1, (p) -> ItemBuilder.copyOf(Material.FEATHER).name("&8Next menu").build(),
                    new SimpleMenu.SBuilder(3)
                            .title(p -> "child menu 1")
                            .background()
                            .childButton(4, 1, (p) -> ItemBuilder.copyOf(Material.FEATHER).name("&8Next menu").build(),
                                    new SimpleMenu.SBuilder(3)
                                            .title(p -> "child menu 2")
                                            .background()
                                            .childButton(4, 1, (p) -> ItemBuilder.copyOf(Material.FEATHER).name("&8Next menu").build(),
                                                    new SimpleMenu.SBuilder(3)
                                                            .title(p -> "child menu 3")
                                                            .background()

                                                            .parentButton(4, 2))
                                            .parentButton(4, 2))
                            .parentButton(4, 2))
    ),
    PARALLAX(new ParallaxMenu.PBuilder()
            .title(p -> ChatColor.DARK_GRAY + "Test Parallax Menu")
            .addAll((builder, p) -> {
                ArrayList<Button> result = new ArrayList<>();
                Material values[] = Material.values();
                for (int i = 0; i < 59; i++) {
                    Material material = values[ProbabilityUtil.randomRange(0, values.length - 1)];
                    while (!material.isItem()) {
                        material = values[ProbabilityUtil.randomRange(0, values.length - 1)];
                    }
                    Material finalMaterial = material;
                    result.add(new Button.Builder()
                            .icon(p1 -> ItemBuilder.copyOf(finalMaterial).build())
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
    HEADS(new ParallaxMenu.PBuilder().title(p -> "Minecraft-Heads").addAllAsync((builder, p) -> {
        ArrayList<Button> buttons = new ArrayList<>();
        try {
            String s = "https://minecraft-heads.com/scripts/api.php?";



            URL api = new URL(s + "cat=blocks");
            URLConnection con = api.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            JsonArray json = JsonParser.parseReader(new InputStreamReader(con.getInputStream())).getAsJsonArray();
            json.forEach(e -> buttons.add(
                    new Button.Builder().icon(p1 -> ItemBuilder.fromModernMaterial("PLAYER_HEAD").name(e.getAsJsonObject().get("name").getAsString())
                            .skull(e.getAsJsonObject().get("value").getAsString()).build()).lmb(interact -> Result.GRAB()).get())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buttons;
    })
    )
    ;

    private final AbstractMenu.Builder builder;

    EnumTest(AbstractMenu.Builder builder) {
        this.builder = builder;
    }

    public AbstractMenu.Builder getMenuBuilder() {
        return this.builder;
    }
}
