package com.crazicrafter1.gapi;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public abstract class ComponentMenu extends Menu {

    private final int columns;
    protected final HashMap<Integer, Component> components = new HashMap<>();

    public ComponentMenu(String title, int columns) {
        super(title);
        this.columns = columns;
    }

    @Override
    final void initInventory() {
        inventory = Bukkit.createInventory(
                null, columns * 9, getTitle());
    }

    @Override
    void setupInventory() {
        for (Map.Entry<Integer, Component> entry : components.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getIcon());
        }
    }

    protected final Component getComponent(int x, int y) {
        return components.get(y*9 + x);
    }

    protected final void setComponent(int x, int y, Component component) {
        components.put(y*9 + x, component);
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

    void removeComponent(int x, int y) {
        components.remove(y*9 + x);
    }

}
