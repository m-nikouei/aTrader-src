package pureData;

import java.util.ArrayList;
import java.util.Calendar;

public class HPTable {
		

	private String instrument = null;
	private String TimeFrame = null;
	private Calendar startTime = null;
	private Calendar endTime = null;
	public ArrayList<priceRow> rows = null;
	public String TestR = "";
	
	public int size(){
		return rows.size();
	}
	
	public HPTable(String inst, String tf, Calendar st, Calendar et, ArrayList<priceRow> pr){
		instrument = inst;
		TimeFrame = tf;
		startTime = st;
		endTime = et;
		rows = pr;
	}
	
	public String getInst(){
		return instrument;
	}
	
	public String getTFrame(){
		return TimeFrame;
	}
	
	public Calendar getStartTime(){
		return startTime;
	}
	
	public Calendar getEndTime(){
		return endTime;
	}

}
