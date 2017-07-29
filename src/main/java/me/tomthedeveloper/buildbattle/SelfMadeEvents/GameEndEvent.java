package me.tomthedeveloper.buildbattle.SelfMadeEvents;

import me.tomthedeveloper.buildbattle.instance.BuildInstance;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Tom on 1/11/2015.
 */
public class GameEndEvent extends Event {


    private BuildInstance buildInstance;


    public GameEndEvent(BuildInstance buildInstance){
        this.buildInstance = buildInstance;
    }

    public BuildInstance getBuildInstance(){
        return buildInstance;
    }

    private static final HandlerList handlers = new HandlerList();



    public static HandlerList getHandlerList() {
        return handlers;
    }



    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
