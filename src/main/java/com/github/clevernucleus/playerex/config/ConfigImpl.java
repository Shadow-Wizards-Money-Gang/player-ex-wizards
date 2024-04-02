package com.github.clevernucleus.playerex.config;

import com.github.clevernucleus.dataattributes_dc.api.DataAttributesAPI;
import com.github.clevernucleus.playerex.api.ExAPI;
import com.github.clevernucleus.playerex.api.ExConfig;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.entity.player.PlayerEntity;

@Config(name = ExAPI.MODID)
public class ConfigImpl implements ConfigData, ExConfig {
	public static enum Tooltip {
		DEFAULT, VANILLA, PLAYEREX;
	}

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 100)
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected int resetOnDeath = 100;

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected boolean disableAttributesGui = false;

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected boolean showLevelNameplates = true;

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected int skillPointsPerLevelUp = 1;

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected String levelFormula = "stairs(x,0.2,2.4,17,10,25)";

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected int restorativeForceTicks = 600;

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.BoundedDiscrete(min = 101, max = 200)
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected int restorativeForceMultiplier = 110;

	@ConfigEntry.Category(value = "server")
	@ConfigEntry.BoundedDiscrete(min = 1, max = 100)
	@ConfigEntry.Gui.Tooltip(count = 2)
	protected int expNegationFactor = 95;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 150)
	@ConfigEntry.Gui.Tooltip
	private int levelUpVolume = 100;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 150)
	@ConfigEntry.Gui.Tooltip
	private int skillUpVolume = 100;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 200)
	@ConfigEntry.Gui.Tooltip
	private int skillUpPitch = 100;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.Gui.Tooltip
	private boolean disableInventoryTabs = false;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 50)
	@ConfigEntry.Gui.Tooltip
	private int textScaleX = 40;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.BoundedDiscrete(min = 0, max = 50)
	@ConfigEntry.Gui.Tooltip
	private int textScaleY = 40;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.Gui.Tooltip
	private float levelNameplateHeight = 0.3F;

	@ConfigEntry.Category(value = "client")
	@ConfigEntry.Gui.Tooltip
	private Tooltip tooltip = Tooltip.PLAYEREX;

	public void createServerConfig() {
		ConfigServer.INSTANCE.init(this);
	}

	public ConfigServer getServerInstance() {
		return ConfigServer.INSTANCE;
	}

	/** Server & Client */
	public boolean levelNameplate() {
		return ConfigServer.INSTANCE.showLevelNameplates;
	}

	/** Client */
	public Tooltip tooltip() {
		return this.tooltip;
	}

	@Override
	public int resetOnDeath() {
		return ConfigServer.INSTANCE.resetOnDeath;
	}

	@Override
	public boolean isAttributesGuiDisabled() {
		return ConfigServer.INSTANCE.disableAttributesGui;
	}

	@Override
	public int skillPointsPerLevelUp() {
		return ConfigServer.INSTANCE.skillPointsPerLevelUp;
	}

	@Override
	public int requiredXp(final PlayerEntity player) {
		return DataAttributesAPI.ifPresent(player, ExAPI.LEVEL, 1, ConfigServer.INSTANCE::level);
	}

	@Override
	public int restorativeForceTicks() {
		return ConfigServer.INSTANCE.restorativeForceTicks;
	}

	@Override
	public float restorativeForceMultiplier() {
		return (float) ConfigServer.INSTANCE.restorativeForceMultiplier * 0.01F;
	}

	@Override
	public float expNegationFactor() {
		return (float) ConfigServer.INSTANCE.expNegationFactor * 0.01F;
	}

	@Override
	public float levelUpVolume() {
		return this.levelUpVolume * 0.01F;
	}

	@Override
	public float skillUpVolume() {
		return this.skillUpVolume * 0.01F;
	}

	@Override
	public float skillUpPitch() {
		return this.skillUpPitch * 0.01F;
	}

	@Override
	public boolean disableInventoryTabs() {
		return this.disableInventoryTabs;
	}

	@Override
	public float textScaleX() {
		return (this.textScaleX + 25) * 0.01F;
	}

	@Override
	public float textScaleY() {
		return (this.textScaleY + 25) * 0.01F;
	}

	@Override
	public float levelNameplateHeight() {
		return this.levelNameplateHeight;
	}
}
