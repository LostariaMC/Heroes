package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.cosmox.game.events.PlayerJoinGameEvent;
import fr.worsewarn.heroes.manager.HPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinGame implements Listener {

    private Main pl;

    public PlayerJoinGame(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerJoinGameEvent(PlayerJoinGameEvent event) {

        Player player = event.getPlayer();
        CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(player);

        if(cosmoxPlayer.getTeam().equals(Team.NO_TEAM)) pl.getManager().addPlayerToPendingSpawns(player.getUniqueId());

        cosmoxPlayer.setScoreboard(pl.getManager().getScoreboard(player));

    }
}
