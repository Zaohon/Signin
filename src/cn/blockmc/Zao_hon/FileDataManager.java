package cn.blockmc.Zao_hon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class FileDataManager extends DataManager {
	// private Signin plugin;
	private File datafolder;
	private HashMap<UUID, FileConfiguration> datas = new HashMap<UUID, FileConfiguration>();

	public FileDataManager(Signin plugin) {
		// this.plugin = plugin;
		datafolder = new File(plugin.getDataFolder(), "userdata");
		if (!datafolder.exists()) {
			datafolder.mkdir();
		}
		File[] files = datafolder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			datas.put(UUID.fromString(file.getName()), YamlConfiguration.loadConfiguration(file));
		}
	}

	@Override
	public boolean isTodayFirstSignin() {
		String date = getTodayDate();
		for (FileConfiguration c : datas.values()) {
			if (c.getStringList("Signin").contains(date)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getPlayerPatch(Player p) {
		return this.getPlayerData(p.getUniqueId()).getInt("PatchNumber");
	}

	@Override
	public void addPlayerPatch(Player p, int i) {
		FileConfiguration config = this.getPlayerData(p.getUniqueId());
		int a = config.getInt("PatchNumber") + i;
		this.set(p, "PatchNumber", a);
	}

	@Override
	public void setPlayerPatch(Player p, int i) {
		// this.getPlayerData(p.getUniqueId()).set("PatchNumber", i);
		this.set(p, "PatchNumber", i);
	}

	@Override
	public void addNewPlayerPatch(Player p) {
		this.setPlayerPatch(p, 0);
	}

	@Override
	public List<String> getPlayerRewards(Player p) {
		return this.getPlayerData(p.getUniqueId()).getStringList("Rewards");
	}

	@Override
	public void addPlayerReward(Player p, String reward) {
//		FileConfiguration config = this.getPlayerData(p.getUniqueId());
//		config.set("Rewards", config.getStringList("Rewards").add(reward));
		this.set(p, "Rewards", reward);
	}

	@Override
	public boolean isSigninToday(Player p) {
		return this.getAllPlayerSignin(p).contains(getTodayDate());
	}

	@Override
	public List<String> getAllPlayerSignin(Player p) {
		List<String> signin = new ArrayList<String>();
		List<String> list = this.getPlayerData(p.getUniqueId()).getStringList("Signin");
		for (String s : list) {
			signin.add(s.split(";")[0]);
		}
		return signin;
	}

	@Override
	public void inserctPlayerSignin(Player p, String date, Boolean islate) {
		this.set(p, "Signin", date + ";" + islate);
		// FileConfiguration config = this.getPlayerData(p.getUniqueId());
		// config.set("Signin", config.getStringList("Signin").add());
	}

	@Override
	public void onDisbale() {
		datas.clear();
	}

	public FileConfiguration getPlayerData(UUID uuid) {
		return datas.containsKey(uuid) ? datas.get(uuid) : createNewData(uuid);
	}

	public FileConfiguration createNewData(UUID uuid) {
		File file = getFile(uuid);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		config.set("PatchNumber", 0);
		config.set("Signin", new ArrayList<String>());
		config.set("Rewards", new ArrayList<String>());
		// try {
		// config.save(file);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		datas.put(uuid, config);
		return config;
	}

	private String getTodayDate() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.DAY_OF_MONTH);
	}

	private File getFile(UUID uuid) {
		return new File(datafolder, uuid.toString());
	}

	private boolean set(Player p, String key, Object vaule) {
		UUID uuid = p.getUniqueId();
		FileConfiguration config = getPlayerData(uuid);
		if (config.get(key) instanceof List<?>) {
			List<String> list = config.getStringList(key);
			list.add((String) vaule);
			config.set(key, list);
		} else {
			config.set(key, vaule);
		}
		try {
			config.save(getFile(uuid));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
