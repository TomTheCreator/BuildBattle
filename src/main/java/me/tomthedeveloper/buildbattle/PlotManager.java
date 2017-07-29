package me.tomthedeveloper.buildbattle;

import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.Handlers.UserManager;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tom on 17/08/2015.
 */
public class PlotManager {


    private List<BuildPlot> plots = new ArrayList<BuildPlot>();
    private List<BuildPlot> plotsToClear = new ArrayList<BuildPlot>();
    private BuildInstance buildInstance;

    public PlotManager(BuildInstance buildInstance){
        this.buildInstance = buildInstance;
    }


    public  void  addBuildPlot(BuildPlot buildPlot){
        plots.add(buildPlot);
    }

    public void distributePlots(){
        List<Player> players =  new ArrayList<Player>(buildInstance.getPlayers());
        for(BuildPlot buildPlot:plots){
            if(!players.isEmpty() &&buildPlot.getOwner() == null &&getPlot(players.get(0)) == null) {
                buildPlot.setOwner(players.get(0).getUniqueId());
                UserManager.getUser(players.get(0).getUniqueId()).setObject(buildPlot, "plot");

                players.remove(0);
                continue;
            }else{
                break;
            }
        }
        if(!players.isEmpty()){
            System.out.print("YOU HAVENT SET ENOUGH PLOTS! SET FOR ARENA " + buildInstance.getID() + ". YOU HAVE TO SET " + players.size() + " MORE PLOTS!");
            System.out.print("STOPPING THE GAME");
            buildInstance.setGameState(GameState.ENDING);
        }
    }

    public void addToClearList(BuildPlot buildPlot){
        plotsToClear.add(buildPlot);
    }

    public BuildPlot getPlot(Player player){
        for(BuildPlot buildPlot:plots){
            if(buildPlot.getOwner() != null) {
                if (buildPlot.getOwner()==player.getUniqueId())
                    return buildPlot;
            }
        }
        return null;
    }

    public BuildPlot getPlot(UUID uuid){
        for(BuildPlot buildPlot:plots){
            if(buildPlot.getOwner() != null) {
                if (buildPlot.getOwner().equals(uuid))
                    return buildPlot;
            }
        }
        return null;
    }
    public void resetPlots(){
                for(BuildPlot buildPlot:plots){
                    buildPlot.reset();
                }


    }

    public void resetQeuedPlots(){
        for(BuildPlot buildPlot:plotsToClear){
            buildPlot.reset();
        }
        plotsToClear.clear();
    }

    public boolean isPlotsCleared(){
        return plotsToClear.isEmpty();
    }

    public void resetPlotsGradually(){
        if(plotsToClear.isEmpty())
            return;

            plotsToClear.get(0).reset();
        plotsToClear.remove(0);
    }

    public void teleportToPlots(){
        for(BuildPlot buildPlot:plots){
            if(buildPlot.getOwner() != null) {
                Location tploc = buildPlot.getCenter();
                while(tploc.getBlock().getType() != Material.AIR)
                    tploc = tploc.add(0,1,0);
                Player player = Bukkit.getServer().getPlayer(buildPlot.getOwner());
                if(player != null){
                    player.teleport(buildPlot.getCenter());
                }
            }
        }
    }

    public void teleportAllToPlot(BuildPlot buildPlot){
                Location tploc = buildPlot.getTeleportLocation();
                while(tploc.getBlock().getType() != Material.AIR) {
                    tploc = tploc.add(0, 1, 0);
                }
                for(Player player:buildInstance.getPlayers()) {
                  //  Bukkit.getServer().getPlayer(buildPlot.getOwner()).teleport(player);
                    player.teleport(tploc);
                    player.setGameMode(GameMode.CREATIVE);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                }


    }


    public List<BuildPlot> getPlots(){
        return plots;
    }




}
