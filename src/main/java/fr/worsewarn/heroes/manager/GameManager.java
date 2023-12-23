package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.api.languages.Language;
import fr.worsewarn.cosmox.api.languages.LanguageManager;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.Phase;
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
import fr.worsewarn.heroes.entities.entities.CaveSpider;
import fr.worsewarn.heroes.entities.entities.Endermite;
import fr.worsewarn.heroes.entities.entities.Husk;
import fr.worsewarn.heroes.entities.entities.PolarBear;
import fr.worsewarn.heroes.entities.entities.Silverfish;
import fr.worsewarn.heroes.entities.entities.Spider;
import fr.worsewarn.heroes.entities.entities.Vex;
import fr.worsewarn.heroes.entities.entities.Zombie;
import fr.worsewarn.heroes.entities.entities.ZombieVillager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Stream;

public class GameManager {

    private Main pl;
    private GameMap gameMap;
    private int difficulty;

    private List<UUID> pendingSpawns;
    private List<Entity> currentEntities;
    private List<HEntity> entities;

    private String villagerName;
    private Villager villager;
    private List<Location> villagerTargetSpawns;
    private List<Location> playerTargetSpawns;

    private HashMap<Language, BossBar> bossBars;

    public GameManager(Main pl) {
        this.pl = pl;
        this.pendingSpawns = new ArrayList<>();
        this.currentEntities = new ArrayList<>();
        this.difficulty = 0;
        this.bossBars = new HashMap<>();

        this.entities = Arrays.asList(
                new CaveSpider(),
                new Endermite(),
                new Husk(),
                new PolarBear(),
                new Silverfish(),
                new Spider(),
                new Vex(),
                new Zombie(),
                new ZombieVillager()
                //new Slime(),
                //new Hoglin()
        );
    }

    public Location getMapCenter() {

        return gameMap.getLocation("spawn");
    }

    public Villager getVillager() {
        return villager;
    }

    public void startGame(GameMap gameMap) {

        this.gameMap = gameMap;
        this.villagerTargetSpawns = gameMap.getLocations("villagerTargetSpawns");
        this.playerTargetSpawns = gameMap.getLocations("playerTargetSpawns");
        this.villagerName = pl.getAPI().getUtils().getRandomElement(Arrays.asList(
                "Bob Blagueur",
                "Rigobert Farceur",
                "Léa Rirette",
                "Gaston Gag",
                "Camille Fou-rire",
                "Marcel Amuzo",
                "Sophie Plaisante",
                "Félix Comique",
                "Colette Sourireau",
                "Romain Rigolo"
        ));
        this.villager = gameMap.getWorld().spawn(gameMap.getLocation("villager"), Villager.class);

        villager.setAI(false);
        villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(50);
        villager.setHealth(villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        villager.setCollidable(true);
        villager.setProfession(pl.getAPI().getUtils().getRandomElement(Arrays.asList(Villager.Profession.values())));
        villager.setVillagerType(pl.getAPI().getUtils().getRandomElement(Arrays.asList(Villager.Type.values())));
        villager.setCustomName(villagerName);
        villager.setCustomNameVisible(true);

        Stream.of(Language.values()).forEach(all -> bossBars.put(all, Bukkit.createBossBar(new MessageBuilder("@lang/heroes.bossbar_villager/", true).formatted(villagerName).toString(all), BarColor.GREEN, BarStyle.SOLID)));

        gameMap.getWorld().setTime(Long.valueOf(gameMap.getStr("worldTime")));
        gameMap.getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, gameMap.getStr("worldTime").equals("1"));
        pl.getTask().run();

        for(Player all : Bukkit.getOnlinePlayers()) {

            CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(all);
            HPlayer hPlayer = pl.getPlayer(all.getUniqueId());

            cosmoxPlayer.setScoreboard(getScoreboard(all));
            addPlayerBossBar(all, cosmoxPlayer.getRedisPlayer().getLanguage());

            if(cosmoxPlayer.getTeam().equals(Team.NO_TEAM)) {

                cosmoxPlayer.setStatistic(GameVariables.GAMES_PLAYED, 1);
                hPlayer.spawn();


            } else {

                all.setGameMode(GameMode.SPECTATOR);
                all.teleport(gameMap.getLocation("spawn"));
            }
        }

        new MessageBuilder(pl.getGame().getPrefix() + "§f@lang/heroes.game_description_ingame/", true).broadcast();
        pl.registerListeners();
        pl.getAPI().getManager().setPhase(Phase.GAME);

        new BukkitRunnable() {

            @Override
            public void run() {

                unlockEntity();
            }
        }.runTaskLater(pl, 20*2L);

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

        if(pendingSpawns.contains(uuid) || pl.getAPI().getManager().getPhase().equals(Phase.END)) return;

        pendingSpawns.add(uuid);

        if(pendingSpawns.size() >= pl.getAPI().getPlayers().stream().filter(all -> all.getTeam().equals(Team.NO_TEAM)).count()) {

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

                    if(!entity.getTargetType().equals(targetType)) continue;
                    if(entity.isLocked()) continue;

                    int chance = Math.round(entity.getAttribute(EntityAttribute.SPAWN_PERCENT) * (playersCount * 0.5F));

                    while(chance >= 0) {

                        if(MathsUtils.getRandomByPercent(chance)) {

                            Entity entity1 = entity.spawn(entity instanceof Flying ? spawn.clone().add(0, 2, 0) : spawn).getBukkitEntity();
                            LivingEntity livingEntity = ((LivingEntity) entity1);
                            livingEntity.setRemoveWhenFarAway(false);
                            livingEntity.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(255);
                            livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(MathsUtils.getNumberByPercent(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), 100 + entity.getAttribute(EntityAttribute.VITALITY)));
                            livingEntity.setHealth(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                            livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(MathsUtils.getNumberByPercent(livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue(), 100 + entity.getAttribute(EntityAttribute.AGILITY)));

                            if (entity.getTargetType().equals(TargetType.VILLAGER)) {

                                ((Mob)livingEntity).setTarget(villager);
                            } else {

                                Player p = null;
                                double d = 0;
                                for(Player all : Bukkit.getOnlinePlayers()) {

                                    if(all.getGameMode().equals(GameMode.SPECTATOR)) continue;


                                    double nd = all.getLocation().distance(entity1.getLocation());
                                    if(p == null || nd < d) {

                                        p = all;
                                        d = nd;
                                    }

                                }

                                if(p != null) ((Mob)livingEntity).setTarget(p);
                            }

                            currentEntities.add(entity1);
                        }

                        chance-=100;
                    }

                }

            }
        }

        pl.getPlayers().forEach(all -> {

            CosmoxPlayer cosmoxPlayer = WrappedPlayer.of(all).toCosmox();

            cosmoxPlayer.getScoreboard().updateLine(3, new MessageBuilder(ScoreboardFormat.INFO_ENTITY_COUNT, true).formatted(currentEntities.size()).toString(cosmoxPlayer.getRedisPlayer().getLanguage()));


        });

        checkRoundStatus();
    }

    public List<Entity> getCurrentEntities() {
        return currentEntities;
    }

    public HEntity getEntity(Entity entity) {

        EntityType entityType = entity.getType();

        return entities.stream().filter(all -> all.getEntityType().equals(entityType)).findFirst().orElse(null);

    }

    public void end(DefeatReason reason) {

        if(!pl.getAPI().getManager().getPhase().equals(Phase.END)) {

            pl.getAPI().getManager().setPhase(Phase.END);
            new MessageBuilder(pl.getGame().getPrefix() + ChatColor.RED + "@lang/heroes.game_end_" + reason.name().toLowerCase() + "/", true).broadcast();
        }

        Bukkit.getOnlinePlayers().forEach(all -> {

            HPlayer hPlayer = pl.getPlayer(all);

            all.setGameMode(GameMode.SPECTATOR);
            bossBars.values().forEach(BossBar::removeAll);
        });

        pl.getTask().cancel();
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

        checkRoundStatus();

    }

    private void checkRoundStatus() {

        if(currentEntities.isEmpty()) {

            int next_round = 8;

            respawnPendingPlayers();
            villager.setHealth(villager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
            new MessageBuilder(pl.getGame().getPrefix() + ChatColor.of("#74cc8c") + "@lang/heroes.game_round_ended/", true).formatted(next_round).broadcast();
            Bukkit.getOnlinePlayers().forEach(all -> all.playSound(all.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.AMBIENT, 1, 1.2F));

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

        upgradeEntity();
        if(difficulty%3 == 0) unlockEntity();
        spawnMobs();

        Bukkit.getOnlinePlayers().forEach(all -> {

            Language lang = WrappedPlayer.of(all).toCosmox().getRedisPlayer().getLanguage();

            pl.getAPI().getPlayer(all).getScoreboard().updateLine(2, new MessageBuilder(ScoreboardFormat.INFO_ROUND, true).formatted(difficulty).toString(all));
            all.playSound(all.getLocation(), Sound.EVENT_RAID_HORN, SoundCategory.AMBIENT, 0.1F, 1.3F);
            all.sendTitle(ChatColor.of("#68829c") + LanguageManager.getInstance().translate("heroes.title_start_round", lang).formatted(difficulty), LanguageManager.getInstance().translate("heroes.subtitle_start_round", lang).formatted(currentEntities.size()), 8, 30, 8);
        });


    }

    private void unlockEntity() {

        HEntity entity = pl.getAPI().getUtils().getRandomElement(entities.stream().filter(HEntity::isLocked).toList());

        if(entity != null) {

            entity.unlock();
            Bukkit.getOnlinePlayers().forEach(all -> {

                all.sendMessage(" ");
                new MessageBuilder(new MessageBuilder(pl.getGame().getPrefix() + ChatColor.of("#c78b6f") + "@lang/heroes.entity_unlocked/", true).formatted(ChatColor.of("#c4b6af") + "" + ChatColor.BOLD + entity.getName() + ChatColor.of("#c78b6f")).toString(WrappedPlayer.of(all).toCosmox().getRedisPlayer().getLanguage()), true).sendMessage(all);
                all.sendMessage(" ");
                all.playSound(all.getLocation(), Sound.ENTITY_EVOKER_FANGS_ATTACK, SoundCategory.AMBIENT, 1, 0.47F);
            });
        }

    }

    private void upgradeEntity() {

        HEntity entity = pl.getAPI().getUtils().getRandomElement(entities.stream().filter(HEntity::isUnlocked).toList());

        if(entity != null) {

            EntityAttribute attribute = pl.getAPI().getUtils().getRandomElement(Arrays.stream(EntityAttribute.values()).toList());
            entity.increaseAttribute(attribute);

            Bukkit.getOnlinePlayers().forEach(all -> {

                Language language = WrappedPlayer.of(all).toCosmox().getRedisPlayer().getLanguage();
                String entityName = new MessageBuilder(entity.getName(), true).toString(language);
                String attributeName = new MessageBuilder("@lang/" + attribute.getName() + "/", true).toString(language);
                new MessageBuilder(pl.getGame().getPrefix() + ChatColor.of("#5e69a6") + "@lang/heroes.entity_attribute_increased/", true).formatted(ChatColor.of("#7e88bf") + "" + ChatColor.BOLD + entityName + ChatColor.of("#b1b4c7"), attributeName, entity.getAttribute(attribute)-attribute.getModifier(), entity.getAttribute(attribute)).sendMessage(all);

                //all.playSound(all.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.AMBIENT, 0.15F, 1.75F);
            });
        }
    }

    public void openUpgrades(Player player) {

        HPlayer hPlayer = pl.getPlayer(player);
        CosmoxPlayer cosmoxPlayer = pl.getAPI().getPlayer(player);
        Language language = cosmoxPlayer.getRedisPlayer().getLanguage();

        CosmoxInventory cosmoxInventory = new CosmoxInventory(pl.getAPI(), player, LanguageManager.getInstance().translate("heroes.inventory_upgrades_title", language), 9*1);

        int i = 0;
        for(PlayerAttribute playerAttribute : PlayerAttribute.values()) {

            int actualLevel = hPlayer.getAttributeLevel(playerAttribute);
            float actualValue = hPlayer.getAttributeValue(playerAttribute);
            float nextValue = hPlayer.getAttributeValue(playerAttribute, actualLevel+1);
            boolean maxLevel = actualLevel >= 15;
            int cost = 10 + actualLevel;
            boolean hasEnoughGolds = hPlayer.getGolds() >= cost;

            cosmoxInventory.addCosmoxItem(new CosmoxItem(

                    new ItemBuilder(playerAttribute.getMaterial())
                            .setDisplayName("§f" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_" + playerAttribute.getID(), language) + " " + String.valueOf(actualLevel))
                            .setLore(" ")
                            .addLore("§7§o" + new MessageBuilder(playerAttribute.getName(), true).toString(player))
                            .addLore(" ")
                            .addLore(maxLevel ? " " : " §7" + actualValue + playerAttribute.getSuffix() + " ➡ " + nextValue + playerAttribute.getSuffix() + " §f(" + cost + " ⛃)")
                            .addLore(" ")
                            .addLore(maxLevel ? "§c" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_max_level", language) : hasEnoughGolds ? "§a" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_buy", language) : "§c" + LanguageManager.getInstance().translate("heroes.inventory_upgrades_no_golds", language))
                            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                            .addItemFlag(ItemFlag.HIDE_ENCHANTS)

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
                hPlayer.changeGolds(-cost);
                openUpgrades(player);


            }));

            i++;
        }

        player.openInventory(cosmoxInventory.getInventory());
    }

    public void addPlayerBossBar(Player player, Language language) {

        if(!pl.getAPI().getManager().getPhase().equals(Phase.END)) {

            bossBars.get(language).addPlayer(player);
        }

    }

    public void removePlayerBossBar(Player player) {

        bossBars.values().forEach(all -> all.removePlayer(player));

    }

    public Collection<BossBar> getBossBars() {

        return bossBars.values();
    }
}
