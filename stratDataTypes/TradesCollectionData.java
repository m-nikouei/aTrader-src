package stratDataTypes;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;

public class TradesCollectionData {
	
	//public Status tStatus = Status.notIn;

	public TradeType State = TradeType.NotIn;
	public double NetProfit = 0;
	public double TotalProfit = 0;
	public double TotalLoss = 0;
	public int BoughtNum = 0;
	public int SoldNum = 0;
	public int TradeNum = 0;
	public int PTNum = 0;
	public int LTNum = 0;
	public int maxOpen = 0;
	
	public double stopDist = 0;
	public double limitDist = 0;
	public boolean TrailingStopF = false;
		
	public ArrayList<TradeInfo> closedTL = new ArrayList<TradeInfo>();
	public ArrayList<TradeInfo> openTL = new ArrayList<TradeInfo>();
	
	public TradesCollectionData(double sd, double ld, boolean tsf){
		stopDist = sd;
		limitDist = ld;
		TrailingStopF = tsf;
	}
	
	private String Long(priceRow pr){
		BoughtNum++;
		String ret = "";
		TradeInfo TI = new TradeInfo();
		TI.tradeTime = 1;
		TI.entryPrice = pr.rAC;
		TI.currPrice = pr.rBC;
		TI.Type = TradeType.Long;
		TI.entryTime = pr.rDate;
		TI.solidStop = TI.entryPrice - stopDist;
		TI.solidLimit = TI.entryPrice + limitDist;
		openTL.add(TI);
		ret += ",Bot," + Pures.doubleFPoint(TI.entryPrice) + ","
				+ Pures.doubleFPoint(TI.currPrice);
		return ret;
	}
	
	private String Short(priceRow pr){
		SoldNum++;
		String ret = "";
		TradeInfo TI = new TradeInfo();
		TI.tradeTime = 1;
		TI.entryPrice = pr.rBC;
		TI.currPrice = pr.rAC;
		TI.Type = TradeType.Short;
		TI.entryTime = pr.rDate;
		TI.solidStop = TI.entryPrice + stopDist;
		TI.solidLimit = TI.entryPrice - limitDist;
		openTL.add(TI);
		ret += ",Sol," + Pures.doubleFPoint(TI.entryPrice) + ","
				+ Pures.doubleFPoint(TI.currPrice);
		return ret;
	}
	
	private String OpenTrade(Signal ts, priceRow pr){
		String ret = "";
		if (ts.equals(Signal.Buy)){
			ret += Long(pr);
		}else if (ts.equals(Signal.Sell)){
			ret += Short(pr);
		}
		TradeNum++;
		return ret;
	}
	
	public String UpdateTrade(Signal sig, priceRow pr){
		String ret = "";
		if (State.equals(TradeType.NotIn)){
			if (sig.equals(Signal.Buy)){
				ret += OpenTrade(sig, pr);
				State = TradeType.Long;
			}else if (sig.equals(Signal.Sell)){
				ret += OpenTrade(sig, pr);
				State = TradeType.Short;
			}
		}else if (State.equals(TradeType.Long)){
			if (sig.equals(Signal.Buy))
				ret = OpenTrade(sig, pr);
			else if (sig.equals(Signal.Sell))
				ret = CloseTrade(pr);
			else if (sig.equals(Signal.Wait)){
				for (int i = 0; i < openTL.size(); i++){
					openTL.get(i).tradeTime++;
					openTL.get(i).currPrice = pr.rBC;
					if (openTL.get(i).maxP < pr.rBC)
						openTL.get(i).maxP = pr.rBC;
					if (openTL.get(i).minP > pr.rBC)
						openTL.get(i).minP = pr.rBC;
				}
				ret = ",,," + Pures.doubleFPoint(pr.rBC);
				ret += stopHandler(pr);
			}
		}else if (State.equals(TradeType.Short)){
			if (sig.equals(Signal.Sell))
				ret = OpenTrade(sig, pr);
			else if (sig.equals(Signal.Buy))
				ret = CloseTrade(pr);
			else if (sig.equals(Signal.Wait)){
				for (int i = 0; i < openTL.size(); i++){
					openTL.get(i).tradeTime++;
					openTL.get(i).currPrice = pr.rAC;
					if (openTL.get(i).maxP < pr.rAC)
						openTL.get(i).maxP = pr.rAC;
					if (openTL.get(i).minP > pr.rAC)
						openTL.get(i).minP = pr.rAC;
				}
				ret = ",,," + Pures.doubleFPoint(pr.rAC);
				ret += stopHandler(pr);
			}
		}
		if (openTL.size() > maxOpen)
			maxOpen = openTL.size();
		return ret;
	}
	
	public String UpdateHedge(priceRow pr, Signal sig){
		String ret = "";
		if (State.equals(TradeType.NotIn)){
			if (sig.equals(Signal.Buy) || sig.equals(Signal.Sell)){
				ret += OpenTrade(sig, pr);
				State = TradeType.Hedge;
			}
		}else if (State.equals(TradeType.Hedge)){
			if (sig.equals(Signal.Buy) || sig.equals(Signal.Sell)){
				ret = OpenTrade(sig, pr);
			}else if (sig.equals(Signal.CloseBuy) || sig.equals(Signal.CloseSell)){
				ret += CloseHedge(pr, sig);	
			}else if (sig.equals(Signal.Close)){
				ret = CloseTrade(pr);
			}else if (sig.equals(Signal.Wait)){
				for (int i = 0; i < openTL.size(); i++){
					openTL.get(i).tradeTime++;
					if (openTL.get(i).Type.equals(TradeType.Long))
						openTL.get(i).currPrice = pr.rBC;
					else if (openTL.get(i).Type.equals(TradeType.Short))
						openTL.get(i).currPrice = pr.rAC;
				}
				ret = ",,," + Pures.doubleFPoint(pr.rBC);
				ret += stopHandler(pr);
			}
		}
		return ret;
	}
	
	private String stopHandler(priceRow pr){
		//System.out.println("STOPLIMIT: " + stopDist + " " + limitDist);
		int sign = 0;
		if (State.equals(TradeType.Long))
			sign = 1;
		else if (State.equals(TradeType.Short))
			sign = -1;
		
		String ret = "";
		String terminal = "";
		if (stopDist > 0){
			for (int i = 0; i < openTL.size(); i++){
				double stopdist = sign * (openTL.get(i).currPrice - openTL.get(i).solidStop);
				//ret += i + " Stop: " + Pures.doubleFPoint(stopDist) + "\r\n";
				terminal += " Stop: " + Pures.doubleFPoint(openTL.get(i).solidStop) + " sd:" + Pures.doubleFPoint(stopdist);
				if (stopdist < 0){
					CloseTrade(pr);
					//ret += "Stop Reached!! Trade " + i + " is closed!!\r\n";
				}else{
					break;
				}
			}
		}
		if (limitDist > 0){
			for (int i = 0; i< openTL.size(); i++){
				double limitdist = sign * (openTL.get(i).solidLimit - openTL.get(i).currPrice);
				terminal += "Limit: " + Pures.doubleFPoint(openTL.get(i).solidLimit) + " ld:" + Pures.doubleFPoint(limitdist);
				if (limitdist < 0){
					CloseTrade(pr);
				}else{
					break;
				}
			}
		}
		if (TrailingStopF) {
			for (int i = 0; i< openTL.size(); i++){
				if (openTL.get(i).trailStop > 0) {
					if (sign * (openTL.get(i).currPrice - openTL.get(i).trailStop) > 0) {
						openTL.get(i).solidStop += openTL.get(i).currPrice
																			  - openTL.get(i).trailStop;
						openTL.get(i).trailStop = openTL.get(i).currPrice;
					}
				}else {
					openTL.get(i).trailStop =  openTL.get(i).currPrice;
				}
				terminal += " CurP: " + Pures.doubleFPoint(openTL.get(i).currPrice) + 
									" TS: " + Pures.doubleFPoint(openTL.get(i).trailStop);
			}
		}
		//System.out.println(terminal);		
		return ret;
	}
	
	public String CloseTrade(priceRow pr){
		String ret = "";
		TradeInfo TI = openTL.get(0);
		openTL.remove(0);
		TI.tradeTime++;
		TI.exitTime = pr.rDate;
		if (State.equals(TradeType.Long)){
			TI.currPrice = pr.rBC;
			TI.exitPrice = pr.rBC;
			TI.profit = TI.exitPrice - TI.entryPrice;
		}else if (State.equals(TradeType.Short)){
			TI.currPrice = pr.rAC;
			TI.exitPrice = pr.rAC;
			TI.profit = TI.entryPrice - TI.exitPrice;
		}else if (State.equals(TradeType.Hedge)){
			if (TI.Type.equals(TradeType.Long)){
				TI.currPrice = pr.rBC;
				TI.exitPrice = pr.rBC;
				TI.profit = TI.exitPrice - TI.entryPrice;
			}else if (TI.Type.equals(TradeType.Short)){
				TI.currPrice = pr.rAC;
				TI.exitPrice = pr.rAC;
				TI.profit = TI.entryPrice - TI.exitPrice;
			}
		}
		closedTL.add(TI);
		ret += ",Cls,," + Pures.doubleFPoint(TI.currPrice) + "," + 
				Pures.doubleFPoint(TI.profit) + "," + TI.tradeTime;
		if (TI.profit >= 0){
			PTNum++;
			TotalProfit += TI.profit;
			ret += ",profit";
		}else{
			LTNum++;
			TotalLoss += TI.profit;
			ret+= ",loss";
		}
		NetProfit += TI.profit;
		/*ret += "---------------------------------------\r\n";
		ret += "TP: " + Pures.doubleFPoint(TotalProfit) + " TL: " + Pures.doubleFPoint(TotalLoss) + " NP: " + Pures.doubleFPoint(NetProfit) +
				" TN: " + TradeNum + " PN: " + PTNum + " LN: " + LTNum + "\r\n";
		ret += "---------------------------------------\r\n";*/
		if (openTL.size() == 0){
			//ret += "***************************************\r\n";
			State = TradeType.NotIn;
		}
		return ret;
	}
	
	public String CloseHedge(priceRow pr, Signal sig){
		String ret = "";
		int i = 0;
		while (i < openTL.size()){
			if ((sig.equals(Signal.CloseBuy) && openTL.get(i).Type.equals(TradeType.Long)) ||
					(sig.equals(Signal.CloseSell) && openTL.get(i).Type.equals(TradeType.Short)))
				ret += CloseByInd(pr, i);
			else i++;
		}
		/*System.out.println(pr.rDate.getTime() + ": " + sig.toString());
		for (i = 0; i < openTL.size(); i++)
			System.out.println("Trade " + i + ": " + openTL.get(i).Type);
		System.out.println("**************************");*/
		return ret;
	}
	
	public String CloseByInd(priceRow pr, int ind){
		String ret = "";
		TradeInfo TI = openTL.get(ind);
		openTL.remove(ind);
		TI.tradeTime++;
		TI.exitTime = pr.rDate;
		if (State.equals(TradeType.Long)){
			TI.currPrice = pr.rBC;
			TI.exitPrice = pr.rBC;
			TI.profit = TI.exitPrice - TI.entryPrice;
		}else if (State.equals(TradeType.Short)){
			TI.currPrice = pr.rAC;
			TI.exitPrice = pr.rAC;
			TI.profit = TI.entryPrice - TI.exitPrice;
		}else if (State.equals(TradeType.Hedge)){
			if (TI.Type.equals(TradeType.Long)){
				TI.currPrice = pr.rBC;
				TI.exitPrice = pr.rBC;
				TI.profit = TI.exitPrice - TI.entryPrice;
			}else if (TI.Type.equals(TradeType.Short)){
				TI.currPrice = pr.rAC;
				TI.exitPrice = pr.rAC;
				TI.profit = TI.entryPrice - TI.exitPrice;
			}
		}
		closedTL.add(TI);
		ret += ",Cls,," + Pures.doubleFPoint(TI.currPrice) + "," + 
				Pures.doubleFPoint(TI.profit) + "," + TI.tradeTime;
		if (TI.profit >= 0){
			PTNum++;
			TotalProfit += TI.profit;
			ret += ",profit";
		}else{
			LTNum++;
			TotalLoss += TI.profit;
			ret+= ",loss";
		}
		NetProfit += TI.profit;
		/*ret += "---------------------------------------\r\n";
		ret += "TP: " + Pures.doubleFPoint(TotalProfit) + " TL: " + Pures.doubleFPoint(TotalLoss) + " NP: " + Pures.doubleFPoint(NetProfit) +
				" TN: " + TradeNum + " PN: " + PTNum + " LN: " + LTNum + "\r\n";
		ret += "---------------------------------------\r\n";*/
		if (openTL.size() == 0){
			//ret += "***************************************\r\n";
			State = TradeType.NotIn;
		}
		return ret;
	}
	
}


