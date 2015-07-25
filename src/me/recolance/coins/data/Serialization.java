package me.recolance.coins.data;

import java.util.ArrayList;
import java.util.List;

public class Serialization{

	public static String logToString(List<String> log){
		if(log == null || log.isEmpty()) return "";
		StringBuilder string = new StringBuilder();
		for(String logEntry : log){
			if(logEntry.contains(";")) logEntry.replace(';', ':');
			string.append(logEntry + ";");
		}
		if(string.length() > 0) string.setLength(string.length() - 1);
		return string.toString();
	}
	
	public static List<String> stringToLog(String logAsString){
		if(logAsString == null || logAsString == "") return new ArrayList<String>();
		ArrayList<String> log = new ArrayList<String>();
		for(String logEntry : logAsString.split(";")){
			log.add(logEntry);
		}
		return log;
	}
}
