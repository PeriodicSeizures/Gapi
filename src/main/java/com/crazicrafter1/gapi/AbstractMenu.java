package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ColorUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractMenu {

    enum Status {
        OPEN,
        REROUTING,
        CLOSED,
    }

    final static HashMap<UUID, AbstractMenu> openMenus =
            new HashMap<>();

    final Player player;

    final Function<Player, String> getTitleFunction;
    final HashMap<Integer, Button> buttons;
    final Runnable openRunnable;
    final BiFunction<Player, Boolean, Result> closeFunction;
    final Builder builder;

    Inventory inventory;
    Status status;

    AbstractMenu(Player player,
                 Function<Player, String> getTitleFunction,
                 HashMap<Integer, Button> buttons,
                 Runnable openRunnable,
                 BiFunction<Player, Boolean, Result> closeFunction,
                 Builder builder
    ) {
        Validate.notNull(player, "Player cannot be null");
        Validate.notNull(getTitleFunction);


        this.player = player;

        this.getTitleFunction = getTitleFunction;
        this.buttons = buttons;
        this.openRunnable = openRunnable;
        this.closeFunction = closeFunction;
        this.builder = builder;
    }

    void openInventory(boolean sendOpenPacket) {
        placeButtons();

        openMenus.put(player.getUniqueId(), this);
        this.status = Status.OPEN;
    }

    void placeButtons() {
        for (Map.Entry<Integer,Button> entry : buttons.entrySet()) {
            Function<Player, ItemStack> supplier = entry.getValue().getItemStackFunction;
            if (supplier != null) {
                ItemStack itemStack = supplier.apply(player);
                inventory.setItem(entry.getKey(), itemStack);
            }
        }
    }

    final void closeInventory() {
        closeInventory(true);
    }

    void closeInventory(boolean sendClosePacket) {
        if (status == Status.REROUTING) {
            // The close was caused by a new menu opening
            if (closeFunction != null) //                      player did NOT request
                invokeResult(null, closeFunction.apply(player, false));
        }
        else if (status == Status.OPEN) { // first iteration
            status = Status.CLOSED;

            /*
             * Underlying nms implementation
             *      CraftEventFactory.handleInventoryCloseEvent(this);
             *      this.b.sendPacket(new PacketPlayOutCloseWindow(this.bV.j));
             *      this.o();
             */
            if (sendClosePacket)
                player.closeInventory();

            // The close was directly caused by the player
            if (closeFunction != null)
                //                                              player did request
                invokeResult(null, closeFunction.apply(player, true));
        }
    }

    final Result invokeButtonAt(InventoryClickEvent event) {
        Button button = buttons.get(event.getSlot());

        if (button == null) {
            return null;
        }

        Button.Event e =
                new Button.Event(player,
                        Objects.requireNonNull(event.getCursor()).getType() != Material.AIR ?
                                event.getCursor() : null,
                        event.getCurrentItem(),
                        event.isShiftClick(),
                        event.getClick() == ClickType.NUMBER_KEY ? event.getSlot() : -1,
                        builder);

        if (event.isLeftClick() && button.leftClickFunction != null)
            return button.leftClickFunction.apply(e);
        else if (event.isRightClick() && button.rightClickFunction != null)
            return button.rightClickFunction.apply(e);
        else if (event.getClick() == ClickType.MIDDLE && button.middleClickFunction != null)
            return button.middleClickFunction.apply(e);
        else if (event.getClick() == ClickType.NUMBER_KEY && button.numberKeyFunction != null)
            return button.numberKeyFunction.apply(e);
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
        Function<Player, String> getTitleFunction;
        HashMap<Integer, Button.Builder> buttons = new HashMap<>();
        public Builder parentMenuBuilder;
        Runnable openRunnable;
        BiFunction<Player, Boolean, Result> closeFunction;

        public Builder title(Function<Player, String> getTitleFunction) {
            return this.title(getTitleFunction, ColorUtil.RENDER_ALL);
        }

        public Builder title(Function<Player, String> getTitleFunction, ColorUtil titleColorMode) {
            Validate.notNull(getTitleFunction);
            this.getTitleFunction = p -> titleColorMode.a(getTitleFunction.apply(p));
            return this;
        }

        /**
         * Execute a function on open
         *
         * @param openRunnable the runnable
         * @return this
         */
        public Builder onOpen(Runnable openRunnable) {
            this.openRunnable = openRunnable;
            return this;
        }

        /**
         * Execute a function on close
         *  TODO Debating on whether to remove
         *      this function and reroute detection
         * @param closeFunction the runnable
         * @return this
         */
        public Builder onClose(BiFunction<Player, Boolean, Result> closeFunction) {
            Validate.notNull(closeFunction);
            this.closeFunction = closeFunction;
            return this;
        }

        /**
         * Execute a function on close
         *
         * @param closeFunction the runnable
         * @return this
         */
        public Builder onClose(Function<Player, Result> closeFunction) {
            Validate.notNull(closeFunction);
            this.closeFunction = (p, request) -> closeFunction.apply(p);
            return this;
        }

        /**
         * Set the parent of this menu
         *
         * @param builder the parent
         * @return this
         */
        public final Builder parent(Builder builder) {
            Validate.notNull(builder);
            parentMenuBuilder = builder;
            return this;
        }

        final Builder button(int slot, Button.Builder button) {
            return button(slot, button, null);
        }

        final Builder button(int slot, Button.Builder button, @Nullable Button.Builder[] resOld) {
            Validate.notNull(button);
            Button.Builder b = buttons.putIfAbsent(slot, button);
            if (resOld != null) resOld[0] = b;
            return this;
        }

        @Nonnull
        final Button.Builder getOrMakeButton(int slot, Function<Player, ItemStack> getItemStackFunction) {
            Button.Builder[] old = new Button.Builder[1];
            button(slot, new Button.Builder().icon(getItemStackFunction), old);

            if (old[0] == null)
                old[0] = buttons.get(slot);

            return old[0];
        }

        /**
         * Constructs and opens the {@link AbstractMenu} to the player
         * @param player the player
         * @return the menu
         */
        public abstract AbstractMenu open(Player player);
    }
}
