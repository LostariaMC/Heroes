package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.tools.utils.MathsUtils;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.manager.EntityAttribute;
import fr.worsewarn.heroes.manager.HPlayer;
import fr.worsewarn.heroes.manager.PlayerAttribute;
import jodd.util.MathUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityDamageByEntity implements Listener {

    private Main pl;

    public EntityDamageByEntity(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent event) {

        if(event.getEntity() instanceof Villager) {

            if(event.getDamager() instanceof Player || (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player)) {
                event.setCancelled(true);
            }
        }

        if(event.getDamager() instanceof Player player) {

            HPlayer hPlayer = pl.getPlayer(player);
            ItemStack item = player.getItemInUse();

            if(item != null) {

                if(item.getType().equals(Material.IRON_SWORD)) {

                    float sword = hPlayer.getAttributeValue(PlayerAttribute.SWORD);

                    double actualDamages = event.getDamage();
                    double modifiedDamages = actualDamages + MathsUtils.getNumberByPercent(event.getDamage(),  sword);
                    event.setDamage(modifiedDamages);

                    Bukkit.getLogger().info("[DEBUG] Sword bonus applied : " + actualDamages + " => " + modifiedDamages);
                }
            }
        }

        if(event.getDamager() instanceof Arrow arrow) {

            if(arrow.getShooter() instanceof Player player) {

                HPlayer hPlayer = pl.getPlayer(player);
                float bow = hPlayer.getAttributeValue(PlayerAttribute.BOW) + hPlayer.getAttributeLevel(PlayerAttribute.BOW) >= 10 ? 50 : 0;

                double actualDamages = event.getDamage();
                double modifiedDamages = actualDamages + MathsUtils.getNumberByPercent(event.getDamage(),  bow);
                event.setDamage(modifiedDamages);

                Bukkit.getLogger().info("[DEBUG] Bonus bow applied : " + actualDamages + " => " + modifiedDamages);

            }
        }

        if(event.getEntity() instanceof Player player) {

            HPlayer hPlayer = pl.getPlayer(player);

            double actualDamages = event.getDamage();
            double modifiedDamages = event.getDamage() - (MathsUtils.getNumberByPercent(event.getDamage(), hPlayer.getAttributeValue(PlayerAttribute.ARMOR)));

            event.setDamage(modifiedDamages);

            Bukkit.getLogger().info("[DEBUG] Bonus armor applied : " + actualDamages + " => " + modifiedDamages);

            if(player.getHealth() - event.getFinalDamage() <= 0) {
                hPlayer.kill();
                event.setCancelled(true);
            }
        }

        Entity damager = event.getDamager();
        HEntity hEntity = pl.getManager().getEntity(damager);
        if(hEntity != null) {

            double actualDamages = event.getDamage();
            double modifiedDamages = actualDamages + MathsUtils.getNumberByPercent(actualDamages, hEntity.getAttribute(EntityAttribute.STRENGTH));

            event.setDamage(modifiedDamages);
            Bukkit.getLogger().info("[DEBUG] Bonus strength (E) applied : " + actualDamages + " => " + modifiedDamages);

        }

    }
}
