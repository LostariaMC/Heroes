package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Arrays;

public class InventoryClick implements Listener {

    private Main pl;

    public InventoryClick(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void InventoryClickEvent(InventoryClickEvent event) {

        if(Arrays.asList(InventoryType.SlotType.ARMOR, InventoryType.SlotType.CRAFTING).contains(event.getSlotType())) event.setCancelled(true);
    }
}
