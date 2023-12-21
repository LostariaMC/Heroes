package fr.worsewarn.heroes.entities;

import fr.worsewarn.cosmox.tools.chat.MessageBuilder;
import fr.worsewarn.heroes.manager.EntityAttribute;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.HashMap;

public abstract class HEntity {

    private String name;
    private EntityType entityType;
    private HashMap<EntityAttribute, Integer> attributes;
    private boolean locked;

    private TargetType targetType;

    public abstract Entity spawn(Location location);

    public HEntity(String name, EntityType entityType, int spawnPercent, TargetType targetType) {
        this.name = name;
        this.entityType = entityType;
        this.attributes = new HashMap<>();
        Arrays.stream(EntityAttribute.values()).forEach(all -> attributes.put(all, 0));
        attributes.put(EntityAttribute.SPAWN_PERCENT, spawnPercent);
        this.targetType = targetType;
        this.locked = true;
    }

    public String getName() {
        return name;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void increaseAttribute(EntityAttribute attribute) {

        attributes.put(attribute, getAttribute(attribute) + attribute.getModifier());

    }

    public int getAttribute(EntityAttribute attribute) {
        return attributes.getOrDefault(attribute, 0);
    }

    public boolean isLocked() {
        return locked;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public boolean isUnlocked() { return !locked; }

    public void unlock() { this.locked = false; }
}
