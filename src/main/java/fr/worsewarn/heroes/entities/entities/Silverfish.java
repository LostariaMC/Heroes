package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;

public class Silverfish extends HEntity {

    public Silverfish() {
        super("@lang/main.entity_silverfish/", EntityType.SILVERFISH, 125, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {
        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Silverfish.EntitySilverfish(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class EntitySilverfish extends net.minecraft.world.entity.monster.Silverfish {

        public EntitySilverfish(Level var1) {
            super(net.minecraft.world.entity.EntityType.SILVERFISH, var1);
        }

        protected void registerGoals() {
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
            this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Villager.class, 8.0F));
            this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Villager.class, true));
        }
    }
}
