package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.*;

public abstract class AbstractMenu {

    enum Status {
        OPEN,
        REROUTING,
        CLOSED,
    }

    final static HashMap<UUID, AbstractMenu> openMenus =
            new HashMap<>();

    final Player player;

    final String inventoryTitle;
    final HashMap<Integer, Button> buttons;
    final Runnable openRunnable;
    final BiFunction<Player, Boolean, Result> closeFunction;
    final AbstractMenu.Builder parentBuilder;
    final AbstractMenu.Builder thisBuilder;

    Inventory inventory;
    Status status;

    AbstractMenu(Player player,
                 String inventoryTitle,
                 HashMap<Integer, Button> buttons,
                 Runnable openRunnable,
                 BiFunction<Player, Boolean, Result> closeFunction,
                 Builder parentBuilder,
                 Builder thisBuilder
    ) {
        Validate.notNull(inventoryTitle);

        this.player = player;

        this.inventoryTitle = inventoryTitle;
        this.buttons = buttons;
        this.openRunnable = openRunnable;
        this.closeFunction = closeFunction;
        this.parentBuilder = parentBuilder;
        this.thisBuilder = thisBuilder;
    }

    void openInventory(boolean sendOpenPacket) {
        for (Map.Entry<Integer,Button> entry : buttons.entrySet()) {
            Supplier<ItemStack> supplier = entry.getValue().getItemStackFunction;
            if (supplier != null) {
                ItemStack itemStack = supplier.get();
                inventory.setItem(entry.getKey(), itemStack);
            }
        }

        openMenus.put(player.getUniqueId(), this);
        this.status = Status.OPEN;
    }


    final void closeInventory() {
        closeInventory(true);
    }

    void closeInventory(boolean sendClosePacket) {
        if (status == Status.REROUTING) {
            if (closeFunction != null)
                invokeResult(null, closeFunction.apply(player, true));

            return;
        }
        if (status != Status.OPEN) {
            return;
        }

        status = Status.CLOSED;

        /*
         * Underlying nms implementation
         *      CraftEventFactory.handleInventoryCloseEvent(this);
         *      this.b.sendPacket(new PacketPlayOutCloseWindow(this.bV.j));
         *      this.o();
         */
        if (sendClosePacket)
            player.closeInventory();

        if (closeFunction != null)
            invokeResult(null, closeFunction.apply(player, false));
    }

    final Result invokeButtonAt(InventoryClickEvent event) {
        Button button = buttons.get(event.getSlot());

        if (button == null) {
            return null;
        }

        Button.Interact interact =
                new Button.Interact(player,
                        Objects.requireNonNull(event.getCursor()).getType() != Material.AIR ?
                                event.getCursor() : null,
                        event.getCurrentItem(),
                        event.isShiftClick(),
                        event.getClick() == ClickType.NUMBER_KEY ? event.getSlot() : -1);

        if (event.isLeftClick() && button.leftClickFunction != null)
            return button.leftClickFunction.apply(interact);
        else if (event.isRightClick() && button.rightClickFunction != null)
            return button.rightClickFunction.apply(interact);
        else if (event.getClick() == ClickType.MIDDLE && button.middleClickFunction != null)
            return button.middleClickFunction.apply(interact);
        else if (event.getClick() == ClickType.NUMBER_KEY && button.numberKeyFunction != null)
            return button.numberKeyFunction.apply(interact);
        else
            return null;
    }

    void invokeResult(InventoryClickEvent event, Result result) {
        if (result != null)
            result.invoke(this, event);
    }

    abstract void onInventoryClick(InventoryClickEvent event);

    void onInventoryDrag(InventoryDragEvent event) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (event.getRawSlots().contains(slot)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    void onInventoryClose(InventoryCloseEvent event) {
        closeInventory(false);

        if (status != Status.REROUTING) {
            openMenus.remove(player.getUniqueId());
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
        HashMap<Integer, Button.Builder> buttons = new HashMap<>();
        public AbstractMenu.Builder parentMenuBuilder;
        Runnable openRunnable;
        BiFunction<Player, Boolean, Result> closeFunction;

        boolean recursiveTitle = false;

        public Builder title(String title) {
            return this.title(title, this.recursiveTitle);
        }

        public Builder title(String title, boolean recursiveTitle) {
            Validate.notNull(title);
            this.title = title;
            this.recursiveTitle = recursiveTitle;
            return this;
        }

        public Builder onOpen(Runnable openRunnable) {
            this.openRunnable = openRunnable;
            return this;
        }

        public Builder onClose(BiFunction<Player, Boolean, Result> closeFunction) {
            Validate.notNull(closeFunction);
            this.closeFunction = closeFunction;
            return this;
        }

        /**
         * Set the parent MenuBuilder of this MenuBuilder
         * @param builder parent builder
         * @return this
         */
        public final Builder parent(Builder builder) {
            Validate.notNull(builder);
            parentMenuBuilder = builder;
            return this;
        }

        final Builder button(int slot, Button.Builder button) {
            Validate.notNull(button);
            buttons.put(slot, button);
            return this;
        }

        final Button.Builder getOrMakeButton(int slot, Supplier<ItemStack> getItemStackFunction) {
            Button.Builder button = buttons.putIfAbsent(slot, new Button.Builder().icon(getItemStackFunction));

            if (button == null)
                button = buttons.get(slot);

            return button;
        }

        final String getTitle() {
            return ChatColor.DARK_GRAY + (getRecursiveTitle()
                    .replace("> ",
                            ChatColor.GRAY + "> " + ChatColor.DARK_GRAY));
        }

        private static final int CUT_LENGTH = 29;

        private String getRecursiveTitle() {
            if (recursiveTitle && this.parentMenuBuilder != null) {
                String path = parentMenuBuilder.getRecursiveTitle() +
                        " > " + this.title;

                if (path.length() > CUT_LENGTH) {
                    return "..." + path.substring(path.length() - CUT_LENGTH);
                }
                return path;
            } else {
                return this.title;
            }
        }

        void validate() {
            Validate.notNull(title, "must assign a title");
        }

        /**
         * Constructs and opens the {@link AbstractMenu} to the player
         * @param player player to open to
         * @return the constructed menu
         */
        public abstract AbstractMenu open(Player player);
    }
}
