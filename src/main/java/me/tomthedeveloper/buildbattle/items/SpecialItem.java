package me.tomthedeveloper.buildbattle.items;

import me.TomTheDeveloper.Handlers.ConfigurationManager;
import me.TomTheDeveloper.Utils.ParticleEffect;
import me.TomTheDeveloper.Utils.Util;
import me.tomthedeveloper.buildbattle.ChatFormatter;
import me.tomthedeveloper.buildbattle.entities.EntityItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 5/02/2016.
 */
public class SpecialItem {


    private Material material;
    private Byte data = null;
    private String[] lore;
    private String displayName;
    private ParticleEffect effect;
    private String permission;
    private boolean enabled = true;
    private Location location;
    private int slot;
    private String name;

    public SpecialItem(String name){
        this.name = name;

    }

    public static void loadAll(){
        new SpecialItem("Leave").load(ChatColor.RED + "Leave", new String[]{
                org.bukkit.ChatColor.GRAY + "Click to teleport to hub"
        }, Material.BED,8);
    }

    public void load(String displayName, String[] lore, Material material, int slot){
        FileConfiguration config = ConfigurationManager.getConfig("SpecialItems");


        if(!config.contains(name)){
            config.set(name + ".data",0);
            config.set(name + ".displayname",displayName);
            config.set(name + ".lore", Arrays.asList(lore));
            config.set(name + ".material", material.getId());
            config.set(name + ".slot",slot);
        }
        try {
            config.save(ConfigurationManager.getFile("SpecialItems"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SpecialItem particleItem = new SpecialItem(name);
        particleItem.setData(config.getInt(name + ".data"));
        particleItem.setEnabled(config.getBoolean(name + ".enabled"));
        particleItem.setMaterial(org.bukkit.Material.getMaterial(config.getInt(name + ".material")));
        particleItem.setLore(config.getStringList(name + ".lore"));
        particleItem.setDisplayName(config.getString(name + ".displayname"));
        particleItem.setPermission(config.getString(name + ".permission"));
        particleItem.setSlot(config.getInt(name + ".slot"));
        SpecialItemManager.addEntityItem(name, particleItem);

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

    public String[] getLore() {
        return lore;
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

    public ParticleEffect getEffect() {
        return effect;
    }

    public void setEffect(ParticleEffect effect) {
        this.effect = effect;
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

    public ItemStack getItemStack(){
        ItemStack itemStack;
        if(data != null) {
            itemStack = new ItemStack(getMaterial(), 1, getData());
        }else{
            itemStack = new ItemStack(getMaterial());

        }
        Util.setItemNameAndLore(itemStack, ChatFormatter.formatMessage(this.getDisplayName()), lore);
        return itemStack;
    }


}
