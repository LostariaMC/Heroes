package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.tools.utils.MathsUtils;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.scoreboard.CosmoxScoreboard;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.Phase;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.cosmox.tools.chat.Messages;
import fr.worsewarn.cosmox.tools.items.Items;
import fr.worsewarn.cosmox.tools.map.GameMap;
import fr.worsewarn.cosmox.tools.world.FireworkUtils;
import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import fr.worsewarn.heroes.entities.entities.PolarBear;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    private Main pl;
    private GameMap gameMap;
    private int difficulty;

    private List<UUID> pendingSpawns;
    private List<Entity> currentEntities;
    private List<HEntity> entities;

    private Villager villager;
    private List<Location> villagerTargetSpawns;
    private List<Location> playerTargetSpawns;

    public GameManager(Main pl) {
        this.pl = pl;
        this.pendingSpawns = new ArrayList<>();
        this.currentEntities = new ArrayList<>();

        this.entities = Arrays.asList(
                new PolarBear()
        );
    }

    public Location getMapCenter() {

        return gameMap.getLocation("spawn");
    }

    public void startGame(GameMap gameMap) {

        this.gameMap = gameMap;
        this.villagerTargetSpawns = gameMap.getLocations("villagerTargetSpawns");
        this.playerTargetSpawns = gameMap.getLocations("playerTargetSpawns");
        this.villager = gameMap.getWorld().spawn(gameMap.getLocation("villager"), Villager.class);

        villager.setAI(false);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        villager.setCollidable(true);
        villager.setProfession(pl.getAPI().getUtils().getRandomElement(Arrays.asList(Villager.Profession.values())));
        villager.setVillagerType(pl.getAPI().getUtils().getRandomElement(Arrays.asList(Villager.Type.values())));

        for(Player all : Bukkit.getOnlinePlayers()) {

            CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(all);
            HPlayer hPlayer = pl.getPlayer(all.getUniqueId());

            cosmoxPlayer.setScoreboard(getScoreboard(all));

            if(cosmoxPlayer.getTeam().equals(Team.NO_TEAM)) {

                cosmoxPlayer.setStatistic(GameVariables.GAMES_PLAYED, 1);
                hPlayer.spawn();


            } else {

                all.setGameMode(GameMode.SPECTATOR);
                all.teleport(gameMap.getLocation("spawn"));
            }
        }

        pl.registerListeners();
        pl.getTask().run();

    }

    public CosmoxScoreboard getScoreboard(Player p) {

        if(p == null) return null;

        CosmoxScoreboard cosmoxScoreboard = new CosmoxScoreboard(p);

        cosmoxScoreboard.updateTitle("§f§lHEROES§7");

        int i = -1;

        cosmoxScoreboard.updateLine(i+=1, "§a ");


        cosmoxScoreboard.updateLine(i+=1, "§e ");

        return cosmoxScoreboard;
    }

    public List<UUID> getPendingSpawns() {
        return pendingSpawns;
    }

    public void addPlayerToPendingSpawns(UUID uuid) {

        if(pendingSpawns.contains(uuid)) return;

        pendingSpawns.add(uuid);
    }

    public void respawnPendingPlayers() {

        pendingSpawns.forEach(all -> pl.getPlayer(all).spawn());
        pendingSpawns.clear();
    }

    public List<Location> getSpawns(TargetType targetType) {

        if(targetType.equals(TargetType.VILLAGER)) return villagerTargetSpawns;

        return playerTargetSpawns;
    }

    public void spawnMobs() {

        int playersCount = (int) Bukkit.getOnlinePlayers().stream().filter(all -> pl.getAPI().getPlayer(all).getTeam().equals(Team.NO_TEAM)).count();

        for(TargetType targetType : TargetType.values()) {

            for(Location spawn : getSpawns(targetType)) {

                for(HEntity entity : entities) {

                    int chance = entity.getSpawnPercent() * playersCount;

                    while(chance >= 0) {

                        if(MathsUtils.getRandomByPercent(chance)) {

                            currentEntities.add(entity.spawn(spawn).getBukkitEntity());
                        }

                        chance-=100;
                    }

                }

            }
        }
    }

    public List<Entity> getCurrentEntities() {
        return currentEntities;
    }

    public void death(Entity entity) {

        currentEntities.remove(entity);

        if(currentEntities.isEmpty()) {

            respawnPendingPlayers();
        }

    }

}
