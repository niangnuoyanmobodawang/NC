package nivia.modules.player;

import net.minecraft.block.BlockCactus;
import net.minecraft.util.AxisAlignedBB;
import nivia.events.Event;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventBoundingBox;
import nivia.events.events.EventDeath;
import nivia.modules.Module;
import nivia.modules.Module.Category;

public class AutoRespawn extends Module {
 
    public AutoRespawn() {
        super("AutoRespawn", 0, 0, Category.PLAYER, "AutoRespawn.", new String[] { "autore", "autoresp" }, true);
    }
    
	@EventTarget(Priority.LOWEST)
    public void onEvent(Event event) {
        if (mc.thePlayer.isDead) 
           mc.thePlayer.respawnPlayer();
     }
    
}
