package cn.blockmc.Zao_hon;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventListener implements Listener{
	private Signin plugin;
	public EventListener(Signin plugin){
		this.plugin = plugin;
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		plugin.getSql().addNewPlayerPatch(e.getPlayer());
	}

}
