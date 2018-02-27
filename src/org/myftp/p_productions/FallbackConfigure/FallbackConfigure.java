package org.myftp.p_productions.FallbackConfigure;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class FallbackConfigure extends JavaPlugin{
	BukkitTask backupTask;
	String prefix = ChatColor.BLUE+"["+getName()+"] "+ChatColor.RESET;
	String result = "";
	public static FallbackConfigure instance;
	
	@Override
	public void onEnable() {
		
		instance=this;
		NetworkUtils.setLogger(getLogger());
		
		NetworkUtils.updateRuleNo(getConfig().getInt("ruleno", -1));
		
		if(getConfig().getBoolean("enabled", false)){
			NetworkUtils.toServer();
		}
		
		this.getCommand("fallbackconfigure").setExecutor(new FallbackConfigureExecutor(this));
		
		OtherUtils.notify(this, "FallbackConfigure activated");
		
	}
	
	@Override
	public void onDisable() {
		
		if(getConfig().getBoolean("enabled", false)){
			NetworkUtils.toFallback();
		}
		OtherUtils.notify(this, "FallbackConfigure deactivated");
		
	}
}
