package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.events.PlayerChangeLanguageEvent;
import fr.worsewarn.heroes.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerChangeLanguage implements Listener {

    private Main pl;

    public PlayerChangeLanguage(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PlayerChangeLanguageEvent(PlayerChangeLanguageEvent event) {

        Player player = event.getPlayer();
        CosmoxPlayer cosmoxPlayer = WrappedPlayer.of(player).toCosmox();

        cosmoxPlayer.setScoreboard(pl.getManager().getScoreboard(player));
        pl.getManager().removePlayerBossBar(player);
        pl.getManager().addPlayerBossBar(player, cosmoxPlayer.getRedisPlayer().getLanguage());
    }
}
