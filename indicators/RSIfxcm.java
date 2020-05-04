package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class RSIfxcm {
	
	public double RSI = 0;
	public int RSIStep = 14;
	
	private double lclose = 0;
	private double lPos = 0;
	private double lNeg = 0;
	
	private void RSIer(double nowP, double preP){
		double sump = 0;
		double sumn = 0;
		double diff = nowP - preP;
		if (diff >=0)
			sump = diff;
		else
			sumn = - diff;
		double pos = (lPos * (RSIStep - 1) + sump) / RSIStep;
		double neg = (lNeg * (RSIStep - 1) + sumn) / RSIStep;
		lPos = pos;
		lNeg = neg;
		if (neg == 0)
			RSI = 0;
		else
			RSI = 100 - (100 / (1 + pos / neg));
	}
	
	private double RSIinit(ArrayList<Double> data){
		double sump = 0;
		double sumn = 0;
		for (int i = 1; i < RSIStep; i++){
			double diff = data.get(i) - data.get(i - 1);
			if (diff >=0)
				sump += diff;
			else
				sumn += - diff;
		}
		double pos = sump / RSIStep;
		double neg = sumn / RSIStep;
		//lPos = pos;
		//lNeg = neg;
		if (neg == 0)
			RSI = 0;
		else
			RSI = 100 - (100 / (1 + pos / neg));
		return RSI;
	}
	
	public RSIfxcm(ArrayList<Double> data){
		RSIStep = data.size();
		RSIinit(data);
	}
	
	public RSIfxcm(ArrayList<priceRow> pr, int step, priceFields pf){
		RSIinit(Pures.getPrices(pr, step, pf));
		RSIStep = step;
		for(int i = step; i < pr.size(); i++){
			NewRSIfxcm(Pures.getPrice(pr.get(i), pf));
		}
	}
	
	public void NewRSIfxcm(double nclose){
		RSIer(nclose, lclose);
		lclose = nclose;
	}

}
