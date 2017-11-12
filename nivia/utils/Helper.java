package nivia.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import nivia.Pandora;
import nivia.managers.FriendManager;
import nivia.managers.PropertyManager.Property;
import nivia.utils.utils.*;
import nivia.utils.utils.RenderUtils.ColorUtils;
import nivia.utils.utils.RenderUtils.R2DUtils;
import nivia.utils.utils.RenderUtils.R3DUtils;

public class Helper {
	public static boolean xray = false;
	public static List<Block> dimblock = new ArrayList<Block>();
	public static HashSet blockIDs = new HashSet();
	private static EntityUtils entityUtils = new EntityUtils();
    private static CombatUtils combatUtils = new CombatUtils();
    private static BlockUtils blockUtils = new BlockUtils();
    private static PlayerUtils playerUtils = new PlayerUtils();
    private static R2DUtils r2DUtils = new R2DUtils();
    private static R3DUtils r3DUtils = new R3DUtils();
    private static ColorUtils colorUtils = new ColorUtils();
    private static WorldUtils worldUtils = new WorldUtils();
    private static MathUtils mathUtils = new MathUtils();
    private static InventoryUtils inventoryUtils = new InventoryUtils();
	public static int opacity;
	public static boolean ViewClip = true;
	public static Boolean bypass = true;
	public static Boolean ClearChat = false;
	public static Boolean ClearBag;
	public static Boolean DIF;
//	public static ArrayList xs;
	public static Boolean ClearBagRP = true;
	public static Boolean skinmeS = false;
	public static Boolean skinmeC= false;
	public static String skinmeName = "ho3";
	public static boolean nofire = false;
	public static boolean ItemPhysic = false;
	public static String portx;
	public static boolean invmove = false;
	public static boolean scaf = false;
	public static boolean nofov = false;
	public static boolean tower = false;
	
	public static void addis(Block pos){
		try{
		//	synchronized(this){
			dimblock.add(pos);
		//	}
	//		renderTracer(pos.getX(),pos.getY(),pos.getZ());
	//		dimblock.add(pos);
		}catch(Exception x){x.printStackTrace();}
	}
	
	private static void renderTracer(double x, double y, double z) {
		final float distance = (float) Helper.player().getDistance(x, y, z);

	//	boolean isSpecial = FriendManager.isFriend(entity.getName());
		int color;
	//	if (FriendManager.isFriend(entity.getName()))
			color = 0xFF00CCFF;
	//	else {
			float xD = distance / 48;
			if(xD >= 1) xD = 1;
				color = 0;
	//	}
		
		boolean entityesp = Pandora.getModManager().getModState("ESP");
		
		
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);

	
			Helper.colorUtils().glColor(color);
		
		GL11.glLineWidth(3);
		GL11.glBegin(1);
		GL11.glVertex3d(0.0D, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0D);
		GL11.glVertex3d(x, y, z);
		GL11.glEnd();
		GL11.glBegin(1);
		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x, y + (entityesp ? 0 : 1.2), z);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glPopMatrix();
		
		
	
	}
	
	public static boolean containsID(int id) {
		return blockIDs.contains(id);
	}
	
	/*
	public static void log56(Block bl) {
		if(Block.getIdFromBlock(bl) == 56){
			if(xs.contains(bl))
				return;
			xs.add(bl);
			Logger.logChat("·¢ÏÖ×êÊ¯£¡X:"+bl.getBlockBoundsMaxX() +" Y:"+bl.getBlockBoundsMaxY() +" Z:"+bl.getBlockBoundsMaxZ());
		}
	}
    */
    
    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }
    public static EntityPlayerSP player() {
        return mc().thePlayer;
    }
    public static WorldClient world() {
        return mc().theWorld;
    }
    public static EntityUtils entityUtils(){
    	return entityUtils;
    }
    public static WorldUtils worldUtils() {
    	return worldUtils;
    }
    public static CombatUtils combatUtils(){
    	return combatUtils;
    }
    public static InventoryUtils inventoryUtils(){
    	return inventoryUtils;
    }
    public static BlockUtils blockUtils(){
    	return blockUtils;
    }
    public static PlayerUtils playerUtils(){
    	return playerUtils;
    }
    public static R2DUtils get2DUtils(){
    	return r2DUtils;
    }
    public static R3DUtils get3DUtils(){
    	return r3DUtils;
    }
    public static ColorUtils colorUtils(){
    	return colorUtils;
    }
    public static MathUtils mathUtils(){
        return mathUtils;
    }
    public static void sendPacket(Packet p){
    	mc().getNetHandler().addToSendQueue(p);
    }

    
    
}
