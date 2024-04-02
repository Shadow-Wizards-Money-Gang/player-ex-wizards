package com.github.clevernucleus.playerex.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;

@Mixin(InventoryScreen.class)
abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {

    // Constructor for the mixin, called by the superclass constructor
    private InventoryScreenMixin(PlayerEntity player, Text text) {
        super(player.playerScreenHandler, player.getInventory(), text);
    }

    // // Custom method to iterate over TabButtonWidget elements and apply a
    // consumer
    // private void playerex_forEachTab(Consumer<TabButtonWidget> consumer) {
    // // Filter children to get only TabButtonWidget instances and apply the
    // consumer
    // this.children().stream().filter(e -> e instanceof TabButtonWidget)
    // .forEach(e -> consumer.accept((TabButtonWidget) e));
    // }

    // // Inject code at the end of the render method
    // @Inject(method = "render", at = @At("TAIL"))
    // private void playerex_render(DrawContext context, int mouseX, int mouseY,
    // float delta, CallbackInfo info) {
    // // Render tooltips for each TabButtonWidget using the playerex_forEachTab
    // method
    // this.playerex_forEachTab(tab -> tab.renderTooltip(context, mouseX, mouseY,
    // delta));
    // }
}
