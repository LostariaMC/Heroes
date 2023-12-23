package fr.worsewarn.heroes.events;

import fr.worsewarn.cosmox.api.languages.LanguageManager;
import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.tools.chat.MessageBuilder;
import fr.worsewarn.cosmox.tools.items.ItemBuilder;
import fr.worsewarn.cosmox.tools.utils.Cooldown;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.heroes.manager.HPlayer;
import fr.worsewarn.heroes.manager.PlayerAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ProjectileLaunch implements Listener {

    private Main pl;

    public ProjectileLaunch(Main pl) {
        this.pl = pl;
    }

    @EventHandler
    public void ProjectileLaunchEvent(ProjectileLaunchEvent event) {

        if(event.getEntity() instanceof ThrownPotion potion && potion.getShooter() instanceof Player player) {

            HPlayer hPlayer = pl.getPlayer(player);
            CosmoxPlayer cosmoxPlayer = WrappedPlayer.of(hPlayer).toCosmox();

            float cooldown = 20 - hPlayer.getAttributeValue(PlayerAttribute.HEAL);

            if(!Cooldown.isInCooldown(hPlayer.getUUID(), "heroes.heal_potion") && potion.getEffects().stream().anyMatch(all -> all.getType().equals(PotionEffectType.HEAL))) {

                new Cooldown(hPlayer.getUUID(), "heroes.heal_potion", cooldown).start();
                player.setCooldown(Material.SPLASH_POTION, Math.round(cooldown*20));

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        cosmoxPlayer.getDefaultItemManager().setItemInventoryCustomSlot(new ItemBuilder(Material.SPLASH_POTION).setPotionColor(PotionEffectType.INVISIBILITY.getColor()).setDisplayName(new MessageBuilder("§f@lang/heroes.item_heal_potion/", true).toString(player)).build(), "heroes_heal", true);

                    }
                }.runTaskLater(pl, 2L);

                BukkitTask task = hPlayer.getTasks().getOrDefault(PlayerAttribute.HEAL, null);

                if(task != null) task.cancel();

                hPlayer.getTasks().put(PlayerAttribute.HEAL, new BukkitRunnable() {

                    @Override
                    public void run() {

                        player.getInventory().remove(Material.SPLASH_POTION);
                        cosmoxPlayer.getDefaultItemManager().setItemInventoryCustomSlot(new ItemBuilder(Material.SPLASH_POTION).setPotionColor(PotionEffectType.HEAL.getColor()).setPotionEffect(new PotionEffect(PotionEffectType.HEAL, 0, 0)).setDisplayName(new MessageBuilder("§f@lang/heroes.item_heal_potion/", true).toString(player)).build(), "heroes_heal", true);
                        player.playSound(player.getLocation(), Sound.BLOCK_BREWING_STAND_BREW, SoundCategory.AMBIENT, 0.15F, 1.2F);

                    }
                }.runTaskLater(pl, Math.round(cooldown*20)));

            } else {
                event.setCancelled(true);

            }
        }
    }
}
