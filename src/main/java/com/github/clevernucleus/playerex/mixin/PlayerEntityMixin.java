package com.github.clevernucleus.playerex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.github.clevernucleus.playerex.api.event.PlayerEntityEvents;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin {
    
    // Modify the 'bl3' variable in the 'attack' method before it's stored
    @ModifyVariable(method = "attack", at = @At("STORE"), name = "bl3", ordinal = 2)
    private boolean playerex_attack(boolean bl3, Entity target) {
        // Invoke the SHOULD_CRIT event to determine if the attack should be a critical hit
        return PlayerEntityEvents.SHOULD_CRIT.invoker().shouldCrit((PlayerEntity) (Object) this, target, bl3);
    }
    
    // Modify the 'f' variable in the 'attack' method before it's stored
    @ModifyVariable(method = "attack", at = @At(value = "STORE", ordinal = 2), name = "f", ordinal = 0)
    private float playerex_attack(float f, Entity target) {
        // Invoke the ON_CRIT event to modify the critical hit damage
        return PlayerEntityEvents.ON_CRIT.invoker().onCrit((PlayerEntity) (Object) this, target, f);
    }
}
