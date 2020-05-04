package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class MACD {
	
	int sStep = 12;
	int bStep = 26;
	int mStep = 9;
	
	public EMA sEMA = null;
	public EMA bEMA = null;
	public EMA mEMA = null;
	
	public double MACDL = 0;
	public double SIGN = 0;
	public double HIST = 0;
	
	public MACD(ArrayList<priceRow> prices, int ss, int bs, int ms,priceFields pf){
			sStep = ss;
			bStep = bs;
			mStep = ms;
			ArrayList<Double> sP = Pures.getPrices(prices, sStep, pf);
			ArrayList<Double> bP = Pures.getPrices(prices, bStep, pf);
			sEMA = new EMA(sP);
			for (int i = sStep; i < bStep; i++){
				sEMA.NewMA(prices.get(i).rBC);
			}
			bEMA = new EMA(bP);
			ArrayList<Double> MACDLL = new ArrayList<Double>();
			for (int i = bStep; i < bStep + mStep; i++){
				sEMA.NewMA(prices.get(i).rBC);
				bEMA.NewMA(prices.get(i).rBC);
				MACDLL.add(sEMA.EMA - bEMA.EMA);
			}
			MACDL = MACDLL.get(MACDLL.size() - 1);
			mEMA = new EMA(MACDLL);
			SIGN = mEMA.EMA;
			HIST = MACDL - SIGN;
			for(int i = bStep + mStep; i < prices.size(); i++)
				NewMACD(Pures.getPrice(prices.get(i), pf));
	}
	
	public void NewMACD(double npr){
		sEMA.NewMA(npr);
		bEMA.NewMA(npr);
		MACDL = sEMA.EMA - bEMA.EMA;
		mEMA.NewMA(MACDL);
		SIGN = mEMA.EMA;
		HIST = MACDL - SIGN;
		
	}

}
