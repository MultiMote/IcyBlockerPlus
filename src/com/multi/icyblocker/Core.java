package com.multi.icyblocker;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class Core extends JavaPlugin {

    private static Logger logger;

    public Core(){
        logger = getLogger();
    }

    public void onEnable() {

        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if(!(worldGuard instanceof WorldGuardPlugin)){
            logger.log(Level.SEVERE, "Can't find WorldGuard, aborting.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.getCommand("icy").setExecutor(new CommandHandler());
        PlayerActionsHandler handler = new PlayerActionsHandler(worldGuard);
        Bukkit.getPluginManager().registerEvents(handler, this);

        logger.info("Reading item list...");
        BlockedItems.instance.setCoreInstance(this);
        BlockedItems.instance.readFile();
        logger.info("Loaded.");
        if(Math.random() < 0.3F)logger.info("You are awesome!");
    }

    public static Logger getPluginLogger() {
        return logger;
    }
}