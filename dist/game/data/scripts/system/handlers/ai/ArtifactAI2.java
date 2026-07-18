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
package system.handlers.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Request;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.controllers.observer.ItemUseObserver;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.ArtifactStatus;
import com.aionemu.gameserver.model.team.legion.LegionPermissionsMask;
import com.aionemu.gameserver.model.templates.siegelocation.ArtifactActivation;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ABYSS_ARTIFACT_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.properties.TargetSpeciesAttribute;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * Handles the artificial intelligence logic for artifact-related NPCs.<br>
 * This class manages behaviors such as activation and interaction within siege locations. It extends {@link NpcAI2} to provide specific actions for these entities.
 * @author ATracer, Source
 */
@AIName("artifact")
public class ArtifactAI2 extends NpcAI2
{
	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final Map<Integer, ItemUseObserver> observers = new HashMap<>();
	
	/**
	 * Retrieves the spawn template for this siege.<br>
	 * This method calls the parent class to get the {@code SiegeSpawnTemplate}.
	 * @return the {@code SiegeSpawnTemplate} associated with this object.
	 */
	@Override
	protected SiegeSpawnTemplate getSpawnTemplate()
	{
		return (SiegeSpawnTemplate) super.getSpawnTemplate();
	}
	
	/**
	 * This method is called when a dialog starts with an NPC.<br>
	 * It triggers the start of the item usage logic for the {@code player}.
	 * @param player The {@link Player} who initiated the interaction.
	 */
	@Override
	protected void handleDialogStart(Player player)
	{
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
		AI2Actions.addRequest(this, player, 160028, new AI2Request()
		{
			@Override
			public void acceptRequest(Creature requester, Player responder)
			{
				AI2Actions.addRequest(ArtifactAI2.this, player, 160016, new AI2Request()
				{
					
					@Override
					public void acceptRequest(Creature requester, Player responder)
					{
						onActivate(responder);
					}
				}, new DescriptionId((2 * 716570) + 1), SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId()).getTemplate().getActivation().getCount());
				
			}
		}, loc);
	}
	
	/**
	 * This method handles the logic after a dialog ends.<br>
	 * It is called when a {@link Player} finishes interacting with an NPC.
	 * @param player The {@code Player} who finished the dialog.
	 */
	@Override
	protected void handleDialogFinish(Player player)
	{
	}
	
	/**
	 * Handles the logic when a {@link Player} activates an artifact.<br>
	 * This method checks for cooldowns, legion permissions, and required items.<br>
	 * It manages the transition between activation states and schedules the resulting skills.
	 * @param player The {@link Player} attempting to use the artifact.
	 */
	public void onActivate(Player player)
	{
		final ArtifactLocation loc = SiegeService.getInstance().getArtifact(getSpawnTemplate().getSiegeId());
		
		// Get Skill id, item, count and target defined for each artifact.
		final ArtifactActivation activation = loc.getTemplate().getActivation();
		final int skillId = activation.getSkillId();
		final int itemId = activation.getItemId();
		final int count = activation.getCount();
		final SkillTemplate skillTemplate = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		
		if (skillTemplate == null)
		{
			LoggerFactory.getLogger(ArtifactAI2.class).error("No skill template for artifact effect id : " + skillId);
			return;
		}
		
		if ((loc.getCoolDown() > 0) || !loc.getStatus().equals(ArtifactStatus.IDLE))
		{
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ARTIFACT_OUT_OF_ORDER);
			return;
		}
		
		if (loc.getLegionId() != 0)
		{
			if (!player.isLegionMember() || (player.getLegion().getLegionId() != loc.getLegionId()) || !player.getLegionMember().hasRights(LegionPermissionsMask.ARTIFACT))
			{
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ARTIFACT_HAVE_NO_AUTHORITY);
				return;
			}
		}
		
		if (player.getInventory().getItemCountByItemId(itemId) < count)
		{
			return;
		}
		
		if (LoggingConfig.LOG_SIEGE)
		{
			log.info("Artifact " + getSpawnTemplate().getSiegeId() + " activated by " + player.getName() + ". Activator race: " + player.getRace().toString());
		}
		
		if (!loc.getStatus().equals(ArtifactStatus.IDLE))
		{
			return;
		}
		
		// Brodcast start activation.
		final SM_SYSTEM_MESSAGE startMessage = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CASTING(player.getRace().getRaceDescriptionId(), player.getName(), new DescriptionId(skillTemplate.getNameId()));
		loc.setStatus(ArtifactStatus.ACTIVATION);
		final SM_ABYSS_ARTIFACT_INFO artifactInfo = new SM_ABYSS_ARTIFACT_INFO(loc.getLocationId());
		player.getPosition().getWorldMapInstance().doOnAllPlayers(player1 ->
		{
			PacketSendUtility.sendPacket(player1, startMessage);
			PacketSendUtility.sendPacket(player1, artifactInfo);
		});
		
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 1));
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_QUESTLOOT, 0, getObjectId()), true);
		
		final ItemUseObserver observer = new ItemUseObserver()
		{
			@Override
			public void abort()
			{
				player.getController().cancelTask(TaskId.ACTION_ITEM_NPC);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
				final SM_SYSTEM_MESSAGE message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CANCELED(loc.getRace().getDescriptionId(), new DescriptionId(skillTemplate.getNameId()));
				loc.setStatus(ArtifactStatus.IDLE);
				final SM_ABYSS_ARTIFACT_INFO artifactInfo = new SM_ABYSS_ARTIFACT_INFO(loc.getLocationId());
				getOwner().getPosition().getWorldMapInstance().doOnAllPlayers(player1 ->
				{
					PacketSendUtility.sendPacket(player1, message);
					PacketSendUtility.sendPacket(player1, artifactInfo);
				});
			}
		};
		observers.put(player.getObjectId(), observer);
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ACTION_ITEM_NPC, ThreadPoolManager.getInstance().schedule(() ->
		{
			final ItemUseObserver observer1 = observers.remove(player.getObjectId());
			if (observer1 != null)
			{
				player.getObserveController().removeObserver(observer1);
			}
			
			PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), getObjectId(), 10000, 0));
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.END_QUESTLOOT, 0, getObjectId()), true);
			if (!player.getInventory().decreaseByItemId(itemId, count))
			{
				return;
			}
			
			final SM_SYSTEM_MESSAGE message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_CORE_CASTING(loc.getRace().getDescriptionId(), new DescriptionId(skillTemplate.getNameId()));
			loc.setStatus(ArtifactStatus.CASTING);
			final SM_ABYSS_ARTIFACT_INFO artifactInfo1 = new SM_ABYSS_ARTIFACT_INFO(loc.getLocationId());
			
			player.getPosition().getWorldMapInstance().doOnAllPlayers(player1 ->
			{
				PacketSendUtility.sendPacket(player1, message);
				PacketSendUtility.sendPacket(player1, artifactInfo1);
			});
			
			loc.setLastActivation(System.currentTimeMillis());
			if (loc.getTemplate().getRepeatCount() == 1)
			{
				ThreadPoolManager.getInstance().schedule(new ArtifactUseSkill(loc, player, skillTemplate), 13000);
			}
			else
			{
				final ScheduledFuture<?> s = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ArtifactUseSkill(loc, player, skillTemplate), 13000, loc.getTemplate().getRepeatInterval() * 1000);
				ThreadPoolManager.getInstance().schedule(() ->
				{
					s.cancel(true);
					loc.setStatus(ArtifactStatus.IDLE);
				}, 13000 + (loc.getTemplate().getRepeatInterval() * loc.getTemplate().getRepeatCount() * 1000));
			}
			
		}, 10000));
	}
	
	class ArtifactUseSkill implements Runnable
	{
		private final ArtifactLocation artifact;
		private final Player player;
		private final SkillTemplate skill;
		private int runCount = 1;
		private final SM_ABYSS_ARTIFACT_INFO pkt;
		private final SM_SYSTEM_MESSAGE message;
		
		/**
		 * @param artifact
		 * @param activator
		 * @param skill
		 */
		private ArtifactUseSkill(ArtifactLocation artifact, Player activator, SkillTemplate skill)
		{
			this.artifact = artifact;
			player = activator;
			this.skill = skill;
			pkt = new SM_ABYSS_ARTIFACT_INFO(artifact.getLocationId());
			message = SM_SYSTEM_MESSAGE.STR_ARTIFACT_FIRE(activator.getRace().getRaceDescriptionId(), player.getName(), new DescriptionId(skill.getNameId()));
		}
		
		@Override
		public void run()
		{
			if (artifact.getTemplate().getRepeatCount() < runCount)
			{
				return;
			}
			
			final boolean start = (runCount == 1);
			final boolean end = (runCount == artifact.getTemplate().getRepeatCount());
			
			runCount++;
			player.getPosition().getWorldMapInstance().doOnAllPlayers(player ->
			{
				if (start)
				{
					PacketSendUtility.sendPacket(player, message);
				}
				
				artifact.setStatus(ArtifactStatus.ACTIVATED);
				PacketSendUtility.sendPacket(player, pkt);
				if (end)
				{
					artifact.setStatus(ArtifactStatus.IDLE);
					PacketSendUtility.sendPacket(player, pkt);
				}
			});
			final boolean pc = skill.getProperties().getTargetSpecies() == TargetSpeciesAttribute.PC;
			for (Creature creature : artifact.getCreatures().values())
			{
				if ((creature.getActingCreature() instanceof Player) || ((creature instanceof SiegeNpc) && !pc))
				{
					switch (skill.getProperties().getTargetRelation())
					{
						case FRIEND:
							if (player.isEnemy(creature))
							{
								continue;
							}
							break;
						case ENEMY:
							if (!player.isEnemy(creature))
							{
								continue;
							}
							break;
						default:
							break;
					}
					
					AI2Actions.applyEffect(ArtifactAI2.this, skill, creature);
				}
			}
		}
	}
}
