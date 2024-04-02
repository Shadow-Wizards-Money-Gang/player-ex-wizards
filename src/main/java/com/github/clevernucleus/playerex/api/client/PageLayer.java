package com.github.clevernucleus.playerex.api.client;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

/**
 * This is a page layer. By registering these with
 * {@link PageRegistry#registerLayer(net.minecraft.util.Identifier, Builder)},
 * one can add to any pre-existing page. It should be noted, this is essentially
 * a fully fledged HandledScreen to offer
 * unlimited rendering utility.
 * 
 * @author CleverNucleus
 *
 */
@Environment(EnvType.CLIENT)
public abstract class PageLayer extends HandledScreen<ScreenHandler> {
	protected final HandledScreen<?> parent;

	/**
	 * 
	 * @param parent
	 * @param handler
	 * @param inventory
	 * @param title
	 */
	public PageLayer(HandledScreen<?> parent, ScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);

		this.parent = parent;
	}

	/**
	 * This is where text and tooltips can be rendered, in that order.
	 */
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
	}

	/**
	 * This is where textures and widgets can be rendered, in that order.
	 */
	@Override
	public void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
	}

	@Override
	public void setTooltip(List<OrderedText> tooltip, TooltipPositioner positioner, boolean focused) {
	}

	@FunctionalInterface
	public interface Builder {

		/**
		 * 
		 * @param parent
		 * @param handler
		 * @param inv
		 * @param text
		 * @return
		 */
		PageLayer build(HandledScreen<?> parent, ScreenHandler handler, PlayerInventory inv, Text text);
	}
}
