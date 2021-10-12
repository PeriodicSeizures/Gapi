package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import net.wesjd.anvilgui.version.VersionMatcher;
import net.wesjd.anvilgui.version.VersionWrapper;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    private final BiFunction<Player, String, EnumResult> completeFunction;

    private int containerId;

    private TextMenu(Player player,
                     String inventoryTitle,
                     HashMap<Integer, Button> buttons,
                     boolean preventClose,
                     Function<Player, EnumResult> closeFunction,
                     Builder parentBuilder,
                     Builder thisBuilder,
                     BiFunction<Player, String, EnumResult> completeFunction) {
        super(player, inventoryTitle, buttons, preventClose, closeFunction, parentBuilder, thisBuilder);
        this.completeFunction = completeFunction;
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

        Object o;

        if (event.getSlot() == Slot.SLOT_OUTPUT) {
            o = completeFunction.apply(player,
                    clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : "");
        } else {
            o = invokeButtonAt(event);
        }
        invokeResult(event, o);

    }

    public static class TBuilder extends Builder {
        private BiFunction<Player, String, EnumResult> completeFunction;
        private String itemText;

        @Override
        public TBuilder title(String title) {
            return (TBuilder) super.title(title);
        }

        //@Override
        //TBuilder button(int slot, Button.Builder button) {
        //    return (TBuilder) super.button(slot, button);
        //}

        @Override
        public TBuilder preventClose() {
            return (TBuilder) super.preventClose();
        }

        @Override
        public TBuilder onClose(Function<Player, EnumResult> closeFunction) {
            return (TBuilder) super.onClose(closeFunction);
        }

        public TBuilder leftInput(Button.Builder button) {
            return (TBuilder) super.button(Slot.SLOT_LEFT, button);
        }

        public TBuilder rightInput(Button.Builder button) {
            return (TBuilder) super.button(Slot.SLOT_RIGHT, button);
        }

        //public TBuilder right(String name, String lore) {
        //    return super.button(Slot.SLOT_RIGHT)
        //}

        public TBuilder onComplete(BiFunction<Player, String, EnumResult> completeFunction) {
            this.completeFunction = completeFunction;

            return this;
        }

        /**
         * Sets the initial item-text that is displayed to the user
         *
         * @param text The initial name of the item in the anvil
         * @return The {@link TextMenu.TBuilder} instance
         * @throws IllegalArgumentException if the text is null
         */
        public TBuilder text(String text) {
            Validate.notNull(text, "Text cannot be null");
            this.itemText = text;
            return this;
        }

        @Override
        public Builder validate() {
            Validate.notNull(completeFunction, "must assign an on complete function");

            // analyze SLOT 0 first item
            // must be not null and non-air material
            Button.Builder LEFT = buttons.get(Slot.SLOT_LEFT);
            if (LEFT == null) {
                LEFT = new Button.Builder().icon(() -> new ItemStack(Material.NAME_TAG));
            } else if (LEFT.get().getItemStackFunction == null) {
                LEFT.icon(() -> new ItemBuilder(Material.PAPER).toItem());
            }

            if (itemText != null) {
                // apply text to item
                ItemStack itemStack = LEFT.get().getItemStackFunction.get();
                LEFT.icon(() -> new ItemBuilder(itemStack).name(itemText).toItem());
            }

            buttons.put(Slot.SLOT_LEFT, LEFT);

            return super.validate();
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
                                             preventClose,
                                             closeFunction,
                                             parentMenuBuilder,
                                             this,
                                             completeFunction);

            textMenu.openInventory(true);

            return textMenu;
        }
    }

}
