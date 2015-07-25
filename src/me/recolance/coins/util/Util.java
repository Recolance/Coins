package me.recolance.coins.util;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Util{

	public static void message(Player player, String message){
		if(player == null) return;
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	public static void consoleMessage(String message){
		Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	@SuppressWarnings("deprecation")
	public static String getReason(String reason){
		Date date = new Date();
		String month = getMonth(date.getMonth());
		String day = String.valueOf(date.getDate());
		String year = String.valueOf(date.getYear() + 1900);
		String hour = String.valueOf(date.getHours());
		String min = String.valueOf(date.getMinutes());
		return ChatColor.translateAlternateColorCodes('&', "&9" + month + "/" + day + "/" + year + " " + hour + ":" + min + " - &e" + reason);		
	}
	
	public static String getMonth(int month){
		switch(month){
		case 0:
			return "Jan";
		case 1:
			return "Feb";
		case 2:
			return "Mar";
		case 3:
			return "Apr";
		case 4:
			return "May";
		case 5:
			return "Jun";
		case 6:
			return "Jly";
		case 7:
			return "Aug";
		case 8:
			return "Sep";
		case 9:
			return "Oct";
		case 10:
			return "Nov";
		case 11:
			return "Dec";
		default:
			return "Unk";
		}
	}
	
	public static String coinOrCoins(int amount){
		return (amount == 1) ? "Coin" : "Coins";
	}
	
	public static boolean isNumerical(String string){
		if(string.length() > 9) return false;
		Pattern pattern = Pattern.compile("[^0-9]");
		return pattern.matcher(string).find() ? false : true;
	}
	
	public static String commaInt(int number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
}
