package tools;

import java.io.FileWriter;
import java.io.IOException;

public class TXTWriter {

String Fname = "";
	
	public TXTWriter(String fname, boolean newF){
		Fname = fname;
		try {
			FileWriter Tfile = new FileWriter(fname,newF);
			Tfile.write("");
			Tfile.close();
		} catch (IOException e) {
			System.out.println("File Ad: " + Fname);
			//System.out.println("Error: Cannot create file!!");
			e.printStackTrace();
		}
	}
	
	public void write(String txt){
		try {
			FileWriter Tfile = new FileWriter(Fname,true);
			Tfile.write(txt);
			Tfile.close();
		} catch (IOException e) {
			System.out.println("Error: Cannot write to file!!");
			//e.printStackTrace();
		}
	}
	
}
