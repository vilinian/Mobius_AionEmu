/*
 * This file is part of the Mobius AionEmu project.
 * 
 * Mobius AionEmu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Mobius AionEmu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionemu.gameserver.services.vortexservice;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.model.vortex.VortexStateType;
import com.aionemu.gameserver.services.VortexService;

/**
 * Manages the logic and state transitions for dimensional vortexes in the game world.<br>
 * This class provides a base implementation for handling {@link VortexLocation} interactions.<br>
 * It coordinates how players and NPCs interact with different {@code VortexStateType} values.
 * @author Source, Mobius
 * @param <VL>
 */
public abstract class DimensionalVortex<VL extends VortexLocation>
{
	private final VL vortexLocation;
	private final AtomicBoolean finished = new AtomicBoolean();
	private boolean generatorDestroyed;
	private Npc generator;
	private boolean started;
	
	protected abstract void startInvasion();
	
	protected abstract void stopInvasion();
	
	public abstract void addPlayer(Player player, boolean isInvader);
	
	public abstract void kickPlayer(Player player, boolean isInvader);
	
	public abstract void updateDefenders(Player defender);
	
	public abstract void updateInvaders(Player invader);
	
	public abstract Map<Integer, Player> getDefenders();
	
	public abstract Map<Integer, Player> getInvaders();
	
	/**
	 * Creates a new instance of a {@link DimensionalVortex}.<br>
	 * This constructor initializes the vortex with a specific location.
	 * @param vortexLocation The {@code VL} object defining where the vortex is located.
	 */
	public DimensionalVortex(VL vortexLocation)
	{
		this.vortexLocation = vortexLocation;
	}
	
	/**
	 * Starts the dimensional vortex invasion.<br>
	 * This method checks if the process has already begun.<br>
	 * If not started, it calls {@code startInvasion}.
	 */
	public void start()
	{
		boolean doubleStart = false;
		
		synchronized (this)
		{
			if (started)
			{
				doubleStart = true;
			}
			else
			{
				started = true;
			}
		}
		
		if (doubleStart)
		{
			return;
		}
		
		startInvasion();
	}
	
	/**
	 * Stops the current invasion process.<br>
	 * This method sets the {@code finished} state to {@code true}.<br>
	 * It calls the {@code stopInvasion} method if it was not already finished.
	 */
	public void stop()
	{
		if (finished.compareAndSet(false, true))
		{
			stopInvasion();
		}
	}
	
	/**
	 * Initializes the rift generator for this vortex.<br>
	 * It searches for specific {@code Npc} IDs in the current location.<br>
	 * The method sets the generator and calls {@code registerSiegeBossListeners}.
	 */
	protected void initRiftGenerator()
	{
		Npc gen = null;
		
		for (VisibleObject obj : getVortexLocation().getSpawned())
		{
			final int npcId = ((Npc) obj).getNpcId();
			if ((npcId == 209487) || (npcId == 209486))
			{
				gen = (Npc) obj;
			}
		}
		
		if (gen == null)
		{
			throw new NullPointerException("No generator was found in loc:" + getVortexLocationId());
		}
		
		setGenerator(gen);
		registerSiegeBossListeners();
	}
	
	/**
	 * Spawns the vortex based on the specified state.<br>
	 * This method calls {@code VortexStateType)}.
	 * @param type The {@code VortexStateType} to use for spawning.
	 */
	protected void spawn(VortexStateType type)
	{
		VortexService.getInstance().spawn(getVortexLocation(), type);
	}
	
	/**
	 * Removes the base and its associated entities from the game world.<br>
	 * This method clears the {@code flag}, deletes spawned NPCs, and cancels active assault tasks.<br>
	 * It also calls {@code despawnAttackers} if a stop task is present.
	 */
	protected void despawn()
	{
		VortexService.getInstance().despawn(getVortexLocation());
	}
	
	/**
	 * Registers the death handler for the generator.<br>
	 * It wires the generator AI to stop the invasion when the generator is destroyed.
	 */
	protected void registerSiegeBossListeners()
	{
		((AbstractAI) getGenerator().getAi2()).setOnDeathAfter(() ->
		{
			setGeneratorDestroyed(true);
			VortexService.getInstance().stopInvasion(getVortexLocationId());
		});
	}
	
	/**
	 * Removes the listeners associated with the generator.<br>
	 * It ensures that the death callback is removed from the generator's AI components.
	 */
	protected void unregisterSiegeBossListeners()
	{
		if (getGenerator() != null)
		{
			((AbstractAI) getGenerator().getAi2()).setOnDeathAfter(null);
		}
	}
	
	/**
	 * Checks if the generator has been destroyed.<br>
	 * This method returns the current state of the {@code generatorDestroyed} flag.
	 * @return {@code true} if the generator is destroyed, {@code false} otherwise.
	 */
	public boolean isGeneratorDestroyed()
	{
		return generatorDestroyed;
	}
	
	/**
	 * Updates the destruction status of the generator.<br>
	 * This method sets the {@code generatorDestroyed} flag to the provided value.
	 * @param state The new status to set. Use {@code true} if destroyed and {@code false} otherwise.
	 */
	public void setGeneratorDestroyed(boolean state)
	{
		generatorDestroyed = state;
	}
	
	/**
	 * Retrieves the {@link Npc} object that acts as the generator.<br>
	 * This method returns the current generator instance associated with this vortex.
	 * @return The {@code Npc} representing the generator, or {@code null} if it has not been set.
	 */
	public Npc getGenerator()
	{
		return generator;
	}
	
	/**
	 * Sets the {@link Npc} object that acts as the generator.<br>
	 * This method updates the internal generator field.
	 * @param generator The {@code Npc} to be used as the generator.
	 */
	public void setGenerator(Npc generator)
	{
		this.generator = generator;
	}
	
	/**
	 * Checks if the current process has completed.<br>
	 * This method retrieves the status from the internal {@code Future}.
	 * @return {@code true} if the process is finished, {@code false} otherwise.
	 */
	public boolean isFinished()
	{
		return finished.get();
	}
	
	/**
	 * Retrieves the current location of the dimensional vortex.<br>
	 * This method returns the {@code VL} object associated with this instance.
	 * @return The {@code VortexLocation} of the vortex.
	 */
	public VL getVortexLocation()
	{
		return vortexLocation;
	}
	
	/**
	 * Retrieves the unique identifier for the current vortex location.<br>
	 * This method calls {@code getVortexLocation} to fetch the ID.
	 * @return The {@code int} ID of the vortex location.
	 */
	public int getVortexLocationId()
	{
		return vortexLocation.getId();
	}
}
