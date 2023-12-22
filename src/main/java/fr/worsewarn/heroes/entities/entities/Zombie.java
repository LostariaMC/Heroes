package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;

public class Zombie extends HEntity {
    public Zombie() {
        super("Zombie", EntityType.ZOMBIE, 100, TargetType.PLAYERS);
        unlock();
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Zombie.ZombieEntity(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class ZombieEntity extends net.minecraft.world.entity.monster.Zombie {

        public ZombieEntity(Level var1) {
            super(net.minecraft.world.entity.EntityType.ZOMBIE, var1);
        }

        protected void addBehaviourGoals() {
            this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0, false));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
        }
    }
}
