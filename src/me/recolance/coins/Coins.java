package me.recolance.coins;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import me.recolance.coins.data.DataHandler;
import me.recolance.coins.data.Database;
import me.recolance.coins.data.PlayerAccount;
import me.recolance.coins.util.CoinsUtil;
import me.recolance.coins.util.Util;
import me.recolance.globalutil.utils.HoverMessage;
import me.recolance.playerlog.api.PlayerLogAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Coins extends JavaPlugin{

	/*
	 * TODO
	 * Add crash / reload protection.
	 * Asyn saving
	 */
	public static Plugin plugin;
	
	public void onEnable(){
		plugin = this;
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new CoinsUtil(), this);
		Database.openDatabaseConnection();
	}
	
	public void onDisable(){
		DataHandler.saveAllAccounts();
		Database.closeConnection();
		plugin = null;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] argument){
		int length = argument.length;
		switch(command.getName().toLowerCase()){
		case "coins":
			if(length == 0) return handleCoins(sender);
			switch(argument[0].toLowerCase()){
			case "set":
				if(length != 3) return handleCoinsSetNonArg(sender);
				else return handleCoinsSet(sender, argument[1], argument[2]);
			case "add":
				if(length != 3) return handleCoinsAddNonArg(sender);
				else return handleCoinsAdd(sender, argument[1], argument[2]);
			case "remove":
				if(length != 3) return handleCoinsRemoveNonArg(sender);
				else return handleCoinsRemove(sender, argument[1], argument[2]);
			case "log":
				if(length != 2) return handleLogNonArg(sender);
				else return handleLog(sender, argument[1]);
			default:
				if(length != 1) return handleCoinsOtherNonArg(sender);
				else return handleCoinsOther(sender, argument[0]);
			}
		case "pay":
			if(length != 2) return handlePayNonArg(sender);
			else return handlePay(sender, argument[0], argument[1]);
			
		case "baltop": return handleBalTop(sender);
			
		default: return false;
		}
	}
	
	public static boolean handleCoins(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			PlayerAccount account = CoinsUtil.getPlayerAccount(player.getUniqueId());
			if(account == null){
				Util.message(player, "&cThat player does not exist.");
			}else{
				Util.message(player, "&eBalance: &6" + Util.commaInt(account.getCoins()) + " " + Util.coinOrCoins(account.getCoins()));
			}
		}else{
			Util.consoleMessage("&cConsole doesnt have coins.");
		}
		return true;
	}
	
	public static boolean handleCoinsOtherNonArg(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			new HoverMessage().messageTip("&c[Try This Instead]", "&6Coins", "&9/coins <player>", "&eLookup the balance of another player.").send(player);
		}else{
			Util.consoleMessage("&cUse /coins <player>");
		}
		return true;
	}
	
	public static boolean handleCoinsOther(CommandSender sender, String who){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		PlayerAccount account = CoinsUtil.getPlayerAccount(who);
		if(account == null){
			if(player != null){
				Util.message(player, "&cThat player does not exist.");
			}else{
				Util.consoleMessage("&cThat player does not exist.");
			}
			return true;
		}
		if(player != null){
			Util.message(player, "&e" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s Balance: &6" + Util.commaInt(account.getCoins()) + " " + Util.coinOrCoins(account.getCoins()));
		}else{
			Util.consoleMessage("&e" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s Balance: &6" + Util.commaInt(account.getCoins()) + " " + Util.coinOrCoins(account.getCoins()));
		}
		return true;
	}
	
	public static boolean handleCoinsSetNonArg(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			if(player.hasPermission("coins.set")){
				new HoverMessage().messageTip("&c[Try This Instead]", "&6Coins Setting", "&9/coins set <player> <amount>", "&eSet a player's coin amount.").send(player);
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
		}else{
			Util.consoleMessage("&cUse /coins set <player> <amount>");
		}
		return true;
	}
	
	public static boolean handleCoinsSet(CommandSender sender, String who, String amountString){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(!Util.isNumerical(amountString)){
			Util.message(player, "&cIncorrect amount.");
			return true;
		}
		int amount = Integer.valueOf(amountString);
		PlayerAccount account = CoinsUtil.getPlayerAccount(who);
		if(account == null){
			if(player != null){
				if(player.hasPermission("coins.set")){
					Util.message(player, "&cThat player does not exist.");
				}else{
					Util.message(player, "&cYou do not have permission.");
				}
			}else{
				Util.message(player, "&cThat player does not exist.");
			}
			return true;
		}
		if(player != null){
			if(player.hasPermission("coins.set")){
				CoinsUtil.setCoins(account.getAccountOwner(), amount, player.getName() + " set your balance to " + amount + " " + Util.coinOrCoins(amount) + ".", player.getName() + " set balance to " + amount + " " + Util.coinOrCoins(amount) + ".");
				Util.message(player, "&6" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s &eaccount was set to &6" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount));
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
		}else{
			CoinsUtil.setCoins(account.getAccountOwner(), amount, "Console set your balance to " + amount + " " + Util.coinOrCoins(amount) + ".", "Console set balance to " + amount + " " + Util.coinOrCoins(amount) + ".");
			Util.consoleMessage("&6" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s &eaccount was set to &6" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount));
		}
		return true;
	}
	
	public static boolean handleCoinsRemoveNonArg(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			if(player.hasPermission("coins.remove")){
				new HoverMessage().messageTip("&c[Try This Instead]", "&6Coins Remove", "&9/coins remove <player> <amount>", "&eRemove coins from a player.").send(player);
			}else{
				Util.consoleMessage("&cUse /coins remove <player> <amount>");
			}
		}else{
			Util.consoleMessage("&cUse /coins remove <player> <amount>");
		}
		return true;
	}
	
	public static boolean handleCoinsRemove(CommandSender sender, String who, String amountString){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(!Util.isNumerical(amountString)){
			Util.message(player, "&cIncorrect amount.");
			return true;
		}
		int amount = Integer.valueOf(amountString);
		PlayerAccount account = CoinsUtil.getPlayerAccount(who);
		if(account == null){
			if(player != null){
				if(player.hasPermission("coins.remove")){
					Util.message(player, "&cThat player does not exist.");
				}else{
					Util.message(player, "&cYou do not have permission.");
				}
			}else{
				Util.consoleMessage("&cThat player does not exist.");
			}
			return true;
		}
		if(player != null){
			if(player.hasPermission("coins.remove")){
				int newBalance = CoinsUtil.removeCoins(account.getAccountOwner(), amount, player.getName() + " removed " + Util.commaInt(amount) + ".", player.getName() + " removed " + Util.commaInt(amount) + ".");
				Util.message(player, "&6" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s &enew balance is &6" + Util.commaInt(newBalance) + " " + Util.coinOrCoins(newBalance) + "&e.");
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
		}else{
			int newBalance = CoinsUtil.removeCoins(account.getAccountOwner(), amount, "Console removed " + Util.commaInt(amount) + ".", "Console removed " + Util.commaInt(amount) + ".");
			Util.consoleMessage("&6" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s &enew balance is &6" + Util.commaInt(newBalance) + " " + Util.coinOrCoins(newBalance) + "&e.");
		}
		return true;
	}
	
	public static boolean handleCoinsAddNonArg(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			if(player.hasPermission("coins.add")){
				new HoverMessage().messageTip("&c[Try This Instead]", "&6Coins Add", "&9/coins add <player> <amount>", "&eAdd coins to a player.").send(player);
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
		}else{
			Util.consoleMessage("&cUse /coins add <player> <amount>");
		}
		return true;
	}
	
	public static boolean handleCoinsAdd(CommandSender sender, String who, String amountString){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		PlayerAccount account = CoinsUtil.getPlayerAccount(who);
		if(!Util.isNumerical(amountString)){
			Util.message(player, "&cIncorrect amount.");
			return true;
		}
		int amount = Integer.valueOf(amountString);
		if(account == null){
			if(player != null){
				if(player.hasPermission("coins.add")){
					Util.message(player, "&cThat player does not exist.");
				}else{
					Util.message(player, "&cYou do not have permission.");
				}
			}else{
				Util.consoleMessage("&cThat player does not exist.");
			}
			return true;
		}
		if(player != null){
			if(player.hasPermission("coins.add")){
				int newBalance = CoinsUtil.addCoins(account.getAccountOwner(), amount, player.getName() + " added " + Util.commaInt(amount) + ".", player.getName() + " added " + Util.commaInt(amount) + ".");
				Util.message(player, "&6" + who + "'s &enew balance is &6" + Util.commaInt(newBalance) + " " + Util.coinOrCoins(newBalance) + "&e.");
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
		}else{
			int newBalance = CoinsUtil.addCoins(account.getAccountOwner(), amount, "Console added " + Util.commaInt(amount) + ".", "Console added " + Util.commaInt(amount) + ".");
			Util.consoleMessage("&6" + Bukkit.getOfflinePlayer(account.getAccountOwner()).getName() + "'s &enew balance is &6" + Util.commaInt(newBalance) + " " + Util.coinOrCoins(newBalance) + "&e.");
		}
		return true;
	}
	
	public static boolean handlePayNonArg(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			new HoverMessage().messageTip("&c[Try This Instead]", "&6Pay Coins", "&9/pay <player> <amount>", "&ePay another player some coins.").send(player);
		}else{
			Util.consoleMessage("&cThe console has no money to pay.");
		}
		return true;
	}
	
	public static boolean handlePay(CommandSender sender, String who, String amountString){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			if(player.getName().equalsIgnoreCase(who)){
				Util.message(player, "&cYou are trying to pay yourself.");
				return true;
			}
		}
		if(!Util.isNumerical(amountString)){
			Util.message(player, "&cIncorrect amount.");
			return true;
		}
		int amount = Integer.valueOf(amountString);
		UUID payingId = PlayerLogAPI.nameToUUID(who);
		if(payingId == null){
			if(player != null){
				Util.message(player, "&cThat player does not exist.");
			}else{
				Util.consoleMessage("&cThe console has no money to pay.");
			}
			return true;
		}
		if(player != null){
			CoinsUtil.payPlayer(player.getUniqueId(), payingId, amount);
		}else{
			Util.consoleMessage("&cThe console has no money to pay.");
		}
		return true;
	}
	
	public static boolean handleLogNonArg(CommandSender sender){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		if(player != null){
			if(player.hasPermission("coins.log")){
				new HoverMessage().messageTip("&c[Try This Instead]", "&6Coins Log", "&9/coins log <player>", "&eView a player's coin log.").send(player);
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
			return true;
		}else{
			Util.consoleMessage("&cUse /coins log <player>");
		}
		return true;
	}
	
	@SuppressWarnings("unused")
	public static boolean handleLog(CommandSender sender, String who){
		Player player = null;
		if(sender instanceof Player) player = (Player)sender;
		PlayerAccount account = CoinsUtil.getPlayerAccount(who);
		if(account == null){
			if(player != null){
				if(player.hasPermission("coins.log")){
					Util.message(player, "&cThat player does not exist.");
				}else{
					Util.message(player, "&cYou do not have permission.");
				}
			}else{
				Util.consoleMessage("&cThat player does not exist.");
			}
			return true;
		}
		if(player != null){
			if(player.hasPermission("coins.log")){
				Util.message(player, "&6############## &eLOG &6##############");
				List<String> log = account.getLog();
				int index = account.getLog().size() - 1;
				for(int i = index; i > -1; i--) Util.message(player, log.get(i));
			}else{
				Util.message(player, "&cYou do not have permission.");
			}
			return true;
		}else{
			Util.consoleMessage("&6############## &eLOG &6##############");
			int index = account.getLog().size() - 1;
			for(String logEntry : account.getLog()){
				Util.consoleMessage(logEntry);
				index--;
			}
		}
		return true;
	}
	
	public static boolean handleBalTop(CommandSender sender){
		if(!(sender instanceof Player)) Util.consoleMessage("Only a player can use this command.");
		else{
			Player player = (Player)sender;
			LinkedHashMap<UUID, Integer> baltop = CoinsUtil.getTopTen();
			Util.message(player, "&e-- &6Top 10 Wallets &e--");
			int i = 1;
			for(UUID id : baltop.keySet()){
				Util.message(player, "&e" + i + ". " + baltop.get(id) + " Coins - &6" + Bukkit.getOfflinePlayer(id).getName());
				i++;
			}
		}
		return true;
	}
}
