package strategies;

import java.util.ArrayList;
import java.util.Calendar;

import aTWorker.strategy;
import indicators.RSI;
import pureData.priceRow;
import stratDataTypes.MATrend;
import stratDataTypes.Signal;
import stratDataTypes.TradeType;
import stratDataTypes.TradesCollectionData;
import tools.Pures;
import tools.priceFields;

public class RDRRSIst implements strategy{
	
	int aRSIStep = 14;
	int bRSIStep = 14;
	RSI aRSI = null;
	RSI bRSI = null;
	
	double aRSIV = 0;
	double bRSIV = 0;
	
	boolean aBalance = false;
	boolean bBalance = false;
	
	boolean tradeRange = false;
	boolean trShape = false;
	int H1 = 16;
	int H2 = 22;
	
	TradeType state = TradeType.NotIn;
	
	double stopVal = 0;
	
	double changeStep = 5;
	double upRange = 70;
	double downRange = 30;
	
	public TradesCollectionData TD = null;
	String WLog = "";
	
	MATrend Flag = MATrend.SiG;
	
	public RDRRSIst(int arsis, int brsis, double chR, double sv, int h1, int h2, boolean trs){
		aRSIStep = arsis;
		bRSIStep = brsis;
		stopVal = sv;
		changeStep = chR;
		TD = new TradesCollectionData(stopVal, 0.0001,false);
		H1 = h1;
		H2 = h2;
		trShape = trs;
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
		aRSI = new RSI(prl, aRSIStep, priceFields.AskClose);
		bRSI = new RSI(prl, bRSIStep, priceFields.BidClose);
	}

	@Override
	public void DataHandler(priceRow pr) {
		aRSI.NewRSI(pr.rAC);
		bRSI.NewRSI(pr.rBC);
		
		aRSIV = Math.round(aRSI.RSI * 10);
		aRSIV = aRSIV / 10;
		
		bRSIV = Math.round(bRSI.RSI * 10);
		bRSIV = bRSIV / 10;
		
		WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(aRSIV) + "," +
				Pures.doubleFPoint(bRSIV);
		
		if (trShape){
			if (pr.rDate.get(Calendar.HOUR_OF_DAY) < H2 &&
					pr.rDate.get(Calendar.HOUR_OF_DAY) > H1)
				tradeRange = false;
			else
				tradeRange = true;
		}else{
			if (pr.rDate.get(Calendar.HOUR_OF_DAY) > H2 ||
					pr.rDate.get(Calendar.HOUR_OF_DAY) < H1)
				tradeRange = false;
			else
				tradeRange = true;
		}
		
		if (aBalance && bBalance){
			SignalSender(pr);
		}else{
			if (aRSIV < 65)
				aBalance = true;
			if (bRSIV < 65)
				bBalance = true;
			WLog += "\r\n";
		}
	}

	@Override
	public void SignalSender(priceRow pr) {
		if (Flag.equals(MATrend.SiG)){
			if (bRSIV < downRange)
				Flag = MATrend.UpS;
			else if (aRSIV > upRange)
				Flag = MATrend.DnS;
		}else if (Flag.equals(MATrend.DnS)){
				if (aRSIV < upRange)
					Flag = MATrend.DnT;
		}else if (Flag.equals(MATrend.DnT)){
				Flag = MATrend.SiG;
		}else if (Flag.equals(MATrend.UpS)){
				if (bRSIV > downRange)
					Flag = MATrend.UpT;
		}else if (Flag.equals(MATrend.UpT)){
				Flag = MATrend.SiG;
		}
		
		String upLog = "";
		Signal sig = Signal.Wait;
		if (state.equals(TradeType.NotIn)){
			if (Flag.equals(MATrend.DnT) && tradeRange){
				state = TradeType.Short;
				sig = Signal.Sell;
				upLog = TD.UpdateTrade(sig, pr);
				rangeChange(sig);
			}else if (Flag.equals(MATrend.UpT) && tradeRange){
				state = TradeType.Long;
				sig = Signal.Buy;
				upLog = TD.UpdateTrade(sig, pr);
				rangeChange(sig);
			}
		}else if (state.equals(TradeType.Short)){
			if (Flag.equals(MATrend.DnT) && tradeRange){
				sig = Signal.Sell;
				upLog = TD.UpdateTrade(sig, pr);
				rangeChange(sig);
			}else if (Flag.equals(MATrend.UpS)){
				state = TradeType.NotIn;
				sig = Signal.Buy;
				int sz = TD.openTL.size();
				//upRange = 70;
				//downRange = 30;
				for(int i = 0; i < sz; i++){
					upLog += TD.UpdateTrade(sig, pr);
				}
			}else{
				sig = Signal.Wait;
				upLog = TD.UpdateTrade(sig, pr);
			}
		}else if (state.equals(TradeType.Long)){
			if (Flag.equals(MATrend.UpT) && tradeRange){
				sig = Signal.Buy;
				upLog = TD.UpdateTrade(sig, pr);
				rangeChange(sig);
			}else if (Flag.equals(MATrend.DnS)){
				state = TradeType.NotIn;
				sig = Signal.Sell;
				int sz = TD.openTL.size();
				//upRange = 70;
				//downRange = 30;
				for(int i = 0; i < sz; i++){
					upLog += TD.UpdateTrade(sig, pr);
				}
			}else{
				sig = Signal.Wait;
				upLog = TD.UpdateTrade(sig, pr);
			}
		}
		
		WLog += "," + Flag.toString() + "," + state.toString() + "," + tradeRange
				+ "," + sig.toString() + upLog + "\r\n";
		
		/*String fix = Pures.doubleFPoint(aRSI.RSI) + " " + Pures.doubleFPoint(bRSI.RSI) + " " 
				+ " -- " + Pures.doubleFPoint(upRange) + " -- " + Pures.doubleFPoint(downRange) + " | " + Flag.toString() + " - " + sig + " - " + state + "\r\n";
		String tdLog = WLog;
		WLog ="----- " + pr.rDate.getTime() + " -----\r\n" + tdLog + fix;*/
	}
	
	private void rangeChange(Signal sig){
		if (sig.equals(Signal.Buy)){
			upRange = upRange - changeStep;
			downRange = downRange - changeStep;
		}else if (sig.equals(Signal.Sell)){
			upRange = upRange + changeStep;
			downRange = downRange + changeStep;
		}
	}

	@Override
	public TradesCollectionData getTradesData() {
		return TD;
	}
	
	@Override
	public String getLog() {
		return WLog;
	}
	
	@Override
	public int getLen() {
		int max = aRSIStep;
		if (max  < bRSIStep)
			max = bRSIStep;
		return max;
	}

	@Override
	public void TickHandler(priceRow pr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getHisLen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTickerLog() {
		// TODO Auto-generated method stub
		return "";
	}


}
