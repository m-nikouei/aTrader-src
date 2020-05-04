package stratDataTypes;

import java.util.Calendar;

public class TradeInfo {
	
	public int tradeTime = 0;
	
	public double entryPrice = 0;
	public double currPrice = 0;
	public double exitPrice = 0;
	public double profit = 0;
	public double maxP = 0;
	public double minP = 100;
	
	public Calendar entryTime = null;
	public Calendar exitTime = null;
	public TradeType Type = null;
	
	public double solidStop = 0;
	public double trailStop = 0;
	public double solidLimit = 0;

}
