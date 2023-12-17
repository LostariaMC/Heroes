package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {

    private Main pl;

    public PlayerMove(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent event) {


    }
}
