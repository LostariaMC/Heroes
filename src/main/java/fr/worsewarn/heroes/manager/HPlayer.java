package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.cosmox.tools.items.ItemBuilder;
import fr.worsewarn.heroes.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class HPlayer {

    private Main pl;

    private UUID uuid;
    private CosmoxPlayer cosmoxPlayer;

    private int golds;

    private int sword;
    private int bow;
    private int heal;
    private int stick;
    private int crossbow;
    private int armor;

    public HPlayer(Main pl, UUID uuid) {
        this.uuid = uuid;
        this.cosmoxPlayer = pl.getAPI().getPlayer(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void spawn() {

        if(!cosmoxPlayer.getTeam().equals(Team.NO_TEAM)) return;

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        equip();
        player.setFallDistance(0);
        player.teleport(pl.getManager().getMapCenter());
        player.setGameMode(GameMode.ADVENTURE);

    }

    private void equip() {

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        PlayerInventory playerInventory = player.getInventory();

        playerInventory.clear();
        playerInventory.setHelmet(new ItemBuilder(Material.IRON_HELMET).addEnchant(Enchantment.BINDING_CURSE, 1).setGlow(false).setUnbreakable(true).build());
        playerInventory.setChestplate(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).addEnchant(Enchantment.BINDING_CURSE, 1).setGlow(false).setUnbreakable(true).build());
        playerInventory.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS).addEnchant(Enchantment.BINDING_CURSE, 1).setGlow(false).setUnbreakable(true).build());
        playerInventory.setBoots(new ItemBuilder(Material.IRON_BOOTS).addEnchant(Enchantment.BINDING_CURSE, 1).setGlow(false).setUnbreakable(true).build());

        cosmoxPlayer.getDefaultItemManager().setItemInventoryCustomSlot(new ItemBuilder(Material.IRON_SWORD).setUnbreakable(true).build(), "sword");
        cosmoxPlayer.getDefaultItemManager().setItemInventoryCustomSlot(new ItemBuilder(Material.BOW).addEnchant(Enchantment.ARROW_INFINITE, 1).setGlow(false).setUnbreakable(true).build(), "bow");
        cosmoxPlayer.getDefaultItemManager().setItemInventoryCustomSlot(new ItemBuilder(Material.ARROW).build(), "arrow");

    }

    public void kill() {

        pl.getManager().addPlayerToPendingSpawns(uuid);

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
    }

    public int getGolds() {
        return golds;
    }

    public int getSword() {
        return sword;
    }

    public int getBow() {
        return bow;
    }

    public int getHeal() {
        return heal;
    }

    public int getStick() {
        return stick;
    }

    public int getCrossbow() {
        return crossbow;
    }

    public int getArmor() {
        return armor;
    }

    /**
     *
     * Au démarrage, un mob est sélectionné pour apparaitre (même proba pour tous)
     * Chaque 3 paliers difficulté, un nouveau type de mob se débloque
     *
     * Quand un mob spawn, boucle for sur le nombre de jouers et chaque spawn et on regarde le taux de spawn
     *
     * full fer
     * épée fer
     * arc
     * infini
     *
     * gain d'or
     *
     *
     */

}
