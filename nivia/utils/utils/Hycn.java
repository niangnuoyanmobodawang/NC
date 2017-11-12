package nivia.utils.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.launchwrapper.network.common.Common;
import net.minecraft.launchwrapper.network.socket.NetworkSocket;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import nivia.Pandora;
import nivia.files.Alts;
import nivia.gui.altmanager.Alt;
import nivia.gui.altmanager.AltLoginThread;
import nivia.gui.altmanager.AltManager;
import nivia.gui.altmanager.GuiAddAlt;
import nivia.gui.altmanager.GuiAltLogin;
import nivia.gui.altmanager.GuiRenameAlt;
import nivia.gui.customscreens.CustomScreenUtils;
import nivia.gui.mainmenu.PandoraMainMenu;
import nivia.utils.Helper;
import nivia.utils.utils.RenderUtils.R2DUtils;

public class Hycn extends GuiScreen
{

	  private AltLoginThread loginThread;
	  private int offset;
	  public Alt selectedAlt = null;
	  private String status = EnumChatFormatting.GRAY + "准备ing";
	  
	public static String line;
	public static String Uname;
	public static String uuid;
	public static boolean is18Mode;
	public static String token;
	public static String port;
	
	
	   public static void inject(){
			   
			   
		    Runnable runnable = new Runnable() {


				public void run() {
		    		  Process p;
					try {
					  p = Runtime.getRuntime().exec("C:\\Windows\\system32\\wbem\\wMiC.eXe PROCESS where \"name like '%java%'\" get Commandline");
					
						
		    		  InputStream is = p.getInputStream();
		    		  BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    		  
		    		   StringBuilder s=new StringBuilder("");
		    		while((line=reader.readLine())!= null){
		    			s.append(line);   		      		         			
		    		  }
		    		
		    		  p.waitFor();
		    		  is.close();
		    		  reader.close();
		    		  p.destroy();	


		    		  line=s.toString();
		    		  
		    		  line.replace("CommandLine", "");
		    		  
		    		 // lins=lins.substring(line.lastIndexOf("-DlauncherControlPort="), line.length());
			    		
		    		  String lins = line;
		    		  lins=lins.substring(line.lastIndexOf("-DlauncherControlPort="), line.length());
			    		System.out.println(lins);
		    		  
		    		//  if(line.indexOf("--accessToken ")>0){
		    			  line=line.substring(line.lastIndexOf("--username "), line.length());
		    		//  }
		    		 System.out.println("DEBUG|"+line); 

		    	

		    	      
		    	      
		    		  if(!line.contains("No Instance(s) Available.")){
		    		Uname = line.substring(line.lastIndexOf("--username ")+11,line.indexOf(" --version")).replace(" ", "");
		    		uuid = line.substring(line.lastIndexOf("--uuid ")+7,line.indexOf(" --accessToken")).replace(" ", "");
		    		token = line.substring(line.lastIndexOf("--accessToken ")+14,line.lastIndexOf("--accessToken ")+47).replace(" ", "");
		       //    if(is18Mode)
		    		port = lins.substring(lins.lastIndexOf("-DlauncherControlPort="),lins.lastIndexOf(" -XX:HeapDumpPath=Moj")).replace("-DlauncherControlPort=", "");
		    		
		    		if(uuid.equals(token)){
		    			return;
		    	//		JOptionPane.showMessageDialog(null, "检测到重复回环!是否多次启动MC?按确定自动关闭");
		    	//		Runtime.getRuntime().exec("Taskkill /F /IM javaw.exe");
		    	//		Runtime.getRuntime().exec("Taskkill /F /IM java.exe");   			
		    		}
		    		
		    	//   NetEaseControl.Kill();
		           JOptionPane.showMessageDialog(null, "获取——成功！\n"+
		    	"Token: "
		        +
		        token
		    	+
		    	"\n"
		    	+
		    	"UUID: "
		    	+
		    	uuid
		    	+"\n"
		    	+"Port: "
		    	+port
		    	,Uname,1); 
		           Helper.portx = port;
		           Helper.mc().session = new Session(Uname, uuid, token, "legacy");
	            	NetworkSocket.init();
	            	Common.debug("[Decrypt] Done");
	         //   	NetworkSocket.HandleMessage();
	            	NetworkSocket.GetModKey(1, 1);
		//           Helper.mc().displayGuiScreen(new GuiConnecting(new PandoraMainMenu(),Helper.mc(), new ServerData("x19hypixel.nie.netease.com", "25565")));
		           
		           Thread.currentThread().stop();
		           Thread.currentThread().interrupt();
		           return;
		    		  }

		    		  
			  } catch (Exception e) {
				  e.printStackTrace();
			    }
		    	}
		    	};
		    	ScheduledExecutorService service = Executors
		    	.newSingleThreadScheduledExecutor();
		    	service.scheduleAtFixedRate(runnable, 0, 3, TimeUnit.SECONDS);
		    	
		    
		   
		   }
	   
	   public void actionPerformed(GuiButton button)
	   {
	     switch (button.id)
	     {
	       case 0:
	         inject();
	         status = "打开网易！";
	         break;
	     }
	   }

	   public void drawScreen(int par1, int par2, float par3)
	   {

	     if (Mouse.hasWheel())
	     {
	       int wheel = Mouse.getDWheel();
	       if (wheel < 0)
	       {
	         offset += 26;
	         if (offset < 0) {
	           offset = 0;
	         }
	       }
	       else if (wheel > 0)
	       {
	         offset -= 26;
	         if (offset < 0) {
	           offset = 0;
	         }
	       }
	     }
	     CustomScreenUtils.drawBackground(CustomScreenUtils.alt);
	     this.drawString(fontRendererObj, mc.session.getUsername(), 10, 10, 0xDDDDDD);
	     this.drawCenteredString(fontRendererObj, "网易 " + Pandora.getAltManager().registry.size() + " alts", width / 2, 10, -1);
	     this.drawCenteredString(fontRendererObj, loginThread == null ? status : loginThread.getStatus(), width / 2, 20, -1);
	     R2DUtils.drawBorderedRect(50.0F, 33.0F, width - 50, height - 50, 1.0F, Helper.colorUtils().RGBtoHEX(25, 25, 25, 255), Helper.colorUtils().RGBtoHEX(5, 5, 5, 255));
	     GL11.glPushMatrix();
	     this.prepareScissorBox(0.0F, 33.0F, width, height - 50);
	     GL11.glEnable(3089);
	     int y = 38;

	     for (Alt alt : Pandora.getAltManager().registry) {
	       if (isAltInArea(y)) {
	         String name;
	         if (alt.getMask().equals("")) {
	           name = alt.getUsername();
	         } else
	           name = alt.getMask();
	         String pass;
	         if (alt.getPassword().equals("")) {
	           pass = "\247cCracked";
	         } else {
	           pass = alt.getPassword().replaceAll(".", "*");
	         }
	         if (alt == selectedAlt)
	         {
	           if ((isMouseOverAlt(par1, par2, y - offset)) && (Mouse.isButtonDown(0))) {
	             R2DUtils.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - offset + 20, 1.0F, Helper.colorUtils().RGBtoHEX(45, 45, 45, 255), -2142943931);
	           } else if (isMouseOverAlt(par1, par2, y - offset)) {
	             R2DUtils.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - offset + 20, 1.0F, Helper.colorUtils().RGBtoHEX(45, 45, 45, 255), -2142088622);
	           } else {
	             R2DUtils.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - offset + 20, 1.0F, Helper.colorUtils().RGBtoHEX(45, 45, 45, 255), -2144259791);
	           }
	         }
	         else if ((isMouseOverAlt(par1, par2, y - offset)) && (Mouse.isButtonDown(0))) {
	           R2DUtils.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - offset + 20, 1.0F, -Helper.colorUtils().RGBtoHEX(45, 45, 45, 255), -2146101995);
	         } else if (isMouseOverAlt(par1, par2, y - offset)) {
	           R2DUtils.drawBorderedRect(52.0F, y - offset - 4, width - 52, y - offset + 20, 1.0F, Helper.colorUtils().RGBtoHEX(45, 45, 45, 255), -2145180893);
	         }
	         drawCenteredString(fontRendererObj, name, width / 2, y - offset, -1);
	         drawCenteredString(fontRendererObj, pass, width / 2, y - offset + 10, 5592405);
	         if(!alt.getMask().isEmpty())
	           Helper.get2DUtils().drawCustomImage(55F , y - offset - 1, 17, 17, alt.getHead());
	         y += 26;
	       }
	     }

	     GL11.glDisable(3089);
	     GL11.glPopMatrix();
	     super.drawScreen(par1, par2, par3);
	     if (Keyboard.isKeyDown(200))
	     {
	       offset -= 26;
	       if (offset < 0) {
	         offset = 0;
	       }
	     }
	     else if (Keyboard.isKeyDown(208))
	     {
	       offset += 26;
	       if (offset < 0) {
	         offset = 0;
	       }
	     }
	   }

	   public void initGui()
	   {
	     buttonList.add(new GuiButton(0, width / 2 + 116, height - 24, 75, 20, "监听网易"));
	   }

	   private boolean isAltInArea(int y)
	   {
	     return y - offset <= height - 50;
	   }

	   private boolean isMouseOverAlt(int x, int y, int y1)
	   {
	     return (x >= 52) && (y >= y1 - 4) && (x <= width - 52) && (y <= y1 + 20) && (x >= 0) && (y >= 33) && (x <= width) && (y <= height - 50);
	   }


	   protected void mouseClicked(int par1, int par2, int par3)
	   {
	     if (offset < 0) {
	       offset = 0;
	     }
	     int y = 38 - offset;
	     for (Alt alt : Pandora.getAltManager().registry)
	     {
	       if (isMouseOverAlt(par1, par2, y))
	       {
	         if (alt == selectedAlt)
	         {
	           actionPerformed((GuiButton)buttonList.get(1));
	           return;
	         }
	         selectedAlt = alt;
	       }
	       y += 26;
	     }
	     try
	     {
	       super.mouseClicked(par1, par2, par3);
	     }
	     catch (IOException e)
	     {
	       e.printStackTrace();
	     }
	   }

	   public void prepareScissorBox(float x, float y, float x2, float y2)
	   {
	     ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
	     int factor = scale.getScaleFactor();
	     GL11.glScissor((int)(x * factor), (int)((scale.getScaledHeight() - y2) * factor), (int)((x2 - x) * factor), (int)((y2 - y) * factor));
	   }
}
