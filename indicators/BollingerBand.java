package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class BollingerBand {
	
	public int BBMAStep = 0;
	public int SDDis = 0;
	
	public double[] Band = new double[3];
	
	public StandardDeviation sd = null;
	
	private double[] BBInit(ArrayList<Double> data, int dis){
		BBMAStep = data.size();
		SDDis = dis;
		sd = new StandardDeviation(data);
		Band[1] = sd.ma.MA;
		Band[2] = Band[1] + SDDis * sd.SD;
		Band[0] = Band[1] - SDDis * sd.SD;
		return Band;
	}
	
	public BollingerBand(ArrayList<Double> data, int dis){
		BBInit(data,dis);
	}
	
	public BollingerBand(ArrayList<priceRow> pr, int step, priceFields pf,int dis){
		BBInit(Pures.getPrices(pr, step, pf),dis);
		for (int i = step; i < pr.size(); i++)
			NewBand(Pures.getPrice(pr.get(i),pf));
	}
	
	public double[] NewBand(double npr){
		sd.NewSD(npr);
		Band[1] = sd.ma.MA;
		Band[2] = Band[1] + SDDis * sd.SD;
		Band[0] = Band[1] - SDDis * sd.SD;
		return Band;
	}

}
