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
	private Economy economy;
	private AnvilLogin anvillogin;
	private DataManager datamanager;
	
	private HashMap<String,Reward> rewards = new HashMap<String,Reward>();
	private List<String> totalrewards = new ArrayList<String>();
	private List<String> continuousrewards = new ArrayList<String>();
	
	
	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.getLogger().info("SignIn Started");
		this.initDataManager();
		this.getCommand("Signin").setExecutor(new Commands(this));
		this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
		this.loadDepends();
		this.setupEconomy();
		this.loadRewards();
	}

	@Override
	public void onDisable() {
		datamanager.onDisbale();
	}

	private void loadRewards() {
		
		ConfigurationSection totalsec = this.getConfig().getConfigurationSection("TotalRewards");
		if (totalsec != null) {
			totalsec.getKeys(false).forEach(key -> {
				String fullkey = "TotalRewards."+key;
				rewards.put(key, loadReward(fullkey));
				totalrewards.add(key);
			});
		}
		ConfigurationSection continuoussec = this.getConfig().getConfigurationSection("ContinuousRewards");
		if (continuoussec != null) {
			continuoussec.getKeys(false).forEach(key -> {
				String fullkey = "ContinuousRewards."+key;
				rewards.put(key, loadReward(fullkey));
				continuousrewards.add(key);
			});
		}

		String first = "FirstSigninReward";
		rewards.put(first, loadReward(first));

		String signin = "SigninReward";
		rewards.put(signin, loadReward(signin));

		String patch = "PatchReward";
		rewards.put(patch, loadReward(patch));
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
	private void initDataManager(){
		if(this.getConfig().getBoolean("MYSQL.Enable")){
			datamanager = new SQLManager(this);
		}else{
			datamanager = new FileDataManager(this);
		}
	}

	public void reload() {
		reloadConfig();
		datamanager.onDisbale();
		initDataManager();
		totalrewards.clear();
		continuousrewards.clear();
		loadRewards();
		loadDepends();
	}

	public DataManager getData() {
		return datamanager;
	}

	public AnvilLogin getAnvilLogin() {
		return anvillogin;
	}
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

}
