package com.github.clevernucleus.playerex.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.github.clevernucleus.dataattributes_dc.api.DataAttributesAPI;
import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.client.ClientUtil;
import com.github.clevernucleus.playerex.api.client.PageLayer;
import com.github.clevernucleus.playerex.api.client.RenderComponent;
import com.github.clevernucleus.playerex.client.PlayerExClient;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.projectile_damage.api.EntityAttributes_ProjectileDamage;

@Environment(EnvType.CLIENT)
public class CombatPageLayer extends PageLayer {
	private static Supplier<Float> scaleX = () -> ExAPI.getConfig().textScaleX();
	private static Supplier<Float> scaleY = () -> ExAPI.getConfig().textScaleY();
	private static float scaleZ = 0.75F;

	private static final List<RenderComponent> COMPONENTS = new ArrayList<RenderComponent>();

	public CombatPageLayer(HandledScreen<?> parent, ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(parent, handler, inventory, title);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		MatrixStack stack = context.getMatrices();

		stack.push();
		stack.scale(scaleX.get(), scaleY.get(), scaleZ);

		COMPONENTS.forEach(component -> component.renderText(this.client.player, context, this.textRenderer, this.x,
				this.y, scaleX.get(), scaleY.get()));

		context.drawText(textRenderer,
				Text.translatable("playerex.gui.page.combat.text.melee").formatted(Formatting.DARK_GRAY),
				(int) ((this.x + 21) / scaleX.get()), (int) ((this.y + 26) / scaleY.get()), 4210752, false);
		context.drawText(textRenderer,
				Text.translatable("playerex.gui.page.combat.text.defense").formatted(Formatting.DARK_GRAY),
				(int) ((this.x + 21) / scaleX.get()), (int) ((this.y + 92) / scaleY.get()), 4210752, false);
		context.drawText(textRenderer,
				Text.translatable("playerex.gui.page.combat.text.ranged").formatted(Formatting.DARK_GRAY),
				(int) ((this.x + 105) / scaleX.get()), (int) ((this.y + 26) / scaleY.get()), 4210752, false);

		stack.pop();

		COMPONENTS.forEach(component -> component.drawTooltip(this.client.player, context::drawTooltip, context,
				this.textRenderer, this.x, this.y, mouseX, mouseY, scaleX.get(), scaleY.get()));
	}

	@Override
	public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		context.drawTexture(PlayerExClient.GUI, this.x + 9, this.y + 24, 244, 9, 9, 9);
		context.drawTexture(PlayerExClient.GUI, this.x + 9, this.y + 90, 226, 18, 9, 9);
		context.drawTexture(PlayerExClient.GUI, this.x + 93, this.y + 24, 235, 18, 9, 9);

		DataAttributesAPI.ifPresent(this.client.player, ExAPI.ATTACK_RANGE, (Object) null, value -> {
			context.drawTexture(PlayerExClient.GUI, this.x + 93, this.y + 79, 226, 27, 9, 9);
			return (Object) null;
		});

		DataAttributesAPI.ifPresent(this.client.player, ExAPI.LIFESTEAL, (Object) null, value -> {
			context.drawTexture(PlayerExClient.GUI, this.x + 93, this.y + 90, 244, 18, 9, 9);
			return (Object) null;
		});
	}

	static {
		COMPONENTS.add(RenderComponent.of(() -> EntityAttributes.GENERIC_ATTACK_SPEED, value -> {
			return Text
					.translatable("playerex.gui.page.combat.text.attack_speed", ClientUtil.FORMATTING_2.format(value))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add(
					(Text.translatable("playerex.gui.page.combat.tooltip.attack_speed[0]")).formatted(Formatting.GRAY));
			tooltip.add(
					(Text.translatable("playerex.gui.page.combat.tooltip.attack_speed[1]")).formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 146));
		COMPONENTS.add(RenderComponent.of(() -> EntityAttributes.GENERIC_ATTACK_DAMAGE, value -> {
			return Text
					.translatable("playerex.gui.page.combat.text.attack_damage", ClientUtil.FORMATTING_2.format(value))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.attack_damage[0]"))
					.formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.attack_damage[1]"))
					.formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 37));
		COMPONENTS.add(RenderComponent.of(ExAPI.MELEE_CRIT_DAMAGE, value -> {
			double disp = 100.0D + ClientUtil.displayValue(ExAPI.MELEE_CRIT_DAMAGE, value);
			return Text.translatable("playerex.gui.page.combat.text.melee_crit_damage",
					ClientUtil.FORMATTING_2.format(disp)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.melee_crit_damage[0]"))
					.formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.melee_crit_damage[1]"))
					.formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 48));
		COMPONENTS.add(RenderComponent.of(ExAPI.MELEE_CRIT_CHANCE, value -> {
			double disp = ClientUtil.displayValue(ExAPI.MELEE_CRIT_CHANCE, value);
			return Text.translatable("playerex.gui.page.combat.text.melee_crit_chance",
					ClientUtil.FORMATTING_2.format(disp)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.melee_crit_chance[0]"))
					.formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.melee_crit_chance[1]"))
					.formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 59));

		COMPONENTS.add(RenderComponent.of(() -> EntityAttributes_ProjectileDamage.GENERIC_PROJECTILE_DAMAGE, value -> {
			double disp = ClientUtil.displayValue(() -> EntityAttributes_ProjectileDamage.GENERIC_PROJECTILE_DAMAGE,
					value);
			return Text
					.translatable("playerex.gui.page.combat.text.ranged_damage", ClientUtil.FORMATTING_2.format(disp))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.ranged_damage[0]"))
					.formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.ranged_damage[1]"))
					.formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 70));
		COMPONENTS.add(RenderComponent.of(ExAPI.RANGED_CRIT_CHANCE, value -> {
			double disp = ClientUtil.displayValue(ExAPI.RANGED_CRIT_CHANCE, value);
			return Text.translatable("playerex.gui.page.combat.text.ranged_crit_chance",
					ClientUtil.FORMATTING_2.format(disp)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.ranged_crit_chance[0]"))
					.formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.ranged_crit_chance[1]"))
					.formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 81));
		COMPONENTS.add(RenderComponent.of(ExAPI.ATTACK_RANGE, value -> {
			return Text.translatable("playerex.gui.page.combat.text.attack_range",
					ClientUtil.FORMATTING_2.format(3.0F + value)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.attack_range",
					ClientUtil.FORMATTING_2.format(3.0F + value))).formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 92));

		COMPONENTS.add(RenderComponent.of(() -> EntityAttributes.GENERIC_ARMOR, value -> {
			return Text.translatable("playerex.gui.page.combat.text.armor", ClientUtil.FORMATTING_2.format(value))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.armor[0]")).formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.armor[1]")).formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 103));
		COMPONENTS.add(RenderComponent.of(() -> EntityAttributes.GENERIC_ARMOR_TOUGHNESS, value -> {
			return Text.translatable("playerex.gui.page.combat.text.armor_toughness",
					ClientUtil.FORMATTING_2.format(value)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.armor_toughness[0]"))
					.formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.armor_toughness[1]"))
					.formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 114));
		COMPONENTS.add(RenderComponent.of(() -> EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, value -> {
			double disp = ClientUtil.displayValue(() -> EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, value);
			return Text.translatable("playerex.gui.page.combat.text.knockback_resistance",
					ClientUtil.FORMATTING_2.format(disp)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			double disp = 100.0D * value;

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.knockback_resistance",
					ClientUtil.FORMATTING_2.format(disp))).formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 125));
		COMPONENTS.add(RenderComponent.of(ExAPI.EVASION, value -> {
			double disp = ClientUtil.displayValue(ExAPI.EVASION, value);
			return Text.translatable("playerex.gui.page.combat.text.evasion", ClientUtil.FORMATTING_2.format(disp))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.evasion[0]")).formatted(Formatting.GRAY));
			tooltip.add((Text.translatable("playerex.gui.page.combat.tooltip.evasion[1]")).formatted(Formatting.GRAY));

			return tooltip;
		}, 9, 136));

		COMPONENTS.add(RenderComponent.of(entity -> {
			return Text.translatable("playerex.gui.page.attributes.text.movement_speed",
					ClientUtil.FORMATTING_3.format(entity.getMovementSpeed())).formatted(Formatting.DARK_GRAY);
		}, entity -> {
			List<Text> tooltip = new ArrayList<Text>();
			String formatted = ClientUtil.FORMATTING_3.format(20.0D * entity.getMovementSpeed());
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.movement_speed", formatted)
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 32+9));
		COMPONENTS.add(RenderComponent.of(ExAPI.BREAKING_SPEED, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.breaking_speed",
					ClientUtil.FORMATTING_3.format(value)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.breaking_speed")
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 32));
		COMPONENTS.add(RenderComponent.of(ExAPI.REACH_DISTANCE, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.reach_distance",
					ClientUtil.FORMATTING_2.format(4.5F + value)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.reach_distance",
					ClientUtil.FORMATTING_2.format(4.5F + value)).formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 40+100));
		COMPONENTS.add(RenderComponent.of(entity -> {
			String current = ClientUtil.FORMATTING_2.format(entity.getHealth());
			String maximum = ClientUtil.FORMATTING_2.format(entity.getMaxHealth());
			return Text.translatable("playerex.gui.page.attributes.text.health", current, maximum)
					.formatted(Formatting.DARK_GRAY);
		}, entity -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.health").formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 50));
		COMPONENTS.add(RenderComponent.of(ExAPI.HEALTH_REGENERATION, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.health_regeneration",
					ClientUtil.FORMATTING_3.format(value)).formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.health_regeneration[0]")
					.formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.health_regeneration[1]")
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 60));
		COMPONENTS.add(RenderComponent.of(ExAPI.HEAL_AMPLIFICATION, value -> {
			String displ = ClientUtil.FORMATTING_2.format(ClientUtil.displayValue(ExAPI.HEAL_AMPLIFICATION, value))
					.formatted(Formatting.DARK_GRAY);
			return Text.translatable("playerex.gui.page.attributes.text.heal_amplification", displ)
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.heal_amplification[0]")
					.formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.heal_amplification[1]")
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 70));
		COMPONENTS.add(RenderComponent.of(ExAPI.FIRE_RESISTANCE, value -> {
			String displ = ClientUtil.FORMATTING_2.format(ClientUtil.displayValue(ExAPI.FIRE_RESISTANCE, value));
			return Text.translatable("playerex.gui.page.attributes.text.fire_resistance", displ)
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			String displ = ClientUtil.FORMATTING_2.format(100.0F * value);
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.fire_resistance", displ)
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 80));
		COMPONENTS.add(RenderComponent.of(ExAPI.FREEZE_RESISTANCE, value -> {
			String displ = ClientUtil.FORMATTING_2.format(ClientUtil.displayValue(ExAPI.FREEZE_RESISTANCE, value));
			return Text.translatable("playerex.gui.page.attributes.text.freeze_resistance", displ)
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			String displ = ClientUtil.FORMATTING_2.format(100.0F * value);
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.freeze_resistance", displ)
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 90));
		COMPONENTS.add(RenderComponent.of(ExAPI.LIGHTNING_RESISTANCE, value -> {
			String displ = ClientUtil.FORMATTING_2.format(ClientUtil.displayValue(ExAPI.LIGHTNING_RESISTANCE, value));
			return Text.translatable("playerex.gui.page.attributes.text.lightning_resistance", displ)
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			String displ = ClientUtil.FORMATTING_2.format(100.0F * value);
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.lightning_resistance", displ)
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 100));
		COMPONENTS.add(RenderComponent.of(ExAPI.POISON_RESISTANCE, value -> {
			String displ = ClientUtil.FORMATTING_2.format(ClientUtil.displayValue(ExAPI.POISON_RESISTANCE, value));
			return Text.translatable("playerex.gui.page.attributes.text.poison_resistance", displ)
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			String displ = ClientUtil.FORMATTING_2.format(100.0F * value);
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.poison_resistance", displ)
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 110));
		COMPONENTS.add(RenderComponent.of(ExAPI.WITHER_RESISTANCE, value -> {
			String displ = ClientUtil.FORMATTING_2.format(ClientUtil.displayValue(ExAPI.WITHER_RESISTANCE, value))
					.formatted(Formatting.DARK_GRAY);
			return Text.translatable("playerex.gui.page.attributes.text.wither_resistance", displ);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			String displ = ClientUtil.FORMATTING_2.format(100.0F * value);
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.wither_resistance", displ)
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 105, 120));

		/*
		COMPONENTS.add(RenderComponent.of(ExAPI.LIFESTEAL, value -> {
			double disp = ClientUtil.displayValue(ExAPI.LIFESTEAL, value);
			return Text.translatable("playerex.gui.page.combat.text.lifesteal", ClientUtil.FORMATTING_2.format(disp))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();

			tooltip.add(
					(Text.translatable("playerex.gui.page.combat.tooltip.lifesteal[0]")).formatted(Formatting.GRAY));
			tooltip.add(
					(Text.translatable("playerex.gui.page.combat.tooltip.lifesteal[1]")).formatted(Formatting.GRAY));

			return tooltip;
		}, 105, 92));
		 */
	}
}