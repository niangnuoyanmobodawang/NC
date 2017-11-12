package nivia.modules.ghost;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import nivia.Pandora;
import nivia.commands.Command;
import nivia.events.EventTarget;
import nivia.events.events.EventPostMotionUpdates;
import nivia.events.events.EventPreMotionUpdates;
import nivia.events.events.EventTick;
import nivia.managers.PropertyManager.Property;
import nivia.modules.Module;
import nivia.utils.Helper;
import nivia.utils.Logger;
import nivia.utils.utils.BlockUtils.BlockData;
import nivia.utils.utils.Timer;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AutoClicker extends Module {
    private List<Block> invalid = Arrays.asList(new Block[] { Blocks.air, Blocks.water, Blocks.fire,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.chest, Blocks.anvil, Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.gravel });
    private Timer timer = new Timer();
    private Timer timerMotion = new Timer();
    private Timer timerMotion2 = new Timer();
    private Timer timersn = new Timer();
    private BlockData blockData;
    boolean placing;
    public Property<Boolean> Switch = new Property<>(this, "Switch Item", true);
    public Property<Boolean> Hypixel = new Property<>(this, "Hypixel", true);
    public Property<Boolean> needJump = new Property<>(this, "AutoJump", false);
    public Property<Boolean> Snake = new Property<>(this, "Snake", false);
    public Property<Boolean> Rju = new Property<>(this, "RandomJump", false);
	
    @EventTarget
    public void onTT(EventTick event) {
        if(needJump.value && timer.hasTimeElapsed(1100)){
        	if(Rju.value && new Random().nextBoolean()){
        		mc.thePlayer.jump();
        	}
        	else if(!Rju.value)
        	{
        		mc.thePlayer.jump();
        	}
        }
        
        if(mc.thePlayer.isSneaking() && Snake.value && timersn.hasTimeElapsed(10+new Random().nextLong())){
        	mc.thePlayer.setSneaking(false);
        	timersn.reset();
        }
    }
    
    @Override
    public void onEnable(){
        super.onEnable();
        Helper.tower = true;
 
    }
    
    @Override
	public void onDisable() {
		super.onDisable();
		mc.thePlayer.setSneaking(false);
		Helper.tower = false;
	}
    
    
    public AutoClicker() {
        super("Tower", 0, 0xE6B800, Category.MISCELLANEOUS, "Clicks Automatically.", new String[]{"aclicker", "ac", "autoc", "aclick"}, true);
    }
    public static float rando05(long seed){
        seed = System.currentTimeMillis() + seed;
        return 0.30000000000f + (new Random(seed).nextInt(70000000) / 100000000.000000000000f) + 0.00000001458745f;
    }
    
    @EventTarget
    public void onPre(EventPreMotionUpdates event) {
        blockData = null;
       //Logger.logChat("test"); 
       // if (!Helper.player().isSneaking()) {
            BlockPos blockBelow1 = new BlockPos(Helper.player().posX, Helper.player().posY - 1.0D,
                    Helper.player().posZ);
            if (Helper.world().getBlockState(blockBelow1).getBlock() == Blocks.air) {
            	
                blockData = Hypixel.value ? getBlockData(blockBelow1, invalid) : getBlockData1(blockBelow1);
                if (blockData != null) {
                    float yaw = aimAtLocation((double) blockData.position.getX(),
                            (double) blockData.position.getY(), (double) blockData.position.getZ(),
                            blockData.face)[0];
                    float pitch = aimAtLocation((double) blockData.position.getX(),
                            (double) blockData.position.getY(), (double) blockData.position.getZ(),
                            blockData.face)[1];
 
                    event.setGround(true);
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                }
            }
     //   }
    }
    
    private BlockData getBlockData(BlockPos pos, List list) {
        return !list.contains(Helper.world().getBlockState(pos.add(0, -1, 0)).getBlock())
                ? new BlockData(pos.add(0, -1, 0), EnumFacing.UP)
                : (!list.contains(Helper.world().getBlockState(pos.add(-1, 0, 0)).getBlock())
                        ? new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST)
                        : (!list.contains(Helper.world().getBlockState(pos.add(1, 0, 0)).getBlock())
                                ? new BlockData(pos.add(1, 0, 0), EnumFacing.WEST)
                                : (!list.contains(Helper.world().getBlockState(pos.add(0, 0, -1)).getBlock())
                                        ? new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH)
                                        : (!list.contains(
                                        		Helper.world().getBlockState(pos.add(0, 0, 1)).getBlock())
                                                        ? new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH) : null))));
    }
    //This is cancer, but it's awesome.
    public BlockData getBlockData1(BlockPos pos) {
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new BlockData( pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new BlockData( pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new BlockData( pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new BlockData( pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new BlockData( pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add = pos.add(-1, 0, 0);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(-1, 0, 0)).getBlock())) {
            return new BlockData( add.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(1, 0, 0)).getBlock())) {
            return new BlockData( add.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(0, 0, -1)).getBlock())) {
            return new BlockData( add.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add.add(0, 0, 1)).getBlock())) {
            return new BlockData( add.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add2 = pos.add(1, 0, 0);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock())) {
            return new BlockData( add2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(1, 0, 0)).getBlock())) {
            return new BlockData( add2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(0, 0, -1)).getBlock())) {
            return new BlockData( add2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add2.add(0, 0, 1)).getBlock())) {
            return new BlockData( add2.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add3 = pos.add(0, 0, -1);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock())) {
            return new BlockData( add3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(1, 0, 0)).getBlock())) {
            return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(0, 0, -1)).getBlock())) {
            return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add3.add(0, 0, 1)).getBlock())) {
            return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
        }
        final BlockPos add4 = pos.add(0, 0, 1);
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock())) {
            return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(1, 0, 0)).getBlock())) {
            return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(0, 0, -1)).getBlock())) {
            return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (!invalid.contains(Minecraft.getMinecraft().theWorld.getBlockState(add4.add(0, 0, 1)).getBlock())) {
            return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
        }
        BlockData blockData = null;
 
        return blockData;
    }
 
    private class BlockData {
        public BlockPos position;
        public EnumFacing face;
 
        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
   
    private int getBlockAmount() {
        int n = 0;
        for (int i = 9; i < 45; ++i) {
            final ItemStack stack = Helper.player().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock().isCollidable()) {
                n += stack.stackSize;
            }
        }
        return n;
    }
    
    
   
    private float[] aimAtLocation(double x, double y, double z, EnumFacing facing) {
        EntitySnowball temp = new EntitySnowball(Helper.world());
        temp.posX = x + 0.5D;
        temp.posY = y - 2.70352523530000001D;
        temp.posZ = z + 0.5D;
        temp.posX += (double) facing.getDirectionVec().getX() * 0.25D;
        temp.posY += (double) facing.getDirectionVec().getY() * 0.25D;
        temp.posZ += (double) facing.getDirectionVec().getZ() * 0.25D;
        return aimAtLocation(temp.posX, temp.posY, temp.posZ);
    }
 
    private float[] aimAtLocation(double positionX, double positionY, double positionZ) {
        double x = positionX - Helper.player().posX;
        double y = positionY - Helper.player().posY;
        double z = positionZ - Helper.player().posZ;
        double distance = (double) MathHelper.sqrt_double(x * x + z * z);
        return new float[] { (float) (Math.atan2(z, x) * 180.0D / 3.141592653589793D) - 90.0F,
                (float) (-(Math.atan2(y, distance) * 180.0D / 3.141592653589793D)) };
    }
    
    
    @EventTarget
    public void onPost(EventPostMotionUpdates post) {
        if (blockData != null) {
        	if(!(Switch.value ? this.getBlockAmount() != 0 : (Helper.player().getCurrentEquippedItem() != null && Helper.player().getCurrentEquippedItem().getItem() instanceof ItemBlock))) return;
            if (timer.hasTimeElapsed(1100)) {
                Helper.mc().rightClickDelayTimer = 0;
                final String playerPos = new StringBuilder(String.valueOf(Helper.player().posY)).toString();
                if (Helper.player().movementInput.jump) {
              if(Snake.value){ 
            	  timersn.reset();
            	  mc.thePlayer.setSneaking(true);
              }
                //	mc.timer.timerSpeed = 1.0f;
                	
                    Helper.player().motionY = 0.42; 
                    mc.thePlayer.motionX *= 0.3;
                    mc.thePlayer.motionZ *= 0.3;
                    
                //    post.setPitch(-90f);
                    if(timerMotion.hasTimeElapsed(1500)) {
                //    	mc.timer.timerSpeed = 1.0f;
               //     	mc.thePlayer.motionX *= 0.3;
                //        mc.thePlayer.motionZ *= 0.3;
                        Helper.player().motionY = -0.28;
                        timerMotion.reset();
                        if(timerMotion.hasTimeElapsed(2)) {
                  //      	mc.timer.timerSpeed = 1.0f;
                //        	mc.thePlayer.motionX *= 0.3;
                //            mc.thePlayer.motionZ *= 0.3;
                            Helper.player().motionY = 0.42;
                 //           post.setPitch(-90f);
                        }
                    } 
                    if(mc.thePlayer.isSneaking() && Snake.value && timersn.hasTimeElapsed(10+new Random().nextLong())){
                    	mc.thePlayer.setSneaking(false);
                    	timersn.reset();
                    }
                }else if(mc.thePlayer.isSneaking() && Snake.value && timersn.hasTimeElapsed(10+new Random().nextLong())){
                	mc.thePlayer.setSneaking(false);
                	timersn.reset();
                }
 
                int heldItem = Helper.player().inventory.currentItem;
                BlockPos bp = new BlockPos(Helper.player().posX, Helper.player().posY - 1.0, Helper.player().posZ);
                for (int i = 0; i < 9; i++) {
                    if (Helper.player().inventory.getStackInSlot(i) != null
                            && Helper.player().inventory.getStackInSlot(i).stackSize != 0
                            && Helper.player().inventory.getStackInSlot(i).getItem() instanceof ItemBlock && Switch.value) {
                        Helper.sendPacket(new C09PacketHeldItemChange(Helper.player().inventory.currentItem = i));
                        break;
                    }
                }
              
            	   if (Helper.mc().playerController.func_178890_a(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockData.position, blockData.face, new Vec3((double) blockData.position.getX() + rando05(0) + blockData.face.getDirectionVec().getX() * rando05(1), (double) blockData.position.getY()+ rando05(2) + blockData.face.getDirectionVec().getY() * rando05(3), (double) blockData.position.getZ() + rando05(4) + blockData.face.getDirectionVec().getZ() * rando05(5)))) 
                       Helper.sendPacket(new C0APacketAnimation());         	   
              
            //  Helper.player().inventory.currentItem = heldItem;
             //   if(Switch.value)
             //   Helper.sendPacket(new C09PacketHeldItemChange(Helper.player().inventory.currentItem = heldItem));
                mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.UP));
            }
        }
    }
    protected void addCommand() {
		Pandora.getCommandManager().cmds
				.add(new Command("AutoClicker", "Manages autoclicker", Logger.LogExecutionFail("Option, Options:",
						new String[] { "CPS" }), "aclicker", "aclick") {
					@Override
					public void execute(String commandName, String[] arguments) {
		                String message = arguments[1];
		                switch (message.toLowerCase()) {                   
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