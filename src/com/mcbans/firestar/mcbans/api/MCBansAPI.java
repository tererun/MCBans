package com.mcbans.firestar.mcbans.api;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.mcbans.firestar.mcbans.BanType;
import com.mcbans.firestar.mcbans.MCBans;
import com.mcbans.firestar.mcbans.pluginInterface.Ban;
import com.mcbans.firestar.mcbans.pluginInterface.Kick;

public class MCBansAPI {
    private final MCBans plugin;
    private final String pname;

    private MCBansAPI(final MCBans plugin, final String pname) {
        plugin.getLog().info("MCBans API linked with: " + pname);
        this.plugin = plugin;
        this.pname = pname;
    }

    private void ban(BanType type, String targetName, String senderName, String reason, String duration, String measure){
        // check null
        if (targetName == null || senderName == null){
            return;
        }

        String targetIP = "";
        if (type != BanType.UNBAN){
            final Player target = Bukkit.getPlayerExact(targetName);
            targetIP = (target != null) ? target.getAddress().getAddress().getHostAddress() : "";
        }

        Ban banControl = new Ban(plugin, type.getActionName(), targetName, targetIP, senderName, reason, duration, measure);
        Thread triggerThread = new Thread(banControl);
        triggerThread.start();
    }

    /**
     * Add Locally BAN
     * @param targetName BAN target player's name
     * @param senderName BAN issued admin's name
     * @param reason BAN reason
     */
    public void localBan(String targetName, String senderName, String reason){
        plugin.getLog().info("Plugin " + pname + " tried to local ban player " + targetName);

        reason = (reason == null || reason == "") ? plugin.getConfigs().getDefaultLocal() : reason;
        this.ban(BanType.LOCAL, targetName, senderName, reason, "", "");
    }

    /**
     * Add Globally BAN
     * @param targetName BAN target player's name
     * @param senderName BAN issued admin's name
     * @param reason BAN reason
     */
    public void globalBan(String targetName, String senderName, String reason){
        plugin.getLog().info("Plugin " + pname + " tried to global ban player " + targetName);

        if (reason == null || reason == "") return;
        this.ban(BanType.GLOBAL, targetName, senderName, reason, "", "");
    }

    /**
     * Add Temporary BAN
     * @param targetName BAN target player's name
     * @param senderName BAN issued admin's name
     * @param reason BAN reason
     * @param duration Banning length duration (intValue)
     * @param measure Banning length measure (m(minute), h(hour), d(day), w(week))
     */
    public void tempBan(String targetName, String senderName, String reason, String duration, String measure){
        plugin.getLog().info("Plugin " + pname + " tried to temp ban player " + targetName);

        reason = (reason == null || reason == "") ? plugin.getConfigs().getDefaultTemp() : reason;
        duration = (duration == null) ? "" : duration;
        measure = (measure == null) ? "" : measure;
        this.ban(BanType.TEMP, targetName, senderName, reason, duration, measure);
    }

    /**
     * Remove BAN
     * @param targetName UnBan target player's name
     * @param senderName UnBan issued admin's name
     */
    public void unBan(String targetName, String senderName){
        plugin.getLog().info("Plugin " + pname + " tried to unban player " + targetName);

        this.ban(BanType.UNBAN, targetName, senderName, "", "", "");
    }

    /**
     * Kick Player
     * @param targetName Kick target player's name
     * @param senderName Kick issued admin's name
     * @param reason Kick reason
     */
    public void kick(String targetName, String senderName, String reason){
        //plugin.getLog().info("Plugin " + pname + " tried to kick player " + targetName);
        reason = (reason == null || reason == "") ? plugin.getConfigs().getDefaultKick() : reason;

        // Start
        Kick kickPlayer = new Kick(plugin, targetName, senderName, reason);
        Thread triggerThread = new Thread(kickPlayer);
        triggerThread.start();
    }

    /**
     * Get MCBans plugin version
     * @return plugin version
     */
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    private static HashMap<Plugin, MCBansAPI> apiHandles = new HashMap<Plugin, MCBansAPI>();
    public static MCBansAPI getHandle(final MCBans plugin, final Plugin otherPlugin){
        if (otherPlugin == null) return null;

        MCBansAPI api = apiHandles.get(otherPlugin);

        if (api == null){
            // get new api
            api = new MCBansAPI(plugin, otherPlugin.getName());

            apiHandles.put(otherPlugin, api);
        }

        return api;
    }
}
