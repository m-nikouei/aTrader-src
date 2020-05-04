package pureData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class priceRow {
	
	public Calendar rDate = null;
	 public double rBO = 0; public double rBH = 0; public  double rBL = 0;
	 public double rBC = 0; public double rAO = 0; public double rAH = 0; 
	 public double rAL = 0; public double rAC = 0;
	
	public priceRow(Calendar d, double[] rowStr){ 
		rDate = d;
		rBO = rowStr[0];
		rBH = rowStr[1];
		rBL = rowStr[2];
		rBC = rowStr[3];
		rAO = rowStr[4];
		rAH = rowStr[5];
		rAL = rowStr[6];
		rAC = rowStr[7];
	}
	
	public priceRow(String[] rowStr) throws ParseException{
		DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		Date pDate = df.parse(rowStr[0]); 
		rDate = Calendar.getInstance();
		rDate.setTime(pDate);
		rBO = Double.parseDouble(rowStr[1]);
		rBH = Double.parseDouble(rowStr[2]);
		rBL = Double.parseDouble(rowStr[3]);
		rBC = Double.parseDouble(rowStr[4]);
		rAO = Double.parseDouble(rowStr[5]);
		rAH = Double.parseDouble(rowStr[6]);
		rAL = Double.parseDouble(rowStr[7]);
		rAC = Double.parseDouble(rowStr[8]);
	}
	
	public boolean equals(priceRow nw){
		boolean res = false;
		if (rDate == nw.rDate)
			res = true;
		return res;
	}

}
