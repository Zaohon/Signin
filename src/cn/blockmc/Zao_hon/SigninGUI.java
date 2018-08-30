package cn.blockmc.Zao_hon;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SigninGUI implements Listener {
	private Calendar timeday;
	private ItemStack gomaininv;
	private boolean destroy = true;
	private Player player;
	private Signin plugin;
	private Inventory maininventory;
	private Inventory totalinventory;
	private Inventory continueinventory;
	private int patchnum;
	private int continuousnum;
	private List<String> signinlist = null;
	private List<String> hasrewards = null;

	public SigninGUI(Signin plugin, Player p) {
		timeday = Calendar.getInstance();
		this.player = p;
		this.plugin = plugin;
		signinlist = plugin.getSql().getAllPlayerSignin(p);
		hasrewards = plugin.getSql().getPlayerRewards(p);
		maininventory = Bukkit.createInventory(null, 54, "§a§l签到系统");
		totalinventory = Bukkit.createInventory(null, 54, "§a累计签到");
		continueinventory = Bukkit.createInventory(null, 54, "§a连续签到");

		gomaininv = new ItemStack(Material.ARROW);
		ItemMeta meta = gomaininv.getItemMeta();
		meta.setDisplayName("§a返回主菜单");
		gomaininv.setItemMeta(meta);

		patchnum = plugin.getSql().getPlayerPatch(p);
		updateContinuousSign();

		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		updateMainInventory();
		updateTotalInventory();
		updateContinuousInventory();

	}

	public void openContinuousInventory() {
		destroy = false;
		player.openInventory(continueinventory);
		destroy = true;
	}

	public void openTotalInventory() {
		destroy = false;
		player.openInventory(totalinventory);
		destroy = true;
	}

	public void openMainInventory() {
		destroy = false;
		player.openInventory(maininventory);
		destroy = true;
	}

	public void updateContinuousInventory() {
		continueinventory.clear();
		HashMap<String, Reward> rewards = plugin.getContinuousRewards();
		Iterator<String> it = rewards.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			String rewardname = it.next();
			Reward reward = rewards.get(rewardname);
			int day = reward.getDays();
			ItemStack item = new ItemStack(Material.SIGN, day);
			String displayname = reward.getDisplayName();
			if (hasrewards.contains(rewardname)) {
				List<String> lore = reward.getLores();
				lore.add("§a§l已领取");
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.setDisplayName(displayname);
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else if (signinlist.size() >= day) {
				List<String> lore = reward.getLores();
				lore.add("§d§l可领取");
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(lore);
				meta.setLocalizedName(rewardname);
				item.setItemMeta(meta);

			} else {
				List<String> lore = reward.getLores();
				lore.add("§8不可领取");
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			continueinventory.setItem(i, item);
			i++;
		}
		continueinventory.setItem(45, gomaininv.clone());
	}

	public void updateTotalInventory() {
		totalinventory.clear();
		HashMap<String, Reward> rewards = plugin.getTotalRewards();
		Iterator<String> it = rewards.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			String rewardname = it.next();
			Reward reward = rewards.get(rewardname);
			int day = reward.getDays();
			ItemStack item = new ItemStack(Material.SIGN, day);
			String displayname = reward.getDisplayName();
			if (hasrewards.contains(rewardname)) {
				List<String> lore = reward.getLores();
				lore.add("§a§l已领取");
				item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
				ItemMeta meta = item.getItemMeta();
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.setDisplayName(displayname);
				meta.setLore(lore);
				item.setItemMeta(meta);
			} else if (signinlist.size() >= day) {
				List<String> lore = reward.getLores();
				lore.add("§d§l可领取");
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(lore);
				meta.setLocalizedName(rewardname);
				item.setItemMeta(meta);

			} else {
				List<String> lore = reward.getLores();
				lore.add("§8不可领取");
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(lore);
				item.setItemMeta(meta);
			}
			totalinventory.setItem(i, item);
			i++;
		}
		totalinventory.setItem(45, gomaininv.clone());
	}

	public void updateMainInventory() {
		// 今天
		Calendar today = Calendar.getInstance();
		// GUI时间的克隆
		Calendar ctimeday = (Calendar) timeday.clone();
		// 年
		int year = ctimeday.get(Calendar.YEAR);
		// 设置为第一天
		ctimeday.set(Calendar.DAY_OF_MONTH, 1);
		// 获得第一天是星期几
		int dayoffirstweek = ctimeday.get(Calendar.DAY_OF_WEEK);

		today.set(Calendar.MINUTE, ctimeday.get(Calendar.MINUTE));
		today.set(Calendar.SECOND, ctimeday.get(Calendar.SECOND));
		today.set(Calendar.MILLISECOND, ctimeday.get(Calendar.MILLISECOND));

		// 获得这个月最大天数
		// int max = ctimeday.getActualMaximum(Calendar.DAY_OF_MONTH);
		// int e = max + dayoffirstweek <= 35 ? max + dayoffirstweek + 7 : max +
		// dayoffirstweek + 9;s
		ctimeday.add(Calendar.DAY_OF_MONTH, -dayoffirstweek + 1);
		for (int i = 0; i < 54; i++) {
			if (i % 9 == 0 || (i + 1) % 9 == 0) {
				continue;
			}

			ItemStack item = null;
			String sdate = ctimeday.get(Calendar.MONTH) + 1 + "." + ctimeday.get(Calendar.DAY_OF_MONTH);

			if (signinlist.contains(year + "." + sdate)) {
				item = new ItemStack(Material.LIME_WOOL);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(sdate);
				meta.setLore(Arrays.asList("§a§l已签到"));
				item.setItemMeta(meta);
			} else if (ctimeday.before(today)) {
				Calendar atheday = (Calendar) ctimeday.clone();
				atheday.add(Calendar.DAY_OF_MONTH, plugin.getConfig().getInt("PatchDay"));
				if (atheday.before(today)) {
					item = new ItemStack(Material.BLACK_WOOL);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(sdate);
					meta.setLore(Arrays.asList("§8未签到"));
					item.setItemMeta(meta);
				} else {
					item = new ItemStack(Material.PINK_WOOL);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(sdate);
					meta.setLore(Arrays.asList("§8未签到", "§d§l可补签"));
					item.setItemMeta(meta);
				}
			} else if (ctimeday.equals(today)) {
				item = new ItemStack(Material.RED_WOOL);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(sdate);
				meta.setLore(Arrays.asList("§a§l可签到"));
				item.setItemMeta(meta);
			} else {
				item = new ItemStack(Material.GRAY_WOOL);

				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(sdate);
				meta.setLore(Arrays.asList("§0不可签到"));
				item.setItemMeta(meta);
			}
			maininventory.setItem(i, item);
			ctimeday.add(Calendar.DAY_OF_MONTH, 1);
		}

		ItemStack continuous = new ItemStack(Material.SIGN, continuousnum);
		ItemMeta cmeta = continuous.getItemMeta();
		cmeta.setDisplayName("§a累计签到");
		continuous.setItemMeta(cmeta);
		maininventory.setItem(35, continuous);

		ItemStack total = new ItemStack(Material.SIGN, signinlist.size());
		ItemMeta tmeta = total.getItemMeta();
		tmeta.setDisplayName("§a连续签到");
		total.setItemMeta(tmeta);
		maininventory.setItem(26, total);

		ItemStack ranklist = new ItemStack(Material.SIGN);
		ItemMeta rmeta = ranklist.getItemMeta();
		rmeta.setDisplayName("§7§l排行榜");

		ItemStack lastmonth = new ItemStack(Material.REPEATER);
		ItemMeta lmeta = lastmonth.getItemMeta();
		lmeta.setDisplayName("§a查看上个月");
		lastmonth.setItemMeta(lmeta);
		maininventory.setItem(45, lastmonth);

		ItemStack nextmonth = new ItemStack(Material.COMPARATOR);
		ItemMeta nmeta = nextmonth.getItemMeta();
		nmeta.setDisplayName("§a查看下个月");
		nextmonth.setItemMeta(nmeta);
		maininventory.setItem(53, nextmonth);

		ItemStack patch = new ItemStack(Material.PAPER, patchnum);
		patch.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta pmeta = patch.getItemMeta();
		pmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		pmeta.setDisplayName("§d补签卡");
		patch.setItemMeta(pmeta);
		maininventory.setItem(18, patch);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		ItemStack clicked = e.getCurrentItem();
		if (clicked == null) {
			return;
		}
		if (clicked.equals(gomaininv)) {
			openMainInventory();
			return;
		}
		// Main Inventory
		if (e.getInventory().equals(maininventory)) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			int slot = e.getRawSlot();
			if (slot == 26) {
				openTotalInventory();
				return;
			} else if (slot == 35) {
				openContinuousInventory();
				return;
			} else if (slot == 45) {
				timeday.add(Calendar.MONTH, -1);
				updateMainInventory();
				openMainInventory();
			} else if (slot == 53) {
				timeday.add(Calendar.MONTH, 1);
				updateMainInventory();
				openMainInventory();
			} else if (clicked != null && clicked.hasItemMeta()) {
				ItemMeta meta = clicked.getItemMeta();
				if (meta.hasDisplayName() && meta.hasLore()) {
					String d = meta.getDisplayName();
					List<String> lores = meta.getLore();
					if (lores.contains("§d§l可补签")) {
						if (patchnum > 0) {
							int year = timeday.get(Calendar.YEAR);
							String fd = year + "." + d;
							plugin.getSql().inserctPlayerSignin(p, fd, true);
							plugin.getSql().addPlayerPatch(p, -1);

							Reward reward = plugin.getPatchReward();
							reward.givePlayer(plugin, player);
							player.sendMessage(reward.getMessage().replace("%year%", year + "")
									.replace("%month%", timeday.get(Calendar.MONTH) + 1 + "")
									.replace("%day%", timeday.get(Calendar.DAY_OF_MONTH) + ""));
							if (plugin.getConfig().getBoolean("CloseInvAfterPatchSignin")) {
								p.closeInventory();
								return;
							}
							signinlist.add(fd);
							patchnum--;
							updateContinuousSign();
							updateMainInventory();
							updateTotalInventory();
							updateContinuousInventory();
							return;
						} else {
							p.sendMessage("你没有补签卡了");
							return;
						}
					} else if (lores.contains("§a§l可签到")) {
						int year = timeday.get(Calendar.YEAR);
						String fd = year + "." + d;
						if (plugin.getSql().isTodayFirstSignin()) {
							Reward reward = plugin.getFirstSigninReward();
							reward.givePlayer(plugin, player);
							player.sendMessage(reward.getMessage().replace("%year%", year + "")
									.replace("%month%", timeday.get(Calendar.MONTH) + 1 + "")
									.replace("%day%", timeday.get(Calendar.DAY_OF_MONTH) + ""));
						}
						plugin.getSql().inserctPlayerSignin(p, fd, false);
						Reward reward = plugin.getSigninReward();
						reward.givePlayer(plugin, player);
						player.sendMessage(reward.getMessage().replace("%year%", year + "")
								.replace("%month%", timeday.get(Calendar.MONTH) + 1 + "")
								.replace("%day%", timeday.get(Calendar.DAY_OF_MONTH) + ""));
						if (plugin.getConfig().getBoolean("CloseInvAfterSignin")) {
							p.closeInventory();
							return;
						}
						signinlist.add(fd);
						updateContinuousSign();
						updateMainInventory();
						updateTotalInventory();
						updateContinuousInventory();
						return;
					}
				}
			}

		}
		// Total Inventory
		else if (e.getInventory().equals(totalinventory)) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (clicked != null && clicked.hasItemMeta()) {
				ItemMeta meta = clicked.getItemMeta();
				if (meta.hasLocalizedName() && meta.hasLore()) {
					List<String> lores = meta.getLore();
					if (lores.contains("§d§l可领取")) {
						String rewardname = meta.getLocalizedName();
						Reward reward = plugin.getTotalRewards().get(rewardname);
						reward.givePlayer(plugin, p);
						plugin.getSql().addPlayerReward(p, rewardname);
						hasrewards.add(rewardname);
						updateTotalInventory();
					}
				}
			}

		}
		// Continuous Inventory
		else if (e.getInventory().equals(continueinventory)) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			if (clicked != null && clicked.hasItemMeta()) {
				ItemMeta meta = clicked.getItemMeta();
				if (meta.hasLocalizedName() && meta.hasLore()) {
					List<String> lores = meta.getLore();
					if (lores.contains("§d§l可领取")) {
						String rewardname = meta.getLocalizedName();
						Reward reward = plugin.getContinuousRewards().get(rewardname);
						reward.givePlayer(plugin, p);
						plugin.getSql().addPlayerReward(p, rewardname);
						hasrewards.add(rewardname);
						updateContinuousInventory();
					}
				}
			}
		}

	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (p.equals(player)) {
			if (destroy)
				destroy();
		}
	}

	private void updateContinuousSign() {
		Calendar today = Calendar.getInstance();
		continuousnum = 0;
		// boolean begin = false;

		for (int i = 0; i <= signinlist.size(); i++) {
			String sday = today.get(Calendar.YEAR) + "." + (today.get(Calendar.MONTH) + 1) + "."
					+ today.get(Calendar.DAY_OF_MONTH);
			if (signinlist.contains(sday)) {
				continuousnum++;
			} else {
				return;
			}
			today.add(Calendar.DAY_OF_MONTH, -1);
		}
	}

	private void destroy() {
		maininventory.clear();
		totalinventory.clear();
		continueinventory.clear();
		HandlerList.unregisterAll(this);
	}

}
