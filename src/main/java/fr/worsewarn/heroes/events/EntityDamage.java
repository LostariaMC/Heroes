package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamage implements Listener {

    private Main pl;

    public EntityDamage(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void EntityDamageEvent(EntityDamageEvent event) {



    }
}
