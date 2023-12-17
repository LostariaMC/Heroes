package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItem implements Listener {

    private Main pl;

    public PlayerDropItem(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerDropItemEvent(PlayerDropItemEvent event) {

        event.setCancelled(true);
    }
}
