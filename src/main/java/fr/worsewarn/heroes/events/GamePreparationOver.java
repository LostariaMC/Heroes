package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.game.events.GamePreparationOverEvent;
import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GamePreparationOver implements Listener {

    private Main pl;

    public GamePreparationOver(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void GamePreparationOverEvent(GamePreparationOverEvent event) {

        pl.getManager().spawnMobs();
    }
}
