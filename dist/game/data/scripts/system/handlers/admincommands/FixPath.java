/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 *  Aion-Lightning is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Aion-Lightning is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details. *
 *  You should have received a copy of the GNU General Public License
 *  along with Aion-Lightning.
 *  If not, see <http://www.gnu.org/licenses/>.
 */
package system.handlers.admincommands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.GameServer;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.WalkerData;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.templates.walker.RouteStep;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import java.util.concurrent.ScheduledFuture;

/**
 * Handles the admin command to fix pathfinding issues for NPCs.<br>
 * It recalculates and updates the {@link WalkerTemplate} routes in the game world.
 * @author CoolyT
 */
public class FixPath extends AdminCommand
{
	static volatile boolean canceled = false;
	static volatile boolean isRunning = false;
	static Player runner = null;
	static boolean invul = false;
	static boolean log = true;
	
	static ScheduledFuture<?> thread = null;
	
	/**
	 * Initializes a new instance of the {@link FixPath} class.<br>
	 * This constructor registers the command as {@code fixpath}.<br>
	 * It allows administrators to repair NPC paths in the game world.
	 */
	public FixPath()
	{
		super("fixpath");
	}
	
	/**
	 * Executes the command to fix NPC pathfinding.<br>
	 * It allows fixing paths for a specific target, all NPCs, or canceling an active process.
	 * @param admin The {@code Player} who is running the command.
	 * @param params A variable list of strings containing the jump height, target name, or special keywords like "all" and "cancel".
	 */
	@Override
	public void execute(Player admin, String... params)
	{
		if ((params == null) || (params.length < 1))
		{
			PacketSendUtility.sendMessage(admin, "Syntax : //fixpath <jump height> || <all> || <cancel>");
			return;
		}
		
		// String routeId = "";
		
		float jumpHeight = 2F;
		if (((admin.getTarget() == null) || !(admin.getTarget() instanceof Npc)) && (!("cancel".equals(params[0])) && (!("npc".equals(params[0])) && !("all".equals(params[0])))))
		{
			PacketSendUtility.sendMessage(admin, "You need to target an Npc ..");
			return;
		}
		
		try
		{
			if (isRunning && (runner != null) && !admin.equals(runner))
			{
				PacketSendUtility.sendMessage(admin, "Someone is already running this command!");
				return;
			}
			
			if ("cancel".equalsIgnoreCase(params[0]))
			{
				// thread.cancel(true);
				canceled = true;
				PacketSendUtility.sendMessage(admin, "Canceled.");
			}
			else if ("all".equalsIgnoreCase(params[0]))
			{
				final Collection<String> walkerIds = new ArrayList<>();
				final Collection<Npc> npcs = new ArrayList<>();
				for (Npc npc : World.getInstance().getWorldMap(admin.getWorldId()).getWorldMapInstanceById(admin.getInstanceId()).getNpcs())
				{
					if ((npc.getSpawn().getWalkerId() != null) && !npc.getSpawn().getWalkerId().isEmpty() && !walkerIds.contains(npc.getSpawn().getWalkerId()))
					{
						npcs.add(npc);
						walkerIds.add(npc.getSpawn().getWalkerId());
					}
				}
				
				fix(admin, jumpHeight, "");
			}
			else if ("npc".equalsIgnoreCase(params[0]))
			{
				if (params.length < 2)
				{
					onFail(admin, "");
				}
				else if (params.length == 3)
				{
					jumpHeight = Float.parseFloat(params[2].toLowerCase());
				}
				
				fix(admin, jumpHeight, params[1]);
			}
			else if (params.length < 1)
			{
				PacketSendUtility.sendMessage(admin, "Syntax : //fixpath <jump height> || <all> || <cancel>");
				return;
			}
			else
			{
				jumpHeight = Float.parseFloat(params[0]);
				fix(admin, jumpHeight, ((Npc) admin.getTarget()).getName().toLowerCase());
			}
			
			if (!canceled)
			{
				invul = admin.isInvul();
			}
		}
		catch (NumberFormatException e)
		{
			PacketSendUtility.sendMessage(admin, "Only numbers please!!!");
		}
		
	}
	
	/**
	 * Fixes the Z coordinates of NPC walking routes by teleporting an admin through them.<br>
	 * This method updates the {@link WalkerTemplate} data based on the actual terrain height.<br>
	 * It automatically handles jumping and falling logic to ensure correct placement.
	 * @param admin The {@link Player} who will be used as the reference for coordinate fixing.
	 * @param jumpHeight The base vertical distance to add during each teleport step.
	 * @param npcName The name of the specific NPC to fix. If empty, it processes all NPCs with routes.
	 */
	public static void fix(Player admin, float jumpHeight, String npcName)
	{
		isRunning = true;
		runner = admin;
		runner.setPortAnimation(0);
		runner.setInvul(true);
		final long startTime = System.currentTimeMillis();
		thread = ThreadPoolManager.getInstance().schedule((Runnable) () ->
		{
			final Map<Integer, Float> fix = new HashMap<>();
			final TreeMap<String, WalkerTemplate> newTemplates = new TreeMap<>();
			final WalkerData newData = new WalkerData();
			int npcCounter = 1;
			
			// Special Fixes for Some npcs
			fix.put(806143, -8F); // Sanctum, Juina
			final List<Npc> npcs = new ArrayList<>();
			final List<Npc> allNpcs = World.getInstance().getWorldMap(admin.getWorldId()).getWorldMapInstanceById(admin.getInstanceId()).getNpcs();
			final Collection<String> walkerIds = new ArrayList<>();
			
			// Filter out Npcs without WalkingRoute
			for (Npc npc : allNpcs)
			{
				if ((npc.getSpawn().getWalkerId() != null) && !npc.getSpawn().getWalkerId().isEmpty() && !walkerIds.contains(npc.getSpawn().getWalkerId()))
				{
					npcs.add(npc);
					walkerIds.add(npc.getSpawn().getWalkerId());
				}
			}
			
			for (Npc npc : npcs)
			{
				// if is only on Npc skip all others..
				if (!npcName.isEmpty() && !npc.getName().toLowerCase().equals(npcName))
				{
					continue;
				}
				
				if ((npc.getSpawn().getWalkerId() == null) || npc.getSpawn().getWalkerId().isEmpty())
				{
					msg(admin, "Npc " + npc.getName() + " has no Route !");
					continue;
				}
				
				final String routeId = npc.getSpawn().getWalkerId();
				final WalkerTemplate oldTemplate = DataManager.WALKER_DATA.getWalkerTemplate(routeId);
				final WalkerTemplate newTemplate = new WalkerTemplate(routeId);
				
				if (oldTemplate == null)
				{
					msg(admin, "Skipping Invalid route id - Npc: " + npc.getName() + " id: " + npc.getNpcId());
					continue;
				}
				
				float corr = 0F;
				if (fix.containsKey(npc.getNpcId()))
				{
					corr = fix.get(npc.getNpcId());
				}
				
				// get SpawnPosition and teleport to it..
				final WorldPosition walkerSpawnPos = new WorldPosition(npc.getSpawn().getWorldId(), npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ() + corr + 1F, npc.getSpawn().getHeading());
				msg(admin, "You were teleported to " + npc.getName() + "'s Spawnposition and you will be teleported to each step of this route to fix their z coordinates. ");
				TeleportService2.teleportTo(admin, admin.getWorldId(), walkerSpawnPos.getX(), walkerSpawnPos.getY(), walkerSpawnPos.getZ() + 1F);
				
				try
				{
					Thread.sleep(1000);
					final List<RouteStep> steps = new ArrayList<>();
					final boolean reversed = false;
					
					for (RouteStep step : oldTemplate.getRouteSteps())
					{
						steps.add(step);
					}
					
					final int stepSize = steps.size();
					for (RouteStep step : steps)
					{
						boolean jumpHigher = false;
						if (canceled || admin.isInState(CreatureState.DEAD))
						{
							thread.cancel(true);
							return;
						}
						
						if ((step.getX() == 0) || (step.getY() == 0))
						{
							// newTemplate.addRouteStep(step);
							msg(admin, "Skipping zero coordinate...");
							continue;
						}
						
						// calculating the Z difference of last step and this step
						step.setZ(admin.getZ() + (step.getZ() - admin.getZ()));
						
						// check if difference of last step and this step is really high .. because it's possible tehe npc is "flying" on route ..
						final float diff = (step.getZ() - admin.getZ());
						if (diff >= 5)
						{
							// msg(null," ZDifference: "+ (step.getZ() - admin.getZ())+" AdminZ: "+admin.getZ()+" StepZ: "+ step.getZ());
							// jumpHigher = true;
							// msg(admin, "Looks Like there is someone Flying on Route .. Name: "+npc.getName()+" - Route: "+routeId);
							jumpHigher = true;
							step.setZ(admin.getZ());
						}
						else
						{
							jumpHigher = false;
						}
						
						final float jh = jumpHeight + (jumpHigher ? ((step.getRouteStep() > 12) && (step.getRouteStep() < (steps.size() - 10)) && (diff > 3F) ? 6F : 3F) : 0F);
						msg(admin, "Teleporting to " + npc.getName() + "'s Route (" + (npcCounter) + "/" + npcs.size() + ") " + (reversed ? "reversed" : "") + " Step [" + step.getRouteStep() + "/" + stepSize + "] ... jumpHeight:" + jh);
						TeleportService2.teleportTo(admin, admin.getWorldId(), step.getX(), step.getY(), step.getZ() + jh);
						admin.getController().stopProtectionActiveTask();
						
						// Dummy for first use
						float lastAdminZ = admin.getZ() + 3F;
						boolean skip = false;
						
						// boolean sucess = false;
						int retryCount = 1;
						float tempJumpHeight = 2.5F;
						boolean retry = false;
						
						// Falling
						while (lastAdminZ > admin.getZ())
						{
							// msg(admin, "LastZ: "+lastAdminZ + " adminZ: "+admin.getZ());
							
							if (canceled || admin.isInState(CreatureState.DEAD))
							{
								thread.cancel(true);
								return;
							}
							
							retry = false;
							
							// Falling trough ground fix
							while ((retryCount <= 11) && (((lastAdminZ - admin.getZ()) > 15) || (admin.getZ() < (World.getInstance().getWorldMap(admin.getWorldId()).getDeathLevel() + 10))))
							{
								if (retryCount == 11)
								{
									msg(admin, " Skipping step: (" + (npcCounter) + "/" + npcs.size() + ") step [" + step.getRouteStep() + "/" + stepSize + "] - because there is an black hole ?!");
									skip = true;
									break;
								}
								
								msg(admin, "ReTry (" + retryCount + "/10) step: (" + (npcCounter) + "/" + npcs.size() + ") step [" + step.getRouteStep() + "/" + stepSize + "] with new jumpheight: " + tempJumpHeight + " newZ: " + (step.getZ() + tempJumpHeight));
								TeleportService2.teleportTo(admin, admin.getWorldId(), step.getX(), step.getY(), step.getZ() + (tempJumpHeight += 5F));
								lastAdminZ = admin.getZ();
								retryCount++;
								retry = true;
							}
							
							if (skip)
							{
								break;
							}
							
							lastAdminZ = admin.getZ();
							Thread.sleep(1000);
						}
						
						// Set the new Step after Player reached the ground
						if (!retry)
						{
							step.setZ(admin.getZ());
						}
						
						//
					}
					
					final ArrayList<RouteStep> newSteps = new ArrayList<>();
					newSteps.addAll(steps);
					newTemplate.setRouteSteps(newSteps);
				}
				catch (Exception e)
				{
					msg(null, "Exception: " + e.getLocalizedMessage());
				}
				
				npcCounter++;
				
				if (!canceled && !newTemplate.getRouteSteps().isEmpty())
				{
					DataManager.WALKER_DATA.replaceTemplate(newTemplate);
					newTemplate.setPool(oldTemplate.getPool());
					newTemplates.put(newTemplate.getRouteId(), newTemplate);
				}
			}
			
			// Add the old Templates to the NEW DATA.
			if (!npcName.isEmpty())
			{
				for (Npc npc : npcs)
				{
					if (!npc.getName().toString().toLowerCase().equals(npcName))
					{
						newTemplates.put(npc.getSpawn().getWalkerId(), DataManager.WALKER_DATA.getWalkerTemplate(npc.getSpawn().getWalkerId()));
					}
				}
			}
			
			for (WalkerTemplate temp : newTemplates.values())
			{
				newData.AddTemplate(temp);
			}
			
			if (!canceled)
			{
				msg(admin, "Updating changes to XML");
				newData.writeXml(admin.getWorldId());
				final long minutes = ((System.currentTimeMillis() - startTime) / 1000) / 60;
				final String timeString = ((minutes / 60) % 24) + " hours and " + (minutes % 60) + " minutes";
				msg(admin, "Finished after " + timeString);
			}
		}, 2000);
		reset();
	}
	
	/*
	 * for (Npc npc: npcs) { npcCounter++; HashMap<Integer, Float> corrections = new HashMap<Integer, Float>(); try { Thread.sleep(1000); //int i = 1; List <RouteStep> steps = template.getRouteSteps(); int stepSize = steps.size(); for (RouteStep step : steps) { int retryCount = 0; float oldAdminZ = admin.getZ(); float tempJumpHeight = jumpHeight; while (updatedZ > admin.getZ()) //Falling { if (canceled) { thread.cancel(true); return; } updatedZ = admin.getZ(); Thread.sleep(800); while (retryCount < 6 && (oldAdminZ - admin.getZ()) > 20 || admin.getZ() <= (World.getInstance().getWorldMap(admin.getWorldId()).getDeathLevel() + 10)) { if (retryCount == 5) { msg(admin, " Skipping step: ("+(npcCounter)+"/"+npcs.size()+") step [" + step.getRouteStep() + "/"+stepSize+"] - because there is an black hole ?!"); continue; } tempJumpHeight += 2; msg(admin, "ReTry ("+retryCount+"/5) step: ("+(npcCounter)+"/"+npcs.size()+") step [" + step.getRouteStep() + "/"+stepSize+"] with new jumpheight: "+tempJumpHeight); step.setZ(oldAdminZ + tempJumpHeight); TeleportService2.teleportTo(admin, admin.getWorldId(), step.getX(), step.getY(), step.getZ()); admin.getController().stopProtectionActiveTask(); updatedZ = step.getZ() +3F; oldAdminZ = step.getZ() +3F; retryCount++; } if (updatedZ == admin.getZ()) tempJumpHeight = jumpHeight; } step.setZ(admin.getZ()); lastStepZ = step.getZ(); if (!corrections.containsKey(step.getRouteStep())) corrections.put(step.getRouteStep(), admin.getZ()); else { corrections.remove(step.getRouteStep()); corrections.put(step.getRouteStep(), admin.getZ()); } } msg(admin, "Saving corrections..."); WalkerTemplate newTemplate = new WalkerTemplate(template.getRouteId()); ArrayList<RouteStep> newSteps = new ArrayList<RouteStep>(); int lastStep = template.isReversed() ? (stepSize + 2) / 2 : stepSize; for (int s = 0; s < lastStep; s++) { RouteStep step = template.getRouteSteps().get(s); RouteStep fixedStep = new RouteStep(step.getX(), step.getY(), corrections.get(step.getRouteStep()), step.getRestTime()); fixedStep.setRouteStep(step.getRouteStep()); newSteps.add(fixedStep); } newTemplate.setRouteSteps(newSteps); if (template.isReversed()) { newTemplate.setIsReversed(true); } newTemplate.setPool(template.getPool()); data.AddTemplate(newTemplate); msg(admin, "Done with routeid: "+routeId); } catch (Exception e) { } } if (!canceled) { if (npcs.size() > 1) data.saveData(admin.getWorldId()+"_"+World.getInstance().getWorldMap(admin.getWorldId()).getName()); else data.saveData("new_"+admin.getWorldId()+"_"+routeId); }
	 */
	
	/**
	 * Handles the failure of an {@code execute} command.<br>
	 * It sends a syntax hint to the player.
	 * @param player The {@code Player} who attempted the command.
	 * @param message The error message associated with the failure.
	 */
	@Override
	public void onFail(Player player, String message)
	{
		PacketSendUtility.sendMessage(player, "Syntax : //fixpath <jumpheight> | <npc [NpcName] (jumpheight)> | <cancel>");
	}
	
	/**
	 * Sends a message to the administrator.<br>
	 * The message is also recorded in the server logs.<br>
	 * This method only executes if {@code log} is set to {@code true}.
	 * @param admin The {@link Player} who will receive the message.
	 * @param txt The text content of the message to send.
	 */
	public static void msg(Player admin, String txt)
	{
		if (!log)
		{
			return;
		}
		
		if (admin != null)
		{
			PacketSendUtility.sendMessage(admin, txt);
		}
		
		GameServer.log.info("[FixPath] " + txt);
	}
	
	/**
	 * Resets the internal state of the {@link FixPath} class.<br>
	 * This method clears all active runners and resets flags to their default values.
	 */
	public static void reset()
	{
		if (!invul)
		{
			runner.setInvul(false);
		}
		
		runner = null;
		canceled = false;
		isRunning = false;
		thread = null;
		invul = false;
	}
}
