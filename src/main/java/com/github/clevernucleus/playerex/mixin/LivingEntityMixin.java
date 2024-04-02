package com.github.clevernucleus.playerex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.clevernucleus.playerex.api.event.LivingEntityEvents;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {

    // Unique field to store tick counter
    @Unique
    private int playerex_ticks;

    // Modify the amount of healing before it occurs
    @ModifyVariable(method = "heal", at = @At("HEAD"))
    private float playerex_heal(float amount) {
        return LivingEntityEvents.ON_HEAL.invoker().onHeal((LivingEntity) (Object) this, amount);
    }

    // Inject code at the beginning of the heal method to potentially cancel healing
    @Inject(method = "heal", at = @At("HEAD"), cancellable = true)
    private void playerex_heal(float amount, CallbackInfo info) {
        // Check if healing should be cancelled
        final boolean cancel = LivingEntityEvents.SHOULD_HEAL.invoker().shouldHeal((LivingEntity) (Object) this, amount);

        if (!cancel) {
            // If not cancelled, proceed with the default healing
            info.cancel();
        }
    }

    // Inject code at the end of the tick method to trigger an event every second
    @Inject(method = "tick", at = @At("TAIL"))
    private void playerex_tick(CallbackInfo info) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        // Increment the tick counter, trigger an event every second, and reset the counter after 20 ticks (1 second)
        if (this.playerex_ticks < 20) {
            this.playerex_ticks++;
        } else {
            LivingEntityEvents.EVERY_SECOND.invoker().everySecond(livingEntity);
            this.playerex_ticks = 0;
        }
    }

    // Modify the amount of damage before it occurs
    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0)
    private float playerex_damage(float amount, DamageSource source) {
        return LivingEntityEvents.ON_DAMAGE.invoker().onDamage((LivingEntity) (Object) this, source, amount);
    }

    // Inject code before the despawnCounter field is accessed to potentially cancel damage
    @Inject(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;despawnCounter:I", ordinal = 0), cancellable = true)
    private void playerex_damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        // Check if damage should be cancelled
        final boolean cancel = LivingEntityEvents.SHOULD_DAMAGE.invoker().shouldDamage((LivingEntity) (Object) this, source, amount);

        if (!cancel) {
            // If not cancelled, proceed with the default damage handling
            info.setReturnValue(false);
        }
    }
}
