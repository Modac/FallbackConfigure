package org.myftp.p_productions.FallbackConfigure;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FallbackConfigureExecutor implements CommandExecutor {
	FallbackConfigure plugin;
	
	public FallbackConfigureExecutor(FallbackConfigure plugin) {
		this.plugin=plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("fallbackconfigure")) {
			if( !sender.hasPermission("fallbackconfigure.fallbackconfigure")){
				sender.sendMessage(ChatColor.RED+"You don't have permission!");
				return true;
			}
			if(args.length<1)
				return false;
			switch(args[0].toLowerCase()){
				case "enable":
					return enableCommand(sender, cmd, label);
				case "disable":
					return disableCommand(sender, cmd, label);
				case "toserver":
					return toServerCommand(sender, cmd, label);
				case "tofallback":
					return toFallbackCommand(sender, cmd, label);
				case "debug":
					return debugCommand(sender, cmd, label);
			}
		}
		return false;
			
	}

	// TODO: permissions
	private boolean enableCommand(CommandSender sender, Command cmd, String label) {
		if(!sender.hasPermission(Constants.enableCommandPermission)){
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		plugin.getConfig().set("enabled", true);
		plugin.saveConfig();
		OtherUtils.notify(plugin, "Auto configuring enabled");
		return true;
	}

	// TODO: permissions
	private boolean disableCommand(CommandSender sender, Command cmd, String label) {
		if(!sender.hasPermission(Constants.disableCommandPermission)){
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		plugin.getConfig().set("enabled", false);
		plugin.saveConfig();
		OtherUtils.notify(plugin, "Auto configuring disabled");
		return true;
		
	}

	// TODO: permissions
	private boolean toServerCommand(CommandSender sender, Command cmd, String label) {
		if(!sender.hasPermission(Constants.toServerCommandPermission)){
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		plugin.result = plugin.result + "\nTo Fallback:\n" + NetworkUtils.toServer();
		return true;
		
	}
	
	// TODO: permissions
	private boolean toFallbackCommand(CommandSender sender, Command cmd, String label) {
		if(!sender.hasPermission(Constants.toFallbackCommandPermission)){
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		plugin.result = plugin.result + "\nTo Fallback:\n" + NetworkUtils.toFallback();
		return true;
	}
	
	// TODO: permissions
	private boolean debugCommand(CommandSender sender, Command cmd, String label) {
		if(!sender.hasPermission(Constants.debugCommandPermission)){
			sender.sendMessage(ChatColor.RED + "You don't have permission!");
			return true;
		}
		sender.sendMessage(plugin.result);
		return true;
	}
	
}
