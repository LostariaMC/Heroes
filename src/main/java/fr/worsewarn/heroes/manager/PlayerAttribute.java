package fr.worsewarn.heroes.manager;

import org.bukkit.Material;

public enum PlayerAttribute {

    SWORD("sword", "@lang/heroes.inventory_upgrades_sword_description/", Material.IRON_SWORD, 5, "%"),
    BOW("bow", "@lang/heroes.inventory_upgrades_bow_description/", Material.BOW, 5, "%"),
    HEAL("heal", "@lang/heroes.inventory_upgrades_heal_description/", Material.SPLASH_POTION, -0.5F, "s"),
    //STICK("stick", Material.STICK, 5),
    //CROSSBOW("crossbow", Material.CROSSBOW, 5),
    ARMOR("armor", "@lang/heroes.inventory_upgrades_armor_description/", Material.CHAINMAIL_CHESTPLATE, 5, "%");

    private String id;
    private String name;
    private Material material;
    private float value;

    private String suffix;

    PlayerAttribute(String id, String name, Material material, float value, String suffix) {
        this.id = id;
        this.name = name;
        this.material = material;
        this.value = value;
        this.suffix = suffix;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public float getValue() {
        return value;
    }

    public String getSuffix() {
        return suffix;
    }
}
