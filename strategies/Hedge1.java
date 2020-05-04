package strategies;

import java.util.ArrayList;
import java.util.Calendar;

import aTWorker.strategy;
import pureData.priceRow;
import stratDataTypes.Signal;
import stratDataTypes.TradeType;
import stratDataTypes.TradesCollectionData;

public class Hedge1 implements strategy {
	
	double inDiff = 0.001;
	double outDiff = 0.001;
	int max = 10;
		
	double[] ranges = new double[4];
	
	boolean trRange = false;
	boolean dataNInj = true;
	
	TradeType state = TradeType.NotIn;
	
	int Shorts = 0;
	int Longs = 0;
	
	public TradesCollectionData TD = null;
	String WLog = "";
	
	public Hedge1(double id, double od, int m){
		inDiff = id;
		outDiff = od;
		max = m;
		TD = new TradesCollectionData(0, 0,false);
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
		
	}

	@Override
	public void DataHandler(priceRow pr) {
		
		WLog = pr.rDate.getTime() + "," + pr.rBC;
		
		if (pr.rDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY 
				||  (pr.rDate.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY 
						&& pr.rDate.get(Calendar.HOUR_OF_DAY) >= 12)){
			WLog += "\r\n";
		}else{ 
			if (dataNInj){
				ranges[1] = pr.rBC - inDiff / 2;
				ranges[2] = pr.rBC + inDiff / 2;
				ranges[0] = ranges[1] - outDiff;
				ranges[3] = ranges[2] + outDiff;
				dataNInj = false;
				WLog += "\r\n";
				//System.out.println(pr.rBC + " - " + ranges[0] + " - " + ranges[1] + " - " + ranges[2] + " - " + ranges[3]);
			}else{
				WLog += "," + ranges[0] + "," + ranges[1] + "," + ranges[2] + "," + ranges[3];   
				SignalSender(pr);
			}
		}
			
	}

	@Override
	public void SignalSender(priceRow pr) {
		priceHedge(pr.rBC,pr);
	}
	
	private void priceHedge(double p, priceRow pr){
		Signal sig = Signal.Wait;
		String upLog = "";
		if (state.equals(TradeType.NotIn)){
			if (p >= ranges[2]){
				sig = Signal.Buy;
				Longs++;
				state = TradeType.Hedge;
			}else if (p <= ranges[1]){
				sig = Signal.Sell;
				Shorts++;
				state = TradeType.Hedge;
			}
			upLog = TD.UpdateHedge(pr, sig);
		}else if (state.equals(TradeType.Hedge)){
			if (p >= ranges[2] && Shorts > Longs){
				if (Longs + Shorts < max){
					sig = Signal.Buy;
					int sum = 2 * Shorts + 1 - Longs;
					for (int i = 0; i < sum; i++){
						upLog += TD.UpdateHedge(pr, sig);
					}
					Longs = sum + Longs;
				}else{
					sig = Signal.CloseSell;
					Shorts = 0;
					upLog += TD.UpdateHedge(pr, sig);
				}
			}else if (p <= ranges[1] && Shorts < Longs){
				if (Shorts + Longs < max){
					sig = Signal.Sell;
					int sum = 2 * Longs + 1 - Shorts;
					for (int i = 0; i < sum; i++){
						upLog += TD.UpdateHedge(pr, sig);
					}
					Shorts = sum + Shorts;
				}else{
					sig = Signal.CloseBuy;
					Longs = 0;
					upLog += TD.UpdateHedge(pr, sig);
				}
			}else if (p >= ranges[3] || p <= ranges[0]){
				sig = Signal.Close;
				int size = TD.openTL.size();
				for (int i = 0; i < size; i++)
					upLog += TD.UpdateHedge(pr, sig);
				if (Longs > Shorts){
					ranges[1] = p - inDiff;
					ranges[2] = p;
				}else{
					ranges[1] = p;
					ranges[2] = p + inDiff;
				}
				ranges[0] = ranges[1] - outDiff;
				ranges[3] = ranges[2] + outDiff;
				Shorts = 0;
				Longs = 0;
				state = TradeType.NotIn;
				if (p >= ranges[2]){
					sig = Signal.Buy;
					Longs++;
					state = TradeType.Hedge;
				}else if (p <= ranges[1]){
					sig = Signal.Sell;
					Shorts++;
					state = TradeType.Hedge;
				}
				upLog += TD.UpdateHedge(pr, sig);
			}else 
				upLog += TD.UpdateHedge(pr, sig);
		}
		WLog += "," + state.toString() + "," + TD.State.toString() + "," + sig.toString() + "," + Shorts + "," + Longs + upLog + "\r\n";
	}

	@Override
	public TradesCollectionData getTradesData() {
		return TD;
	}

	/*@Override
	public ArrayList<TimeSeries> getSeries() {
		return series;
	}*/

	@Override
	public String getLog() {
		return WLog;
	}

	@Override
	public int getLen() {
		return 0;
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
