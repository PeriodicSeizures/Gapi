package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
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
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractMenu {

    final static HashMap<UUID, AbstractMenu> openMenus =
            new HashMap<>();

    final Player player;

    String inventoryTitle;
    final HashMap<Integer, Button> buttons;
    boolean preventClose;
    final Function<Player, EnumResult> closeFunction;
    private final AbstractMenu.Builder parentMenuBuilder;
    //private final AbstractMenu.Builder originalBuilder;
    //private int depth;
    //Consumer<Integer> refreshFunction;

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
                 Function<Player, EnumResult> closeFunction,
                 AbstractMenu.Builder parentMenuBuilder
                 //AbstractMenu.Builder originalBuilder,
                 //int depth,
                 //Consumer<Integer> refreshFunction
    ) {
        Validate.notNull(inventoryTitle, "Inventory must be given a title");

        this.player = player;

        this.inventoryTitle = inventoryTitle;
        this.buttons = buttons;
        this.preventClose = preventClose;
        this.closeFunction = closeFunction;
        this.parentMenuBuilder = parentMenuBuilder;
        //this.originalBuilder = originalBuilder;
        //this.depth = depth;
        //this.refreshFunction = refreshFunction;


        //this.depth = parentMenuBuilder.
    }

    void openInventory() {
        // super impl should create inventory
        // then calls this supermethod

        // there are several calls that have to occur sometime before this,
        // but should't be called in

        for (Map.Entry<Integer,Button> entry : buttons.entrySet()) {
            ItemStack itemStack = entry.getValue().getItemStackFunction.get();
            Main.getInstance().debug(entry.getKey() + " " +
                    itemStack.getType() + " " + itemStack.getItemMeta().getDisplayName());
            inventory.setItem(entry.getKey(), itemStack);
        }

        //player.openInventory(inventory);
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
                        event.getCursor(),
                        event.getCurrentItem(),
                        event.isShiftClick(),
                        event.getClick() == ClickType.NUMBER_KEY ? event.getSlot() : -1);

        if (event.isLeftClick() && button.leftClickFunction != null)
            return button.leftClickFunction.apply(interact);
        else if (event.isRightClick() && button.rightClickFunction != null)
            return button.rightClickFunction.apply(interact);
        else if (event.getClick() == ClickType.MIDDLE)
            return button.middleClickFunction.apply(interact);
        else if (event.getClick() == ClickType.NUMBER_KEY)
            return button.numberKeyFunction.apply(interact);
        else
            return EnumResult.OK;
    }

    void invokeResult(InventoryClickEvent event, Object o) {
        if (o instanceof AbstractMenu.Builder builder) {
            Main.getInstance().debug("Result: OPEN");
            //Main.getInstance().debug("Builder: ");
            builder.open(player);
        } else if (o instanceof EnumResult result) {
            Main.getInstance().debug("Result: " + result.name());
            switch (result) {
                case GRAB_ITEM -> event.setCancelled(false);
                case CLOSE -> closeInventory(true);
                case BACK -> parentMenuBuilder.open(player);
                case REFRESH -> openInventory(); //originalBuilder.open(player); //openInventory();
                case OK -> {
                    // do nothing
                }
            }
        }
        else
            throw new RuntimeException("Returned object must not be null, got: " + o);
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
        if (open) {
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
                Main.getInstance().debug("Removing AbstractMenu from HashMap");
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

    /**
     * Used to easily build a Menu from scratch
     * Everything within this class is a one time
     * construction that will require a refresh/construct method
     * to reload everything
     */
    public static abstract class Builder {

        final static ItemStack PREV_1 = new ItemBuilder(Material.ARROW).name("&cBack").toItem();

        String title;
        final HashMap<Integer, Button.Builder> buttons = new HashMap<>();
        boolean preventClose;
        Function<Player, EnumResult> closeFunction;
        AbstractMenu.Builder parentMenuBuilder;

        //Consumer<Integer> func;

        // new AbstractMenu.Builder()
        Supplier<Builder> makeMethod;

        //TODO might be able to remove
        //int depth = 0;


        public Builder title(String title) {
            Validate.notNull(title, "title cannot be null");
            this.title = title;
            return this;
        }

        public Builder preventClose() {
            preventClose = true;
            return this;
        }

        public Builder onClose(Function<Player, EnumResult> closeFunction) {
            Validate.notNull(closeFunction);
            this.closeFunction = closeFunction;
            return this;
        }

        public Builder validate() {
            Validate.notNull(title);
            return this;
        }

        /**
         * Set the parent MenuBuilder of this MenuBuilder
         * @param builder parent builder
         * @return this
         */
        final Builder parent(Builder builder) {
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
