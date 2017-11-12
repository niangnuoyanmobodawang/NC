package nivia.modules.movement;

import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.events.EventMove;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.modules.Module;
import nivia.utils.Helper;

public class FastLadder extends Module {
	public FastLadder() {
		super("FastLadder", 0, 0, Category.MOVEMENT, "fastla", new String[] { "fl", "fastl" }, true);
	}

    public DoubleProperty FPS = new DoubleProperty(this, "Speed", 0.17, 0.08, 0.29 , 0.01);
 
    @EventTarget
    private void onMove(final EventMove event) {
        mc.timer.timerSpeed = 1.0f;
        if (event.getMotionY() > 0.0 && Helper.player().isOnLadder()) {
            event.setMotionY(FPS.getValue());
        }
    }
}

