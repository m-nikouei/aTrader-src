package strategies;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;

import aTWorker.strategy;
import pureData.priceRow;
import stratDataTypes.TradesCollectionData;
import tools.Pures;

public class MCTester implements strategy{
	
	int predStep = 0;
	int window = 0;
	int mcTry = 0;
	double nbrRd  = 0;
	double rdSpd = 0;
	
	priceRow prP = null;
	ArrayList<Double> winData = new ArrayList<Double>();
	
	int moveAhead  = 0;
	int[] inrangePreds = null;
	Guess max = new Guess();
	Guess curr = new Guess();
	
	ArrayList<double[]> prevRes = new ArrayList<double[]>() ;
	
	public TradesCollectionData TD = null;
	String WLog = "";
	
	public MCTester(int ps, int w, int mct,double nr, double rs) {
		predStep = ps;
		window = w;
		nbrRd = nr;
		rdSpd = rs;
		mcTry = mct;
		inrangePreds = new int[predStep];
		TD = new TradesCollectionData(0.003, 0.005,true);
	}

	@Override
	public void DataEntry(ArrayList<priceRow> prl) {
		for (int i = prl.size() - window; i < prl.size(); i++) {
			winData.add(Math.log(prl.get(i).rBC/prl.get(i - 1).rBC));
		}
		//System.out.println(prl.size() + " , " + winData.size());
		prP = prl.get(prl.size() - 1);
	}

	@Override
	public void DataHandler(priceRow pr) {
		predResChecker(pr.rBC, pr.rDate);
		moveAhead++;
		winData.remove(0);
		winData.add(Math.log(pr.rBC/prP.rBC));
		double price = pr.rBC;
		prP = prCopy(pr);
		Statistics stat = new Statistics();
		stat.statCal(winData);
		double[][] mcRes = new double[predStep][mcTry];
		for (int j = 0; j < mcTry; j++) {
		    double[] res = doMonteCarlo(stat,price);
		    for (int k =0; k < predStep; k++)
		    	mcRes[k][j] = res[k];
		}
		double[] aveRes = new double[predStep];
		for (int k =0; k < predStep; k++)
			aveRes[k] = avgCal(mcRes[k]);
		prevRes.add(aveRes);
		if (prevRes.size() > predStep)
			prevRes.remove(0);
		WLog = moveAhead + "," + pr.rDate.getTime().toString();
		for (int i = 0; i < predStep; i++)
			WLog += "," + Pures.doubleFPoint2((double)inrangePreds[i] * 100 / moveAhead, 2) + "%";
		WLog += "," + curr.corrNum;
		if (curr.corrNum > max.corrNum) {
			max.last = curr.last;
			max.corrNum = curr.corrNum;
			WLog += "," + max.last.getTime(); 
		}
		WLog += "\n";
		
	}
	
	private void predResChecker(double pr, Calendar cal) {
		for (int i = 0; i < predStep; i ++) {
			if (prevRes.size() > i) {
					if (Math.abs(prevRes.get(prevRes.size() - 1 - i)[i] - pr) <= nbrRd + i * rdSpd) {
						inrangePreds[i]++;
						curr.last = cal;
						if (i == 0)
							curr.corrNum++;
					}else
						if (i == 0)
							curr.corrNum = 0;
			}
		}
	}
	
	private priceRow prCopy(priceRow from) {
		double[] prs = {from.rBO,from.rBH,from.rBL,from.rBC,
				from.rAO,from.rAH,from.rAL,from.rAC};
		priceRow pr = new priceRow(from.rDate, prs);
		return pr;
	}
	
	private double[] doMonteCarlo (Statistics stat, double prc) {
		double lp = prc;
		double[] preds = new double[predStep];
		Random rnd = new Random(System.nanoTime());
		for (int i = 0; i < predStep; i++) {
			if (stat.sd != 0) {
				NormalDistribution distribution = new NormalDistribution(stat.avg, stat.sd);
				double ninvn = distribution.inverseCumulativeProbability(rnd.nextDouble());
				lp = lp * Math.exp(stat.drift + (stat.sd * ninvn));
				preds[i] = lp;
			}else
				System.out.println(i + " sd 0");
		}
		return preds;
	}
	
	private static double avgCal (double[] rets) {
		double sum = 0;
		for (int i = 0; i < rets.length; i++)
			sum += rets[i];
		return sum / rets.length;
	}

	@Override
	public void SignalSender(priceRow pr) {
		// TODO Auto-generated method stub
		
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

	@Override
	public void TickHandler(priceRow pr) {
		
	}

	@Override
	public int getHisLen() {
		return window + 1;
	}

	@Override
	public String getTickerLog() {
		return "";
	}

}


class Statistics{
	
	double avg = 0;
	double var = 0;
	double sd = 0;
	double drift = 0;
	
	public void statCal(ArrayList<Double> rets) {
		avgCal(rets);
		varCal(rets);
		sd = Math.sqrt(var);
		drift = avg - var / 2;
	}
	
	private void avgCal (ArrayList<Double> rets) {
		double sum = 0;
		for (int i = 0; i < rets.size(); i++)
			sum += rets.get(i);
		avg = sum / rets.size();
	}
	
	private void varCal (ArrayList<Double> rets) {
		double sum = 0;
		for (int i = 0; i < rets.size(); i++)
			sum += Math.pow(rets.get(i) - avg, 2);
		var = sum / rets.size();
	}
	
}

class Guess{
	
	Calendar last = null;
	int corrNum = 0;
	
}
