package com.slava_110.customizedwither.mixin;

import com.slava_110.customizedwither.CustomizedWitherConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityWither.class)
public abstract class MixinEntityWither extends EntityMob {

    private MixinEntityWither(World worldIn) {
        super(worldIn);
    }

    @Redirect(
            method = "updateAITasks()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"
            )
    )
    public Explosion explodeOnSpawning(
            World world,
            Entity entity,
            double x,
            double y,
            double z,
            float strength,
            boolean causesFire,
            boolean damagesTerrain
    ) {
        if(CustomizedWitherConfig.summoning.enableExplosion) {
            return world.newExplosion(
                    entity,
                    x, y, z,
                    CustomizedWitherConfig.summoning.explosionStrength,
                    CustomizedWitherConfig.summoning.explosionCausesFire,
                    CustomizedWitherConfig.summoning.explosionDamagesTerrain
            );
        }
        return null;
    }

    /**
     * @author slava_110
     * @reason Customization of entity armor threshold
     */
    @Overwrite
    public boolean isArmored() {
        return this.getHealth() <= this.getMaxHealth() * CustomizedWitherConfig.general.secondStageHealthPercentage;
    }

    /**
     * @author slava_110
     * @reason Customization of entity attributes. Could be done better, but I don't think it's necessary
     */
    @Overwrite
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(
                CustomizedWitherConfig.general.maxHealth
        );
        getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(
                CustomizedWitherConfig.general.movementSpeed
        );
        getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(
                CustomizedWitherConfig.general.followRange
        );
        getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(
                CustomizedWitherConfig.general.armor
        );
    }

    @ModifyConstant(
            method = "onLivingUpdate",
            constant = @Constant(
                    doubleValue = 5.0
            )
    )
    public double flyingHeight(double prev) {
        return CustomizedWitherConfig.general.flyingHeight;
    }

    @ModifyConstant(
            method = "onLivingUpdate",
            constant = @Constant(
                    doubleValue = 9.0
            )
    )
    public double distanceToPlayer(double prev) {
        return CustomizedWitherConfig.general.distanceToPlayer;
    }

    @ModifyConstant(
            method = "ignite",
            constant = @Constant(
                    intValue = 220
            )
    )
    public int invulTime(int prev) {
        return CustomizedWitherConfig.summoning.invulnerabilityTime;
    }

    @Redirect(
            method = "ignite",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/boss/EntityWither;setHealth(F)V")
    )
    public void setHealth(EntityWither entity, float v) {
        entity.setHealth(entity.getMaxHealth() * CustomizedWitherConfig.summoning.initialHealthPercentage);
    }
}
