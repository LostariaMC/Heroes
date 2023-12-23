package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.Flyable;
import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;

public class Vex extends HEntity implements Flyable {

    public Vex() {
        super("@lang/main.entity_vex/", EntityType.VEX, 25, TargetType.PLAYERS);
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Vex.VexEntity(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class VexEntity extends net.minecraft.world.entity.monster.Vex {


        public VexEntity(Level world) {
            super(net.minecraft.world.entity.EntityType.VEX, world);
        }
    }
}
