package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class ATR {
	
	public double ATR = 0;
	double LastClose = 0;
	int ATRStep = 0;
	SMA ATRMA = null;
	
	public ATR(ArrayList<Double> High, ArrayList<Double> Low, ArrayList<Double> Close){
		ArrayList<Double> TR = new ArrayList<Double>();
		for (int i = 0; i < High.size(); i++){
			TR.add(TRCal(High.get(i), Low.get(i), Close.get(i)));
		}
		ATRStep = High.size();
		ATRMA = new SMA(TR);
		ATR = ATRMA.SMA;
	}
	
	public ATR(ArrayList<priceRow> pr,int step, priceFields h, priceFields l, priceFields c){
		ArrayList<Double> TR = new ArrayList<Double>();
		ArrayList<Double> High = Pures.getPrices(pr,step,h);
		ArrayList<Double> Low = Pures.getPrices(pr,step,l);
		ArrayList<Double> Close = Pures.getPrices(pr,step,c);
		for (int i = 0; i < step; i++){
			TR.add(TRCal(High.get(i), Low.get(i), Close.get(i)));
		}
		ATRStep = step;
		ATRMA = new SMA(TR);
		for(int i = step; i < pr.size(); i++){
			NewATR(Pures.getPrice(pr.get(i), h),Pures.getPrice(pr.get(i), l),
					Pures.getPrice(pr.get(i), c));
		}
		ATR = ATRMA.SMA;
	}
	
	public double NewATR(double High, double Low, double Close){
		double NewTR = TRCal(High, Low, Close);
		ATR = ATRMA.NewMA(NewTR);
		return ATR;
	}
	
	private double TRCal(double High, double Low, double Close){
		double tR = High - Low;
		double yH = Math.abs(LastClose - High);
		double yL = Math.abs(LastClose - Low);
		LastClose = Close;
		double MtRyH = Math.max(tR, yH);
		double TR = Math.max(MtRyH, yL);
		return TR;
	}

}
