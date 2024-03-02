package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import fr.worsewarn.heroes.manager.HPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.entity.EntityType;

public class Endermite extends HEntity {

    public Endermite() {
        super("@lang/main.entity_endermite/", EntityType.ENDERMITE, 110, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {
        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Endermite.EntityEndermite(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class EntityEndermite extends net.minecraft.world.entity.monster.Endermite {

        public EntityEndermite(Level var1) {
            super(net.minecraft.world.entity.EntityType.ENDERMITE, var1);
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
