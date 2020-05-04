package pureData;

import java.util.Calendar;

import fxcmIF.aTrader;


public class FXOrder {

	private String OrderID = "";
	private String AccountID = "";
	private String OrderStatus = "";
	private String OfferID = "";
	private int OrderAmt = 0;
	private String BuySell = "";
	private double Rate = 0d;
	private double Stop = 0d;
	private double Limit = 0d;
	private Calendar StatusTime = null;

	public FXOrder(String oid, String aid, String os, String off, int amt, String bs, double r, double s, double l, Calendar c){
		OrderID = oid; AccountID = aid; OrderStatus = os; OfferID = off; OrderAmt = amt; BuySell = bs;
		Rate = r; Stop = s; Limit = l; StatusTime = c;
	}
	
	public String getOrderID(){
		return OrderID;
	}
	
	public String getAccountID(){
		return AccountID;
	}
	
	public String getOrderStatus(){
		return OrderStatus;
	}
	
	public String getOfferID(){
		return OfferID;
	}
	
	public int getOrderAmt(){
		return OrderAmt;
	}
	
	public String getBuySell(){
		return BuySell;
	}
	
	public double getRate(){
		return Rate;
	}
	
	public double getStop(){
		return Stop;
	}
	
	public double getLimit(){
		return Limit;
	}
	
	public Calendar getStatusTime(){
		return StatusTime;
	}
	
	public String[] getOrderRow(){
		String[] rStr = {getOrderID(), getAccountID(), getOrderStatus(), aTrader.OfferIDtoInstrument(getOfferID()), 
				Integer.toString(getOrderAmt()), getBuySell(), Double.toString(getRate()), Double.toString(getStop()), 
				Double.toString(getLimit()), getStatusTime().getTime().toString()};
		return rStr;
	}
	

}
