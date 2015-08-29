package me.tomthedeveloper.buildbattle.playerheads;

import me.TomTheDeveloper.Handlers.ConfigurationManager;
import me.TomTheDeveloper.Utils.Items;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.Utils.Util;
import me.tomthedeveloper.buildbattle.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 26/08/2015.
 */
public class HeadsItem {

    private Material material;
    private Byte data = null;
    private String[] lore;
    private String displayName;
    private String permission;
    private boolean enabled = true;
    private Location location;
    private int slot;
    private String owner;
    private String config;
    private int size=18;
    private String menuName;
    private ItemStack itemStack=null;


    public HeadsItem() {

    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void onClick(Player player) {
        player.getInventory().addItem(Items.getPlayerHead(Bukkit.getOfflinePlayer(owner)));
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public FileConfiguration getConfig() {
        return ConfigurationManager.getConfig("playerheadmenu/menus/"+config);
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getConfigName(){
        return config;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public byte getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data.byteValue();
    }

    public List<String> getLore() {
        List<String> lorelist = new ArrayList<String>();
        for(String string:lore){
            string = ChatFormatter.formatMessage(string);
            lorelist.add(string);
        }
        return lorelist;
    }

    public void setLore(String[] lore) {
        this.lore = lore;
    }

    public String getDisplayName() {
        return ChatFormatter.formatMessage(displayName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public void setLore(List<String> lore) {

        this.lore = lore.toArray(new String[lore.size()]);
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        if(itemStack == null) {
            if (data != null) {
                itemStack = new ItemStack(getMaterial(), 1, getData());
            } else {
                itemStack = new ItemStack(getMaterial());

            }
            if (itemStack.getType() == Material.SKULL_ITEM && itemStack.getData().getData() == SkullType.PLAYER.ordinal()) {
                itemStack = Items.getPlayerHead(Bukkit.getOfflinePlayer(getOwner()));
            }
            Util.setItemNameAndLore(itemStack, ChatFormatter.formatMessage(this.getDisplayName()), getLore().toArray(new String[getLore().size()]));

            return itemStack;
        }else{
            return itemStack;
        }
    }
}
