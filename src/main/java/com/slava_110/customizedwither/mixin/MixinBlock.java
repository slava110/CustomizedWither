package com.slava_110.customizedwither.mixin;

import com.slava_110.customizedwither.CustomizedWitherConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Block.class)
public abstract class MixinBlock {

    @Redirect(
            method = "canEntityDestroy",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/boss/EntityWither;canDestroyBlock(Lnet/minecraft/block/Block;)Z"
            )
    )
    public boolean canWitherDestroy(Block block, IBlockState state) {
        String id = block.getRegistryName().toString();
        String idWithMeta = id + ":" + block.getMetaFromState(state);

        for(String whitelisted : CustomizedWitherConfig.general.destroyableBlocksWhitelist) {
            if(whitelisted.equals(id) || whitelisted.equals(idWithMeta))
                return true;
        }

        for(String blacklisted : CustomizedWitherConfig.general.destroyableBlocksBlacklist) {
            if(blacklisted.equals(id) || blacklisted.equals(idWithMeta))
                return false;
        }

        return true;
    }
}
