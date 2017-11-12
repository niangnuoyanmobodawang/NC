package nivia.modules.player;

import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;
import nivia.events.EventTarget;
import nivia.events.events.EventPacketSend;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;

public class FastEat extends Module {
//	public Property<Boolean> NCP = new Property<Boolean>(this, "NCP", true);
    public FastEat() {
        super("FastEat", 0, 0, Category.PLAYER, "AuctoReat.", new String[] { "autocre", "autocresp" }, true);
    }

    @EventTarget
	public void onPacketsent(EventPacketSend packet) {
		if(mc.thePlayer == null) {
			return;
		}
		if(mc.thePlayer.inventory == null) {
			return;
		}
		if (mc.thePlayer.inventory.getCurrentItem() == null) {
			return;
		}
		if (!(mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood)) {
			return;
		}
		if (mc.gameSettings.keyBindUseItem.getIsKeyPressed()) {
			if (packet.getPacket() instanceof C03PacketPlayer) {
				mc.getNetHandler().getNetworkManager().sendPacketFinal(packet);
			}
		}
	//	super.onPacketSent(packet);
	}
    
}