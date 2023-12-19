package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.cosmox.tools.items.ItemBuilder;
import fr.worsewarn.heroes.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.UUID;

public class HPlayer {

    private Main pl;

    private UUID uuid;
    private CosmoxPlayer cosmoxPlayer;

    private int golds;

    private HashMap<PlayerAttribute, Integer> attributes;

    public HPlayer(Main pl, UUID uuid) {
        this.uuid = uuid;
        this.cosmoxPlayer = pl.getAPI().getPlayer(uuid);
        this.attributes = new HashMap<>();

        for(PlayerAttribute attribute : PlayerAttribute.values()) {
            attributes.put(attribute, 0);
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public void spawn() {

        if(!cosmoxPlayer.getTeam().equals(Team.NO_TEAM)) return;

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        equip();
        updateExtraAttributes();
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

    public void addGolds(int amount) {

        this.golds+=amount;

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
    }

    public int getLevel(PlayerAttribute attribute) {

        return attributes.get(attribute);
    }

    public void upgrade(PlayerAttribute attribute) {

        int currentLevel = getLevel(attribute);

        updateExtraAttributes();

        attributes.put(attribute, currentLevel+1);
    }

    public void updateExtraAttributes() {

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        PlayerInventory playerInventory = player.getInventory();

        if(getLevel(PlayerAttribute.SWORD) >= 10) {

            //Add enchantment on sword
        }

        if(getLevel(PlayerAttribute.CROSSBOW) >= 10) {

            //Add enchantment on crossbow
        }

        if(getLevel(PlayerAttribute.ARMOR) >= 10) {

            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        }



    }

}
