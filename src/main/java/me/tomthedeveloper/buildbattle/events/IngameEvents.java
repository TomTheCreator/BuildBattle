package me.tomthedeveloper.buildbattle.events;

import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.TomTheDeveloper.Handlers.UserManager;
import me.TomTheDeveloper.User;
import me.tomthedeveloper.buildbattle.BuildBattle;
import me.tomthedeveloper.buildbattle.BuildPlot;
import me.tomthedeveloper.buildbattle.ConfigPreferences;
import me.tomthedeveloper.buildbattle.VoteItems;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import me.tomthedeveloper.buildbattle.menu.IngameMenu;
import me.tomthedeveloper.buildbattle.particles.ParticleHandler;
import me.tomthedeveloper.buildbattle.particles.ParticleMenu;
import me.tomthedeveloper.buildbattle.particles.ParticleRemoveMenu;
import me.tomthedeveloper.buildbattle.playerheads.PlayerHeadsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created by Tom on 17/08/2015.
 */
public class IngameEvents implements Listener {


    private BuildBattle plugin;

    public IngameEvents(BuildBattle buildBattle){
        this.plugin = buildBattle;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if((plugin.isBungeeActivated() && plugin.getGameInstanceManager().getGameInstances().get(0).getGameState() == GameState.INGAME)
                ||(plugin.isBungeeActivated() && plugin.getGameInstanceManager().getGameInstances().get(0).getGameState() == GameState.ENDING)
                ||(plugin.isBungeeActivated() && plugin.getGameInstanceManager().getGameInstances().get(0).getGameState() == GameState.RESTARTING))
            event.getPlayer().kickPlayer(ChatManager.getSingleMessage("Kicked-Game-Already-Started", ChatManager.HIGHLIGHTED + "Kicked! Game has already started!"));
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event){
        if((plugin.isBungeeActivated() && plugin.getGameInstanceManager().getGameInstances().get(0).getGameState() == GameState.INGAME)
                ||(plugin.isBungeeActivated() && plugin.getGameInstanceManager().getGameInstances().get(0).getGameState() == GameState.ENDING)
                ||(plugin.isBungeeActivated() && plugin.getGameInstanceManager().getGameInstances().get(0).getGameState() == GameState.RESTARTING)) {
            event.setKickMessage(ChatManager.getSingleMessage("Kicked-Game-Already-Started", ChatManager.HIGHLIGHTED + "Kicked! Game has already started!"));
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        }

    }

    @EventHandler
    public void onVote(PlayerInteractEvent event){
        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if(event.getItem() == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        if(gameInstance.getGameState() != GameState.INGAME)
            return;

        if(!event.getItem().hasItemMeta())
            return;
        if(!event.getItem().getItemMeta().hasDisplayName())
            return;
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        if(!buildInstance.isVoting())
            return;
        if(buildInstance.getVotingPlot().getOwner() == event.getPlayer().getUniqueId()){
            event.getPlayer().sendMessage(ChatManager.getSingleMessage("Cant-Vote-On-Own-Plot", ChatColor.RED + "U can't vote on your own plot!!"));
            return;
        }
        UserManager.getUser(event.getPlayer().getUniqueId()).setInt("points", VoteItems.getPoints(event.getItem()));
        event.getPlayer().sendMessage(ChatManager.getSingleMessage("Voted", ChatColor.GREEN + "Voted succesfully!"));
    }

    @EventHandler
    public void onOpenOptionMenu(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            return;
        if (event.getItem() == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if (gameInstance == null)
            return;
        if (gameInstance.getGameState() != GameState.INGAME)
            return;

        if (!event.getItem().hasItemMeta())
            return;
        if (!event.getItem().getItemMeta().hasDisplayName())
            return;
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        if (buildInstance.isVoting())
            return;
        if(!IngameMenu.getMenuItem().getItemMeta().getDisplayName().equalsIgnoreCase(event.getItem().getItemMeta().getDisplayName()))
            return;
        IngameMenu.openMenu(event.getPlayer(),buildInstance.getPlotManager().getPlot(event.getPlayer()));
    }

    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent event){
        for(GameInstance gameInstance:plugin.getGameInstanceManager().getGameInstances()){
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot:buildInstance.getPlotManager().getPlots()){
                for(Block block:event.getBlocks()){
                    if(!buildPlot.isInPlotRange(block.getLocation(),-1) && buildPlot.isInPlot(event.getBlock().getLocation())){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWaterFlowEvent(BlockFromToEvent event){
        for(GameInstance gameInstance:plugin.getGameInstanceManager().getGameInstances()){
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot:buildInstance.getPlotManager().getPlots()){

                    if(!buildPlot.isInPlot(event.getToBlock().getLocation()) && buildPlot.isInPlot(event.getBlock().getLocation())){
                        event.setCancelled(true);
                    }

            }
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent event){
        for(GameInstance gameInstance:plugin.getGameInstanceManager().getGameInstances()){
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot:buildInstance.getPlotManager().getPlots()){

                if(buildPlot.isInPlotRange(event.getEntity().getLocation(),0)) {
                    event.blockList().clear();
                }else if(buildPlot.isInPlotRange(event.getEntity().getLocation(),5)){
                    event.getEntity().getLocation().getBlock().setType(Material.TNT);
                    event.blockList().clear();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTreeGrow(StructureGrowEvent event){
        GameInstance gameInstance =plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        BuildPlot buildPlot = buildInstance.getPlotManager().getPlot(event.getPlayer());
        if(buildPlot == null)
            return;
        for(BlockState blockState:event.getBlocks()){
            if(!buildPlot.isInPlot(blockState.getLocation()))
                blockState.setType(Material.AIR);
        }

    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event){
        for(GameInstance gameInstance:plugin.getGameInstanceManager().getGameInstances()){
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot:buildInstance.getPlotManager().getPlots()){

                    if(!buildPlot.isInPlotRange(event.getBlock().getLocation(),-1)){
                        event.setCancelled(true);
                    }
                }

        }
    }

    @EventHandler
    public void onFloorChange(InventoryClickEvent event){

        if(event.getCurrentItem() == null)
            return;

        if(!event.getCurrentItem().hasItemMeta())
            return;


        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;

        // if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.getSingleMessage("Ingame-Menu-Name", "Option Menu")))
         //   event.setCancelled(true);








        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player) event.getWhoClicked());
        if(gameInstance == null)
            return;

        BuildInstance buildInstance = (BuildInstance) gameInstance;
        if(buildInstance.getGameState() != GameState.INGAME)
            return;
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.getSingleMessage("Particle-Option-Name", ChatColor.GREEN + "Particles"))) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            ParticleMenu.openMenu((Player)event.getWhoClicked(), buildInstance.getPlotManager().getPlot((Player)event.getWhoClicked()));
            return;
        }

        if(event.getInventory().getName().equalsIgnoreCase(ChatManager.getSingleMessage("Particle-Remove-Menu-Name", "Remove Particles"))) {
            event.setCancelled(true);
            ParticleRemoveMenu.onClick(event.getInventory(), event.getCurrentItem(), buildInstance.getPlotManager().getPlot((Player) event.getWhoClicked()));

            return;
        }
        if(event.getInventory().getName().equalsIgnoreCase(ChatManager.getSingleMessage("Player-Head-Main-Inventory-Name","Player Head Menu"))) {
            event.setCancelled(true);
            PlayerHeadsMenu.onClickInMainMenu((Player)event.getWhoClicked(),event.getCurrentItem());

            return;
        }
        if(PlayerHeadsMenu.getMenuNames().contains(event.getInventory().getName())){
            event.setCancelled(true);
            PlayerHeadsMenu.onClickInDeeperMenu((Player) event.getWhoClicked(),event.getCurrentItem(),event.getInventory().getName());
            return;
        }
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.getSingleMessage("Heads-Option-Name", ChatColor.GREEN + "Particles"))) {

            event.setCancelled(true);
            PlayerHeadsMenu.openMenu((Player) event.getWhoClicked());
        }
        if(event.getInventory().getName().equalsIgnoreCase(ChatManager.getSingleMessage("Particle-Menu-Name", "Particle Menu"))){

            if(event.getCurrentItem().getItemMeta().getDisplayName().contains(ChatManager.getSingleMessage("Remove-Particle-Item-Name" ,ChatColor.RED + "Remove Particles"))) {
                event.setCancelled(true);
                event.getWhoClicked().closeInventory();
                ParticleRemoveMenu.openMenu((Player)event.getWhoClicked(), buildInstance.getPlotManager().getPlot((Player)event.getWhoClicked()));
                return;
            }
            ParticleMenu.onClick((Player) event.getWhoClicked(),event.getCurrentItem(),buildInstance.getPlotManager().getPlot((Player) event.getWhoClicked()));

            event.setCancelled(true);
        }
        if(event.getCursor() == null) {
            event.setCancelled(true);
            return;
        }
        if(!((event.getCursor().getType().isBlock() && event.getCursor().getType().isSolid()) || event.getCursor().getType() == Material.WATER_BUCKET || event.getCursor().getType() == Material.LAVA_BUCKET)) {
            event.setCancelled(true);
            return;
        }

        if(event.getCursor().getType() == null || event.getCursor().getType() == Material.SAPLING
                || event.getCursor().getType() == Material.TRAP_DOOR
                || event.getCursor().getType() == Material.WOOD_DOOR
                || event.getCursor().getType() == Material.IRON_TRAPDOOR
                || event.getCursor().getType() == Material.WOODEN_DOOR
                || event.getCursor().getType() == Material.ACACIA_DOOR
                || event.getCursor().getType() == Material.BIRCH_DOOR
                || event.getCursor().getType() == Material.WOOD_DOOR
                || event.getCursor().getType() == Material.JUNGLE_DOOR
                || event.getCursor().getType() == Material.SPRUCE_DOOR
                || event.getCursor().getType() == Material.IRON_DOOR
                || event.getCursor().getType() == Material.CHEST
                ||event.getCursor().getType() == Material.TRAPPED_CHEST
                ||event.getCursor().getType() == Material.FENCE_GATE
                || event.getCursor().getType() == Material.BED
                ||event.getCursor().getType() == Material.LADDER
                || event.getCursor().getType() == Material.JUNGLE_FENCE_GATE
                || event.getCursor().getType() == Material.JUNGLE_DOOR_ITEM
                || event.getCursor().getType() == Material.SIGN
                || event.getCursor().getType() == Material.SIGN_POST
                || event.getCursor().getType() == Material.WALL_SIGN
                || event.getCursor().getType() == Material.CACTUS
                ||event.getCursor().getType() == Material.TNT || event.getCursor().getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }
        if(event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.getSingleMessage("Floor-Option-Name", ChatColor.GREEN + "Floor Material"))){
            buildInstance.getPlotManager().getPlot((Player)event.getWhoClicked()).changeFloor(event.getCursor().getType(), event.getCursor().getData().getData());
            event.getWhoClicked().sendMessage(ChatManager.getSingleMessage("Floor-Changed", ChatColor.GREEN + "Floor changed!"));
            event.getCursor().setAmount(0);
            event.getCursor().setType(Material.AIR);
            event.getCurrentItem().setType(Material.AIR);
            ((Player)event.getWhoClicked()).closeInventory();
            for(Entity entity:event.getWhoClicked().getNearbyEntities(5,5,5)){
                if(entity.getType() == EntityType.DROPPED_ITEM){
                    entity.remove();
                }
            }
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onChatIngame(AsyncPlayerChatEvent event) {
        if (plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null) {
            for (GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                for (Player player : gameInstance.getPlayers()) {
                    if (event.getRecipients().contains(player))
                        event.getRecipients().remove(player);
                }
            }
            return;
        }

            GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
            event.getRecipients().clear();
            event.getRecipients().addAll(new ArrayList<Player>(gameInstance.getPlayers()));



    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void disableCommands(PlayerCommandPreprocessEvent event){
        if (plugin.getGameInstanceManager().getGameInstance(event.getPlayer()) == null)
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(event.getMessage().contains("leave") || event.getMessage().contains("stats")){
            return;
        }
        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("minigames.edit"))
            return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatManager.getSingleMessage("Only-Command-Ingame-Is-Leave",ChatColor.RED + "You have to leave the game first to perform commands. The only command that works is /leave!"));


    }

    @EventHandler
    public void playerEmtpyBucket(PlayerBucketEmptyEvent event){
        GameInstance gameInstance =plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        BuildPlot buildPlot = buildInstance.getPlotManager().getPlot(event.getPlayer());
        if(buildPlot == null)
            return;

            if(!buildPlot.isInPlot(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation()))
                event.setCancelled(true);

    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event)
    {
        for(GameInstance gameInstance: plugin.getGameInstanceManager().getGameInstances()) {
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            if(buildInstance.getPlotManager().getPlots().get(0) != null) {
                if(buildInstance.getPlotManager().getPlots().get(0).getCenter().getWorld().getName().equalsIgnoreCase(event.getBlock().getWorld().getName())) {
                    if(event.getSource().getType() == Material.FIRE)
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onWItherBos(CreatureSpawnEvent event){
        if(plugin.isBungeeActivated() && event.getEntity().getType() == EntityType.WITHER){
            event.setCancelled(true);
            return;
        }
        if(event.getEntity().getType() == EntityType.WITHER || event.getEntity().getType() == EntityType.CREEPER || ConfigPreferences.isMobSpawningDisabled()) {
            for (GameInstance gameInstance : plugin.getGameInstanceManager().getGameInstances()) {
                BuildInstance buildInstance = (BuildInstance) gameInstance;
                for(BuildPlot buildplot: buildInstance.getPlotManager().getPlots()){
                    if (buildplot.isInPlotRange(event.getEntity().getLocation(),1))
                        event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void LeaveDecay(LeavesDecayEvent event){
        for(GameInstance gameInstance:plugin.getGameInstanceManager().getGameInstances()){
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot:buildInstance.getPlotManager().getPlots()){

                if(buildPlot.isInPlotRange(event.getBlock().getLocation(),5)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        if(event.getItemDrop().getItemStack() == null)
            return;
        ItemStack drop = (ItemStack) event.getItemDrop().getItemStack();
        if(!drop.hasItemMeta())
            return;
        if(!drop.getItemMeta().hasDisplayName())
            return;
        if(drop.getItemMeta().getDisplayName().equals(ChatManager.getSingleMessage("Options-Menu-Item", ChatColor.GREEN + "Options"))
                || VoteItems.getPoints(drop) != 0)
            event.setCancelled(true);
    }


    @EventHandler
    public void onTntExplode(BlockExplodeEvent event){
        for(GameInstance gameInstance:plugin.getGameInstanceManager().getGameInstances()){
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            for(BuildPlot buildPlot:buildInstance.getPlotManager().getPlots()){

                if(buildPlot.isInPlotRange(event.getBlock().getLocation(),0)) {
                    event.blockList().clear();
                }else if(buildPlot.isInPlotRange(event.getBlock().getLocation(),5)){
                    event.getBlock().getLocation().getBlock().setType(Material.TNT);
                    event.blockList().clear();
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
     public void onBreak(BlockBreakEvent event){
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        if(gameInstance.getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        if(buildInstance.getBlacklist().contains(event.getBlock().getTypeId())) {
            event.setCancelled(true);
            return;
        }
        if(buildInstance.isVoting()){
            event.setCancelled(true);
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        BuildPlot buildPlot = (BuildPlot) user.getObject("plot");
        if(buildPlot.isInPlot(event.getBlock().getLocation())) {
            UserManager.getUser(event.getPlayer().getUniqueId()).addInt("blocksbroken",1);

            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance(event.getPlayer());
        if(gameInstance == null)
            return;
        if(gameInstance.getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        if(buildInstance.getBlacklist().contains(event.getBlock().getTypeId())) {
            event.setCancelled(true);
            return;
        }
        if(buildInstance.isVoting()){
            event.setCancelled(true);
        }
        User user = UserManager.getUser(event.getPlayer().getUniqueId());
        BuildPlot buildPlot = (BuildPlot) user.getObject("plot");
        if(buildPlot.isInPlot(event.getBlock().getLocation())){
            UserManager.getUser(event.getPlayer().getUniqueId()).addInt("blocksplaced",1);
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player)event.getWhoClicked());
        if(gameInstance == null)
            return;
        if(gameInstance.getGameState() != GameState.INGAME) {
            event.setCancelled(true);
            return;
        }
        BuildInstance buildInstance = (BuildInstance) gameInstance;
        if(!buildInstance.isVoting()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event){
        if(event.getInventory().getTitle() == null)
            return;
        if(!event.getInventory().getTitle().equalsIgnoreCase(ChatManager.getSingleMessage("Ingame-Menu-Name","Options Menu")))
            return;
        GameInstance gameInstance = plugin.getGameInstanceManager().getGameInstance((Player)event.getWhoClicked());
    }






}
