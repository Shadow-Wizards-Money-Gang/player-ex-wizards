package com.github.clevernucleus.playerex.factory;

import java.util.function.BiConsumer;

import com.github.clevernucleus.dataattributes_dc.api.DataAttributesAPI;
import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.damage.DamageFunction;
import com.github.clevernucleus.playerex.api.damage.DamagePredicate;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.PotionEntity;

public final class DamageFactory {
	public static final DamageFactory STORE = new DamageFactory();

	private DamageFactory() {
	}

	public void forEach(BiConsumer<DamagePredicate, DamageFunction> registry) {
		registry.accept((living, source, damage) -> source.isOf(DamageTypes.ON_FIRE),
				(living, source, damage) -> DataAttributesAPI // <- ON_FIRE? unsure about implementation, but should be
																// right
						.ifPresent(living, ExAPI.FIRE_RESISTANCE, damage, value -> (float) (damage * (1.0 - value))));
		registry.accept((living, source, damage) -> source.isOf(DamageTypes.FREEZE),
				(living, source, damage) -> DataAttributesAPI.ifPresent(living, ExAPI.FREEZE_RESISTANCE, damage,
						value -> (float) (damage * (1.0 - value))));
		registry.accept((living, source, damage) -> source.isOf(DamageTypes.LIGHTNING_BOLT),
				(living, source, damage) -> DataAttributesAPI.ifPresent(living, ExAPI.LIGHTNING_RESISTANCE, damage,
						value -> (float) (damage * (1.0 - value))));
		registry.accept(
				(living, source, damage) -> living.hasStatusEffect(StatusEffects.POISON)
						&& source.isOf(DamageTypes.MAGIC)
						&& damage <= 1.0F,
				(living, source, damage) -> DataAttributesAPI.ifPresent(living, ExAPI.FREEZE_RESISTANCE, damage,
						value -> (float) (damage * (1.0 - value))));
		registry.accept(
				(living, source, damage) -> source.isOf(DamageTypes.WITHER) || (source.isOf(DamageTypes.INDIRECT_MAGIC)
						&& (source.getSource() instanceof PotionEntity
								|| source.getSource() instanceof AreaEffectCloudEntity)),
				(living, source, damage) -> {
					return DataAttributesAPI.ifPresent(living, ExAPI.WITHER_RESISTANCE, damage, value -> {
						if (source.isOf(DamageTypes.WITHER) && living.isUndead())
							return 0.0F;
						if (source.getName().equals("indirectMagic") && source.getSource() instanceof PotionEntity
								&& living.isUndead())
							return damage;
						return (float) (damage * (1.0 - value));
					});
				});
	}
}
