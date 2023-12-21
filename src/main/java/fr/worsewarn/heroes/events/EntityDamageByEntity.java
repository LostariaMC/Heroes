package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.tools.utils.MathsUtils;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.heroes.manager.HPlayer;
import fr.worsewarn.heroes.manager.PlayerAttribute;
import jodd.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntity implements Listener {

    private Main pl;

    public EntityDamageByEntity(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        if(event.getEntity() instanceof Player player) {

            HPlayer hPlayer = pl.getPlayer(player);

            event.setDamage(event.getDamage() - (MathsUtils.getNumberByPercent(event.getDamage(), hPlayer.getAttributeValue(PlayerAttribute.ARMOR))));

            if(player.getHealth() - event.getFinalDamage() <= 0) {
                hPlayer.kill();
                event.setCancelled(true);
            }
        }

    }
}
