package com.github.clevernucleus.playerex;

import com.github.clevernucleus.dataattributes_dc.api.event.EntityAttributeModifiedEvents;
import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.event.LivingEntityEvents;
import com.github.clevernucleus.playerex.api.event.PlayerEntityEvents;
import com.github.clevernucleus.playerex.config.ConfigImpl;
import com.github.clevernucleus.playerex.factory.DamageFactory;
import com.github.clevernucleus.playerex.factory.EventFactory;
import com.github.clevernucleus.playerex.factory.ExScreenFactory;
import com.github.clevernucleus.playerex.factory.NetworkFactory;
import com.github.clevernucleus.playerex.factory.PlaceholderFactory;
import com.github.clevernucleus.playerex.factory.RefundFactory;
import com.github.clevernucleus.playerex.impl.CommandsImpl;

import eu.pb4.placeholders.api.Placeholders;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class PlayerEx implements ModInitializer {

    // Screen handler type for the custom screen
    public static final ScreenHandlerType<ExScreenFactory.Handler> EX_SCREEN = Registry
            .register(Registries.SCREEN_HANDLER, new Identifier(ExAPI.MODID, "ex_screen"), ExScreenFactory.type());

    // Sound events for level up and SP spend
    public static final SoundEvent LEVEL_UP_SOUND = SoundEvent.of(new Identifier(ExAPI.MODID, "level_up"));
    public static final SoundEvent SP_SPEND_SOUND = SoundEvent.of(new Identifier(ExAPI.MODID, "sp_spend"));

    @Override
    public void onInitialize() {
        // Register the configuration class
        AutoConfig.register(ConfigImpl.class, GsonConfigSerializer::new);

        // Register networking receivers for custom network packets
        ServerLoginNetworking.registerGlobalReceiver(NetworkFactory.CONFIG, NetworkFactory::loginQueryResponse);
        ServerPlayNetworking.registerGlobalReceiver(NetworkFactory.SCREEN, NetworkFactory::switchScreen);
        ServerPlayNetworking.registerGlobalReceiver(NetworkFactory.MODIFY, NetworkFactory::modifyAttributes);

        // Register custom commands
        CommandRegistrationCallback.EVENT.register(CommandsImpl::register);

        // Register event listeners for various server events
        ServerLoginConnectionEvents.QUERY_START.register(NetworkFactory::loginQueryStart);
        ServerLifecycleEvents.SERVER_STARTING.register(EventFactory::serverStarting);
        ServerPlayerEvents.COPY_FROM.register(EventFactory::reset);

        // Register LivingEntity events
        LivingEntityEvents.ON_HEAL.register(EventFactory::healed);
        LivingEntityEvents.EVERY_SECOND.register(EventFactory::healthRegeneration);
        LivingEntityEvents.ON_DAMAGE.register(EventFactory::onDamage);
        LivingEntityEvents.SHOULD_DAMAGE.register(EventFactory::shouldDamage);

        // Register PlayerEntity events
        PlayerEntityEvents.ON_CRIT.register(EventFactory::onCritAttack);
        PlayerEntityEvents.SHOULD_CRIT.register(EventFactory::attackIsCrit);

        // Register attribute modification events
        EntityAttributeModifiedEvents.CLAMPED.register(EventFactory::clamped);

        // Register damage modifications, refund conditions, and placeholders
        DamageFactory.STORE.forEach(ExAPI::registerDamageModification);
        RefundFactory.STORE.forEach(ExAPI::registerRefundCondition);
        PlaceholderFactory.STORE.forEach(Placeholders::register);

        // Register custom sound events
        Registry.register(Registries.SOUND_EVENT, LEVEL_UP_SOUND.getId(), LEVEL_UP_SOUND);
        Registry.register(Registries.SOUND_EVENT, SP_SPEND_SOUND.getId(), SP_SPEND_SOUND);
    }
}
