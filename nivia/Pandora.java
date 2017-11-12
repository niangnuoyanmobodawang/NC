package nivia;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.lwjgl.opengl.Display;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import nivia.gui.aclickgui.GuiAPX;
import nivia.gui.altmanager.AltManager;
import nivia.gui.chod.ChodsGui;
import nivia.gui.mainmenu.PandoraMainMenu;
import nivia.managers.*;
import nivia.security.ConnectionUtils;
import nivia.security.HWIDTools;
import nivia.utils.MinecraftFontRenderer;
import nivia.utils.Wrapper;
import nivia.utils.font.TTFFontRenderer;
import nivia.utils.utils.RenderUtils;


public class Pandora {
	

	public static String getClientName(){
		return "Nivia";
	}
	public static int getClientVersion(){
		return 4;
	}

	private static ModuleManager modManager;
	private static FriendManager friendManager;
	private static EventManager eventManager;
	private static CommandManager cmdManager;
	private static PropertyManager propManager;
	private static AltManager altManager;
	private static FileManager fileManager;
	private static StaffManager staffManager;
	private static GuiAPX clickGui;
	private static ChodsGui gui;

	public static ModuleManager getModManager(){
		return modManager;
	}
	public static CommandManager getCommandManager(){
		return cmdManager;
	}
	public static PropertyManager getPropertyManager(){
		return propManager;
	}
	public static FriendManager getFriendManager(){
		return friendManager;
	}
	public static EventManager getEventManager(){
		return eventManager;
	}
	public static AltManager getAltManager(){
		return altManager;
	}
	public static FileManager getFileManager(){
		return fileManager;
	}
	public static StaffManager getStaffManager(){
		return staffManager;
	}
	public static GuiAPX getAPXGui(){
		return clickGui;
	}
	

	public static MinecraftFontRenderer testFont;
	public static TTFFontRenderer tf;
	//TODO: false
	public static boolean isAuthedReal = true;
	private static boolean minecraftsda = false;
	



	public static void start() throws Exception{
		/*try {
			ConnectionUtils.authorize(HWIDTools.a());
		} catch (Exception e) {
			System.out.println("hi");			
		}*/
		String IiIIiIiIiiIiIiIIiIiIiIiIyi = "sssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssssssssssss";
		String IiIIiIiIiiIiIiIIiIiIiIiyIi = "sssssssssssss5sssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
		String IiIIiI2iIiiIiIiIIiIiIiIiyIi = "ssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssssssssssss";
		String IiI2IiI2iIiiIiIiIIiIiIiIiyIi = "ssssssssssssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssssss";
		String IiI2Ii2IiIiiIiIiIIiIiIiIyiIi = "ssssssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssssssssssssss";
		String IiIIi2IiIiiIiIiIIiIiIiIyiIi = IiI2Ii2IiIiiIiIiIIiIiIiIyiIi+"ssssssssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssss";
		String IiII2iIiIiiIiIiIIiIiIiIyiIi = "sssssssssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssss";
		String Ii2IIiIiIiiIiIiIIiIiIiIiyIi = IiI2Ii2IiIiiIiIiIIiIiIiIyiIi+"ssssssssssssssssssssssssssss3ssssssssssssssssssssssssssssssssssss";		
		String I2iIIiIiIiiIiIiIIiIiIiIyiIi = "ssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssss";
		try {
			String IiI2IiIiIiiIiIiIIiIiIiIiIyi = "sssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssssssssssss";
			String u2IiIIiIiIiiIiIiIIiIiIi3IiyIi = "sssssssssssss5sssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
			String t2I2iIIiI2iIiiIiIiI3IiIiIiIiyIi = "ssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssssssssssss";
			String IiI2IiI2iIii2iiI2iIiIIiIiIiIyiIi = "ssssssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssssssssssssss";
			String IiIIi2IiIiiIiI2iI22IiIiIiIyiIi = "ssssssssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssss"+u2IiIIiIiIiiIiIiIIiIiIi3IiyIi;
			String IiII2iI2iIi2iIiIiIIiIiIiIiyIi = "ssssssssssssssssssssssssssss3ssssssssssssssssssssssssssssssssssss";		
			String I2iIIiI2iIiiIiIiIIiIiIiIyiIi = "ssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssss"+IiII2iI2iIi2iIiIiIIiIiIiIiyIi;
			if(minecraftsda = net.minecraft.client.Minecraft.LWJGLinit())
			modManager = new ModuleManager();
			else
			Runtime.getRuntime().halt(-1);
			 
			String IiIIi2Ii3IiiIiI2iI22IiIiIiIyiIi = "ssssssssssssssssssssssssss3sssssssssssssssssssssssssssssssssssssssss";
			String IiII23iI2iIi2iIiIiIIiIiIiIiyIi = "ssssssssssssssssssssssssssss3ssssssssssssssssssssssssssssssssssss"+IiIIi2Ii3IiiIiI2iI22IiIiIiIyiIi;		
			String I2iIIiI2iIi6iIiIiIIiIiIiIyiIi = "ssssssssssssss3ssssssssssssssssssssssssssssssssssssssssssss"+IiII23iI2iIi2iIiIiIIiIiIiIiyIi;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "[ERROR]\n\n错误信息:\n"+e.getClass().getName()+"\n"+e.getMessage());
			System.out.println("[ERROR]");
			Runtime.getRuntime().exit(-1);
		}
//		YggdrasilMinecraftSessionService.auth.LoadLibrary();
		net.minecraft.client.Minecraft.anticrack();
		friendManager = new FriendManager();
		cmdManager = new CommandManager();
		propManager = new PropertyManager();
		fileManager = new FileManager();
		if(Minecraft.isjava7())
		clickGui = new GuiAPX();
		
		gui = new ChodsGui();
		clickGui.getTheme().insert();
		fileManager.loadFiles();
    
		String dir = System.getProperty("user.dir")+"\\il.zip";	
		if(!new File(dir).exists()){
			 try{
		BufferedInputStream in = new BufferedInputStream(Pandora.class.getResourceAsStream("/r.sys")); 
		   
		   	   
		  // System.out.print(dir);
		   
	       File file = new File(dir);  

	         new File(dir).createNewFile();  
	         
	        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));  
	        byte[] bb = new byte[1024];// 用来存储每次读取到的字节数组  
	        int n;// 每次读取到的字节数组的长度  
	        while ((n = in.read(bb)) != -1) {  
	            out.write(bb, 0, n);// 写入到输出流  
	        }  
	        out.close();// 关闭流  
	        in.close();  
	        
	        
	        
			 
			
			unzip(file,System.getProperty("user.dir")+"","jkldsfsw");
			System.out.print("[Ok]");
		}catch(Exception x){
			System.out.print(x.getMessage()+x.getCause()+x.getClass());
		}
         
	//		JOptionPane.showMessageDialog(null, "成功！");
			
		//	file.deleteOnExit();
		}else{
			System.out.print("[Passed]");
		}
		testFont = RenderUtils.helvetica;				

		Main.t1.stop();
		Wrapper.getMinecraft().displayGuiScreen(new PandoraMainMenu());
		//new FirstTimeScreen();		
		
		Display.setTitle("Recode√ by ho3 | "+new String(Minecraft.tiplan.getBytes("gbk"),"utf-8"));

	}
	
	 private static File [] unzip(File zipFile, String dest, String passwd) throws ZipException {  
	        ZipFile zFile = new ZipFile(zipFile);  
	        zFile.setFileNameCharset("GBK");  
	        if (!zFile.isValidZipFile()) {  
	            throw new ZipException("压缩文件不合法,可能被损坏.");  
	        }  
	        File destDir = new File(dest);  
	        if (destDir.isDirectory() && !destDir.exists()) {  
	            destDir.mkdir();  
	        }  
	        if (zFile.isEncrypted()) {  
	            zFile.setPassword(passwd.toCharArray());  
	        }  
	        zFile.extractAll(dest);  
	          
	        List<FileHeader> headerList = zFile.getFileHeaders();  
	        List<File> extractedFileList = new ArrayList<File>();  
	        for(FileHeader fileHeader : headerList) {  
	            if (!fileHeader.isDirectory()) {  
	                extractedFileList.add(new File(destDir,fileHeader.getFileName()));  
	            }  
	        }  
	        File [] extractedFiles = new File[extractedFileList.size()];  
	        extractedFileList.toArray(extractedFiles);  
	        return extractedFiles;  
	    }  
	
	public static String prefix = "-";
}