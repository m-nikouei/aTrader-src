package pureData;


import java.util.Calendar;

import tools.Pures;

public class FXOffer {
	
	private int index = -1;
	private String offerID = "";
	private String Inst = "";
	private double Bid = 0d;
	private double Ask = 0d;
	private double Spread = 0d;
	private double Low = 0d;
	private double High = 0d;
	private double volume = 0d;
	private Calendar TimeStamp = null;
	
	public FXOffer(String OID, String in, double b, double a, double l, double h, double v,Calendar t){
		offerID = OID; Inst = in; Bid = b; Ask = a;	Low = l; High = h; volume = v; TimeStamp = t;
		Spread = Ask - Bid;
	}
	
	public void setIndex(int i){
		index = i;
	}
	
	public int getIndex(){
		return index;
	}
	
	public String getofferID(){
		return offerID;
	}
	
	public String getInstrument(){
		return Inst;
	}
	
	public double getBid(){
		return Bid;
	}
	
	public double getAsk(){
		return Ask;
	}
	
	public double getSpread(){
		return Spread;
	}
	
	public double getLow(){
		return Low;
	}
	
	public double getHigh(){
		return High;
	}
	
	public double getVolume(){
		return volume;
	}
	
	public Calendar getTimeStamp(){
		return TimeStamp;
	}
	
	public String[] getOfferRow(){
		String[] rStr = {Integer.toString(index), getInstrument(), Double.toString(getBid()), 
				Double.toString(getAsk()), Pures.doubleFPoint(getSpread()), Double.toString(getLow()), 
				Double.toString(getHigh()), Double.toString(getVolume()), TimeStamp.getTime().toString()};
		return rStr;
	}

}
