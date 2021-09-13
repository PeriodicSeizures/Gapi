package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Variable size menu
 */
public class ParallaxMenu extends ComponentMenu {

    private final ArrayList<Component> items = new ArrayList<>();
    private int page = 1;

    private static final int ITEM_X = 1;
    private static final int ITEM_Y = 1;
    private static final int ITEM_W = 7;
    private static final int ITEM_H = 3;

    private static final int ITEM_X2 = ITEM_X + ITEM_W - 1;
    private static final int ITEM_Y2 = ITEM_Y + ITEM_H - 1;
    private static final int SIZE = ITEM_W * ITEM_H;

    private final String originalTitle;

    public ParallaxMenu(String title) {
        super("", 6);
        this.originalTitle = title;
    }

    @Override
    final void onMenuClick(InventoryClickEvent event) {
        int x = event.getRawSlot() % 9;
        int y = event.getRawSlot() / 9;

        // Item click
       if (x >= ITEM_X && x <= ITEM_X2 && y >= ITEM_Y && y <= ITEM_Y2) {
           int relativeItemPitch = (y - ITEM_Y) * ITEM_W + (x - ITEM_X);
           int itemPitch = relativeItemPitch + (page-1) * SIZE;
           if (itemPitch < items.size())
               onComponentClick(event, items.get(itemPitch));
           //else {
           //    Main.getInstance().info("cancelled by default");
           //}
       } else {
            // Component click
            super.onMenuClick(event);
            //onComponentClick(event, getComponent(x, y));
       }

    }

    @Override
    final void setupMenu() {
        int maxPages = getMaxPages();
        setTitle("&8(" + page + "/" + maxPages + ") " + originalTitle);

        if (page != 1) {
            // Previous page
            //
            this.setComponent(0, 5,
                new TriggerComponent() {
                    @Override
                    public void onLeftClick(Player p) {
                        if (lastPage())
                            show(p);
                    }

                    @Override
                    public ItemStack getIcon() {
                        return new ItemBuilder(Material.ARROW).name("&aPrevious Page").lore("&ePage " + (page-1)).toItem();
                    }
                });
        } else
            removeComponent(0, 5);

        if (page != maxPages) {
            // Next page
            //
            setComponent(8, 5,
                new TriggerComponent() {
                    @Override
                    public void onLeftClick(Player p) {
                        if (nextPage())
                            show(p);
                    }

                    @Override
                    public ItemStack getIcon() {
                        return new ItemBuilder(Material.ARROW).name("&aNext Page").lore("&ePage " + (page+1)).toItem();
                    }
                });
        } else
            removeComponent(8, 5);
    }

    @Override
    final void fillInventory() {
        final int size = items.size();

        int startIndex = (page-1) * SIZE;
        final int endIndex = Util.clamp(
                startIndex + Util.clamp(size - startIndex, 0, SIZE),
                0, size-1);

        loop:
        for (int y = ITEM_Y; y < ITEM_Y2 + 1; y++) {
            for (int x = ITEM_X; x < ITEM_X2 + 1; x++) {
                inventory.setItem(y * 9 + x, items.get(startIndex).getIcon());
                startIndex++;
                if (startIndex > endIndex)
                    break loop;
                // hours of nothing just to find out that
                // the return was breaking everything
                // return; // <<< curse this
            }
        }

        super.fillInventory();
    }

    protected final void addItem(Component item) {
        items.add(item);
    }

    private int getMaxPages() {
        return 1 + (items.size() - 1) / SIZE;
    }

    private boolean lastPage() {
        if (page > 1) {
            page--;
            return true;
        }
        return false;
    }

    private boolean nextPage() {
        if (page < getMaxPages()) {
            page++;
            return true;
        }
        return false;
    }
}
