package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {

    private Main pl;

    public PlayerDeath(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event) {

        event.setDeathMessage("");
        event.setKeepInventory(true);
        event.setDroppedExp(0);

        pl.getPlayer(event.getEntity()).kill();
    }
}
