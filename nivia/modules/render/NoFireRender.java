package nivia.modules.render;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventTick;
import nivia.modules.Module;
import nivia.utils.Helper;

public class NoFireRender extends Module {

	public NoFireRender() {
		super("NoFireRender", 0, 0, Category.RENDER, "NoFireRenderNoFireRenders",
				new String[] { "nc", "fxvb", "fxvht" }, false);
	}



	@Override
	public void onDisable() {
		super.onDisable();
	    Helper.nofire = false;
	    
	//	this.mc.gameSettings.gammaSetting = oldGamma;
	//	mc.entityRenderer.updateRenderer();
	//    this.mc.gameSettings.gammaSetting = 100.0F;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
	    Helper.nofire = true;
	//	oldGamma = mc.gameSettings.gammaSetting;
	//	this.mc.gameSettings.gammaSetting = 1000.0F;
	}
}