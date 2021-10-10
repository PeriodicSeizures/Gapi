# Gapi

---

Gapi is an acronym for Graphical API.

## Features
This plugin has 3 different type of built-in menus for use with another plugin. Clickable buttons,
icons, backgrounds, and more are supplied for the menus.

## Requirements
Currently, only works for Spigot/Paper 1.17.1 with Java 16 recommended.

## Usage
## `? extends AbstractMenu`
The following is shared between all menus for building:

`title(String)` Set the inventory title of the menu

`preventClose()` Prevents the menu from closing on user exit

`onClose(Function<Player, Button.Result>)` Run when the menu closes

`validate()` Nice utility method to spot issues before creating menu



## `SimpleMenu : AbstractMenu`
Use `new SimpleMenu.SBuilder(int)` to create the most basic of menus, with specified columns

`button(int, int, Button.Builder)`
Set button in menu

`childButton(int, int, Supplier<ItemStack>, Builder, ...)`
Set button which opens the specified menu on LMB, with optional RMB press event

`background(...)`
Set the optional background ItemStack of the menu, defaults to a nameless BLACK_STAINED_GLASS_PANE

`parentButton(int, int, ...)`
Add a back button to return to the previous menu. 

* Parent menu must call `childButton(...)` or equivalent for this to work correctly



## `ParallaxMenu : SimpleMenu`
Use `new ParallaxMenu.PBuilder()` to create a menu which
can take a variable number of buttons, and displays them across 
pages

`append(Button.Builder button)`
Add an ordered button to the page list

`appendChild(Supplier<ItemStack>, Builder, ...)`
Same as above, except opens the target menu on LMB. Optional RMB event specification

`action(Consumer<ParallaxMenu.PBuilder>)`
Useful for adding multiple elements during a large nested menu construction



## `TextMenu : AbstractMenu`
Use `new TextMenu.TBuilder()` to create a menu similar to `AnvilGui`, but with support
for AbstractMenu features

`leftInput(Button.Builder)`
Set the left most item in the anvil menu

`text(String)`
Set the default input text

`onComplete(BiFunction<Player, String, Button.Result>)`
Run a function on output press. Is required; must be specified or error will occur

These functions can be chained together to easily create menus.

---

## Examples

SimpleMenu example:
```java  
new SimpleMenu.SBuilder(3)
        .title("Simple Menu")
        .background()
        .button(4, 1, new Button.Builder()
                        .icon(() -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem()))
        .open(p);
``` 

Locking a menu example:
```java 
new SimpleMenu.SBuilder(1)
        .title("Test Lockable Menu")
        .preventClose()
        .button(4, 0,
                new Button.Builder()
                        .icon(() -> new ItemBuilder(Material.IRON_DOOR).name("Unlock menu").toItem())
                        .lmb(interact -> EnumResult.CLOSE))
        .open(p);
break;
```

ParallaxMenu example:
```java  
new ParallaxMenu.PBuilder()
        .title(ChatColor.DARK_GRAY + "Test Parallax Menu")
        .action(self -> {
            Material values[] = Material.values();
            for (int i = 0; i < 59; i++) {
                Material material = values[Util.randomRange(0, values.length - 1)];
                while (!material.isItem()) {
                    material = values[Util.randomRange(0, values.length - 1)];
                }
                Material finalMaterial = material;
                self.append(new Button.Builder()
                        .icon(() -> new ItemBuilder(finalMaterial).toItem())
                        .lmb(interact -> {
                            interact.player.sendMessage(ChatColor.GOLD + "I'm a " +
                                    interact.clickedItem.getType().name().toLowerCase().replaceAll("_", " "));

                            // do nothing else on click
                            return EnumResult.OK;
                        })
                );
            }
        })
        .open(p);
``` 

TextMenu example:
```java  
new TextMenu.TBuilder()
        .title("Text menu")
        .text("Default text!")
        .onComplete((player, s) -> {
            p.sendMessage("You typed " + s);
            return EnumResult.OK;
        })
        .open(p);
```

Nested example:
```java 
new SimpleMenu.SBuilder(3)
        .title("Test Nested Menu")
        .background()
        .childButton(4, 1, () -> new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
                new SimpleMenu.SBuilder(3)
                        .title("Child menu 1")
                        .background()
                        .childButton(4, 1, () ->new ItemBuilder(Material.FEATHER).name("&8Next menu").toItem(),
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
        .open(p);
```

Complex nested example:
```java 
new SimpleMenu.SBuilder(5)
        .title(ChatColor.DARK_GRAY + "LootCrates")
        .background()
        /*
         * CrateList
         */
        .childButton(1, 1, () -> new ItemBuilder(Material.CHEST).name("&3&lCrates").toItem(), new ParallaxMenu.PBuilder()
                .title(ChatColor.DARK_GRAY + "Crates")
                .action(self -> {
                    for (Map.Entry<String, Crate> entry : Main.get().data.crates.entrySet()) {
                        Crate crate = entry.getValue();
                        self.appendChild(() -> new ItemBuilder(crate.itemStack).lore("&8id: " + crate.id).toItem(), new SimpleMenu.SBuilder(5)
                                .title("Crate: " + crate.id)
                                .background()
                                //.childButton(1, 1, new )
                                .childButton(3, 1, () -> new ItemBuilder(Material.PAPER).name("&e&lChange Title").lore("&8Current: &r" + crate.title).toItem(), new TextMenu.TBuilder()
                                        .onComplete((player, s) -> EnumResult.BACK)
                                        .onClose(player -> EnumResult.BACK))
                                .parentButton(4, 4)
                                .validate()
                        );
                    }
                })
                .parentButton(4, 5)
                .validate()
        /*
         * LootList
         */
        ).childButton(3, 1, () -> new ItemBuilder(Material.EXPERIENCE_BOTTLE).name("&6&lLoot").toItem(), new ParallaxMenu.PBuilder()
                .title(ChatColor.GOLD + "Loot")
                .action(self -> {
                    for (Map.Entry<String, LootSet> entry : Main.get().data.lootGroups.entrySet()) {
                        LootSet lootSet = entry.getValue();
                        self.appendChild(() -> new ItemBuilder(entry.getValue().itemStack).lore("&8id: " + entry.getKey() + "\n&8" + entry.getValue().loot.size() + " elements\n&8LMB: &2edit\n&8RMB: &cdelete").toItem(), new ParallaxMenu.PBuilder()
                                .title("lootSet.id: " + lootSet.id)
                                .action(c1_interact -> {
                                    for (AbstractLoot a : lootSet.loot) {
                                        Main.get().info("a: " + a);
                                        c1_interact.appendChild(() -> new ItemBuilder(a.getIcon()).lore(a + "\n&8LMB: &2edit\n&8RMB: &cdelete").toItem(),
                                                LootCratesAPI.getWrapperMenu(p, a, lootSet, c1_interact)
                                                //new Button.Builder()
                                                //.icon()
                                        );
                                    }
                                })
                                .childButton(6, 5, () -> new ItemBuilder(Material.NETHER_STAR).name("&2New...").toItem(), new TextMenu.TBuilder()
                                        .onClose(player -> EnumResult.BACK)
                                        .onComplete((player, text) -> {
                                            if (text.isEmpty()
                                                    || Main.get().data.lootGroups.containsKey(text)) {
                                                return EnumResult.OK;
                                            } else {
                                                // add that lootgroup
                                                Main.get().data.lootGroups.put(text,
                                                        new LootSet(text, new ItemStack(Material.RED_STAINED_GLASS),
                                                                new ArrayList<>(List.of(new LootItem()))));

                                                return EnumResult.OK;
                                            }
                                        })
                                )
                                .parentButton(4, 5)
                                .validate(),
                                // RMB event
                                interact -> {
                                    Main.get().data.lootGroups.remove(entry.getKey());
                                    for (Crate crate : Main.get().data.crates.values()) {
                                        Integer removed = crate.lootByWeight.remove(entry.getValue());
                                        if (removed != null)
                                            crate.weightsToSums();
                                    }
                                    return EnumResult.OK;
                                }
                        );

                    }
                })
                .parentButton(4, 5)
                .validate()
        )
        //.childButton(5, 1, new ItemBuilder(Material.FIREWORK_ROCKET).name("&e&lFireworks").toItem(), new SimpleMenu.SBuilder()
        //        .)
        .validate().open(p);
```

## Remarks
 - `Supplier<ItemStack>` is used for retrieving the icon from a `Button` instead of
`ItemStack` because of issues with trying to somehow refresh and update the menu.

## License
This project is licensed under the [MIT License](LICENSE).