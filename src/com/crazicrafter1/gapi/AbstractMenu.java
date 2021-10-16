package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.Util;
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
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractMenu {

    final static HashMap<UUID, AbstractMenu> openMenus =
            new HashMap<>();

    final Player player;

    String inventoryTitle;
    final HashMap<Integer, Button> buttons;
    //boolean preventClose;
    final Function<Player, EnumResult> closeFunction;
    private final AbstractMenu.Builder parentBuilder;
    final AbstractMenu.Builder thisBuilder;

    Inventory inventory;

    Status status;

    enum Status {
        OPEN,
        REROUTING,
        CLOSED,
    }

    /**
     * Needed to prevent infinite recursion
     * Impl will access too
     */
    //boolean open;

    AbstractMenu(Player player,
                 String inventoryTitle,
                 HashMap<Integer, Button> buttons,
                 //boolean preventClose,
                 Function<Player, EnumResult> closeFunction,
                 Builder parentBuilder,
                 Builder thisBuilder
                 //int depth,
                 //Consumer<Integer> refreshFunction
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


    public final void closeInventory() {
        closeInventory(true);
    }

    /**
     * Forcibly close inventory
     * Should be overridden for special behaviour
     *  but still called via super
     */
    void closeInventory(boolean sendClosePacket) {
        if (status != Status.OPEN) {
            return;
        }

        status = Status.CLOSED;

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
            invokeResult(null, closeFunction.apply(player));
    }

    /**
     * Invoke the button corresponding to @{event}
     * @param event
     * @return an {@link Object} of instance {@link Object[]} or {@link EnumResult}
     */
    Object invokeButtonAt(InventoryClickEvent event) {
        Button button = buttons.get(event.getSlot());

        if (button == null) {
            return EnumResult.OK;
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
            return EnumResult.OK;
    }

    void invokeResult(InventoryClickEvent event, Object o) {
        if (o instanceof Builder)
            ((Builder)o).open(player);
        else if (o instanceof String) {
            if (!(this instanceof TextMenu)) {
                throw new UnsupportedOperationException("EnumResult.TEXT is only usable with TextMenu");
            }
            //buttons.get(TextMenu.Slot.SLOT_LEFT).
            inventory.setItem(TextMenu.Slot.SLOT_LEFT,
                    new ItemBuilder(Objects.requireNonNull(inventory.getItem(TextMenu.Slot.SLOT_LEFT))).name((String) o, false).toItem());
        }
        else if (o instanceof EnumResult) {
            EnumResult result = (EnumResult) o;
            Main.getInstance().debug("Result: " + result.name());

            switch (result) {
                case GRAB_ITEM: event.setCancelled(false); break;
                case CLOSE: closeInventory(true); break;
                case BACK: new BukkitRunnable() {
                    @Override
                    public void run() {
                        status = Status.REROUTING;
                        parentBuilder.open(player);//.invokeResult(null, EnumResult.REFRESH);
                        //parentMenuBuilderopenInventory(false);
                    }
                }.runTaskLater(Main.getInstance(), 0);
                break;
                case REFRESH: new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.clear();
                        openInventory(false); //originalBuilder.open(player); //openInventory();
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
        // inventory size should start at 0 and be continuous until ending slot
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

        if (status != Status.REROUTING) {
            // fire closeFunction lambda
            closeInventory(false);

            //if (preventClose) {
            //    //parentMenuBuilder.open(player);
            //    new BukkitRunnable() {
            //        @Override
            //        public void run() {
            //            openInventory(true);
            //        }
            //    }.runTaskLater(Main.getInstance(), 0);
            //} else {
                Main.getInstance().debug("Removing AbstractMenu from HashMap");
                openMenus.remove(player.getUniqueId());

                // add impl to remove menu
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
    public static abstract class Builder /*implements Cloneable*/ {

        final static ItemStack PREV_1 = new ItemBuilder(Material.ARROW).name("&cBack").toItem();

        String title;
        HashMap<Integer, Button.Builder> buttons = new HashMap<>();
        //boolean preventClose = false;
        Function<Player, EnumResult> closeFunction;
        public AbstractMenu.Builder parentMenuBuilder;

        //Consumer<Integer> func;

        // new AbstractMenu.Builder()
        Supplier<Builder> makeMethod;

        //TODO might be able to remove
        //int depth = 0;


        public Builder title(String title) {
            Validate.notNull(title, "title cannot be set to null");
            this.title = title;
            return this;
        }

        //public Builder preventClose() {
        //    preventClose = true;
        //    return this;
        //}

        public Builder onClose(Function<Player, EnumResult> closeFunction) {
            Validate.notNull(closeFunction);
            this.closeFunction = closeFunction;
            return this;
        }

        void validate() {
            Validate.notNull(title, "must assign a title");
        }

        /**
         * Set the parent MenuBuilder of this MenuBuilder
         * @param builder parent builder
         * @return this
         */
        public final Builder parent(Builder builder) {
            Validate.notNull(builder);
            //this.depth = builder.depth + 1;
            parentMenuBuilder = builder;
            return this;
        }

        final Builder button(int slot, Button.Builder button) {
            buttons.put(slot, button);
            return this;
        }

        //final Builder append(int slot, EnumPress press, ItemStack defItemStack, Function<Button.Interact, Object> func) {
        //    Button.Builder button = buttons.putIfAbsent(slot, new Button.Builder().icon(defItemStack));

        //    if (button == null)
        //        button = buttons.get(slot);

        //    button.append(press, func);
        //    return this;
        //}

        // reconstruction refresh function

        final Button.Builder getOrMakeButton(int slot, Supplier<ItemStack> getItemStackFunction) {
            Button.Builder button = buttons.putIfAbsent(slot, new Button.Builder().icon(getItemStackFunction));

            if (button == null)
                button = buttons.get(slot);

            return button;
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
