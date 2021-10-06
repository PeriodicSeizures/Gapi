package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Menu {
    final static HashMap<UUID, Menu> openMenus = new HashMap<>();

    private final static ItemStack BACKGROUND_1 = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").toItem();
    private final static ItemStack PREV_1 = new ItemBuilder(Material.ARROW).name("&cBack").toItem();

    private final Player player;

    private final String title;
    private final HashMap<Integer, Button> buttons;
    private final boolean preventClose;
    private final Consumer<Player> closeFunction;
    private final ItemStack background;
    private final int columns;
    private Inventory inventory;

    private boolean open = false;

    Menu(Player player,
                String title,
                HashMap<Integer, Button> buttons,
                boolean preventClose,
                Consumer<Player> closeFunction,
                ItemStack background,
                int columns) {
        this.player = player;
        this.title = title;
        this.buttons = buttons;
        this.preventClose = preventClose;
        this.closeFunction = closeFunction;
        this.background = background;
        this.columns = columns;

        openInventory();
    }

    private void openInventory() {
        // Including this reset mouse position each time, is annoying
        //player.closeInventory();

        Menu instance = this;
        this.inventory = Bukkit.createInventory(null, columns*9, title);

        if (background != null)
            for (int i =0; i < inventory.getSize(); i++) {
                inventory.setItem(i, background);
            }

        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().itemStack);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                open = true;
                player.openInventory(inventory);
                openMenus.put(player.getUniqueId(), instance);
                player.updateInventory();
            }
        }.runTaskLater(Main.getInstance(), 1);
    }

    void handleInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == this.inventory) {

            Button button = buttons.get(event.getSlot());

            if (button == null) {
                event.setCancelled(true);
                return;
            }

            Player p = (Player) event.getWhoClicked();

            Button.Interact interact = new Button.Interact(p, event.getCursor(), event.getCurrentItem(), event.isShiftClick());

            if (event.isLeftClick()) {
                Button.Result result = button.lmb.apply(interact);

                event.setCancelled(result.isCancelled());

                if (result.getBuilder() != null) {
                    // open me then
                    result.getBuilder().open(p);
                } else if (result.doClose()) {
                    open = false;
                    player.closeInventory();
                }
            } else if (event.isRightClick()) {
                Button.Result result = button.rmb.apply(interact);

                event.setCancelled(result.isCancelled());

                if (result.getBuilder() != null) {
                    // open me then
                    result.getBuilder().open(p);
                }
            }
        }
    }

    void handleInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == this.inventory) {
            if (closeFunction != null)
                closeFunction.accept(player);

            if (preventClose && open) {
                openInventory();
            } else {
                Main.getInstance().debug("Removing inventory");
                openMenus.remove(player.getUniqueId());
            }
        }
    }

    public static class Builder {
        private String title;
        private final HashMap<Integer, Button> buttons = new HashMap<>();
        private boolean preventClose;
        private Consumer<Player> closeFunction;
        private ItemStack background;
        private int columns;

        // ref prev button
        private Button.Builder backButton;
        private int backButtonX;
        private int backButtonY;

        public Builder title(String title) {
            Validate.notNull(title, "title cannot be null");
            this.title = title;
            return this;
        }

        public Builder openButton(int x, int y, ItemStack itemStack, Builder otherMenu) {
            // Insert A BUTTON to OPEN A NEW MENU
            // Set BACK BUTTON TARGET of OTHER_MENU to THIS

            //

            return button(x, y, new Button.Builder()
                    .icon(itemStack)
                    .lmb(interact -> Button.Result.open(otherMenu.backButtonMenu(this))));
        }

        public Builder button(int x, int y, Button.Builder button) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");
            buttons.put(y*9 + x, button.get());
            return this;
        }

        public Builder preventClose() {
            preventClose = true;
            return this;
        }

        public Builder onClose(Consumer<Player> closeFunction) {
            this.closeFunction = closeFunction;
            return this;
        }

        public Builder background() {
            return this.background(BACKGROUND_1);
        }

        public Builder background(ItemStack itemStack) {
            this.background = itemStack;
            return this;
        }

        public Builder columns(int columns) {
            Validate.isTrue(columns >= 1, "columns must be greater or equal to 0 (" + columns + ")");
            Validate.isTrue(columns <= 6, "columns must be less or equal to 6 (" + columns + ")");
            this.columns = columns;
            return this;
        }

        public Builder backButton(int x, int y) {
            return this.backButton(x, y, PREV_1);
        }

        public Builder backButton(int x, int y, ItemStack itemStack) {
            Validate.isTrue(x >= 0, "x must be greater or equal to 0 (" + x + ")");
            Validate.isTrue(x <= 8, "x must be less or equal to 8 (" + x + ")");
            Validate.isTrue(y >= 0, "y must be greater or equal to 0 (" + y + ")");
            Validate.isTrue(y < columns, "y must be less than columns " + columns + " (" + y + ")");
            backButton = new Button.Builder().icon(itemStack);
            this.backButtonX = x;
            this.backButtonY = y;
            return this;
        }

        private Builder backButtonMenu(Builder builder) {
            backButton.lmb(interact -> Button.Result.open(builder));
            return this;
        }

        public Menu open(Player player) {
            if (backButton != null)
                button(this.backButtonX, this.backButtonY, backButton);

            return new Menu(player, title, buttons, preventClose, closeFunction, background, columns);
        }
    }
}
