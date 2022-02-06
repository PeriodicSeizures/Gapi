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

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TextMenu extends AbstractMenu {

    public static final int SLOT_LEFT = 0;
    public static final int SLOT_RIGHT = 1;
    public static final int SLOT_OUTPUT = 2;

    private static final VersionWrapper WRAPPER = new VersionMatcher().match();

    private int containerId;

    private TextMenu(Player player,
                     String inventoryTitle,
                     HashMap<Integer, Button> buttons,
                     Runnable openRunnable,
                     BiFunction<Player, Boolean, Result> closeFunction,
                     Builder parentBuilder,
                     Builder thisBuilder

    ) {
        super(player, inventoryTitle, buttons, openRunnable, closeFunction, parentBuilder, thisBuilder);
    }

    @Override
    void openInventory(boolean sendOpenPacket) {
        if (openRunnable != null)
            openRunnable.run();

        WRAPPER.handleInventoryCloseEvent(player);
        WRAPPER.setActiveContainerDefault(player);

        final Object container = WRAPPER.newContainerAnvil(player, inventoryTitle);

        containerId = WRAPPER.getNextContainerId(player, container);

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

        final ItemStack clicked = inventory.getItem(SLOT_OUTPUT);
        if (clicked == null || clicked.getType() == Material.AIR) return;

        invokeResult(event, invokeButtonAt(event));
    }

    public static class TBuilder extends Builder {

        @Override
        public TBuilder title(String title) {
            return (TBuilder) super.title(title);
        }

        @Override
        public TBuilder title(String title, boolean recursiveTitle) {
            return (TBuilder) super.title(title, recursiveTitle);
        }

        @Override
        public TBuilder onOpen(Runnable openRunnable) {
            return (TBuilder) super.onOpen(openRunnable);
        }

        @Override
        public TBuilder onClose(BiFunction<Player, Boolean, Result> closeFunction) {
            return (TBuilder) super.onClose(closeFunction);
        }

        public TBuilder onComplete(BiFunction<Player, String, Result> completeFunction) {
            return (TBuilder) this.button(SLOT_OUTPUT, new Button.Builder()
                    .lmb((interact) -> completeFunction.apply(interact.player,
                            interact.clickedItem.hasItemMeta() ? interact.clickedItem.getItemMeta().getDisplayName() : ""))
            );
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
            Validate.notNull(itemTextFunction);
            return (TBuilder) this.button(SLOT_LEFT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemTextFunction.get(), false)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, false).toItem()));
        }

        public TBuilder leftF(Supplier<String> itemTextFunction, @Nullable Supplier<String> itemLoreFunction) {
            Validate.notNull(itemTextFunction);
            return (TBuilder) this.button(SLOT_LEFT, new Button.Builder()
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
            Validate.notNull(itemNameFunction);
            return (TBuilder) super.button(SLOT_RIGHT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemNameFunction.get(), false)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, false).toItem()));
        }

        public TBuilder rightF(Supplier<String> itemNameFunction, @Nullable Supplier<String> itemLoreFunction) {
            Validate.notNull(itemNameFunction);
            return (TBuilder) super.button(SLOT_RIGHT, new Button.Builder()
                    .icon(() -> new ItemBuilder(Material.IRON_SWORD)
                            .name(itemNameFunction.get(), true)
                            .lore(itemLoreFunction != null ? itemLoreFunction.get() : null, true).toItem()));
        }

        @Override
        public TextMenu open(Player player) {
            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            TextMenu textMenu = new TextMenu(player,
                                             title,
                                             btns,
                                             openRunnable,
                                             closeFunction,
                                             parentMenuBuilder,
                                             this);

            textMenu.openInventory(true);

            return textMenu;
        }
    }

}
