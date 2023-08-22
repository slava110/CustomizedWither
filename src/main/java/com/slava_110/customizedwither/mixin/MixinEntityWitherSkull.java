package com.slava_110.customizedwither.mixin;

import com.slava_110.customizedwither.CustomizedWitherConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityWitherSkull.class)
public abstract class MixinEntityWitherSkull extends EntityFireball {

    private MixinEntityWitherSkull(World worldIn) {
        super(worldIn);
    }

    @Overwrite
    public boolean canBeCollidedWith() {
        return CustomizedWitherConfig.skullConfig.allowReturningToTheSender;
    }

    @Overwrite
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!CustomizedWitherConfig.skullConfig.allowReturningToTheSender) {
            return false;
        } else {
            return super.attackEntityFrom(source, amount);
        }
    }

    @Redirect(
            method = "onImpact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
                    ordinal = 0
            )
    )
    public boolean damageAlive(Entity entity, DamageSource source, float damage) {
        return entity.attackEntityFrom(source, CustomizedWitherConfig.skullConfig.damageWitherAlive);
    }

    @Redirect(
            method = "onImpact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;heal(F)V"
            )
    )
    public void healSelf(EntityLivingBase entity, float healAmount) {
        if(CustomizedWitherConfig.skullConfig.witherHealOnKill > 0)
            entity.heal(CustomizedWitherConfig.skullConfig.witherHealOnKill);
    }

    @Redirect(
            method = "onImpact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z",
                    ordinal = 1
            )
    )
    public boolean damageDead(Entity entity, DamageSource source, float damage) {
        return entity.attackEntityFrom(source, CustomizedWitherConfig.skullConfig.damageWitherDead);
    }

    @Redirect(
            method = "onImpact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/EntityLivingBase;addPotionEffect(Lnet/minecraft/potion/PotionEffect;)V"
            )
    )
    public void addEffects(EntityLivingBase entity, PotionEffect effect) {
        for(PotionEffect effect1 : CustomizedWitherConfig.skullConfig.getEffectsForDifficulty(world.getDifficulty())) {
            entity.addPotionEffect(new PotionEffect(effect1));
        }
    }

    @Redirect(
            method = "onImpact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;newExplosion(Lnet/minecraft/entity/Entity;DDDFZZ)Lnet/minecraft/world/Explosion;"
            )
    )
    public Explosion skullExplosion(
            World world,
            Entity entity,
            double x,
            double y,
            double z,
            float strength,
            boolean causesFire,
            boolean damagesTerrain
    ) {
        if(CustomizedWitherConfig.skullConfig.enableExplosion) {
            return world.newExplosion(
                    entity,
                    x, y, z,
                    CustomizedWitherConfig.skullConfig.explosionStrength,
                    CustomizedWitherConfig.skullConfig.explosionCausesFire,
                    CustomizedWitherConfig.skullConfig.explosionDamagesTerrain
                            && ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)
            );
        }
        return null;
    }
}
