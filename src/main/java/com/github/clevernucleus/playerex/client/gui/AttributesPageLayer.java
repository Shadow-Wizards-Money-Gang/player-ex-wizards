package com.github.clevernucleus.playerex.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.github.clevernucleus.dataattributes_dc.api.DataAttributesAPI;
import com.github.clevernucleus.dataattributes_dc.api.attribute.IEntityAttribute;
import com.github.clevernucleus.playerex.PlayerEx;
import com.github.clevernucleus.playerex.api.EntityAttributeSupplier;
import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.PacketType;
import com.github.clevernucleus.playerex.api.PlayerData;
import com.github.clevernucleus.playerex.api.client.ClientUtil;
import com.github.clevernucleus.playerex.api.client.PageLayer;
import com.github.clevernucleus.playerex.api.client.RenderComponent;
import com.github.clevernucleus.playerex.client.PlayerExClient;
import com.github.clevernucleus.playerex.client.gui.widget.ScreenButtonWidget;
import com.google.common.collect.ImmutableList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AttributesPageLayer extends PageLayer {
	private static Supplier<Float> scaleX = () -> ExAPI.getConfig().textScaleX();
	private static Supplier<Float> scaleY = () -> ExAPI.getConfig().textScaleY();
	private static float scaleZ = 0.75F;

	private static final List<RenderComponent> COMPONENTS = new ArrayList<RenderComponent>();
	private static final List<Identifier> BUTTON_KEYS = ImmutableList.of(ExAPI.LEVEL.getId(),
			ExAPI.CONSTITUTION.getId(), ExAPI.STRENGTH.getId(), ExAPI.DEXTERITY.getId(), ExAPI.INTELLIGENCE.getId(),
			ExAPI.LUCKINESS.getId(), ExAPI.WISDOM.getId());

	private PlayerData playerData;
	private final Map<Identifier, Integer> buttonDelay = new HashMap<Identifier, Integer>();

	public AttributesPageLayer(HandledScreen<?> parent, ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(parent, handler, inventory, title);
		this.buttonDelay.put(ExAPI.LEVEL.getId(), 0);
		this.buttonDelay.put(ExAPI.CONSTITUTION.getId(), 0);
		this.buttonDelay.put(ExAPI.STRENGTH.getId(), 0);
		this.buttonDelay.put(ExAPI.DEXTERITY.getId(), 0);
		this.buttonDelay.put(ExAPI.INTELLIGENCE.getId(), 0);
		this.buttonDelay.put(ExAPI.LUCKINESS.getId(), 0);
		this.buttonDelay.put(ExAPI.WISDOM.getId(), 0);
	}

	private boolean canRefund() {
		return this.playerData.refundPoints() > 0;
	}

	private void forEachScreenButton(Consumer<ScreenButtonWidget> consumer) {
		this.children().stream().filter(e -> e instanceof ScreenButtonWidget)
				.forEach(e -> consumer.accept((ScreenButtonWidget) e));
	}

	private void buttonPressed(ButtonWidget buttonIn) {
		ScreenButtonWidget button = (ScreenButtonWidget) buttonIn;
		Identifier key = button.key();
		EntityAttributeSupplier attribute = EntityAttributeSupplier.of(key);
		DataAttributesAPI.ifPresent(this.client.player, attribute, (Object) null, amount -> {
			double value = this.canRefund() ? -1.0D : 1.0D;
			ClientUtil.modifyAttributes(this.canRefund() ? PacketType.REFUND : PacketType.SKILL,
					c -> c.accept(attribute, value));
			this.client.player.playSound(PlayerEx.SP_SPEND_SOUND, SoundCategory.NEUTRAL,
					ExAPI.getConfig().skillUpVolume(), ExAPI.getConfig().skillUpPitch());
			return (Object) null;
		});
		// this.buttonDelay.put(key, 40);
	}

	private Tooltip createAttributeTooltip(Identifier key) {
		Identifier lvl = new Identifier("playerex:level");

		if (key.equals(lvl)) {
			int requiredXp = ExAPI.getConfig().requiredXp(this.client.player);
			int currentXp = this.client.player.experienceLevel;
			String progress = "(" + currentXp + "/" + requiredXp + ")";
			Text tooltip = (Text.translatable("playerex.gui.page.attributes.tooltip.button.level", progress))
					.formatted(Formatting.GRAY);

			return Tooltip.of(tooltip);
		}

		Supplier<EntityAttribute> attribute = DataAttributesAPI.getAttribute(key);

		return DataAttributesAPI.ifPresent(this.client.player, attribute, null, value -> {
			Text text = Text.translatable(attribute.get().getTranslationKey());
			String type = "playerex.gui.page.attributes.tooltip.button." + (this.canRefund() ? "refund" : "skill");
			Text tooltip = (Text.translatable(type)).append(text).formatted(Formatting.GRAY);

			return Tooltip.of(tooltip);
		});
	}

	private MutableText narrationButtonTooltip(Supplier<MutableText> textSupplier) {
		return textSupplier.get();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		MatrixStack matrices = context.getMatrices();

		matrices.push();
		matrices.scale(scaleX.get(), scaleY.get(), scaleZ);

		COMPONENTS.forEach(component -> component.renderText(this.client.player, context, this.textRenderer, this.x,
				this.y, scaleX.get(), scaleY.get()));

		/*context.drawText(this.textRenderer,
				Text.translatable("playerex.gui.page.attributes.text.vitality").formatted(Formatting.DARK_GRAY),
				(int) ((this.x + 105) / scaleX.get()), (int) ((this.y + 26) / scaleY.get()), 4210752, false);
		context.drawText(this.textRenderer,
				Text.translatable("playerex.gui.page.attributes.text.resistances").formatted(Formatting.DARK_GRAY),
				(int) ((this.x + 105) / scaleX.get()), (int) ((this.y + 81) / scaleY.get()), 4210752, false);
		 */

		matrices.pop();

		COMPONENTS.forEach(component -> component.drawTooltip(this.client.player, context::drawTooltip, context,
				this.textRenderer, this.x, this.y, mouseX, mouseY, scaleX.get(), scaleY.get()));

	}

	@Override
	public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
		//context.drawTexture(PlayerExClient.GUI, this.x + 9, this.y + 35, 226, 0, 9, 9);
		// context.drawTexture(PlayerExClient.GUI, this.x + 9, this.y + 123, 235, 0, 9, 9);
		//context.drawTexture(PlayerExClient.GUI, this.x + 93, this.y + 24, 226, 9, 9, 9);
		// context.drawTexture(PlayerExClient.GUI, this.x + 93, this.y + 79, 235, 9, 9, 9);

		DataAttributesAPI.ifPresent(this.client.player, ExAPI.BREAKING_SPEED, (Object) null, value -> {
			//context.drawTexture(PlayerExClient.GUI, this.x + 9, this.y + 134, 235, 36, 9, 9);
			return (Object) null;
		});

		DataAttributesAPI.ifPresent(this.client.player, ExAPI.REACH_DISTANCE, (Object) null, value -> {
			//context.drawTexture(PlayerExClient.GUI, this.x + 9, this.y + 145, 244, 0, 9, 9);
			return (Object) null;
		});

		this.forEachScreenButton(button -> {
			Identifier key = button.key();
			Identifier lvl = new Identifier("playerex:level");
			EntityAttributeSupplier attribute = EntityAttributeSupplier.of(key);
			PlayerEntity player = this.client.player;

			DataAttributesAPI.ifPresent(player, attribute, (Object) null, value -> {
				if (BUTTON_KEYS.contains(key)) {
					double max = ((IEntityAttribute) attribute.get()).maxValue();

					if (key.equals(lvl)) {
						button.active = value < max && player.experienceLevel >= ExAPI.getConfig().requiredXp(player);
					} else {
						double modifierValue = this.playerData.get(attribute);

						if (this.canRefund()) {
							button.active = modifierValue >= 1.0D;
						} else {
							button.active = modifierValue < max && this.playerData.skillPoints() >= 1;
						}

						button.alt = this.canRefund();
					}

					int buttonDelay = this.buttonDelay.getOrDefault(key, 0);
					button.active &= (buttonDelay == 0);

					if (buttonDelay > 0) {
						this.buttonDelay.put(key, Math.max(0, buttonDelay - 1));
					}
				}

				return (Object) null;
			});
		});
	}

	@Override
	protected void init() {
		super.init();

		this.playerData = ExAPI.PLAYER_DATA.get(this.client.player);
		var x = 8;
		var increase = 81;

		this.addDrawableChild(createAttributeButton(x + 25, 23+21, BUTTON_KEYS.get(0), btn -> {
			ClientUtil.modifyAttributes(PacketType.LEVEL, c -> c.accept(ExAPI.LEVEL, 1.0D));
			this.buttonDelay.put(((ScreenButtonWidget) btn).key(), 30);
		}, true).setTooltipSupplier(widget -> this.createAttributeTooltip(widget.key())));

		this.addDrawableChild(createAttributeButton(x, 56, BUTTON_KEYS.get(1), this::buttonPressed, false));
		this.addDrawableChild(createAttributeButton(x + increase, 56, BUTTON_KEYS.get(2), this::buttonPressed, false));
		this.addDrawableChild(createAttributeButton(x, 89, BUTTON_KEYS.get(3), this::buttonPressed, false));
		this.addDrawableChild(createAttributeButton(x + increase, 89, BUTTON_KEYS.get(4), this::buttonPressed, false));
		this.addDrawableChild(createAttributeButton(x, 122, BUTTON_KEYS.get(5), this::buttonPressed, false));
		this.addDrawableChild(createAttributeButton(x + increase, 122, BUTTON_KEYS.get(6), this::buttonPressed, false));

	}

	private ScreenButtonWidget createAttributeButton(int x, int y, Identifier key,
			ButtonWidget.PressAction pressAction, boolean lvlButton) {
		var button = new ScreenButtonWidget(this.parent, x, y, 204, 0, lvlButton ? 11 : 32, lvlButton ? 10 : 32, key, pressAction,
				this::narrationButtonTooltip);

		button.setTooltip(this.createAttributeTooltip(key));
		button.levelButton = lvlButton;
		return button;
	}

	static {
		COMPONENTS.add(RenderComponent.of(ExAPI.LEVEL, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.level", Math.round(value))
					.formatted(Formatting.DARK_GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.level[0]").formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.level[1]").formatted(Formatting.GRAY));
			tooltip.add(Text.empty());
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.level[2]",
					ExAPI.getConfig().skillPointsPerLevelUp()).formatted(Formatting.GRAY));
			return tooltip;
		}, 21+30, 26));

		COMPONENTS.add(RenderComponent.of(entity -> {
			return Text.translatable("playerex.gui.page.attributes.text.skill_points",
					ExAPI.PLAYER_DATA.get(entity).skillPoints()).formatted(Formatting.DARK_GRAY);
		}, entity -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.skill_points[0]")
					.formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("playerex.gui.page.attributes.tooltip.skill_points[1]")
					.formatted(Formatting.GRAY));
			return tooltip;
		}, 21+30, 37));

		int currentX = 15;
		int currentY = 56+5;
		COMPONENTS.add(RenderComponent.of(ExAPI.CONSTITUTION, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.constitution").formatted(Formatting.WHITE);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.CONSTITUTION.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.CONSTITUTION);
			return tooltip;
		}, currentX + 25, currentY + 5));
		COMPONENTS.add(RenderComponent.of(ExAPI.CONSTITUTION, value -> {
			return Text.literal(String.valueOf(Math.round(value)) + "/" + 25)
					.formatted(Formatting.GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.CONSTITUTION.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.CONSTITUTION);
			return tooltip;
		}, currentX + 25, currentY + 14));

		currentX = 95;
		COMPONENTS.add(RenderComponent.of(ExAPI.STRENGTH, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.strength").formatted(Formatting.WHITE);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.STRENGTH.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.STRENGTH);
			return tooltip;
		}, currentX + 25, currentY + 5));
		COMPONENTS.add(RenderComponent.of(ExAPI.STRENGTH, value -> {
			return Text.literal(String.valueOf(Math.round(value)) + "/" + 25)
					.formatted(Formatting.GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.STRENGTH.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.STRENGTH);
			return tooltip;
		}, currentX + 25, currentY + 14));

		currentY = 89+5;
		currentX = 15;

		COMPONENTS.add(RenderComponent.of(ExAPI.DEXTERITY, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.dexterity", Math.round(value)).formatted(Formatting.WHITE);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.DEXTERITY.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.DEXTERITY);
			return tooltip;
		}, currentX + 25, currentY + 5));
		COMPONENTS.add(RenderComponent.of(ExAPI.DEXTERITY, value -> {
			return Text.literal(String.valueOf(Math.round(value)) + "/" + 25)
					.formatted(Formatting.GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.DEXTERITY.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.DEXTERITY);
			return tooltip;
		}, currentX + 25, currentY + 14));

		currentX = 95;
		COMPONENTS.add(RenderComponent.of(ExAPI.INTELLIGENCE, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.intelligence").formatted(Formatting.WHITE);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.INTELLIGENCE.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.INTELLIGENCE);
			return tooltip;
		}, currentX + 25, currentY + 5));
		COMPONENTS.add(RenderComponent.of(ExAPI.INTELLIGENCE, value -> {
			return Text.literal(String.valueOf(Math.round(value)) + "/" + 25)
					.formatted(Formatting.GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.INTELLIGENCE.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.INTELLIGENCE);
			return tooltip;
		}, currentX + 25, currentY + 14));


		currentY = 122+5;
		currentX = 15;

		COMPONENTS.add(RenderComponent.of(ExAPI.LUCKINESS, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.luckiness", Math.round(value)).formatted(Formatting.WHITE);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.LUCKINESS.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.LUCKINESS);
			return tooltip;
		}, currentX + 25, currentY + 5));
		COMPONENTS.add(RenderComponent.of(ExAPI.LUCKINESS, value -> {
			return Text.literal(String.valueOf(Math.round(value)) + "/" + 25)
					.formatted(Formatting.GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.LUCKINESS.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.LUCKINESS);
			return tooltip;
		}, currentX + 25, currentY + 14));

		currentX = 95;
		COMPONENTS.add(RenderComponent.of(ExAPI.WISDOM, value -> {
			return Text.translatable("playerex.gui.page.attributes.text.wisdom").formatted(Formatting.WHITE);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.WISDOM.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.WISDOM);
			return tooltip;
		}, currentX + 25, currentY + 5));

		COMPONENTS.add(RenderComponent.of(ExAPI.WISDOM, value -> {
			return Text.literal(String.valueOf(Math.round(value)) + "/" + 25)
					.formatted(Formatting.GRAY);
		}, value -> {
			List<Text> tooltip = new ArrayList<Text>();
			tooltip.add(Text.translatable(ExAPI.WISDOM.get().getTranslationKey()).formatted(Formatting.GRAY));
			tooltip.add(Text.empty());

			ClientUtil.appendChildrenToTooltip(tooltip, ExAPI.WISDOM);
			return tooltip;
		}, currentX + 25, currentY + 14));
	}
}
