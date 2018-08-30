package cn.blockmc.Zao_hon;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import cn.BlockMC.Zao_hon.Events.PlayerLoggedEvent;

public class AnvilLoginEventListener implements Listener {
	private Signin plugin;

	public AnvilLoginEventListener(Signin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerLogged(PlayerLoggedEvent e) {
		Player p = e.getPlayer();
		if (!plugin.getData().isSigninToday(p)) p.sendMessage("��d����컹ûǩ��Ŷ������/Signin��ǩ��");
	}

}
