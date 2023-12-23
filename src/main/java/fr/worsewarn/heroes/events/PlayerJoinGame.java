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
        HPlayer hPlayer = pl.getPlayer(player);
        CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(player);

        if(cosmoxPlayer.getTeam().equals(Team.NO_TEAM)) {
            pl.getManager().addPlayerToPendingSpawns(player.getUniqueId());
            player.getInventory().clear();
        }

        cosmoxPlayer.setScoreboard(pl.getManager().getScoreboard(player));
        pl.getManager().removePlayerBossBar(player);
        pl.getManager().addPlayerBossBar(player, cosmoxPlayer.getRedisPlayer().getLanguage());

        player.teleport(pl.getManager().getMapCenter());

    }
}
