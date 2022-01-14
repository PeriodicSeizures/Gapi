package com.crazicrafter1.gapi;

import org.bukkit.event.inventory.InventoryClickEvent;

class ResultOpen extends Result {

    public AbstractMenu.Builder builder;

    public ResultOpen(AbstractMenu.Builder builder) {
        this.builder = builder;
    }

    @Override
    public void invoke(AbstractMenu menu, InventoryClickEvent event) {
        builder.open(menu.player);
    }
}
