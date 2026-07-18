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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.List;

import com.aionemu.gameserver.controllers.attack.AttackResult;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;

/**
 * This packet handles the processing of an attack action initiated by a player.<br>
 * It communicates the results of an attack to the client using {@link AttackResult}.
 * @author -Nemesiss-, Sweetkr
 * @author GiGatR00n v4.7.5.x
 */
public class SM_ATTACK extends AionServerPacket
{
	private final int attackNo;
	private final int time;
	private final int type;
	private final int SimpleAttackType;
	private final List<AttackResult> attackList;
	private final Creature attacker;
	private final Creature target;
	
	/**
	 * This packet handles the data for a creature performing an attack.<br>
	 * It stores details about the attacker, the target, and the result of the action.
	 * @param attacker The {@link Creature} who is initiating the attack.
	 * @param target The {@link Creature} being attacked.
	 * @param attackNo The unique identifier for the specific attack instance.
	 * @param time The timestamp or duration associated with the attack.
	 * @param type The category or classification of the attack.
	 * @param attackList A {@code List} containing the results of the attack actions.
	 */
	public SM_ATTACK(Creature attacker, Creature target, int attackNo, int time, int type, List<AttackResult> attackList)
	{
		this.attacker = attacker;
		this.target = target;
		this.attackNo = attackNo; // empty
		this.time = time; // empty
		this.type = type; // empty
		this.attackList = attackList;
		SimpleAttackType = attacker.getController().getSimpleAttackType();
	}
	
	@Override
	protected void writeImpl(AionConnection con)
	{
		writeD(attacker.getObjectId());
		writeC(attackNo); // Attack Number e.g. 1, 2, 3, 5, ..., Max_Integer_Value
		writeH(time); // unknown
		writeC((byte) SimpleAttackType); // 0=Ground Attacks | 1=Air Attacks (v4.7.5.17)
		writeC(type); // 0, 1, 2
		writeD(target.getObjectId());
		
		final int attackerMaxHp = attacker.getLifeStats().getMaxHp();
		final int attackerCurrHp = attacker.getLifeStats().getCurrentHp();
		final int targetMaxHp = target.getLifeStats().getMaxHp();
		final int targetCurrHp = target.getLifeStats().getCurrentHp();
		
		writeC((int) ((100f * targetCurrHp) / targetMaxHp)); // target %hp
		writeC((int) ((100f * attackerCurrHp) / attackerMaxHp)); // attacker %hp
		
		// TODO refactor attack controller
		switch (attackList.get(0).getAttackStatus().getId()) // Counter skills
		{
			case 196: // case CRITICAL_BLOCK 4.5
			case 4: // case BLOCK
			case 5:
			case 213:
				writeD(32);
				break;
			case 194: // case CRITICAL_PARRY 4.5
			case 2: // case PARRY
			case 3:
			case 211:
				writeD(64);
				break;
			case 192: // case CRITICAL_DODGE 4.5
			case 0: // case DODGE
			case 1:
			case 209:
				writeD(128);
				break;
			case 198: // case CRITICAL_RESIST 4.5
			case 6: // case RESIST
			case 7:
			case 215:
				writeD(256); // need more info becuz sometimes 0
				break;
			default:
				writeD(0);
				break;
		}
		
		// setting counter skill from packet to have the best synchronization of time with client
		if (target instanceof Player)
		{
			if (attackList.get(0).getAttackStatus().isCounterSkill())
			{
				((Player) target).setLastCounterSkill(attackList.get(0).getAttackStatus());
			}
		}
		
		writeC(attackList.size());
		for (AttackResult attack : attackList)
		{
			writeD(attack.getDamage());
			writeC(attack.getAttackStatus().getId());
			final byte shieldType = (byte) attack.getShieldType();
			writeC(shieldType);
			writeB(new byte[16]); // TODO
			
			/**
			 * shield Type: 1: reflector 2: normal shield 8: protect effect (ex. skillId: 417 Bodyguard) TODO find out 4
			 */
			switch (shieldType)
			{
				case 0:
				case 2:
					break;
				case 8:
				case 10:
					writeD(attack.getProtectorId());
					writeD(attack.getProtectedDamage());
					writeD(attack.getProtectedSkillId());
					break;
				case 16:
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(0);
					writeD(attack.getShieldMp());
					writeD(attack.getReflectedSkillId());
					break;
				default:
					System.out.println("Default Called: ");
					writeD(attack.getProtectorId());
					writeD(attack.getProtectedDamage());
					writeD(attack.getProtectedSkillId());
					writeD(attack.getReflectedDamage());
					writeD(attack.getReflectedSkillId());
					writeD(0);
					writeD(0);
					break;
			}
		}
		
		writeC(0); // list size
	}
}
