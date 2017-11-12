package nivia.modules.player;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventPacketSend;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;

public class Timer extends Module {
  //  public PropertyManager.DoubleProperty delay = new PropertyManager.DoubleProperty(this, "delay", 1337, 0, 2000, 100);
    public List keepAlives = new ArrayList();
    public PropertyManager.DoubleProperty delay = new PropertyManager.DoubleProperty(this, "Pingdelay", 450, 0, 1500, 10);   
	public DoubleProperty low = new DoubleProperty(this, "MinSpeed", 0.02, 0.01, 1);
    public DoubleProperty max = new DoubleProperty(this, "MaxSpeed", 0.033, 0.01, 1);
    public Property<Boolean> C03 = new Property<Boolean>(this, "C03Raycast", true);
    
    public Timer() {
        super("Timer", Keyboard.CHAR_NONE, 0xE6B800, Category.PLAYER, "Change timerino m8erino",
                new String[] { "tim", "slowmo", "ti" }, false);
    }
    
    public PropertyManager.DoubleProperty timerino = new PropertyManager.DoubleProperty(this, "Timerino", 0.1, 0.035, 20, 0.1);
   @EventTarget
    public void onPre(EventPreMotionUpdates event) {
    	  float min = (float) low.getValue();
    	  float max2 = (float) max.getValue();
    	  float floatBounded = min + new Random().nextFloat() * (max2 - min);
    	if (Helper.playerUtils().MovementInput())
    		mc.timer.timerSpeed = (float) timerino.getValue() + floatBounded;
    	else
    		mc.timer.timerSpeed = 1.0F;
    }
  
       @EventTarget(Priority.HIGH)
	   public void onEvent(EventPacketSend event) {
		  if(!C03.value)
			  return;
       if(event.getPacket() instanceof C00PacketKeepAlive) {
           if(this.keepAlives.contains(event.getPacket())) {
              return;
           }

           event.setCancelled(true);
           Packet packet = event.getPacket();
           this.keepAlives.add(event.getPacket());
           (new Thread(() -> {
              try {
            //	  Logger.logChat("xx");
                 Thread.sleep((long) ((long) timerino.getValue()+delay.getValue()));
              } catch (InterruptedException var4) {
                 var4.printStackTrace();
              }

              try {
                 Helper.player().sendQueue.addToSendQueue(packet);
              } catch (Exception var3) {
                 ;
              }

              this.keepAlives.remove(packet);
           }, "KeepAlive-Delay")).start();
        }
		
		
	}
   
    
    @Override
    protected void onEnable() {
        super.onEnable();
        if(!(low.getValue()<max.getValue())){
        	low.setValue(0.01);
            max.setValue(0.02);
        }
        Logger.logChat("使用-timer lowspeed/maxspeed 来设置");
    }
    
    
    @Override
    protected void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0F;
        keepAlives.clear();
    }
    
    protected void addCommand() {
        Pandora.getCommandManager().cmds.add(new Command("Timer", "Manages timer values",
                Logger.LogExecutionFail("Option, Options:", new String[] { "delay","minspeed","maxspeed" }), "tim", "slowmo", "ti") {
            @Override
            public void execute(String commandName, String[] arguments) {
                String message = arguments[1];
                switch (message.toLowerCase()) {
                    case "timer":
                    case "delay":
                    case "t":
                    case "d":
                        try {
                            String message2 = arguments[2];
                            Double d = Double.parseDouble(message2);
                            timerino.setValue(d);
                            Logger.logSetMessage("Timer", "Delay", timerino);
                        } catch (Exception e) {
                            Logger.LogExecutionFail("Value!");
                        }
                        break;
                    case "lowspeed":
                    case "minspeed":
                    try {
                        String message2 = arguments[2];
                        Double d = Double.parseDouble(message2);
                        low.setValue(d);
                        Logger.logSetMessage("Timer", "low", low);
                    } catch (Exception e) {
                        Logger.LogExecutionFail("Value!");
                    }
                    break;
                    case "maxspeed":
                        try {
                            String message2 = arguments[2];
                            Double d = Double.parseDouble(message2);
                            max.setValue(d);
                            Logger.logSetMessage("Timer", "max", low);
                        } catch (Exception e) {
                            Logger.LogExecutionFail("Value!");
                        }
                        break;
                    case "values":
                    case "actual":
                        logValues();
                        break;
                    default:
                        Logger.logChat(this.getError());
                        break;
                }
            }
        });
    }
}
