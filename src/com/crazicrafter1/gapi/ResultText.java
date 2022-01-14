package com.crazicrafter1.gapi;

import com.crazicrafter1.crutils.ItemBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

class ResultText extends Result {

    public String text;

    public ResultText(String text) {
        this.text = text;
    }

    @Override
    public void invoke(AbstractMenu menu, InventoryClickEvent event) {
        Validate.isTrue(menu instanceof TextMenu, "Must be used with text menu");

        menu.inventory.setItem(TextMenu.Slot.SLOT_LEFT,
                new ItemBuilder(Objects.requireNonNull(
                        menu.inventory.getItem(TextMenu.Slot.SLOT_LEFT)))
                        .name(text, false).toItem());


    }
}
