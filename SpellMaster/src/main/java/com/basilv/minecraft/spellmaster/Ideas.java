package com.basilv.minecraft.spellmaster;


public class Ideas {

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

	public void teleportingArrow() {
//		bukkit.on("entity.ProjectileHitEvent", function(listener, event){
//		    var projectile = event.entity;
//		    var world = projectile.world;
//		    if (projectile instanceof Arrow && projectile.shooter instanceof Player){
//		        projectile.remove();
//		        projectile.shooter.teleport(projectile.location,
//		                                    PlayerTeleportEvent.TeleportCause.ENDER_PEARL);
//		    }
//		});
		
	}
	
	public void npcWithQuest() {
//		https://dullahansoftware.wordpress.com/2013/02/11/scriptcrafting-a-quest-in-minecraft/
//		spawn:function(){
//		  var player = getPlayerObject();
//		  var world  = player.getWorld();
//				
//		  // create a villager
//		  var quest_npc = world.spawnCreature(player.getLocation().add(1,0,0), org.bukkit.entity.EntityType.VILLAGER);
//		  quest_npc.setProfession(org.bukkit.entity.Villager.Profession.PRIEST);
//		  quest.store.npcs[quest_npc.getUniqueId()] = quest_npc;
//				
//		  // listen for interaction events	
//		  events.on("player.PlayerInteractEntityEvent",this.proc_quest);
//		}		
//		
//		initialize_quest:function(player_name){
//			  quest.store.players[player_name] = {
//			    current:-1,
//			    accepted:false,
//			    skeleton_counter:0,
//			    steps:[{
//			      step:1,
//			      text:"Hello, " + player_name + ". I have an unpleasant task if you desire some work."
//			    },
//			    {
//			      step:2,
//			      text:"You see a gang of skeletons have grown bold and are attacking travelers in the area. If you thinned their numbers, the area would again be safe."
//			    },
//			    {
//			      step:3,
//			      text:"Kill 10 skeletons and return to me for your reward. \nType /jsp accept to accept: SKELETON MENACE."
//			    },
//			    {
//			      step:4,
//			      text:"Thank you " + player_name + ". The roads are now much safer. Here is your reward."
//			    }]};
//			},
//			
//			proc_quest:function(event,data){
//				  // make sure we are interacting with the quest giver
//				  var target = data.getRightClicked();
//				  var player = data.getPlayer();
//				  if(quest.store.npcs[target.getUniqueId()] != null){
//				    // get the player's current quest progress
//				    var quest_progress = quest.store.players[player.name];
//									
//				    if(quest_progress == null){
//				      quest.initialize_quest(player.name);
//				    }
//				    if(quest.store.players[player.name].current > 1){
//				      if(quest.store.players[player.name].skeleton_counter >= 10){
//				        var world = player.getWorld();
//				        quest.store.players[player.name].current = (quest.store.players[player.name].current + 1) % 4;
//				        world.spawnEntity(player.getLocation().add(1,0,1), org.bukkit.entity.EntityType.EXPERIENCE_ORB).setExperience(20);
//				        world.spawnEntity(player.getLocation().add(2,0,1), org.bukkit.entity.EntityType.EXPERIENCE_ORB).setExperience(10);
//				        world.spawnEntity(player.getLocation().add(4,0,1), org.bukkit.entity.EntityType.EXPERIENCE_ORB).setExperience(5);
//				        quest.store.players[player.name].skeleton_counter = 0;
//				        quest.store.players[player.name].accepted = false;
//				        quest.store.players[player.name].current = 3;
//				      }
//				    }
//				    else{
//				      quest.store.players[player.name].current = (quest.store.players[player.name].current + 1) % 4;
//				    }
//				    player.sendMessage(quest.store.players[player.name].steps[quest.store.players[player.name].current].text);
//				  }
//				},
//				
//				skeleton_kill_counter:function(event,data){
//					  var target = data.getEntity();
//					  var killer = target.getKiller();
//					  // check if it is the player who did the killing and if a skeleton was the target
//					  if(killer != null && killer.getUniqueId() == getPlayerObject().getUniqueId() && target.getType() == org.bukkit.entity.EntityType.SKELETON && quest.store.players[killer.name] != null){
//					    quest.store.players[killer.name].skeleton_counter = (quest.store.players[killer.name].skeleton_counter + 1);
//					    if(quest.store.players[killer.name].skeleton_counter < 10){
//					      killer.sendMessage("" + quest.store.players[killer.name].skeleton_counter + "/10 skeletons killed.");
//					    }
//					    else{
//					      killer.sendMessage("Return to the priest to recieve your reward.");
//					    }
//					  }
//					},
//					accept_quest:function(){
//					  var player = getPlayerObject();
//					  if(quest.store.players[player.name] != null){
//					    quest.store.players[player.name].accepted = true;
//					    // add skeleton killing watch
//					    events.on("entity.EntityDeathEvent",this.skeleton_kill_counter);
//					    player.sendMessage("\nYou accepted the quest: SKELETON MENACE.\n");
//					  }
//					},				
					
	}
	
	public void petWithInventory() {
//		https://dullahansoftware.wordpress.com/2013/01/26/scripting-a-simple-minecraft-wolfbot/
		// place wolf two squares in front of you
//				var my_bot = world.spawnCreature(player.getLocation().add(0,0,2), org.bukkit.entity.EntityType.WOLF);
//				
//				my_bot.setTamed(true);
//				my_bot.setOwner(player);
//				my_bot.setTarget(player);
//				
//				var inventory; 
//				var b = this.get_bot(player);
//				if(b == null || b.inventory == null)
//					inventory = player.getServer().createInventory(player, 18, "Bot's Pack");
//				else
//					inventory = b.inventory;
//				
//				this.set_bot(player,my_bot,inventory);
//			},
//		  /*
//		     dismisses your bot
//		  */
//		  dismiss: function(){
//				var b = this.get_bot(getPlayerObject());
//				if( b != null && b.bot !== null){
//					b.bot.remove();
//					b.bot = null;
//				}
//			},
//			/*
//			 instructs your bot to stay
//			*/
//			stay: function(){
//				var b = this.get_bot(getPlayerObject());
//				if(b!= null && b.bot !== null){
//					b.bot.setTarget(null);
//					b.bot.setSitting(true);
//				}
//			},
//			/*
//			 instructs your bot to follow you
//			*/
//			come: function(){
//				var b = this.get_bot(getPlayerObject());
//				if(b!= null){
//					b.bot.setSitting(false);
//					b.bot.setTarget(getPlayerObject());
//				}
//			},
//			/*
//			 instructs your bot to display its pack
//			*/
//			pack: function(){
//				var b = this.get_bot(getPlayerObject());
//				if(b!= null && b.bot !== null){
//					b.bot.setSitting(true);
//					getPlayerObject().openInventory(b.inventory);
//				}
//			}
	}
	
	public void explodingArrow() {
//		events.projectileHit( function(event){
//		    var projectile = event.projectile;
//		    var world = projectile.world;
//		    if (projectile instanceof Arrow && projectile.shooter instanceof Player){
//		        projectile.remove();
//		        world.createExplosion(projectile.location,2.5);
//		    }
//		});
	}
	
	public void firework() {
//	 Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
//     FireworkMeta fwm = fw.getFireworkMeta();
//    
//     //Our random generator
//     Random r = new Random();  
//
//     //Get the type
//     int rt = r.nextInt(4) + 1;
//     Type type = Type.BALL;      
//     if (rt == 1) type = Type.BALL;
//     if (rt == 2) type = Type.BALL_LARGE;
//     if (rt == 3) type = Type.BURST;
//     if (rt == 4) type = Type.CREEPER;
//     if (rt == 5) type = Type.STAR;
//    
//     //Get our random colours  
//     int r1i = r.nextInt(17) + 1;
//     int r2i = r.nextInt(17) + 1;
//     Color c1 = getColor(r1i);
//     Color c2 = getColor(r2i);
//    
//     //Create our effect with this
//     FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
//    
//     //Then apply the effect to the meta
//     fwm.addEffect(effect);
//    
//     //Generate some random power and set it
//     int rp = r.nextInt(2) + 1;
//     fwm.setPower(rp);
//    
//     //Then apply this to our rocket
//     fw.setFireworkMeta(fwm);
	}
	
	public void fling() {
//	    double pitch = (player.getPitch() + 90.0F) * Math.PI / 180.0D;
//	    double rot = (player.getRotation() + 90.0F) * Math.PI / 180.0D;
//	    double x = Math.sin(pitch) * Math.cos(rot);
//	    double z = Math.sin(pitch) * Math.sin(rot);
//	    double y = Math.cos(pitch);
//	 
//	    entity.moveEntity(x * factor, y + 0.5, z * factor);
		
	}
	
}
