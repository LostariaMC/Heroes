package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class IronGolem extends HEntity {
    public IronGolem() {
        super("@lang/main.entity_iron_golem/", EntityType.IRON_GOLEM, 15, TargetType.PLAYERS);
    }

    @Override
    public Entity spawn(Location location) {
        return null;
    }
}
