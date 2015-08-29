package me.tomthedeveloper.buildbattle;

import me.TomTheDeveloper.Handlers.ConfigurationManager;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tom on 17/08/2015.
 */
public class ConfigPreferences {

    private static FileConfiguration config = ConfigurationManager.getConfig("config");
    private static HashMap<String,Integer>  options = new HashMap<String, Integer>();
    private static BuildBattle buildBattle;

    public ConfigPreferences(BuildBattle buildBattle){
        config = buildBattle.getConfig();
        this.buildBattle = buildBattle;
    }


    public static void loadThemes(){
        if(!config.contains("themes")){
            config.set("themes", Arrays.asList(new String[]{"Bees", "Pirates", "Medieval","Castle", "Love", "Hate", "Ugly", "SchoolBus"}));
            saveConfig();
        }
        for(String theme:config.getStringList("themes")){
            BuildInstance.addTheme(theme);
        }

    }

    public static boolean isMobSpawningDisabled(){
        return options.get("Disable-Mob-Spawning-Completely") == 1;
    }

    public static boolean isDynamicSignSystemEnabled(){
        return options.get("Dynamic-Sign-System") == 1;
    }

    public static int getDefaultFloorMaterial(){
        return options.get("Default-Floor-Material");
    }

    public static void loadBlackList(){
        if(!config.contains("blacklist")){
            config.set("blacklist", Arrays.asList(new int[]{46, 57}));
            saveConfig();
        }
        for(int ID:config.getIntegerList("blacklist")){
            BuildInstance.addToBlackList(ID);
        }

    }

    public static boolean isBarEnabled(){
        if(options.get("bar")==1)
            return true;
        return false;
    }

    public static int getAmountFromOneParticle(){
       return options.get("Amount-One-Particle-Effect-Contains");
    }

    public static int getMaxParticles(){
        return options.get("Max-Amount-Particles");
    }



    public static int getVotingTime(){
        return options.get("Voting-Time-In-Seconds");
    }

    public static int getBuildTime(){
        return options.get("Build-Time-In-Seconds");
    }

    public static boolean getBungeeShutdown(){
        return options.get("Bungee-Shutdown-On-End") == 1;
    }

    public static int getParticlOffset(){
        return options.get("Particle-Offset");
    }

    public static void loadOptions(){
        loadThemes();
        loadBlackList();
        List<String> loadOptions = new ArrayList<String>();
        loadOptions.add("Build-Time-In-Seconds");
        loadOptions.add("Voting-Time-In-Seconds");
        loadOptions.add("bar");
        loadOptions.add("Fly-Range-Out-Plot");
        loadOptions.add("Default-Floor-Material");
        loadOptions.add("Disable-Mob-Spawning-Completely");
        loadOptions.add("Dynamic-Sign-System");
        loadOptions.add("Amount-One-Particle-Effect-Contains");
        loadOptions.add("Max-Amount-Particles");
        loadOptions.add("Particle-Refresh-Per-Tick");
        loadOptions.add("Bungee-Shutdown-On-End");
        loadOptions.add("Particle-Offset");
        for(String option:loadOptions){
            if(config.contains(option)) {
                if(config.isBoolean(option)){
                    boolean b = config.getBoolean(option);
                    if(b){
                        options.put(option,1);
                    }else{
                        options.put(option,0);
                    }
                }else {
                    options.put(option, config.getInt(option));
                }
            }else{
                if(option.equalsIgnoreCase("Build-Time-In-Seconds"))
                    config.set("Build-Time-In-Seconds",60*8);
                if(option.equals("Voting-Time-In-Seconds"))
                    config.set("Voting-Time-In-Seconds",20);
                if(option.equals("bar"))
                    config.set("bar", true);
                if(option.equals("Fly-Range-Out-Plot"))
                    config.set("Fly-Range-Out-Plot",5);
                if(option.equals("Default-Floor-Material"))
                    config.set("Default-Floor-Material",17);
                if(option.equals("Disable-Mob-Spawning-Completely"))
                    config.set("Disable-Mob-Spawning-Completely",true);
                if(option.equals("Dynamic-Sign-System"))
                    config.set("Dynamic-Sign-System",true);
                if(option.equals("Amount-One-Particle-Effect-Contains"))
                    config.set("Amount-One-Particle-Effect-Contains",20);
                if(option.equals("Max-Amount-Particles"))
                    config.set("Max-Amount-Particles",25);
                if(option.equals("Particle-Refresh-Per-Tick"))
                    config.set("Particle-Refresh-Per-Tick",10);
                if(option.equals("Bungee-Shutdown-On-End"))
                    config.set("Bungee-Shutdown-On-End",false);
                if(option.equals("Particle-Offset"))
                    config.set("Particle-Offset",1);
                saveConfig();
        }
            saveConfig();
        }
    }

    public static long getParticleRefreshTick(){
        return options.get("Particle-Refresh-Per-Tick");
    }



    public static int getExtraPlotRange(){
        return options.get("Fly-Range-Out-Plot");
    }


    private static void saveConfig(){
       buildBattle.saveConfig();
    }

}
