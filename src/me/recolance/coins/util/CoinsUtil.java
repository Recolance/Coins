package me.recolance.coins.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import me.recolance.coins.data.DataHandler;
import me.recolance.coins.data.Database;
import me.recolance.coins.data.PlayerAccount;
import me.recolance.globalutil.utils.HoverMessage;
import me.recolance.playerlog.api.PlayerLogAPI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CoinsUtil implements Listener{

	public static HashMap<UUID, PlayerAccount> onlineAccounts = new HashMap<UUID, PlayerAccount>();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(!player.hasPlayedBefore() || DataHandler.loadPlayerAccount(player.getUniqueId()) == null){
			System.out.println(true);
			onlineAccounts.put(player.getUniqueId(), DataHandler.createPlayerAccount(player));
			return;
		}
		onlineAccounts.put(player.getUniqueId(), DataHandler.loadPlayerAccount(player.getUniqueId()));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		DataHandler.savePlayerAccount(onlineAccounts.get(event.getPlayer().getUniqueId()), true);
		onlineAccounts.remove(event.getPlayer().getUniqueId());
	}
	
	public static int addCoins(UUID playerId, int amount, String frontReason, String backReason){
		PlayerAccount account = getPlayerAccount(playerId);
		if(account == null) return 0;
		int sum = account.getCoins() + amount;
		account.setCoins(sum);
		account.setCoinsTotal(account.getCoinsTotal() + sum);
		if(backReason != null) account.addLogEntry(Util.getReason(backReason));
		if(Bukkit.getPlayer(playerId) != null) new HoverMessage().messageTip("&6[+" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount) + "]", "&6+" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount), "&9Reason: &e" + frontReason, "&9New Balance: &e" + sum + " " + Util.coinOrCoins(amount)).send(Bukkit.getPlayer(playerId));;
		if(!onlineAccounts.containsKey(playerId)) DataHandler.savePlayerAccount(account, true);
		return sum;
	}
	
	public static int removeCoins(UUID playerId, int amount, String frontReason, String backReason){
		PlayerAccount account = getPlayerAccount(playerId);
		if(account == null) return 0;
		int min = 0;
		int diff = account.getCoins() - amount;
		if(diff < min) diff = min;
		account.setCoins(diff);
		if(backReason != null) account.addLogEntry(Util.getReason(backReason));
		if(Bukkit.getPlayer(playerId) != null) new HoverMessage().messageTip("&c[-" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount) + "]", "&c-" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount), "&9Reason: &e" + frontReason, "&9New Balance: &e" + diff + " " + Util.coinOrCoins(amount)).send(Bukkit.getPlayer(playerId));
		if(!onlineAccounts.containsKey(playerId)) DataHandler.savePlayerAccount(account, true);
		return diff;
	}
	
	public static int setCoins(UUID playerId, int amount, String frontReason, String backReason){
		PlayerAccount account = getPlayerAccount(playerId);
		if(account == null) return 0;
		account.setCoins(amount);
		if(backReason != null) account.addLogEntry(Util.getReason(backReason));
		if(Bukkit.getPlayer(playerId) != null) new HoverMessage().messageTip("&a[+-" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount) + "]", "&a+-" + Util.commaInt(amount) + " " + Util.coinOrCoins(amount), "&9Reason: &e" + frontReason, "&9New Balance: &e" + amount + " " + Util.coinOrCoins(amount)).send(Bukkit.getPlayer(playerId));
		if(!onlineAccounts.containsKey(playerId)) DataHandler.savePlayerAccount(account, true);
		return amount;
	}
	
	public static boolean hasCoins(UUID playerId, int amount){
		PlayerAccount account = getPlayerAccount(playerId);
		if(account == null) return false;
		if(account.getCoins() >= amount) return true;
		return false;
	}
	
	public static int getCoins(UUID playerId){
		PlayerAccount account = getPlayerAccount(playerId);
		if(account == null) return 0;
		return account.getCoins();
	}
	
	public static LinkedHashMap<UUID, Integer> getTopTen(){
		LinkedHashMap<UUID, Integer> baltop = new LinkedHashMap<UUID, Integer>();
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT player_id, coins FROM coins ORDER BY coins DESC LIMIT 10");
			ResultSet set = statement.executeQuery();
			while(set.next()){
				baltop.put(UUID.fromString(set.getString("player_id")), set.getInt("coins"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return baltop;
	}
	
	public static void payPlayer(UUID payer, UUID accepting, int amount){
		if(getPlayerAccount(payer) == null || getPlayerAccount(accepting) == null) return;
		if(getPlayerAccount(payer).getCoins() < amount){
			Util.message(Bukkit.getPlayer(payer), "&cYou do not have enough coins.");
			return;
		}
		String reasonRemove = "Paid " + Bukkit.getOfflinePlayer(accepting).getName() + " " + Util.commaInt(amount) + " " + Util.coinOrCoins(amount) + ".";
		removeCoins(payer, amount, reasonRemove, reasonRemove);
		String reasonAdd = "Payment from " + Bukkit.getPlayer(payer).getName() + " for " + Util.commaInt(amount) + " " + Util.coinOrCoins(amount) + "."; 
		addCoins(accepting, amount, reasonAdd, reasonAdd);
	}
	
	public static PlayerAccount getPlayerAccount(UUID playerId){
		if(onlineAccounts.containsKey(playerId)) return onlineAccounts.get(playerId);
		else return DataHandler.loadPlayerAccount(playerId);
	}
	
	public static PlayerAccount getPlayerAccount(String name){
		UUID playerId = PlayerLogAPI.nameToUUID(name);
		if(playerId == null) return null;
		return getPlayerAccount(playerId);
	}
}
