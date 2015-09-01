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
    private static List<String> winCommands = new ArrayList<String>();
    private static List<String> endGameCommands = new ArrayList<String>();
    private static List<String> secondPlaceCommands = new ArrayList<String>();

    private static List<String> thirdPlaceCommands = new ArrayList<String>();


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

    public static void loadWinCommands(){
        if(!config.contains("Win-Commands")){
            config.set("Win-Commands", Arrays.asList(new String[]{"say %PLAYER% won the game!", "give %PLAYER% 1000"}));
            saveConfig();
        }
        for(String command:config.getStringList("Win-Commands")){
            winCommands.add(command);
        }

    }

    public static void loadThirdPlaceCommands(){
        if(!config.contains("Third-Place-Commands")){
            config.set("Third-Place-Commands", Arrays.asList(new String[]{"say %PLAYER% became third", "give %PLAYER% 1000"}));
            saveConfig();
        }
        for(String command:config.getStringList("Third-Place-Commands")){
            thirdPlaceCommands.add(command);
        }

    }
    public static void loadSecondPlaceCommands(){
        if(!config.contains("Second-Place-Commands")){
            config.set("Second-Place-Commands", Arrays.asList(new String[]{"say %PLAYER% become second", "give %PLAYER% 1000"}));
            saveConfig();
        }
        for(String command:config.getStringList("Second-Place-Commands")){
            secondPlaceCommands.add(command);
        }

    }

    public static List<String> getSecondPlaceCommands(){
        return secondPlaceCommands;
    }

    public static List<String> getThirdPlaceCommands(){
        return thirdPlaceCommands;
    }
    public static List<String> getWinCommands(){
        return winCommands;
    }

    public static List<String> getEndGameCommands(){
        return endGameCommands;
    }


    public static void loadEndGameCommands(){
        if(!config.contains("End-Game-Commands")){
            config.set("End-Game-Commands", Arrays.asList(new String[]{"say %PLAYER% has played a game!", "give %PLAYER% 100"}));
            saveConfig();
        }
        for(String command:config.getStringList("End-Game-Commands")){
            endGameCommands.add(command);
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


    public static boolean restartOnEnd(){
        return options.get("Bungee-Restart-On-End")==1;
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

    public static boolean isWinCommandsEnabled(){
        return options.get("Win-Commands-Activated")==1;
    }

    public static boolean isSecondPlaceCommandsEnabled(){
        return options.get("Second-Place-Commands-Activated")==1;
    }
    public static boolean isThirdPlaceCommandsEnabled(){
        return options.get("Third-Place-Commands-Activated")==1;
    }
    public static boolean isEndGameCommandsEnabled(){
        return options.get("End-Game-Commands-Activated")==1;
    }
    public static void loadOptions(){

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
        loadOptions.add("Bungee-Restart-On-End");
        loadOptions.add("Particle-Offset");
        loadOptions.add("Win-Commands-Activated");
        loadOptions.add("End-Game-Commands-Activated");
        loadOptions.add("Second-Place-Commands-Activated");
        loadOptions.add("Third-Place-Commands-Activated");

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
                if(option.equals("Win-Commands-Activated"))
                    config.set("Win-Commands-Activated",false);
                if(option.equals("Second-Place-Commands-Activated"))
                    config.set("Second-Place-Commands-Activated",false);
                if(option.equals("Third-Place-Commands-Activated"))
                    config.set("Third-Place-Commands-Activated",false);
                if(option.equals("End-Game-Commands-Activated"))
                    config.set("End-Game-Commands-Activated",true);
                if(option.equals("Bungee-Restart-On-End"))
                    config.set("Bungee-Restart-On-End",false);
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
