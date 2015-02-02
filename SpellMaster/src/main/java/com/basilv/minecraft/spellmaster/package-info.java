/**
 * SpellMaster Plugin for CanaryMod Server
 *
 * Players can obtain spellbooks by performing ceremonies and then cast the corresponding spells
 * Spells are designed to introduce more variety to the game and/or reduce the tedium (e.g. mining), but spells are balanced 
 * by having a variety of costs (e.g. consume inventory slots for spellbooks and spell components, cost health, or cost materials)
 * The power of spells generally increases with the player's level, introducing yet another tradeoff between having more powerful spells
 * and enchanting / repairing via the enchantment table or anvil.
 * The learning of spells is designed to be difficult enough to not make the very early game any easier: for example, the spell to help with mining
 * cannot be learned until the player has already done some amount of mining.
 * 
 * @author Basil
 *
 */
package com.basilv.minecraft.spellmaster;

// TODO: Consider allowing multiple levels of a spell to be learned, increasing its power. E.g. lightning bolt I versus lightning bolt II or III or lightning ball

/* TODO: Other thoughts


Each spell costs material ingredients
- make same ingredient (simpler)
- allow multiple options for ingredients
- tailor ingredients to spell (e.g. flight requires feathers)
- different levels of spells, higher levels require more advanced materials/spell-casting item
- craft spell-casting item
- increase power with better spell-casting item
OR bind particular spell to spell-casting item. (more complex, do later)
OR spells cost exhaustion/hunger/health as well
OR spells require a certain minimum level and get more powerful when current level > min level required 
OR spells cost experience

Spell ideas:
teleport
change weather
shield
heal
flurry of arrows
fireball
entomb self / other
drown
summon x
sense village
sense monster
sense trees
find water
detect gold
water field (water flowing away in all directions)
fast mining
fly (player.getCapabilities.setFlying)
light (from stick)
wall

start game with one cantrip???

 */
