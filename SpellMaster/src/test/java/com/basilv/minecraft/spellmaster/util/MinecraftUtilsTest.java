package com.basilv.minecraft.spellmaster.util;

import net.canarymod.api.world.position.Position;

public class MinecraftUtilsTest {

	public static void main(String[] args) {
		Position position = new Position(0, 0, 1);
		if (position.getBlockZ() != 1) {
			throw new RuntimeException("Unexpected value.");
		}
//		List<String> text = Arrays.asList("l1", "l2");
//		List<List<String>> pages = Arrays.asList(text);
 
		// TODO: Test won't work because factory fails with Null Pointer Exception, and this warning is output first:
		// WARN Unable to instantiate org.fusesource.jansi.WindowsAnsiOutputStream
//		MinecraftUtils.createWrittenBookWithContent("foo", pages, text);
	}
}
