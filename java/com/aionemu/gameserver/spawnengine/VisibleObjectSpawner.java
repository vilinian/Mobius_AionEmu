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
package com.aionemu.gameserver.spawnengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.controllers.GatherableController;
import com.aionemu.gameserver.controllers.MinionController;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.PetController;
import com.aionemu.gameserver.controllers.SiegeWeaponController;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.NpcData;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.base.BaseLocation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.GroupGate;
import com.aionemu.gameserver.model.gameobjects.Homing;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.NpcObjectType;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Servant;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.SummonedHouseNpc;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.MinionCommonData;
import com.aionemu.gameserver.model.gameobjects.player.PetCommonData;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.siege.SiegeNpc;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.rift.RiftLocation;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.VisibleObjectTemplate;
import com.aionemu.gameserver.model.templates.minion.MinionTemplate;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.pet.PetTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.riftspawns.RiftSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.vortexspawns.VortexSpawnTemplate;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.skillengine.effect.SummonOwner;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.CreatureAwareKnownList;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;
import com.aionemu.gameserver.world.knownlist.PlayerAwareKnownList;

/**
 * This class handles the spawning of {@link VisibleObject} entities within the game world.<br>
 * It manages the creation and placement of various objects based on defined spawn templates. It ensures that new objects are correctly registered with the appropriate controllers and services.
 * @author ATracer
 */
public class VisibleObjectSpawner
{
	private static final Logger log = LoggerFactory.getLogger(VisibleObjectSpawner.class);
	
	/**
	 * Creates and adds a new NPC to the game world.<br>
	 * This method retrieves the template data and initializes the {@link Npc} object.<br>
	 * It handles the placement of the NPC into the specific world instance.
	 * @param spawn The configuration data for the NPC spawn location and properties.
	 * @param instanceIndex The index of the world instance where the NPC will be placed.
	 * @return The created {@link VisibleObject} if successful, or {@code null} if the template is missing.
	 */
	protected static VisibleObject spawnNpc(SpawnTemplate spawn, int instanceIndex)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null)
		{
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		
		final IDFactory iDFactory = IDFactory.getInstance();
		final Npc npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
		npc.setCreatorId(spawn.getCreatorId());
		npc.setMasterName(spawn.getMasterName());
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		
		if (WalkerFormator.processClusteredNpc(npc, spawn.getWorldId(), instanceIndex))
		{
			return npc;
		}
		
		try
		{
			SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		}
		catch (Exception ex)
		{
			log.error("Error during spawn of npc {}, world {}, x-y {}-{}", new Object[]
			{
				npcTemplate.getTemplateId(),
				spawn.getWorldId(),
				spawn.getX(),
				spawn.getY()
			});
			log.error("Npc {} will be despawned", npcTemplate.getTemplateId(), ex);
			World.getInstance().despawn(npc);
		}
		
		return npc;
	}
	
	/**
	 * Creates and spawns a new {@link SummonedHouseNpc} into the game world.<br>
	 * This method initializes the NPC with the provided template and owner details.<br>
	 * It then registers the NPC in the world using the specified instance index.
	 * @param spawn The {@code SpawnTemplate} containing the NPC data.
	 * @param instanceIndex The index of the current map instance.
	 * @param creator The {@link House} that owns or created this NPC.
	 * @param masterName The name of the house master associated with the NPC.
	 * @return The newly created {@code SummonedHouseNpc} object.
	 */
	public static SummonedHouseNpc spawnHouseNpc(SpawnTemplate spawn, int instanceIndex, House creator, String masterName)
	{
		final int npcId = spawn.getNpcId();
		final NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		final SummonedHouseNpc npc = new SummonedHouseNpc(IDFactory.getInstance().nextId(), new NpcController(), spawn, template, creator, masterName);
		npc.setKnownlist(new PlayerAwareKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	/**
	 * Creates and initializes a base NPC into the game world.<br>
	 * This method validates the template data and checks if the spawn is active.<br>
	 * It also verifies that the NPC race matches the base location if no specific handler is set.
	 * @param spawn The {@code BaseSpawnTemplate} containing the NPC configuration.
	 * @param instanceIndex The index of the world instance where the NPC will be spawned.
	 * @return The created {@link VisibleObject} if successful, or {@code null} if any validation fails.
	 */
	protected static VisibleObject spawnBaseNpc(BaseSpawnTemplate spawn, int instanceIndex)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		
		if (npcTemplate == null)
		{
			log.error("No template for Base NPC " + String.valueOf(objectId));
			return null;
		}
		
		final boolean isActive = BaseService.getInstance().isActive(spawn.getId());
		if (!isActive)
		{
			return null;
		}
		
		// Chk owner race for non handled spawn
		final BaseLocation base = BaseService.getInstance().getBaseLocation(spawn.getId());
		if ((spawn.getHandlerType() == null) && !spawn.getBaseRace().equals(base.getRace()))
		{
			return null;
		}
		
		final IDFactory iDFactory = IDFactory.getInstance();
		final Npc npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
		
		npc.setKnownlist(new NpcKnownList(npc));
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		
		return npc;
	}
	
	/**
	 * Spawns an NPC specifically for a Rift location.<br>
	 * This method checks if Rifts are enabled in the {@code CustomConfig}.<br>
	 * It verifies that the Rift is open before creating the {@link Npc}.
	 * @param spawn The {@code RiftSpawnTemplate} containing the NPC data.
	 * @param instanceIndex The index of the current world instance.
	 * @return The spawned {@code VisibleObject} or {@code null} if spawning fails.
	 */
	protected static VisibleObject spawnRiftNpc(RiftSpawnTemplate spawn, int instanceIndex)
	{
		if (!CustomConfig.RIFT_ENABLED)
		{
			return null;
		}
		
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null)
		{
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		
		final IDFactory iDFactory = IDFactory.getInstance();
		Npc npc;
		
		final int spawnId = spawn.getId();
		final RiftLocation loc = RiftService.getInstance().getRiftLocation(spawnId);
		if (loc.isOpened() && (spawnId == loc.getId()))
		{
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else
		{
			return null;
		}
		
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	/**
	 * Creates and spawns a siege NPC based on the provided template.<br>
	 * This method checks if siege is enabled in {@link SiegeConfig}.<br>
	 * It validates the location and race before bringing the NPC into the world.
	 * @param spawn The {@code SiegeSpawnTemplate} containing the NPC data.
	 * @param instanceIndex The index of the current game instance.
	 * @return The spawned {@link VisibleObject} or {@code null} if conditions are not met.
	 */
	protected static VisibleObject spawnSiegeNpc(SiegeSpawnTemplate spawn, int instanceIndex)
	{
		if (!SiegeConfig.SIEGE_ENABLED)
		{
			return null;
		}
		
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null)
		{
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		
		final IDFactory iDFactory = IDFactory.getInstance();
		Npc npc = null;
		
		final int spawnSiegeId = spawn.getSiegeId();
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(spawnSiegeId);
		if ((spawn.isPeace() || loc.isVulnerable()) && (spawnSiegeId == loc.getLocationId()) && (spawn.getSiegeRace() == loc.getRace()))
		{
			// default: GUARD
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else if (spawn.isAssault() && loc.isVulnerable() && spawn.getSiegeRace().equals(SiegeRace.BALAUR))
		{
			// attakers
			npc = new SiegeNpc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else
		{
			return null;
		}
		
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	/**
	 * Spawns an NPC specifically for a vortex invasion.<br>
	 * This method checks if the vortex is active or peaceful before creating the {@code Npc}.<br>
	 * It returns {@code null} if the configuration is disabled or the location is invalid.
	 * @param spawn The {@link VortexSpawnTemplate} containing the NPC data and location.
	 * @param instanceIndex The index of the current world instance.
	 * @return The spawned {@link VisibleObject} or {@code null} if spawning failed.
	 */
	protected static VisibleObject spawnInvasionNpc(VortexSpawnTemplate spawn, int instanceIndex)
	{
		if (!CustomConfig.VORTEX_ENABLED)
		{
			return null;
		}
		
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		if (npcTemplate == null)
		{
			log.error("No template for NPC " + String.valueOf(objectId));
			return null;
		}
		
		final IDFactory iDFactory = IDFactory.getInstance();
		Npc npc;
		
		final int spawnId = spawn.getId();
		final VortexLocation loc = VortexService.getInstance().getVortexLocation(spawnId);
		if (loc.isActive() && (spawnId == loc.getId()) && spawn.isInvasion())
		{
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else if (!loc.isActive() && (spawnId == loc.getId()) && spawn.isPeace())
		{
			npc = new Npc(iDFactory.nextId(), new NpcController(), spawn, npcTemplate);
			npc.setKnownlist(new NpcKnownList(npc));
		}
		else
		{
			return null;
		}
		
		npc.setEffectController(new EffectController(npc));
		SpawnEngine.bringIntoWorld(npc, spawn, instanceIndex);
		return npc;
	}
	
	/**
	 * Creates and adds a new gatherable object to the game world.<br>
	 * This method uses the provided {@code SpawnTemplate} to initialize the object.<br>
	 * It handles data retrieval and registration via {@code bringIntoWorld}.
	 * @param spawn The template containing the data for the object to be created.
	 * @param instanceIndex The index of the world instance where the object will appear.
	 * @return The newly created {@code VisibleObject} instance.
	 */
	protected static VisibleObject spawnGatherable(SpawnTemplate spawn, int instanceIndex)
	{
		final int objectId = spawn.getNpcId();
		final VisibleObjectTemplate template = DataManager.GATHERABLE_DATA.getGatherableTemplate(objectId);
		final Gatherable gatherable = new Gatherable(spawn, template, IDFactory.getInstance().nextId(), new GatherableController());
		gatherable.setKnownlist(new PlayerAwareKnownList(gatherable));
		SpawnEngine.bringIntoWorld(gatherable, spawn, instanceIndex);
		return gatherable;
	}
	
	/**
	 * Creates and spawns a {@link Trap} object into the game world.<br>
	 * This method initializes the trap with specific skills and visual states.<br>
	 * It then handles the physical placement and network synchronization of the trap.
	 * @param spawn The template containing the data for the trap to be created.
	 * @param instanceIndex The index used to identify the specific world instance.
	 * @param creator The {@link Creature} that placed or triggered the trap.
	 * @param skillId The unique identifier for the skill associated with this trap.
	 * @return The newly created and spawned {@link Trap} object.
	 */
	public static Trap spawnTrap(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		final Trap trap = new Trap(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		trap.setKnownlist(new NpcKnownList(trap));
		trap.setEffectController(new EffectController(trap));
		trap.setCreator(creator);
		trap.getSkillList().addSkill(trap, skillId, 1);
		if ((objectId != 749300) || (objectId != 749300))
		{
			trap.setVisualState(CreatureVisualState.HIDE1);
		}
		
		try
		{
			trap.getAi2().onCustomEvent(1, DataManager.SKILL_DATA.getSkillTemplate(skillId).getProperties().getEffectiveRange());
		}
		catch (Exception e)
		{
			trap.getAi2().onCustomEvent(1, creator);
		}
		
		SpawnEngine.bringIntoWorld(trap, spawn, instanceIndex);
		PacketSendUtility.broadcastPacket(trap, new SM_PLAYER_STATE(trap));
		return trap;
	}
	
	/**
	 * Creates and spawns a new {@link GroupGate} object into the game world.<br>
	 * This method initializes the gate using the provided template and creator information.<br>
	 * It handles all necessary setup before bringing the object into the world.
	 * @param spawn The {@code SpawnTemplate} containing the data for the gate.
	 * @param instanceIndex The index of the current map instance.
	 * @param creator The {@link Creature} that triggered the creation of this gate.
	 * @return The newly created {@link GroupGate} object.
	 */
	public static GroupGate spawnGroupGate(SpawnTemplate spawn, int instanceIndex, Creature creator)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		final GroupGate groupgate = new GroupGate(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		groupgate.setKnownlist(new PlayerAwareKnownList(groupgate));
		groupgate.setEffectController(new EffectController(groupgate));
		groupgate.setCreator(creator);
		SpawnEngine.bringIntoWorld(groupgate, spawn, instanceIndex);
		return groupgate;
	}
	
	/**
	 * Creates and spawns a new {@link Kisk} object into the game world.<br>
	 * This method initializes the {@code Kisk} with the provided template and creator.<br>
	 * It then registers the object in the current instance.
	 * @param spawn The {@code SpawnTemplate} containing the data for the new kisk.
	 * @param instanceIndex The index of the world instance where the kisk will appear.
	 * @param creator The {@link Player} who is responsible for creating this kisk.
	 * @return The newly created and spawned {@code Kisk} object.
	 */
	public static Kisk spawnKisk(SpawnTemplate spawn, int instanceIndex, Player creator)
	{
		final int npcId = spawn.getNpcId();
		final NpcTemplate template = DataManager.NPC_DATA.getNpcTemplate(npcId);
		final Kisk kisk = new Kisk(IDFactory.getInstance().nextId(), new NpcController(), spawn, template, creator);
		kisk.setKnownlist(new PlayerAwareKnownList(kisk));
		kisk.setCreator(creator);
		kisk.setEffectController(new EffectController(kisk));
		SpawnEngine.bringIntoWorld(kisk, spawn, instanceIndex);
		return kisk;
	}
	
	/**
	 * Spawns a postman NPC for a specific player.<br>
	 * The NPC is placed in front of the {@code owner}.<br>
	 * It automatically sets the correct race based on the player's faction.
	 * @param owner The {@link Player} who will own the spawned postman.
	 * @return The newly created {@link Npc} object.
	 */
	public static Npc spawnPostman(Player owner)
	{
		final int npcId = owner.getRace() == Race.ELYOS ? 798100 : 798101;
		final NpcData npcData = DataManager.NPC_DATA;
		final NpcTemplate template = npcData.getNpcTemplate(npcId);
		final IDFactory iDFactory = IDFactory.getInstance();
		final int worldId = owner.getWorldId();
		final int instanceId = owner.getInstanceId();
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(owner.getHeading()));
		final Vector3f pos = GeoService.getInstance().getClosestCollision(owner, owner.getX() + (float) (Math.cos(radian) * 5), owner.getY() + (float) (Math.sin(radian) * 5), owner.getZ(), false, CollisionIntention.PHYSICAL.getId());
		final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
		final Npc postman = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		postman.setKnownlist(new PlayerAwareKnownList(postman));
		postman.setEffectController(new EffectController(postman));
		postman.getAi2().onCustomEvent(1, owner);
		SpawnEngine.bringIntoWorld(postman, spawn, instanceId);
		owner.setPostman(postman);
		return postman;
	}
	
	/**
	 * Spawns a functional {@link Npc} for a specific player.<br>
	 * This method calculates the correct position based on the owner's heading.<br>
	 * It initializes the NPC with necessary controllers and adds it to the world.
	 * @param owner The {@link Player} who triggers the spawn.
	 * @param npcId The unique identifier for the NPC template.
	 * @param summonOwner The {@link SummonOwner} associated with the summon.
	 * @return The newly created {@link Npc} object.
	 */
	public static Npc spawnFunctionalNpc(Player owner, int npcId, SummonOwner summonOwner)
	{
		final NpcData npcData = DataManager.NPC_DATA;
		final NpcTemplate template = npcData.getNpcTemplate(npcId);
		final IDFactory iDFactory = IDFactory.getInstance();
		final int worldId = owner.getWorldId();
		final int instanceId = owner.getInstanceId();
		final double radian = Math.toRadians(MathUtil.convertHeadingToDegree(owner.getHeading()));
		final Vector3f pos = GeoService.getInstance().getClosestCollision(owner, owner.getX() + (float) (Math.cos(radian) * 1), owner.getY() + (float) (Math.sin(radian) * 1), owner.getZ(), false, CollisionIntention.PHYSICAL.getId());
		final SpawnTemplate spawn = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, pos.getX(), pos.getY(), pos.getZ(), (byte) 0);
		final Npc functionalNpc = new Npc(iDFactory.nextId(), new NpcController(), spawn, template);
		functionalNpc.setKnownlist(new PlayerAwareKnownList(functionalNpc));
		functionalNpc.setEffectController(new EffectController(functionalNpc));
		functionalNpc.getAi2().onCustomEvent(1, owner);
		SpawnEngine.bringIntoWorld(functionalNpc, spawn, instanceId);
		return functionalNpc;
	}
	
	/**
	 * Creates and spawns a new {@link Servant} into the game world.<br>
	 * This method initializes the servant based on a provided template and skill data.<br>
	 * It handles level calculation, effect setup, and initial health conditions.
	 * @param spawn The {@link SpawnTemplate} containing the NPC ID and location data.
	 * @param instanceIndex The index of the world instance where the servant will appear.
	 * @param creator The {@link Creature} that is summoning or creating the servant.
	 * @param skillId The unique identifier for the skill used to spawn the servant.
	 * @param level The base level provided for the spawning action.
	 * @param objectType The specific {@link NpcObjectType} to assign to the new servant.
	 * @return The newly created and spawned {@link Servant} object.
	 */
	public static Servant spawnServant(SpawnTemplate spawn, int instanceIndex, Creature creator, int skillId, int level, NpcObjectType objectType)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		final int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		final byte servantLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);
		
		final Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, servantLevel);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(objectType);
		servant.getSkillList().addSkill(servant, skillId, 1);
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		final SkillTemplate st = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if ((st.getStartconditions() != null) && (st.getHpCondition() != null))
		{
			final int hp = (st.getHpCondition().getHpValue() * 3);
			servant.getLifeStats().setCurrentHp(hp);
		}
		
		return servant;
	}
	
	/**
	 * Creates and spawns a new {@link Servant} into the game world.<br>
	 * This method initializes the servant with the correct level and creator data.<br>
	 * It then registers the servant in the current instance.
	 * @param spawn The template containing the NPC data for the servant.
	 * @param instanceIndex The index of the map instance where the servant will appear.
	 * @param creator The {@link Creature} that is summoning or creating the servant.
	 * @param servantLvl The level to assign to the new servant.
	 * @return The newly created {@link Servant} object.
	 */
	public static Servant spawnEnemyServant(SpawnTemplate spawn, int instanceIndex, Creature creator, byte servantLvl)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		final Servant servant = new Servant(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, servantLvl);
		servant.setKnownlist(new NpcKnownList(servant));
		servant.setEffectController(new EffectController(servant));
		servant.setCreator(creator);
		servant.setNpcObjectType(NpcObjectType.SERVANT);
		SpawnEngine.bringIntoWorld(servant, spawn, instanceIndex);
		return servant;
	}
	
	/**
	 * Creates and spawns a new {@link Homing} object into the game world.<br>
	 * This method initializes the homing properties based on the provided template and creator.<br>
	 * It also handles skill assignment and brings the object into the current instance.
	 * @param spawn The {@link SpawnTemplate} containing the base data for the homing.
	 * @param instanceIndex The index of the world instance where the object will be spawned.
	 * @param creator The {@link Creature} that is creating or summoning the homing.
	 * @param attackCount The number of attacks the homing is allowed to perform.
	 * @param skillId The primary skill ID used for calculations.
	 * @param level The base level used to determine skill requirements.
	 * @param homingSkillId The specific skill ID to be assigned to the homing object.
	 * @return The newly created {@link Homing} instance.
	 */
	public static Homing spawnHoming(SpawnTemplate spawn, int instanceIndex, Creature creator, int attackCount, int skillId, int level, int homingSkillId)
	{
		final int objectId = spawn.getNpcId();
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		final int creatureLevel = creator.getLevel();
		level = SkillLearnService.getSkillLearnLevel(skillId, creatureLevel, level);
		final byte homingLevel = (byte) SkillLearnService.getSkillMinLevel(skillId, creatureLevel, level);
		final Homing homing = new Homing(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate, homingLevel, skillId);
		homing.setState(CreatureState.WEAPON_EQUIPPED);
		homing.setKnownlist(new NpcKnownList(homing));
		homing.setEffectController(new EffectController(homing));
		homing.setCreator(creator);
		if (homingSkillId != 0)
		{
			homing.getSkillList().addSkill(homing, homingSkillId, 1);
		}
		
		homing.setActiveSkillId(homingSkillId);
		homing.setAttackCount(attackCount);
		SpawnEngine.bringIntoWorld(homing, spawn, instanceIndex);
		return homing;
	}
	
	/**
	 * Creates and spawns a new {@link Summon} object in the game world.<br>
	 * This method calculates the correct position based on the creator's location.<br>
	 * It also determines the appropriate skill level for the summon.
	 * @param creator The {@link Player} who is summoning the creature.
	 * @param npcId The unique identifier for the NPC type to spawn.
	 * @param skillId The ID of the skill used to perform the summon.
	 * @param skillLevel The requested level of the skill.
	 * @param time The duration for which the summon should exist.
	 * @return The newly created {@link Summon} instance.
	 */
	public static Summon spawnSummon(Player creator, int npcId, int skillId, int skillLevel, int time)
	{
		final float x = creator.getX() - 2;
		final float y = creator.getY();
		final float z = creator.getZ();
		final byte heading = creator.getHeading();
		final int worldId = creator.getWorldId();
		final int instanceId = creator.getInstanceId();
		
		final SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, npcId, x, y, z, heading);
		final NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(npcId);
		
		skillLevel = SkillLearnService.getSkillLearnLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		final byte level = (byte) SkillLearnService.getSkillMinLevel(skillId, creator.getCommonData().getLevel(), skillLevel);
		final boolean isSiegeWeapon = npcTemplate.getAi().equals("siege_weapon");
		final Summon summon = new Summon(IDFactory.getInstance().nextId(), isSiegeWeapon ? new SiegeWeaponController(npcId) : new SummonController(), spawn, npcTemplate, isSiegeWeapon ? npcTemplate.getLevel() : level, time);
		summon.setKnownlist(new CreatureAwareKnownList(summon));
		summon.setEffectController(new EffectController(summon));
		summon.setMaster(creator);
		summon.getLifeStats().synchronizeWithMaxStats();
		
		SpawnEngine.bringIntoWorld(summon, spawn, instanceId);
		return summon;
	}
	
	/**
	 * Creates and spawns a new {@link Pet} for a specific player.<br>
	 * This method retrieves the pet data based on the provided ID.<br>
	 * It then initializes the pet object and places it in the game world.
	 * @param player The {@link Player} who will own the new pet.
	 * @param petId The unique identifier for the pet type to spawn.
	 * @return The newly created {@link Pet} object, or {@code null} if the data is invalid.
	 */
	public static Pet spawnPet(Player player, int petId)
	{
		final PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (petCommonData == null)
		{
			return null;
		}
		
		final PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		if (petTemplate == null)
		{
			return null;
		}
		
		final PetController controller = new PetController();
		final Pet pet = new Pet(petTemplate, controller, petCommonData, player);
		pet.setKnownlist(new PlayerAwareKnownList(pet));
		player.setToyPet(pet);
		
		final float x = player.getX() - 2;
		final float y = player.getY();
		final float z = player.getZ();
		final byte heading = player.getHeading();
		final int worldId = player.getWorldId();
		final int instanceId = player.getInstanceId();
		final SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, petId, x, y, z, heading);
		
		SpawnEngine.bringIntoWorld(pet, spawn, instanceId);
		return pet;
	}
	
	/**
	 * Creates and spawns a new {@link Minion} for a specific player.<br>
	 * This method validates the data before bringing the minion into the game world.
	 * @param player The {@link Player} who will own the new minion.
	 * @param minionObjId The unique object identifier for the minion template.
	 * @param minionId The specific ID of the minion from the player's list.
	 * @return The created {@link Minion} object, or {@code null} if data is missing.
	 */
	public static Minion spawnMinion(Player player, int minionObjId, int minionId)
	{
		final MinionCommonData mcd = player.getMinionList().getMinion(minionId);
		if (mcd == null)
		{
			return null;
		}
		
		final MinionTemplate mt = DataManager.MINION_DATA.getMinionTemplate(minionObjId);
		if (mt == null)
		{
			return null;
		}
		
		final MinionController controller = new MinionController();
		final Minion minion = new Minion(mt, controller, mcd, player);
		minion.setKnownlist(new PlayerAwareKnownList(minion));
		player.setMinion(minion);
		
		final float x = player.getX();
		final float y = player.getY();
		final float z = player.getZ();
		final byte heading = player.getHeading();
		final int worldId = player.getWorldId();
		final int instanceId = player.getInstanceId();
		final SpawnTemplate spawn = SpawnEngine.createSpawnTemplate(worldId, minionObjId, x, y, z, heading);
		
		SpawnEngine.bringIntoWorld(minion, spawn, instanceId);
		return minion;
	}
}
