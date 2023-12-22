package fr.worsewarn.heroes.events;

import fr.worsewarn.heroes.Main;
import fr.worsewarn.heroes.manager.HPlayer;
import fr.worsewarn.heroes.manager.PlayerAttribute;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;

public class PotionSplash implements Listener {

    private Main pl;

    public PotionSplash(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void PotionSplashEvent(PotionSplashEvent event) {

        if(event.getPotion().getShooter() instanceof Player player) {

            HPlayer hPlayer = pl.getPlayer(player);

            Iterator<LivingEntity> iterator = event.getAffectedEntities().iterator();
            while(iterator.hasNext()) {

                LivingEntity livingEntity = iterator.next();

                if(livingEntity.getType().equals(EntityType.PLAYER) || livingEntity.getType().equals(EntityType.VILLAGER)) {

                    if(hPlayer.getAttributeLevel(PlayerAttribute.HEAL) >= 10) {
                        event.setIntensity(livingEntity, 1);
                        Bukkit.getLogger().info("[DEBUG] Bonus heal applied");

                    }
                    continue;
                }

                event.setIntensity(livingEntity, 0);
            }
        }
    }
}
