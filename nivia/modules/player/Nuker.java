package nivia.modules.player;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import nivia.events.EventTarget;
import nivia.events.Priority;
import nivia.events.events.Event3D;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.managers.PropertyManager;
import nivia.managers.PropertyManager.DoubleProperty;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.utils.BlockUtils;
import nivia.utils.utils.Timer;

public class Nuker extends Module {
    public Nuker() {
        super("BedOrNuker", 0, 0x666666, Category.PLAYER, "Mines blocks around you.",
                new String[] { "swalk", "sw" }, true);
    }

    private int posX, posY, posZ;

    
    public DoubleProperty radius = new DoubleProperty(this, "Horizontal Radius", 3, 0, 50);
    public DoubleProperty height = new DoubleProperty(this, "Height Radius", 1, 0, 50);
    public Property<Boolean> rayc = new Property<>(this, "Raycast", true);
    public Property<Boolean> bed = new Property<>(this, "Bed", true);
    public Property<Boolean> bedt = new Property<>(this, "Teleport", false);
    public PropertyManager.Property<Boolean> silent = new PropertyManager.Property<Boolean>(this, "Silent", true);
    private boolean isRunning;
    private boolean canteleport = false;
    private Timer timer = new Timer();



    @EventTarget
    public void onTick(EventTick e){
		if(!bedt.value)
			return;
		if(mc.thePlayer.getHealth()>0 && !canteleport){
			damagePlayerMini();
		}else if(mc.thePlayer.getHealth()<=0)
			canteleport = true;
		if(canteleport && mc.thePlayer.getHealth()>0){
			synchronized(this){
			ArrayList<BlockPos> targets = new ArrayList<BlockPos>();
			int range = 100;
			for(int y = range; y >= -range; y--)
			{
				for(int x = range; x >= -range; x--)
				{
					for(int z = range; z >= -range; z--)
					{
						int posX = (int)(mc.thePlayer.posX + x);
						int posY = (int)(mc.thePlayer.posY + y);
						int posZ = (int)(mc.thePlayer.posZ + z);
						BlockPos pos = new BlockPos(posX, posY, posZ);
						if(getBlock(pos).equals(Blocks.bed)) {
							targets.add(pos);
						}
					}
				}
			}
			for(BlockPos pos : targets) {
				blinkToPosFromPos(mc.thePlayer.getPositionVector(),new Vec3(pos.getX(), pos.getY(), pos.getZ()),2000);
			}
			}
		}
    }
    @EventTarget(Priority.HIGHEST)
    public void onPre(EventPreMotionUpdates e){
        this.isRunning = false;
        int radius = (int) this.radius.getValue();
        int height = (int) this.height.getValue();
        for(int y = height; y >= -height; --y) {
            for(int x = -radius; x < radius; ++x) {
                for(int z = -radius; z < radius; ++z) {
                    this.posX = (int)Math.floor(this.mc.thePlayer.posX) + x;
                    this.posY = (int)Math.floor(this.mc.thePlayer.posY) + y;
                    this.posZ = (int)Math.floor(this.mc.thePlayer.posZ) + z;
                    if(this.mc.thePlayer.getDistanceSq(this.mc.thePlayer.posX + (double)x, this.mc.thePlayer.posY + (double)y, this.mc.thePlayer.posZ + (double)z) <= 16.0D) {
                        Block block = Helper.blockUtils().getBlock(this.posX , this.posY, this.posZ);
                        boolean blockChecks = timer.hasTimeElapsed(50L);
                        Block selected = Helper.blockUtils().getBlock(mc.objectMouseOver.func_178782_a());

                        final int currentID = Block.getIdFromBlock(block);
                        blockChecks = blockChecks && (rayc.value ? Helper.blockUtils().canSeeBlock(this.posX + 0.5F, this.posY + 0.9f, this.posZ + 0.5F) : true) && !(block instanceof BlockAir);
                        blockChecks = blockChecks && (block.getBlockHardness(this.mc.theWorld, BlockPos.ORIGIN) != -1.0F || this.mc.playerController.isInCreativeMode());
                        Boolean isbed = currentID != 0 && currentID == 26;
                        if(blockChecks && (bed.value && isbed) || !bed.value) {
                            
                     
                        	
                            this.isRunning = true;
                            
                            float[] angles = Helper.worldUtils().faceBlock(this.posX + 0.5F, this.posY + 0.9, this.posZ + 0.5F);
                            if(silent.value){

                            	e.setYaw(angles[0]);
                                e.setPitch(angles[1]);
                            } else {
                            	
                                mc.thePlayer.rotationYaw = angles[0];
                                mc.thePlayer.rotationPitch = angles[1];
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
    @EventTarget
    public void onRender(Event3D e) {

    }
    public Block getBlock(BlockPos pos) {
		return mc.theWorld.getBlockState(pos).getBlock();
	}
    
	public void damagePlayerMini() {
		for (int index = 0; index < 70; ++index) {
			mc.thePlayer.sendQueue
					.addToSendQueue((Packet) new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							mc.thePlayer.posY + 0.02, mc.thePlayer.posZ, false));
			mc.thePlayer.sendQueue
					.addToSendQueue((Packet) new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
							mc.thePlayer.posY, mc.thePlayer.posZ, false));
		}
		mc.thePlayer.sendQueue
				.addToSendQueue((Packet) new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
	}
    
	@Override
	public void onEnable() {
		super.onEnable();
		if(!bedt.value)
			return;
	//	if(mc.thePlayer.getHealth()>0)
	//		damagePlayerMini();

	}
	public void blinkToPosFromPos(Vec3 src, Vec3 dest, double maxTP) {
		double range = 0;
		double xDist = src.xCoord - dest.xCoord;
		double yDist = src.yCoord - dest.yCoord;
		double zDist = src.zCoord - dest.zCoord;
		double x1 = src.xCoord;
		double y1 = src.yCoord;
		double z1 = src.zCoord;
		double x2 = dest.xCoord;
		double y2 = dest.yCoord;
		double z2 = dest.zCoord;
		range = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
		double step = maxTP / range;
		int steps = 0;
		for (int i = 0; i < range; i++) {
			steps++;
			if (maxTP * steps > range) {
				break;
			}
		}
		for (int i = 0; i < steps; i++) {
			double difX = x1 - x2;
			double difY = y1 - y2;
			double difZ = z1 - z2;
			double divider = step * i;
			double x = x1 - difX * divider;
			double y = y1 - difY * divider;
			double z = z1 - difZ * divider;
			//Jigsaw.chatMessage(y);
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
		}
		mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
	}
    @Override
    public void onDisable(){
        super.onDisable();
        isRunning = false;
        posX = posY = posZ = 0;
    }
    @EventTarget
    public void onPost(EventPostMotionUpdates e){
        Block block = Helper.blockUtils().getBlock(this.posX, this.posY, this.posZ);
        final int currentID = Block.getIdFromBlock(block);
        Boolean isbed = currentID != 0 && currentID == 26;
        if(this.isRunning && (bed.value && isbed) || !bed.value) {
            this.mc.thePlayer.swingItem();
            this.mc.playerController.func_180512_c(new BlockPos(this.posX , this.posY, this.posZ), Helper.blockUtils().getFacing(new BlockPos(this.posX, this.posY, this.posZ)));
            if((double)this.mc.playerController.curBlockDamageMP >= 1.0D)
               timer.reset();

        }
    }
}
