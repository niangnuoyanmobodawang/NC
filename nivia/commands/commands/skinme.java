package nivia.commands.commands;

import nivia.commands.Command;
import nivia.utils.Helper;
import nivia.utils.Logger;

public class skinme extends Command {
	 public skinme() {
		 	super("skinme", "Ƥ������", null, false, "skin", "sins", "sdadasdddd");
	    } 
	 
	 @Override
		public void execute(String commandName, String[] arguments) {
		 Helper.skinmeName = arguments[1];
		 Logger.logChat("����SkinMe���֣�"+arguments[1] +"������������Ч��");
	 }
	 
}
