package me.tomthedeveloper.buildbattle.instance;

import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.MessageHandler;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.User;
import me.TomTheDeveloper.Utils.Util;
import me.tomthedeveloper.buildbattle.*;
import me.tomthedeveloper.buildbattle.menu.IngameMenu;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.inventivetalent.bossbar.BossBarAPI;

import java.util.*;

/**
 * Created by Tom on 17/08/2015.
 */
public class BuildInstance extends GameInstance {


    private String theme;
    private PlotManager plotManager;
    private boolean receivedVoteItems;
    private Queue<UUID> queue = new LinkedList<UUID>();
    HashMap<Integer, UUID> toplist = new HashMap<Integer, UUID>();
    private static List<String> themes = new ArrayList<String>();
    private static List<Integer> blacklist = new ArrayList<Integer>();
    private Random random = new Random();
    private int extracounter;
    private BuildPlot votingPlot = null;
    private boolean votetime;

    public BuildInstance(String ID) {
        super(ID);
        plotManager = new PlotManager(this);
    }

    public boolean isVoting() {
        return votetime;
    }

    public void setVoting(boolean voting) {
        votetime = voting;
    }


    public PlotManager getPlotManager() {
        return plotManager;
    }

    public void setPlotManager(PlotManager plotManager) {
        this.plotManager = plotManager;
    }

    @Override
    public boolean needsPlayers() {
        if (!ConfigPreferences.isDynamicSignSystemEnabled()) {
            return true;
        } else {
            if (((getGameState() == GameState.STARTING && getTimer() >= 15) || getGameState() == GameState.WAITING_FOR_PLAYERS)) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void setRandomTheme() {
        setTheme(themes.get(random.nextInt(themes.size() - 1)));
    }

    public static void addTheme(String string) {
        themes.add(string);
    }


    @Override
    public void leaveAttempt(Player p) {
        if (queue.contains(p))
            queue.remove(p);
        User user = UserManager.getUser(p.getUniqueId());
        if (getGameState() == GameState.INGAME || getGameState() == GameState.ENDING)
            UserManager.getUser(p.getUniqueId()).addInt("gamesplayed", 1);
        this.teleportToEndLocation(p);
        this.removePlayer(p);
        if (!user.isSpectator()) {
            getChatManager().broadcastLeaveMessage(p);
        }
        user.setFakeDead(false);
        user.setAllowDoubleJump(false);
        user.setSpectator(false);
        user.removeScoreboard();
        // if(plugin.isBarEnabled())
        //BossbarAPI.removeBar(p);

        p.setMaxHealth(20.0);
        p.setFoodLevel(20);
        p.setFlying(false);
        p.setAllowFlight(false);
        p.getInventory().clear();
        for (PotionEffect effect : p.getActivePotionEffects()) {
            p.removePotionEffect(effect.getType());
        }
        p.setFireTicks(0);
        if (getPlayers().size() == 0) {
            this.setGameState(GameState.RESTARTING);
        }
       /* if(!plugin.isBungeeActivated()) {
            plugin.getInventoryManager().loadInventory(p);

        } */
        if (plugin.isInventoryManagerEnabled()) {
            plugin.getInventoryManager().loadInventory(p);
        }
        p.setGameMode(GameMode.SURVIVAL);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!getPlayers().contains(player)) {
                p.showPlayer(player);
                player.showPlayer(p);
            }
        }


    }


    @Override
    public void run() {
        updateScoreboard();
        if (ConfigPreferences.isBarEnabled()) {


            updateBar();
        }
        switch (getGameState()) {

            case WAITING_FOR_PLAYERS:
                if (getPlayers().size() < getMIN_PLAYERS()) {

                    if (getTimer() <= 0) {
                        setTimer(60);
                        getChatManager().broadcastMessage("Waiting-For-Players-Message");
                        return;
                    }
                } else {
                    getChatManager().broadcastMessage("Enough-Players-To-Start", "We now have enough players. The game is starting soon!");
                    setGameState(GameState.STARTING);

                    setTimer(60);
                    this.showPlayers();

                }
                setTimer(getTimer() - 1);
                break;

            case STARTING:
                if (getTimer() == 0) {
                    extracounter = 0;
                    setGameState(GameState.INGAME);
                    setTimer(ConfigPreferences.getBuildTime());
                    for (Player player : getPlayers()) {
                        player.getInventory().clear();
                        player.setGameMode(GameMode.CREATIVE);
                        hidePlayersOutsideTheGame(player);
                        player.getInventory().setItem(8, IngameMenu.getMenuItem());
                    }
                    setRandomTheme();
                    getPlotManager().distributePlots();
                    getPlotManager().teleportToPlots();
                    getChatManager().broadcastMessage("The-Game-Has-Started", "The game has started! Start building guys!!");


                }
                setTimer(getTimer() - 1);

                break;
            case INGAME:
                if (getPlayers().size() <= 1) {
                    getChatManager().broadcastMessage("Only-Player-Left", ChatColor.RED + "U are the only player left. U will be teleported to the lobby");
                    setGameState(GameState.ENDING);
                    setTimer(10);
                }
                if ((getTimer() == (4 * 60)
                        || getTimer() == (3 * 60)
                        || getTimer() == 5 * 60
                        || getTimer() == 30
                        || getTimer() == 2 * 60
                        || getTimer() == 60
                        || getTimer() == 15) && !this.isVoting()) {
                    getChatManager().broadcastMessage("Time-Left-To-Build", ChatManager.PREFIX + "%FORMATTEDTIME% " + ChatManager.NORMAL + "time left to build!", getTimer());
                }
                if (getTimer() != 0 && !receivedVoteItems) {
                    if (extracounter == 1) {
                        extracounter = 0;
                        for (Player player : getPlayers()) {
                            User user = UserManager.getUser(player.getUniqueId());
                            BuildPlot buildPlot = (BuildPlot) user.getObject("plot");
                            if (buildPlot != null) {
                                if (!buildPlot.isInFlyRange(player)) {
                                    player.teleport(buildPlot.getTeleportLocation());
                                    player.sendMessage(ChatManager.getSingleMessage("Cant-Fly-Out-Of-Plot", ChatColor.RED + "U can't fly so far out!"));
                                }
                            }
                        }
                    }
                    extracounter++;
               /* }else{
                    if(extracounter == 1){
                        extracounter = 0;
                        for(Player player:getPlayers()){
                            BuildPlot buildPlot = getVotingPlot();
                            if(buildPlot != null) {
                                if (!buildPlot.isInFlyRange(player)) {
                                    player.teleport(buildPlot.getTeleportLocation());
                                    player.sendMessage(ChatManager.getSingleMessage("Cant-Fly-Out-Of-Plot", ChatColor.RED + "U can't fly so far out!"));
                                }
                            }
                        }
                    }
                    extracounter++; */
                }
                if (getTimer() == 0 && !receivedVoteItems) {

                    for (Player player : getPlayers()) {
                        queue.add(player.getUniqueId());
                    }
                    for (Player player : getPlayers()) {
                        player.getInventory().clear();
                        VoteItems.giveVoteItems(player);
                    }
                    receivedVoteItems = true;
                    setTimer(1);
                } else if (getTimer() == 0 && receivedVoteItems) {
                    setVoting(true);
                    if (!queue.isEmpty()) {
                        if (getVotingPlot() != null) {
                            for (Player player : getPlayers()) {
                                getVotingPlot().setPoints(getVotingPlot().getPoints() + UserManager.getUser(player.getUniqueId()).getInt("points"));
                                UserManager.getUser(player.getUniqueId()).setInt("points", 0);
                            }
                        }
                        voteRoutine();
                    } else {
                        if (getVotingPlot() != null) {
                            for (Player player : getPlayers()) {
                                getVotingPlot().setPoints(getVotingPlot().getPoints() + UserManager.getUser(player.getUniqueId()).getInt("points"));
                                UserManager.getUser(player.getUniqueId()).setInt("points", 0);
                            }
                        }
                        calculateResults();
                        announceResults();
                        BuildPlot winnerPlot = getPlotManager().getPlot(toplist.get(1));

                        for (Player player : getPlayers()) {
                            player.teleport(winnerPlot.getTeleportLocation());
                        }
                        this.setGameState(GameState.ENDING);

                        setTimer(10);
                    }


                }
                setTimer(getTimer() - 1);
                break;
            case ENDING:
                setVoting(false);
                setTimer(getTimer() - 1);
                if (getTimer() == 0) {

                    teleportAllToEndLocation();
                    setGameState(GameState.RESTARTING);
                    for (Player player : getPlayers()) {
                        player.getInventory().clear();
                        UserManager.getUser(player.getUniqueId()).removeScoreboard();
                        player.setFlying(false);
                        player.setAllowFlight(false);
                        UserManager.getUser(player.getUniqueId()).addInt("gamesplayed", 1);

                    }

                    clearPlayers();
                    if (plugin.isBungeeActivated()) {
                        for (Player player : plugin.getServer().getOnlinePlayers()) {
                            this.addPlayer(player);
                        }
                    }
                }
                break;
            case RESTARTING:
                setTimer(14);
                getPlotManager().resetPlots();
                setVoting(false);
                receivedVoteItems = false;
                if (ConfigPreferences.isDynamicSignSystemEnabled()) {
                    plugin.getSignManager().addToQueue(this);
                }
                if (plugin.isBungeeActivated() && ConfigPreferences.getBungeeShutdown()) {
                    plugin.getServer().shutdown();
                }
                setGameState(GameState.WAITING_FOR_PLAYERS);
        }
    }

    public void hidePlayersOutsideTheGame(Player player) {
        for (Player players : plugin.getServer().getOnlinePlayers()) {
            if (getPlayers().contains(players))
                continue;
            player.hidePlayer(players);
            players.hidePlayer(player);
        }
    }

    public void updateBar() {
        for (Player player : getPlayers()) {
            BossBarAPI.removeBar(player);
            switch (getGameState()) {
                case WAITING_FOR_PLAYERS:
                    BossBarAPI.setMessage(player, ChatManager.getSingleMessage("Waiting-For-Players-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " +
                            ChatManager.HIGHLIGHTED + "TomTheDeveloper"));
                    break;
                case STARTING:
                    BossBarAPI.setMessage(player, ChatManager.getSingleMessage("Starting-Bar-Message", ChatManager.PREFIX + "BuildBattle made by " +
                            ChatManager.HIGHLIGHTED + "TomTheDeveloper"));
                    break;
                case INGAME:
                    if (!isVoting()) {
                        BossBarAPI.setMessage(player, ChatFormatter.formatMessage(ChatManager.getSingleMessage("Time-Left-Bar-Message", ChatFormatter.formatMessage(ChatManager.PREFIX + "Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%")), getTimer()));
                    } else {
                        BossBarAPI.setMessage(player, ChatFormatter.formatMessage(ChatManager.getSingleMessage("Vote-Time-Left-Bar-Message", ChatManager.PREFIX + "Vote Time left :" + ChatManager.HIGHLIGHTED + " %FORMATTEDTIME%"), getTimer()));

                    }
                    break;
            }


        }
    }


    public void start() {
        this.runTaskTimer(plugin, 20L, 20L);
        System.out.print(getID() + " STARTED!");
        plugin.getSignManager().addToQueue(this);
    }


    public void updateScoreboard() {
        if (getPlayers().size() == 0)
            return;
        for (Player p : getPlayers()) {

            User user = UserManager.getUser(p.getUniqueId());

            user.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            if (user.getScoreboard().getObjective("waiting") == null) {
                user.getScoreboard().registerNewObjective("waiting", "dummy");
                user.getScoreboard().registerNewObjective("starting", "dummy");
                user.getScoreboard().registerNewObjective("ingame", "dummy");

            }
            switch (getGameState()) {
                case WAITING_FOR_PLAYERS:
                    Objective waitingobj = user.getScoreboard().getObjective("waiting");
                    waitingobj.setDisplayName(getChatManager().getMessage("Scoreboard-Header", ChatManager.PREFIX + "BuildBattle"));
                    waitingobj.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score playerscore1 = waitingobj.getScore(getChatManager().getMessage("Scoreboard-Players", ChatManager.NORMAL + "Players: "));
                    playerscore1.setScore(getPlayers().size());
                    Score minplayerscore1 = waitingobj.getScore(getChatManager().getMessage("Scoreboard-MinPlayers-Message", ChatManager.NORMAL + "Min Players: "));
                    minplayerscore1.setScore(getMIN_PLAYERS());

                    break;
                case STARTING:
                    Objective startingobj = user.getScoreboard().getObjective("starting");
                    startingobj.setDisplayName(getChatManager().getMessage("Scoreboard-Header", ChatManager.PREFIX + "Build Battle"));
                    startingobj.setDisplaySlot(DisplaySlot.SIDEBAR);

                    Score timerscore = startingobj.getScore(getChatManager().getMessage("Scoreboard-Starting-In", ChatManager.NORMAL + "Starting in: "));
                    timerscore.setScore(getTimer());

                    Score playerscore = startingobj.getScore(getChatManager().getMessage("Scoreboard-Players", ChatManager.NORMAL + "Players: "));
                    playerscore.setScore(getPlayers().size());
                    Score minplayerscore = startingobj.getScore(getChatManager().getMessage("Scoreboard-MinPlayers-Message", ChatManager.NORMAL + "Min Players: "));
                    minplayerscore.setScore(getMIN_PLAYERS());

                    break;
                case INGAME:
                    user.getScoreboard().getObjective("ingame").unregister();
                    user.getScoreboard().registerNewObjective("ingame", "dummy");
                    Objective ingameobj = user.getScoreboard().getObjective("ingame");
                    ingameobj.setDisplayName(getChatManager().getMessage("Scoreboard-Header", ChatManager.PREFIX + "Freeze Tag"));
                    ingameobj.setDisplaySlot(DisplaySlot.SIDEBAR);
                    Score timeleft = ingameobj.getScore(getChatManager().getMessage("SCOREBOARD-Time-Left", ChatColor.RED + "" + ChatColor.BOLD + "Time Left: "));
                    timeleft.setScore(9);
                    Score timeleftscore = ingameobj.getScore(ChatColor.WHITE + getFormattedTimeLeft());
                    timeleftscore.setScore(8);
                    Score empty = ingameobj.getScore(" ");
                    empty.setScore(6);
                    Score theme = ingameobj.getScore(getChatManager().getMessage("SCOREBOARD-Theme", ChatColor.GREEN + "Theme"));
                    theme.setScore(5);
                    Score themescore = ingameobj.getScore(ChatColor.WHITE + getTheme());
                    themescore.setScore(4);


                    break;

                case ENDING:
                    break;
                case RESTARTING:

                    break;
                default:
                    setGameState(GameState.WAITING_FOR_PLAYERS);
            }
            user.setScoreboard(user.getScoreboard());


        }


    }

    public static void addToBlackList(int ID) {
        blacklist.add(ID);
    }

    public List<Integer> getBlacklist() {
        return blacklist;
    }

    @Override
    public void joinAttempt(Player p) {
        if ((getGameState() == GameState.INGAME || getGameState() == GameState.ENDING || getGameState() == GameState.RESTARTING)) {
            return;


        }
        if (plugin.isInventoryManagerEnabled()) {
            plugin.getInventoryManager().saveInventoryToFile(p);
        }
        teleportToLobby(p);
        this.addPlayer(p);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
        p.getInventory().clear();
        p.setGameMode(GameMode.CREATIVE);
        showPlayers();
        if (!UserManager.getUser(p.getUniqueId()).isSpectator())
            getChatManager().broadcastJoinMessage(p);
        if (plugin.areKitsEnabled())
            plugin.getKitMenuHandler().giveKitMenuItem(p);
        p.updateInventory();
        for (Player player : getPlayers()) {
            showPlayer(player);
        }
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!getPlayers().contains(player)) {
                p.hidePlayer(player);
                player.hidePlayer(p);
            }
        }
    }


    public long getTimeleft() {
        return getTimer();
    }

    public void setTimeleft(int timeleft) {
        setTimer(timeleft);
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    private String getFormattedTimeLeft() {
        return Util.formatIntoMMSS(getTimer());
    }

    private String getTheme() {
        return theme;
    }


    public void voteRoutine() {
        if (!queue.isEmpty()) {
            setTimer(ConfigPreferences.getVotingTime());
            setTimer(ConfigPreferences.getVotingTime());
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(queue.poll());
            //while(player.isOnline() && !queue.isEmpty()){
            //   player = plugin.getServer().getPlayer(queue.poll());

            //}

            while (getPlotManager().getPlot(player.getUniqueId()) == null && !queue.isEmpty()) {
                System.out.print("A PLAYER HAS NO PLOT!");
                player = plugin.getServer().getPlayer(queue.poll());
            }
            if (queue.isEmpty() && getPlotManager().getPlot(player.getUniqueId()) == null) {
                setVotingPlot(null);
            } else {
                getPlotManager().teleportAllToPlot(plotManager.getPlot(player.getUniqueId()));
                setVotingPlot(plotManager.getPlot(player.getUniqueId()));
                for (Player player1 : getPlayers()) {
                    player1.teleport(getVotingPlot().getTeleportLocation());
                }
                if (plugin.is1_8_R3()) {
                    for (Player player1 : getPlayers())
                        MessageHandler.sendTitleMessage(player1, getChatManager().getMessage("Plot-Owner-Title-Message", ChatManager.PREFIX + "Plot Owner: " + ChatManager.HIGHLIGHTED + "%PLAYER%", player));
                }
                getChatManager().broadcastMessage("Voting-For-Player-Plot", ChatManager.NORMAL + "Voting for " + ChatManager.HIGHLIGHTED + "%PLAYER%" + ChatManager.NORMAL + "'s plot!", player);
            }
        }

    }

    public void setVotingPlot(BuildPlot buildPlot) {
        votingPlot = buildPlot;
    }

    public BuildPlot getVotingPlot() {
        return votingPlot;
    }

    public void announceResults() {
        if (plugin.is1_8_R3()) {
            for (Player player : getPlayers()) {
                MessageHandler.sendTitleMessage(player, getChatManager().getMessage("Title-Winner-Message", ChatColor.YELLOW + "WINNER: " + ChatColor.GREEN + "%PLAYER%", plugin.getServer().getOfflinePlayer(toplist.get(1))));
            }
        }
        for (Player player : getPlayers()) {
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Header-Line", ChatColor.GREEN + "=============================="));
            player.sendMessage(ChatManager.getSingleMessage("Empty-Message", " "));
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(toplist.get(1)), getPlotManager().getPlot(toplist.get(1)).getPoints()));
            if (toplist.containsKey(2) && toplist.get(2) != null) {
                if (getPlotManager().getPlot(toplist.get(1)).getPoints() == getPlotManager().getPlot(toplist.get(2)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(toplist.get(2)), getPlotManager().getPlot(toplist.get(2)).getPoints()));
                } else {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Two", ChatColor.YELLOW + "2. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(toplist.get(2)), getPlotManager().getPlot(toplist.get(2)).getPoints()));
                }
            }
            if (toplist.containsKey(3) && toplist.get(3) != null) {
                if (getPlotManager().getPlot(toplist.get(1)).getPoints() == getPlotManager().getPlot(toplist.get(3)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-One", ChatColor.YELLOW + "1. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(toplist.get(3)), getPlotManager().getPlot(toplist.get(3)).getPoints()));
                } else if (getPlotManager().getPlot(toplist.get(2)).getPoints() == getPlotManager().getPlot(toplist.get(3)).getPoints()) {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Two", ChatColor.YELLOW + "2. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(toplist.get(3)), getPlotManager().getPlot(toplist.get(3)).getPoints()));
                } else {
                    player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Number-Three", ChatColor.YELLOW + "3. " + ChatColor.DARK_GREEN + "%PLAYER%" + ChatColor.GREEN + "- %NUMBER%", plugin.getServer().getOfflinePlayer(toplist.get(3)), getPlotManager().getPlot(toplist.get(3)).getPoints()));
                }
            }
            player.sendMessage(ChatManager.getSingleMessage("Empty-Message", " "));
            player.sendMessage(ChatManager.getSingleMessage("Winner-Announcement-Footer-Line", ChatColor.GREEN + "=============================="));
        }
        for (Integer rang : toplist.keySet()) {
            if (toplist.get(rang) != null) {
                if (plugin.getServer().getPlayer(toplist.get(rang)) != null) {
                    plugin.getServer().getPlayer(toplist.get(rang)).sendMessage(ChatManager.getSingleMessage("You-Became-xth",
                            ChatColor.GREEN + "You became " + ChatColor.DARK_GREEN + "%NUMBER%" + ChatColor.GREEN + "th", rang));
                    if (rang == 1) {
                        UserManager.getUser(plugin.getServer().getPlayer(toplist.get(rang)).getUniqueId()).addInt("wins", 1);
                        if (getPlotManager().getPlot(toplist.get(rang)).getPoints() > UserManager.getUser(toplist.get(rang)).getInt("highestwin")) {
                            UserManager.getUser(plugin.getServer().getPlayer(toplist.get(rang)).getUniqueId()).setInt("highestwin", getPlotManager().getPlot(toplist.get(rang)).getPoints());
                        }
                    } else {
                        UserManager.getUser(plugin.getServer().getPlayer(toplist.get(rang)).getUniqueId()).addInt("loses", 1);

                    }

                }
            }

        }

    }

    public void calculateResults() {

        for (int b = 1; b <= 10; b++) {
            toplist.put(b, null);
        }
        for (BuildPlot buildPlot : getPlotManager().getPlots()) {
            long i = buildPlot.getPoints();
            Iterator it = toplist.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Integer rang = (Integer) pair.getKey();
                if ((UUID) toplist.get(rang) == null || getPlotManager().getPlot(toplist.get(rang)) == null) {
                    toplist.put(rang, buildPlot.getOwner());
                    break;
                }
                if (i > getPlotManager().getPlot(toplist.get(rang)).getPoints()) {
                    insertScore(rang, buildPlot.getOwner());
                    break;
                }

            }
        }
    }

    private void insertScore(int rang, UUID uuid) {
        UUID after = toplist.get(rang);
        toplist.put(rang, uuid);
        if (!(rang > 10) && after != null)
            insertScore(rang + 1, after);
    }
}
