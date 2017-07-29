package me.tomthedeveloper.buildbattle.events;

import me.TomTheDeveloper.Game.GameInstance;
import me.tomthedeveloper.buildbattle.BuildBattle;
import me.tomthedeveloper.buildbattle.BuildPlot;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;

/**
 * Created by Tom on 6/02/2016.
 */
public class v1_8IngameEvents implements Listener{



    private BuildBattle plugin;


    public v1_8IngameEvents(BuildBattle plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void onTntExplode(BlockExplodeEvent event) {
        for (GameInstance gameInstance : plugin.getGameAPI().getGameInstanceManager().getGameInstances()) {
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for (BuildPlot buildPlot : buildInstance.getPlotManager().getPlots()) {

                if (buildPlot.isInPlotRange(event.getBlock().getLocation(), 0)) {
                    event.blockList().clear();
                } else if (buildPlot.isInPlotRange(event.getBlock().getLocation(), 5)) {
                    event.getBlock().getLocation().getBlock().setType(Material.TNT);
                    event.blockList().clear();
                    event.setCancelled(true);
                }
            }
        }
    }


}
