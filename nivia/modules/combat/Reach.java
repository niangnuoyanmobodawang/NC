package nivia.modules.combat;


import net.minecraft.network.play.client.C02PacketUseEntity;
import nivia.events.EventTarget;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPreMotionUpdates;
import nivia.modules.Module;
import nivia.utils.utils.NewValues;

public class Reach extends Module{

	public Reach() {
		super("Reach", 0, 0, Category.COMBAT, "", new String[]{""});
	}
	public static NewValues range = new NewValues("reach_Range", 3.5f, 3f, 6f, 0.1f);
	public static NewValues combo = new NewValues("reach_Combo Only", true);
	public static int ohmama = 0;
	@EventTarget
	public void onEvent(EventPreMotionUpdates event){
		range.min = 3f;
		if(ohmama > 0){
			ohmama--;
		}
	}
	@EventTarget
	public void onPacket(EventPacketSend event){
		if(event.getPacket() instanceof C02PacketUseEntity){
			ohmama = 10;
		}
	}
	public static boolean isComboing(){
		return ohmama > 0;
	}
}
