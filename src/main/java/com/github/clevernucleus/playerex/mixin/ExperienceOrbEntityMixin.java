package com.github.clevernucleus.playerex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.playerex.api.ExAPI;

import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

@Mixin(ExperienceOrbEntity.class)
abstract class ExperienceOrbEntityMixin {

    // Inject code at the end of the constructor method
    @Inject(method = "<init>", at = @At("TAIL"))
    public void playerex_init(World world, double x, double y, double z, int amount, CallbackInfo ci) {
        // Create a BlockPos based on the provided coordinates
        BlockPos pos = BlockPos.ofFloored(x, y, z);

        // Get the chunk at the specified position
        Chunk chunk = world.getChunk(pos);

        // Access the experience data through the ExAPI and perform actions based on it
        ExAPI.EXPERIENCE_DATA.maybeGet(chunk).ifPresent(data -> {
            // Update the experience negation factor with the given amount
            if (data.updateExperienceNegationFactor(amount)) {
                // If the factor was updated and it evaluates to true, remove the experience orb
                ((ExperienceOrbEntity) (Object) this).remove(RemovalReason.DISCARDED);
            }
        });
    }
}
