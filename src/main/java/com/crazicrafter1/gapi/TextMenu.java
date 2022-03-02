package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ColorMode;
import com.crazicrafter1.crutils.ItemBuilder;
import com.crazicrafter1.crutils.TriFunction;
import com.crazicrafter1.crutils.Util;
import net.wesjd.anvilgui.version.VersionMatcher;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TextMenu extends AbstractMenu {

    public static final int SLOT_LEFT = 0;
    public static final int SLOT_RIGHT = 1;
    public static final int SLOT_OUTPUT = 2;

    private static final VersionWrapper WRAPPER = new VersionMatcher().match();

    private int containerId;

    private TextMenu(Player player,
                     Function<Player, String> getTitleFunction,
                     HashMap<Integer, Button> buttons,
                     Runnable openRunnable,
                     BiFunction<Player, Boolean, Result> closeFunction,
                     Builder builder

    ) {
        super(player, getTitleFunction, buttons, openRunnable, closeFunction, builder);
    }

    @Override
    void openInventory(boolean sendOpenPacket) {
        if (openRunnable != null)
            openRunnable.run();

        WRAPPER.handleInventoryCloseEvent(player);
        WRAPPER.setActiveContainerDefault(player);

        String title = getTitleFunction.apply(player);

        final Object container = WRAPPER.newContainerAnvil(player, title);

        containerId = WRAPPER.getNextContainerId(player, container);

        WRAPPER.sendPacketOpenWindow(player, containerId, title);
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
        public TBuilder title(@Nonnull Function<Player, String> getTitleFunction) {
            return (TBuilder) super.title(getTitleFunction);
        }

        @Override
        public TBuilder onOpen(@Nonnull Runnable openRunnable) {
            return (TBuilder) super.onOpen(openRunnable);
        }

        @Override
        public TBuilder onClose(@Nonnull BiFunction<Player, Boolean, Result> closeFunction) {
            return (TBuilder) super.onClose(closeFunction);
        }

        @Override
        public TBuilder onClose(@Nonnull Function<Player, Result> closeFunction) {
            return (TBuilder) super.onClose(closeFunction);
        }

        public TBuilder onComplete(@Nonnull TriFunction<Player, String, TBuilder, Result> completeFunction) {
            return (TBuilder) this.button(SLOT_OUTPUT, new Button.Builder()
                    .lmb((interact) -> completeFunction.apply(interact.player,
                            Util.strDef(ItemBuilder.mutable(interact.clickedItem).getName(), ""), (TBuilder) interact.menuBuilder))
            );
        }

        /**
         * Set left item text. Defaults to ColorMode.STRIP
         *
         * @param itemTextFunction The supplier text of the item
         * @return The {@link TBuilder} instance
         * @throws IllegalArgumentException if the text is null
         */
        public TBuilder leftRaw(@Nonnull Function<Player, String> itemTextFunction) {
            return this.leftRaw(itemTextFunction, null, ColorMode.STRIP);
        }

        public TBuilder leftRaw(@Nonnull Function<Player, String> itemTextFunction, @Nullable Function<Player, String> itemLoreFunction, @Nonnull ColorMode nameColorMode) {
            Validate.notNull(itemTextFunction);
            Validate.isTrue(nameColorMode == ColorMode.STRIP || nameColorMode == ColorMode.INVERT, "ColorMode must be STRIP or INVERT");

            return (TBuilder) this.button(SLOT_LEFT, new Button.Builder()
                    .icon((p) -> ItemBuilder.copyOf(Material.IRON_SWORD)
                            .name(Objects.requireNonNull(itemTextFunction.apply(p), "MenuBuilder incomplete; assign a name function"), nameColorMode)
                            .lore(itemLoreFunction != null ? itemLoreFunction.apply(p) : null).build()));
        }

        public TBuilder right(@Nonnull Function<Player, String> itemNameFunction) {
            return this.right(itemNameFunction, null);
        }

        public TBuilder right(@Nonnull Function<Player, String> itemNameFunction, @Nullable Function<Player, String> itemLoreFunction) {
            Validate.notNull(itemNameFunction);
            return (TBuilder) super.button(SLOT_RIGHT, new Button.Builder()
                    .icon((p) -> ItemBuilder.copyOf(Material.IRON_SWORD)
                            .name(itemNameFunction.apply(p))
                            .lore(itemLoreFunction != null ? itemLoreFunction.apply(p) : null).build()));
        }

        @Override
        public TextMenu open(@Nonnull Player player) {
            HashMap<Integer, Button> btns = new HashMap<>();
            buttons.forEach((i, b) -> btns.put(i, b.get()));

            TextMenu textMenu = new TextMenu(player,
                                             getTitleFunction,
                                             btns,
                                             openRunnable,
                                             closeFunction,
                                             this);

            textMenu.openInventory(true);

            return textMenu;
        }
    }

}
