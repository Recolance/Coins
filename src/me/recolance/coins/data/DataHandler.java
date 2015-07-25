package me.recolance.coins.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import me.recolance.coins.Coins;
import me.recolance.coins.util.CoinsUtil;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DataHandler{
	
	public static void generateTables(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS coins("
					+ "player_id VARCHAR(255) NOT NULL,"
					+ "coins INT(10) NOT NULL,"
					+ "log TEXT,"
					+ "coins_total BIGINT(19) NOT NULL,"
					+ "PRIMARY KEY(player_id))");
			statement.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
		generateIndex();
	}
	
	public static void generateIndex(){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("CREATE INDEX IDX_COINS ON coins (coins)");
			statement.execute();
		}catch(SQLException e){}
	}
	
	public static PlayerAccount createPlayerAccount(Player player){
		return new PlayerAccount(0, new ArrayList<String>(), player.getUniqueId(), 0L);
	}
	
	public static PlayerAccount loadPlayerAccount(UUID playerId){
		Connection connection = Database.getConnection();
		try{
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM coins WHERE player_id=?");
			statement.setString(1, playerId.toString());
			ResultSet result = statement.executeQuery();
			int count = 0;
			while(result.next()){
				return new PlayerAccount(result.getInt("coins"), Serialization.stringToLog(result.getString("log")), UUID.fromString(result.getString("player_id")), result.getLong("coins_total"));
			}
			if(count == 0) return null;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void savePlayerAccount(PlayerAccount account, boolean async){
		Connection connection = Database.getConnection();
		try{
			final PreparedStatement statement = connection.prepareStatement("REPLACE INTO coins(player_id, coins, log, coins_total) VALUES (?, ?, ?, ?)");
			statement.setString(1, account.getAccountOwner().toString());
			statement.setInt(2, account.getCoins());
			statement.setString(3, Serialization.logToString(account.getLog()));
			statement.setLong(4, account.getCoinsTotal());
			if(async){
				new BukkitRunnable(){
					@Override
					public void run(){
						try{
							statement.execute();
						}catch(SQLException e){
							e.printStackTrace();
						}
					}
				}.runTask(Coins.plugin);
			}else{
				statement.execute();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static void saveAllAccounts(){
		for(PlayerAccount account : CoinsUtil.onlineAccounts.values()){
			savePlayerAccount(account, false);
		}
	}
}
