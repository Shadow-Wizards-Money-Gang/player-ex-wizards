package com.github.clevernucleus.playerex.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.factory.NetworkFactory;
import com.github.clevernucleus.playerex.impl.PlayerDataManager;

import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin {
    
    // Inject code at the end of the addExperienceLevels method
    @Inject(method = "addExperienceLevels", at = @At("TAIL"))
    private void playerex_addExperienceLevels(int levels, CallbackInfo info) {
        // #TODO can't go higher than lv 18 exp wise.


        // Cast this mixin to ServerPlayerEntity to access player-related methods and fields
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        
        // Get the PlayerDataManager instance associated with the player from the ExAPI
        PlayerDataManager playerDataManager = (PlayerDataManager) ExAPI.PLAYER_DATA.get(player);
        
        // Get the current experience level and calculate the required experience using ExAPI.getConfig().requiredXp(player)
        int currentXp = player.experienceLevel;
        int requiredXp = ExAPI.getConfig().requiredXp(player);
        
        // Check if the player's current experience level is greater than or equal to the required experience level
        if (currentXp >= requiredXp) {
            // If the condition is true and the player has not been notified of the level up yet
            if (!playerDataManager.hasNotifiedLevelUp) {
                // Notify a level up using the NetworkFactory and update the flag
                NetworkFactory.notifyLevelUp(player);
                playerDataManager.hasNotifiedLevelUp = true;
            }
        } else {
            // If the condition is false, reset the notification flag
            playerDataManager.hasNotifiedLevelUp = false;
        }
    }
}
