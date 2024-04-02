package com.github.clevernucleus.playerex.mixin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.github.clevernucleus.playerex.client.PageRegistryImpl;
import com.github.clevernucleus.playerex.client.PlayerExClient;
import com.github.clevernucleus.playerex.client.gui.ExScreenData;
import com.github.clevernucleus.playerex.client.gui.Page;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Mixin(AbstractInventoryScreen.class)
abstract class AbstractInventoryScreenMixin<T extends ScreenHandler> extends HandledScreen<T> implements ExScreenData {

    @Unique
    private List<Page> playerex_pages = new ArrayList<Page>();

    // Constructor for the mixin, called by the superclass constructor
    private AbstractInventoryScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    // Method to filter out specific pages based on their identifiers
    private boolean filter(Map.Entry<Identifier, Supplier<Page>> entry) {
        Identifier key = entry.getKey();
        // Exclude ATTRIBUTES_PAGE and COMBAT_PAGE
        return !(key.equals(PlayerExClient.ATTRIBUTES_PAGE) || key.equals(PlayerExClient.COMBAT_PAGE));
    }

    // Inject code at the end of the superclass constructor
    @Inject(method = "<init>", at = @At("TAIL"))
    private void playerex_init(T screenHandler, PlayerInventory playerInventory, Text text, CallbackInfo info) {
        // Add ATTRIBUTES_PAGE and COMBAT_PAGE at specific indices
        this.playerex_pages.add(0, PageRegistryImpl.findPage(PlayerExClient.ATTRIBUTES_PAGE));
        this.playerex_pages.add(1, PageRegistryImpl.findPage(PlayerExClient.COMBAT_PAGE));
        
        // Add other pages (excluding ATTRIBUTES_PAGE and COMBAT_PAGE) from the PageRegistry
        PageRegistryImpl.pages().entrySet().stream().filter(this::filter).map(Map.Entry::getValue).forEach(page -> this.playerex_pages.add(page.get()));
    }

    // Implementation of the ExScreenData interface method
    @Override
    public int getX() {
        return this.x;
    }

    // Implementation of the ExScreenData interface method
    @Override
    public int getY() {
        return this.y;
    }

    // Implementation of the ExScreenData interface method
    @Override
    public List<Page> pages() {
        return this.playerex_pages;
    }
}
