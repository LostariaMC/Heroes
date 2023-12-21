package fr.worsewarn.heroes.manager;

import org.bukkit.Material;

public enum PlayerAttribute {

    SWORD("sword", Material.IRON_SWORD, 5),
    BOW("bow", Material.BOW, 5),
    HEAL("heal", Material.SPLASH_POTION, -0.5F),
    STICK("stick", Material.STICK, 5),
    CROSSBOW("crossbow", Material.CROSSBOW, 5),
    ARMOR("armor", Material.CHAINMAIL_CHESTPLATE, 5);

    private String id;
    private Material material;
    private float value;

    PlayerAttribute(String id, Material material, float value) {
        this.id = id;
        this.material = material;
        this.value = value;
    }

    public String getID() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public float getValue() {
        return value;
    }
}
