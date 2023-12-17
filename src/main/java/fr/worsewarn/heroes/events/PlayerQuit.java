package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import fr.worsewarn.heroes.manager.ScoreboardFormat;
import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.game.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    private Main pl;

    public PlayerQuit(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(player);

        if(!cosmoxPlayer.getTeam().equals(Team.SPEC)) {

            pl.getPlayer(player).kill();

        }
    }
}
