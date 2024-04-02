package com.github.clevernucleus.playerex.client.gui.widget;

import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.client.gui.ExScreenData;
import com.github.clevernucleus.playerex.client.gui.Page;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TabButtonWidget extends ButtonWidget {
	private static final Identifier TABS = new Identifier(ExAPI.MODID, "textures/gui/tab.png");
	private HandledScreen<?> parent;
	private Page page;
	private int index, dx, dy;
	private final float scale = 1.0F / 16.0F;

	public TabButtonWidget(HandledScreen<?> parent, Page page, int index, int x, int y, boolean startingState,
			PressAction onPress) {
		super(x, y, 28, 32, Text.empty(), onPress, DEFAULT_NARRATION_SUPPLIER);

		this.parent = parent;
		this.page = page;
		this.index = index;
		this.dx = x;
		this.dy = y;
		this.active = startingState;
	}

	private boolean isTopRow() {
		return this.index < 6;
	}

	public int index() {
		return this.index;
	}

	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		ExScreenData handledScreen = (ExScreenData) this.parent;
		this.setX(handledScreen.getX() + this.dx);
		this.setY(handledScreen.getY() + this.dy);

		RenderSystem.disableDepthTest();

		int u = (this.index % 6) * this.width;
		int v = this.isTopRow() ? 0 : (2 * this.height);
		int w = this.isTopRow() ? 9 : 7;

		if (!this.active) {
			v += this.height;
		}

		context.drawTexture(TABS, this.getX(), this.getY(), u, v, this.width, this.height);

		MatrixStack matrices = context.getMatrices();

		matrices.push();
		matrices.scale(this.scale, this.scale, 0.75F);

		context.drawTexture(this.page.icon(), (int) ((this.getX() + 6) / this.scale),
				(int) ((this.getY() + w) / this.scale), 0, 0, 256, 256);

		matrices.pop();
		RenderSystem.enableDepthTest();
	}
}