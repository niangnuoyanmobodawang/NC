package nivia.utils.utils;


import java.util.ArrayList;

public class NewValues {
	public String name;
	public static ArrayList<NewValues> modes = new ArrayList();
	public int counter;
	
	public Float value;
	public Float min;
	public Float max;
	public Float increment;
	private Float defaultfloat;
	public boolean isafloat = false;
	
	public boolean boolvalue;
	public boolean defaultboolean;
	public boolean isaboolean = false;
	
	public String stringvalue;
	public String defaultstring;
	public String[] allothers;
	public boolean isamode = false;
	
	public String editvalue;
	public int maxsize;
	public boolean iseditable = false;
	
	public NewValues(String name, Float value, Float min, Float max, Float increment){
		this.name = name;
		this.value = value;
		this.min = min;
		this.max = max;
		this.increment = increment;
		this.defaultfloat = value;
		isafloat = true;
		modes.add(this);
	}
	public NewValues(String name, Boolean value){
		this.name = name;
		this.boolvalue = value;
		this.defaultboolean = value;
		this.isaboolean = true;
		modes.add(this);
	}
	public NewValues(String name, String value, String[] allothers){
		this.name = name;
		this.stringvalue = value;
		this.defaultstring = value;
		this.allothers = allothers;
		isamode = true;
		modes.add(this);
	}
	public NewValues(String name, String value, int maxsize){
		this.name = name;
		this.editvalue = value;
		this.maxsize = maxsize;
		iseditable = true;
		modes.add(this);
	}
	public void interact(){
		if(isaboolean){
			this.boolvalue = !this.boolvalue;
		}
		if(isamode){
		    	counter++;
		    	if(counter == allothers.length){
		    		counter = 0;
		    	}
		    	stringvalue = allothers[counter];
		}
	}
}
