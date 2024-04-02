package com.github.clevernucleus.playerex.client.gui.widget;

import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.client.PlayerExClient;
import com.github.clevernucleus.playerex.client.gui.ExScreenData;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class ScreenButtonWidget extends ButtonWidget {
	public static final Identifier EMPTY_KEY = new Identifier("playerex:empty");
	private HandledScreen<?> parent;
	private final Identifier key;
	private int u, v, dx, dy;
	public boolean alt;
	public boolean levelButton;

	@Nullable
	private Function<ScreenButtonWidget, @Nullable Tooltip> tooltipSupplier = null;

	public ScreenButtonWidget(HandledScreen<?> parent, int x, int y, int u, int v, int width, int height,
			Identifier key, PressAction pressAction, NarrationSupplier narrationSupplier) {
		super(x, y, width, height, Text.empty(), pressAction, narrationSupplier);

		this.parent = parent;
		this.key = key;
		this.u = u;
		this.v = v;
		this.dx = x;
		this.dy = y;
		this.alt = false;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (tooltipSupplier != null && this.visible)
			this.setTooltip(tooltipSupplier.apply(this));

		super.render(context, mouseX, mouseY, delta);
	}

	public ScreenButtonWidget setTooltipSupplier(Function<ScreenButtonWidget, @Nullable Tooltip> tooltipSupplier) {
		this.tooltipSupplier = tooltipSupplier;

		return this;
	}

	public Identifier key() {
		return this.key;
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		ExScreenData handledScreen = (ExScreenData) this.parent;
		this.setX(handledScreen.getX() + this.dx);
		this.setY(handledScreen.getY() + this.dy);

		RenderSystem.setShaderTexture(0, PlayerExClient.GUI);
		RenderSystem.disableDepthTest();

		if(levelButton) {
			int i = this.u;
			int j = this.v;

			if (this.alt) {
				i += this.width;
			}

			if (this.active) {
				if (this.isHovered()) {
					j += this.height;
				}
			} else {
				j += (2 * this.height);
			}

			context.drawTexture(PlayerExClient.DEFAULT, this.getX()+15, this.getY()+1, 0, 231, 80, 8);
			context.drawTexture(PlayerExClient.GUI, this.getX(), this.getY(), i, j, this.width, this.height);
        } else {
			context.drawTexture(PlayerExClient.DEFAULT, this.getX(), this.getY(), 79, 166, 79, 32);
			context.drawTexture(PlayerExClient.BUTTONS, this.getX()+7, this.getY()+7, 220, 202, 18, 18);
		}


		RenderSystem.enableDepthTest();
	}

}
