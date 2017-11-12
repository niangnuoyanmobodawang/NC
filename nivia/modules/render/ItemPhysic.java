package nivia.modules.render;



import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventTick;
import nivia.modules.Module;
import nivia.utils.Helper;

public class ItemPhysic extends Module {

	public ItemPhysic() {
		super("ItemPhysic", 0, 0, Category.RENDER, "MItemPhysicingame brightness",
				new String[] { "fb", "fullb", "fbright" }, false);
	}

	@Override
	public void onDisable() {
		super.onDisable();
	    Helper.ItemPhysic = false;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		Helper.ItemPhysic = true;
	//	oldGamma = mc.gameSettings.gammaSetting;
	//	this.mc.gameSettings.gammaSetting = 1000.0F;
	}
}