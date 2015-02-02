package com.basilv.minecraft.spellmaster.util;

import net.canarymod.Canary;
import net.canarymod.tasks.ServerTask;

/**
 * Base implementation of ServerTask that runs once.
 * 
 * @author Basil
 *
 */
public abstract class OneTimeServerTask extends ServerTask {

    public OneTimeServerTask(long durationInTicks) {
		super(Canary.getServer(), durationInTicks); 
	}

    public final void run() {
    	doTask();
        Canary.getServer().removeSynchronousTask(this);
    }
    
    public abstract void doTask();
}