package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class PlayerInteractEntity implements Listener {

    private Main pl;

    public PlayerInteractEntity(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

        if(pl.getAPI().getManager().getPhase().getState() == 1 && event.getRightClicked() instanceof Villager && !event.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {

            pl.getManager().openUpgrades(event.getPlayer());
            event.setCancelled(true);

        }
    }
}
