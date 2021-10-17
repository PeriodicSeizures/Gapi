package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import com.sun.istack.internal.Nullable;
import net.wesjd.anvilgui.version.VersionMatcher;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class TextMenu extends AbstractMenu {

    public static class Slot {

        private static final int[] values = new int[]{Slot.SLOT_LEFT,
                                                      Slot.SLOT_RIGHT,
                                                      Slot.SLOT_OUTPUT};

        public static final int SLOT_LEFT = 0;
        public static final int SLOT_RIGHT = 1;
        public static final int SLOT_OUTPUT = 2;
        public static int[] values() {
            return values;
        }

    }

    private static final VersionWrapper WRAPPER = new VersionMatcher().match(); //new Wrapper1_17_1_R1(); //new VersionMatcher().match();

    private int containerId;

    private TextMenu(Player player,
                     String inventoryTitle,
                     HashMap<Integer, Button> buttons,
                     //boolean preventClose,
                     Function<Player, EnumResult> closeFunction,
                     Builder parentBuilder,
                     Builder thisBuilder

    ) {
        super(player, inventoryTitle, buttons/*, preventClose*/, closeFunction, parentBuilder, thisBuilder);
    }

    @Override
    void openInventory(boolean sendOpenPacket) {
        WRAPPER.handleInventoryCloseEvent(player);
        WRAPPER.setActiveContainerDefault(player);

        //Bukkit.getPluginManager().registerEvents(listener, plugin);

        final Object container = WRAPPER.newContainerAnvil(player, inventoryTitle);

        //inventory.setItem(AnvilGUI.Slot.INPUT_LEFT, this.inputLeft);
        //if (this.inputRight != null) {
        //    inventory.setItem(AnvilGUI.Slot.INPUT_RIGHT, this.inputRight);
        //}

        containerId = WRAPPER.getNextContainerId(player, container);

        //if (sendOpenPacket)
            WRAPPER.sendPacketOpenWindow(player, containerId, inventoryTitle);
        WRAPPER.setActiveContainer(player, container);
        WRAPPER.setActiveContainerId(container, containerId);
        WRAPPER.addActiveContainerSlotListener(container, player);

        inventory = WRAPPER.toBukkitInventory(container);

        super.openInventory(sendOpenPacket);

    }

    @Override
    void closeInventory(boolean sendClosePacket) {
        if (status != Status.OPEN)
            return;
        //if (!open)
            //return;

        if (sendClosePacket) {
            WRAPPER.handleInventoryCloseEvent(player);
            WRAPPER.setActiveContainerDefault(player);
            WRAPPER.sendPacketCloseWindow(player, containerId);
        }

        super.closeInventory(false);
    }

    @Override
    void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);

        final ItemStack clicked = inventory.getItem(Slot.SLOT_OUTPUT);
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Object o = invokeButtonAt(event);
        invokeResult(event, o);
    }

    public static class TBuilder extends Builder {
        //private BiFunction<Player, String, EnumResult> completeFunction;
        //private Supplier<String> itemTextFunction;
        //private Supplier<String> itemLoreFunction;

        @Override
        public TBuilder title(String title) {
            return (TBuilder) super.title(title);
        }

        @Override
        public TBuilder title(String title, boolean recursiveTitle) {
            return (TBuilder) super.title(title, recursiveTitle);
        }

        //@Override
        //TBuilder button(int slot, Button.Builder button) {
        //    return (TBuilder) super.button(slot, button);
        //}

        //@Override
        //public TBuilder preventClose() {
        //    return (TBuilder) super.preventClose();
        //}

        @Override
        public TBuilder onClose(Function<Player, EnumResult> closeFunction) {
            return (TBuilder) super.onClose(closeFunction);
        }

        ///**
        // * These behaviours make little sense when also using text/lore java functional @interfaces
        // * too confusing
        // * @param button
        // * @return
        // */
        //public TBuilder leftInput(Button.Builder button) {
        //    return (TBuilder) super.button(Slot.SLOT_LEFT, button);
        //}

        //public TBuilder rightInput(Button.Builder button) {
        //    return (TBuilder) super.button(Slot.SLOT_RIGHT, button);
        //}

        //public TBuilder right(String name, String lore) {
        //    return super.button(Slot.SLOT_RIGHT)
        //}

        public TBuilder onComplete(BiFunction<Player, String, Object> completeFunction) {
            return (TBuilder) this.button(Slot.SLOT_OUTPUT, new Button.Builder()
                    .lmb(interact -> completeFunction.apply(interact.player,
                            interact.clickedItem.hasItemMeta() ? interact.clickedItem.getItemMeta().getDisplayName() : ""))
            );
            //this.completeFunction = completeFunction;

            //return this;
        }

        /**
         * Sets the left item. Will not translate color codes
         *
         * @param itemTextFunction The supplier text of the item
         * @return The {@link TextMenu.TBuilder} instance
         * @throws IllegalArgumentException if the text is null
         */
        public TBuilder left(Supplier<String> itemTextFunction) {
            return this.left(itemTextFunction, null);
        }

        /**
         * Sets the left item. Will translate color codes
         * @param itemTextFunction The supplier text of the item
         * @return The {@link TextMenu.TBuilder} instance
         * @throws IllegalArgumentException if the text is null
         */
        public TBuilder leftF(Supplier<String> itemTextFunction) {
            return this.leftF(itemTextFunction, null);
        }

        public TBuilder left(Supplier<String> itemTextFunction, @Nullable Supplier<String> itemLoreFunction) {
            Validate.notNull(itemTextFunction, "Left text function cannot be null");
            //this.itemTextFunction = itemTextFunction;
            return (TBuilder) this.button(Slot.SLOT_LEFT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemTextFunction.get(), false)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, false).toItem()));
        }

        public TBuilder leftF(Supplier<String> itemTextFunction, @Nullable Supplier<String> itemLoreFunction) {
            Validate.notNull(itemTextFunction, "Left text function cannot be null");
            //this.itemTextFunction = itemTextFunction;
            return (TBuilder) this.button(Slot.SLOT_LEFT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemTextFunction.get(), true)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, true).toItem()));
        }

        public TBuilder right(Supplier<String> itemNameFunction) {
            return this.right(itemNameFunction, null);
        }

        public TBuilder rightF(Supplier<String> itemNameFunction) {
            return this.rightF(itemNameFunction, null);
        }

        public TBuilder right(Supplier<String> itemNameFunction, @Nullable Supplier<String> itemLoreFunction) {
            Validate.notNull(itemNameFunction, "Right text function cannot be null");
            return (TBuilder) super.button(Slot.SLOT_RIGHT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemNameFunction.get(), false)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, false).toItem()));
        }

        public TBuilder rightF(Supplier<String> itemNameFunction, @Nullable Supplier<String> itemLoreFunction) {
            Validate.notNull(itemNameFunction, "Right text function cannot be null");
            return (TBuilder) super.button(Slot.SLOT_RIGHT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemNameFunction.get(), true)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, true).toItem()));
        }

        @Override
        void validate() {
            Validate.notNull(buttons.get(Slot.SLOT_LEFT), "Must assign left item");
            Validate.notNull(buttons.get(Slot.SLOT_OUTPUT), "Must assign complete function");

            super.validate();
        }

        @Override
        public TextMenu open(Player player) {
            Validate.notNull(player, "Player cannot be null");

            this.validate();

            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            TextMenu textMenu = new TextMenu(player,
                                             title,
                                             btns,
                                             //preventClose,
                                             closeFunction,
                                             parentMenuBuilder,
                                             this);

            textMenu.openInventory(true);

            return textMenu;
        }
    }

}
