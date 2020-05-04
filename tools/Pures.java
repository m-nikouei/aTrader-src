package tools;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import pureData.*;

public class Pures {

	public static String doubleFPoint(Double num){
		DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(5);
        String numStr = df.format(num);
        String[] vectors = numStr.split("\\.");
        if (vectors.length > 1){
        	if (vectors[0].length() == 0 || (vectors[0].length() == 1 && vectors[0].contains("-")))
        		vectors[0] = vectors[0] + "0";
        	numStr = vectors[0] + "." + vectors[1];
        } else
        	numStr = vectors[0];
        return numStr;
	}
	
	public static String doubleFPoint2(Double num, int fPos){
		DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(fPos);
        String numStr = df.format(num);
        String[] vectors = numStr.split("\\.");
        if (vectors.length > 1){
        	if (vectors[0].length() == 0 || (vectors[0].length() == 1 && vectors[0].contains("-")))
        		vectors[0] = vectors[0] + "0";
        	numStr = vectors[0] + "." + vectors[1];
        } else
        	numStr = vectors[0];
        return numStr;
	}
	
	public static boolean IntArrayEq(int[] f, int[] s){
		if (f.length == s.length){
			boolean res = true;
			for(int i = 0; i < f.length; i++)
				if (f[i] != s[i])
					res = false;
			return res;
		}else 
			return false;
	}
	
	public static double getPrice(priceRow pr, priceFields pf){
		double res = 0;
		if (pf.equals(priceFields.AskOpen)){
			res  = pr.rAO;
		}else if (pf.equals(priceFields.AskHigh)){
			res = pr.rAH;
		}else if (pf.equals(priceFields.AskLow)){
			res = pr.rAL;
		}else if (pf.equals(priceFields.AskClose)){
			res = pr.rAC;
		}else if (pf.equals(priceFields.BidOpen)){
			res = pr.rBO;
		}else if (pf.equals(priceFields.BidHigh)){
			res = pr.rBH;
		}else if (pf.equals(priceFields.BidLow)){
			res = pr.rBL;
		}else if (pf.equals(priceFields.BidClose)){
			res = pr.rBC;
		}
		return res;
	}
	
	public static ArrayList<Double> getPrices(ArrayList<priceRow> pr, int step, priceFields pf){
		ArrayList<Double> priceA = new ArrayList<Double>();
		for(int i = 0; i < step; i++){
			priceA.add(getPrice(pr.get(i), pf));
		}
		return priceA;
	}

	public static String HisPriceTester(HPTable HTP) throws ParseException{
		String ret = "p";
		Calendar myNow = HTP.getStartTime();
		for (int i = 0; i < HTP.rows.size(); i++){
			if (myNow.after(HTP.rows.get(i).rDate)){
				ret = "f " + i;
				//System.out.println(i + " failed!");
				break;
			}else if (myNow.before(HTP.rows.get(i).rDate)){
				//System.out.println(i + " jump from " + myNow.getTime() + " to " + HTP.rows.get(i).rDate.getTime());
				myNow = HTP.rows.get(i).rDate;
			}
			myNow = CalTools.CalJump(myNow, HTP.getTFrame(), 1);
		}
		
		return ret;
	}
	
}
