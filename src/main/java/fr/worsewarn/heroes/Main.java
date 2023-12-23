package fr.worsewarn.heroes;

import fr.worsewarn.cosmox.API;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.api.statistics.Statistic;
import fr.worsewarn.cosmox.game.Game;
import fr.worsewarn.cosmox.game.GameVariables;
import fr.worsewarn.cosmox.game.configuration.Parameter;
import fr.worsewarn.cosmox.tools.items.DefaultItemSlot;
import fr.worsewarn.cosmox.tools.items.ItemBuilder;
import fr.worsewarn.cosmox.tools.map.MapLocation;
import fr.worsewarn.cosmox.tools.map.MapLocationType;
import fr.worsewarn.cosmox.tools.map.MapTemplate;
import fr.worsewarn.cosmox.tools.map.MapType;
import fr.worsewarn.heroes.events.*;
import fr.worsewarn.heroes.manager.HPlayer;
import fr.worsewarn.heroes.manager.GameManager;
import fr.worsewarn.heroes.manager.GameTask;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Main extends JavaPlugin {

    private API api;
    private Game game;
    private GameManager manager;
    private GameTask task;
    private HashMap<UUID, HPlayer> players;

    @Override
    public void onEnable() {

        api = API.instance();

        game = new Game("heroes", "Heroes", ChatColor.of("#51826d"), Material.CHAINMAIL_CHESTPLATE, Arrays.asList(fr.worsewarn.cosmox.game.teams.Team.NO_TEAM), 3, false, false,

                /*Statistiques*/
                Arrays.asList(
                        new Statistic("@lang/main.statistics_time_played/", GameVariables.TIME_PLAYED, true),
                        new Statistic("@lang/main.statistics_games_played/", GameVariables.GAMES_PLAYED),
                        new Statistic("@lang/heroes.statistics_round_max/", GameVariables.CUSTOM_1).uploadOnlyIfSuperior()

                ),
                /*Achievements*/
                Arrays.asList(),

                /*Description*/
                Arrays.asList(
                        " ",
                        "@lang/heroes.game_description/"

                ),
                /*MapTemplate*/
                Arrays.asList(

                        new MapTemplate(MapType.NONE,
                                Arrays.asList(
                                        new MapLocation("authors", MapLocationType.STRING),
                                        new MapLocation("name", MapLocationType.STRING),
                                        new MapLocation("map", MapLocationType.CUBOID),

                                        new MapLocation("worldTime", MapLocationType.STRING),
                                        new MapLocation("dayCycle", MapLocationType.STRING), //boolean 0 ou 1

                                        new MapLocation("spawn", MapLocationType.LOCATION),
                                        new MapLocation("villager", MapLocationType.LOCATION),
                                        new MapLocation("playerTargetSpawns", MapLocationType.LIST_LOCATION),
                                        new MapLocation("villagerTargetSpawns", MapLocationType.LIST_LOCATION)

                                ))

                ))

                .addDefaultItem(new DefaultItemSlot("heroes_sword", new ItemBuilder(Material.IRON_SWORD).setDisplayName("§f@lang/heroes.item_sword/").build(), 0))
                .addDefaultItem(new DefaultItemSlot("heroes_bow", new ItemBuilder(Material.BOW).setDisplayName("§f@lang/heroes.item_bow/").build(), 1))
                .addDefaultItem(new DefaultItemSlot("heroes_arrow", new ItemBuilder(Material.ARROW).setDisplayName("§f@lang/heroes.item_arrows/").build(), 35))
                .addDefaultItem(new DefaultItemSlot("heroes_heal", new ItemBuilder(Material.SPLASH_POTION).setPotionColor(PotionEffectType.HEAL.getColor()).setDisplayName("§f@lang/heroes.item_potion/").build(), 2))

                .setGameAuthor("Worsewarn")
                .setTags("@lang/heroes.game_tags/")
                .setScoreboardOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
                .setPreparationTime(15)
                .activeJoinInGame()
                ;

        api.registerNewGame(game);

        manager = new GameManager(this);
        task = new GameTask(this);
        players = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new GameStart(this), this);

        WrappedPlayer.registerType(new WrappedPlayer.PlayerWrapper<>(HPlayer.class) {
            @Override
            public HPlayer unWrap(java.util.UUID uuid) {
                return getPlayer(uuid);
            }

            @Override
            public java.util.UUID wrap(HPlayer hPlayer) {
                return hPlayer.getUUID();
            }
        });

    }

    public void registerListeners() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new EntityDamage(this), this);
        pluginManager.registerEvents(new EntityDamageByEntity(this), this);
        pluginManager.registerEvents(new EntityDeath(this), this);
        pluginManager.registerEvents(new InventoryClick(this), this);
        pluginManager.registerEvents(new GamePreparationOver(this), this);
        pluginManager.registerEvents(new PlayerDeath(this), this);
        pluginManager.registerEvents(new PlayerChangeLanguage(this), this);
        pluginManager.registerEvents(new PlayerDropItem(this), this);
        pluginManager.registerEvents(new PlayerInteract(this), this);
        pluginManager.registerEvents(new PlayerInteractEntity(this), this);
        pluginManager.registerEvents(new PlayerJoinGame(this), this);
        pluginManager.registerEvents(new PlayerJoinTeam(this), this);
        pluginManager.registerEvents(new PlayerMove(this), this);
        pluginManager.registerEvents(new PlayerQuit(this), this);
        pluginManager.registerEvents(new PotionSplash(this), this);
        pluginManager.registerEvents(new ProjectileLaunch(this), this);

    }

    public API getAPI() { return api; }

    public GameManager getManager() { return manager; }

    public GameTask getTask() { return task;}

    public Game getGame() { return game; }

    public HPlayer getPlayer(Player player) { return getPlayer(player.getUniqueId()); }

    public HPlayer getPlayer(UUID uuid) {

        if(!players.containsKey(uuid)) players.put(uuid, new HPlayer(this, uuid));

        return players.get(uuid);
    }

    public Collection<HPlayer> getPlayers() { return players.values(); }
}
