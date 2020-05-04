package strategies;

import java.util.ArrayList;

import aTWorker.strategy;
import indicators.BollingerBand;
import pureData.priceRow;
import stratDataTypes.MATrend;
import stratDataTypes.Signal;
import stratDataTypes.TradeType;
import stratDataTypes.TradesCollectionData;
import tools.Pures;
import tools.priceFields;

public class BBStrat implements strategy{
	
	int BBStep = 20;
	int BBDis = 2;
	double breakOut = 0.003;
	double stop = 0.003;
	double limit = 0.005;
	BollingerBand BB = null;
	
	TradeType state = TradeType.NotIn;
	MATrend Flag = MATrend.SiG;
	boolean oflag = false;
	
	public TradesCollectionData TD = null;
	String WLog = "";
	
	public BBStrat(int bbs, int bbd, double bo, double s, double l){
		BBStep = bbs;
		BBDis = bbd;
		stop = s;
		limit = l;
		breakOut = bo;
		TD = new TradesCollectionData(stop, limit,true);
		System.out.println("BBand Params -> ");
		System.out.println("BBand Step: " + BBStep);
		System.out.println("BBand Distance: " + BBDis);
		System.out.println("BBand BreakOutBorder " + breakOut);
		System.out.println("BBand Stop: " + stop + " - Limit: " + limit);
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
		BB = new BollingerBand(prl,BBStep, priceFields.BidClose, BBDis);
		
	}

	@Override
	public void DataHandler(priceRow pr) {
		BB.NewBand(pr.rBC);
		if (BB.Band[1] - BB.Band[0] > breakOut){
			Flag = MATrend.DUS;
		}else if (BB.Band[2] - BB.Band[1] > breakOut){
			Flag = MATrend.DUS;
		}else 
			Flag = MATrend.SiG;
		if (!state.equals(TradeType.NotIn))
			oflag = true;
		else 
			oflag = false;
		WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(pr.rBC) + "," + 
				Pures.doubleFPoint(BB.Band[0]) + "," + 	Pures.doubleFPoint(BB.Band[1]) 
				+ "," + Pures.doubleFPoint(BB.Band[2]) + "," + Pures.doubleFPoint((BB.Band[1] - BB.Band[0])) + "," + 
						Pures.doubleFPoint((BB.Band[2] - BB.Band[1])) +
						"," + Flag.toString() + "," + state.toString() + "," + oflag + "\r\n";
		
		//SignalSender(pr);		
	}

	@Override
	public void SignalSender(priceRow pr) {
		
		if (oflag){
			Flag = MATrend.SiG;
		}else{
			if (BB.Band[1] - BB.Band[0] > breakOut){
				Flag = MATrend.DUS;
			}else if (BB.Band[2] - BB.Band[1] > breakOut){
				Flag = MATrend.DUS;
			}
		}
		
		if (Flag.equals(MATrend.DUS)){
			if (pr.rBC < BB.Band[0])
				Flag = MATrend.DnT;
			else if (pr.rBC > BB.Band[2])
				Flag = MATrend.UpT;
		}
		
		String upLog = "";
		Signal sig = Signal.Wait;
		if (state.equals(TradeType.NotIn)){
			if (Flag.equals(MATrend.DnT)){
				state = TradeType.Short;
				sig = Signal.Sell;
				Flag = MATrend.DUS;
				upLog = TD.UpdateTrade(sig, pr);
			}else if (Flag.equals(MATrend.UpT)){
				state = TradeType.Long;
				sig = Signal.Buy;
				Flag = MATrend.DUS;
				upLog = TD.UpdateTrade(sig, pr);
			}
		}else{
			    System.out.print("in Trade");
				if (state.equals(TradeType.Short)){
				    System.out.print("->in Short");	
					if (pr.rBC >= BB.Band[1]){
						System.out.print("->Cross MA");
						sig = Signal.Buy;
					}
				}else if (state.equals(TradeType.Long)){
					System.out.print("->in Long");	
					if (pr.rBC <= BB.Band[1] ){
						System.out.print("->Cross MA");
						sig = Signal.Sell;
					}
				}
				upLog = TD.UpdateTrade(sig, pr);
				if (TD.State.equals(TradeType.NotIn)){
					System.out.print("->Stop-Limit\r\n");
					state = TradeType.NotIn; 
				}
		}
		
		WLog += "," + Flag.toString() + "," + state.toString() + "," + sig.toString() + "," + oflag +
				upLog + "\r\n";
		
		/*System.out.println(pr.rDate.get(Calendar.HOUR_OF_DAY) + ":" + pr.rDate.get(Calendar.MINUTE) + ":" +
				pr.rDate.get(Calendar.SECOND) + " f:" + Flag.toString() + " s:" + state.toString() + " t:" + TD.State.toString() 
				+ " sg:" + sig.toString() + " tdls:" + TD.openTL.size()); 
		System.out.println("p:" + Pures.doubleFPoint(pr.rBC) + " ma:" + Pures.doubleFPoint(BB.Band[1]) );*/
		
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
		return 0;
	}

	public void TickHandler(priceRow pr) {
		if (state.equals(TradeType.NotIn)){
			WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(pr.rBC) + "," + 
					Pures.doubleFPoint(BB.Band[0]) + "," + Pures.doubleFPoint(BB.Band[2]);
		}else{
			WLog = pr.rDate.getTime() + "," + Pures.doubleFPoint(pr.rBC) + "," + Pures.doubleFPoint(BB.Band[1]);
		}
		SignalSender(pr);
	}

	@Override
	public int getHisLen() {
		return BBStep;
	}

	@Override
	public String getTickerLog() {
		return WLog;
	}

}
