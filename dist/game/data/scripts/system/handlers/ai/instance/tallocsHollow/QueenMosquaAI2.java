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
package system.handlers.ai.instance.tallocsHollow;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

import system.handlers.ai.SummonerAI2;

/**
 * Handles the artificial intelligence for the {@code queenmosqua} NPC.<br>
 * This class extends {@link SummonerAI2} to manage specific behaviors within the Tallocs Hollow instance.
 * @author xTz
 */
@AIName("queenmosqua")
public class QueenMosquaAI2 extends SummonerAI2
{
	private boolean isHome = true;
	
	/**
	 * This method manages the behavior when a {@code Creature} becomes aggressive.<br>
	 * It checks if the AI is currently able to think.<br>
	 * If so, it triggers the {@code onAggro} logic.
	 * @param creature The {@code Creature} that has triggered the aggro state.
	 */
	@Override
	protected void handleCreatureAggro(Creature creature)
	{
		super.handleCreatureAggro(creature);
		if (isHome)
		{
			isHome = false;
			getPosition().getWorldMapInstance().getDoors().get(7).setOpen(false);
		}
	}
	
	/**
	 * This method handles the logic when an NPC returns home.<br>
	 * It calls {@code handleBackHome} from the parent class.<br>
	 * It also updates the skill status using {@code setUseInSpawnedSkill()}.
	 */
	@Override
	protected void handleBackHome()
	{
		isHome = true;
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);
		super.handleBackHome();
	}
	
	/**
	 * This method is called when the NPC dies.<br>
	 * It opens a specific door and spawns a new entity.<br>
	 * It also sends system messages and handles summon releases for players.
	 */
	@Override
	protected void handleDied()
	{
		super.handleDied();
		final WorldMapInstance instance = getPosition().getWorldMapInstance();
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);
		
		final Npc npc = instance.getNpc(700738);
		if (npc != null)
		{
			final SpawnTemplate template = npc.getSpawn();
			spawn(700739, template.getX(), template.getY(), template.getZ(), template.getHeading(), 11);
			npc.getKnownList().doOnAllPlayers(player ->
			{
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400476));
				final Summon summon = player.getSummon();
				if (summon != null)
				{
					if ((summon.getNpcId() == 799500) || (summon.getNpcId() == 799501))
					{
						SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
						PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 435));
					}
				}
			});
			npc.getController().onDelete();
		}
	}
}
