package tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GUISettings {
	
	public String settingsAd = "nset.atr"; //"settings.atr";
	
	public String userName = "";
	public String passWrd = "";
	public String serverAd = "";
	public String acType = "";
	public String accountNum = "";
	
	public double maxEq = 0d;
	public double maxUsedMargin = 0d;
	public double sessionProfit = 0d;
	
	public String dataFolder = "";
	
	public boolean readSettings(){
		boolean readRes = false;
		try{
			FileReader fileReader = new FileReader(settingsAd);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			while (line  != null){
				String[] splitted = line.split(" = ");
				if (splitted[0].equals("username")){
					userName = splitted[1];
				}else if (splitted[0].equals("passwrd")){
					passWrd = splitted[1];
				}else if (splitted[0].equals("server")){
					serverAd = splitted[1];
				}else if (splitted[0].equals("type")){
					acType = splitted[1];
				}else if (splitted[0].equals("account")){
					accountNum = splitted[1]; 
				}else if (splitted[0].equals("maxEq")){
					maxEq = Double.parseDouble(splitted[1]);
				}else if (splitted[0].equals("datafolder")){
					dataFolder = splitted[1];
				}
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			readRes = true;
		}catch(FileNotFoundException ex) {
			System.out.println("Error: Setting file not found!!");
		} catch (IOException e) {
			System.out.println("Error: cannot open setting file!!");
		}
		return readRes;
	}

}
