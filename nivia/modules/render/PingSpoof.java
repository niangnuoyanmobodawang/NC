package nivia.modules.render;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import nivia.events.*;
import nivia.events.events.*;
import nivia.managers.PropertyManager;
import nivia.modules.Module;
import nivia.modules.Module.Category;
import nivia.utils.Helper;
import nivia.utils.Logger;

public class PingSpoof extends Module {
    public PropertyManager.DoubleProperty delay = new PropertyManager.DoubleProperty(this, "delay", 1337, 0, 2000, 100);
    public List keepAlives = new ArrayList();
	
    public PingSpoof() {
        super("PingSpoof", 0, 0, Category.EXPLOITS, "PingSpoof.", new String[]{"DI", "di", "DID"} , true);
    }

    
       @EventTarget(Priority.HIGH)
	   public void onEvent(EventPacketSend event) {
        if(event.getPacket() instanceof C00PacketKeepAlive) {
        //	Logger.logChat("test23");
            if(this.keepAlives.contains(event.getPacket())) {
          //  	Logger.logChat("test2");
            	return;
            }

            event.setCancelled(true);
            Packet packet = event.getPacket();
            this.keepAlives.add(event.getPacket());
            (new Thread(() -> {
               try {
                  Thread.sleep((long) delay.getValue());
               } catch (InterruptedException var4) {
                  var4.printStackTrace();
               }
 
               try {
            	//  Logger.logChat("test");
                  Helper.player().sendQueue.addToSendQueue(packet);
               } catch (Exception var3) {
                  ;
               }

               this.keepAlives.remove(packet);
            }, "KeepAlive-Delay")).start();
         }
		
		
	}
    
	@EventTarget
	   public void onEvent(EventTick event) {

	         if(this.delay.getValue() > 1500) {
	            this.delay.setValue(1500);
	            Logger.logChat("不能过高！");
	         } else if(this.delay.getValue() <= 0) {
	        	this.delay.setValue(1);
	            Logger.logChat("不能小于0！");
	         }

	         this.setSuffix(delay.getValue()+"");
	   }

	   
}
