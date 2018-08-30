package cn.blockmc.Zao_hon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
public class Commands implements CommandExecutor{
	private Signin plugin;
	public Commands(Signin plugin){
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("该指令无法在后台使用");
			return true;
		}
		Player p = (Player) sender;
		int lenth = args.length;
		if(lenth==0){
			new SigninGUI(plugin, p).openMainInventory();
		}else{
			if(args[0].equals("reload")){
				if(p.hasPermission("Signin.reload")){
					plugin.reload();
					p.sendMessage("§dSignin Reloaded");
				}
			}
		}
		return false;
	}

}
