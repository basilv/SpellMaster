package com.basilv.minecraft.spellmaster;

import java.util.ArrayList;
import java.util.List;

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.world.effects.Particle;
import net.canarymod.api.world.effects.SoundEffect;
import net.canarymod.api.world.position.Location;
import net.canarymod.chat.ChatFormat;

import com.basilv.minecraft.spellmaster.util.MinecraftUtils;

/**
 * Class representing the procedure a player must perform to acquire a book of magic.
 * @author Basil
 *
 */
public class Ceremony extends NamedObject {

	/**
	 * Indicates that the implementing class has a corresponding ceremony.
	 *  
	 */
	public static interface CeremonyCapable {
		 
		String getCeremonyName();
		
		/**
		 * Populate information about the ceremony to obtain this book, organized into lines of text.
		 * Each line of text will be wrapped as necessary. 
		 */
		void populateCeremonyInformation(List<String> lines);

		/**
		 * Try to perform the ceremony.
		 * @param context
		 * @return true if the ceremony was performed
		 */
		boolean tryPerformCeremony(MagicContext context);
	}
	
	private CeremonyCapable ceremonyCapable;
	
	public Ceremony(CeremonyCapable ceremonyCapable) {
		super(ceremonyCapable.getCeremonyName());
		this.ceremonyCapable = ceremonyCapable;
	}

	public boolean tryPerformCeremony(MagicContext context) {
		if (!ceremonyCapable.tryPerformCeremony(context)) {
			return false;
		}
		
		createCeremonyVisualAndSoundEffects(context.getPlayer());
		
		return true;
	}

	private void createCeremonyVisualAndSoundEffects(Player player) {
		MinecraftUtils.spawnParticleInFrontOfPlayer(player, Particle.Type.SPELL);

		Location loc = player.getLocation();
		float volume = 1.0f;
		float pitch = 1.0f;
		player.getWorld().playSound(new SoundEffect(SoundEffect.Type.LEVEL_UP, loc.getX(), loc.getY(), loc.getZ(), volume, pitch));
	}
	
	public List<String> createBookPage() {
		List<String> ceremonyPage = new ArrayList<String>();
		ceremonyPage.add(ChatFormat.BOLD + getName() + " Ceremony" + ChatFormat.RESET);
		ceremonyPage.add(""); // Blank line
		ceremonyCapable.populateCeremonyInformation(ceremonyPage);
		return ceremonyPage;
	}
	
}