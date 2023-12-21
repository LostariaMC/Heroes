package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.cosmox.game.events.PlayerJoinTeamEvent;
import fr.worsewarn.heroes.manager.HPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinTeam implements Listener {

    private Main pl;

    public PlayerJoinTeam(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerJoinTeamEvent(PlayerJoinTeamEvent event) {

        Player player = event.getPlayer();
        HPlayer hPlayer = pl.getPlayer(player);
        CosmoxPlayer cosmoxPlayer = WrappedPlayer.of(player).toCosmox();

        if(event.getTeam().equals(Team.NO_TEAM)) {

            pl.getManager().addPlayerToPendingSpawns(player.getUniqueId());
            cosmoxPlayer.setStatistic(GameVariables.GAMES_PLAYED, 1);
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(pl.getManager().getMapCenter());

    }
}
