package pureData;

import java.util.Calendar;

import tools.Pures;
import fxcmIF.aTrader;


public class FXTrade {

	private String TradeID = "";
	private String AID = "";
	private String OID = "";
	private int TradeAmt = 0;
	private String TradeBS = "";
	private double OpenRate = 0d;
	private double Close = 0d;
	private double PL = 0d;
	private double GrossPL = 0d;
	private double Commission = 0d;
	private double Roll = 0d;
	private double UsedMr = 0d;
	private Calendar OpenTime = null;
	
	public FXTrade(String tid, String aid, String oid, int a, String bs, double or, double c, double pl, double g, double com, double r,
			double u, Calendar t){
		TradeID = tid; AID = aid; OID = oid; TradeAmt = a; TradeBS = bs; OpenRate = or; Close = c; PL = pl; GrossPL = g;
		Roll = r; UsedMr = u; OpenTime = t; Commission = com;
	}
	
	public String getTradeID(){
		return TradeID;
	}
	
	public String getAccountID(){
		return AID;
	}
	
	public String getOfferID(){
		return OID;
	}
	
	public int getTradeAmt(){
		return TradeAmt;
	}
	
	public String getBuySell(){
		return TradeBS;
	}
	
	public double getOpenRate(){
		return OpenRate;
	}
	
	public double getClose(){
		return Close;
	}
	
	public double getPL(){
		return PL;
	}
	
	public double getGrossPL(){
		return GrossPL;
	}
	
	public double getCommission(){
		return Commission;
	}
	
	public double getRoll(){
		return Roll;
	}
	
	public double getUsedMr(){
		return UsedMr;
	}
	
	public Calendar getOpenTime(){
		return OpenTime;
	}
	
	public String[] getTradeRow(){
		String[] rStr = {getTradeID(), getAccountID(), aTrader.OfferIDtoInstrument(getOfferID()), 
				Integer.toString(getTradeAmt()), getBuySell(), Double.toString(getOpenRate()), 
				Double.toString(getClose()), Pures.doubleFPoint(getPL()), 
				Pures.doubleFPoint(getGrossPL()), Pures.doubleFPoint(getCommission()), Double.toString(getRoll()), Double.toString(getUsedMr()), 
				getOpenTime().getTime().toString()};
		return rStr;
	}

}
