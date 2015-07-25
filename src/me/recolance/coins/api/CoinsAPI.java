package me.recolance.coins.api;

import java.util.UUID;

import me.recolance.coins.util.CoinsUtil;

public class CoinsAPI{

	public static int addCoins(UUID playerId, int amount, String frontReason, String backReason){
		return CoinsUtil.addCoins(playerId, amount, frontReason, backReason);
	}
	public static int addCoins(UUID playerId, int amount, String frontReason){
		return CoinsUtil.addCoins(playerId, amount, frontReason, null);
	}
	
	public static int removeCoins(UUID playerId, int amount, String frontReason, String backReason){
		return CoinsUtil.removeCoins(playerId, amount, frontReason, backReason);
	}
	public static int removeCoins(UUID playerId, int amount, String frontReason){
		return CoinsUtil.removeCoins(playerId, amount, frontReason, null);
	}
	
	public static int setCoins(UUID playerId, int amount, String frontReason, String backReason){
		return CoinsUtil.setCoins(playerId, amount, frontReason, backReason);
	}
	public static int setCoins(UUID playerId, int amount, String frontReason){
		return CoinsUtil.setCoins(playerId, amount, frontReason, null);
	}
	
	public static boolean hasCoins(UUID playerId, int amount){
		return CoinsUtil.hasCoins(playerId, amount);
	}
	
	public static int getCoins(UUID playerId){
		return CoinsUtil.getCoins(playerId);
	}
}
