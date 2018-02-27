package org.myftp.p_productions.FallbackConfigure;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;

public class OtherUtils {
	
	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static int countChars(String input){
		return input.length();
	}
	
	public static int broadcastToPlayers(Server server, String msg, String perm){
		AtomicInteger count = new AtomicInteger(0);
		server.getOnlinePlayers().forEach(player->{if(player.hasPermission(perm)) player.sendMessage(msg); count.incrementAndGet();});
		return count.get();
	}
	
	public static int broadcastToPlayers(String msg, String perm){
		return broadcastToPlayers(Bukkit.getServer(), msg, perm);
	}
	
	
	// Very specific for my needs.
	public static void notify(FallbackConfigure plugin, String msg){
		notify(plugin, msg, Level.INFO);
	}
	
	public static void notify(FallbackConfigure plugin, String msg, Level level){
		plugin.getLogger().log(level, msg);
		broadcastToPlayers(plugin.getServer(), plugin.prefix + msg, Constants.broadcastPermission);
	}
	
}
