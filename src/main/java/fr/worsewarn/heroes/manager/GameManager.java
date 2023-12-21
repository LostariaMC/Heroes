package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.api.languages.Language;
import fr.worsewarn.cosmox.api.languages.LanguageManager;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.Phase;
import fr.worsewarn.cosmox.tools.Utils;
import fr.worsewarn.cosmox.tools.chat.MessageBuilder;
import fr.worsewarn.cosmox.tools.items.ItemBuilder;
import fr.worsewarn.cosmox.tools.items.inventory.CosmoxInventory;
import fr.worsewarn.cosmox.tools.items.inventory.CosmoxItem;
import fr.worsewarn.cosmox.tools.utils.MathsUtils;
import fr.worsewarn.heroes.Main;
import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.scoreboard.CosmoxScoreboard;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.cosmox.tools.map.GameMap;
import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import fr.worsewarn.heroes.entities.entities.PolarBear;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
        this.difficulty = 0;

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

        gameMap.getWorld().setTime(Long.valueOf(gameMap.getStr("worldTime")));
        gameMap.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, gameMap.getStr("worldTime").equals("1"));

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
        HPlayer hPlayer = WrappedPlayer.of(p).to(HPlayer.class);

        cosmoxScoreboard.updateTitle(ScoreboardFormat.TITLE.formatted(pl.getTask().getFormattedTimer()));

        cosmoxScoreboard.updateLine(0, "§a ");
        cosmoxScoreboard.updateLine(1, new MessageBuilder(ScoreboardFormat.INFO, true).toString(p));
        cosmoxScoreboard.updateLine(2, new MessageBuilder(ScoreboardFormat.INFO_ROUND, true).formatted(difficulty).toString(p));
        cosmoxScoreboard.updateLine(3, new MessageBuilder(ScoreboardFormat.INFO_ENTITY_COUNT, true).formatted(currentEntities.size()).toString(p));
        cosmoxScoreboard.updateLine(4, "§b ");
        cosmoxScoreboard.updateLine(5, new MessageBuilder(ScoreboardFormat.GOLDS, true).formatted(hPlayer.getGolds()).toString(p));
        cosmoxScoreboard.updateLine(6, "§c ");
        cosmoxScoreboard.updateLine(7, "§d ");

        return cosmoxScoreboard;
    }

    public List<UUID> getPendingSpawns() {
        return pendingSpawns;
    }

    public void addPlayerToPendingSpawns(UUID uuid) {

        if(pendingSpawns.contains(uuid)) return;

        pendingSpawns.add(uuid);

        if(pendingSpawns.size() >= pl.getAPI().getPlayers().stream().filter(all -> all.getTeam().equals(Team.RANDOM)).count()) {

            end(DefeatReason.PLAYERS_DEATH);
        }
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

                    if(entity.isLocked()) continue;

                    int chance = entity.getAttribute(EntityAttribute.SPAWN_PERCENT) * playersCount;

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

    public HEntity getEntity(Entity entity) {

        EntityType entityType = entity.getType();

        return entities.stream().filter(all -> all.getEntityType().equals(entityType)).findFirst().orElseGet(null);

    }

    public void end(DefeatReason reason) {

        if(!pl.getAPI().getManager().getPhase().equals(Phase.END)) {

            pl.getAPI().getManager().setPhase(Phase.END);
            new MessageBuilder(pl.getGame().getPrefix() + "@lang/heroes.game_end_" + reason.name().toLowerCase(), true).broadcast();
        }
    }

    public void death(Entity entity) {

        if(entity.getType().equals(EntityType.VILLAGER) && villager != null && villager.getEntityId() == entity.getEntityId()) {

            end(DefeatReason.VILLAGER_DEATH);
            return;
        }

        if(currentEntities.remove(entity)) {

            pl.getPlayers().forEach(all -> {

                CosmoxPlayer cosmoxPlayer = WrappedPlayer.of(all).toCosmox();
                HEntity entity1 = getEntity(entity);

                if(entity1 != null) {

                    all.changeGolds(Math.round(100F / (float)entity1.getAttribute(EntityAttribute.SPAWN_PERCENT)));
                    cosmoxPlayer.getScoreboard().updateLine(3, new MessageBuilder(ScoreboardFormat.INFO_ENTITY_COUNT, true).formatted(currentEntities.size()).toString(cosmoxPlayer.getRedisPlayer().getLanguage()));

                }

            });
        }

        if(currentEntities.isEmpty()) {

            int next_round = 8;

            respawnPendingPlayers();
            new MessageBuilder("@lang/heroes.game_round_ended/", true).formatted(next_round).broadcast();

            new BukkitRunnable() {

                @Override
                public void run() {

                    difficulty++;
                    startRound();
                }
            }.runTaskLater(pl, 20*8);
        }

    }

    private void startRound() {

        Bukkit.getOnlinePlayers().forEach(all -> pl.getAPI().getPlayer(all).getScoreboard().updateLine(2, new MessageBuilder(ScoreboardFormat.INFO_ROUND, true).formatted(difficulty).toString(all)));

        upgradeEntity();
        if(difficulty%3 == 0) unlockEntity();
        spawnMobs();


    }

    private void unlockEntity() {

        HEntity entity = pl.getAPI().getUtils().getRandomElement(entities.stream().filter(HEntity::isLocked).toList());

        if(entity != null) {

            entity.unlock();
            new MessageBuilder(pl.getGame().getPrefix() + "@lang/heroes.entity_unlocked", true).formatted(entity.getName()).broadcast();
        }

    }

    private void upgradeEntity() {

        HEntity entity = pl.getAPI().getUtils().getRandomElement(entities.stream().filter(HEntity::isUnlocked).toList());

        if(entity != null) {

            EntityAttribute attribute = pl.getAPI().getUtils().getRandomElement(Arrays.stream(EntityAttribute.values()).toList());
            entity.increaseAttribute(attribute);

            new MessageBuilder(pl.getGame().getPrefix() + "@lang/heroes.entity_attribute_increased", true).formatted(entity.getName(), entity.getAttribute(attribute)-attribute.getModifier(), entity.getAttribute(attribute)).broadcast();
        }
    }

    public void openUpgrades(Player player) {

        HPlayer hPlayer = pl.getPlayer(player);
        CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(player);
        Language language = cosmoxPlayer.getRedisPlayer().getLanguage();

        CosmoxInventory cosmoxInventory = new CosmoxInventory(pl.getAPI(), player, LanguageManager.getInstance().translate("heroes.inventory_upgrades_title", language), 9*6);

        int i = 0;
        for(PlayerAttribute playerAttribute : PlayerAttribute.values()) {

            int actualLevel = hPlayer.getAttributeLevel(playerAttribute);
            boolean maxLevel = actualLevel >= 15;
            int cost = 10 + actualLevel;
            boolean hasEnoughGolds = hPlayer.getGolds() >= cost;

            cosmoxInventory.addCosmoxItem(new CosmoxItem(

                    new ItemBuilder(playerAttribute.getMaterial())
                            .setDisplayName("§f" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_" + playerAttribute.getID(), language))
                            .setLore(" ")
                            .addLore(maxLevel ? "§c" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_max_level", language) : hasEnoughGolds ? "§a" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_buy", language) : "§c" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_no_golds", language))

                    , i).addClickAction((player1, clickType, inventoryAction) -> {

                if(actualLevel >= 15) {

                    player.sendMessage(pl.getGame().getPrefix() + "§c" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_max_level_message", language));
                    return;
                }

                if(!hasEnoughGolds) {

                    player.sendMessage(pl.getGame().getPrefix() + "§c" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_no_golds_message", language));
                    return;
                }

                player.sendMessage(pl.getGame().getPrefix() + "§a" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_success", language));
                hPlayer.upgrade(playerAttribute);
                openUpgrades(player);


            }));

            i++;
        }
    }

}
