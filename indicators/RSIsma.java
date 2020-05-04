package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class RSIsma {
	
	public double RSI = 0;
	public int RSIStep = 14;
	
	private ArrayList<Double> Data = null;
	
	private double RSIer(){
		if (Data.size() == RSIStep){
			double sump = 0;
			double sumn = 0;
			for (int i = 1; i < RSIStep; i++){
				double diff = Data.get(i) - Data.get(i - 1);
					if (diff >=0)
						sump += diff;
					else
						sumn += - diff;
			}
			double pos = sump / RSIStep;
			double neg = sumn / RSIStep;
			if (neg == 0)
				RSI = 0;
			else
				RSI = 100 - (100 / (1 + pos / neg));
		}else
			RSI = 0;
		return RSI;
	}
	
	public RSIsma(ArrayList<Double> data){
		RSIStep = data.size();
		Data = data;
		RSIer();
	}
	
	public RSIsma(ArrayList<priceRow> pr, int step, priceFields pf){
		RSIStep = step;
		Data = Pures.getPrices(pr, step, pf);
		RSIer();
		for(int i = step; i < pr.size(); i++){
			NewRSI(Pures.getPrice(pr.get(i), pf));
		}
	}
	
	public void NewRSI(double nclose){
		Data.remove(0);
		Data.add(nclose);
		RSIer();
	}

}
