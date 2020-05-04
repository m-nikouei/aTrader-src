package pureData;

import java.util.Calendar;

import tools.Pures;
import fxcmIF.aTrader;

public class FXClosedTrade {

	private String ClosedID = "";
	private String CAID = "";
	private String COID = "";
	private int CAmt = 0;
	private String CBS = "";
	private double COpenRate = 0d;
	private double ClosedRate = 0d;
	private double CPL = 0d;
	private double CGrossPL = 0d;
	private double Commission = 0d;
	private double CRoll = 0d;
	private Calendar OpenedTime = null;
	private Calendar ClosedTime = null;
	
	public FXClosedTrade(String cid, String caid, String coid, int ca, String cbs, double cor, double cr, double pl,
			double g, double com, double r, Calendar ot, Calendar ct){
		ClosedID = cid; CAID = caid; COID = coid; CAmt = ca; CBS = cbs; COpenRate = cor; ClosedRate = cr; CPL = pl;
		CGrossPL = g; CRoll = r; OpenedTime = ot; ClosedTime = ct; Commission = com; 
	}
	
	public String getClosedID(){
		return ClosedID;
	}
	
	public String getAccountID(){
		return CAID;
	}
	
	public String getOfferID(){
		return COID;
	}
	
	public int getAmt(){
		return CAmt;
	}
	
	public String getBuySell(){
		return CBS;
	}
	
	public double getOpenRate(){
		return COpenRate;
	}
	
	public double getCloseRate(){
		return ClosedRate;
	}
	
	public double getPL(){
		return CPL;
	}
	
	public double getGrossPL(){
		return CGrossPL;
	}
	
	public double getCommission(){
		return Commission;
	}
	
	public double getRoll(){
		return CRoll;
	}
	
	public Calendar getOpenTime(){
		return OpenedTime;
	}
	
	public Calendar getClosedTime(){
		return ClosedTime;
	}
	
	public String[] getClosedTRow(){
		String[] rStr = {getClosedID(), getAccountID(), aTrader.OfferIDtoInstrument(getOfferID()), 
				Integer.toString(getAmt()), getBuySell(), Double.toString(getOpenRate()), 
				Double.toString(getCloseRate()), Pures.doubleFPoint(getPL()), Pures.doubleFPoint(getGrossPL()), 
				Pures.doubleFPoint(getCommission()), Double.toString(getRoll()), getOpenTime().getTime().toString(),getClosedTime().getTime().toString()};
		return rStr;
	}

}
