package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.api.languages.Language;
import fr.worsewarn.cosmox.tools.chat.MessageBuilder;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.scoreboard.CosmoxScoreboard;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.Phase;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.cosmox.tools.utils.MathsUtils;
import jodd.util.MathUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class GameTask {

    private Main pl;
    private int duration;
    private BukkitTask bukkitTask;

    public GameTask(Main pl) {
        this.pl = pl;
        this.duration = 0;

    }

    public void run() {

        if(bukkitTask != null) return;

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

                duration++;

                for(Player all : Bukkit.getOnlinePlayers()) {

                    HPlayer hPlayer = pl.getPlayer(all);
                    CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(all);
                    CosmoxScoreboard cosmoxScoreboard = cosmoxPlayer.getScoreboard();

                    cosmoxScoreboard.updateTitle(ScoreboardFormat.TITLE.formatted(getFormattedTimer()));

                    if(!cosmoxPlayer.getTeam().equals(Team.SPEC)) {

                        cosmoxPlayer.addStatistic(GameVariables.TIME_PLAYED, 1);
                    }

                    if(pl.getManager().getPendingSpawns().contains(all.getUniqueId())) all.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(new MessageBuilder("§c@lang/heroes.game_player_death_waiting/", true).toString(cosmoxPlayer.getRedisPlayer().getLanguage())));
                }

                double villagerHealth = pl.getManager().getVillager().getHealth();
                double villagerMaxHealth = pl.getManager().getVillager().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                double progress = villagerHealth / (float)villagerMaxHealth;
                BarColor barColor = progress > 0.66 ? BarColor.GREEN : progress > 0.33 ? BarColor.YELLOW : BarColor.RED;
                for(BossBar bossBar : pl.getManager().getBossBars()) {

                    bossBar.setProgress(progress);
                    bossBar.setColor(barColor);
                }
            }
        }.runTaskTimer(pl, 20, 20);
    }

    public void cancel() {

        if(bukkitTask == null) return;

        bukkitTask.cancel();
        bukkitTask = null;
    }

    public String getFormattedTimer() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        String min = (minutes < 10 ? "0" : "") + minutes;
        String sec = (seconds < 10 ? "0" : "") + seconds;

        return min + ":" + sec;
    }

    public int getDuration() {
        return duration;
    }

}
