package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;

public class CaveSpider extends HEntity {

    public CaveSpider() {
        super("@lang/main.entity_cave_spider/", EntityType.CAVE_SPIDER, 100, TargetType.PLAYERS);
    }

    @Override
    public Entity spawn(Location location) {
        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new CaveSpider.EntityCaveSpider(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class EntityCaveSpider extends net.minecraft.world.entity.monster.CaveSpider {

        public EntityCaveSpider(Level world) {
            super(net.minecraft.world.entity.EntityType.CAVE_SPIDER, world);
        }

        protected void registerGoals() {
            this.goalSelector.addGoal(1, new FloatGoal(this));
            this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
            this.goalSelector.addGoal(4, new SpiderAttackGoal(this));
            this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
            this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
            this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
            this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
            this.targetSelector.addGoal(2, new SpiderTargetGoal(this, Player.class));
            this.targetSelector.addGoal(3, new SpiderTargetGoal(this, IronGolem.class));
        }

        private static class SpiderAttackGoal extends MeleeAttackGoal {
            public SpiderAttackGoal(net.minecraft.world.entity.monster.Spider entityspider) {
                super(entityspider, 1.0, true);
            }

            public boolean canUse() {
                return super.canUse() && !this.mob.isVehicle();
            }

            public boolean canContinueToUse() {
                float f = this.mob.getLightLevelDependentMagicValue();
                if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                    this.mob.setTarget((LivingEntity)null);
                    return false;
                } else {
                    return super.canContinueToUse();
                }
            }

            protected double getAttackReachSqr(LivingEntity entityliving) {
                return (double)(4.0F + entityliving.getBbWidth());
            }
        }

        private static class SpiderTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
            public SpiderTargetGoal(net.minecraft.world.entity.monster.Spider entityspider, Class<T> oclass) {
                super(entityspider, oclass, true);
            }

            public boolean canUse() {
                return super.canUse();
            }
        }
    }
}
