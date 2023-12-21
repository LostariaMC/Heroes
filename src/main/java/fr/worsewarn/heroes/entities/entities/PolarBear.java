package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;

import java.util.function.Predicate;

public class PolarBear extends HEntity {

    public PolarBear() {
        super(EntityType.POLAR_BEAR, 50, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new PolarBearEntity(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class PolarBearEntity extends net.minecraft.world.entity.animal.PolarBear {

        public PolarBearEntity(Level var1) {
            super(EntityType.POLAR_BEAR, var1);
        }

        protected void registerGoals() {
            super.registerGoals();
            this.goalSelector.addGoal(0, new FloatGoal(this));
            this.goalSelector.addGoal(1, new PolarBearMeleeAttackGoal());
            this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25));
            this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0));
            this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
            this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Player.class, 10, true, true, (Predicate)null));
            this.targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal(this, false));
        }

        class PolarBearMeleeAttackGoal extends MeleeAttackGoal {
            public PolarBearMeleeAttackGoal() {
                super(PolarBearEntity.this, 1.25, true);
            }

            protected void checkAndPerformAttack(LivingEntity var0, double var1) {
                double var3 = this.getAttackReachSqr(var0);
                if (var1 <= var3 && this.isTimeToAttack()) {
                    this.resetAttackCooldown();
                    this.mob.doHurtTarget(var0);
                    setStanding(false);
                } else if (var1 <= var3 * 2.0) {
                    if (this.isTimeToAttack()) {
                        setStanding(false);
                        this.resetAttackCooldown();
                    }

                    if (this.getTicksUntilNextAttack() <= 10) {
                        setStanding(true);
                        playWarningSound();
                    }
                } else {
                    this.resetAttackCooldown();
                    setStanding(false);
                }

            }

            public void stop() {
                setStanding(false);
                super.stop();
            }

            protected double getAttackReachSqr(LivingEntity var0) {
                return (double)(4.0F + var0.getBbWidth());
            }
        }

    }
}
