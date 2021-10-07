package com.crazicrafter1.gapi;

import com.crazicrafter1.gapi.anvil.AnvilGUI;
import com.crazicrafter1.gapi.anvil.VersionWrapper;
import com.crazicrafter1.gapi.anvil.Wrapper1_17_1_R1;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.function.Consumer;

public class TextMenu extends AbstractMenu {

    private static final VersionWrapper WRAPPER = new Wrapper1_17_1_R1(); //new VersionMatcher().match();

    public TextMenu(Player player,
                    String inventoryTitle,
                    HashMap<Integer, Button> buttons,
                    boolean preventClose,
                    Consumer<Player> closeFunction,
                    Builder parentMenuBuilder) {
        super(player, inventoryTitle, buttons, preventClose, closeFunction, parentMenuBuilder);
    }

    @Override
    void openInventory() {
        WRAPPER.handleInventoryCloseEvent(player);
        WRAPPER.setActiveContainerDefault(player);

        //Bukkit.getPluginManager().registerEvents(listener, plugin);

        final Object container = WRAPPER.newContainerAnvil(player, inventoryTitle);

        inventory = WRAPPER.toBukkitInventory(container);
        inventory.setItem(AnvilGUI.Slot.INPUT_LEFT, this.inputLeft);
        if (this.inputRight != null) {
            inventory.setItem(AnvilGUI.Slot.INPUT_RIGHT, this.inputRight);
        }

        containerId = WRAPPER.getNextContainerId(player, container);
        WRAPPER.sendPacketOpenWindow(player, containerId, inventoryTitle);
        WRAPPER.setActiveContainer(player, container);
        WRAPPER.setActiveContainerId(container, containerId);
        WRAPPER.addActiveContainerSlotListener(container, player);
        open = true;
    }

    @Override
    void closeInventory(boolean sendClosePacket) {
        super.closeInventory(sendClosePacket);
    }

    // abstract

}
