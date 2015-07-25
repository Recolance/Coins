package me.recolance.coins.data;

import java.util.List;
import java.util.UUID;

public class PlayerAccount{

	int coins;
	List<String> log;
	UUID accountOwner;
	long coinsTotal;
	
	PlayerAccount(int coins, List<String> log, UUID accountOwner, long coinsTotal){
		this.coins = coins;
		this.log = log;
		this.accountOwner = accountOwner;
		this.coinsTotal = coinsTotal;
	}
	
	public void setCoins(int coins){
		if(coins < 0) coins = 0;
		this.coins = coins;
	}
	public int getCoins(){
		return this.coins;
	}
	
	public void removeCoins(int amount){
		setCoins(getCoins() - amount);
	}
	
	public void setLog(List<String> log){
		this.log = log;
	}
	public List<String> getLog(){
		return this.log;
	}
	public void addLogEntry(String logEntry){
		this.log.add(0, logEntry);
		if(log.size() > 99) log.remove(99);
	}
	
	public void setAccountOwner(UUID accountOwner){
		this.accountOwner = accountOwner;
	}
	public UUID getAccountOwner(){
		return this.accountOwner;
	}
	
	public void setCoinsTotal(long coinsTotal){
		this.coinsTotal = coinsTotal;
	}
	public long getCoinsTotal(){
		return this.coinsTotal;
	}
}
