package indicators;

import java.util.ArrayList;

import pureData.priceRow;
import tools.Pures;
import tools.priceFields;

public class StandardDeviation {
	
	ArrayList<Double> data = new ArrayList<Double>();
	int SDStep = 0;
	NMA ma = null;
	double SD = 0;
	
	private double SDCal(){
		double sum = 0;
		for (int i = 0; i < data.size(); i++)
			sum += Math.pow((data.get(i)-ma.MA), 2);
		double var = sum / SDStep;
		double sd = Math.sqrt(var);
		return sd;
	}
	
	public StandardDeviation(ArrayList<Double> d){
		for (int i = 0; i < d.size(); i++){
			data.add(d.get(i));
		}
		SDStep = d.size();
		ma = new NMA(data);
		SD = SDCal();
	}
	
	public StandardDeviation(ArrayList<priceRow> pr, int step, priceFields pf){
		SDStep = step;
		ArrayList<Double> d = Pures.getPrices(pr, step, pf);
		for (int i = 0; i < d.size(); i++){
			data.add(d.get(i));
		}
		ma = new NMA(data);
		SD = SDCal();
		for(int i = step; i < pr.size(); i++){
			NewSD(Pures.getPrice(pr.get(i),pf));
		}
	}
	
	public double NewSD(double newData){
		data.remove(0);
		data.add(newData);
		ma.NewMA(newData);
		SD = SDCal();
		return SD;
	}

}
