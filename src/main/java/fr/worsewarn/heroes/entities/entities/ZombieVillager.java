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

public class ZombieVillager extends HEntity {
    public ZombieVillager() {
        super("@lang/main.entity_zombie_villager/", EntityType.ZOMBIE_VILLAGER, 100, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new ZombieVillager.ZombieVillagerEntity(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class ZombieVillagerEntity extends net.minecraft.world.entity.monster.ZombieVillager {

        public ZombieVillagerEntity(Level var1) {
            super(net.minecraft.world.entity.EntityType.ZOMBIE_VILLAGER, var1);
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

        @Override
        protected boolean isSunSensitive() {
            return false;
        }

    }


}
