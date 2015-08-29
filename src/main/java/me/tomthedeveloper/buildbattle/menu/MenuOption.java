package me.tomthedeveloper.buildbattle.menu;

import me.TomTheDeveloper.Utils.ItemBuilder;
import me.TomTheDeveloper.Utils.Util;
import me.tomthedeveloper.buildbattle.BuildPlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 18/08/2015.
 */
public class MenuOption {


    private Material material;
    private String displayname;
    private String[] lore;
    private BuildPlot buildPlot;
    private byte data;


    public byte getData() {
        return data;
    }

    public void setData(byte data) {
        this.data = data;
    }

    public MenuOption(BuildPlot buildPlot){
        this.buildPlot = buildPlot;
    }

    public BuildPlot getRelatedBuildPlot(){
        return buildPlot;
    }


    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public List<String> getLore() {
        return Arrays.asList(lore);
    }

    public void setLore(List<String> lore) {

        this.lore = lore.toArray(new String[lore.size()]);
    }

    public ItemStack getItemStack(){
        ItemStack itemStack = new ItemStack(getMaterial());
        Util.setItemNameAndLore( itemStack,this.getDisplayname(),lore);
        return itemStack;
    }
}
