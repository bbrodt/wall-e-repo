package org.wally.control.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import org.wally.control.WallyController;

public class FileUtils {

	public static String load(File file) {
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			fr.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showErrorDialog("Can't open file "+file.getName()+"\n"+e.getMessage());
		}
		return null;
	}
	
	public static boolean save(File file, String text) {
		return save(file,text,null);
	}
	
	public static boolean saveScript(File file, String text) {
		return save(file,text,"js");
	}

	public static boolean save(File file, String text, String extension) {
		try {
			if (extension!=null && !file.getName().endsWith("."+extension)) {
				file = new File(file.getAbsolutePath()+"."+extension);
				if (!file.exists())
					file.createNewFile();
			}
			FileWriter fr = new FileWriter(file);
			BufferedWriter br = new BufferedWriter(fr);
			br.append(text);
			br.close();
			fr.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			UIUtils.showErrorDialog("Can't save file "+file.getName()+"\n"+e.getMessage());
		}
		return false;
	}

	public static String getScriptDirectory() {
		return System.getProperty("user.home")+"/wall-e";
	}
}
