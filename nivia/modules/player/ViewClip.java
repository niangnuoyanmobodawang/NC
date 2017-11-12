package nivia.modules.player;

import nivia.modules.Module;
import nivia.modules.Module.Category;
import nivia.utils.Helper;

public class ViewClip extends Module {

    public ViewClip() {
        super("ViewClip", 0, 0, Category.PLAYER, "ViewClip.", new String[] { "ViewClip", "Vcl" }, true);
    }
	
	@Override
	public void onEnable() {
		super.onEnable();
		Helper.ViewClip = true;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		Helper.ViewClip = false;
	}
	
}
