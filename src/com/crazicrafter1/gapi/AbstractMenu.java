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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.*;

public abstract class AbstractMenu {

    final static HashMap<UUID, AbstractMenu> openMenus =
            new HashMap<>();

    final Player player;

    String inventoryTitle;
    final HashMap<Integer, Button> buttons;
    //boolean preventClose;
    final BiFunction<Player, Boolean, EnumResult> closeFunction;
    private final AbstractMenu.Builder parentBuilder;
    final AbstractMenu.Builder thisBuilder;

    Inventory inventory;

    Status status;

    enum Status {
        OPEN,
        REROUTING,
        CLOSED,
    }

    AbstractMenu(Player player,
                 String inventoryTitle,
                 HashMap<Integer, Button> buttons,
                 //boolean preventClose,
                 BiFunction<Player, Boolean, EnumResult> closeFunction,
                 Builder parentBuilder,
                 Builder thisBuilder
    ) {
        Validate.notNull(inventoryTitle, "Inventory must be given a title");

        this.player = player;

        this.inventoryTitle = inventoryTitle;
        this.buttons = buttons;
        //this.preventClose = preventClose;
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
        Main.getInstance().debug("Putting AbstractMenu into HashMap");
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

    final Object invokeButtonAt(InventoryClickEvent event) {
        Button button = buttons.get(event.getSlot());

        if (button == null) {
            return EnumResult.OK;
        }

        /// TODO
        /// something here might be broken
        /// To reproduce issues:
        ///  - left click with no cancel
        ///  - left click again
        Button.Interact interact =
                new Button.Interact(player,
                        Objects.requireNonNull(event.getCursor()).getType() != Material.AIR ?
                                event.getCursor() : null,
                        event.getCurrentItem(),
                        event.isShiftClick(),
                        event.getClick() == ClickType.NUMBER_KEY ? event.getSlot() : -1);

        //Main.getInstance().error("" + interact);

        if (event.isLeftClick() && button.leftClickFunction != null)
            return button.leftClickFunction.apply(interact);
        else if (event.isRightClick() && button.rightClickFunction != null)
            return button.rightClickFunction.apply(interact);
        else if (event.getClick() == ClickType.MIDDLE && button.middleClickFunction != null)
            return button.middleClickFunction.apply(interact);
        else if (event.getClick() == ClickType.NUMBER_KEY && button.numberKeyFunction != null)
            return button.numberKeyFunction.apply(interact);
        else
            return EnumResult.OK;
    }

    void invokeResult(InventoryClickEvent event, Object o) {
        Main.getInstance().debug("Click invocation result: " + o);
        if (o instanceof Builder) {
            Main.getInstance().info("invokeResult: Got Builder");
            ((Builder) o).open(player);
        } else if (o instanceof String) {
            if (!(this instanceof TextMenu)) {
                throw new UnsupportedOperationException("EnumResult.TEXT is only usable with TextMenu");
            }
            inventory.setItem(TextMenu.Slot.SLOT_LEFT,
                    new ItemBuilder(Objects.requireNonNull(inventory.getItem(TextMenu.Slot.SLOT_LEFT))).name((String) o, false).toItem());
        }
        else if (o instanceof EnumResult) {
            Main.getInstance().info("invokeResult: Got EnumResult");

            EnumResult result = (EnumResult) o;

            switch (result) {
                case GRAB_ITEM: event.setCancelled(false); break;
                case CLOSE: closeInventory(true); break;
                case BACK: new BukkitRunnable() {
                    @Override
                    public void run() {
                        status = Status.REROUTING;
                        parentBuilder.open(player);
                    }
                }.runTaskLater(Main.getInstance(), 0);
                break;
                case REFRESH: new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.clear();
                        openInventory(false);
                    }
                }.runTaskLater(Main.getInstance(), 0);
                break;
            }
        }
        else
            throw new RuntimeException("Returned object must be of AbstractMenu.Builder, String, or EnumResult. Got: " + o);
    }

    /**
     * Event handlers
     */
    abstract void onInventoryClick(InventoryClickEvent event);

    void onInventoryDrag(InventoryDragEvent event) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (event.getRawSlots().contains(slot)) {
                event.setCancelled(true);
                break;
            }
        }
    }

    /**
     * Open has the intentional effects of preventing anomalies
     * from happening due to either:
     *  - InventoryClickEvent -> close
     *  - InventoryCloseEvent ->
     */
    void onInventoryClose(InventoryCloseEvent event) {
        closeInventory(false);
        Main.getInstance().info("AbstractMenu::onInventoryClose(): " + status.name());
        if (status != Status.REROUTING) {
            //if (preventClose) {
            //    //parentMenuBuilder.open(player);
            //    new BukkitRunnable() {
            //        @Override
            //        public void run() {
            //            openInventory(true);
            //        }
            //    }.runTaskLater(Main.getInstance(), 0);
            //} else {
                openMenus.remove(player.getUniqueId());
                Main.getInstance().debug("Removed AbstractMenu from HashMap");
            //}
        }
    }

    void button(int x, int y, Button button) {
        buttons.put(y*9 + x, button);
    }

    void delButton(int x, int y) {
        buttons.remove(y*9 + x);
    }

    /**
     * Used to easily build a Menu from scratch
     * Everything within this class is a one time
     * construction that will require a refresh/construct method
     * to reload everything
     */
    public static abstract class Builder {

        final static ItemStack PREV_1 = new ItemBuilder(Material.ARROW).name("&cBack").toItem();

        String title;
        //BiConsumer<AbstractMenu, Builder> openFunction;
        HashMap<Integer, Button.Builder> buttons = new HashMap<>();
        //boolean preventClose = false;
        public AbstractMenu.Builder parentMenuBuilder;
        BiFunction<Player, Boolean, EnumResult> closeFunction;

        boolean recursiveTitle = false;

        public Builder title(String title) {
            return this.title(title, this.recursiveTitle);
        }

        public Builder title(String title, boolean recursiveTitle) {
            Validate.notNull(title, "title cannot be set to null");
            this.title = title;
            this.recursiveTitle = recursiveTitle;
            return this;
        }

        //
        //public Builder onOpen(BiConsumer<AbstractMenu, Builder> openFunction) {
        //    // apply it
        //    this.openFunction = openFunction;
        //    return this;
        //}

        //public Builder preventClose() {
        //    preventClose = true;
        //    return this;
        //}

        public Builder onClose(BiFunction<Player, Boolean, EnumResult> closeFunction) {
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

                if (path.length() > CUT_LENGTH) { // 30 normally
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
