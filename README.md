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

`childButton(int, int, ItemStack, Builder, ...)`
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

`appendChild(ItemStack, Builder, ...)`
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

SimpleMenu:
```java 
new SimpleMenu.SBuilder(3)
    .title("Simple Menu")
    .background()
    .button(4, 1, new Button.Builder()
                    .icon(new ItemBuilder(Material.FEATHER).name("&8Sample item").toItem()))
    .open(p);
``` 

pParallaxMenu:
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
            self.append(new Button.Builder()
                .icon(new ItemBuilder(material).toItem())
                .lmb(interact -> {
                    interact.player.sendMessage(ChatColor.GOLD + "I'm a " +
                        interact.clickedItem.getType().name().toLowerCase().replaceAll("_", " "));

                    // do nothing else on click
                    return Button.Result.OK();
                })
            );
        }
    })
    .open(p);
``` 

TextMenu:
```java 
new TextMenu.TBuilder()
    .title("Text menu")
    .text("Default text!")
    .onComplete((player, s) -> {
        p.sendMessage("You typed " + s);
        return Button.Result.OK();
    })
    .open(p);
``` 

### Complex nested example:
```java
new SimpleMenu.SBuilder(5)
    .title(ChatColor.DARK_GRAY + "LootCrates")
    .background()
    .childButton(1, 1, new ItemBuilder(Material.CHEST).name("&3&lCrates").toItem(), new ParallaxMenu.PBuilder()
        .title(ChatColor.DARK_GRAY + "Crates")
        .action(self -> {
            /*
             * CrateList
             */
            for (Map.Entry<String, Crate> entry : Main.get().data.crates.entrySet()) {
                Crate crate = entry.getValue();
                self.appendChild(new ItemBuilder(crate.itemStack).lore("&8id: " + crate.id).toItem(), new SimpleMenu.SBuilder(5)
                    .title("Crate: " + crate.id)
                    .background()
                    .childButton(3, 1, new ItemBuilder(Material.PAPER).name("&e&lChange Title").lore("&8Current: &r" + crate.title).toItem(), new TextMenu.TBuilder()
                        .onComplete((player, s) -> Button.Result.back())
                        .onClose(player -> Button.Result.back()))
                    .parentButton(4, 4));
            }
        })
        .parentButton(4, 5)
        .validate()
    ).childButton(3, 1, new ItemBuilder(Material.EXPERIENCE_BOTTLE).name("&6&lLoot").toItem(), new ParallaxMenu.PBuilder()
        .title(ChatColor.GOLD + "Loot")
        .action(self -> {
            for (Map.Entry<String, LootSet> entry : Main.get().data.lootGroups.entrySet()) {
                LootSet lootSet = entry.getValue();
                self.appendChild(new ItemBuilder(entry.getValue().itemStack).lore("&8id: " + entry.getKey() + "\n&8" + entry.getValue().loot.size() + " elements\n&8LMB: &2edit\n&8RMB: &cdelete").toItem(), new ParallaxMenu.PBuilder()
                    .title("lootSet.id: " + lootSet.id)
                    .action(c1_interact -> {
                        Main.get().info("lootSet: " + lootSet);
                        for (AbstractLoot a : lootSet.loot) {
                            Main.get().info("a: " + a);
                            c1_interact.append(new Button.Builder()
                                    .icon(new ItemBuilder(a.getIcon()).lore(a + "\n&8LMB: &2edit\n&8RMB: &cdelete").toItem())
                            );
                        }
                    })
                    .parentButton(4, 5)
                    .validate(),
                    interact -> {
                        Main.get().data.lootGroups.remove(entry.getKey());
                        for (Crate crate : Main.get().data.crates.values()) {
                            //self.add(new Button.Builder()
                            //        .)
                        }
                        return Button.Result.OK();
                    }
                );

            }
        })
        .parentButton(4, 5)
        .validate()
    ).validate().open(p);

```

## License
This project is licensed under the [MIT License](LICENSE).