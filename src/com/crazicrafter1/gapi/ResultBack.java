package com.crazicrafter1.gapi;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

class ResultBack extends Result {

    @Override
    public void invoke(AbstractMenu menu, InventoryClickEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                menu.status = AbstractMenu.Status.REROUTING;
                menu.parentBuilder.open(menu.player);
            }
        }.runTaskLater(Main.getInstance(), 0);
    }
}
