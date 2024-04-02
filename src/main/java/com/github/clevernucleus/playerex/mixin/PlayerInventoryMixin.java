package com.github.clevernucleus.playerex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.dataattributes_dc.api.DataAttributesAPI;
import com.github.clevernucleus.playerex.api.ExAPI;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;

@Mixin(PlayerInventory.class)
abstract class PlayerInventoryMixin {

    // Inject code into the getBlockBreakingSpeed method after its return
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void playerex_getBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> info) {
        // Store the original return value of the method
        float original = info.getReturnValue();

        // Use DataAttributesAPI to get the breaking speed attribute from the player
        float result = DataAttributesAPI.ifPresent(((PlayerInventory) (Object) this).player, ExAPI.BREAKING_SPEED,
                original, value -> {
                    // Modify the original breaking speed based on the attribute value
                    return (float) (original + value - 1.0);
                });

        // Set the modified value as the new return value
        info.setReturnValue(result);
    }
}
