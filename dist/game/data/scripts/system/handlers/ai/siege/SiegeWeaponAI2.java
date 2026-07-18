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
package system.handlers.ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISummon;
import com.aionemu.gameserver.ai2.handler.FollowEventHandler;
import com.aionemu.gameserver.controllers.SiegeWeaponController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;
import com.aionemu.gameserver.services.summons.SummonsService;

/**
 * Handles the artificial intelligence logic for siege weapons.<br>
 * This class manages how these entities behave during combat and interaction with {@link Player} targets.
 * @author xTz
 */
@AIName("siege_weapon")
public class SiegeWeaponAI2 extends AISummon
{
	private long lastAttackTime;
	private int skill;
	private int skillLvl;
	private int duration;
	
	/**
	 * Handles the logic when an NPC is first spawned.<br>
	 * It sets the initial state to {@code AIState.IDLE}.<br>
	 * It configures the summon mode and retrieves skill data from {@link NpcSkillTemplates}.
	 */
	@Override
	protected void handleSpawned()
	{
		setStateIfNot(AIState.IDLE);
		SummonsService.doMode(SummonMode.GUARD, getOwner());
		final NpcSkillTemplate skillTemplate = getNpcSkillTemplates().getNpcSkills().get(0);
		skill = skillTemplate.getSkillid();
		skillLvl = skillTemplate.getSkillLevel();
		duration = DataManager.SKILL_DATA.getSkillTemplate(skill).getDuration();
	}
	
	/**
	 * Updates the state of this NPC to follow a specific {@code Creature}.<br>
	 * This method sets the current {@link AIState} to {@code FOLLOWING}.
	 * @param creature The {@code Creature} that is being followed.
	 */
	@Override
	protected void handleFollowMe(Creature creature)
	{
		setStateIfNot(AIState.FOLLOWING);
	}
	
	/**
	 * This method handles the logic when a {@code Creature} moves.<br>
	 * It triggers the movement behavior for the AI.
	 * @param creature The {@code Creature} that has moved.
	 */
	@Override
	protected void handleCreatureMoved(Creature creature)
	{
	}
	
	/**
	 * This method stops a {@link Creature} from following the NPC.<br>
	 * It calls the {@code stopFollow} method in {@link FollowEventHandler}.
	 * @param creature The {@code Creature} that is currently following.
	 */
	@Override
	protected void handleStopFollowMe(Creature creature)
	{
		setStateIfNot(AIState.IDLE);
		getOwner().getMoveController().abortMove();
	}
	
	/**
	 * This method is called when the current target moves too far away.<br>
	 * It triggers the {@code onTargetTooFar} logic.
	 */
	@Override
	protected void handleTargetTooFar()
	{
		getOwner().getMoveController().moveToDestination();
	}
	
	/**
	 * This method is called when an NPC reaches its destination.<br>
	 * It triggers the logic for completing a movement action.<br>
	 * It calls {@code onMoveArrived} to process the event.
	 */
	@Override
	protected void handleMoveArrived()
	{
		getOwner().getController().onMove();
		getOwner().getMoveController().abortMove();
	}
	
	/**
	 * Processes the movement logic for the siege weapon.<br>
	 * This method triggers {@code onMove()} on the owner's controller.<br>
	 * It also tells the move controller to proceed toward the target object.
	 */
	@Override
	protected void handleMoveValidate()
	{
		getOwner().getController().onMove();
		getMoveController().moveToTargetObject();
	}
	
	/**
	 * Retrieves the {@link SiegeWeaponController} for this AI.<br>
	 * This method casts the base controller to the specific siege weapon type.
	 * @return The {@code SiegeWeaponController} instance.
	 */
	@Override
	protected SiegeWeaponController getController()
	{
		return (SiegeWeaponController) super.getController();
	}
	
	/**
	 * Retrieves the skill templates for this NPC.<br>
	 * It fetches data from the {@link SiegeWeaponController}.
	 * @return the {@code NpcSkillTemplates} object.
	 */
	private NpcSkillTemplates getNpcSkillTemplates()
	{
		return getController().getNpcSkillTemplates();
	}
	
	/**
	 * Processes the logic for when this AI is attacked by a {@code Creature}.<br>
	 * It checks if the attacker is within 40 units of the owner.<br>
	 * If close enough, it calculates a path to move away from the attacker.<br>
	 * The owner will then move toward the nearest valid collision point.
	 * @param creature The {@code Creature} that initiated the attack.
	 */
	@Override
	protected void handleAttack(Creature creature)
	{
		if (creature == null)
		{
			return;
		}
		
		final Race race = creature.getRace();
		final Player master = getOwner().getMaster();
		if (master == null)
		{
			return;
		}
		
		final Race masterRace = master.getRace();
		if (masterRace.equals(Race.ASMODIANS) && !race.equals(Race.PC_LIGHT_CASTLE_DOOR) && !race.equals(Race.DRAGON_CASTLE_DOOR))
		{
			return;
		}
		else if (masterRace.equals(Race.ELYOS) && !race.equals(Race.PC_DARK_CASTLE_DOOR) && !race.equals(Race.DRAGON_CASTLE_DOOR))
		{
			return;
		}
		
		if (!getOwner().getMode().equals(SummonMode.ATTACK))
		{
			return;
		}
		
		if ((System.currentTimeMillis() - lastAttackTime) > (duration + 2000))
		{
			lastAttackTime = System.currentTimeMillis();
			getOwner().getController().useSkill(skill, skillLvl);
		}
	}
}
