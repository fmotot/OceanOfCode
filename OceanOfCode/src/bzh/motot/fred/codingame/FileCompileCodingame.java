package bzh.motot.fred.codingame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileCompileCodingame {
	private static String SEPARATOR = "//#################################################################################################";
	
	
	private static ArrayList<File> list;

	
	public static void main(String[] args) {
		list = new ArrayList<File>();

		try {
			File dir = new File("C:\\Users\\fmoto\\git\\repository\\OceanOfCode\\src");
			
			getListFile(dir);
			
			FileReader fr;
			String line;
			BufferedReader br;
			
			FileWriter fw = new FileWriter("codingame.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			for (File f : list) {
				line = "";
				fr = new FileReader(f);
				br = new BufferedReader(fr);
				boolean start = false;
				String[] split = null;
				
				while(!start) {
					 split = br.readLine().split(" "); 
					for (String s : split) {
						if (s.contains("class")) {
							start = true;
						}
					}	
				}
				
				for (int i = 1; i < split.length; i++) {
					line += split[i] +" ";
				}
				bw.write(line + "\n");
				
				while((line = br.readLine()) != null)
				{
					bw.write(line + "\n");
				}
				br.close();
				
				
				bw.write("\n");
				for (int i = 0; i < 6; i++) {
					bw.write(SEPARATOR + "\n");
				}
				bw.write("\n");
				
				bw.flush();
				
			}
			
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void getListFile(File dir){
		
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				getListFile(file);
			}
			else {
				if (!file.getName().contentEquals("FileCompileCodingame.java") && !file.getName().contentEquals("Launcher.java")) {
					list.add(file);
				}
			}	
		}
	}
}
