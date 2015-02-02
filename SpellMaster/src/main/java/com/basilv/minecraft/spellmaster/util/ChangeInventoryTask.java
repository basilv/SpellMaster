package com.basilv.minecraft.spellmaster.util;


/**
 * Task to update the contents of the player's inventory.
 * This is a workaround since updating directly from an ItemUseHook handler doesn't seem to work correctly.
 * Make sure to call inventory.update() after making the change.
 * 
 * Sample invocation:
        Canary.getServer().addSynchronousTask(new ChangeInventoryTask() {
			@Override public void changeInventory() {
				PlayerInventory inventory = player.getInventory();
				inventory.addItem(item);
				inventory.update();
			}
        });
 * 
 * @author Basil
 *
 */
public abstract class ChangeInventoryTask extends OneTimeServerTask {

    public ChangeInventoryTask() {
		super(0); // Run as soon as possible.
	}

}