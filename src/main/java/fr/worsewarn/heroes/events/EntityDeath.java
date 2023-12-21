package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

    private Main pl;

    public EntityDeath(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void EntityDeathEvent(EntityDeathEvent event) {

        event.setDroppedExp(0);
        event.getDrops().clear();
        pl.getManager().death(event.getEntity());
    }
}
