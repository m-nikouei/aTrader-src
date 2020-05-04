package pureData;

import tools.Pures;

public class FXAccount {

	private String AccountName = "";
	private double Equity = 0d;
	private double DayPL = 0d;
	private double GrossPL = 0d;
	private double UsedMr = 0d;
	private double UsedMainMr = 0d;
	private double UsableMr = 0d;
	
	public FXAccount(String an, double e, double d, double g, double u, double um, double m){
		AccountName = an; Equity = e; DayPL = d; GrossPL = g; UsedMr = u; UsedMainMr = um; UsableMr = m;
	}
	
	public String getAccountName(){
		return AccountName;
	}
	
	public double getEquity(){
		return Equity;
	}
	
	public double getDayPL(){
		return DayPL;
	}
	
	public double getGrossPL(){
		return GrossPL;
	}
	
	public double getUsedMr(){
		return UsedMr;
	}
	
	public double getUsedMainMr(){
		return UsedMainMr;
	}
	
	public double getUsableMr(){
		return UsableMr;
	}
	
	public String[] getAccountRow(){
		String[] rStr = {getAccountName(), Pures.doubleFPoint(getEquity()), 
				Pures.doubleFPoint(getDayPL()), Pures.doubleFPoint(getUsedMr()),Pures.doubleFPoint(getUsedMainMr()),
				Pures.doubleFPoint(getUsableMr()), Pures.doubleFPoint(getGrossPL())};
		return rStr;
	}

}
