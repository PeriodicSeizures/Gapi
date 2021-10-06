package com.crazicrafter1.gapi.DEPRECATED.impl;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.ReflectionUtil;
import com.crazicrafter1.gapi.Button;
import com.crazicrafter1.gapi.Menu;
import com.crazicrafter1.gapi.DEPRECATED.RemovableComponent;
import com.crazicrafter1.gapi.DEPRECATED.TriggerComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public class ItemModifyMenu {
    /**
     * Default input item,
     * confirm button,
     * insertion callback?
     * confirm callback?
     *
     *
     *
     * @param itemDef
     */

    private ItemStack slottedItemStack;

    // @params        clicker  itemstack   close?
    private BiFunction<Player, ItemStack, Boolean> confirmFunction;

    public ItemModifyMenu(ItemStack itemDef) {
        super("Loot add: Item", 5, BACKGROUND_1);

        Button inputPerimeter = new Button() {
            @Override
            public ItemStack getIcon() {
                return new ItemBuilder(Material.RED_STAINED_GLASS).name("&eSet to").toItem();
            }
        };

        setComponent(1 + 1, 0, inputPerimeter);
        setComponent(1, 1, inputPerimeter);
        setComponent(2 + 1, 1, inputPerimeter);
        setComponent(1 + 1, 2, inputPerimeter);

        // Change to
        RemovableComponent rem = new RemovableComponent(loot.getIcon());
        setComponent(2, 1, rem);

        // Confirm
        setComponent(7, 1, new TriggerComponent() {
            @Override
            public void onLeftClick(Player p, boolean shift) {
                ItemStack item = rem.getIcon();
                if (item != null) {
                    Main.get().info("Applying changes here!");

                    // add ordinate item with <item, min, max>
                    // somehow
                    //lootGroup.loot.add(loot);
                    loot.itemStack = new ItemStack(item);

                    // then go back to prev menu
                    ((Menu) ReflectionUtil.invokeConstructor(prevMenuClass, lootSet)).open(p);
                }
            }

            @Override
            public ItemStack getIcon() {
                return new ItemBuilder(Material.EMERALD_BLOCK).name("&6&lApply changes").toItem();
            }
        });

        // MIN
        setComponent(1, 3, new TriggerComponent() {
            @Override
            public void onLeftClick(Player p, boolean shift) {
                // DECREMENT
                int change = shift ? 5 : 1;
                if (loot.min > change) {
                    // need to pass the rem icon if it is not null
                    //if (rem.getIcon() != null) {
                    //    loot.icon = rem.getIcon();
                    //}
                    loot.min -= change;
                    new EditLootItemMenu(loot, lootSet, prevMenuClass).show(p);
                }
            }

            @Override
            public void onRightClick(Player p, boolean shift) {
                //INCREMENT
                int change = shift ? 5 : 1;
                if (loot.min+change <= loot.max) {
                    //if (rem.getIcon() != null) {
                    //    loot.icon = rem.getIcon();
                    //}
                    loot.min += change;
                    new EditLootItemMenu(loot, lootSet, prevMenuClass).show(p);
                }
            }

            @Override
            public ItemStack getIcon() {
                return new ItemBuilder(Material.IRON_TRAPDOOR).count(loot.min)
                        .name("&aMin bound")
                        .lore("""
                                &8LMB: &c-   &8SHIFT: x5
                                &8RMB: &2+
                                """
                        ).toItem();
            }
        });

        // MAX
        setComponent(3, 3, new TriggerComponent() {
            @Override
            public void onLeftClick(Player p, boolean shift) {
                // DECREMENT
                int change = shift ? 5 : 1;
                if (loot.max >= loot.min + change) {
                    //if (rem.getIcon() != null) {
                    //    loot.icon = rem.getIcon();
                    //}
                    loot.max -= change;
                    new EditLootItemMenu(loot, lootSet, prevMenuClass).show(p);
                }
            }

            @Override
            public void onRightClick(Player p, boolean shift) {
                //INCREMENT
                int change = shift ? 5 : 1;
                if (loot.max+change <= 64) {
                    //if (rem.getIcon() != null) {
                    //    loot.icon = rem.getIcon();
                    //}
                    loot.max += change;
                    new EditLootItemMenu(loot, lootSet, prevMenuClass).show(p);
                }
            }

            @Override
            public ItemStack getIcon() {
                return new ItemBuilder(Material.IRON_DOOR).count(loot.max)
                        .name("&2Max bound")
                        .lore("""
                                &8LMB: &c-   &8SHIFT: x5
                                &8RMB: &2+
                                """
                        ).toItem();
            }
        });

        // back
        backButton(4, 4, BACK_1, prevMenuClass, lootSet);
    }

    // usage:
    // (x1, x2) -> x1 + x2
    // (player, itemStack) -> {// do stuff}
    public void onConfirm(BiFunction<Player, ItemStack> onConfirmCallback) {

    }

}

