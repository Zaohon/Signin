package cn.blockmc.Zao_hon;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class Reward {
	private final int day;
	private final String displayname;
	private final List<String> lore;
	private final String message;
	private final String command;
	private final int vault;
	private final int patch;

	public Reward(final int day, final String displayname, final List<String> lore, final String message,
			final String command, final int vault, final int patch) {
		this.day = day;
		this.displayname = displayname;
		this.lore = lore;
		this.message = message;
		this.command = command;
		this.vault = vault;
		this.patch = patch;
	}

	public int getDays() {
		return day;
	}

	public String getDisplayName() {
		return displayname;
	}

	public List<String> getLores() {
		return new ArrayList<String>(lore);
	}

	public void givePlayer(Signin plugin, Player p) {
		if (command !="")
			plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
		if (vault != 0) {
			if (plugin.getEconomy() != null) {
				plugin.getEconomy().depositPlayer(p, vault);
			}
		}
		if (patch != 0)
			plugin.getData().addPlayerPatch(p, patch);
	}

	public String getMessage() {
		return message;
	}

}
