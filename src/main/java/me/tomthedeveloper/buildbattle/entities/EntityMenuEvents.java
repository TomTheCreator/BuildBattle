package me.tomthedeveloper.buildbattle.entities;

import me.TomTheDeveloper.Game.GameInstance;
import me.TomTheDeveloper.Game.GameState;
import me.TomTheDeveloper.GameAPI;
import me.TomTheDeveloper.Handlers.ChatManager;
import me.tomthedeveloper.buildbattle.BuildBattle;
import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Tom on 1/02/2016.
 */
public class EntityMenuEvents implements Listener {

    private BuildBattle plugin;
    private GameAPI gameAPI;
    private HashMap<UUID, BuildBattleEntity> links = new HashMap<UUID, BuildBattleEntity>();


    public EntityMenuEvents(BuildBattle plugin) {
        this.plugin = plugin;
        this.gameAPI = plugin.getGameAPI();
    }


    @EventHandler
    public void onRightClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(player);
        if (gameInstance == null)
            return;
        event.setCancelled(true);
        if (gameInstance.getGameState() != GameState.INGAME || ((BuildInstance) gameInstance).isVoting())
            return;
        EntityType type = event.getRightClicked().getType();
        if(type == EntityType.ITEM_FRAME || type== EntityType.ARMOR_STAND
                || type == EntityType.DROPPED_ITEM
                || type == EntityType.PRIMED_TNT
                || type == EntityType.FALLING_BLOCK
                || type == EntityType.COMPLEX_PART
                || type == EntityType.ENDER_CRYSTAL
                || type == EntityType.LEASH_HITCH
                || type == EntityType.MINECART
                || type == EntityType.MINECART_CHEST
                || type == EntityType.MINECART_FURNACE
                || type == EntityType.MINECART_COMMAND
                || type == EntityType.MINECART_HOPPER
                || type == EntityType.MINECART_MOB_SPAWNER
                || type == EntityType.MINECART_TNT
                || type == EntityType.PLAYER
                || type == EntityType.PAINTING
                || type == EntityType.WITHER_SKULL)
            return;
        BuildBattleEntity buildBattleEntity = new BuildBattleEntity(event.getRightClicked());
        player.openInventory(buildBattleEntity.getMenu());
        links.put(player.getUniqueId(), buildBattleEntity);

    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event){
        if(event.getDamager().getType() != EntityType.PLAYER)
            return;
        Player player = (Player) event.getDamager();
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(player);
        if (gameInstance == null)
            return;
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(player);
        if (gameInstance == null)
            return;
        if (!event.getInventory().getTitle().equals(ChatManager.getSingleMessage("Entity-Menu", "Entity Menu")))
            return;
        if (event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String key = EntityItemManager.getRelatedEntityItemName(event.getCurrentItem());
        if (key.equalsIgnoreCase("Close")) {
            links.remove(player.getUniqueId());
            player.closeInventory();
            event.setCancelled(true);
        } else if (key.equalsIgnoreCase("Move-On") || key.equalsIgnoreCase("Move-Off")) {
            links.get(player.getUniqueId()).switchMoveeable();
            event.setCancelled(true);
            player.closeInventory();

        } else if (key.equalsIgnoreCase("Adult") || key.equalsIgnoreCase("Baby")) {
            links.get(player.getUniqueId()).switchAge();
            event.setCancelled(true);

        } else if (key.equalsIgnoreCase("Look-At-Me")) {
            links.get(player.getUniqueId()).setLook(player.getLocation());
            event.setCancelled(true);
            player.closeInventory();
        } else if (key.equals("Saddle-On") || key.equals("Saddle-Off")) {
            links.get(player.getUniqueId()).switchSaddle();
            event.setCancelled(true);
            player.closeInventory();
        } else if (key.equals("Despawn")) {
            links.get(player.getUniqueId()).remove();
            player.closeInventory();
            event.setCancelled(true);
            BuildInstance buildInstance = (BuildInstance) gameInstance;
            buildInstance.getPlotManager().getPlot(player).removeEntity();

        }else if(key.equals("Profession-Villager-Selecting")){
            player.openInventory(VillagerProffesionMenu.getMenu(links.get(player.getUniqueId()).getEntity()));
            event.setCancelled(true);
        }
        return;


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVillagerProfessionChoose(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        GameInstance gameInstance = gameAPI.getGameInstanceManager().getGameInstance(player);
        if (gameInstance == null)
            return;
        if (!event.getInventory().getTitle().equals(ChatManager.getSingleMessage("Villager-Profession-Menu", "Choose villager profession")))
            return;
        if (event.getCurrentItem() == null)
            return;
        if(!event.getCurrentItem().hasItemMeta())
            return;
        if(!event.getCurrentItem().getItemMeta().hasDisplayName())
            return;
        String key = EntityItemManager.getRelatedEntityItemName(event.getCurrentItem());
        Villager villager = (Villager) links.get(player.getUniqueId()).getEntity();
        if (key.equalsIgnoreCase("Profession.Butcher")) {
            villager.setProfession(Villager.Profession.BUTCHER);
        }else if(key.equals("Profession.Blacksmith")){
            villager.setProfession(Villager.Profession.BLACKSMITH);
        }else if(key.equals("Profession.Farmer")){
            villager.setProfession(Villager.Profession.FARMER);
        }else if(key.equals("Profession.Priest")){
            villager.setProfession(Villager.Profession.PRIEST);
        }else if(key.equals("Profession.Librarian")){
            villager.setProfession(Villager.Profession.LIBRARIAN);
        }

        event.setCancelled(true);
        player.closeInventory();
    }


}
