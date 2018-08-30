package cn.blockmc.Zao_hon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import cn.BlockMC.Zao_hon.AnvilLogin;
import net.milkbowl.vault.economy.Economy;

public class Signin extends JavaPlugin {
//	private HashMap<String, Reward> totalrewards = new HashMap<String, Reward>();
//	private HashMap<String, Reward> continuousrewards = new HashMap<String, Reward>();
//	private Reward firstsigninreward;
//	private Reward signinreward;
//	private Reward patchreward;
	private Economy economy;
	private AnvilLogin anvillogin;
	private SQLManager sqlmanager;
	
	private HashMap<String,Reward> rewards = new HashMap<String,Reward>();
	private List<String> totalrewards = new ArrayList<String>();
	private List<String> continuousrewards = new ArrayList<String>();
	
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getLogger().info("SignIn Started");
		this.sqlmanager = new SQLManager(this);
		this.getCommand("Signin").setExecutor(new Commands(this));
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.loadDepends();
		this.setupEconomy();
		this.loadRewards();

		// this.loadConfig();
	}

	@Override
	public void onDisable() {
		sqlmanager.close();
	}

	private void loadRewards() {
		
		ConfigurationSection totalsec = this.getConfig().getConfigurationSection("TotalRewards");
		if (totalsec != null) {
			totalsec.getKeys(false).forEach(key -> {
				String fullkey = "TotalRewards."+key;
				rewards.put(key, loadReward(fullkey));
				totalrewards.add(key);
				
//				int day = totalsec.getInt(key + ".Day");
//				String displayname = totalsec.getString(key + ".DisplayName");
//				List<String> lore = totalsec.getStringList(key + ".Lore");
//				String msg = totalsec.getString(key + ".Message");
//				String command = totalsec.getString(key + ".Command", "");
//				int vault = totalsec.getInt(key + ".Vault", 0);
//				int patch = totalsec.getInt(key + ".Patch", 0);
//				Reward r = new Reward(day, displayname, lore, msg, command, vault, patch);
//				totalrewards.put(key, r);
			});
		}
		ConfigurationSection continuoussec = this.getConfig().getConfigurationSection("ContinuousRewards");
		if (continuoussec != null) {
			continuoussec.getKeys(false).forEach(key -> {
				String fullkey = "ContinuousRewards."+key;
				rewards.put(key, loadReward(fullkey));
				continuousrewards.add(key);
				
//				int day = continuoussec.getInt(key + ".Day");
//				String displayname = continuoussec.getString(key + ".DisplayName");
//				List<String> lore = continuoussec.getStringList(key + ".Lore");
//				String msg = totalsec.getString(key + ".Message");
//				String command = continuoussec.getString(key + ".Command", "");
//				int vault = continuoussec.getInt(key + ".Vault", 0);
//				int patch = continuoussec.getInt(key + ".Patch", 0);
//				Reward r = new Reward(day, displayname, lore, msg, command, vault, patch);
//				continuousrewards.put(key, r);
			});
		}

		String first = "FirstSigninReward";
		rewards.put(first, loadReward(first));
//		this.firstsigninreward = new Reward(0, "", null, getConfig().getString(first + ".Message"),
//				getConfig().getString(first + ".Command", ""), getConfig().getInt(first + ".Vault"),
//				getConfig().getInt(first + ".Patch"));

		String signin = "SigninReward";
		rewards.put(signin, loadReward(signin));
//		this.signinreward = new Reward(0, "", null, getConfig().getString(signin + ".Message"),
//				getConfig().getString(signin + ".Command", ""), getConfig().getInt(signin + ".Vault"),
//				getConfig().getInt(signin + ".Patch"));

		String patch = "PatchReward";
		rewards.put(patch, loadReward(patch));
//		this.patchreward = new Reward(0, "", null, getConfig().getString(patch + ".Message"),
//				getConfig().getString(patch + ".Command", ""), getConfig().getInt(patch + ".Vault"),
//				getConfig().getInt(patch + ".Patch"));
	}
	private Reward loadReward(String key){
		int day = getConfig().getInt(key + ".Day");
		String displayname = getConfig().getString(key + ".DisplayName");
		List<String> lore = getConfig().getStringList(key + ".Lore");
		String msg = getConfig().getString(key + ".Message");
		String command = getConfig().getString(key + ".Command", "");
		int vault = getConfig().getInt(key + ".Vault", 0);
		int patch = getConfig().getInt(key + ".Patch", 0);
		return new Reward(day, displayname, lore, msg, command, vault, patch);
		
	}

	private void loadDepends() {
		Plugin anv = this.getServer().getPluginManager().getPlugin("AnvilLogin");
		if (anv == null) {
			this.getLogger().info("没有找到AnvilLogin");
		} else {
			this.getLogger().info("已加载依赖插件AnvilLogin");
			anvillogin = (AnvilLogin) anv;
			this.getServer().getPluginManager().registerEvents(new AnvilLoginEventListener(this), this);
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		this.getLogger().info("已加载依赖插件Vault");
		economy = rsp.getProvider();
		return economy != null;
	}

	public void reload() {
		reloadConfig();
		sqlmanager.close();
		sqlmanager = new SQLManager(this);
		totalrewards.clear();
		continuousrewards.clear();
		// loadConfig();
		loadRewards();
		loadDepends();
	}

	public SQLManager getSql() {
		return sqlmanager;
	}

	public AnvilLogin getAnvilLogin() {
		return anvillogin;
	}

//	public HashMap<String, Reward> getTotalRewards() {
//		return totalrewards;
//	}
//
//	public HashMap<String, Reward> getContinuousRewards() {
//		return continuousrewards;
//	}
	public List<String> getTotalRewards(){
		return totalrewards;
	}
	public List<String> getContinuousRewards(){
		return continuousrewards;
	}
	public Reward getReward(String rewardname){
		return rewards.get(rewardname);
	}

	public Economy getEconomy() {
		return economy;
	}

//	public Reward getFirstSigninReward() {
//		return firstsigninreward;
//	}
//
//	public Reward getSigninReward() {
//		return signinreward;
//	}
//
//	public Reward getPatchReward() {
//		return patchreward;
//	}

}
