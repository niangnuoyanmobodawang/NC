package nivia.modules.miscellanous;

import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import nivia.gui.chod.ChodsGui;
import nivia.managers.PropertyManager.Property;

import org.lwjgl.input.Keyboard;

import nivia.Pandora;
import nivia.events.EventTarget;
import nivia.events.events.EventPreMotionUpdates;
import nivia.gui.aclickgui.GuiAPX;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;

import java.util.Objects;

public class InventoryMove extends Module {
	public InventoryMove() {
		super("InventoryMove", 0, 0x005C00, Category.MISCELLANEOUS, "Makes you able to move while displaying certain screens.",
				new String[] { "invmove", "imove", "im" }, true);
	}
	private Property<Boolean> bypass = new Property<>(this, "Bypass", false);

	@Override
	public void onEnable() {
		super.onEnable();
		Helper.invmove = true;
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		Helper.invmove = false;
	}
	
    @EventTarget
    public void onPre(EventPreMotionUpdates e) {

        if (bypass.value && mc.thePlayer != null && ((mc.currentScreen instanceof GuiInventory) || (Helper.mc().currentScreen instanceof GuiContainer))) {
        	mc.timer.timerSpeed = 0.902f;
        	mc.thePlayer.setSprinting(false);
        }
    }
	/*
	@EventTarget
	public void call(EventPreMotionUpdates event) {
        boolean wasSprinting = mc.thePlayer.isSprinting();
	      KeyBinding[] moveKeys = { Helper.mc().gameSettings.keyBindRight, Helper.mc().gameSettings.keyBindLeft, Helper.mc().gameSettings.keyBindBack, Helper.mc().gameSettings.keyBindForward, Helper.mc().gameSettings.keyBindJump, Helper.mc().gameSettings.keyBindSprint};
	      if ((Helper.mc().currentScreen instanceof GuiContainer) || (Helper.mc().currentScreen instanceof ChodsGui) && (Helper.mc().currentScreen instanceof GuiAPX ) && !mc.playerController.isInCreativeMode()) {
	    	  if (Keyboard.isKeyDown(200))
	                mc.thePlayer.rotationPitch -= 2.0f;	            
	            if (Keyboard.isKeyDown(208))
	                mc.thePlayer.rotationPitch += 2.0f;	            
	            if (Keyboard.isKeyDown(203)) 
	                mc.thePlayer.rotationYaw -= 3.0f;	            
	            if (Keyboard.isKeyDown(205)) 
	                mc.thePlayer.rotationYaw += 3.0f;	
	      }
	      if ((Helper.mc().currentScreen instanceof GuiContainer ) || (Helper.mc().currentScreen instanceof GuiAPX ) || (Helper.mc().currentScreen instanceof ChodsGui) ||  Helper.mc().currentScreen instanceof GuiGameOver && !mc.playerController.isInCreativeMode()) {
	        for (KeyBinding key : moveKeys) {
	          key.pressed = Keyboard.isKeyDown(key.getKeyCode());
                if(key.pressed)
                mc.thePlayer.setSprinting(true);
	        }
	      } else if (Objects.isNull(Helper.mc().currentScreen)) {
	        for (KeyBinding bind : moveKeys) {
	          if (!Keyboard.isKeyDown(bind.getKeyCode())) {
	            KeyBinding.setKeyBindState(bind.getKeyCode(), false);
	          }
	        }
	      }
		}*/
}
