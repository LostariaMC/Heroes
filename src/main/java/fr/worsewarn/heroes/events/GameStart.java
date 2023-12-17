package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import fr.worsewarn.cosmox.game.events.GameStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameStart implements Listener {

    private Main pl;

    public GameStart(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void GameStartEvent(GameStartEvent event) {

        pl.getManager().startGame(event.getMap());

    }
}
