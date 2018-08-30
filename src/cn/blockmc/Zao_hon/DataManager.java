package cn.blockmc.Zao_hon;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class DataManager {
	public abstract boolean isTodayFirstSignin();
	public abstract int getPlayerPatch(Player p);
	public abstract void addPlayerPatch(Player p, int i);
	public abstract void setPlayerPatch(Player p, int i);
	public abstract void addNewPlayerPatch(Player p);
	public abstract List<String> getPlayerRewards(Player p);
	public abstract void addPlayerReward(Player p, String reward);
	public abstract boolean isSigninToday(Player player);
	public abstract List<String> getAllPlayerSignin(Player p);
	public abstract void inserctPlayerSignin(Player p, String date, Boolean islate);
	public abstract void onDisbale();
}
