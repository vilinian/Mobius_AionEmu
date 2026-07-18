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
package com.aionemu.gameserver.services.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.configs.main.BaseConfig;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * This class serves as a base component for various game services.<br>
 * It provides common functionality and shared logic for objects related to {@code BaseLocation}. It is designed to be extended by specific service implementations.
 * @author Source
 * @author Mobius
 * @param <BL>
 */
public class Base<BL extends BaseLocation>
{
	private static final Logger log = LoggerFactory.getLogger(Base.class);
	private Future<?> startAssault, stopAssault;
	private final BL baseLocation;
	private final List<Race> list = new ArrayList<>();
	private final List<Npc> attackers = new ArrayList<>();
	private final AtomicBoolean finished = new AtomicBoolean();
	private boolean started;
	private Npc boss, flag;
	
	/**
	 * Initializes a new instance of the {@code Base} class.<br>
	 * This constructor sets up the required location and internal lists.
	 * @param baseLocation The {@code BL} object representing the base location.
	 */
	public Base(BL baseLocation)
	{
		list.add(Race.ASMODIANS);
		list.add(Race.ELYOS);
		list.add(Race.NPC);
		this.baseLocation = baseLocation;
	}
	
	/**
	 * Starts the base service operations.<br>
	 * This method sets the {@code started} flag to {@code true}.<br>
	 * It then calls the {@code spawn} method if it is the first time being called.
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
		
		if (!doubleStart)
		{
			spawn();
		}
	}
	
	/**
	 * Stops the current service and cleans up resources.<br>
	 * This method sets the {@code finished} state to {@code true}.<br>
	 * It removes the boss listener if a boss exists.<br>
	 * It also calls the {@code despawn} method.
	 */
	public void stop()
	{
		if (finished.compareAndSet(false, true))
		{
			if (getBoss() != null)
			{
				rmvBossListener();
			}
			
			despawn();
		}
	}
	
	/**
	 * Retrieves the list of spawn groups for this base.<br>
	 * It uses {@code SPAWNS_DATA2} to find data based on the current ID.
	 * @return A {@code List} of {@code SpawnGroup2} objects.
	 */
	private List<SpawnGroup2> getBaseSpawns()
	{
		final List<SpawnGroup2> spawns = DataManager.SPAWNS_DATA2.getBaseSpawnsByLocId(getId());
		
		if (spawns == null)
		{
			throw new NullPointerException("No spawns for base:" + getId());
		}
		
		return spawns;
	}
	
	/**
	 * Handles the initial spawning logic for the base area.<br>
	 * It iterates through {@link SpawnGroup2} to create NPCs based on the current race.<br>
	 * This method also triggers the delayed assault and additional spawns.
	 */
	protected void spawn()
	{
		for (SpawnGroup2 group : getBaseSpawns())
		{
			for (SpawnTemplate spawn : group.getSpawnTemplates())
			{
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace()))
				{
					if (template.getHandlerType() == null)
					{
						final Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						final NpcTemplate npcTemplate = npc.getObjectTemplate();
						if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.FLAG))
						{
							setFlag(npc);
							final MapRegion mr = npc.getPosition().getMapRegion();
							mr.activate();
						}
					}
				}
			}
		}
		
		delayedAssault();
		delayedSpawn(getRace());
	}
	
	/**
	 * Schedules a delayed task to initiate an assault.<br>
	 * This method uses {@link ThreadPoolManager} to run the logic after a random delay.<br>
	 * It calls {@code chooseAttackersRace} and sends a system message to players.
	 */
	private void delayedAssault()
	{
		startAssault = ThreadPoolManager.getInstance().schedule(() ->
		{
			chooseAttackersRace();
			sendLDF4AdvanceMsgKiller(getId()); // Akaron Base Message
		}, Rnd.get(BaseConfig.ASSAULT_MIN_DELAY, BaseConfig.ASSAULT_MAX_DELAY) * 60000); // Randomly every 15 - 20 min start assault
	}
	
	// Akaron Base Message
	/**
	 * Sends a specific LDF4 advance killer system message to all players.<br>
	 * The message type is determined by the provided {@code id}.
	 * @param id The unique identifier for the message version.
	 * @return {@code true} if the message was sent successfully, otherwise {@code false}.
	 */
	public boolean sendLDF4AdvanceMsgKiller(int id)
	{
		switch (id)
		{
			case 100:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v13));
				return true;
			case 101:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v04));
				return true;
			case 102:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v12));
				return true;
			case 103:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v03));
				return true;
			case 104:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v06));
				return true;
			case 105:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v05));
				return true;
			case 106:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v01));
				return true;
			case 107:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v09));
				return true;
			case 108:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v11));
				return true;
			case 109:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v10));
				return true;
			case 110:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v07));
				return true;
			case 111:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v02));
				return true;
			case 112:
				World.getInstance().doOnAllPlayers(player -> PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LDF4_Advance_killer_v08));
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Schedules a delayed task to spawn a boss.<br>
	 * This method checks if the current race matches the provided {@code race}.<br>
	 * It ensures that the boss is not already present before calling {@code spawnBoss}.<br>
	 * The delay is calculated based on values from {@code BaseConfig}.
	 * @param race The specific {@code Race} required to trigger the spawn.
	 */
	private void delayedSpawn(Race race)
	{
		ThreadPoolManager.getInstance().schedule(() ->
		{
			if (getRace().equals(race) && (getBoss() == null))
			{
				spawnBoss();
			}
		}, Rnd.get(BaseConfig.BOSS_SPAWN_MIN_DELAY, BaseConfig.BOSS_SPAWN_MAX_DELAY) * 60000); // Boss spawn between 30 min and 4 hours delay on retail
	}
	
	/**
	 * This method handles the creation of a boss NPC.<br>
	 * It iterates through all available spawn templates in the base.<br>
	 * If a template matches the current race and is marked as a {@code BOSS}, it spawns the object.<br>
	 * The spawned NPC is then set as the active boss and listeners are added.
	 */
	protected void spawnBoss()
	{
		for (SpawnGroup2 group : getBaseSpawns())
		{
			for (SpawnTemplate spawn : group.getSpawnTemplates())
			{
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace()))
				{
					if ((template.getHandlerType() != null) && template.getHandlerType().equals(SpawnHandlerType.BOSS))
					{
						final Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setBoss(npc);
						addBossListeners();
					}
				}
			}
		}
	}
	
	/**
	 * Selects a random race to spawn as attackers.<br>
	 * This method iterates through the internal list of {@code Race} objects.<br>
	 * It ensures that the chosen race is not the same as the base's primary race.<br>
	 * It calls {@code spawnAttackers} for the selected race.
	 */
	protected void chooseAttackersRace()
	{
		final AtomicBoolean next = new AtomicBoolean(Math.random() < 0.5);
		for (Race race : list)
		{
			if (race == null)
			{
				throw new NullPointerException("Base:" + race + " race is null chooseAttackersRace!");
			}
			else if (!race.equals(getRace()))
			{
				if (next.compareAndSet(true, false))
				{
					continue;
				}
				
				spawnAttackers(race);
			}
		}
	}
	
	/**
	 * Spawns attacker NPCs for the base based on the provided race.<br>
	 * This method checks if the base is currently under attack before spawning.<br>
	 * It uses {@code int)} to create new units.<br>
	 * A scheduled task is created to despawn these attackers after 5 minutes.
	 * @param race The {@code Race} type of the attackers to spawn.
	 */
	public void spawnAttackers(Race race)
	{
		if ((getFlag() == null) && !isNoFlag(getId()))
		{
			throw new NullPointerException("Base:" + getId() + " flag is null!");
		}
		else if (!getFlag().getPosition().getMapRegion().isMapRegionActive())
		{
			// 20% chance to capture base in not active region by invaders assault
			final Race CurrentRace = getFlag().getRace();
			if ((Math.random() < 0.2) && !race.equals(CurrentRace))
			{
				BaseService.getInstance().capture(getId(), race);
			}
			else
			{
				// Next attack
				delayedAssault();
			}
			return;
		}
		
		if (!isAttacked())
		{
			despawnAttackers();
			
			for (SpawnGroup2 group : getBaseSpawns())
			{
				for (SpawnTemplate spawn : group.getSpawnTemplates())
				{
					final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
					if (template.getBaseRace().equals(race))
					{
						if ((template.getHandlerType() != null) && template.getHandlerType().equals(SpawnHandlerType.ATTACKER))
						{
							final Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
							getAttackers().add(npc);
						}
					}
				}
			}
			
			// Since patch 4.7 in kaldor are siege important bases that only have balaur attackers for back occupying.
			if (getAttackers().isEmpty() && !isOnlyBalaur(getId()))
			{
				throw new NullPointerException("No attackers was found for base:" + getId());
			}
			
			stopAssault = ThreadPoolManager.getInstance().schedule(() ->
			{
				despawnAttackers();
				
				// Next attack
				delayedAssault();
			}, 5 * 60000); // After 5 min attackers despawned
		}
	}
	
	/**
	 * Checks if a specific location ID contains only Balaur.<br>
	 * This method returns {@code true} for specific outpost IDs.<br>
	 * It returns {@code false} for all other IDs.
	 * @param id The unique identifier of the location to check.
	 * @return {@code true} if the ID matches a Balaur-only area, otherwise {@code false}.
	 */
	public boolean isOnlyBalaur(int id)
	{
		switch (id)
		{
			case 90: // Stonereach Outpost
				return true;
			case 91: // Flamecrest Outpost
				return true;
			case 134: // Rattlefrost Outpost
				return true;
			case 135: // Sliversleet Outpost
				return true;
			case 136: // Coldforge Outpost
				return true;
			case 137: // Shimmerfrost Outpost
				return true;
			case 138: // Icehowl Outpost
				return true;
			case 139: // Chillhaunt Outpost
				return true;
			case 140: // Wildersage Artifact Outpost
				return true;
			case 141: // Dauntless Artifact Outpost
				return true;
			case 142: // Anchorbrak Artifact Outpost
				return true;
			case 143: // Brokenblade Artifact Outpost
				return true;
			case 144: // Sootguzzle Outpost
				return true;
			case 145: // Flameruin Outpost
				return true;
			case 146: // Stokebellow Outpost
				return true;
			case 147: // Blazerack Outpost
				return true;
			case 148: // Smoldergeist Outpost
				return true;
			case 149: // Moltenspike Outpost
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if the given {@code id} belongs to a specific set of outposts.<br>
	 * It returns {@code true} for IDs 140, 141, 142, and 143.<br>
	 * It returns {@code false} for all other values.
	 * @param id The unique identifier to check.
	 * @return {@code true} if the ID matches a known outpost, otherwise {@code false}.
	 */
	public boolean isNoFlag(int id)
	{
		switch (id)
		{
			case 140: // Wildersage Artifact Outpost
				return true;
			case 141: // Dauntless Artifact Outpost
				return true;
			case 142: // Anchorbrak Artifact Outpost
				return true;
			case 143: // Brokenblade Artifact Outpost
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if any active attackers are currently alive.<br>
	 * It iterates through the list of attackers provided by {@code getAttackers}.<br>
	 * Returns {@code true} if at least one attacker is not dead.<br>
	 * Returns {@code false} if all attackers are dead or the list is empty.
	 * @return {@code true} if there is an active attacker, {@code false} otherwise.
	 */
	public boolean isAttacked()
	{
		for (Npc attacker : getAttackers())
		{
			if (!attacker.getLifeStats().isAlreadyDead())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the base and its associated entities from the game world.<br>
	 * This method clears the {@code flag}, deletes spawned NPCs, and cancels active assault tasks.<br>
	 * It also calls {@code despawnAttackers} if a stop task is present.
	 */
	protected void despawn()
	{
		setFlag(null);
		
		final List<Npc> spawned = World.getInstance().getBaseSpawns(getId());
		if (spawned != null)
		{
			for (Npc npc : spawned)
			{
				npc.getController().onDelete();
			}
		}
		
		if (startAssault != null)
		{
			startAssault.cancel(true);
		}
		
		if (stopAssault != null)
		{
			stopAssault.cancel(true);
			despawnAttackers();
		}
	}
	
	/**
	 * Removes all current attackers from the base.<br>
	 * This method cancels the {@code RESPAWN} task for each attacker.<br>
	 * It then calls {@code onDelete()} on their controllers and clears the attacker list.
	 */
	protected void despawnAttackers()
	{
		NpcController controller;
		for (Npc attacker : getAttackers())
		{
			controller = attacker.getController();
			if (null != controller)// despawn fix
			{
				controller.cancelTask(TaskId.RESPAWN);
				controller.onDelete();
			}
		}
		
		getAttackers().clear();
	}
	
	/**
	 * Registers the boss death handler on the boss entity.<br>
	 * This method retrieves the {@code AbstractAI} from the boss.<br>
	 * It wires {@code onBossDied} to run before the boss death is handled.
	 */
	protected void addBossListeners()
	{
		((AbstractAI) getBoss().getAi2()).setOnDeathBefore(this::onBossDied);
	}
	
	/**
	 * Removes the boss death listener from the boss entity.<br>
	 * This method clears the death callback from the boss's AI.
	 */
	protected void rmvBossListener()
	{
		if (getBoss() != null)
		{
			((AbstractAI) getBoss().getAi2()).setOnDeathBefore(null);
		}
	}
	
	/**
	 * Handles the base capture logic that occurs when the base boss dies.<br>
	 * This runs before {@code handleDied()} so the boss aggro list is still intact for {@code getMostDamage()}.<br>
	 * It determines the winning party, applies base buffs, grants rewards, announces the capture, and updates the base race.
	 */
	private void onBossDied()
	{
		final AionObject winner = getBoss().getAggroList().getMostDamage();
		final Npc boss = getBoss();
		Race race = null;
		
		if (winner instanceof Creature)
		{
			final Creature kill = (Creature) winner;
			applyBaseBuff();
			if (kill.getRace().isPlayerRace())
			{
				setRace(kill.getRace());
				race = kill.getRace();
				
				if (BaseConfig.ENABLE_BASE_REWARDS)
				{
					if (kill instanceof Player)
					{
						giveBaseRewardsToPlayers((Player) kill);
					}
				}
			}
			
			announceCapture(null, kill);
		}
		else if (winner instanceof TemporaryPlayerTeam)
		{
			final TemporaryPlayerTeam<?> team = (TemporaryPlayerTeam<?>) winner;
			applyBaseBuff();
			if (team.getRace().isPlayerRace())
			{
				setRace(team.getRace());
				race = team.getRace();
			}
			
			announceCapture(team, null);
		}
		else
		{
			setRace(Race.NPC);
		}
		
		BaseService.getInstance().capture(getId(), getRace());
		log.info("Legat kill ! BOSS: " + boss + " in BaseId: " + getBaseLocation().getId() + " killed by RACE: " + race);
	}
	
	/**
	 * Sends a system message to all players regarding a capture.<br>
	 * This method announces which team or creature successfully conquered a base.
	 * @param team The {@link TemporaryPlayerTeam} that won the capture, or {@code null}.
	 * @param kill The {@link Creature} that won the capture, or {@code null}.
	 */
	private void announceCapture(TemporaryPlayerTeam<?> team, Creature kill)
	{
		final String baseName = getBaseLocation().getName();
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if ((team != null) && (kill == null))
				{
					// %0 succeeded in conquering %1
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301039, team.getRace().getRaceDescriptionId(), baseName));
				}
				else
				{
					// %0 succeeded in conquering %1
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1301039, kill.getRace().getRaceDescriptionId(), baseName));
				}
			}
		});
	}
	
	/**
	 * Applies a base buff to all players in the world.<br>
	 * This method checks the {@link Race} of each player.<br>
	 * It grants specific buffs based on whether the player is an Elyos or Asmodian.
	 */
	private void applyBaseBuff()
	{
		World.getInstance().doOnAllPlayers(new Visitor<Player>()
		{
			@Override
			public void visit(Player player)
			{
				if (player.getCommonData().getRace() == Race.ELYOS)
				{
					SkillEngine.getInstance().applyEffectDirectly(12115, player, player, 0); // Kaisinel's Bane
					// The power of Kaisinel's Protection surrounds you
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_WEAK_RACE_BUFF_LIGHT_GAIN, 10000);
				}
				else if (player.getCommonData().getRace() == Race.ASMODIANS)
				{
					SkillEngine.getInstance().applyEffectDirectly(12117, player, player, 0); // Marchutan's Bane
					// The power of Marchutan's Protection surrounds you
					PacketSendUtility.playerSendPacketTime(player, SM_SYSTEM_MESSAGE.STR_MSG_WEAK_RACE_BUFF_DARK_GAIN, 10000);
				}
			}
		});
	}
	
	/**
	 * Sends a specific guide message to the player based on their current world.<br>
	 * This method checks the {@code worldId} of the provided {@link Player}.<br>
	 * It uses {@code sendGuideHtml} to display the correct content.
	 * @param player The {@code Player} who will receive the reward message.
	 */
	private void giveBaseRewardsToPlayers(Player player)
	{
		switch (player.getWorldId())
		{
			case 210020000: // Eltnen
			case 210040000: // Heiron
			case 220020000: // Morheim
			case 220040000: // Beluslan
				HTMLService.sendGuideHtml(player, "adventurers_base1");
				break;
			case 600090000: // Kaldor
			case 600100000: // Levinshor
				HTMLService.sendGuideHtml(player, "adventurers_base2");
				break;
			case 400020000: // Belus
			case 400040000: // Aspida
			case 400050000: // Atanatos
			case 400060000: // Disillon
				HTMLService.sendGuideHtml(player, "adventurers_base3");
				break;
		}
	}
	
	/**
	 * Retrieves the current {@link Npc} object representing the flag.
	 * @return The {@code Npc} instance of the flag, or {@code null} if none exists.
	 */
	public Npc getFlag()
	{
		return flag;
	}
	
	/**
	 * Sets the {@code flag} for this base.<br>
	 * This method updates the internal reference to an {@link Npc} object.
	 * @param flag The {@code Npc} instance to set as the flag.
	 */
	public void setFlag(Npc flag)
	{
		this.flag = flag;
	}
	
	/**
	 * Retrieves the current boss {@link Npc} for this base.
	 * @return The {@code Npc} object representing the boss, or {@code null} if no boss exists.
	 */
	public Npc getBoss()
	{
		return boss;
	}
	
	/**
	 * Sets the current {@link Npc} for the boss.<br>
	 * This method updates the internal boss reference.
	 * @param boss The {@code Npc} object to set as the boss.
	 */
	public void setBoss(Npc boss)
	{
		this.boss = boss;
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
	 * Retrieves the primary location associated with this {@code Base}.<br>
	 * This method returns the internal {@code baseLocation} field.
	 * @return The {@code BL} object representing the base location.
	 */
	public BL getBaseLocation()
	{
		return baseLocation;
	}
	
	/**
	 * Retrieves the unique identifier for this {@link Base}.<br>
	 * This value is obtained from the underlying {@code baseLocation}.
	 * @return The integer ID of the location.
	 */
	public int getId()
	{
		return baseLocation.getId();
	}
	
	/**
	 * Retrieves the {@code Race} associated with the base location.<br>
	 * This method returns the race of the current {@code BL}.
	 * @return The {@link Race} of the base location.
	 */
	public Race getRace()
	{
		return baseLocation.getRace();
	}
	
	/**
	 * Sets the {@code race} for this location.<br>
	 * This updates the internal {@code race} field.
	 * @param race The new {@link Race} to assign.
	 */
	public void setRace(Race race)
	{
		baseLocation.setRace(race);
	}
	
	/**
	 * Retrieves the list of NPCs currently attacking.
	 * @return A {@code List} of {@link Npc} objects that are attacking.
	 */
	public List<Npc> getAttackers()
	{
		return attackers;
	}
}
