package tools;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class CalTools {
	
	public static Calendar CalJump(Calendar sPointgiven, String TF, int step) throws ParseException{
		Calendar sPoint = Calendar.getInstance();
		sPoint.setTime(sPointgiven.getTime());
		if (TF.equals("s1"))
			sPoint.add(Calendar.SECOND, step);
		else if (TF.equals("m1"))
			sPoint.add(Calendar.MINUTE, step);
		else if (TF.equals("m5"))
			sPoint.add(Calendar.MINUTE, 5 * step);
		else if (TF.equals("m15"))
			sPoint.add(Calendar.MINUTE, 15 * step);
		else if (TF.equals("m30"))
			sPoint.add(Calendar.MINUTE, 30 * step);
		else if (TF.equals("H1"))
			sPoint.add(Calendar.HOUR, step);
		else if (TF.equals("H2"))
			sPoint.add(Calendar.HOUR, 2 * step);
		else if (TF.equals("H3"))
			sPoint.add(Calendar.HOUR, 3 * step);
		else if (TF.equals("H4"))
			sPoint.add(Calendar.HOUR, 4 * step);
		else if (TF.equals("H6"))
			sPoint.add(Calendar.HOUR, 6 * step);
		else if (TF.equals("H8"))
			sPoint.add(Calendar.HOUR, 8 * step);
		else if (TF.equals("D1"))
			sPoint.add(Calendar.DAY_OF_MONTH, step);
		else if (TF.equals("W1"))
			sPoint.add(Calendar.DAY_OF_MONTH,  7 * step);
		else if (TF.equals("M1"))
			sPoint.add(Calendar.MONTH, step);
		return sPoint;
	}
	
	public static Calendar CalMover(Calendar cal, String flag){
		boolean changed = false;
		int day = cal.get(Calendar.DAY_OF_WEEK);
		int mday = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (flag.equals("f")){
			if (day == 7){
				changed = true;
				mday++;
			}else if (day == 1 && hour < 17)
				changed = true;
			else if (day == 6 && hour >= 17){
				changed = true;
				mday = mday + 2;
			}
		}else if (flag.equals("b")){
			if (day == 7){
				changed = true;
				mday--;
			}else if (day == 1 && hour < 17){
				changed = true;
				mday = mday - 2;
			}else if (day == 6 && hour >= 17){
				changed = true;
			}
		}
		Calendar res = null;
		if (changed)
			res = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					mday, 17, 0, 0);
		else
			res = cal;
		return res;
	}
	
	public static Calendar StartCal(String TF, int steps, Calendar end) throws ParseException{
		Calendar start = null;
		int tf = Integer.parseInt(TF.substring(1));
		end = CalMover(end, "f");
		Calendar back = CalJump(end, TF, -1 * steps);
		Calendar backf = CalMover(back, "f");
		long diff = end.getTimeInMillis() - backf.getTimeInMillis();
		long mins  = TimeUnit.MILLISECONDS.toMinutes(diff) / tf;
		if (mins < steps){
			long rem = steps - mins;
			Calendar backb = CalMover(back, "b");
			start = CalJump(backb,TF, -1 * (int)rem);
		}else
			start = backf;
		return start;
	}
	
}
