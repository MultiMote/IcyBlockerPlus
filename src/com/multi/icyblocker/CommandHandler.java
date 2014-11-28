package com.multi.icyblocker;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.DARK_RED + "Команда должна выполняться игроком.");
            return false;
        }

        if (args.length < 1) {
            this.printHelp(player);
            return false;
        }

        ItemStack held = player.getItemInHand();


        if (args[0].equals("block")) {
            if (this.checkPermAndSendMsg("icyblocker.add", sender))return false;
            if (this.checkItemAndSendMsg(held, sender))return false;

            String name = held.getType().name();
            String meta = Short.toString(held.getDurability());

            boolean nometa = this.containsArg(args, "ignoremeta") || this.containsArg(args, "nometa");
            boolean craft = this.containsArg(args, "allowcraft") || this.containsArg(args, "craft");

            ItemData data = ItemData.create(name, nometa ? "-1" : meta, craft ? "CR" : "");

            if(BlockedItems.instance.matches(data, ProtectionsEnum.BLOCKED_ITEM)){
                sender.sendMessage(ChatColor.DARK_RED + "Предмет " + ChatColor.GRAY + name + ChatColor.GREEN + " уже блокируется.");
                return false;
            }else {
                BlockedItems.instance.add(data, ProtectionsEnum.BLOCKED_ITEM);
                String str  = "Предмет " + ChatColor.GRAY + name + ChatColor.GREEN + " теперь блокируется";
                if(nometa) str += ", любая метадата";
                if(craft) str += ", крафт разрешён";
                str += ".";
                sender.sendMessage(ChatColor.GREEN + str);
                return true;
            }


        }
        else if (args[0].equals("unblock")) {
            if (this.checkPermAndSendMsg("icyblocker.remove", sender))return false;
            if (this.checkItemAndSendMsg(held, sender))return false;

            String name = held.getType().name();
            String meta = Short.toString(held.getDurability());

            boolean nometa = this.containsArg(args, "ignoremeta") || this.containsArg(args, "nometa");

            ItemData data = ItemData.create(name, nometa ? "-1" : meta);

            if(BlockedItems.instance.matchAndRemove(data, ProtectionsEnum.BLOCKED_ITEM)){
                sender.sendMessage(ChatColor.GREEN + "Предмет " + ChatColor.GRAY + name + ChatColor.GREEN + " больше не блокируется.");
                return true;
            }else {
                sender.sendMessage(ChatColor.DARK_RED + "Нечего удалять.");
                return false;
            }

        }else if (args[0].equals("protect")) {
            if (this.checkPermAndSendMsg("icyblocker.add", sender))return false;
            if (this.checkItemAndSendMsg(held, sender))return false;

            String name = held.getType().name();
            String meta = Short.toString(held.getDurability());

            boolean nometa = this.containsArg(args, "ignoremeta") || this.containsArg(args, "nometa");

            ItemData data = ItemData.create(name, nometa ? "-1" : meta);

            if(BlockedItems.instance.matches(data, ProtectionsEnum.PROTECTED_BLOCK)){
                sender.sendMessage(ChatColor.DARK_RED + "Предмет " + ChatColor.GRAY + name + ChatColor.GREEN + " уже защищен.");
                return false;
            }else {
                BlockedItems.instance.add(data, ProtectionsEnum.PROTECTED_BLOCK);
                String str  = "Предмет " + ChatColor.GRAY + name + ChatColor.GREEN + " теперь защищен";
                if(nometa) str += ", любая метадата";
                str += ".";
                sender.sendMessage(ChatColor.GREEN + str);
                return true;
            }


        }
        else if (args[0].equals("unprotect")) {
            if (this.checkPermAndSendMsg("icyblocker.remove", sender))return false;
            if (this.checkItemAndSendMsg(held, sender))return false;

            String name = held.getType().name();
            String meta = Short.toString(held.getDurability());

            boolean nometa = this.containsArg(args, "ignoremeta") || this.containsArg(args, "nometa");

            ItemData data = ItemData.create(name, nometa ? "-1" : meta);

            if(BlockedItems.instance.matchAndRemove(data, ProtectionsEnum.PROTECTED_BLOCK)){
                sender.sendMessage(ChatColor.GREEN + "Предмет " + ChatColor.GRAY + name + ChatColor.GREEN + " больше не защищен.");
                return true;
            }else {
                sender.sendMessage(ChatColor.DARK_RED + "Нечего удалять.");
                return false;
            }
        }
        else if (args[0].equals("blocked") || args[0].equals("items") || args[0].equals("list")) {
            player.sendMessage(ChatColor.GREEN + "Заблокированные предметы: " + ChatColor.GRAY + BlockedItems.instance.generateList(ProtectionsEnum.BLOCKED_ITEM) + ";");
            player.sendMessage(ChatColor.GREEN + "Защищенные предметы: " + ChatColor.GRAY + BlockedItems.instance.generateList(ProtectionsEnum.PROTECTED_BLOCK) + ";");
            return true;
        } else this.printHelp(player);

        return false;
    }

    public void printHelp(Player player) {
        player.sendMessage(ChatColor.YELLOW + "Возможные варианты:");
        player.sendMessage(ChatColor.GREEN + "icy block "+ ChatColor.GRAY +"[ignoremeta / nometa] [allowcraft / craft]");
        player.sendMessage(ChatColor.RED + "icy unblock "+ ChatColor.GRAY + "[ignoremeta / nometa]");
        player.sendMessage(ChatColor.GREEN + "icy protect "+ ChatColor.GRAY +"[ignoremeta / nometa]");
        player.sendMessage(ChatColor.RED + "icy unprotect "+ ChatColor.GRAY + "[ignoremeta / nometa]");
        player.sendMessage(ChatColor.LIGHT_PURPLE + "icy <blocked / items / list>");
        player.sendMessage(ChatColor.GRAY + "предмет должен находиться в руке");
    }

    private boolean checkPermAndSendMsg(String perm, CommandSender sender) {
        if (!(sender.hasPermission(perm) || sender.isOp())) {
            sender.sendMessage(ChatColor.DARK_RED + "Не дозволено.");
            return true;
        }
        return false;
    }

    private boolean checkItemAndSendMsg(ItemStack is, CommandSender sender) {
        if (is == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Нужно держать предмет в руке.");
            return true;
        } else if (is.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.DARK_RED + "Нужно держать предмет в руке.");
            return true;
        }
        return false;
    }

    private boolean containsArg(String[] args, String arg) {
        for (String a : args)
            if (a.equals(arg)) return true;
        return false;
    }
}
