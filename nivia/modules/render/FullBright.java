package nivia.modules.render;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventTick;
import nivia.modules.Module;
import nivia.utils.Helper;

public class FullBright extends Module {

	public FullBright() {
		super("Fullbright", 0, 0, Category.RENDER, "Maxes out ingame brightness",
				new String[] { "fb", "fullb", "fbright" }, false);
	}

	float oldGamma;

    @EventTarget(Priority.LOWEST)
	public void onTick(EventTick tick) {
		
        if (mc.thePlayer.isPotionActive(Potion.blindness.getId())) {
            mc.thePlayer.removePotionEffect(Potion.blindness.getId());
         }
        WorldClient world = Helper.world();
        world.getWorldInfo().setRaining(true);
        world.setRainStrength(0.0F);
	     mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.getId(), 5200, 2));
		
	}

	@Override
	public void onDisable() {
		super.onDisable();
	    mc.thePlayer.removePotionEffect(Potion.nightVision.getId());
	    
	//	this.mc.gameSettings.gammaSetting = oldGamma;
	//	mc.entityRenderer.updateRenderer();
	//    this.mc.gameSettings.gammaSetting = 100.0F;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		
	//	oldGamma = mc.gameSettings.gammaSetting;
	//	this.mc.gameSettings.gammaSetting = 1000.0F;
	}
}
