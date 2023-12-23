package fr.worsewarn.heroes.entities.entities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Dynamic;
import fr.worsewarn.heroes.entities.HEntity;
import fr.worsewarn.heroes.entities.TargetType;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.EntityType;

public class Hoglin extends HEntity {

    public Hoglin() {
        super("@lang/main.entity_hoglin/", EntityType.HOGLIN, 25, TargetType.VILLAGER);
    }

    @Override
    public Entity spawn(Location location) {

        Level level = ((CraftWorld)location.getWorld()).getHandle();
        Entity entity = new Hoglin.EntityHoglin(level);;

        entity.setPos(location.getX(), location.getY(), location.getZ());
        level.addFreshEntity(entity);

        return entity;
    }

    class EntityHoglin extends net.minecraft.world.entity.monster.hoglin.Hoglin {

        protected static final ImmutableList<? extends SensorType<? extends Sensor<? super net.minecraft.world.entity.monster.hoglin.Hoglin>>> SENSOR_TYPES_SPECIAL;
        public EntityHoglin(Level var1) {
            super(net.minecraft.world.entity.EntityType.HOGLIN, var1);

            this.setImmuneToZombification(true);
            //getBrain().removeAllBehaviors();
            //getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, var1, 200L);
            //getBrain().addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, ImmutableList.of(BecomePassiveIfMemoryPresent.create(MemoryModuleType.NEAREST_REPELLENT, 200), new AnimalMakeLove(net.minecraft.world.entity.EntityType.HOGLIN, 0.6F), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0F), BehaviorBuilder.triggerIf(net.minecraft.world.entity.monster.hoglin.Hoglin::isAdult, MeleeAttack.create(40)), BehaviorBuilder.triggerIf(AgeableMob::isBaby, MeleeAttack.create(15)), StopAttackingIfTargetInvalid.create()), MemoryModuleType.ATTACK_TARGET);


        }

        @Override
        protected Brain.Provider<net.minecraft.world.entity.monster.hoglin.Hoglin> brainProvider() {
            return Brain.provider(MEMORY_TYPES, SENSOR_TYPES_SPECIAL);
        }

        protected Brain<?> makeBrain(Dynamic<?> var0) {
            Brain<net.minecraft.world.entity.monster.hoglin.Hoglin> var1 = this.brainProvider().makeBrain(var0);
            var1.setCoreActivities(ImmutableSet.of(Activity.CORE));
            var1.setDefaultActivity(Activity.IDLE);
            var1.useDefaultActivity();
            return var1;
        }

        static {
            SENSOR_TYPES_SPECIAL = ImmutableList.of(SensorType.VILLAGER_HOSTILES);
        }

    }
}
