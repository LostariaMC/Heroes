package fr.worsewarn.heroes.entities;

import net.minecraft.world.entity.Entity;
import org.bukkit.Location;

public abstract class HEntity {

    private final int STRENGHT_PERCENT_ADDED = 20;
    private final int VITALITY_PERCENT_ADDED = 20;
    private final int AGILITY_PERCENT_ADDED = 20;
    private final int SPAWN_PERCENT_ADDED = 20;

    private int strenght, vitality, agility, spawnPercent;
    private boolean locked;

    private TargetType targetType;

    public abstract Entity spawn(Location location);

    public HEntity(int spawnPercent, TargetType targetType) {
        this.spawnPercent = spawnPercent;
        this.targetType = targetType;
        this.locked = true;
    }

    public int getStrenght() {
        return strenght;
    }

    public void increaseStrenght() { this.strenght +=STRENGHT_PERCENT_ADDED; }

    public int getVitality() {
        return vitality;
    }

    public void increaseVitality() { this.strenght +=VITALITY_PERCENT_ADDED; }

    public int getAgility() {
        return agility;
    }

    public void increaseAgility() { this.strenght +=AGILITY_PERCENT_ADDED; }

    public int getSpawnPercent() {
        return spawnPercent;
    }

    public void increaseSpawnPercent() { this.strenght +=SPAWN_PERCENT_ADDED; }

    public boolean isLocked() {
        return locked;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public boolean isUnlocked() { return !locked; }

    public void unlock() { this.locked = false; }
}
