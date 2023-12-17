package fr.worsewarn.heroes.events;

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

        if(event.getTeam().equals(Team.NO_TEAM)) {

            pl.getManager().addPlayerToPendingSpawns(player.getUniqueId());
            return;
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(pl.getManager().getMapCenter());

    }
}
