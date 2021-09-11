package com.crazicrafter1.gapi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("SameParameterValue")
public abstract class Menu {

    /**
     * Back button implementation:
     *  - On raw initial open, no actions needed
     *  - On overlap open, from either <listener or show>, set the back component
     *  -
     */
    static final HashMap<UUID, Menu> openMenus = new HashMap<>();

    private String title;
    Inventory inventory; // package-private access

    boolean justOpened = false; // to make the eventlistener not break in the case of
                                // overlap menus open
    private final int columns;
    protected final HashMap<Integer, Component> components = new HashMap<>();

    Menu(String title, int columns) {
        this.title = title;
        this.columns = columns;
    }

    abstract void onMenuClick(InventoryClickEvent event);

    abstract void setupMenu();

    // Override for further functionality
    void setupInventory() {
        for (Map.Entry<Integer, Component> entry : components.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getIcon());
        }
    }

    final protected Component getComponent(int x, int y) {
        return components.get(y*9 + x);
    }

    final protected void setComponent(int x, int y, Component component) {
        components.put(y*9 + x, component);
    }

    final protected void removeComponent(int x, int y) {
        components.remove(y*9 + x);
    }

    final protected void onComponentClick(InventoryClickEvent event, Component component) {
        event.setCancelled(true);

        if (event.isShiftClick() ||
                event.getClick() == ClickType.CREATIVE ||
                event.getClick() == ClickType.DROP
        )
            return;

        if (component == null)
            return;

        Player player = (Player) event.getWhoClicked();

        if (component instanceof TriggerComponent trigger) {
            Player p = (Player) event.getWhoClicked();
            switch (event.getClick()) {
                case LEFT -> trigger.onLeftClick(p);
                case RIGHT -> trigger.onRightClick(p);
                case MIDDLE -> trigger.onMiddleClick(p);
            }
        } else if (component instanceof SwapComponent swap) {
            // event.getCurrentItem() is null on insert
            // event.getCursor() is AIR on grab (never NULL)
            event.setCancelled(false);

            if (event.getCurrentItem() == null)
                swap.onInsert(player, event.getCursor());
            else swap.onRemoval(player, event.getCurrentItem());
        }
    }

    final public void show(Player player) {
        show(player, true);
    }

    final void show(Player player, boolean setupMenus) {
        openMenus.put(player.getUniqueId(), this);

        // Use when menu needs to be completely reloaded
        if (setupMenus)
            setupMenu(); // Component assign or whatever

        inventory = Bukkit.createInventory(
                null, columns * 9, getTitle());

        setupInventory(); // assign inventory items

        justOpened = true;
        player.openInventory(inventory);
        justOpened = false;
        player.updateInventory();
    }

    final String getTitle() {
        return title;
    }

    final void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes(
                '&', title);
    }

    // Class<Menu> prevMenuClass
    final protected void backButton(Class<? extends Menu> prevMenuClass, int x, int y, ItemStack itemStack) {
        setComponent(x, y, new TriggerComponent(itemStack) {
            @Override
            public void onLeftClick(Player p) {
                // instantiate new prevMenuClass
                // then show it
                try {
                    prevMenuClass.getConstructor().newInstance().show(p);
                    //menu.show(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
