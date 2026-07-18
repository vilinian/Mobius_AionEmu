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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * Handles the logic for the polymorph effect in the game.<br>
 * This class manages how a {@link Creature} is transformed into another form.<br>
 * It extends {@link TransformEffect} to provide specific behavior for this transformation.
 * @author ATracer
 * @modified Cheatkiller
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PolymorphEffect")
public class PolymorphEffect extends TransformEffect
{
	/**
	 * Applies a specific {@code Effect} to a target.<br>
	 * This method checks if the target is an instance of {@link Player}.<br>
	 * It processes the logic required for the effect to take place.
	 * @param effect The {@code Effect} object to be applied.
	 */
	@Override
	public void applyEffect(Effect effect)
	{
		super.applyEffect(effect);
		if (effect.getEffected() instanceof Player)
		{
			final Player player = (Player) effect.getEffected();
			player.getKnownList().doOnAllNpcs(new Visitor<Npc>()
			{
				@Override
				public void visit(Npc npc)
				{
					PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, npc.getType(player), 0));
				}
			});
		}
	}
	
	/**
	 * Starts a new {@link Effect} instance.<br>
	 * This method initializes the effect and begins its execution.<br>
	 * It is a convenience method that passes {@code null} for the abnormal state.
	 * @param effect The {@code Effect} object to be started.
	 */
	@Override
	public void startEffect(Effect effect)
	{
		if (model > 0)
		{
			final Creature effected = effect.getEffected();
			final NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(model);
			if (template != null)
			{
				effected.getTransformModel().setTribe(template.getTribe(), false);
			}
		}
		
		super.startEffect(effect);
	}
	
	/**
	 * Stops a specific {@code Effect} from being active.<br>
	 * This method removes the associated observers from the target controller.<br>
	 * Use this to clean up effects when they expire or are removed.
	 * @param effect The {@code Effect} object to stop.
	 */
	@Override
	public void endEffect(Effect effect)
	{
		effect.getEffected().getTransformModel().setActive(false);
		if (effect.getEffected() instanceof Player)
		{
			final Player player = (Player) effect.getEffected();
			player.getKnownList().doOnAllNpcs(new Visitor<Npc>()
			{
				@Override
				public void visit(Npc npc)
				{
					PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, npc.getType(player), 0));
					player.getTransformModel().setTribe(null, false);
				}
			});
		}
		
		super.endEffect(effect);
	}
}
