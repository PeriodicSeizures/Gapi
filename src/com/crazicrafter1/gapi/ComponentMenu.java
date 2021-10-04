package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.ReflectionUtil;
import com.crazicrafter1.crutils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class ComponentMenu extends Menu {

    /**
     * Easy defaults to use
     */
    final protected static ItemStack BACKGROUND_1 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").toItem();
    final protected static ItemStack BACK_1 = new ItemBuilder(Material.ARROW).name("&cBack").toItem();

    private final int columns;
    protected final HashMap<Integer, Component> components = new HashMap<>();

    ComponentMenu(String title, int columns) {
        super(title);
        this.columns = columns;
        if (!Util.inRange(columns, 1, 6))
            throw new IndexOutOfBoundsException("invalid column (" + columns + ")");
    }

    @Override
    void onMenuClick(InventoryClickEvent event) {
        Component component = components.get(event.getRawSlot());
        onComponentClick(event, component);
    }

    @Override
    void fillInventory() {
        for (Map.Entry<Integer, Component> entry : components.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().getIcon());
        }
    }

    @Override
    void createInventory() {
        inventory = Bukkit.createInventory(
                null, columns * 9, getTitle());
    }

    final protected Component getComponent(int x, int y) {
        if (!Util.inRange(x, 0, 8) || !Util.inRange(y, 0, columns - 1))
            throw new IndexOutOfBoundsException("x or y out of bounds (" + x + ", " + y + ")");
        return components.get(y*9 + x);
    }

    final protected void setComponent(int x, int y, Component component) {
        if (!Util.inRange(x, 0, 8) || !Util.inRange(y, 0, columns - 1))
            throw new IndexOutOfBoundsException("x or y out of bounds (" + x + ", " + y + ")");

        //Main.getInstance().info("setting component at " + x + " " + y);

        components.put(y*9 + x, component);
    }

    final protected void removeComponent(int x, int y) {
        if (!(Util.inRange(x, 0, 8) && Util.inRange(y, 0, columns - 1)))
            throw new IndexOutOfBoundsException("x or y out of bounds (" + x + ", " + y + ")");

        components.remove(y*9 + x);
    }


    final protected void onComponentClick(InventoryClickEvent event, Component component) {
        event.setCancelled(true);

        if (component == null)
            return;

        if (component instanceof TriggerComponent trigger) {
            Player p = (Player) event.getWhoClicked();

            if (event.isLeftClick())
                trigger.onLeftClick(p, event.isShiftClick());
            else if (event.isRightClick())
                trigger.onRightClick(p, event.isShiftClick());
            else if (event.getClick() == ClickType.MIDDLE)
                trigger.onMiddleClick(p);

        } else if (component instanceof RemovableComponent rem) {

            if (event.isShiftClick())
                return;

            ItemStack cursor = event.getCursor();
            if (cursor != null && cursor.getType() != Material.AIR)
                rem.currentItem = new ItemStack(cursor);
            else rem.currentItem = null;
            event.setCancelled(false);
        }
    }

    final protected void backButton(int x, int y, ItemStack itemStack, Class<? extends Menu> prevMenuClass, Object ... args) {
        setComponent(x, y, new TriggerComponent() {
            @Override
            public void onLeftClick(Player p, boolean shift) {
                try {
                    ((Menu)ReflectionUtil.invokeConstructor(prevMenuClass, args)).show(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public ItemStack getIcon() {
                return itemStack;
            }
        });
    }


}
