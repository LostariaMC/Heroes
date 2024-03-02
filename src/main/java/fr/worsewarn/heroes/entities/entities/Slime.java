package fr.worsewarn.heroes.entities.entities;

import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.event.CraftEventFactory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.SlimeSplitEvent;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class Slime extends HEntity {

    public Slime() {
        super("@lang/main.entity_slime/", EntityType.SLIME, 110, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Slime.SlimeEntity(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class SlimeEntity extends net.minecraft.world.entity.monster.Slime {

        public SlimeEntity(Level var1) {
            super(net.minecraft.world.entity.EntityType.SLIME, var1);
            setSize(6, true);
        }

        protected void registerGoals() {
            this.goalSelector.addGoal(1, new SlimeFloatGoal(this));
            this.goalSelector.addGoal(2, new SlimeAttackGoal(this));
            this.goalSelector.addGoal(3, new SlimeRandomDirectionGoal(this));
            this.goalSelector.addGoal(5, new SlimeKeepOnJumpingGoal(this));
            this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Villager.class, true));
        }

        private static class SlimeKeepOnJumpingGoal extends Goal {
            private final net.minecraft.world.entity.monster.Slime slime;

            public SlimeKeepOnJumpingGoal(net.minecraft.world.entity.monster.Slime entityslime) {
                this.slime = entityslime;
                this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
            }

            public boolean canUse() {
                return !this.slime.isPassenger();
            }

            public void tick() {
                MoveControl controllermove = this.slime.getMoveControl();
                if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                    entityslime_controllermoveslime.setWantedMovement(1.0);
                }

            }
        }

        private class SlimeMoveControl extends MoveControl {
            private float yRot;
            private int jumpDelay;
            private final net.minecraft.world.entity.monster.Slime slime;
            private boolean isAggressive;

            public SlimeMoveControl(net.minecraft.world.entity.monster.Slime entityslime) {
                super(entityslime);
                this.slime = entityslime;
                this.yRot = 180.0F * entityslime.getYRot() / 3.1415927F;
            }

            public void setDirection(float f, boolean flag) {
                this.yRot = f;
                this.isAggressive = flag;
            }

            public void setWantedMovement(double d0) {
                this.speedModifier = d0;
                this.operation = Operation.MOVE_TO;
            }

            public void tick() {
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
                this.mob.yHeadRot = this.mob.getYRot();
                this.mob.yBodyRot = this.mob.getYRot();
                if (this.operation != Operation.MOVE_TO) {
                    this.mob.setZza(0.0F);
                } else {
                    this.operation = Operation.WAIT;
                    if (this.mob.onGround()) {
                        this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                        if (this.jumpDelay-- <= 0) {
                            this.jumpDelay = getJumpDelay();
                            if (this.isAggressive) {
                                this.jumpDelay /= 3;
                            }

                            this.slime.getJumpControl().jump();
                            if (doPlayJumpSound()) {
                                this.slime.playSound(getJumpSound(), getSoundVolume(), getSoundPitch());
                            }
                        } else {
                            this.slime.xxa = 0.0F;
                            this.slime.zza = 0.0F;
                            this.mob.setSpeed(0.0F);
                        }
                    } else {
                        this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    }
                }

            }
        }

        private class SlimeAttackGoal extends Goal {
            private final net.minecraft.world.entity.monster.Slime slime;
            private int growTiredTimer;

            public SlimeAttackGoal(net.minecraft.world.entity.monster.Slime entityslime) {
                this.slime = entityslime;
                this.setFlags(EnumSet.of(Flag.LOOK));
            }

            public boolean canUse() {
                LivingEntity entityliving = this.slime.getTarget();
                return entityliving == null ? false : (!this.slime.canAttack(entityliving) ? false : this.slime.getMoveControl() instanceof SlimeMoveControl);
            }

            public void start() {
                this.growTiredTimer = reducedTickDelay(300);
                super.start();
            }

            public boolean canContinueToUse() {
                LivingEntity entityliving = this.slime.getTarget();
                return entityliving == null ? false : (!this.slime.canAttack(entityliving) ? false : --this.growTiredTimer > 0);
            }

            public boolean requiresUpdateEveryTick() {
                return true;
            }

            public void tick() {
                LivingEntity entityliving = this.slime.getTarget();
                if (entityliving != null) {
                    this.slime.lookAt(entityliving, 10.0F, 10.0F);
                }

                MoveControl controllermove = this.slime.getMoveControl();
                if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                    entityslime_controllermoveslime.setDirection(this.slime.getYRot(), isDealsDamage());
                }

            }
        }

        private static class SlimeRandomDirectionGoal extends Goal {
            private final net.minecraft.world.entity.monster.Slime slime;
            private float chosenDegrees;
            private int nextRandomizeTime;

            public SlimeRandomDirectionGoal(net.minecraft.world.entity.monster.Slime entityslime) {
                this.slime = entityslime;
                this.setFlags(EnumSet.of(Flag.LOOK));
            }

            public boolean canUse() {
                return this.slime.getTarget() == null && (this.slime.onGround() || this.slime.isInWater() || this.slime.isInLava() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getMoveControl() instanceof SlimeMoveControl;
            }

            public void tick() {
                if (--this.nextRandomizeTime <= 0) {
                    this.nextRandomizeTime = this.adjustedTickDelay(40 + this.slime.getRandom().nextInt(60));
                    this.chosenDegrees = (float)this.slime.getRandom().nextInt(360);
                }

                MoveControl controllermove = this.slime.getMoveControl();
                if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                    entityslime_controllermoveslime.setDirection(this.chosenDegrees, false);
                }

            }
        }

        private static class SlimeFloatGoal extends Goal {
            private final net.minecraft.world.entity.monster.Slime slime;

            public SlimeFloatGoal(net.minecraft.world.entity.monster.Slime entityslime) {
                this.slime = entityslime;
                this.setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
                entityslime.getNavigation().setCanFloat(true);
            }

            public boolean canUse() {
                return (this.slime.isInWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof SlimeMoveControl;
            }

            public boolean requiresUpdateEveryTick() {
                return true;
            }

            public void tick() {
                if (this.slime.getRandom().nextFloat() < 0.8F) {
                    this.slime.getJumpControl().jump();
                }

                MoveControl controllermove = this.slime.getMoveControl();
                if (controllermove instanceof SlimeMoveControl entityslime_controllermoveslime) {
                    entityslime_controllermoveslime.setWantedMovement(1.2);
                }

            }
        }

        float getSoundPitch() {
            float f = this.isTiny() ? 1.4F : 0.8F;
            return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
        }

        protected boolean isDealsDamage() {
            return !this.isTiny() && this.isEffectiveAi();
        }

        protected SoundEvent getHurtSound(DamageSource damagesource) {
            return this.isTiny() ? SoundEvents.SLIME_HURT_SMALL : SoundEvents.SLIME_HURT;
        }

        protected SoundEvent getDeathSound() {
            return this.isTiny() ? SoundEvents.SLIME_DEATH_SMALL : SoundEvents.SLIME_DEATH;
        }

        protected SoundEvent getSquishSound() {
            return this.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
        }

        protected float getSoundVolume() {
            return 0.4F * (float)this.getSize();
        }

        protected SoundEvent getJumpSound() {
            return this.isTiny() ? SoundEvents.SLIME_JUMP_SMALL : SoundEvents.SLIME_JUMP;
        }

        public void remove(Entity.RemovalReason entity_removalreason) {
            super.remove(entity_removalreason);
        }

    }
}
