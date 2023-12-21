package fr.worsewarn.heroes.events;

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

        pl.getAPI().getPlayer(player).setScoreboard(pl.getManager().getScoreboard(player));
    }
}
