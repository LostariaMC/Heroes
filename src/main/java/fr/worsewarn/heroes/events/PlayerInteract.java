package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteract implements Listener {

    private Main pl;

    public PlayerInteract(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {


    }
}
