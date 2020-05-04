package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class RSI {
	
	public double RSI = 0;
	public int RSIStep = 14;
	
	private SMA uEMA = null;
	private SMA dEMA = null;
	private double lclose = 0;
	
	private double RSIinit(ArrayList<Double> data){
		ArrayList<Double> U = new ArrayList<Double>();
		ArrayList<Double> D = new ArrayList<Double>();
		U.add(data.get(0));
		D.add(0d);
		lclose = data.get(0);
		for (int i = 1; i < data.size(); i++){
			double cclose = data.get(i);
			double u = 0;
			double d = 0;
			if (cclose >= lclose){
				u = cclose - lclose;
			}else{
				d = lclose - cclose;
			}
			U.add(u);
			D.add(d);
			lclose = cclose;
		}
		uEMA = new SMA(U);
		dEMA = new SMA(D);
		double RS = uEMA.SMA / dEMA.SMA;
		double rsI = 100 - 100/(1 + RS);
		return rsI;
	}
	
	public RSI(ArrayList<Double> data){
		RSIStep = data.size();
		RSI = RSIinit(data);
	}
	
	public RSI(ArrayList<priceRow> pr, int step, priceFields pf){
		RSI = RSIinit(Pures.getPrices(pr, step, pf));
		RSIStep = step;
		for(int i = step; i < pr.size(); i++){
			NewRSI(Pures.getPrice(pr.get(i), pf));
		}
	}
	
	public double NewRSI(double nclose){
		double u = 0;
		double d = 0;
		if (nclose >= lclose){
			u = nclose - lclose;
		}else{
			d = lclose - nclose;
		}
		lclose = nclose;
		uEMA.NewMA(u);
		dEMA.NewMA(d);
		double RS = uEMA.SMA / dEMA.SMA;
		RSI = 100 - 100/(1 + RS);
		return RSI;
	}

}
