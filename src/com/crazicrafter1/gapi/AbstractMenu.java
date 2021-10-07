package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractMenu {

    final static HashMap<UUID, AbstractMenu> openMenus =
            new HashMap<>();

    final Player player;

    String inventoryTitle;
    final HashMap<Integer, Button> buttons;
    boolean preventClose;
    final Consumer<Player> closeFunction;

    // parent menu
    private final AbstractMenu.Builder parentMenuBuilder;

    Inventory inventory;

    /**
     * Needed to prevent infinite recursion
     * Impl will access too
     */
    boolean open;

    AbstractMenu(Player player,
                 String inventoryTitle,
                 HashMap<Integer, Button> buttons,
                 boolean preventClose,
                 Consumer<Player> closeFunction,
                 AbstractMenu.Builder parentMenuBuilder) {
        this.player = player;

        this.inventoryTitle = inventoryTitle;
        this.buttons = buttons;
        this.preventClose = preventClose;
        this.closeFunction = closeFunction;
        this.parentMenuBuilder = parentMenuBuilder;
    }

    void openInventory() {
        // super impl should create inventory
        // then calls this supermethod

        // there are several calls that have to occur sometime before this,
        // but should't be called in

        for (Map.Entry<Integer,Button> entry : buttons.entrySet()) {
            inventory.setItem(entry.getKey(), entry.getValue().itemStack);
        }

        openMenus.put(player.getUniqueId(), this);

        open = true;
    }


    public final void closeInventory() {
        closeInventory(true);
    }

    /**
     * Forcibly close inventory
     * Should be overridden for special behaviour
     *  but still called via super
     */
    void closeInventory(boolean sendClosePacket) {
        if (!open) {
            return;
        }

        open = false;

        /*
         * Underlying nms implementation
         *      // same as AnvilGUI
         *      CraftEventFactory.handleInventoryCloseEvent(this);
         *
         *      // same as AnvilGUI
         *      this.b.sendPacket(new PacketPlayOutCloseWindow(this.bV.j));
         *
         *      // this does what...?
         *      this.o();
         */
        if (sendClosePacket)
            player.closeInventory();

        if (closeFunction != null)
            closeFunction.accept(player);
    }

    /**
     * Event handlers
     */
    final void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory().equals(inventory)) {
            Button button = buttons.get(event.getSlot());

            // always cancel the take item, for later override
            event.setCancelled(true);
            if (button == null) {
                return;
            }

            Button.Interact interact =
                    new Button.Interact(player, event.getCursor(), event.getCurrentItem(), event.isShiftClick());


            if (event.isLeftClick()) {
                if (button.lmb == null)
                    return;

                Button.Result result = button.lmb.apply(interact);

                Main.getInstance().debug("allow take: " + result.allowsTake());

                if (result.allowsTake()) // give item
                    event.setCancelled(false);
                else if (result.getBuilder() != null) // open a menu
                    result.getBuilder().open(player);
                else if (result.doClose()) { // close the menu gracefully
                    open = false;
                    player.closeInventory();
                } else {
                    // do nothing
                }
            } else if (event.isRightClick()) {
                if (button.rmb == null)
                    return;

                Button.Result result = button.rmb.apply(interact);

                Main.getInstance().debug("allow take: " + result.allowsTake());

                if (result.allowsTake()) // give item
                    event.setCancelled(false);
                else if (result.getBuilder() != null) // open a menu
                    result.getBuilder().open(player);
                else if (result.doClose()) { // close the menu gracefully
                    open = false;
                    player.closeInventory();
                } else {
                    // do nothing
                }
            }
        }
    }

    final void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().equals(inventory)) {
            // can be cancelled initially because
            // drag is kinda useless

            for (int slot = 0; slot < inventory.getSize(); slot++) {
                if (event.getRawSlots().contains(slot)) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    final void onInventoryClose(InventoryCloseEvent event) {
        if (open && event.getInventory().equals(this.inventory)) {

            // fire closeFunction lambda
            closeInventory(false);

            if (preventClose) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        openInventory();
                    }
                }.runTaskLater(Main.getInstance(), 1);
            } else {
                Main.getInstance().debug("Removing inventory");
                openMenus.remove(player.getUniqueId());

                // add impl to remove menu
            }
        }
    }

    void button(int x, int y, Button button) {
        buttons.put(y*9 + x, button);
    }

    void delButton(int x, int y) {
        buttons.remove(y*9 + x);
    }

    public static abstract class Builder {

        final static ItemStack PREV_1 = new ItemBuilder(Material.ARROW).name("&cBack").toItem();

        String title;
        final HashMap<Integer, Button> buttons = new HashMap<>();
        boolean preventClose;
        Consumer<Player> closeFunction;
        AbstractMenu.Builder parentMenuBuilder;

        public Builder title(String title) {
            Validate.notNull(title, "title cannot be null");
            this.title = title;
            return this;
        }

        /**
         * Insert a button that will open a menu on click
         * Will automatically assign the parent of the other menu
         * @param slot the inventory slot
         * @param itemStack the icon for the button
         * @param menuToOpen the menu to open on LMB
         * @return this
         */
        Builder childButton(int slot, ItemStack itemStack, Builder menuToOpen) {
            return button(slot, new Button.Builder()
                    .icon(itemStack)
                    .lmb(interact -> Button.Result.open(menuToOpen.parent(this))));
        }

        // button should be overridden accordingly
        public Builder button(int slot, Button.Builder button) {
            buttons.put(slot, button.get());
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

        /**
         * Set the parent MenuBuilder of this MenuBuilder
         * @param builder parent builder
         * @return this
         */
        private Builder parent(Builder builder) {
            parentMenuBuilder = builder;
            return this;
        }

        /**
         * Transform this Builder into a graphical menu
         * @param player target
         * @return the built menu
         * This method return type should be overridden to return sub Builder
         */
        public abstract AbstractMenu open(Player player);

    }
}
