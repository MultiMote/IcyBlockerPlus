package com.multi.icyblocker;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class PlayerActionsHandler implements Listener {

    private final WorldGuardPlugin worldGuardInstance;

    public PlayerActionsHandler(Plugin worldGuardInstance) {
        this.worldGuardInstance = (WorldGuardPlugin) worldGuardInstance;
    }

    private void warnPlayer(Player player) {
        player.sendMessage(ChatColor.RED + "Этот предмет вне закона.");
    }

    private void warnPlayerBlock(Player player) {
        player.sendMessage(ChatColor.RED + "Нельзя сотворить здесь.");
    }

    private boolean hasPerm(Player player) {
        return (player.hasPermission("icyblocker.ignore") || player.isOp());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void placeBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.hasPerm(player)) return;
        if (BlockedItems.instance.matches(event.getBlock(), true, ProtectionsEnum.BLOCKED_ITEM)) {
            event.setCancelled(true);
            player.setItemInHand(null);
            this.warnPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (this.hasPerm(player)) return;

        ItemStack stack = event.getItem();

        if (block != null
                && !this.worldGuardInstance.canBuild(player, block.getLocation())
                && BlockedItems.instance.matches(block, true, ProtectionsEnum.PROTECTED_BLOCK)
                ) {

            event.setCancelled(true);
            this.warnPlayerBlock(player);
        }

        if (stack != null) {
            if (BlockedItems.instance.matches(stack, true, ProtectionsEnum.BLOCKED_ITEM)) {
                event.setCancelled(true);
                player.setItemInHand(null);
                this.warnPlayer(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (this.hasPerm(player)) return;
        ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (BlockedItems.instance.matches(items[i], false, ProtectionsEnum.BLOCKED_ITEM)) {
                    player.getInventory().setItem(i, null);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void itemCrafted(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.hasPerm(player)) return;
        if (BlockedItems.instance.matches(event.getCurrentItem(), false, ProtectionsEnum.BLOCKED_ITEM)) {
            event.setCancelled(true);
            this.warnPlayer(player);
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (this.hasPerm(player)) return;
        ItemStack stack = event.getCurrentItem();

        if (stack != null) {
            if (BlockedItems.instance.matches(stack, false, ProtectionsEnum.BLOCKED_ITEM)) {
                event.setCurrentItem(null);
                this.warnPlayer(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        if (this.hasPerm(player)) return;
        if (BlockedItems.instance.matches(event.getItem(), false, ProtectionsEnum.BLOCKED_ITEM)) {
            event.setCancelled(true);
            event.getItem().remove();
            this.warnPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (this.hasPerm(player)) return;
        if (BlockedItems.instance.matches(event.getItemDrop(), false, ProtectionsEnum.BLOCKED_ITEM)) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            this.warnPlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerHeldItem(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        if (this.hasPerm(player)) return;
        int slot = event.getNewSlot();
        ItemStack stack = player.getInventory().getItem(slot);

        if (stack != null) {
            if (BlockedItems.instance.matches(stack, false, ProtectionsEnum.BLOCKED_ITEM)) {
                player.getInventory().setItem(slot, null);
                this.warnPlayer(event.getPlayer());
            }
        }
    }
}