package nivia.commands.commands;

import nivia.commands.Command;
import nivia.utils.Helper;
import nivia.utils.Logger;

public class skinme extends Command {
	 public skinme() {
		 	super("skinme", "皮肤设置", null, false, "skin", "sins", "sdadasdddd");
	    } 
	 
	 @Override
		public void execute(String commandName, String[] arguments) {
		 Helper.skinmeName = arguments[1];
		 Logger.logChat("设置SkinMe名字："+arguments[1] +"更换场景来生效！");
	 }
	 
}
