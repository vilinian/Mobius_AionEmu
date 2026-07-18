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
package com.aionemu.gameserver.skillengine.effect;

/**
 * Represents various types of abnormal status effects that can be applied to characters.<br>
 * This enum is used by the {@code Effect} system to manage debuffs and buffs.<br>
 * It defines states such as stun, silence, or poison for gameplay mechanics.
 * @author ATracer
 * @Reworked Kill3r
 */
public enum AbnormalState
{
	BUFF(0),
	POISON(1),
	BLEED(2),
	PARALYZE(4),
	SLEEP(8),
	ROOT(16), // ?? cannot move ?
	BLIND(32),
	UNKNOWN(64),
	DISEASE(128),
	SILENCE(256),
	FEAR(512), // Fear I
	CURSE(1024),
	CHAOS(2056),
	STUN(4096),
	PETRIFICATION(8192),
	STUMBLE(16384),
	STAGGER(32768),
	OPENAERIAL(65536),
	SNARE(131072),
	SLOW(262144),
	SPIN(524288),
	BIND(1048576),
	DEFORM(2097152), // (Curse of Roots I, Fear I)
	CANNOT_MOVE(4194304), // (Inescapable Judgment I)
	NOFLY(8388608), // cannot fly
	KNOCKBACK(16777216), // simple_root
	HIDE(536870912), // hide 33554432
	
	/**
	 * Compound abnormal states
	 */
	CANT_ATTACK_STATE(SPIN.id | SLEEP.id | STUN.id | STUMBLE.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | FEAR.id | CANNOT_MOVE.id),
	CANT_MOVE_STATE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id),
	CANT_MOVE_STATE2(SPIN.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id), // without root , for CM_EMOTION, because you can buff up and go attack mode while rooted
	DISMOUT_RIDE(SPIN.id | ROOT.id | SLEEP.id | STUMBLE.id | STUN.id | STAGGER.id | OPENAERIAL.id | PARALYZE.id | CANNOT_MOVE.id | FEAR.id | SNARE.id);
	
	private final int id;
	
	/**
	 * Private constructor to initialize the state with a unique identifier.<br>
	 * This method is used internally by the {@link AbnormalState} enum.
	 * @param id The unique integer value for this state.
	 */
	private AbnormalState(int id)
	{
		this.id = id;
	}
	
	/**
	 * Returns the unique identifier of this object.
	 * @return The integer ID.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Finds an {@link AbnormalState} based on its string name.<br>
	 * This method searches through all available states to find a match.
	 * @param name The name of the state to look for.
	 * @return The matching {@link AbnormalState} or {@code null} if no match is found.
	 */
	public static AbnormalState getIdByName(String name)
	{
		for (AbnormalState id : values())
		{
			if (id.name().equals(name))
			{
				return id;
			}
		}
		
		return null;
	}
	
	/**
	 * Finds an {@link AbnormalState} based on its unique identifier.<br>
	 * This method searches through all available states to find a match.
	 * @param id The integer ID of the state to look for.
	 * @return The matching {@link AbnormalState} or {@code null} if no match is found.
	 */
	public static AbnormalState getStateById(int id)
	{
		for (AbnormalState as : values())
		{
			if (as.getId() == id)
			{
				return as;
			}
		}
		
		return null;
	}
}
