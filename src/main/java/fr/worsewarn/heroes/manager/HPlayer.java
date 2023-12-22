package fr.worsewarn.heroes.manager;

import fr.worsewarn.cosmox.api.players.CosmoxPlayer;
import fr.worsewarn.cosmox.api.players.WrappedPlayer;
import fr.worsewarn.cosmox.game.teams.Team;
import fr.worsewarn.cosmox.tools.Utils;
import fr.worsewarn.cosmox.tools.chat.MessageBuilder;
import fr.worsewarn.cosmox.tools.items.ItemBuilder;
import fr.worsewarn.heroes.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class HPlayer extends WrappedPlayer {

    private Main pl;

    private UUID uuid;
    private CosmoxPlayer cosmoxPlayer;

    private int golds;

    private HashMap<PlayerAttribute, Integer> attributes;

    private HashMap<PlayerAttribute, BukkitTask> tasks;

    public HPlayer(Main pl, UUID uuid) {
        super(uuid);

        this.pl = pl;
        this.uuid = uuid;
        this.cosmoxPlayer = pl.getAPI().getPlayer(uuid);
        this.attributes = new HashMap<>();
        this.tasks = new HashMap<>();

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
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
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
        cosmoxPlayer.getDefaultItemManager().setItemInventoryCustomSlot(new ItemBuilder(Material.SPLASH_POTION).setPotionColor(PotionEffectType.HEAL.getColor()).setPotionEffect(new PotionEffect(PotionEffectType.HEAL, 0, 0)).build(), "heal");

    }

    public void kill() {

        tasks.values().forEach(all -> {
            if(all != null) all.cancel();
        });

        pl.getManager().addPlayerToPendingSpawns(uuid);

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        player.setGameMode(GameMode.SPECTATOR);
        player.getInventory().clear();
        new MessageBuilder(pl.getGame().getPrefix() + "§c@lang/heroes.game_player_death/", true).sendMessage(player);
    }

    public int getGolds() {
        return golds;
    }

    public void changeGolds(int amount) {

        this.golds+=amount;

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        //player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(""));
        cosmoxPlayer.getScoreboard().updateLine(5, new MessageBuilder(ScoreboardFormat.GOLDS, true).formatted(golds).toString(cosmoxPlayer.getRedisPlayer().getLanguage()));
    }

    public int getAttributeLevel(PlayerAttribute attribute) {

        return attributes.get(attribute);
    }

    public float getAttributeValue(PlayerAttribute attribute) {

        return attributes.get(attribute) * attribute.getValue();
    }

    public void upgrade(PlayerAttribute attribute) {

        int currentLevel = getAttributeLevel(attribute);

        updateExtraAttributes();

        attributes.put(attribute, currentLevel+1);
    }

    public void updateExtraAttributes() {

        Player player = Bukkit.getPlayer(uuid);

        if(player == null || !player.isOnline()) return;

        if(getAttributeLevel(PlayerAttribute.SWORD) >= 10) {

            ItemStack itemStack = Utils.findFirstItem(Material.IRON_SWORD, player);

            if(itemStack != null) {
                itemStack.removeEnchantment(Enchantment.SWEEPING_EDGE);
                itemStack.addEnchantment(Enchantment.SWEEPING_EDGE, 4);
            }
        }

        if(getAttributeLevel(PlayerAttribute.CROSSBOW) >= 10) {

            //Add enchantment on crossbow
        }

        if(getAttributeLevel(PlayerAttribute.ARMOR) >= 10) {

            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
        }



    }

    public HashMap<PlayerAttribute, BukkitTask> getTasks() {
        return tasks;
    }
}
