package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class Husk extends HEntity {

    public Husk() {
        super("@lang/main.entity_husk/", EntityType.HUSK, 100, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Husk.HuskEntity(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class HuskEntity extends net.minecraft.world.entity.monster.Husk {

        public HuskEntity(Level world) {
            super(net.minecraft.world.entity.EntityType.HUSK, world);
        }

        protected void registerGoals() {
            this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
            this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
            this.addBehaviourGoals();
        }

        protected void addBehaviourGoals() {
            this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Villager.class, true));


        }
    }
}
