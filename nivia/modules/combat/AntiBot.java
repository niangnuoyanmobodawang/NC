package nivia.modules.combat;


import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

import org.lwjgl.input.Keyboard;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.EventPreMotionUpdates;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.security.InvokeDynamics;
import nivia.security.StringEncryption;
import nivia.utils.Helper;
import nivia.utils.Logger;
 

public class AntiBot extends Module {
    private Property<DetectionMode> detectionMode = new Property<DetectionMode>(this, "Mode", DetectionMode.HYPIXEL);
    private Property<Boolean> autoDetection = new Property<>(this, "Auto Detection", true);
    public Property<Boolean> Haxorw = new Property<Boolean>(this, "HackerWarning", true);
    public Property<Boolean> autoWDR = new Property<Boolean>(this, "AutoWDR", true);
    public DoubleProperty nofallv = new DoubleProperty(this, "NoFallValue", 6, 1, 200);
    public DoubleProperty speedV = new DoubleProperty(this, "SpeedValue", 10, 1, 200);
    public DoubleProperty flyV = new DoubleProperty(this, "FlyValue", 14, 1, 200);
    public DoubleProperty tick = new DoubleProperty(this, "LivingTime", 100, 60, 900);

    
    
    private int bufferNoFall;
    private int bufferFlight;
    private int bufferSpeed;
    private int buffernodown;
    
    
	@Override
	public void onDisable() {
		super.onDisable();
        this.bufferNoFall = 0;
        this.bufferFlight = 0;
	}

	
	@Override
	public void onEnable() {
		super.onEnable();
        this.bufferNoFall = 0;
        this.bufferFlight = 0;
        if (this.detectionMode.value == DetectionMode.HYPIXEL) {
        	KillAura.getAura().invisibles.value = false;
        	if(KillAura.getAura().ticks.getValue()<50)
        		KillAura.getAura().ticks.setValue(51);
        }
	}
	
	  public static float getDistanceToGround(final Entity e) {
	        if (Helper.mc().thePlayer.isCollidedVertically) {
	            return 0.0f;
	        }
	        float a = (float)e.posY;
	        while (a > 0.0f) {
	            final int[] stairs = { 53, 67, 108, 109, 114, 128, 134, 135, 136, 156, 163, 164, 180 };
	            final int[] exemptIds = { 6, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76, 77, 83, 92, 93, 94, 104, 105, 106, 115, 119, 131, 132, 143, 147, 148, 149, 150, 157, 171, 175, 176, 177 };
	            final Block block = Helper.mc().theWorld.getBlockState(new BlockPos(e.posX, a - 1.0f, e.posZ)).getBlock();
	            if (!(block instanceof BlockAir)) {
	                if (Block.getIdFromBlock(block) == 44 || Block.getIdFromBlock(block) == 126) {
	                    return ((float)(e.posY - a - 0.5) < 0.0f) ? 0.0f : ((float)(e.posY - a - 0.5));
	                }
	                int[] array;
	                for (int length = (array = stairs).length, i = 0; i < length; ++i) {
	                    final int id = array[i];
	                    if (Block.getIdFromBlock(block) == id) {
	                        return ((float)(e.posY - a - 1.0) < 0.0f) ? 0.0f : ((float)(e.posY - a - 1.0));
	                    }
	                }
	                int[] array2;
	                for (int length2 = (array2 = exemptIds).length, j = 0; j < length2; ++j) {
	                    final int id = array2[j];
	                    if (Block.getIdFromBlock(block) == id) {
	                        return ((float)(e.posY - a) < 0.0f) ? 0.0f : ((float)(e.posY - a));
	                    }
	                }
	                return (float)(e.posY - a + block.getBlockBoundsMaxY() - 1.0);
	            }
	            else {
	                --a;
	            }
	        }
	        return 0.0f;
	    }
	
	@SuppressWarnings("unchecked")
    @EventTarget(Priority.LOWEST)
    public void onPreMotion(final EventPreMotionUpdates event) {
		if(!Haxorw.value)
			return;
		mc.theWorld.loadedEntityList.forEach(o -> {
			Entity p = (Entity)o;
            if (p != Helper.player() && !p.isDead && p instanceof EntityPlayer && !p.getName().contains("§") && !p.getName().equals(Helper.player().getName())) {
                if (getDistanceToGround(p) > 4.0f && p.onGround && p.posY < p.prevPosY && !p.isSlient()) {
                    ++this.bufferNoFall;
                }
                if (this.bufferNoFall >= nofallv.getValue()) {
                	if(autoWDR.value) Helper.player().sendChatMessage("/wdr " + p.getName() + " ka speed reach fly antiknockback autoclicker dolphin");
                    Logger.logChat("[HackerFinder] §c"+p.getName()+" 可能开了NoFall！"); 
                    this.bufferNoFall = 0;
                }
                if (p.lastTickPosY < p.posY - 0.4 && !p.isSlient()) {
                    ++this.bufferFlight;
                }
                if (this.bufferFlight >= flyV.getValue()) {
                	if(autoWDR.value) Helper.player().sendChatMessage("/wdr " + p.getName() + " ka speed reach fly antiknockback autoclicker dolphin");
                    Logger.logChat("[HackerFinder] §c"+p.getName()+" 可能开了Fly！"); 
                     this.bufferFlight = 0;
                }
                if (p.lastTickPosX < p.posX - 0.7) {
                    ++this.bufferSpeed;
                }
                if (p.posX > p.lastTickPosX + 0.7) {
                    ++this.bufferSpeed;
                }
                if (p.lastTickPosZ < p.posZ - 0.7) {
                    ++this.bufferSpeed;
                }
                if (p.posX > p.lastTickPosX + 0.7) {
                    ++this.bufferSpeed;
                }
                if (this.bufferSpeed > speedV.getValue()) {
                   if(autoWDR.value) Helper.player().sendChatMessage("/wdr " + p.getName() + " ka speed reach fly antiknockback autoclicker dolphin");
                   Logger.logChat("[HackerFinder] §c"+p.getName()+" 可能开了Speed！"); 
                   this.bufferSpeed = 0;
                }
            }
        });
    }
   
	
    public AntiBot() {
        super("AntiBot", Keyboard.KEY_NONE, 0xFFFFFFFF, Category.COMBAT, "Remove all entities that are considered bots.", new String[] { "antibots", "nobot", "nobots", "nonpc", "nonpcs" }, true);
    }
 
    @EventTarget(Priority.HIGH)
    public void preMotionUpdates(EventPreMotionUpdates event) {
        this.setSuffix(this.detectionMode.value.toString());
        if (this.autoDetection.value) {
            if (Helper.mc().getCurrentServerData().serverIP.toLowerCase().contains("HYPIXEL")) {
                this.detectionMode.value = DetectionMode.HYPIXEL;
            }
            if (Helper.mc().getCurrentServerData().serverIP.toLowerCase().contains("mineplex.com")) {
                this.detectionMode.value = DetectionMode.MINEPLEX;
            }
        }
        if (this.detectionMode.value == DetectionMode.NULL) {
            for (Object object : Helper.world().playerEntities) {
                EntityPlayer entityPlayer = (EntityPlayer) object;
                if (entityPlayer != null && entityPlayer != Helper.player()) {
                    try {
                        NetworkPlayerInfo networkPlayerInfo = Helper.mc().getNetHandler().func_175102_a(entityPlayer.getUniqueID());
                        if (networkPlayerInfo.getGameType().isSurvivalOrAdventure() || networkPlayerInfo.getGameType().isCreative()) {
                            // empty block
                        }
                    } catch (Exception e) {
                        Logger.logChat("你被/wdr了!");
                        Logger.logChat("你被/wdr了!!");
                    	Helper.world().removeEntity(entityPlayer);
                    }
                }
            }
        }
        if (this.detectionMode.value == DetectionMode.HYPIXEL) {
        //	KillAura.getAura().rnd.setValue(0);
            for (Object object : Helper.world().playerEntities) {
                EntityPlayer entityPlayer = (EntityPlayer) object;
            //    Entity entity = (Entity) object;
                if (entityPlayer != null && entityPlayer != Helper.player()) {
                	// String str = entityPlayer.getDisplayName().getFormattedText();
                   //  if ((!str.equalsIgnoreCase(entityPlayer.getName() + "\u00a7r") && !str.contains("NPC") || mc.thePlayer.getDisplayName().getFormattedText().equalsIgnoreCase(mc.thePlayer.getName() + "\u00a7r"))
                        
                    if (entityPlayer.getDisplayName().getFormattedText().equalsIgnoreCase(entityPlayer.getName() + "\247r") && !Helper.player().getDisplayName().getFormattedText().equalsIgnoreCase(Helper.player().getName() + "\247r")) {
                    	Helper.world().removeEntity(entityPlayer);
                    }else if(entityPlayer.ticksExisted <= tick.getValue()){
                    	entityPlayer.setInvisible(true);
                    }else if(entityPlayer.ticksExisted >= tick.getValue()){
                    	entityPlayer.setInvisible(false);
                   }
                }
            }
        }
        if (this.detectionMode.value == DetectionMode.MINEPLEX) {
            for (Object object : Helper.world().playerEntities) {
                EntityPlayer entityPlayer = (EntityPlayer) object;
                if (entityPlayer != null && entityPlayer != Helper.player()) {
                    if (entityPlayer.getName().startsWith("Body #")) {
                        Logger.logChat("你被/wdr了!");
                        Logger.logChat("你被/wdr了!!");
                    	Helper.world().removeEntity(entityPlayer);
                    }
                    if (entityPlayer.getMaxHealth() == 20.0f) {
                        Logger.logChat("你被/wdr了!");
                        Logger.logChat("你被/wdr了!!");
                        Helper.world().removeEntity(entityPlayer);
                    }
                }
            }
        }
    }
   
    protected void addCommand() {
        Pandora.getCommandManager().cmds.add(new Command("AntiBot", "Manages antibot values", Logger.LogExecutionFail("Option, Options:", new String[]{ "Null", "HYPIXEL", "Mineplex", "AutoDetection", "Values" }) , "ab") {
            @Override
            public void execute(String commandName, String[] arguments) {
            	Logger.logChat(arguments[1].toLowerCase());
                switch (arguments[1].toLowerCase()) {
                    case "null":
                        detectionMode.value = DetectionMode.NULL;
                        Logger.logSetMessage("AntiBot", "Mode", detectionMode);
                        break;
                    case "HYPIXEL": case "hyp": case "hp":
                        detectionMode.value = DetectionMode.HYPIXEL;
                        Logger.logSetMessage("AntiBot", "Mode", detectionMode);
                        break;
                    case "mineplex": case "mp":
                        detectionMode.value = DetectionMode.MINEPLEX;
                        Logger.logSetMessage("AntiBot", "Mode", detectionMode);
                        break;
                    case "autodetection": case "autodetect": case "autod": case "adetect": case "ad":
                        autoDetection.value = !autoDetection.value;
                        Logger.logToggleMessage("Auto Detection", autoDetection.value);
                        break;
                    case "values": case "actual":
                        logValues();
                        break;
                    default:
                        Logger.logChat(this.getError()+"|"+"||||");
                        Logger.logChat(arguments[1].toLowerCase());
                        System.out.println(arguments[1].toLowerCase());
                        break;
                }
            }
        });
    }
   
    private enum DetectionMode {
    	 NULL, HYPIXEL, MINEPLEX;
    }
}