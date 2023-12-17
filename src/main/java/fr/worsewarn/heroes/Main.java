package fr.worsewarn.heroes;

import fr.worsewarn.cosmox.API;
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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
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
                        new Statistic("Temps de jeu", GameVariables.TIME_PLAYED, true),
                        new Statistic("Parties jouées", GameVariables.GAMES_PLAYED),
                        new Statistic("Victoires", GameVariables.WIN)

                ),
                /*Achievements*/
                Arrays.asList(),

                /*Description*/
                Arrays.asList(
                        " ",
                        "§7Défendez votre villageois au",
                        "§7prix de votre vie"
                ),
                /*MapTemplate*/
                Arrays.asList(

                        new MapTemplate(MapType.NONE,
                                Arrays.asList(
                                        new MapLocation("authors", MapLocationType.STRING),
                                        new MapLocation("name", MapLocationType.STRING),
                                        new MapLocation("map", MapLocationType.CUBOID),

                                        new MapLocation("spawn", MapLocationType.LOCATION),
                                        new MapLocation("villager", MapLocationType.LOCATION),
                                        new MapLocation("playerTargetSpawns", MapLocationType.LIST_LOCATION),
                                        new MapLocation("villagerTargetSpawns", MapLocationType.LIST_LOCATION)

                                ))

                ))

                .addDefaultItem(new DefaultItemSlot("sword", new ItemBuilder(Material.IRON_SWORD).setDisplayName("§fÉpée").build(), 0))
                .addDefaultItem(new DefaultItemSlot("bow", new ItemBuilder(Material.BOW).setDisplayName("§fArc").build(), 1))
                .addDefaultItem(new DefaultItemSlot("arrow", new ItemBuilder(Material.ARROW).setDisplayName("§fFlèches").build(), 35))

                .setGameAuthor("Worsewarn")
                .setTags("Coopération, Creeper Attack")
                .setScoreboardOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)

                ;

        api.registerNewGame(game);

        manager = new GameManager(this);
        task = new GameTask(this);
        players = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new GameStart(this), this);

    }

    public void registerListeners() {

        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new EntityDamage(this), this);
        pluginManager.registerEvents(new EntityDamageByEntity(this), this);
        pluginManager.registerEvents(new InventoryClick(this), this);
        pluginManager.registerEvents(new PlayerDropItem(this), this);
        pluginManager.registerEvents(new PlayerInteract(this), this);
        pluginManager.registerEvents(new PlayerJoinGame(this), this);
        pluginManager.registerEvents(new PlayerJoinTeam(this), this);
        pluginManager.registerEvents(new PlayerMove(this), this);
        pluginManager.registerEvents(new PlayerQuit(this), this);

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
