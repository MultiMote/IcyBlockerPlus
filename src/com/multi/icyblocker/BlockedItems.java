package com.multi.icyblocker;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by MultiMote on 24.11.2014.
 */
public class BlockedItems {
    private static final String FILENAME = "data.yml";

    public static BlockedItems instance = new BlockedItems();
    private List<ItemData> blockedItems;
    private List<ItemData> protectedBlocks;
    private File listFile;
    private Core coreInstance;
    private YamlConfiguration yamlList;

    private BlockedItems() {
        this.blockedItems = new ArrayList<ItemData>();
        this.protectedBlocks = new ArrayList<ItemData>();
    }


    public boolean matches(ItemStack item, boolean using, ProtectionsEnum protectionType) { //спички
        return this.matches(item.getType().name(), Short.toString(item.getDurability()), using, protectionType);
    }

    public boolean matches(Item item, boolean using, ProtectionsEnum protectionType) {
        return this.matches(item.getItemStack(), using, protectionType);
    }

    public boolean matches(Block block, boolean using, ProtectionsEnum protectionType) {
        return this.matches(block.getType().name(), Short.toString(block.getState().getData().toItemStack().getDurability()), using, protectionType);
    }

    public boolean matches(String name, String meta, boolean using, ProtectionsEnum protectionType) {
        if (protectionType == ProtectionsEnum.BLOCKED_ITEM) {
            for (ItemData item : this.blockedItems) {
                if (name.equals(item.getName()) && (item.ignoresMeta() || meta.equals(item.getMeta()))) {
                    return !(!using && item.containsParam("CR"));
                }
            }
        } else if (protectionType == ProtectionsEnum.PROTECTED_BLOCK) {
            for (ItemData item : this.protectedBlocks) {
                if (name.equals(item.getName()) && (item.ignoresMeta() || meta.equals(item.getMeta()))) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean matches(ItemData data, ProtectionsEnum protectionType) {
        return this.matches(data.getName(), data.getMeta(), true, protectionType);
    }

    public boolean matches(ItemData first, ItemData second) {
        return first.getName().equals(second.getName()) && (first.ignoresMeta() || first.getMeta().equals(second.getMeta()));
    }

    public void add(ItemData item, ProtectionsEnum protectionType) {
        if (protectionType == ProtectionsEnum.BLOCKED_ITEM) {
            this.blockedItems.add(item);
        } else if (protectionType == ProtectionsEnum.PROTECTED_BLOCK) {
            this.protectedBlocks.add(item);
        }
        this.saveFile();
    }

    public boolean matchAndRemove(ItemData item, ProtectionsEnum protectionType) {
        if (protectionType == ProtectionsEnum.BLOCKED_ITEM) {
            int i = 0;
            for (ItemData id : this.blockedItems) {
                if (this.matches(id, item)) {
                    this.blockedItems.remove(i);
                    this.saveFile();
                    return true;
                }
                i++;
            }
        } else if (protectionType == ProtectionsEnum.PROTECTED_BLOCK) {
            int i = 0;
            for (ItemData id : this.protectedBlocks) {
                if (this.matches(id, item)) {
                    this.protectedBlocks.remove(i);
                    this.saveFile();
                    return true;
                }
                i++;
            }
        }
        return false;
    }

    public String generateList(ProtectionsEnum protectionType) {
        String s = "";
        if (protectionType == ProtectionsEnum.BLOCKED_ITEM) {
            if (this.blockedItems.isEmpty()) return "пусто";

            for (int i = 0; i < this.blockedItems.size(); i++) {
                ItemData data = this.blockedItems.get(i);
                s += data.getName();
                s += data.ignoresMeta() ? "(любая мета)" : ":" + data.getMeta();
                s += data.containsParam("CR") ? "(крафт)" : "";
                if (i < this.blockedItems.size() - 1) s += ", ";
            }
        } else if (protectionType == ProtectionsEnum.PROTECTED_BLOCK) {
            if (this.protectedBlocks.isEmpty()) return "пусто";

            for (int i = 0; i < this.protectedBlocks.size(); i++) {
                ItemData data = this.protectedBlocks.get(i);
                s += data.getName();
                s += data.ignoresMeta() ? "(любая мета)" : ":" + data.getMeta();
                if (i < this.protectedBlocks.size() - 1) s += ", ";
            }
        }
        return s;
    }


    public void setCoreInstance(Core core) {
        this.coreInstance = core;
    }

    public void readFile() {
        this.listFile = new File(this.coreInstance.getDataFolder(), FILENAME);
        if (!this.listFile.exists()) {
            Core.getPluginLogger().info("Item list is not exists, creating...");
            this.coreInstance.saveResource(FILENAME, false);
        }
        this.yamlList = YamlConfiguration.loadConfiguration(this.listFile);

        if (this.yamlList.get("BlockedItems") instanceof List) {
            List strings = (List) this.yamlList.get("BlockedItems");
            for (Object obj : strings) {
                ItemData data = ItemData.parse(obj.toString());
                if (data != null) {
                    this.blockedItems.add(data);
                } else
                    Core.getPluginLogger().log(Level.WARNING, "Can't parse item " + obj + ", check your " + FILENAME);
            }
        }

        if (this.yamlList.get("ProtectedBlocks") instanceof List) {
            List strings = (List) this.yamlList.get("ProtectedBlocks");
            for (Object obj : strings) {
                ItemData data = ItemData.parse(obj.toString());
                if (data != null) {
                    this.protectedBlocks.add(data);
                } else
                    Core.getPluginLogger().log(Level.WARNING, "Can't parse block " + obj + ", check your " + FILENAME);
            }
        }
    }

    public void saveFile() {
        try {
            ArrayList<String> list = new ArrayList<String>();
            for (ItemData data : this.blockedItems) list.add(data.toString());
            Collections.sort(list);
            this.yamlList.set("BlockedItems", list);

            ArrayList<String> list2 = new ArrayList<String>();
            for (ItemData data : this.protectedBlocks) list2.add(data.toString());
            Collections.sort(list2);
            this.yamlList.set("ProtectedBlocks", list2);

            this.yamlList.save(this.listFile);
        } catch (IOException ex) {
            Core.getPluginLogger().info("Can't write " + FILENAME + ", because " + ex.getMessage());
        }
    }

}
