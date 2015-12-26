package me.tomthedeveloper.buildbattle;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.GameAPI;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.User;
import me.tomthedeveloper.buildbattle.events.IngameEvents;
import me.tomthedeveloper.buildbattle.events.NormalEvents;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import me.tomthedeveloper.buildbattle.particles.ParticleHandler;
import me.tomthedeveloper.buildbattle.particles.ParticleMenu;
import me.tomthedeveloper.buildbattle.playerheads.PlayerHeadsMenu;
import me.tomthedeveloper.buildbattle.stats.FileStats;
import me.tomthedeveloper.buildbattle.stats.MySQLDatabase;
import me.tomthedeveloper.buildbattle.stats.statsCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 17/08/2015.
 */
public class BuildBattle extends GameAPI {


    private boolean databaseActivated = false;
    private MySQLDatabase database;
    private FileConfiguration statsConfig = null;
    private FileStats fileStats;

    @Override
    public void setupMessageConfig(){
        ChatManager.getFromLanguageConfig("Waiting-For-Players-Message", "Waiting for players... We need at least " + ChatManager.HIGHLIGHTED + "%MINPLAYERS%"+ ChatManager.NORMAL +" players to start.");
        ChatManager.getFromLanguageConfig("Enough-Players-To-Start","We now have enough players. The game is starting soon!");
        ChatManager.getFromLanguageConfig("The-Game-Has-Started", "The game has started! Start building guys!!");
        ChatManager.getFromLanguageConfig("Scoreboard-Header", ChatManager.PREFIX + "BuildBattle");
        ChatManager.getFromLanguageConfig("Scoreboard-Players", ChatManager.NORMAL + "Players ");
        ChatManager.getFromLanguageConfig("Scoreboard-Min-Players-Message", ChatManager.NORMAL + "Min Players ");
        ChatManager.getFromLanguageConfig("Scoreboard-Starting-In", ChatManager.NORMAL + "Starting in ");
        ChatManager.getFromLanguageConfig("SCOREBOARD-Time-Left",ChatColor.RED  + "" + ChatColor.BOLD+ "Time Left: ");
        ChatManager.getFromLanguageConfig("SCOREBOARD-Theme", ChatColor.GREEN + "Theme");
        ChatManager.getFromLanguageConfig("Time-Left-To-Build",ChatManager.PREFIX + "%FORMATTEDTIME% " + ChatManager.NORMAL + "time left to build!");
        ChatManager.getFromLanguageConfig("Cant-Fly-Out-Of-Plot",ChatColor.RED + "U can't fly so far out!");
        ChatManager.getFromLanguageConfig("Voted", ChatColor.GREEN + "Voted succesfully!");
        ChatManager.getFromLanguageConfig("Voting-For-Player-Plot", ChatManager.NORMAL + "Voting for " + ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatManager.NORMAL + "'s plot!");
        ChatManager.getFromLanguageConfig("Winner-Announcement-Footer-Line", ChatColor.GREEN + "==============================");
        ChatManager.getFromLanguageConfig("Waiting-For-Players-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " +
                ChatManager.HIGHLIGHTED + "TomTheDeveloper");
        ChatManager.getFromLanguageConfig("Starting-Bar-Message",ChatManager.PREFIX + "BuildBattle made by " +
                ChatManager.HIGHLIGHTED + "TomTheDeveloper");
        ChatManager.getFromLanguageConfig("Time-Left-Bar-Message", ChatManager.PREFIX + "Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%");
        ChatManager.getFromLanguageConfig("Vote-Time-Left-Bar-Message", ChatManager.PREFIX + "Vote Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%");
        ChatManager.getFromLanguageConfig("Floor-Option-Name", ChatColor.GREEN + "Floor Material");
        ChatManager.getFromLanguageConfig("Floor-Changed", ChatColor.GREEN + "Floor changed!");
        ChatManager.getFromLanguageConfig("Ingame-Menu-Name","Options Menu");
        ChatManager.getFromLanguageConfig("Options-Menu-Item", ChatColor.GREEN + "Options");
        ChatManager.getFromLanguageConfig("Options-Lore", ChatColor.GRAY + "Right click to open");
        ChatManager.getFromLanguageConfig("Drag-And-Drop-Item-Here", "Drag and drop an item here");
        ChatManager.getFromLanguageConfig("To-Change-Floor", "to change the floor");
        ChatManager.getFromLanguageConfig("Join", ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatColor.GRAY + " joined the game (%PLAYERSIZE%/%MAXPLAYERS%)!");
        ChatManager.getFromLanguageConfig("Leave", ChatManager.HIGHLIGHTED + "%PLAYER% " + ChatColor.GRAY + "left the game (%PLAYERSIZE%/%MAXPLAYERS%)!");
        ChatManager.getFromLanguageConfig("Death", ChatManager.HIGHLIGHTED +"%PLAYER% " + ChatColor.GRAY + "died!");
        ChatManager.getFromLanguageConfig("Seconds-Left-Until-Game-Starts", "The game starts in " + ChatManager.HIGHLIGHTED + "%TIME%" + ChatColor.GRAY + " seconds!");
        ChatManager.getFromLanguageConfig("Waiting-For-Players", "Waiting for players... We need at least " + ChatManager.HIGHLIGHTED + "%MINPLAYERS%"+ ChatColor.GRAY +" players to start.");
        ChatManager.getFromLanguageConfig("Enough-Players-To-Start", "We now have enough players. The game is starting soon!");
        ChatManager.getFromLanguageConfig("Teleport-To-EndLocation-In-X-Seconds", "You will be teleported to the lobby in " + ChatManager.HIGHLIGHTED + "%TIME%" + ChatColor.GRAY + " seconds");
        ChatManager.getFromLanguageConfig("Cant-Vote-On-Own-Plot", ChatColor.RED + "U can't vote on your own plot!!");
        ChatManager.getFromLanguageConfig("You-Became-xth",
                ChatColor.GREEN + "You became " + ChatColor.DARK_GREEN + "%NUMBER%" + ChatColor.GREEN + "th");
        ChatManager.getFromLanguageConfig("Kicked-Game-Already-Started", ChatManager.HIGHLIGHTED + "Kicked! Game has already started!");
        ChatManager.getFromLanguageConfig("Particle-Option-Name", ChatColor.GREEN + "Particles");
        ChatManager.getFromLanguageConfig("Coming-Soon", ChatColor.RED +"Coming Soon");
        ChatManager.getFromLanguageConfig("Heads-Option-Name", ChatColor.GREEN + "Heads");
        ChatManager.getFromLanguageConfig("Only-Command-Ingame-Is-Leave",ChatColor.RED + "You have to leave the game first to perform commands. The only command that works is /leave!");
        ChatManager.getFromLanguageConfig("Particle-Menu-Name", "Particle Menu");
        ChatManager.getFromLanguageConfig("No-Permission-For-This-Particle", ChatColor.RED + " No permission for this particle!");
        ChatManager.getFromLanguageConfig("Reached-Max-Amount-Of-Particles", ChatColor.RED + "Reached max amount of particles!");
        ChatManager.getFromLanguageConfig("Particle-Succesfully-Added", ChatColor.GREEN + "Particle succesfully added!");
        ChatManager.getFromLanguageConfig("Particle-Remove-Menu-Name", "Remove Particles");
        ChatManager.getFromLanguageConfig("Location", "Location: ");
        ChatManager.getFromLanguageConfig("Particle-Removed", ChatColor.GREEN + "Particle Removed!");
        ChatManager.getFromLanguageConfig("Remove-Particle-Item-Lore", "Right click to open menu!");
        ChatManager.getFromLanguageConfig("Particle-Option-Lore", ChatColor.GRAY + "Click to open menu");
        ChatManager.getFromLanguageConfig("Player-Head-Main-Inventory-Name","Player Head Menu");
        ChatManager.getFromLanguageConfig("Starting-In-Title-Screen",ChatColor.GREEN + "Starting in " + ChatColor.RED + "%NUMBER");
        ChatManager.getFromLanguageConfig("STATS-AboveLine",ChatColor.BOLD + "-----YOUR STATS----- ");
        ChatManager.getFromLanguageConfig("STATS-Wins",ChatColor.GREEN + "Wins: " +ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Loses",ChatColor.GREEN + "Loses: " +ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Games-Played",ChatColor.GREEN + "Games played: " +ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Highest-Win",ChatColor.GREEN + "Highest win: " +ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Blocks-Placed",ChatColor.GREEN + "Blocks Placed: " +ChatColor.YELLOW );
        ChatManager.getFromLanguageConfig("STATS-Blocks-Broken",ChatColor.GREEN + "Blocks Broken: " +ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-Particles-Placed", ChatColor.GREEN + "Particles Placed: " + ChatColor.YELLOW);
        ChatManager.getFromLanguageConfig("STATS-UnderLinen",ChatColor.BOLD + "--------------------");
        ChatManager.getFromLanguageConfig("Heads-Option-Lore", ChatColor.GRAY + "Open for heads menu!");
    }


    @Override
    public void onEnable(){
        this.setGameName("BuildBattle");
        this.setAbreviation("BD");
        this.setAllowBuilding(true);
        this.onSetup();
        new ConfigPreferences(this);
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadOptions();
        ConfigPreferences.loadThemes();
        ConfigPreferences.loadBlackList();
        ConfigPreferences.loadWinCommands();
        ConfigPreferences.loadSecondPlaceCommands();
        ConfigPreferences.loadThirdPlaceCommands();
        ConfigPreferences.loadEndGameCommands();
        ParticleMenu.loadFromConfig();
        PlayerHeadsMenu.loadHeadItems();
        loadInstances();
        VoteItems.loadVoteItemsFromConfig();
        this.getServer().getPluginManager().registerEvents(new IngameEvents(this),this);
        ParticleHandler particleHandler = new ParticleHandler(this);
        particleHandler.start();
        if(!this.getConfig().contains("DatabaseActivated"))
            this.getConfig().set("DatabaseActivated", false);
        this.saveConfig();
        databaseActivated = this.getConfig().getBoolean("DatabaseActivated");
        if(databaseActivated)
            this.database = new MySQLDatabase(this);
        else{
            fileStats = new FileStats(this);
        }
        this.getCommand("stats").setExecutor(new statsCommand());
        this.getServer().getPluginManager().registerEvents(new NormalEvents(this),this);
        loadStatsForPlayersOnline();
    }

    @Override
    public void onDisable(){
        for(final Player player:getServer().getOnlinePlayers()){
            if(this.getGameInstanceManager().getGameInstance(player) != null){
                this.getGameInstanceManager().getGameInstance(player).leaveAttempt(player);
            }
            final User user = UserManager.getUser(player.getUniqueId());

       /* List<String> temp = new ArrayList<String>();
        temp.add("gamesplayed");
        temp.add("kills");
        temp.add("deaths");
        temp.add("highestwave");
        temp.add("exp");
        temp.add("level");
        temp.add("orbs");
        for (String s : temp) {
            plugin.getMyDatabase().updateDocument(new BasicDBObject("UUID", event.getPlayer().getUniqueId().toString()), new BasicDBObject(s, user.getInt(s)));
            System.out.println("");
        }
        */
            List<String> temp = new ArrayList<String>();
            temp.add("gamesplayed");
            temp.add("wins");
            temp.add("loses");
            temp.add("highestwin");
            temp.add("blocksplaced");
            temp.add("blocksbroken");
            temp.add("particles");
            for(final String s:temp){

                if(this.isDatabaseActivated()) {
                    Bukkit.getScheduler().runTaskAsynchronously(this,new Runnable() {
                        @Override
                        public void run() {
                            int i;
                            try{
                                i = getMySQLDatabase().getStat(player.getUniqueId().toString(),s);
                            }catch (NullPointerException npe){
                                i = 0;
                                System.out.print("COULDN'T GET STATS FROM PLAYER: " + player.getName());
                            }

                            if(i > user.getInt(s)){
                                getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s) + i);
                            }else {
                                getMySQLDatabase().setStat(player.getUniqueId().toString(), s, user.getInt(s));
                            }
                        }
                    });

                }
                else {
                   getFileStats().saveStat(player, s);
                }


            }

            UserManager.removeUser(player.getUniqueId());
        }

    }
    public boolean isDatabaseActivated(){
        return databaseActivated;
    }

    public FileStats getFileStats(){
        return fileStats;
    }


    @Override
    public boolean checkSpecialCommands(Player player, Command command, String s, String[] strings) {
        if(strings.length == 0){
            player.sendMessage(ChatColor.GOLD + "----------------{BuildBattle Commands}----------");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle create <ARENAID>: " + ChatColor.GRAY + "Create an arena!");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle <ARENAID> edit: " + ChatColor.GRAY + "Opens the menu to edit the arena!");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle addplot <ARENAID>: " + ChatColor.GRAY + "Adds a plot to the arena");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle forcestart: " + ChatColor.GRAY + "Forcestarts the arena u are in");
            player.sendMessage(ChatColor.AQUA + "/BuildBattle reload: " + ChatColor.GRAY + "Reloads plugin");
            player.sendMessage(ChatColor.GOLD + "-------------------------------------------------");
            return true;
        }
        if(strings.length == 2 && strings[0].equalsIgnoreCase("addplot")){
            if (this.getGameInstanceManager().getGameInstance(strings[1]) == null) {
                player.sendMessage(ChatColor.RED + "That gameinstance doesn't exist!");
                return true;
            }
            Selection selection = getWorldEditPlugin().getSelection(player);
            if(selection instanceof CuboidSelection){
                if(getConfig().contains("instances." + strings[1] + ".plots")) {
                    this.saveLoc("instances." + strings[1] + ".plots." + (getConfig().getConfigurationSection("instances." + strings[1] + ".plots").getKeys(false).size() + 1) + ".minpoint", selection.getMinimumPoint());
                    this.saveLoc("instances." + strings[1] + ".plots." + (getConfig().getConfigurationSection("instances." + strings[1] + ".plots").getKeys(false).size()) + ".maxpoint", selection.getMaximumPoint());
                }else{
                    this.saveLoc("instances." + strings[1] + ".plots.0.minpoint", selection.getMinimumPoint());
                    this.saveLoc("instances." + strings[1] + ".plots.0.maxpoint", selection.getMaximumPoint());
                }
                this.saveConfig();
                player.sendMessage(ChatColor.GREEN + "Plot added to instance " + ChatColor.RED + strings[1]);
            }else{
                player.sendMessage(ChatColor.RED + "U don't have the right selection!");
            }
            return true;
        }
        if( strings.length ==1&&strings[0].equalsIgnoreCase("forcestart") ){
            if(getGameInstanceManager().getGameInstance(player) == null)
                return false;
            BuildInstance invasionInstance = (BuildInstance)getGameInstanceManager().getGameInstance(player);
            if(invasionInstance.getGameState() == GameState.WAITING_FOR_PLAYERS) {
                invasionInstance.setGameState(GameState.STARTING);
                invasionInstance.getChatManager().broadcastMessage("Admin-ForceStart-Game", ChatManager.HIGHLIGHTED + "An admin forcestarted the game!");
                return true;
            }
            if(invasionInstance.getGameState() == GameState.STARTING) {
                invasionInstance.setTimer(0);
                invasionInstance.getChatManager().broadcastMessage("Admin-Set-Starting-In-To-0", ChatManager.HIGHLIGHTED + "An admin set waiting time to 0. Game starts now!");
                return true;
            }
        }
        if( strings.length ==1&&strings[0].equalsIgnoreCase("reload") ){
            ConfigPreferences.loadOptions();
            ConfigPreferences.loadOptions();
            ConfigPreferences.loadThemes();
            ConfigPreferences.loadBlackList();
            ConfigPreferences.loadWinCommands();
            ConfigPreferences.loadSecondPlaceCommands();
            ConfigPreferences.loadThirdPlaceCommands();
            ConfigPreferences.loadEndGameCommands();
            this.loadInstances();
            player.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
        }
        return false;
    }

    public MySQLDatabase getMySQLDatabase() {
        return database;
    }

    public void getMySQLDatabase(MySQLDatabase database) {
        this.database = database;
    }



    @Override
    public void loadInstances(){
        this.saveConfig();
        if(getGameInstanceManager().getGameInstances() != null) {
            if (getGameInstanceManager().getGameInstances().size() > 0) {
                for (GameInstance gameInstance : this.getGameInstanceManager().getGameInstances()) {
                    getSignManager().removeSign(gameInstance);
                }
            }
        }
        this.getGameInstanceManager().getGameInstances().clear();
        for(String ID:this.getConfig().getConfigurationSection("instances").getKeys(false)){
            BuildInstance earthMasterInstance;
            String s = "instances." + ID + ".";
            if(s.contains("default"))
                continue;


            earthMasterInstance = new BuildInstance(ID );


            if(getConfig().contains(s + "minimumplayers"))
                earthMasterInstance.setMIN_PLAYERS(getConfig().getInt(s +"minimumplayers"));
            else
                earthMasterInstance.setMIN_PLAYERS(getConfig().getInt("instances.default.minimumplayers"));
            if(getConfig().contains(s + "maximumplayers"))
                earthMasterInstance.setMAX_PLAYERS(getConfig().getInt(s + "maximumplayers"));
            else
                earthMasterInstance.setMAX_PLAYERS(getConfig().getInt("instances.default.maximumplayers"));
            if(getConfig().contains(s + "mapname"))
                earthMasterInstance.setMapName(getConfig().getString(s + "mapname"));
            else
                earthMasterInstance.setMapName(getConfig().getString("instances.default.mapname"));
            if(getConfig().contains(s + "lobbylocation"))
                earthMasterInstance.setLobbyLocation(getLocation(s + "lobbylocation"));
            if(getConfig().contains(s + "Startlocation"))
                earthMasterInstance.setStartLocation(getLocation(s + "Startlocation"));
            else{
                System.out.print(ID + " doesn't contains an start location!");
                this.getGameInstanceManager().registerGameInstance(earthMasterInstance);
                continue;
            }
            if(getConfig().contains(s + "Endlocation"))
                earthMasterInstance.setEndLocation(getLocation(s + "Endlocation"));
            else{
                if(!this.isBungeeActivated()) {
                    System.out.print(ID + " doesn't contains an end location!");
                    this.getGameInstanceManager().registerGameInstance(earthMasterInstance);
                    continue;
                }
            }
            if(getConfig().contains(s+"plots")) {
                for (String plotname : getConfig().getConfigurationSection(s + "plots").getKeys(false)) {
                    BuildPlot buildPlot = new BuildPlot();
                    buildPlot.setMAXPOINT(getLocation(s+"plots." + plotname + ".maxpoint"));
                    buildPlot.setMINPOINT(getLocation(s + "plots." + plotname + ".minpoint"));
                    buildPlot.reset();
                    earthMasterInstance.getPlotManager().addBuildPlot(buildPlot);
                }

            }else{
                System.out.print("Instance doesn't contains plots!");
            }

            this.getGameInstanceManager().registerGameInstance(earthMasterInstance);
            earthMasterInstance.start();


        }
    }


    public void loadStatsForPlayersOnline(){
        for(final Player player:getServer().getOnlinePlayers()){
            if(this.isBungeeActivated())
                this.getGameInstanceManager().getGameInstances().get(0).teleportToLobby(player);
            if(!this.isDatabaseActivated()){
                List<String> temp = new ArrayList<String>();
                temp.add("gamesplayed");
                temp.add("wins");
                temp.add("loses");
                temp.add("highestwin");
                temp.add("blocksplaced");
                temp.add("blocksbroken");
                temp.add("particles");
                for(String s:temp) {
                    this.getFileStats().loadStat(player, s);
                }
                return;
            }
            User user = UserManager.getUser(player.getUniqueId());

/*        if (plugin.getMyDatabase().getSingle(new BasicDBObject().append("UUID", event.getPlayer().getUniqueId().toString())) == null) {
            plugin.getMyDatabase().insertDocument(new String[]{"UUID", "gamesplayed", "kills", "deaths", "highestwave", "exp", "level", "orbs"},
                    new Object[]{event.getPlayer().getUniqueId().toString(), 0, 0, 0, 0, 0, 0, 0});
        }

        List<String> temp = new ArrayList<String>();
        temp.add("gamesplayed");
        temp.add("kills");
        temp.add("deaths");
        temp.add("highestwave");
        temp.add("exp");
        temp.add("level");
        temp.add("orbs");
        for (String s : temp) {
            user.setInt(s, (Integer) plugin.getMyDatabase().getSingle(new BasicDBObject("UUID", event.getPlayer().getUniqueId().toString())).get(s));
        } */

            Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {


                final String playername = player.getUniqueId().toString();
                @Override
                public void run() {
                    boolean b = false;
                    MySQLDatabase database = getMySQLDatabase();
                    ResultSet resultSet = database.executeQuery("SELECT UUID from buildbattlestats WHERE UUID='"+playername+"'");
                    try {
                        if(!resultSet.next()) {
                            database.insertPlayer(playername);
                            b = true;
                        }

                        int gamesplayed = 0;
                        int wins = 0;
                        int highestwin = 0;
                        int loses = 0;
                        int blocksPlaced = 0;
                        int blocksBroken = 0;
                        int particles = 0;
                        gamesplayed =    database.getStat(player.getUniqueId().toString(), "gamesplayed");
                        wins = database.getStat(player.getUniqueId().toString(), "wins");
                        loses = database.getStat(player.getUniqueId().toString(), "loses");
                        highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
                        blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
                        blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
                        particles = database.getStat(player.getUniqueId().toString(),"particles");
                        User user = UserManager.getUser(player.getUniqueId());

                        user.setInt("gamesplayed", gamesplayed);
                        user.setInt("wins", wins);
                        user.setInt("highestwin", highestwin);
                        user.setInt("loses", loses);
                        user.setInt("blocksplaced", blocksPlaced);
                        user.setInt("blocksbroken", blocksBroken);
                        user.setInt("particles",particles);
                        b = true;
                    } catch (SQLException e1) {
                        System.out.print("CONNECTION FAILED FOR PLAYER " + player.getName());
                        //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    if(b=false){
                        try {
                            if(!resultSet.next()) {
                                database.insertPlayer(playername);
                                b = true;
                            }
                            int gamesplayed = 0;
                            int wins = 0;
                            int highestwin = 0;
                            int loses = 0;
                            int blocksPlaced = 0;
                            int blocksBroken = 0;
                            int particles = 0;
                            gamesplayed =    database.getStat(player.getUniqueId().toString(), "gamesplayed");
                            wins = database.getStat(player.getUniqueId().toString(), "wins");
                            loses = database.getStat(player.getUniqueId().toString(), "loses");
                            highestwin = database.getStat(player.getUniqueId().toString(), "highestwin");
                            blocksPlaced = database.getStat(player.getUniqueId().toString(), "blocksplaced");
                            blocksBroken = database.getStat(player.getUniqueId().toString(), "blocksbroken");
                            particles = database.getStat(player.getUniqueId().toString(),"particles");
                            User user = UserManager.getUser(player.getUniqueId());

                            user.setInt("gamesplayed", gamesplayed);
                            user.setInt("wins", wins);
                            user.setInt("highestwin", highestwin);
                            user.setInt("loses", loses);
                            user.setInt("blocksplaced", blocksPlaced);
                            user.setInt("blocksbroken", blocksBroken);
                            user.setInt("particles",particles);
                            b = true;
                        } catch (SQLException e1) {
                            System.out.print("CONNECTION FAILED TWICE FOR PLAYER " + player.getName());
                            //e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                    }
                }
            });
        }
    }
}
