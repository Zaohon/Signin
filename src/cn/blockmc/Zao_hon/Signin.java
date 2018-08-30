package cn.blockmc.Zao_hon;

import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import cn.BlockMC.Zao_hon.AnvilLogin;
import net.milkbowl.vault.economy.Economy;

public class Signin extends JavaPlugin {
	private HashMap<String, Reward> totalrewards = new HashMap<String, Reward>();
	private HashMap<String, Reward> continuousrewards = new HashMap<String, Reward>();
	private Reward firstsigninreward;
	private Reward signinreward;
	private Reward patchreward;
	private Economy economy;
	private AnvilLogin anvillogin;
	private SQLManager sqlmanager;
	// private String signinmsg;
	// private String patchmsg;
	// private boolean iscloseinvaftersignin;
	// private boolean iscloseinvafterpatchsignin;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.reloadConfig();
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

	// private void loadConfig() {
	//// signinmsg = getConfig().getString("SigninMessage");
	//// patchmsg = getConfig().getString("PatchSigninMessage");
	// // iscloseinvaftersignin =
	// // getConfig().getBoolean("CloseInvAfterSignin");
	// // iscloseinvafterpatchsignin =
	// // getConfig().getBoolean("CloseInvAfterPatchSignin");
	// }

	private void loadRewards() {
		ConfigurationSection totalsec = this.getConfig().getConfigurationSection("TotalRewards");
		if (totalsec != null) {
			totalsec.getKeys(false).forEach(key -> {
				int day = totalsec.getInt(key + ".Day");
				String displayname = totalsec.getString(key + ".DisplayName");
				List<String> lore = totalsec.getStringList(key + ".Lore");
				String msg = totalsec.getString(key + ".Message");
				String command = totalsec.getString(key + ".Command", "");
				int vault = totalsec.getInt(key + ".Vault", 0);
				int patch = totalsec.getInt(key + ".Patch", 0);
				Reward r = new Reward(day, displayname, lore, msg, command, vault, patch);
				totalrewards.put(key, r);
			});
		}
		ConfigurationSection continuoussec = this.getConfig().getConfigurationSection("ContinuousRewards");
		if (continuoussec != null) {
			continuoussec.getKeys(false).forEach(key -> {
				int day = continuoussec.getInt(key + ".Day");
				String displayname = continuoussec.getString(key + ".DisplayName");
				List<String> lore = continuoussec.getStringList(key + ".Lore");
				String msg = totalsec.getString(key + ".Message");
				String command = continuoussec.getString(key + ".Command", "");
				int vault = continuoussec.getInt(key + ".Vault", 0);
				int patch = continuoussec.getInt(key + ".Patch", 0);
				Reward r = new Reward(day, displayname, lore, msg, command, vault, patch);
				continuousrewards.put(key, r);
			});
		}

		String first = "FirstSigninReward";
		this.firstsigninreward = new Reward(0, "", null, getConfig().getString(first + ".Message"),
				getConfig().getString(first + ".Command", ""), getConfig().getInt(first + ".Vault"),
				getConfig().getInt(first + ".Patch"));

		String signin = "SigninReward";
		this.signinreward = new Reward(0, "", null, getConfig().getString(signin + ".Message"),
				getConfig().getString(signin + ".Command", ""), getConfig().getInt(signin + ".Vault"),
				getConfig().getInt(signin + ".Patch"));

		String patch = "PatchReward";
		this.patchreward = new Reward(0, "", null, getConfig().getString(patch + ".Message"),
				getConfig().getString(patch + ".Command", ""), getConfig().getInt(patch + ".Vault"),
				getConfig().getInt(patch + ".Patch"));
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

	public HashMap<String, Reward> getTotalRewards() {
		return totalrewards;
	}

	public HashMap<String, Reward> getContinuousRewards() {
		return continuousrewards;
	}

	public Economy getEconomy() {
		return economy;
	}

	// public String getSigninMsg() {
	// return signinmsg;
	// }
	//
	// public String getPatchSigninMsg() {
	// return patchmsg;
	// }

	// public boolean isCloseInvAfterSignin() {
	// return iscloseinvaftersignin;
	// }

	// public boolean isCloseInvAfterPatchSignin() {
	// return iscloseinvafterpatchsignin;
	// }

	public Reward getFirstSigninReward() {
		return firstsigninreward;
	}

	public Reward getSigninReward() {
		return signinreward;
	}

	public Reward getPatchReward() {
		return patchreward;
	}

}
