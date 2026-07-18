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
package com.aionemu.gameserver.model.gameobjects.player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.FlyController;
import com.aionemu.gameserver.controllers.PlayerController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.PlayerAggroList;
import com.aionemu.gameserver.controllers.effect.PlayerEffectController;
import com.aionemu.gameserver.controllers.movement.PlayerMoveController;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dao.PlayerVarsDAO;
import com.aionemu.gameserver.dao.PlayerWorldBanDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Gender;
import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.account.Account;
import com.aionemu.gameserver.model.account.AccountTransfo;
import com.aionemu.gameserver.model.account.AccountTransformList;
import com.aionemu.gameserver.model.account.TransformCollection;
import com.aionemu.gameserver.model.actions.PlayerActions;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.cubics.PlayerMCList;
import com.aionemu.gameserver.model.dorinerk_wardrobe.PlayerWardrobeList;
import com.aionemu.gameserver.model.event_window.PlayerEventWindowList;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.CreatureType;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Minion;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.PersistentState;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.SummonedObject;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank.AbyssRankUpdateType;
import com.aionemu.gameserver.model.gameobjects.player.FriendList.Status;
import com.aionemu.gameserver.model.gameobjects.player.achievement.PlayerAchievement;
import com.aionemu.gameserver.model.gameobjects.player.collection.PlayerCollection;
import com.aionemu.gameserver.model.gameobjects.player.emotion.EmotionList;
import com.aionemu.gameserver.model.gameobjects.player.equipmentsetting.EquipmentSettingList;
import com.aionemu.gameserver.model.gameobjects.player.f2p.F2p;
import com.aionemu.gameserver.model.gameobjects.player.fame.PlayerFame;
import com.aionemu.gameserver.model.gameobjects.player.motion.MotionList;
import com.aionemu.gameserver.model.gameobjects.player.npcFaction.NpcFactions;
import com.aionemu.gameserver.model.gameobjects.player.ranking.ArenaOfCooperationRank;
import com.aionemu.gameserver.model.gameobjects.player.ranking.ArenaOfDisciplineRank;
import com.aionemu.gameserver.model.gameobjects.player.title.TitleList;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.house.HouseRegistry;
import com.aionemu.gameserver.model.house.HouseStatus;
import com.aionemu.gameserver.model.items.ItemCooldown;
import com.aionemu.gameserver.model.items.storage.IStorage;
import com.aionemu.gameserver.model.items.storage.LegionStorageProxy;
import com.aionemu.gameserver.model.items.storage.Storage;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.model.skill.PlayerSkillList;
import com.aionemu.gameserver.model.skinskill.SkillSkinList;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.stats.container.PlayerLifeStats;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.model.team.legion.LegionJoinRequestState;
import com.aionemu.gameserver.model.team.legion.LegionMember;
import com.aionemu.gameserver.model.team2.TeamMember;
import com.aionemu.gameserver.model.team2.TemporaryPlayerTeam;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceGroup;
import com.aionemu.gameserver.model.team2.common.legacy.LootGroupRules;
import com.aionemu.gameserver.model.templates.BoundRadius;
import com.aionemu.gameserver.model.templates.event.MaxCountOfDay;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.templates.ride.RideInfo;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamPath;
import com.aionemu.gameserver.model.templates.zone.ZoneType;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;
import com.aionemu.gameserver.services.HousingService;
import com.aionemu.gameserver.skillengine.condition.ChainCondition;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.effect.EffectTemplate;
import com.aionemu.gameserver.skillengine.effect.RebirthEffect;
import com.aionemu.gameserver.skillengine.effect.ResurrectBaseEffect;
import com.aionemu.gameserver.skillengine.model.ChainSkills;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.task.CraftingTask;
import com.aionemu.gameserver.utils.HumanTime;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.rates.Rates;
import com.aionemu.gameserver.utils.rates.RegularRates;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * This class represents a {@code Player} object in the game world.<br>
 * It contains all necessary data and state information for an individual player character.
 * @author -Nemesiss-
 * @author SoulKeeper
 * @author alexa026
 * @author cura
 * @author GiGatR00n v4.7.5.x
 */
public class Player extends Creature
{
	public RideInfo ride;
	public InRoll inRoll;
	public WindstreamPath windstreamPath;
	private PlayerAppearance playerAppearance;
	private PlayerAppearance savedPlayerAppearance;
	private final PlayerCommonData playerCommonData;
	private final Account playerAccount;
	private LegionMember legionMember;
	private MacroList macroList;
	private PlayerSkillList skillList;
	private FriendList friendList;
	private BlockList blockList;
	private PetList toyPetList;
	private MinionList minionList;
	private Mailbox mailbox;
	private PrivateStore store;
	private TitleList titleList;
	private QuestStateList questStateList;
	private RecipeList recipeList;
	private List<House> houses;
	private ResponseRequester requester;
	private boolean lookingForGroup = false;
	private boolean lookingForEvent = false;
	private Storage inventory;
	private final Storage[] petBag = new Storage[(StorageType.PET_BAG_MAX - StorageType.PET_BAG_MIN) + 1];
	private final Storage[] cabinets = new Storage[(StorageType.HOUSE_WH_MAX - StorageType.HOUSE_WH_MIN) + 1];
	private Storage regularWarehouse;
	private Storage accountWarehouse;
	private Equipment equipment;
	private HouseRegistry houseRegistry;
	private PlayerStatsTemplate playerStatsTemplate;
	private PlayerSettings playerSettings;
	private com.aionemu.gameserver.model.team2.group.PlayerGroup playerGroup2;
	private PlayerAllianceGroup playerAllianceGroup;
	private final AbsoluteStatOwner absStatsHolder;
	private AbyssRank abyssRank;
	private NpcFactions npcFactions;
	private Rates rates;
	private int flyState = 0;
	private boolean isTrading;
	private long prisonTimer = 0;
	private long startPrison;
	private boolean invul;
	private FlyController flyController;
	private CraftingTask craftingTask;
	private int flightTeleportId;
	private int flightDistance;
	private Summon summon;
	private SummonedObject<?> summonedObj;
	private Pet toyPet;
	private Minion minion;
	private Kisk kisk;
	private boolean isResByPlayer = false;
	private int resurrectionSkill = 0;
	private boolean isFlyingBeforeDeath = false;
	private boolean isGagged = false;
	private boolean edit_mode = false;
	private Npc postman = null;
	private boolean isInResurrectPosState = false;
	private float resPosX = 0;
	private float resPosY = 0;
	private float resPosZ = 0;
	private boolean underNoFPConsum = false;
	private boolean isAdminTeleportation = false;
	private boolean cooldownZero = false;
	private boolean isUnderInvulnerableWing = false;
	private boolean isFlying = false;
	private boolean isWispable = true;
	private boolean isCommandUsed = false;
	private int abyssRankListUpdateMask = 0;
	private BindPointPosition bindPoint;
	private Map<Integer, ItemCooldown> itemCoolDowns;
	private PortalCooldownList portalCooldownList;
	private CraftCooldownList craftCooldownList;
	private HouseObjectCooldownList houseObjectCooldownList;
	private long nextSkillUse;
	private long nextSummonSkillUse;
	private ChainSkills chainSkills;
	private final Map<AttackStatus, Long> lastCounterSkill = new HashMap<>();
	private int dualEffectValue = 0;
	private int wordBanTime = 0;
	private boolean bannedFromWorld = false;
	private String bannedFromWorldBy = "";
	private long bannedFromWorldDuring = 0;
	private Date bannedFromWorldDate = null;
	private String bannedFromWorldReason = "";
	private ScheduledFuture<?> taskToUnbanFromWorld = null;
	private Map<Integer, MaxCountOfDay> maxCountEvent;
	private int LunaDiceGame;
	private int LunaDiceGameTry = 0;
	/**
	 * Static information for players
	 */
	private static final int CUBE_SPACE = 9;
	private static final int WAREHOUSE_SPACE = 8;
	private boolean isAttackMode = false;
	private long gatherableTimer = 0;
	private long stopGatherable;
	private String captchaWord;
	private byte[] captchaImage;
	private float instanceStartPosX, instanceStartPosY, instanceStartPosZ;
	private int rebirthResurrectPercent = 1;
	private int rebirthSkill = 0;
	
	/**
	 * Connection of this Player.
	 */
	private AionConnection clientConnection;
	private FlyPathEntry flyLocationId;
	private long flyStartTime;
	private EmotionList emotions;
	private MotionList motions;
	private int partnerId;
	private long flyReuseTime;
	private boolean isMentor;
	private long lastMsgTime = 0;
	private int floodMsgCount = 0;
	private long onlineTime = 0;
	private long onlineBonusTime = 0;
	private int lootingNpcOid;
	private boolean rebirthRevive;
	
	// Needed to remove supplements queue
	private int subtractedSupplementsCount;
	private int subtractedSupplementId;
	private int portAnimation;
	private boolean isInSprintMode;
	private List<ActionObserver> rideObservers;
	byte housingStatus = HousingFlags.BUY_STUDIO_ALLOWED.getId();
	private int battleReturnMap;
	private float[] battleReturnCoords;
	
	// These variables are for the FFA, custom RP, and GM systems.
	private boolean isGmMode = false;
	
	// This variables are for the battleground system
	private int timer = 0;
	private boolean addedStatus;
	private boolean afkmode;
	
	// This variables are for the custom PvE and PK system
	byte buildingOwnerStates = PlayerHouseOwnerFlags.BUY_STUDIO_ALLOWED.getId();
	public static final int CHAT_NOT_FIXED = 0;
	public static final int CHAT_FIXED_ON_WORLD = 1;
	public static final int CHAT_FIXED_ON_ELYOS = 2;
	public static final int CHAT_FIXED_ON_ASMOS = 4;
	public static final int CHAT_FIXED_ON_BOTH = CHAT_FIXED_ON_ELYOS | CHAT_FIXED_ON_ASMOS;
	public int CHAT_FIX_WORLD_CHANNEL = CHAT_NOT_FIXED;
	private int useAutoGroup = 0;
	private boolean robot = false;
	private int robotId = 0;
	public int FAST_TRACK_TYPE = 0; // 0 = nothing,1 = moved exact current,2 = already moved
	private boolean isOnFastTrack = false;
	private boolean isInLiveParty = false;
	// private int linkedSkill;
	private PlayerConquererProtectorData conquerorProtectorData;
	private Map<Integer, PlayerFame> playerFame = new HashMap<>();
	private int worldPlayTime;
	
	private PlayerBonusTime bonusTime;
	private boolean newPlayer = false;
	private long creationDay;
	
	private int playersBonusId = 0;
	@SuppressWarnings("unused")
	private boolean hasBonus;
	private int bonusId = 0;
	private F2p f2p;
	private boolean hasAbyssBonus;
	private int abyssId = 0;
	private List<ActionObserver> hotTeleObservers;
	private int transformModelId;
	private int transformItemId;
	private int transformPanelId;
	private int transformSkillId;
	private boolean invisibleTransform = false;
	private AccountTransformList transformList;
	private int lastUsedTransformation;
	private Map<Integer, TransformCollection> transformCollections = new HashMap<>();
	private List<AccountTransfo> transformCreated = new ArrayList<>();
	private PlayerWardrobeList wardrobe;
	private PlayerLunaShop lunaShop;
	private PlayerSweep shugoSweep;
	private LunaBuffBonus lunaBuffBonus;
	private final Map<Integer, Integer> minions_ = new HashMap<>();
	private boolean setMinionSpawned = false;
	private int minionEnergy;
	private EquipmentSettingList equipmentSettingList;
	private PlayerMCList mc;
	private PlayerCollection playerCollection;
	private Map<Integer, PlayerAchievement> playerAchievements = new HashMap<>();
	private Map<Integer, PlayerAchievement> playerEventAchievements = new HashMap<>();
	private Map<Integer, LumielTransform> playerLumiel = new HashMap<>();
	
	/**
	 * Player Skill Skin List
	 */
	private SkillSkinList skillSkinList;
	
	/**
	 * Player Event Window List
	 */
	private PlayerEventWindowList eventWindowList;
	
	/**
	 * Retrieves the list of active event windows for the player.<br>
	 * This method returns the {@code PlayerEventWindowList} associated with this object.
	 * @return The current {@code PlayerEventWindowList}.
	 */
	public PlayerEventWindowList getEventWindow()
	{
		return eventWindowList;
	}
	
	/**
	 * Sets the list of event windows for the player.<br>
	 * This method updates the {@code eventWindowList} field.
	 * @param eventWindowList The new {@link PlayerEventWindowList} to assign.
	 */
	public void setEventWindow(PlayerEventWindowList eventWindowList)
	{
		this.eventWindowList = eventWindowList;
	}
	
	/**
	 * Initializes a new {@link Player} instance using common data.<br>
	 * This constructor sets up the basic player properties and internal controllers.
	 * @param plCommonData The {@code PlayerCommonData} object containing shared player information.
	 */
	private Player(PlayerCommonData plCommonData)
	{
		super(plCommonData.getPlayerObjId(), new PlayerController(), null, plCommonData, null);
		playerCommonData = plCommonData;
		playerAccount = new Account(0);
		absStatsHolder = new AbsoluteStatOwner(this, 0);
	}
	
	/**
	 * Creates a new {@link Player} instance with all required components.<br>
	 * This constructor initializes the player's data, controllers, and status lists.
	 * @param controller The {@code PlayerController} used to manage player actions.
	 * @param plCommonData The {@code PlayerCommonData} containing basic player information.
	 * @param appereance The {@code PlayerAppearance} defining the visual look of the character.
	 * @param account The {@code Account} object associated with this player.
	 */
	public Player(PlayerController controller, PlayerCommonData plCommonData, PlayerAppearance appereance, Account account)
	{
		super(plCommonData.getPlayerObjId(), controller, null, plCommonData, plCommonData.getPosition());
		daoVars = DAOManager.getDAO(PlayerVarsDAO.class);
		playerCommonData = plCommonData;
		playerAppearance = appereance;
		playerAccount = account;
		
		requester = new ResponseRequester(this);
		questStateList = new QuestStateList();
		titleList = new TitleList();
		invisibleTransform = false;
		playerAchievements = new HashMap<>();
		playerEventAchievements = new HashMap<>();
		transformCollections = new HashMap<>();
		transformCreated = new ArrayList<>();
		portalCooldownList = new PortalCooldownList(this);
		craftCooldownList = new CraftCooldownList(this);
		houseObjectCooldownList = new HouseObjectCooldownList(this);
		toyPetList = new PetList(this);
		minionList = new MinionList(this);
		controller.setOwner(this);
		moveController = new PlayerMoveController(this);
		playerFame = new HashMap<>();
		playerLumiel = new HashMap<>();
		plCommonData.setBoundingRadius(new BoundRadius(0.5f, 0.5f, getPlayerAppearance().getHeight()));
		
		setPlayerStatsTemplate(DataManager.PLAYER_STATS_DATA.getTemplate(this));
		setGameStats(new PlayerGameStats(this));
		setLifeStats(new PlayerLifeStats(this));
		absStatsHolder = new AbsoluteStatOwner(this, 0);
		transformList = new AccountTransformList(this);
	}
	
	/**
	 * Checks if the player is currently in a specific game mode.<br>
	 * This method delegates the check to {@link PlayerActions}.
	 * @param mode The {@code PlayerMode} to verify.
	 * @return {@code true} if the player is in the specified mode, otherwise {@code false}.
	 */
	public boolean isInPlayerMode(PlayerMode mode)
	{
		return PlayerActions.isInPlayerMode(this, mode);
	}
	
	/**
	 * Updates the current player mode.<br>
	 * This method calls {@code setPlayerMode} to change the state.
	 * @param mode The new {@code PlayerMode} to apply.
	 * @param obj An optional object associated with this mode change.
	 */
	public void setPlayerMode(PlayerMode mode, Object obj)
	{
		PlayerActions.setPlayerMode(this, mode, obj);
	}
	
	/**
	 * Removes a specific player mode from the current player.<br>
	 * This method updates the state by calling {@code unsetPlayerMode}.
	 * @param mode The {@code PlayerMode} to be removed.
	 */
	public void unsetPlayerMode(PlayerMode mode)
	{
		PlayerActions.unsetPlayerMode(this, mode);
	}
	
	/**
	 * Retrieves the movement controller for this player.<br>
	 * This method returns a {@link PlayerMoveController} instance.
	 * @return The {@code PlayerMoveController} associated with this player.
	 */
	@Override
	public PlayerMoveController getMoveController()
	{
		return (PlayerMoveController) super.getMoveController();
	}
	
	/**
	 * Creates a new {@link AggroList} for this creature.<br>
	 * This method initializes the list using the current object instance.
	 * @return A new {@code AggroList} associated with this creature.
	 */
	@Override
	protected AggroList createAggroList()
	{
		return new PlayerAggroList(this);
	}
	
	/**
	 * Retrieves the common data associated with this player.<br>
	 * This method returns the {@code PlayerCommonData} object.
	 * @return the {@code PlayerCommonData} of the current player.
	 */
	public PlayerCommonData getCommonData()
	{
		return playerCommonData;
	}
	
	/**
	 * Retrieves the name of the player.<br>
	 * This method returns the {@code String} name from the {@link PlayerCommonData}.
	 * @return The name of the player as a {@code String}.
	 */
	@Override
	public String getName()
	{
		return playerCommonData.getName();
	}
	
	/**
	 * Retrieves the current visual appearance of the player.<br>
	 * This includes details like clothing and equipment.
	 * @return the {@code PlayerAppearance} object for this player.
	 */
	public PlayerAppearance getPlayerAppearance()
	{
		return playerAppearance;
	}
	
	/**
	 * Updates the visual appearance of the player.<br>
	 * This method sets the {@code PlayerAppearance} object for the current character.
	 * @param playerAppearance The new {@link PlayerAppearance} to apply.
	 */
	public void setPlayerAppearance(PlayerAppearance playerAppearance)
	{
		this.playerAppearance = playerAppearance;
	}
	
	/**
	 * Retrieves the appearance data that was last saved for this player.<br>
	 * This is different from the current active appearance.
	 * @return the {@code PlayerAppearance} object containing the saved data.
	 */
	public PlayerAppearance getSavedPlayerAppearance()
	{
		return savedPlayerAppearance;
	}
	
	/**
	 * Updates the stored appearance for the player.<br>
	 * This method saves a {@code PlayerAppearance} object to the internal state.
	 * @param savedPlayerAppearance The new {@code PlayerAppearance} to store.
	 */
	public void setSavedPlayerAppearance(PlayerAppearance savedPlayerAppearance)
	{
		this.savedPlayerAppearance = savedPlayerAppearance;
	}
	
	/**
	 * Sets the {@link AionConnection} for this player.<br>
	 * This method links a specific network connection to the player object.
	 * @param clientConnection The {@code AionConnection} to associate with the player.
	 */
	public void setClientConnection(AionConnection clientConnection)
	{
		this.clientConnection = clientConnection;
	}
	
	/**
	 * Retrieves the current {@link AionConnection} for this player.<br>
	 * This method returns the connection object used to communicate with the game client.
	 * @return The {@code AionConnection} associated with this player instance.
	 */
	public AionConnection getClientConnection()
	{
		return clientConnection;
	}
	
	/**
	 * Retrieves the list of macros associated with this player.
	 * @return a {@code MacroList} containing all player macros.
	 */
	public MacroList getMacroList()
	{
		return macroList;
	}
	
	/**
	 * Updates the list of macros for the player.<br>
	 * This method replaces the current {@code MacroList} with a new one.
	 * @param macroList The new {@code MacroList} to assign.
	 */
	public void setMacroList(MacroList macroList)
	{
		this.macroList = macroList;
	}
	
	/**
	 * Retrieves the list of skills for the player.<br>
	 * This method returns the current {@code PlayerSkillList}.
	 * @return The list of skills belonging to the player.
	 */
	public PlayerSkillList getSkillList()
	{
		return skillList;
	}
	
	/**
	 * Updates the list of skills for the player.<br>
	 * This method assigns a new {@code PlayerSkillList} to the current object.
	 * @param skillList The new list of skills to assign.
	 */
	public void setSkillList(PlayerSkillList skillList)
	{
		this.skillList = skillList;
	}
	
	/**
	 * Retrieves the current pet associated with this player.<br>
	 * This method returns the {@code toyPet} object.
	 * @return the {@link Pet} object of the player.
	 */
	public Pet getPet()
	{
		return toyPet;
	}
	
	/**
	 * Sets the current {@link Pet} for the player.<br>
	 * This method updates the internal {@code toyPet} field.
	 * @param toyPet The {@code Pet} object to assign.
	 */
	public void setToyPet(Pet toyPet)
	{
		this.toyPet = toyPet;
	}
	
	/**
	 * Retrieves the current {@link Minion} associated with this player.<br>
	 * Returns {@code null} if no minion is currently active.
	 * @return The {@code Minion} object.
	 */
	public Minion getMinion()
	{
		return minion;
	}
	
	/**
	 * Sets the current {@link Minion} for this player.<br>
	 * This method updates the internal {@code minion} field.
	 * @param minion The {@code Minion} object to assign.
	 */
	public void setMinion(Minion minion)
	{
		this.minion = minion;
	}
	
	/**
	 * Updates the status of whether a minion has been spawned.<br>
	 * This method sets the {@code minionSpawned} flag for the player.
	 * @param setMinionSpawned The new status to assign to the minion spawn state.
	 */
	public void setMinionSpawned(boolean setMinionSpawned)
	{
		this.setMinionSpawned = setMinionSpawned;
	}
	
	/**
	 * Checks if a minion has been spawned for the player.
	 * @return {@code true} if the minion is currently spawned, {@code false} otherwise.
	 */
	public boolean isMinionSpawned()
	{
		return setMinionSpawned;
	}
	
	/**
	 * Updates the mapping of a minion to its specific ID.<br>
	 * This method stores the relationship between an object and its identifier in the internal list.
	 * @param objId The unique identifier for the object.
	 * @param id The unique identifier for the minion.
	 */
	public void setMinionTempList(int objId, int id)
	{
		minions_.put(objId, id);
	}
	
	/**
	 * Retrieves the list of minions associated with a specific object. <br>
	 * This method looks up the {@code objId} in the internal minion map.
	 * @param objId The unique identifier for the object.
	 * @return The list of minions, or {@code null} if no match is found.
	 */
	public int getMinionTempList(int objId)
	{
		if (objId == 0)
		{
		}
		
		return minions_.get(objId);
	}
	
	/**
	 * Retrieves the current energy level of the player's minion.
	 * @return The {@code int} value representing the minion's energy.
	 */
	public int getMinionEnergy()
	{
		return minionEnergy;
	}
	
	/**
	 * Sets the current energy level for the minion.<br>
	 * This updates the {@code minionEnergy} field.
	 * @param energy The new energy value to assign.
	 */
	public void setMinionEnergy(int energy)
	{
		minionEnergy = energy;
	}
	
	/**
	 * Checks if the player belongs to a magical class.<br>
	 * This method evaluates the {@link PlayerClass} of the current player.<br>
	 * It returns {@code true} for classes like Artist, Bard, Cleric, Sorcerer, Spirit Master, or Rider.
	 * @return {@code true} if the player is a magical type, {@code false} otherwise.
	 */
	public boolean isMagicalTypeClass()
	{
		return (playerCommonData.getPlayerClass() == PlayerClass.ARTIST) || (playerCommonData.getPlayerClass() == PlayerClass.BARD) || (playerCommonData.getPlayerClass() == PlayerClass.CLERIC) || (playerCommonData.getPlayerClass() == PlayerClass.SORCERER) || (playerCommonData.getPlayerClass() == PlayerClass.SPIRIT_MASTER) || (playerCommonData.getPlayerClass() == PlayerClass.RIDER);
	}
	
	/**
	 * Retrieves the current list of friends for the player.
	 * @return a {@code FriendList} containing all added friends.
	 */
	public FriendList getFriendList()
	{
		return friendList;
	}
	
	/**
	 * Checks if the player is currently searching for a group.
	 * @return {@code true} if the player is looking for a group, {@code false} otherwise.
	 */
	public boolean isLookingForGroup()
	{
		return lookingForGroup;
	}
	
	/**
	 * Updates the status of whether the player is currently searching for a group.<br>
	 * This value determines if the player should be shown in the group finding list.
	 * @param lookingForGroup The new {@code boolean} status to set.
	 */
	public void setLookingForGroup(boolean lookingForGroup)
	{
		this.lookingForGroup = lookingForGroup;
	}
	
	/**
	 * Checks if the player is currently waiting for an event.<br>
	 * This status is used to determine if the player should receive event notifications.
	 * @return {@code true} if the player is looking for an event, {@code false} otherwise.
	 */
	public boolean isLookingForEvent()
	{
		return lookingForEvent;
	}
	
	/**
	 * Updates the status of whether the player is currently searching for an event.<br>
	 * This flag determines if the system should trigger specific event-related logic.
	 * @param lookingForEvent The new state to set for the search status. Set {@code true} to enable or {@code false} to disable.
	 */
	public void setLookingForEvent(boolean lookingForEvent)
	{
		this.lookingForEvent = lookingForEvent;
	}
	
	/**
	 * Checks if the player is currently in attack mode.
	 * @return {@code true} if the player is attacking, {@code false} otherwise.
	 */
	public boolean isAttackMode()
	{
		return isAttackMode;
	}
	
	/**
	 * Updates the current attack mode status of the player.<br>
	 * This method sets the {@code isAttackMode} flag to either {@code true} or {@code false}.
	 * @param isAttackMode The new attack mode state to apply.
	 */
	public void setAttackMode(boolean isAttackMode)
	{
		this.isAttackMode = isAttackMode;
	}
	
	/**
	 * Checks if the player is currently unable to be gathered.<br>
	 * This method returns {@code true} if the {@code gatherableTimer} is not {@code 0}.
	 * @return {@code true} if the player cannot be gathered, {@code false} otherwise.
	 */
	public boolean isNotGatherable()
	{
		return gatherableTimer != 0;
	}
	
	/**
	 * Sets the timer for gathering actions.<br>
	 * This method ensures that the value is never less than {@code 0}.
	 * @param gatherableTimer The time in milliseconds to set for gathering.
	 */
	public void setGatherableTimer(long gatherableTimer)
	{
		if (gatherableTimer < 0)
		{
			gatherableTimer = 0;
		}
		
		this.gatherableTimer = gatherableTimer;
	}
	
	/**
	 * Retrieves the current timer for gathering activities.<br>
	 * This value represents the time remaining for a gathering action.
	 * @return The current gathering timer as a {@code long}.
	 */
	public long getGatherableTimer()
	{
		return gatherableTimer;
	}
	
	/**
	 * Retrieves the current status of gatherable items.<br>
	 * This value indicates whether gathering actions are currently disabled.
	 * @return The {@code long} value representing the stop gatherable state.
	 */
	public long getStopGatherable()
	{
		return stopGatherable;
	}
	
	/**
	 * Sets the value for whether gatherable items can be stopped.<br>
	 * This updates the {@code stopGatherable} field in the player object.
	 * @param stopGatherable The new value to set for stopping gatherables.
	 */
	public void setStopGatherable(long stopGatherable)
	{
		this.stopGatherable = stopGatherable;
	}
	
	/**
	 * Retrieves the current captcha word for the player.<br>
	 * This is used to verify human interaction during login or actions.
	 * @return The {@code String} containing the captcha word.
	 */
	public String getCaptchaWord()
	{
		return captchaWord;
	}
	
	/**
	 * Sets the security captcha word for the player.<br>
	 * This value is used to verify user identity during login or actions.
	 * @param captchaWord The {@code String} containing the captcha text.
	 */
	public void setCaptchaWord(String captchaWord)
	{
		this.captchaWord = captchaWord;
	}
	
	/**
	 * Retrieves the current captcha image data.<br>
	 * This method returns the raw bytes of the generated captcha.
	 * @return a {@code byte[]} containing the captcha image data.
	 */
	public byte[] getCaptchaImage()
	{
		return captchaImage;
	}
	
	/**
	 * Sets the image data for the captcha.<br>
	 * This method updates the {@code captchaImage} field with the provided byte array.
	 * @param captchaImage The raw bytes of the captcha image to store.
	 */
	public void setCaptchaImage(byte[] captchaImage)
	{
		this.captchaImage = captchaImage;
	}
	
	/**
	 * Updates the current friend list for the player.<br>
	 * This method replaces the existing {@code FriendList} with a new one.
	 * @param list The new {@code FriendList} to assign to the player.
	 */
	public void setFriendList(FriendList list)
	{
		friendList = list;
	}
	
	/**
	 * Retrieves the current list of blocked items for the player.<br>
	 * This method returns a {@code BlockList} object containing all active blocks.
	 * @return The {@code BlockList} associated with this player.
	 */
	public BlockList getBlockList()
	{
		return blockList;
	}
	
	/**
	 * Updates the current block list for the player.<br>
	 * This method assigns a new {@code BlockList} to the internal field.
	 * @param list The new {@code BlockList} to be assigned.
	 */
	public void setBlockList(BlockList list)
	{
		blockList = list;
	}
	
	/**
	 * Retrieves the list of pets owned by the player.<br>
	 * This method returns the {@code toyPetList} collection.
	 * @return a {@link PetList} containing all current pets.
	 */
	public PetList getPetList()
	{
		return toyPetList;
	}
	
	/**
	 * Retrieves the list of minions associated with this player.
	 * @return a {@code MinionList} containing all current minions.
	 */
	public MinionList getMinionList()
	{
		return minionList;
	}
	
	/**
	 * Retrieves the current life statistics for the player.<br>
	 * This method returns a {@link PlayerLifeStats} object containing health and mana data.
	 * @return the {@code PlayerLifeStats} of the player.
	 */
	@Override
	public PlayerLifeStats getLifeStats()
	{
		return (PlayerLifeStats) super.getLifeStats();
	}
	
	/**
	 * Retrieves the current game statistics for the player.<br>
	 * This method calls the parent class to fetch the {@code PlayerGameStats}.
	 * @return the {@code PlayerGameStats} object.
	 */
	@Override
	public PlayerGameStats getGameStats()
	{
		return (PlayerGameStats) super.getGameStats();
	}
	
	/**
	 * Retrieves the current {@code ResponseRequester} for this player.<br>
	 * This object handles requests sent from the client to the server.
	 * @return the {@code ResponseRequester} instance.
	 */
	public ResponseRequester getResponseRequester()
	{
		return requester;
	}
	
	/**
	 * Checks if the player is currently connected to the server.<br>
	 * This method returns {@code true} if the {@code getClientConnection} is not {@code null}.
	 * @return {@code true} if the player is online, {@code false} otherwise.
	 */
	public boolean isOnline()
	{
		return getClientConnection() != null;
	}
	
	/**
	 * Updates the cube expansion value for the player.<br>
	 * This method also increases the inventory limit based on the {@code cubeExpands} amount.
	 * @param cubeExpands The number of cube expansions to apply.
	 */
	public void setCubeExpands(int cubeExpands)
	{
		playerCommonData.setCubeExpands(cubeExpands);
		getInventory().setLimit(getInventory().getLimit() + ((cubeExpands) * CUBE_SPACE));
	}
	
	/**
	 * Retrieves the number of cube expansions for the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The total count of cube expansions as an {@code int}.
	 */
	public int getCubeExpands()
	{
		return playerCommonData.getCubeExpands();
	}
	
	/**
	 * Retrieves the character class of the player.<br>
	 * This method returns the {@code PlayerClass} associated with this ranking result.
	 * @return The {@code PlayerClass} of the player.
	 */
	public PlayerClass getPlayerClass()
	{
		return playerCommonData.getPlayerClass();
	}
	
	/**
	 * Retrieves the gender of the player.<br>
	 * This value is retrieved from the {@link PlayerCommonData} object.
	 * @return the {@link Gender} of the player.
	 */
	public Gender getGender()
	{
		return playerCommonData.getGender();
	}
	
	/**
	 * Retrieves the {@link PlayerController} for this player.<br>
	 * This method casts the base controller to a specific player type.
	 * @return The {@code PlayerController} associated with this object.
	 */
	@Override
	public PlayerController getController()
	{
		return (PlayerController) super.getController();
	}
	
	/**
	 * Retrieves the level of this player.<br>
	 * This value is fetched from the {@link PlayerCommonData}.
	 * @return The level as a {@code byte}.
	 */
	@Override
	public byte getLevel()
	{
		return (byte) playerCommonData.getLevel();
	}
	
	/**
	 * Retrieves the current {@code Equipment} object for this player.
	 * @return the {@code Equipment} instance.
	 */
	public Equipment getEquipment()
	{
		return equipment;
	}
	
	/**
	 * Updates the current {@code Equipment} for the player.<br>
	 * This method assigns a new {@link Equipment} object to the internal field.
	 * @param equipment The {@code Equipment} object to set.
	 */
	public void setEquipment(Equipment equipment)
	{
		this.equipment = equipment;
	}
	
	/**
	 * Retrieves the private data store for this player.<br>
	 * This method returns the {@code PrivateStore} object associated with the current instance.
	 * @return The {@link PrivateStore} of the player.
	 */
	public PrivateStore getStore()
	{
		return store;
	}
	
	/**
	 * Sets the {@code PrivateStore} for this player.<br>
	 * This method updates the internal {@code store} field.
	 * @param store The {@link PrivateStore} to assign to the player.
	 */
	public void setStore(PrivateStore store)
	{
		this.store = store;
	}
	
	/**
	 * Retrieves the current list of quests for the player.<br>
	 * This method returns a {@code QuestStateList} object containing all active and completed quests.
	 * @return The {@code QuestStateList} associated with this player.
	 */
	public QuestStateList getQuestStateList()
	{
		return questStateList;
	}
	
	/**
	 * Updates the list of current quest states for the player.<br>
	 * This method replaces the existing {@code questStateList} with a new one.
	 * @param questStateList The new {@code QuestStateList} to assign to the player.
	 */
	public void setQuestStateList(QuestStateList questStateList)
	{
		this.questStateList = questStateList;
	}
	
	/**
	 * Retrieves the default statistics template for a player.<br>
	 * This provides the base values used to initialize player stats.
	 * @return the {@code PlayerStatsTemplate} object.
	 */
	public PlayerStatsTemplate getPlayerStatsTemplate()
	{
		return playerStatsTemplate;
	}
	
	/**
	 * Sets the template for the player's statistics.<br>
	 * This method updates the {@code playerStatsTemplate} field of the current object.
	 * @param playerStatsTemplate The new {@link PlayerStatsTemplate} to apply.
	 */
	public void setPlayerStatsTemplate(PlayerStatsTemplate playerStatsTemplate)
	{
		this.playerStatsTemplate = playerStatsTemplate;
	}
	
	/**
	 * Retrieves the current list of recipes.
	 * @return a {@code RecipeList} containing all available recipes.
	 */
	public RecipeList getRecipeList()
	{
		return recipeList;
	}
	
	/**
	 * Updates the list of recipes for the player.<br>
	 * This method assigns a new {@code RecipeList} to the current object.
	 * @param recipeList The new {@code RecipeList} to be stored.
	 */
	public void setRecipeList(RecipeList recipeList)
	{
		this.recipeList = recipeList;
	}
	
	/**
	 * Assigns a specific {@code Storage} object to the player based on its type.<br>
	 * This method updates internal fields like warehouses or pet bags depending on the {@code storageType}.<br>
	 * It also sets this player as the owner of the provided {@code storage}.
	 * @param storage The {@code Storage} instance to be assigned.
	 * @param storageType The {@code StorageType} identifying where the storage should be linked.
	 */
	public void setStorage(Storage storage, StorageType storageType)
	{
		if (storageType == StorageType.CUBE)
		{
			inventory = storage;
		}
		
		if ((storageType.getId() >= StorageType.PET_BAG_MIN) && (storageType.getId() <= StorageType.PET_BAG_MAX))
		{
			petBag[storageType.getId() - StorageType.PET_BAG_MIN] = storage;
		}
		
		if ((storageType.getId() >= StorageType.HOUSE_WH_MIN) && (storageType.getId() <= StorageType.HOUSE_WH_MAX))
		{
			cabinets[storageType.getId() - StorageType.HOUSE_WH_MIN] = storage;
		}
		
		if (storageType == StorageType.REGULAR_WAREHOUSE)
		{
			regularWarehouse = storage;
		}
		
		if (storageType == StorageType.ACCOUNT_WAREHOUSE)
		{
			accountWarehouse = storage;
		}
		
		storage.setOwner(this);
	}
	
	/**
	 * Retrieves the specific storage object based on the provided type.<br>
	 * This method identifies whether to return a warehouse, pet bag, cabinet, or cube.
	 * @param storageType The unique identifier for the type of storage requested.
	 * @return The corresponding {@link IStorage} object, or {@code null} if no match is found.
	 */
	public IStorage getStorage(int storageType)
	{
		if (storageType == StorageType.REGULAR_WAREHOUSE.getId())
		{
			return regularWarehouse;
		}
		
		if (storageType == StorageType.ACCOUNT_WAREHOUSE.getId())
		{
			return accountWarehouse;
		}
		
		if ((storageType == StorageType.LEGION_WAREHOUSE.getId()) && (getLegion() != null))
		{
			return new LegionStorageProxy(getLegion().getLegionWarehouse(), this);
		}
		
		if ((storageType >= StorageType.PET_BAG_MIN) && (storageType <= StorageType.PET_BAG_MAX))
		{
			return petBag[storageType - StorageType.PET_BAG_MIN];
		}
		
		if ((storageType >= StorageType.HOUSE_WH_MIN) && (storageType <= StorageType.HOUSE_WH_MAX))
		{
			return cabinets[storageType - StorageType.HOUSE_WH_MIN];
		}
		
		if (storageType == StorageType.CUBE.getId())
		{
			return inventory;
		}
		
		return null;
	}
	
	/**
	 * Retrieves a list of items that need to be updated in the database.<br>
	 * This method checks various storage types and equipment for pending changes.<br>
	 * It collects items with Kinah or deleted items from any source marked as {@code UPDATE_REQUIRED}.<br>
	 * After collecting these items, it updates the state of each source to {@code UPDATED}.
	 * @return a {@code List} containing all dirty {@link Item} objects.
	 */
	public List<Item> getDirtyItemsToUpdate()
	{
		final List<Item> dirtyItems = new ArrayList<>();
		
		final IStorage cubeStorage = getStorage(StorageType.CUBE.getId());
		if (cubeStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED)
		{
			dirtyItems.addAll(cubeStorage.getItemsWithKinah());
			dirtyItems.addAll(cubeStorage.getDeletedItems());
			cubeStorage.setPersistentState(PersistentState.UPDATED);
		}
		
		final IStorage regularWhStorage = getStorage(StorageType.REGULAR_WAREHOUSE.getId());
		if (regularWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED)
		{
			dirtyItems.addAll(regularWhStorage.getItemsWithKinah());
			dirtyItems.addAll(regularWhStorage.getDeletedItems());
			regularWhStorage.setPersistentState(PersistentState.UPDATED);
		}
		
		final IStorage accountWhStorage = getStorage(StorageType.ACCOUNT_WAREHOUSE.getId());
		if (accountWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED)
		{
			dirtyItems.addAll(accountWhStorage.getItemsWithKinah());
			dirtyItems.addAll(accountWhStorage.getDeletedItems());
			accountWhStorage.setPersistentState(PersistentState.UPDATED);
		}
		
		final IStorage legionWhStorage = getStorage(StorageType.LEGION_WAREHOUSE.getId());
		if (legionWhStorage != null)
		{
			if (legionWhStorage.getPersistentState() == PersistentState.UPDATE_REQUIRED)
			{
				dirtyItems.addAll(legionWhStorage.getItemsWithKinah());
				dirtyItems.addAll(legionWhStorage.getDeletedItems());
				legionWhStorage.setPersistentState(PersistentState.UPDATED);
			}
		}
		
		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++)
		{
			final IStorage petBag = getStorage(petBagId);
			if ((petBag != null) && (petBag.getPersistentState() == PersistentState.UPDATE_REQUIRED))
			{
				dirtyItems.addAll(petBag.getItemsWithKinah());
				dirtyItems.addAll(petBag.getDeletedItems());
				petBag.setPersistentState(PersistentState.UPDATED);
			}
		}
		
		for (int houseWhId = StorageType.HOUSE_WH_MIN; houseWhId <= StorageType.HOUSE_WH_MAX; houseWhId++)
		{
			final IStorage cabinet = getStorage(houseWhId);
			if ((cabinet != null) && (cabinet.getPersistentState() == PersistentState.UPDATE_REQUIRED))
			{
				dirtyItems.addAll(cabinet.getItemsWithKinah());
				dirtyItems.addAll(cabinet.getDeletedItems());
				cabinet.setPersistentState(PersistentState.UPDATED);
			}
		}
		
		final Equipment equipment = getEquipment();
		if (equipment.getPersistentState() == PersistentState.UPDATE_REQUIRED)
		{
			dirtyItems.addAll(equipment.getEquippedItems());
			equipment.setPersistentState(PersistentState.UPDATED);
		}
		
		return dirtyItems;
	}
	
	/**
	 * Retrieves all items that contain {@code Kinah}.<br>
	 * This includes inventory, warehouses, pet bags, and house cabinets.<br>
	 * It also adds currently equipped items to the list.
	 * @return A {@link List} containing all found {@link Item} objects.
	 */
	public List<Item> getAllItems()
	{
		final List<Item> items = new ArrayList<>();
		items.addAll(inventory.getItemsWithKinah());
		if (regularWarehouse != null)
		{
			items.addAll(regularWarehouse.getItemsWithKinah());
		}
		
		if (accountWarehouse != null)
		{
			items.addAll(accountWarehouse.getItemsWithKinah());
		}
		
		for (int petBagId = StorageType.PET_BAG_MIN; petBagId <= StorageType.PET_BAG_MAX; petBagId++)
		{
			final IStorage petBag = getStorage(petBagId);
			if (petBag != null)
			{
				items.addAll(petBag.getItemsWithKinah());
			}
		}
		
		for (int houseWhId = StorageType.HOUSE_WH_MIN; houseWhId <= StorageType.HOUSE_WH_MAX; houseWhId++)
		{
			final IStorage cabinet = getStorage(houseWhId);
			if (cabinet != null)
			{
				items.addAll(cabinet.getItemsWithKinah());
			}
		}
		
		items.addAll(getEquipment().getEquippedItems());
		return items;
	}
	
	/**
	 * Retrieves the current inventory of the player.<br>
	 * This method returns the {@code Storage} object containing all items.
	 * @return The {@code Storage} object for the player.
	 */
	public Storage getInventory()
	{
		return inventory;
	}
	
	/**
	 * Retrieves the current settings for the player.<br>
	 * This method returns the {@code PlayerSettings} object associated with this instance.
	 * @return The {@code PlayerSettings} of the player.
	 */
	public PlayerSettings getPlayerSettings()
	{
		return playerSettings;
	}
	
	/**
	 * Updates the current {@code PlayerSettings} for this player.<br>
	 * This method replaces the existing settings with a new {@code PlayerSettings} object.
	 * @param playerSettings The new {@code PlayerSettings} to apply.
	 */
	public void setPlayerSettings(PlayerSettings playerSettings)
	{
		this.playerSettings = playerSettings;
	}
	
	/**
	 * Retrieves the list of titles associated with the player.
	 * @return a {@code TitleList} containing all current titles.
	 */
	public TitleList getTitleList()
	{
		return titleList;
	}
	
	/**
	 * Sets the {@code TitleList} for the player.<br>
	 * This method checks if additional titles are enabled in the configuration.<br>
	 * If enabled, it adds specific default entries to the list.<br>
	 * It also sets this object as the owner of the provided {@code titleList}.
	 * @param titleList The {@code TitleList} to assign to the player.
	 */
	public void setTitleList(TitleList titleList)
	{
		if (havePermission(MembershipConfig.TITLES_ADDITIONAL_ENABLE))
		{
			titleList.addEntry(102, 0);
			titleList.addEntry(103, 0);
			titleList.addEntry(104, 0);
			titleList.addEntry(105, 0);
			titleList.addEntry(106, 0);
			titleList.addEntry(146, 0);
			titleList.addEntry(151, 0);
			titleList.addEntry(152, 0);
			titleList.addEntry(160, 0);
			titleList.addEntry(161, 0);
		}
		
		this.titleList = titleList;
		titleList.setOwner(this);
	}
	
	/**
	 * Retrieves the second group associated with the player.<br>
	 * This method returns a {@link com.aionemu.gameserver.model.team2.group.PlayerGroup} object.
	 * @return the {@code PlayerGroup} for team 2
	 */
	public com.aionemu.gameserver.model.team2.group.PlayerGroup getPlayerGroup2()
	{
		return playerGroup2;
	}
	
	/**
	 * Sets the group for team 2.<br>
	 * This method updates the {@code playerGroup2} field with a new {@link com.aionemu.gameserver.model.team2.group.PlayerGroup}.
	 * @param playerGroup The new {@code PlayerGroup} to assign to team 2.
	 */
	public void setPlayerGroup2(com.aionemu.gameserver.model.team2.group.PlayerGroup playerGroup)
	{
		playerGroup2 = playerGroup;
	}
	
	/**
	 * Retrieves the current {@code AbyssRank} of the player.<br>
	 * This value represents the player's standing in the abyss system.
	 * @return The {@code AbyssRank} object associated with this player.
	 */
	public AbyssRank getAbyssRank()
	{
		return abyssRank;
	}
	
	/**
	 * Updates the {@code abyssRank} for the player.<br>
	 * This method sets the current rank value in the player object.
	 * @param abyssRank The new {@link AbyssRank} to assign.
	 */
	public void setAbyssRank(AbyssRank abyssRank)
	{
		this.abyssRank = abyssRank;
	}
	
	/**
	 * Retrieves the controller responsible for managing player effects.<br>
	 * This method returns a {@link PlayerEffectController} instance.
	 * @return the {@code PlayerEffectController} associated with this player.
	 */
	@Override
	public PlayerEffectController getEffectController()
	{
		return (PlayerEffectController) super.getEffectController();
	}
	
	/**
	 * Updates the player status to {@code ONLINE}.<br>
	 * This method is called when a player successfully logs into the game.<br>
	 * It synchronizes the status with the {@link PlayerCommonData}.
	 */
	public void onLoggedIn()
	{
		friendList.setStatus(Status.ONLINE, getCommonData());
	}
	
	/**
	 * Handles the logic when a player logs out.<br>
	 * This method denies all requests from the requester.<br>
	 * It also updates the friend list status to {@code OFFLINE}.
	 */
	public void onLoggedOut()
	{
		requester.denyAll();
		friendList.setStatus(FriendList.Status.OFFLINE, getCommonData());
	}
	
	/**
	 * Checks if the player is currently part of a {@link Legion}.<br>
	 * This method verifies if the {@code legionMember} object is not {@code null}.
	 * @return {@code true} if the player is a member, {@code false} otherwise.
	 */
	public boolean isLegionMember()
	{
		return legionMember != null;
	}
	
	/**
	 * Sets the {@code LegionMember} for this player.<br>
	 * This updates the current member information associated with the character.
	 * @param legionMember The {@code LegionMember} object to assign.
	 */
	public void setLegionMember(LegionMember legionMember)
	{
		this.legionMember = legionMember;
	}
	
	/**
	 * Retrieves the {@link LegionMember} associated with this player.<br>
	 * This method returns the current member data for the character.
	 * @return The {@code LegionMember} object or {@code null} if not found.
	 */
	public LegionMember getLegionMember()
	{
		return legionMember;
	}
	
	/**
	 * Retrieves the {@link Legion} associated with this account.<br>
	 * This method fetches data from the internal {@code legionMember} object.
	 * @return the {@code Legion} object or {@code null} if no legion exists.
	 */
	public Legion getLegion()
	{
		return legionMember != null ? legionMember.getLegion() : null;
	}
	
	/**
	 * Checks if the current object's ID matches a given value.<br>
	 * This method compares the internal {@code objectId} with the provided parameter.
	 * @param objectId The unique identifier to compare against.
	 * @return {@code true} if the IDs match, otherwise {@code false}.
	 */
	public boolean sameObjectId(int objectId)
	{
		return getObjectId() == objectId;
	}
	
	/**
	 * Checks if the player currently has a store.<br>
	 * This method calls {@code getStore} to verify the existence of a store object.
	 * @return {@code true} if the store is not {@code null}, otherwise {@code false}.
	 */
	public boolean hasStore()
	{
		if (getStore() != null)
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Resets the legion membership status for the player.<br>
	 * This method clears any existing legion data by calling {@code setLegionMember} with {@code null}.
	 */
	public void resetLegionMember()
	{
		setLegionMember(null);
	}
	
	/**
	 * Checks if the player belongs to group 2.<br>
	 * This method returns {@code true} if the {@code playerGroup2} object is not {@code null}.
	 * @return {@code true} if the player is in group 2, otherwise {@code false}.
	 */
	public boolean isInGroup2()
	{
		return playerGroup2 != null;
	}
	
	/**
	 * Retrieves the current access level of the account.<br>
	 * This value determines what actions the user is allowed to perform.
	 * @return The {@code byte} value representing the account's access level.
	 */
	public byte getAccessLevel()
	{
		return playerAccount.getAccessLevel();
	}
	
	/**
	 * Retrieves the name of the account associated with this player.<br>
	 * This method calls {@code getName} on the internal account object.
	 * @return The name of the account as a {@code String}.
	 */
	public String getAcountName()
	{
		return playerAccount.getName();
	}
	
	/**
	 * Retrieves the current server rates.<br>
	 * If no rates are set, it initializes and returns {@code RegularRates}.
	 * @return the current {@link Rates} object.
	 */
	public Rates getRates()
	{
		if (rates == null)
		{
			rates = new RegularRates();
		}
		
		return rates;
	}
	
	/**
	 * Updates the current rates for the player.<br>
	 * This method assigns a new {@code Rates} object to the internal field.
	 * @param rates The new {@code Rates} object to apply.
	 */
	public void setRates(Rates rates)
	{
		this.rates = rates;
	}
	
	/**
	 * Retrieves the total size of the player's warehouse.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The integer size of the warehouse.
	 */
	public int getWarehouseSize()
	{
		return playerCommonData.getWarehouseSize();
	}
	
	/**
	 * Updates the warehouse capacity for the player.<br>
	 * This method modifies the {@code PlayerCommonData} and increases the current limit.
	 * @param warehouseSize The amount of space to add to the warehouse.
	 */
	public void setWarehouseSize(int warehouseSize)
	{
		playerCommonData.setWarehouseSize(warehouseSize);
		getWarehouse().setLimit(getWarehouse().getLimit() + (warehouseSize * WAREHOUSE_SPACE));
	}
	
	/**
	 * Retrieves the player's primary storage.<br>
	 * This method returns the {@code regularWarehouse} object.
	 * @return The {@link Storage} instance for the warehouse.
	 */
	public Storage getWarehouse()
	{
		return regularWarehouse;
	}
	
	/**
	 * Retrieves the current flight status of the player.<br>
	 * This value indicates whether the player is currently flying or not.
	 * @return The integer representing the current {@code flyState}.
	 */
	public int getFlyState()
	{
		return flyState;
	}
	
	/**
	 * Updates the current flying state of the player.<br>
	 * This method sets the {@code flyState} value.<br>
	 * It automatically calls {@code setFlyingMode} based on the provided integer.
	 * @param flyState The new flight status where {@code 1} is true and {@code 0} is false.
	 */
	public void setFlyState(int flyState)
	{
		this.flyState = flyState;
		if (flyState == 1)
		{
			setFlyingMode(true);
		}
		else if (flyState == 0)
		{
			setFlyingMode(false);
		}
	}
	
	/**
	 * Checks if the player is currently in a trading state.
	 * @return {@code true} if the player is trading, {@code false} otherwise.
	 */
	public boolean isTrading()
	{
		return isTrading;
	}
	
	/**
	 * Updates the trading status of the player.<br>
	 * Sets the {@code isTrading} flag to either {@code true} or {@code false}.
	 * @param isTrading The new trading state for the player.
	 */
	public void setTrading(boolean isTrading)
	{
		this.isTrading = isTrading;
	}
	
	/**
	 * Checks if the player is currently in prison.<br>
	 * This method returns {@code true} if a prison timer exists.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the player is imprisoned, {@code false} if they are free.
	 */
	public boolean isInPrison()
	{
		return prisonTimer != 0;
	}
	
	/**
	 * Sets the duration of the player's prison timer.<br>
	 * If the provided value is less than {@code 0}, it defaults to {@code 0}.
	 * @param prisonTimer The time in milliseconds for the prison stay.
	 */
	public void setPrisonTimer(long prisonTimer)
	{
		if (prisonTimer < 0)
		{
			prisonTimer = 0;
		}
		
		this.prisonTimer = prisonTimer;
	}
	
	/**
	 * Retrieves the current duration of the player's prison sentence.<br>
	 * The value is returned in milliseconds.
	 * @return The remaining time for the prison stay as a {@code long}.
	 */
	public long getPrisonTimer()
	{
		return prisonTimer;
	}
	
	/**
	 * Retrieves the starting prison value for the player.<br>
	 * This method returns the current {@code long} value of the prison status.
	 * @return The starting prison value as a {@code long}.
	 */
	public long getStartPrison()
	{
		return startPrison;
	}
	
	/**
	 * Sets the starting point for the prison status.<br>
	 * This method updates the {@code startPrison} field with a new value.
	 * @param start The new starting value to assign to the prison status.
	 */
	public void setStartPrison(long start)
	{
		startPrison = start;
	}
	
	/**
	 * Checks if the player is currently in a protection state.<br>
	 * This method returns {@code true} if the visual state is set to {@code BLINKING}.
	 * @return {@code true} if protection is active, {@code false} otherwise.
	 */
	public boolean isProtectionActive()
	{
		return isInVisualState(CreatureVisualState.BLINKING);
	}
	
	/**
	 * Checks if the player is currently in an invincible state.
	 * @return {@code true} if the player cannot be damaged, {@code false} otherwise.
	 */
	public boolean isInvul()
	{
		return invul;
	}
	
	/**
	 * Sets the invulnerability status of the player.<br>
	 * This determines if the player can take damage.
	 * @param invul The new invulnerability state to set.
	 */
	public void setInvul(boolean invul)
	{
		this.invul = invul;
	}
	
	/**
	 * Sets the {@code Mailbox} for this player.<br>
	 * This method updates the internal {@code mailbox} field.
	 * @param mailbox The {@link Mailbox} object to assign.
	 */
	public void setMailbox(Mailbox mailbox)
	{
		this.mailbox = mailbox;
	}
	
	/**
	 * Retrieves the current {@link Mailbox} object.<br>
	 * This method returns the mailbox associated with the player.
	 * @return The {@code Mailbox} instance.
	 */
	public Mailbox getMailbox()
	{
		return mailbox;
	}
	
	/**
	 * Retrieves the {@link FlyController} associated with this player.<br>
	 * This controller manages flight-related actions and logic.
	 * @return the {@code FlyController} instance.
	 */
	public FlyController getFlyController()
	{
		return flyController;
	}
	
	/**
	 * Sets the {@link FlyController} for this player.<br>
	 * This method updates the internal reference to the flight controller.
	 * @param flyController The {@code FlyController} instance to assign.
	 */
	public void setFlyController(FlyController flyController)
	{
		this.flyController = flyController;
	}
	
	/**
	 * Retrieves the last time the player was online.<br>
	 * This method returns a Unix timestamp in seconds.<br>
	 * It returns {@code 0} if the player is currently online or has no recorded login history.
	 * @return The last online time as an {@code int} representing seconds, or {@code 0}.
	 */
	public int getLastOnline()
	{
		final Timestamp lastOnline = playerCommonData.getLastOnline();
		if ((lastOnline == null) || isOnline())
		{
			return 0;
		}
		
		return (int) (lastOnline.getTime() / 1000);
	}
	
	/**
	 * Sets the current {@code CraftingTask} for the player.<br>
	 * This updates the internal task state used by the server.
	 * @param craftingTask The {@code CraftingTask} to assign.
	 */
	public void setCraftingTask(CraftingTask craftingTask)
	{
		this.craftingTask = craftingTask;
	}
	
	/**
	 * Retrieves the current {@code CraftingTask} for the player.<br>
	 * This method returns {@code null} if no task is active.
	 * @return The current {@link CraftingTask} object.
	 */
	public CraftingTask getCraftingTask()
	{
		return craftingTask;
	}
	
	/**
	 * Sets the unique identifier for a player's flight teleport.<br>
	 * This value is used to track specific teleportation data.
	 * @param flightTeleportId The {@code int} ID of the flight teleport.
	 */
	public void setFlightTeleportId(int flightTeleportId)
	{
		this.flightTeleportId = flightTeleportId;
	}
	
	/**
	 * Retrieves the unique identifier for the current flight teleport.<br>
	 * This ID is used to track specific teleportation instances.
	 * @return The {@code int} value of the flight teleport ID.
	 */
	public int getFlightTeleportId()
	{
		return flightTeleportId;
	}
	
	/**
	 * Updates the current flight distance for the player.<br>
	 * This method sets the {@code flightDistance} field to a new value.
	 * @param flightDistance The new distance the player can fly.
	 */
	public void setFlightDistance(int flightDistance)
	{
		this.flightDistance = flightDistance;
		
	}
	
	/**
	 * Updates the current flight path for the player.<br>
	 * This method sets the {@code flyLocationId}.<br>
	 * It also updates the {@code flyStartTime} based on whether the provided path is {@code null}.
	 * @param path The new {@link FlyPathEntry} to set as the current flight path.
	 */
	public void setCurrentFlypath(FlyPathEntry path)
	{
		flyLocationId = path;
		if (path != null)
		{
			flyStartTime = System.currentTimeMillis();
		}
		else
		{
			flyStartTime = 0;
		}
	}
	
	/**
	 * Retrieves the current distance traveled while flying.<br>
	 * This value is used to track movement during flight.
	 * @return The total flight distance as an {@code int}.
	 */
	public int getFlightDistance()
	{
		return flightDistance;
	}
	
	/**
	 * Checks if the player is currently performing a flight teleport.<br>
	 * This method verifies that the {@code CreatureState} is set to {@code FLIGHT_TELEPORT}.<br>
	 * It also ensures that the {@code flightTeleportId} is not equal to {@code 0}.
	 * @return {@code true} if the player is in a valid flight teleport state, otherwise {@code false}.
	 */
	public boolean isUsingFlyTeleport()
	{
		return isInState(CreatureState.FLIGHT_TELEPORT) && (flightTeleportId != 0);
	}
	
	/**
	 * Checks if the player has Game Master privileges.<br>
	 * This method compares the current access level against {@code AdminConfig.GM_LEVEL}.
	 * @return {@code true} if the player is a GM, {@code false} otherwise.
	 */
	public boolean isGM()
	{
		return getAccessLevel() >= AdminConfig.GM_LEVEL;
	}
	
	/**
	 * Checks if the specified {@code Creature} is an enemy of this object.<br>
	 * This method checks both directions of hostility between the two creatures.
	 * @param creature The {@code Creature} to check.
	 * @return {@code true} if either creature considers the other an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemy(Creature creature)
	{
		return creature.isEnemyFrom(this) || this.isEnemyFrom(creature);
	}
	
	/**
	 * Checks if the specified {@link Npc} is considered an enemy.<br>
	 * This method evaluates the creature type of the target.<br>
	 * It returns {@code true} for aggressive, attackable, or invulnerable types.
	 * @param enemy The {@link Npc} object to check.
	 * @return {@code true} if the NPC is an enemy, otherwise {@code false}.
	 */
	@Override
	public boolean isEnemyFrom(Npc enemy)
	{
		switch (CreatureType.getCreatureType(enemy.getType(this)))
		{
			case AGGRESSIVE:
			case ATTACKABLE:
			case INVULNERABLE:
				return true;
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Checks if the specified {@link Player} is considered an enemy.<br>
	 * This method verifies if the players are in a valid state for combat.<br>
	 * It returns {@code true} if they can engage in PvP or are currently dueling.
	 * @param enemy The {@link Player} to check against.
	 * @return {@code true} if the player is an enemy, {@code false} otherwise.
	 */
	@Override
	public boolean isEnemyFrom(Player enemy)
	{
		if (getObjectId() == enemy.getObjectId())
		{
			return false;
		}
		else if (((getAdminEnmity() > 1) || (enemy.getAdminEnmity() > 1)))
		{
			return false;
		}
		else if (canPvP(enemy) || getController().isDueling(enemy))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Checks if the current player is hostile toward a specific {@link Player}.<br>
	 * This method returns {@code true} if either player has high admin enmity.<br>
	 * It also returns {@code true} if the two players belong to different races.
	 * @param player The target {@link Player} to check against.
	 * @return {@code true} if the players are hostile, otherwise {@code false}.
	 */
	public boolean isAggroIconTo(Player player)
	{
		if ((getAdminEnmity() > 1) || (player.getAdminEnmity() > 1))
		{
			return true;
		}
		
		return !player.getRace().equals(getRace());
	}
	
	/**
	 * Checks if the player is allowed to engage in PvP with a specific enemy.<br>
	 * This method validates race differences, world map settings, and zone restrictions.<br>
	 * It also verifies team status for specific zone types.
	 * @param enemy The {@code Player} object representing the target.
	 * @return {@code true} if PvP is permitted between both players; {@code false} otherwise.
	 */
	private boolean canPvP(Player enemy)
	{
		final int worldId = enemy.getWorldId();
		if (!enemy.getRace().equals(getRace()))
		{
			if (World.getInstance().getWorldMap(getWorldId()).isPvpAllowed())
			{
				return (!isInDisablePvPZone() && !enemy.isInDisablePvPZone());
			}
			
			return (isInPvPZone() && enemy.isInPvPZone());
		}
		
		if ((worldId != 210020000) && // Elten.
			(worldId != 210040000) && // Heiron.
			(worldId != 210050000) && // Inggison.
			(worldId != 210060000) && // Theobomos.
			(worldId != 210070000) && // Cygnea.
			(worldId != 210100000) && // Iluma.
			(worldId != 220020000) && // Morheim.
			(worldId != 220040000) && // Beluslan.
			(worldId != 220050000) && // Brusthonin.
			(worldId != 220070000) && // Gelkmaros.
			(worldId != 220080000) && // Enshar.
			(worldId != 220110000) && // Norsvold.
			(// \\//\\//\\//\\//\\//
			worldId != 400010000) && // Reshanta.
			(// \\//Panesterra//\\//
			worldId != 400020000) && // Belus.
			(worldId != 400040000) && // Aspida.
			(worldId != 400050000) && // Atanatos.
			(worldId != 400060000) && // Disillon.
			(// \\//\\//\\//\\//\\//
			worldId != 600010000) && // Silentera Canyon.
			(worldId != 600090000) && // Kaldor.
			(worldId != 600100000))
		{
			// Levinshor.
			return (isInsideZoneType(ZoneType.PVP) && enemy.isInsideZoneType(ZoneType.PVP) && !isInSameTeam(enemy));
		}
		
		return false;
	}
	
	/**
	 * Checks if the current player is located in a zone where PvP is disabled.<br>
	 * It iterates through all zones at the player's current position.<br>
	 * Returns {@code true} if any of these zones have PvP turned off.
	 * @return {@code true} if PvP is disabled, {@code false} otherwise.
	 */
	private boolean isInDisablePvPZone()
	{
		final List<ZoneInstance> zones = getPosition().getMapRegion().getZones(this);
		for (ZoneInstance zone : zones)
		{
			if (!zone.isPvpAllowed())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the current player is located in a PvP zone.<br>
	 * It iterates through all zones in the current map region.<br>
	 * Returns {@code true} if any of these zones allow PvP.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the player is in a PvP area, {@code false} otherwise.
	 */
	private boolean isInPvPZone()
	{
		final List<ZoneInstance> zones = getPosition().getMapRegion().getZones(this);
		for (ZoneInstance zone : zones)
		{
			if (zone.isPvpAllowed())
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the given {@code Player} belongs to the same team as this player.<br>
	 * This method compares group, alliance, or league IDs depending on the current game mode.
	 * @param player The {@code Player} object to check against.
	 * @return {@code true} if they are on the same team, {@code false} otherwise.
	 */
	public boolean isInSameTeam(Player player)
	{
		if (isInGroup2() && player.isInGroup2())
		{
			return getPlayerGroup2().getTeamId().equals(player.getPlayerGroup2().getTeamId());
		}
		else if (isInAlliance2() && player.isInAlliance2())
		{
			return getPlayerAlliance2().getObjectId().equals(player.getPlayerAlliance2().getObjectId());
		}
		else if (isInLeague() && player.isInLeague())
		{
			return getPlayerAllianceGroup2().getObjectId().equals(player.getPlayerAllianceGroup2().getObjectId());
		}
		
		return false;
	}
	
	/**
	 * Determines if the current entity can see a specific {@link Creature}.<br>
	 * It checks for special conditions like blinking, team status, or trap ownership.<br>
	 * Finally, it compares the visual states of both entities.
	 * @param creature The {@link Creature} to check visibility for.
	 * @return {@code true} if the creature is visible, {@code false} otherwise.
	 */
	@Override
	public boolean canSee(Creature creature)
	{
		if (creature.isInVisualState(CreatureVisualState.BLINKING) || ((creature instanceof Player) && isInSameTeam((Player) creature)))
		{
			return true;
		}
		
		if ((creature instanceof Trap) && (((Trap) creature).getCreator().getObjectId() == getObjectId()))
		{
			return true;
		}
		
		return creature.getVisualState() <= getSeeState();
	}
	
	/**
	 * Retrieves the {@link TribeClass} for this player.<br>
	 * It first checks if a transform tribe exists.<br>
	 * If no transform is active, it returns a default value based on the player's race.
	 * @return the {@code TribeClass} of the player or their current transformation.
	 */
	@Override
	public TribeClass getTribe()
	{
		final TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null)
		{
			return transformTribe;
		}
		
		return getRace() == Race.ELYOS ? TribeClass.PC : TribeClass.PC_DARK;
	}
	
	/**
	 * Retrieves the base tribe for this player.<br>
	 * It checks if the player is currently transformed and returns the base tribe of that transformation.<br>
	 * If no transformation exists, it returns the player's own tribe.
	 * @return The {@link TribeClass} representing the base tribe.
	 */
	@Override
	public TribeClass getBaseTribe()
	{
		final TribeClass transformTribe = getTransformModel().getTribe();
		if (transformTribe != null)
		{
			return DataManager.TRIBE_RELATIONS_DATA.getBaseTribe(transformTribe);
		}
		
		return getTribe();
	}
	
	/**
	 * Retrieves the current {@code Summon} object associated with this player.
	 * @return the {@code Summon} instance or {@code null} if no summon exists.
	 */
	public Summon getSummon()
	{
		return summon;
	}
	
	/**
	 * Sets the current {@code Summon} for this player.<br>
	 * This method updates the internal {@code summon} field.
	 * @param summon The {@code Summon} object to assign.
	 */
	public void setSummon(Summon summon)
	{
		this.summon = summon;
	}
	
	/**
	 * Retrieves the current {@link SummonedObject} associated with this player.<br>
	 * This method returns the object if it exists, or {@code null} otherwise.
	 * @return The {@link SummonedObject} currently being controlled by the player.
	 */
	public SummonedObject<?> getSummonedObj()
	{
		return summonedObj;
	}
	
	/**
	 * Sets the current {@link SummonedObject} for this player.<br>
	 * This method updates the internal reference to the active summoned entity.
	 * @param summonedObj The {@code SummonedObject} to be assigned.
	 */
	public void setSummonedObj(SummonedObject<?> summonedObj)
	{
		this.summonedObj = summonedObj;
	}
	
	/**
	 * Updates the {@code kisk} value for the player.<br>
	 * This method replaces the current {@code kisk} with a new one.
	 * @param newKisk The new {@code Kisk} object to assign.
	 */
	public void setKisk(Kisk newKisk)
	{
		kisk = newKisk;
	}
	
	/**
	 * Retrieves the {@code Kisk} object associated with this player.
	 * @return The {@code Kisk} instance.
	 */
	public Kisk getKisk()
	{
		return kisk;
	}
	
	/**
	 * Checks if an item is currently disabled due to a cooldown.<br>
	 * This method verifies the delay status from the {@code ItemUseLimits} object.<br>
	 * It returns {@code true} if the item is still on cooldown and {@code false} otherwise.
	 * @param limits The configuration containing the item's delay information.
	 * @return {@code true} if the item cannot be used yet, {@code false} if it can be used.
	 */
	public boolean isItemUseDisabled(ItemUseLimits limits)
	{
		if ((limits == null) || (itemCoolDowns == null) || !itemCoolDowns.containsKey(limits.getDelayId()))
		{
			return false;
		}
		
		final Long coolDown = itemCoolDowns.get(limits.getDelayId()).getReuseTime();
		if (coolDown <= 0)
		{
			return false;
		}
		
		if (coolDown < System.currentTimeMillis())
		{
			itemCoolDowns.remove(limits.getDelayId());
			return false;
		}
		
		return true;
	}
	
	/**
	 * Retrieves the remaining reuse time for a specific item cooldown.<br>
	 * It checks if the {@code delayId} exists in the internal cooldown map.<br>
	 * If the ID is not found, it returns 0.
	 * @param delayId The unique identifier for the item's cooldown.
	 * @return The remaining reuse time as a {@code long}, or 0 if no cooldown exists.
	 */
	public long getItemCoolDown(int delayId)
	{
		if ((itemCoolDowns == null) || !itemCoolDowns.containsKey(delayId))
		{
			return 0;
		}
		
		return itemCoolDowns.get(delayId).getReuseTime();
	}
	
	/**
	 * Retrieves the current cooldown status for all items.<br>
	 * This map links an item ID to its specific {@link ItemCooldown} data.
	 * @return A {@code Map} where keys are item IDs and values are their cooldowns.
	 */
	public Map<Integer, ItemCooldown> getItemCoolDowns()
	{
		return itemCoolDowns;
	}
	
	/**
	 * Adds a new cooldown entry for an item to the internal tracking map.<br>
	 * This method ensures the {@code itemCoolDowns} map is initialized before adding data.
	 * @param delayId The unique identifier for the specific item delay.
	 * @param time The duration of the cooldown in milliseconds.
	 * @param useDelay The additional delay value applied to the item usage.
	 */
	public void addItemCoolDown(int delayId, long time, int useDelay)
	{
		if (itemCoolDowns == null)
		{
			itemCoolDowns = new ConcurrentHashMap<>();
		}
		
		itemCoolDowns.put(delayId, new ItemCooldown(time, useDelay));
	}
	
	/**
	 * Removes a specific item from the active cooldown list.<br>
	 * This method checks if {@code itemCoolDowns} exists before attempting removal.
	 * @param itemMask The unique identifier for the item to remove.
	 */
	public void removeItemCoolDown(int itemMask)
	{
		if (itemCoolDowns == null)
		{
			return;
		}
		
		itemCoolDowns.remove(itemMask);
	}
	
	/**
	 * Updates the gag status of the player.<br>
	 * This method sets whether the player is currently silenced or not.
	 * @param isGagged The new gag status to apply. Use {@code true} to silence the player and {@code false} to allow speech.
	 */
	public void setGagged(boolean isGagged)
	{
		this.isGagged = isGagged;
	}
	
	/**
	 * Checks if the player is currently gagged.<br>
	 * This status determines if the player can communicate with others.
	 * @return {@code true} if the player is gagged, {@code false} otherwise.
	 */
	public boolean isGagged()
	{
		return isGagged;
	}
	
	/**
	 * Checks if the player has admin teleportation enabled.<br>
	 * This is used to verify permissions for moving between locations.
	 * @return {@code true} if the player can teleport as an admin, {@code false} otherwise.
	 */
	public boolean getAdminTeleportation()
	{
		return isAdminTeleportation;
	}
	
	/**
	 * Sets whether the player is currently using admin teleportation.<br>
	 * This flag determines if the movement should be treated as an administrative action.
	 * @param isAdminTeleportation The {@code boolean} value to set for admin teleportation status.
	 */
	public void setAdminTeleportation(boolean isAdminTeleportation)
	{
		this.isAdminTeleportation = isAdminTeleportation;
	}
	
	/**
	 * Checks if the current cooldown period has ended.<br>
	 * Returns {@code true} if the cooldown is at zero.<br>
	 * Returns {@code false} if the cooldown is still active.
	 * @return {@code true} if there is no cooldown, otherwise {@code false}.
	 */
	public boolean isCoolDownZero()
	{
		return cooldownZero;
	}
	
	/**
	 * Updates the {@code cooldownZero} status of the player.<br>
	 * This determines if the player's cooldown is currently set to zero.
	 * @param cooldownZero The new value to set for the cooldown status.
	 */
	public void setCoolDownZero(boolean cooldownZero)
	{
		this.cooldownZero = cooldownZero;
	}
	
	/**
	 * Sets whether the player can activate a resurrection. <br>
	 * This updates the {@code isResByPlayer} status.
	 * @param isActivated The new activation state to set. Use {@code true} to enable or {@code false} to disable.
	 */
	public void setPlayerResActivate(boolean isActivated)
	{
		isResByPlayer = isActivated;
	}
	
	/**
	 * Checks if the player is currently in a resurrection state.<br>
	 * This method returns {@code true} if the player is being resurrected by another player.<br>
	 * It returns {@code false} otherwise.
	 * @return {@code true} if the player is being resurrected, {@code false} otherwise.
	 */
	public boolean getResStatus()
	{
		return isResByPlayer;
	}
	
	/**
	 * Retrieves the current resurrection skill of the player.<br>
	 * This value is used to determine which skill is active for reviving others.
	 * @return The {@code int} ID of the resurrection skill.
	 */
	public int getResurrectionSkill()
	{
		return resurrectionSkill;
	}
	
	/**
	 * Sets the unique identifier for the player's resurrection skill.<br>
	 * This updates the {@code resurrectionSkill} field in the current object.
	 * @param resurrectionSkill The ID of the skill to assign.
	 */
	public void setResurrectionSkill(int resurrectionSkill)
	{
		this.resurrectionSkill = resurrectionSkill;
	}
	
	/**
	 * Sets whether the player can fly before they die.<br>
	 * This updates the {@code isFlyingBeforeDeath} flag.
	 * @param isActivated The state to set for flying before death. Use {@code true} to enable or {@code false} to disable.
	 */
	public void setIsFlyingBeforeDeath(boolean isActivated)
	{
		isFlyingBeforeDeath = isActivated;
	}
	
	/**
	 * Checks if the player was in a flying state before they died.<br>
	 * This status helps determine specific behaviors during the death sequence.
	 * @return {@code true} if the player was flying, {@code false} otherwise.
	 */
	public boolean getIsFlyingBeforeDeath()
	{
		return isFlyingBeforeDeath;
	}
	
	/**
	 * Retrieves the alliance associated with this player.<br>
	 * It checks if the player belongs to an alliance group first.<br>
	 * Returns {@code null} if no alliance is found.
	 * @return The {@link com.aionemu.gameserver.model.team2.alliance.PlayerAlliance} object or {@code null}.
	 */
	public com.aionemu.gameserver.model.team2.alliance.PlayerAlliance getPlayerAlliance2()
	{
		return playerAllianceGroup != null ? playerAllianceGroup.getAlliance() : null;
	}
	
	/**
	 * Retrieves the alliance group associated with the player.<br>
	 * This method returns the {@code PlayerAllianceGroup} object.
	 * @return the current {@code PlayerAllianceGroup} of the player.
	 */
	public PlayerAllianceGroup getPlayerAllianceGroup2()
	{
		return playerAllianceGroup;
	}
	
	/**
	 * Checks if the player belongs to an alliance group.<br>
	 * It returns {@code true} if the group is not {@code null}.<br>
	 * Otherwise, it returns {@code false}.
	 * @return {@code true} if the player is in an alliance, {@code false} otherwise.
	 */
	public boolean isInAlliance2()
	{
		return playerAllianceGroup != null;
	}
	
	/**
	 * Sets the second alliance group for the player.<br>
	 * This updates the {@code playerAllianceGroup} field of the current object.
	 * @param playerAllianceGroup The new {@link PlayerAllianceGroup} to assign.
	 */
	public void setPlayerAllianceGroup2(PlayerAllianceGroup playerAllianceGroup)
	{
		this.playerAllianceGroup = playerAllianceGroup;
	}
	
	/**
	 * Checks if the player is currently in a league.<br>
	 * This method verifies both alliance status and league membership.
	 * @return {@code true} if the player belongs to a league, {@code false} otherwise.
	 */
	public boolean isInLeague()
	{
		return isInAlliance2() && getPlayerAlliance2().isInLeague();
	}
	
	/**
	 * Checks if the player is currently part of a team.<br>
	 * This method returns {@code true} if the player belongs to either group 2 or alliance 2.
	 * @return {@code true} if the player is in a team, otherwise {@code false}.
	 */
	public boolean isInTeam()
	{
		return isInGroup2() || isInAlliance2();
	}
	
	/**
	 * Retrieves the current team for the player.<br>
	 * It checks if the player is in a group first.<br>
	 * If not, it returns the player's alliance.
	 * @return The {@code TemporaryPlayerTeam} of the player.
	 */
	public TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentTeam()
	{
		return isInGroup2() ? getPlayerGroup2() : getPlayerAlliance2();
	}
	
	/**
	 * Retrieves the current group for the player.<br>
	 * It checks if the player is in a secondary group first.<br>
	 * If not, it returns the alliance group instead.
	 * @return The {@code TemporaryPlayerTeam} of the current group.
	 */
	public TemporaryPlayerTeam<? extends TeamMember<Player>> getCurrentGroup()
	{
		return isInGroup2() ? getPlayerGroup2() : getPlayerAllianceGroup2();
	}
	
	/**
	 * Retrieves the unique identifier for the player's current team.<br>
	 * This method checks if the player is currently in a team.<br>
	 * If the player is not in a team, it returns {@code 0}.
	 * @return The {@code int} ID of the current team or {@code 0} if no team exists.
	 */
	public int getCurrentTeamId()
	{
		return isInTeam() ? getCurrentTeam().getTeamId() : 0;
	}
	
	/**
	 * Retrieves the list of current portal cooldowns.<br>
	 * This method returns a {@code PortalCooldownList} object.
	 * @return The list of active portal cooldowns.
	 */
	public PortalCooldownList getPortalCooldownList()
	{
		return portalCooldownList;
	}
	
	/**
	 * Retrieves the list of crafting cooldowns for the player.<br>
	 * This method returns a {@code CraftCooldownList} object.
	 * @return The current {@code CraftCooldownList}.
	 */
	public CraftCooldownList getCraftCooldownList()
	{
		return craftCooldownList;
	}
	
	/**
	 * Retrieves the list of cooldowns for house objects.<br>
	 * This method returns the current {@code HouseObjectCooldownList}.
	 * @return The list of house object cooldowns.
	 */
	public HouseObjectCooldownList getHouseObjectCooldownList()
	{
		return houseObjectCooldownList;
	}
	
	/**
	 * Updates the current editing state of the player.<br>
	 * This method sets whether the player is currently in {@code edit_mode}.
	 * @param edit_mode The new boolean value for the editing state.
	 */
	public void setEditMode(boolean edit_mode)
	{
		this.edit_mode = edit_mode;
	}
	
	/**
	 * Checks if the player is currently in edit mode.<br>
	 * This status determines if certain actions are restricted or allowed.
	 * @return {@code true} if the player is in edit mode, {@code false} otherwise.
	 */
	public boolean isInEditMode()
	{
		return edit_mode;
	}
	
	/**
	 * Retrieves the {@link Npc} object associated with the postman.
	 * @return the {@code Npc} instance of the postman.
	 */
	public Npc getPostman()
	{
		return postman;
	}
	
	/**
	 * Assigns a specific {@link Npc} to the player as their postman.<br>
	 * This updates the internal {@code postman} field of the player object.
	 * @param postman The {@link Npc} object to be set as the postman.
	 */
	public void setPostman(Npc postman)
	{
		this.postman = postman;
	}
	
	/**
	 * Retrieves the {@link Account} associated with this player.<br>
	 * This method returns the current account data object.
	 * @return the {@code Account} object for the player.
	 */
	public Account getPlayerAccount()
	{
		return playerAccount;
	}
	
	/**
	 * Checks if a specific quest has been finished.<br>
	 * This method looks up the status of the quest by its ID.<br>
	 * It returns {@code true} only if the status is {@code COMPLETE}.
	 * @param questId The unique identifier for the quest to check.
	 * @return {@code true} if the quest is finished, otherwise {@code false}.
	 */
	public boolean isCompleteQuest(int questId)
	{
		final QuestState qs = getQuestStateList().getQuestState(questId);
		
		if (qs == null)
		{
			return false;
		}
		
		return qs.getStatus() == QuestStatus.COMPLETE;
	}
	
	/**
	 * Retrieves the timestamp for the next allowed skill use.<br>
	 * This value is used to manage skill cooldowns.
	 * @return the {@code long} timestamp of the next available skill use.
	 */
	public long getNextSkillUse()
	{
		return nextSkillUse;
	}
	
	/**
	 * Sets the timestamp for when the next skill can be used.<br>
	 * This value is stored in the {@code nextSkillUse} field.
	 * @param nextSkillUse The time in milliseconds when the player is allowed to use a skill again.
	 */
	public void setNextSkillUse(long nextSkillUse)
	{
		this.nextSkillUse = nextSkillUse;
	}
	
	/**
	 * Retrieves the timestamp for the next allowed summon skill use.<br>
	 * This value is used to manage cooldowns for summoning abilities.
	 * @return The {@code long} timestamp of the next available summon skill use.
	 */
	public long getNextSummonSkillUse()
	{
		return nextSummonSkillUse;
	}
	
	/**
	 * Sets the timestamp for the next allowed summon skill use.<br>
	 * This value is used to manage cooldowns for summoning abilities.
	 * @param nextSummonSkillUse The {@code long} value representing the next available time.
	 */
	public void setNextSummonSkillUse(long nextSummonSkillUse)
	{
		this.nextSummonSkillUse = nextSummonSkillUse;
	}
	
	/**
	 * Retrieves the {@code ChainSkills} object for the player.<br>
	 * If the object is {@code null}, it creates a new instance.
	 * @return The current {@link ChainSkills} object.
	 */
	public ChainSkills getChainSkills()
	{
		if (chainSkills == null)
		{
			chainSkills = new ChainSkills();
		}
		
		return chainSkills;
	}
	
	/**
	 * Updates the timestamp of the last used counter skill.<br>
	 * This method checks the player's class to determine which status to record.<br>
	 * It handles {@code DODGE}, {@code PARRY}, {@code BLOCK}, and {@code RESIST} statuses.
	 * @param status The {@link AttackStatus} to update in the counter skill map.
	 */
	public void setLastCounterSkill(AttackStatus status)
	{
		final long time = System.currentTimeMillis();
		
		// Dodge
		if (((AttackStatus.getBaseStatus(status) == AttackStatus.DODGE) && (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.WARRIOR)) || (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.SCOUT) || (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.ENGINEER))
		{
			lastCounterSkill.put(AttackStatus.DODGE, time);
		}
		// Parry
		else if (((AttackStatus.getBaseStatus(status) == AttackStatus.PARRY) && (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.WARRIOR)) || (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.PRIEST) || (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.ENGINEER))
		{
			lastCounterSkill.put(AttackStatus.PARRY, time);
		}
		// Block
		else if ((AttackStatus.getBaseStatus(status) == AttackStatus.BLOCK) && (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.WARRIOR))
		{
			lastCounterSkill.put(AttackStatus.BLOCK, time);
		}
		// Resist
		else if (((AttackStatus.getBaseStatus(status) == AttackStatus.RESIST) && (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.WARRIOR)) || (PlayerClass.getStartingClassFor(getPlayerClass()) == PlayerClass.ENGINEER))
		{
			lastCounterSkill.put(AttackStatus.RESIST, time);
		}
	}
	
	/**
	 * Retrieves the timestamp of the last counter skill used for a specific status.<br>
	 * It checks if a record exists for the provided {@code AttackStatus}.<br>
	 * If no record is found, it returns {@code 0}.
	 * @param status The {@link AttackStatus} to check for the last counter skill.
	 * @return The timestamp of the last counter skill or {@code 0} if none exists.
	 */
	public long getLastCounterSkill(AttackStatus status)
	{
		if (lastCounterSkill.get(status) == null)
		{
			return 0;
		}
		
		return lastCounterSkill.get(status);
	}
	
	/**
	 * Retrieves the current value of the dual effect.<br>
	 * This value is used to calculate specific character bonuses.
	 * @return The {@code int} value of the dual effect.
	 */
	public int getDualEffectValue()
	{
		return dualEffectValue;
	}
	
	/**
	 * Updates the current value for the dual effect.<br>
	 * This method sets the {@code dualEffectValue} field for the player.
	 * @param dualEffectValue The new integer value to assign to the dual effect.
	 */
	public void setDualEffectValue(int dualEffectValue)
	{
		this.dualEffectValue = dualEffectValue;
	}
	
	/**
	 * Checks if the player is currently in a resurrection position state.<br>
	 * This is used to determine if the character is waiting to be revived.
	 * @return {@code true} if the player is in a resurrection state, {@code false} otherwise.
	 */
	public boolean isInResPostState()
	{
		return isInResurrectPosState;
	}
	
	/**
	 * Updates the resurrection position state of the player.<br>
	 * This method sets whether the player is currently in a resurrection state.
	 * @param value The {@code boolean} value to set for the resurrection position state.
	 */
	public void setResPosState(boolean value)
	{
		isInResurrectPosState = value;
	}
	
	/**
	 * Sets the horizontal position of the player's resurrection point.<br>
	 * This updates the {@code resPosX} field with a new coordinate.
	 * @param value The new horizontal position to set.
	 */
	public void setResPosX(float value)
	{
		resPosX = value;
	}
	
	/**
	 * Retrieves the current X coordinate of the resurrection point.<br>
	 * This value is used to determine where a player will reappear.
	 * @return The {@code float} value representing the X position.
	 */
	public float getResPosX()
	{
		return resPosX;
	}
	
	/**
	 * Sets the vertical position of the player's resurrection point.<br>
	 * This updates the {@code resPosY} field with a new coordinate.
	 * @param value The new Y coordinate for the resurrection point.
	 */
	public void setResPosY(float value)
	{
		resPosY = value;
	}
	
	/**
	 * Retrieves the current Y coordinate of the player's resurrection point.<br>
	 * This value is used to determine the vertical position where a player reappears.
	 * @return The {@code float} value representing the Y coordinate.
	 */
	public float getResPosY()
	{
		return resPosY;
	}
	
	/**
	 * Sets the Z coordinate for the player's resurrection position.<br>
	 * This updates the {@code resPosZ} field with a new value.
	 * @param value The new Z coordinate to set.
	 */
	public void setResPosZ(float value)
	{
		resPosZ = value;
	}
	
	/**
	 * Retrieves the current Z position of the player.<br>
	 * This value represents the height in the game world.
	 * @return The {@code float} value of the Z coordinate.
	 */
	public float getResPosZ()
	{
		return resPosZ;
	}
	
	/**
	 * Checks if the player is currently in a siege world.<br>
	 * This method compares the current {@code getWorldId()} against known siege IDs.
	 * @return {@code true} if the player is in a siege world, otherwise {@code false}.
	 */
	public boolean isInSiegeWorld()
	{
		switch (getWorldId())
		{
			case 210050000:
			case 220070000:
			case 400070000:
			case 800020000:
			case 800030000:
			case 800040000:
			case 800050000:
			case 800060000:
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if the player is currently inside a PvP Arena.<br>
	 * This method verifies the current world ID against known arena locations.
	 * @return {@code true} if the player is in an arena, otherwise {@code false}.
	 */
	public boolean isInPvPArena()
	{
		switch (getWorldId())
		{
			case 300350000:
			case 300360000:
			case 300420000:
			case 300430000:
			case 300450000:
			case 300550000:
			case 300570000:
				return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if the player is currently under a no-fly restriction.<br>
	 * This method queries the {@link com.aionemu.gameserver.controllers.effect.PlayerEffectController}.<br>
	 * It returns {@code true} if the {@code NOFLY} state is active.
	 * @return {@code true} if the player cannot fly, {@code false} otherwise.
	 */
	public boolean isUnderNoFly()
	{
		return getEffectController().isAbnormalSet(AbnormalState.NOFLY);
	}
	
	/**
	 * Sets whether the player is exempt from FPC consumption.<br>
	 * This flag determines if certain resources are consumed during specific actions.
	 * @param value The boolean value to set for {@code underNoFPConsum}.
	 */
	public void setUnderNoFPConsum(boolean value)
	{
		underNoFPConsum = value;
	}
	
	/**
	 * Checks if the player is currently in a state where FPC consumption is disabled.<br>
	 * This status determines whether certain actions will consume resources.
	 * @return {@code true} if FPC consumption is disabled, {@code false} otherwise.
	 */
	public boolean isUnderNoFPConsum()
	{
		return underNoFPConsum;
	}
	
	/**
	 * Sets the starting coordinates for an instance.<br>
	 * This method updates the {@code instanceStartPosX}, {@code instanceStartPosY}, and {@code instanceStartPosZ} values.
	 * @param instanceStartPosX The X coordinate of the instance start position.
	 * @param instanceStartPosY The Y coordinate of the instance start position.
	 * @param instanceStartPosZ The Z coordinate of the instance start position.
	 */
	public void setInstanceStartPos(float instanceStartPosX, float instanceStartPosY, float instanceStartPosZ)
	{
		this.instanceStartPosX = instanceStartPosX;
		this.instanceStartPosY = instanceStartPosY;
		this.instanceStartPosZ = instanceStartPosZ;
	}
	
	/**
	 * Gets the starting X position of the instance.<br>
	 * This value is used to determine where a player begins in an instance.
	 * @return The {@code float} value representing the start X position.
	 */
	public float getInstanceStartPosX()
	{
		return instanceStartPosX;
	}
	
	/**
	 * Retrieves the starting Y position of the instance.<br>
	 * This value is used to determine where a player begins in an instance.
	 * @return The {@code float} value representing the starting Y coordinate.
	 */
	public float getInstanceStartPosY()
	{
		return instanceStartPosY;
	}
	
	/**
	 * Gets the starting Z coordinate for the player instance.<br>
	 * This value represents the vertical position where the instance begins.
	 * @return The {@code float} value of the starting Z position.
	 */
	public float getInstanceStartPosZ()
	{
		return instanceStartPosZ;
	}
	
	/**
	 * Checks if the player has a specific membership level.<br>
	 * It compares the account's membership value against the required {@code byte}.
	 * @param perm The minimum membership level required for the action.
	 * @return {@code true} if the player meets or exceeds the requirement, {@code false} otherwise.
	 */
	public boolean havePermission(byte perm)
	{
		return playerAccount.getMembership() >= perm;
	}
	
	/**
	 * Retrieves the list of current emotions for the player.<br>
	 * This method returns a {@code EmotionList} object.
	 * @return The list of current emotions.
	 */
	public EmotionList getEmotions()
	{
		return emotions;
	}
	
	/**
	 * Updates the current list of emotions for the player.<br>
	 * This method replaces the existing {@code emotions} collection.
	 * @param emotions The new {@code EmotionList} to assign to the player.
	 */
	public void setEmotions(EmotionList emotions)
	{
		this.emotions = emotions;
	}
	
	/**
	 * Retrieves the current resurrection percentage for a rebirth. <br>
	 * This value is used to determine how much progress has been made.
	 * @return The resurrection percentage as an {@code int}.
	 */
	public int getRebirthResurrectPercent()
	{
		return rebirthResurrectPercent;
	}
	
	/**
	 * Sets the percentage for resurrection upon rebirth.<br>
	 * This value determines how much health a player recovers when they are reborn.
	 * @param rebirthResurrectPercent The percentage value to set.
	 */
	public void setRebirthResurrectPercent(int rebirthResurrectPercent)
	{
		this.rebirthResurrectPercent = rebirthResurrectPercent;
	}
	
	/**
	 * Retrieves the current rebirth skill of the player.<br>
	 * This value is used to determine which special ability is active.
	 * @return The integer ID of the rebirth skill.
	 */
	public int getRebirthSkill()
	{
		return rebirthSkill;
	}
	
	/**
	 * Updates the current rebirth skill for the player.<br>
	 * This method sets the {@code rebirthSkill} value in the player object.
	 * @param rebirthSkill The new skill ID to assign to the player.
	 */
	public void setRebirthSkill(int rebirthSkill)
	{
		this.rebirthSkill = rebirthSkill;
	}
	
	/**
	 * Retrieves the current binding point of the player.<br>
	 * This method returns the {@code BindPointPosition} object associated with this entity.
	 * @return The current {@code BindPointPosition}.
	 */
	public BindPointPosition getBindPoint()
	{
		return bindPoint;
	}
	
	/**
	 * Sets the current {@code BindPointPosition} for the player.<br>
	 * This updates the internal {@code bindPoint} field.
	 * @param bindPoint The new {@code BindPointPosition} to assign.
	 */
	public void setBindPoint(BindPointPosition bindPoint)
	{
		this.bindPoint = bindPoint;
	}
	
	public int speedHackCounter;
	public int abnormalHackCounter;
	public long prevPosUT;
	public byte prevMoveType;
	private WorldPosition prevPos;
	
	/**
	 * Retrieves the attack type of the item held in the main hand.<br>
	 * If no weapon is equipped, it returns {@code ItemAttackType.PHYSICAL}.
	 * @return the {@link ItemAttackType} of the main hand weapon or physical.
	 */
	@Override
	public ItemAttackType getAttackType()
	{
		final Item weapon = getEquipment().getMainHandWeapon();
		if (weapon != null)
		{
			return weapon.getItemTemplate().getAttackType();
		}
		
		return ItemAttackType.PHYSICAL;
	}
	
	/**
	 * Gets the time when the player started flying.
	 * @return The start time as a {@code long}.
	 */
	public long getFlyStartTime()
	{
		return flyStartTime;
	}
	
	/**
	 * Retrieves the current flight path identifier for the player.<br>
	 * This method returns the {@code flyLocationId} associated with the character.
	 * @return The unique identifier of the current flight path.
	 */
	public FlyPathEntry getCurrentFlyPath()
	{
		return flyLocationId;
	}
	
	/**
	 * Sets the player state to not be wispable.<br>
	 * This method updates the {@code isWispable} flag to {@code false}.
	 */
	public void setUnWispable()
	{
		isWispable = false;
	}
	
	/**
	 * Enables the ability for this player to be wispable.<br>
	 * Sets the {@code isWispable} flag to {@code true}.
	 */
	public void setWispable()
	{
		isWispable = true;
	}
	
	/**
	 * Checks if the player can be wisped.<br>
	 * This method returns {@code true} if the player is eligible for wisping.
	 * @return {@code true} if the player is wispable, otherwise {@code false}.
	 */
	public boolean isWispable()
	{
		return isWispable;
	}
	
	/**
	 * Checks if the player is currently under an invulnerable wing.<br>
	 * This status determines if the player can take damage while flying.
	 * @return {@code true} if the player is invulnerable, {@code false} otherwise.
	 */
	public boolean isInvulnerableWing()
	{
		return isUnderInvulnerableWing;
	}
	
	/**
	 * Sets whether the player is currently under an invulnerable wing.<br>
	 * This flag determines if the player can be damaged while using a wing.
	 * @param value The {@code boolean} status to set.
	 */
	public void setInvulnerableWing(boolean value)
	{
		isUnderInvulnerableWing = value;
	}
	
	/**
	 * Resets the {@code abyssRankListUpdateMask} to {@code 0}.<br>
	 * This method clears any pending updates for the Abyss Rank list.
	 */
	public void resetAbyssRankListUpdated()
	{
		abyssRankListUpdateMask = 0;
	}
	
	/**
	 * Updates the internal mask to track which abyss rank list categories have changed.<br>
	 * This method uses the provided {@code AbyssRankUpdateType} to set specific bits in the update mask.
	 * @param type The specific {@code AbyssRankUpdateType} that was updated.
	 */
	public void setAbyssRankListUpdated(AbyssRankUpdateType type)
	{
		abyssRankListUpdateMask |= type.value();
	}
	
	/**
	 * Checks if the Abyss Rank list has been updated for a specific type.<br>
	 * It compares the update mask against the provided {@code AbyssRankUpdateType}.
	 * @param type The {@code AbyssRankUpdateType} to check.
	 * @return {@code true} if the rank list was updated, otherwise {@code false}.
	 */
	public boolean isAbyssRankListUpdated(AbyssRankUpdateType type)
	{
		return (abyssRankListUpdateMask & type.value()) == type.value();
	}
	
	/**
	 * Adds a specific amount of salvation points to the player.<br>
	 * This method updates the {@code PlayerCommonData} and sends a status packet to the client.
	 * @param points The number of points to add.
	 */
	public void addSalvationPoints(long points)
	{
		playerCommonData.addSalvationPoints(points);
		PacketSendUtility.sendPacket(this, new SM_STATS_INFO(this));
	}
	
	/**
	 * Determines if the current entity is a player character.<br>
	 * Returns {@code 1} for a normal player and {@code 2} if it is a GM.
	 * @return A byte value representing the entity type.
	 */
	@Override
	public byte isPlayer()
	{
		if (isGM())
		{
			return 2;
		}
		
		return 1;
	}
	
	/**
	 * Retrieves the list of current motions for the player.
	 * @return a {@code MotionList} containing all active motions.
	 */
	public MotionList getMotions()
	{
		return motions;
	}
	
	/**
	 * Updates the current list of motions for the player.<br>
	 * This method assigns a new {@code MotionList} to the internal motion field.
	 * @param motions The new {@code MotionList} to be assigned.
	 */
	public void setMotions(MotionList motions)
	{
		this.motions = motions;
	}
	
	/**
	 * Updates the active status of the player's transformation model.<br>
	 * This method sets whether the current transformation is active or not.
	 * @param value The {@code boolean} state to set for the transformation.
	 */
	public void setTransformed(boolean value)
	{
		getTransformModel().setActive(value);
	}
	
	/**
	 * Checks if the player is currently in a transformed state.<br>
	 * This method returns {@code true} if the transformation model is active.
	 * @return {@code true} if the player is transformed, {@code false} otherwise.
	 */
	public boolean isTransformed()
	{
		return getTransformModel().isActive();
	}
	
	/**
	 * Retrieves the list of NPC factions for the player.<br>
	 * This method returns the {@code NpcFactions} object associated with this entity.
	 * @return The {@code NpcFactions} data.
	 */
	public NpcFactions getNpcFactions()
	{
		return npcFactions;
	}
	
	/**
	 * Updates the faction relationships for NPCs.<br>
	 * This method sets the {@code npcFactions} field of the player.
	 * @param npcFactions The new {@code NpcFactions} object to assign.
	 */
	public void setNpcFactions(NpcFactions npcFactions)
	{
		this.npcFactions = npcFactions;
	}
	
	/**
	 * Retrieves the remaining time for flying reuse.<br>
	 * This value is used to determine when a player can fly again.
	 * @return The current reuse time as a {@code long}.
	 */
	public long getFlyReuseTime()
	{
		return flyReuseTime;
	}
	
	/**
	 * Sets the time required before a player can reuse the fly skill.<br>
	 * This value is stored in milliseconds.
	 * @param flyReuseTime The cooldown time for reusing flight.
	 */
	public void setFlyReuseTime(long flyReuseTime)
	{
		this.flyReuseTime = flyReuseTime;
	}
	
	/**
	 * Updates the flying status of the player.<br>
	 * This method sets whether the player is currently allowed to fly.
	 * @param value The new flying state to apply. Use {@code true} for flying and {@code false} for walking.
	 */
	public void setFlyingMode(boolean value)
	{
		isFlying = value;
	}
	
	/**
	 * Checks if the player is currently in flying mode.
	 * @return {@code true} if the player can fly, {@code false} otherwise.
	 */
	public boolean isInFlyingMode()
	{
		return isFlying;
	}
	
	/**
	 * Retrieves the self-revival stone for the player.<br>
	 * This method checks several specific item IDs in a priority order.<br>
	 * It returns the first available {@code Item} found from the list.
	 * @return The {@code Item} object if found, or {@code null} if no stones are available.
	 */
	public Item getSelfRezStone()
	{
		Item item = null;
		item = getReviveStone(161001001);
		item = getReviveStone(161001004);
		item = getReviveStone(161001005);
		if (item == null)
		{
			item = getReviveStone(161000003); // Reviving Elemental Stone
		}
		
		if (item == null)
		{
			item = getReviveStone(161000004); // Tombstone Of Revival
		}
		
		if (item == null)
		{
			item = getReviveStone(161000005); // Reviving Elemental Stone
		}
		
		return item;
	}
	
	/**
	 * Retrieves a revive stone from the player inventory based on its ID.<br>
	 * It checks if the item is usable before returning it.
	 * @param stoneId The unique identifier of the stone to find.
	 * @return The {@code Item} object if found and usable, or {@code null}.
	 */
	private Item getReviveStone(int stoneId)
	{
		Item item = getInventory().getFirstItemByItemId(stoneId);
		if ((item != null) && isItemUseDisabled(item.getItemTemplate().getUseLimits()))
		{
			item = null;
		}
		
		return item;
	}
	
	/**
	 * Checks if the player currently possesses a self-resurrection item.<br>
	 * This method returns {@code true} if the self-rez stone is not {@code null}.
	 * @return {@code true} if the item exists, otherwise {@code false}.
	 */
	public boolean haveSelfRezItem()
	{
		return (getSelfRezStone() != null);
	}
	
	/**
	 * Checks if the player has a self-resurrection effect active.<br>
	 * This method verifies if the player meets the admin requirements or possesses a {@code RebirthEffect}.
	 * @return {@code true} if the player can self-resurrect, otherwise {@code false}.
	 */
	public boolean haveSelfRezEffect()
	{
		if (getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES)
		{
			return true;
		}
		
		// Store the effect info.
		final List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects)
		{
			for (EffectTemplate template : effect.getEffectTemplates())
			{
				if ((template.getEffectid() == 160) && (template instanceof RebirthEffect))
				{
					final RebirthEffect rebirthEffect = (RebirthEffect) template;
					setRebirthResurrectPercent(rebirthEffect.getResurrectPercent());
					setRebirthSkill(rebirthEffect.getSkillId());
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the player currently has a resurrection effect active.<br>
	 * It searches through all abnormal effects for a specific {@code ResurrectBaseEffect}.
	 * @return {@code true} if the resurrection base effect is found, otherwise {@code false}
	 */
	public boolean hasResurrectBase()
	{
		final List<Effect> effects = getEffectController().getAbnormalEffects();
		for (Effect effect : effects)
		{
			for (EffectTemplate template : effect.getEffectTemplates())
			{
				if ((template.getEffectid() == 160) && (template instanceof ResurrectBaseEffect))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Resets the resurrection position state for the player.<br>
	 * This method checks if the player is currently in a resurrection post state.<br>
	 * If true, it sets the state to {@code false} and resets all coordinates to {@code 0}.
	 */
	public void unsetResPosState()
	{
		if (isInResPostState())
		{
			setResPosState(false);
			setResPosX(0);
			setResPosY(0);
			setResPosZ(0);
		}
	}
	
	/**
	 * Retrieves the loot group rules for the player.<br>
	 * This method checks if the player is in a group or an alliance.<br>
	 * It returns {@code null} if neither condition is met.
	 * @return the {@link LootGroupRules} object or {@code null}.
	 */
	public LootGroupRules getLootGroupRules()
	{
		if (isInGroup2())
		{
			return getPlayerGroup2().getLootGroupRules();
		}
		
		if (isInAlliance2())
		{
			return getPlayerAlliance2().getLootGroupRules();
		}
		
		return null;
	}
	
	/**
	 * Checks if the player is currently interacting with an NPC to loot.<br>
	 * This method returns {@code true} if the {@code lootingNpcOid} is not {@code 0}.
	 * @return {@code true} if the player is looting, {@code false} otherwise.
	 */
	public boolean isLooting()
	{
		return lootingNpcOid != 0;
	}
	
	/**
	 * Sets the unique identifier for the NPC responsible for looting.<br>
	 * This value is stored in the {@code lootingNpcOid} field.
	 * @param lootingNpcOid The unique ID of the looting NPC.
	 */
	public void setLootingNpcOid(int lootingNpcOid)
	{
		this.lootingNpcOid = lootingNpcOid;
	}
	
	/**
	 * Retrieves the unique identifier for the NPC currently being looted.<br>
	 * This value is used to track which target a player is interacting with.
	 * @return The {@code int} ID of the looting NPC.
	 */
	public int getLootingNpcOid()
	{
		return lootingNpcOid;
	}
	
	/**
	 * Checks if the current player has mentor status.
	 * @return {@code true} if the player is a mentor, {@code false} otherwise.
	 */
	public boolean isMentor()
	{
		return isMentor;
	}
	
	/**
	 * Updates the mentor status of the player.<br>
	 * This method sets whether the current player is a mentor or not.
	 * @param isMentor The new mentor status to set.
	 */
	public void setMentor(boolean isMentor)
	{
		this.isMentor = isMentor;
	}
	
	/**
	 * Retrieves the {@code Race} of the player.<br>
	 * This method returns the current character race.
	 * @return The {@link Race} of the player.
	 */
	@Override
	public Race getRace()
	{
		return playerCommonData.getRace();
	}
	
	/**
	 * Finds a {@link Player} object based on a specific ID.<br>
	 * This method uses the global {@code World} instance to locate the player.
	 * @return The {@code Player} object if found, or {@code null} otherwise.
	 */
	public Player findPartner()
	{
		return World.getInstance().findPlayer(partnerId);
	}
	
	private PlayerVarsDAO daoVars = DAOManager.getDAO(PlayerVarsDAO.class);
	private Map<String, Object> vars = new HashMap<>();
	
	/**
	 * Checks if a specific variable exists for the player.<br>
	 * This method looks into the internal variables map.
	 * @param key The name of the variable to check.
	 * @return {@code true} if the key exists, otherwise {@code false}.
	 */
	public boolean hasVar(String key)
	{
		return vars.containsKey(key);
	}
	
	/**
	 * Updates a variable for the player.<br>
	 * This method saves the value to the local map.<br>
	 * If {@code sql} is {@code true}, it also updates the database.
	 * @param key The unique identifier for the variable.
	 * @param value The data to be stored.
	 * @param sql Set to {@code true} to persist the change in the database.
	 */
	public void setVar(String key, Object value, boolean sql)
	{
		vars.put(key, value);
		if (sql)
		{
			daoVars.set(getObjectId(), key, value);
		}
	}
	
	/**
	 * Retrieves a value from the player's variables map.<br>
	 * This method looks up the data associated with the provided {@code key}.
	 * @param key The unique identifier for the variable to retrieve.
	 * @return The object associated with the key, or {@code null} if it does not exist.
	 */
	public Object getVar(String key)
	{
		return vars.get(key);
	}
	
	/**
	 * Retrieves an integer value associated with a specific key.<br>
	 * This method looks up the variable in the internal storage.<br>
	 * If the key is not found, it returns {@code 0}.
	 * @param key The unique identifier for the variable to retrieve.
	 * @return The integer value of the variable or {@code 0} if it does not exist.
	 */
	public int getVarInt(String key)
	{
		final Object o = vars.get(key);
		if (o != null)
		{
			return Integer.parseInt(o.toString());
		}
		
		return 0;
	}
	
	/**
	 * Retrieves a variable value associated with the provided key.<br>
	 * It converts the object to a {@code String}.
	 * @param key The unique identifier for the variable.
	 * @return The string representation of the value, or {@code null} if not found.
	 */
	public String getVarStr(String key)
	{
		final Object o = vars.get(key);
		if (o != null)
		{
			return o.toString();
		}
		
		return null;
	}
	
	/**
	 * Updates the internal variables of the player.<br>
	 * This method replaces the current variable map with a new one.
	 * @param map The {@code Map<String, Object>} containing the new variables.
	 */
	public void setVars(Map<String, Object> map)
	{
		vars = map;
	}
	
	/**
	 * Checks if the player is currently married.<br>
	 * It returns {@code true} if a valid partner exists.
	 * @return {@code true} if the player is married, {@code false} otherwise.
	 */
	public boolean isMarried()
	{
		return partnerId != 0;
	}
	
	/**
	 * Sets the unique identifier for the player's partner.<br>
	 * This updates the {@code partnerId} field in the current object.
	 * @param partnerId The unique ID of the partner to assign.
	 */
	public void setPartnerId(int partnerId)
	{
		this.partnerId = partnerId;
	}
	
	/**
	 * Retrieves the current cooldown time for a specific skill.<br>
	 * This method checks if the skill is currently on cooldown.<br>
	 * If it is not, it returns {@code 0}.<br>
	 * Otherwise, it returns the value from the provided {@code SkillTemplate}.
	 * @param template The {@code SkillTemplate} containing the skill data.
	 * @return The remaining cooldown duration as an {@code int}.
	 */
	@Override
	public int getSkillCooldown(SkillTemplate template)
	{
		return isCoolDownZero() ? 0 : template.getCooldown();
	}
	
	/**
	 * Retrieves the cooldown time for a specific item.<br>
	 * This value is fetched from the {@code ItemTemplate}.
	 * @param template The {@link ItemTemplate} to check.
	 * @return The delay time as an {@code int}.
	 */
	@Override
	public int getItemCooldown(ItemTemplate template)
	{
		return isCoolDownZero() ? 0 : template.getUseLimits().getDelayTime();
	}
	
	/**
	 * Updates the timestamp of the last received message.<br>
	 * This method checks if messages are being sent too quickly based on {@code SecurityConfig.FLOOD_DELAY}.<br>
	 * It increments the {@code floodMsgCount} if the limit is exceeded or resets it to {@code 0} otherwise.<br>
	 * Finally, it updates the internal {@code lastMsgTime} to the current system time.
	 */
	public void setLastMessageTime()
	{
		if (((System.currentTimeMillis() - lastMsgTime) / 1000) < SecurityConfig.FLOOD_DELAY)
		{
			floodMsgCount++;
		}
		else
		{
			floodMsgCount = 0;
		}
		
		lastMsgTime = System.currentTimeMillis();
	}
	
	/**
	 * Retrieves the current number of flood messages.<br>
	 * This value is used to track message frequency for a player.
	 * @return The total count of flood messages as an {@code int}.
	 */
	public int floodMsgCount()
	{
		return floodMsgCount;
	}
	
	/**
	 * Updates the current online time for the player.<br>
	 * This method sets {@code onlineTime} to the current system time in milliseconds.
	 */
	public void setOnlineTime()
	{
		onlineTime = System.currentTimeMillis();
	}
	
	/*
	 * return online time in sec
	 */
	/**
	 * Calculates the total time the player has been online.<br>
	 * This value is returned in seconds.
	 * @return The number of seconds spent online as a {@code long}.
	 */
	public long getOnlineTime()
	{
		return (System.currentTimeMillis() - onlineTime) / 1000;
	}
	
	/**
	 * Updates the status of whether a command was used.<br>
	 * This method sets the {@code isCommandUsed} flag to the provided {@code boolean} value.
	 * @param value The new status to set for the command usage.
	 */
	public void setCommandUsed(boolean value)
	{
		isCommandUsed = value;
	}
	
	/**
	 * Checks if a command is currently being used.
	 * @return {@code true} if the command is in use, {@code false} otherwise.
	 */
	public boolean isCommandInUse()
	{
		return isCommandUsed;
	}
	
	/**
	 * Updates the rebirth revive status of the player.<br>
	 * This method sets the {@code rebirthRevive} flag to the provided value.
	 * @param result The new boolean value for the rebirth revive status.
	 */
	public void setRebirthRevive(boolean result)
	{
		rebirthRevive = result;
	}
	
	/**
	 * Checks if the player is allowed to use the Rebirth Revive feature.<br>
	 * This method returns the current state of the {@code rebirthRevive} flag.
	 * @return {@code true} if the player can use Rebirth Revive, {@code false} otherwise.
	 */
	public boolean canUseRebirthRevive()
	{
		return rebirthRevive;
	}
	
	/**
	 * Updates the player's subtracted supplements information.<br>
	 * This method sets the amount and the specific ID of the removed supplement.
	 * @param count The number of supplements to subtract.
	 * @param supplementId The unique identifier for the supplement.
	 */
	public void subtractSupplements(int count, int supplementId)
	{
		subtractedSupplementsCount = count;
		subtractedSupplementId = supplementId;
	}
	
	/**
	 * Updates the player inventory by removing a specific amount of supplements.<br>
	 * This method checks if any items were subtracted before modifying the {@code Inventory}.<br>
	 * It resets the count and ID values to {@code 0} after the update is complete.
	 */
	public void updateSupplements()
	{
		if ((subtractedSupplementId == 0) || (subtractedSupplementsCount == 0))
		{
			return;
		}
		
		getInventory().decreaseByItemId(subtractedSupplementId, subtractedSupplementsCount);
		subtractedSupplementsCount = 0;
		subtractedSupplementId = 0;
	}
	
	/**
	 * Retrieves the current animation state for the player's port.
	 * @return The {@code int} value representing the port animation.
	 */
	public int getPortAnimation()
	{
		return portAnimation;
	}
	
	/**
	 * Updates the animation state for the player's port.<br>
	 * This method sets the {@code portAnimation} value for the current object.
	 * @param portAnimation The new animation ID to apply to the port.
	 */
	public void setPortAnimation(int portAnimation)
	{
		this.portAnimation = portAnimation;
	}
	
	/**
	 * Checks if a specific skill is currently disabled.<br>
	 * This method evaluates cooldowns and special chain condition rules.<br>
	 * It returns {@code true} if the skill cannot be used at this time.
	 * @param template The {@link SkillTemplate} to check for availability.
	 * @return {@code true} if the skill is disabled, {@code false} otherwise.
	 */
	@Override
	public boolean isSkillDisabled(SkillTemplate template)
	{
		final ChainCondition cond = template.getChainCondition();
		if ((cond != null) && (cond.getSelfCount() > 0))
		{// exception for multicast
			final int chainCount = getChainSkills().getChainCount(this, template, cond.getCategory());
			if ((chainCount > 0) && (chainCount < cond.getSelfCount()) && getChainSkills().chainSkillEnabled(cond.getCategory(), cond.getTime()))
			{
				return false;
			}
		}
		
		return super.isSkillDisabled(template);
	}
	
	/**
	 * Updates the daily count for a specific item.<br>
	 * This method checks if an entry exists for {@code itemId}.<br>
	 * If it exists, it updates the value to {@code thisCount}.<br>
	 * Otherwise, it creates a new {@link MaxCountOfDay} object.
	 * @param itemId The unique identifier of the item.
	 * @param thisCount The current count to record for the day.
	 */
	public void addItemMaxCountOfDay(int itemId, int thisCount)
	{
		if (maxCountEvent == null)
		{
			maxCountEvent = new ConcurrentHashMap<>();
		}
		
		if (maxCountEvent.get(itemId) != null)
		{
			maxCountEvent.get(itemId).setThisCount(thisCount);
		}
		else
		{
			maxCountEvent.put(itemId, new MaxCountOfDay(thisCount));
		}
	}
	
	/**
	 * Retrieves the current count for a specific item from the maximum count events.<br>
	 * It checks if the {@code itemId} exists in the configuration map.<br>
	 * If the item is not found, it returns {@code 0}.
	 * @param itemId The unique identifier of the item to check.
	 * @return The current count for the specified item or {@code 0} if not found.
	 */
	public int getItemMaxThisCount(int itemId)
	{
		if ((maxCountEvent == null) || !maxCountEvent.containsKey(itemId))
		{
			return 0;
		}
		
		return maxCountEvent.get(itemId).getThisCount();
	}
	
	/**
	 * Removes a specific item from the maximum count event list.<br>
	 * This method checks if {@code maxCountEvent} is not {@code null}.<br>
	 * If it exists, it removes the entry matching the provided {@code itemId}.
	 * @param itemId The unique identifier of the item to remove.
	 */
	public void removeItemMaxThisCount(int itemId)
	{
		if (maxCountEvent == null)
		{
			return;
		}
		
		maxCountEvent.remove(itemId);
	}
	
	/**
	 * Clears the list of events associated with the maximum count.<br>
	 * This method checks if {@code maxCountEvent} is {@code null} before clearing it.
	 */
	public void clearItemMaxThisCount()
	{
		if (maxCountEvent == null)
		{
			return;
		}
		
		maxCountEvent.clear();
	}
	
	/**
	 * Retrieves the maximum counts for events recorded today.<br>
	 * This method returns a map where keys are item IDs and values are {@link MaxCountOfDay} objects.
	 * @return A {@code Map<Integer, MaxCountOfDay>} containing the daily maximum counts.
	 */
	public Map<Integer, MaxCountOfDay> getItemMaxThisCounts()
	{
		return maxCountEvent;
	}
	
	/**
	 * Retrieves the list of houses owned by the player.<br>
	 * This method fetches data from {@link HousingService} if the local cache is empty.
	 * @return A {@code List<House>} containing all houses found for this object.
	 */
	public List<House> getHouses()
	{
		if (houses == null)
		{
			final List<House> found = HousingService.getInstance().searchPlayerHouses(getObjectId());
			if (found.size() > 0)
			{
				houses = found;
			}
			else
			{
				return found;
			}
		}
		
		return houses;
	}
	
	/**
	 * Clears all houses associated with the player.<br>
	 * This method sets the {@code houses} list to {@code null}.
	 */
	public void resetHouses()
	{
		if (houses != null)
		{
			houses.clear();
			houses = null;
		}
	}
	
	/**
	 * Retrieves the first active house belonging to the player.<br>
	 * A house is considered active if its status is {@code ACTIVE} or {@code SELL_WAIT}.<br>
	 * This method returns {@code null} if no such house is found.
	 * @return The first {@link House} with an active status, or {@code null}.
	 */
	public House getActiveHouse()
	{
		for (House house : getHouses())
		{
			if ((house.getStatus() == HouseStatus.ACTIVE) || (house.getStatus() == HouseStatus.SELL_WAIT))
			{
				return house;
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieves the unique identifier of the owner for the currently active house.<br>
	 * If no house is active, it returns {@code 0}.
	 * @return The {@code int} ID of the house owner or {@code 0} if none exists.
	 */
	public int getHouseOwnerId()
	{
		final House house = getActiveHouse();
		if (house != null)
		{
			return house.getAddress().getId();
		}
		
		return 0;
	}
	
	/**
	 * Retrieves the current {@link HouseRegistry} for the player.<br>
	 * This registry contains information about houses owned by the user.
	 * @return The {@code HouseRegistry} object associated with this player.
	 */
	public HouseRegistry getHouseRegistry()
	{
		return houseRegistry;
	}
	
	/**
	 * Sets the {@code HouseRegistry} for this player.<br>
	 * This method updates the internal registry used to manage houses.
	 * @param houseRegistry The {@link HouseRegistry} object to assign.
	 */
	public void setHouseRegistry(HouseRegistry houseRegistry)
	{
		this.houseRegistry = houseRegistry;
	}
	
	/**
	 * Retrieves the current states of building owners.<br>
	 * This value is stored as a {@code byte}.
	 * @return The {@code byte} representing building owner states.
	 */
	public byte getBuildingOwnerStates()
	{
		return buildingOwnerStates;
	}
	
	/**
	 * Checks if the player is currently in a specific building state.<br>
	 * This method compares the current flags against the provided {@code PlayerHouseOwnerFlags}.
	 * @param state The house owner flag to check.
	 * @return {@code true} if the player is in the specified state, otherwise {@code false}.
	 */
	public boolean isBuildingInState(PlayerHouseOwnerFlags state)
	{
		return (buildingOwnerStates & state.getId()) != 0;
	}
	
	/**
	 * Updates the current building owner states.<br>
	 * This method applies a new {@code byte} state to the existing flags.<br>
	 * If an active house is found, it triggers a refresh of all building states.
	 * @param state The new state value to apply.
	 */
	public void setBuildingOwnerState(byte state)
	{
		buildingOwnerStates |= state;
		final House house = getActiveHouse();
		if (house != null)
		{
			house.fixBuildingStates();
		}
	}
	
	/**
	 * Removes a specific state from the building owner states.<br>
	 * This method updates the internal bitmask and refreshes the house status if a house is active.
	 * @param state The {@code byte} value representing the state to remove.
	 */
	public void unsetBuildingOwnerState(byte state)
	{
		buildingOwnerStates &= ~state;
		final House house = getActiveHouse();
		if (house != null)
		{
			house.fixBuildingStates();
		}
	}
	
	/**
	 * Retrieves the coordinates where a player should return after a battle.<br>
	 * This data is used to determine the destination point for the player.
	 * @return a {@code float[]} array containing the X and Y coordinates.
	 */
	public float[] getBattleReturnCoords()
	{
		return battleReturnCoords;
	}
	
	/**
	 * Sets the coordinates where a player should return after a battle.<br>
	 * This method updates both the map ID and the specific location coordinates.
	 * @param mapId The unique identifier for the destination map.
	 * @param coords An array of floats representing the X and Y coordinates on that map.
	 */
	public void setBattleReturnCoords(int mapId, float[] coords)
	{
		battleReturnMap = mapId;
		battleReturnCoords = coords;
	}
	
	/**
	 * Retrieves the current battle return map.<br>
	 * This value represents the location where players are sent after a battle.
	 * @return the {@code int} value of the battle return map.
	 */
	public int getBattleReturnMap()
	{
		return battleReturnMap;
	}
	
	/**
	 * Checks if the player is currently in sprint mode.
	 * @return {@code true} if the player is sprinting, {@code false} otherwise.
	 */
	public boolean isInSprintMode()
	{
		return isInSprintMode;
	}
	
	/**
	 * Updates the sprint mode status for the player.<br>
	 * This method sets whether the player is currently sprinting.
	 * @param isInSprintMode The new sprint state to apply. Set to {@code true} if sprinting, or {@code false} otherwise.
	 */
	public void setSprintMode(boolean isInSprintMode)
	{
		this.isInSprintMode = isInSprintMode;
	}
	
	/**
	 * Registers a new observer to monitor ride actions.<br>
	 * This method adds the provided {@code ActionObserver} to the internal list.<br>
	 * If the list is currently {@code null}, it initializes a new {@code ArrayList}.
	 * @param observer The {@link ActionObserver} to be added.
	 */
	public void setRideObservers(ActionObserver observer)
	{
		if (rideObservers == null)
		{
			rideObservers = new ArrayList<>(3);
		}
		
		rideObservers.add(observer);
	}
	
	/**
	 * Retrieves the list of observers for riding actions.<br>
	 * These observers monitor specific behaviors related to riding.
	 * @return a {@code List} of {@link ActionObserver} objects.
	 */
	public List<ActionObserver> getRideObservers()
	{
		return rideObservers;
	}
	
	/**
	 * Checks if the player is currently in Game Master mode.<br>
	 * This status allows for special administrative privileges.
	 * @return {@code true} if the player is a Game Master, {@code false} otherwise.
	 */
	public boolean isGmMode()
	{
		return isGmMode;
	}
	
	/**
	 * Sets whether the player is in Game Master mode.<br>
	 * This updates the {@code isGmMode} flag for the current player.
	 * @param isGmMode The boolean value to set for GM status. Use {@code true} to enable and {@code false} to disable.
	 */
	public void setGmMode(boolean isGmMode)
	{
		this.isGmMode = isGmMode;
	}
	
	/**
	 * Checks if the player is currently in AFK mode.
	 * @return {@code true} if the player is away, {@code false} otherwise.
	 */
	public boolean isAFKMode()
	{
		return afkmode;
	}
	
	/**
	 * Updates the AFK status of the player.<br>
	 * This method sets whether the player is currently away from keyboard.
	 * @param afkmode The new {@code boolean} value to set for AFK mode.
	 */
	public void setAFKMode(boolean afkmode)
	{
		this.afkmode = afkmode;
	}
	
	/**
	 * Retrieves the current value of the timer.
	 * @return The current {@code int} value of the timer.
	 */
	public int getTimer()
	{
		return timer;
	}
	
	/**
	 * Sets the current value of the {@code timer}.<br>
	 * This updates the internal timer variable for this player.
	 * @param timer The new integer value to assign to the timer.
	 */
	public void setTimer(int timer)
	{
		this.timer = timer;
	}
	
	/**
	 * Retrieves the remaining time for the player's online bonus.<br>
	 * The value is returned in milliseconds.
	 * @return The current online bonus time as a {@code long}.
	 */
	public long getOnlineBonusTime()
	{
		return onlineBonusTime;
	}
	
	/**
	 * Sets the amount of online bonus time for the player.<br>
	 * This updates the {@code onlineBonusTime} field with a new value.
	 * @param value The new amount of online bonus time to set.
	 */
	public void setOnlineBonusTime(long value)
	{
		onlineBonusTime = value;
	}
	
	/**
	 * Checks if the status was successfully added.
	 * @return {@code true} if the status was added, {@code false} otherwise.
	 */
	public boolean getStatus()
	{
		return addedStatus;
	}
	
	/**
	 * Updates the current status of the player.<br>
	 * This method sets the {@code addedStatus} field to a new value.
	 * @param addedStatus The new boolean status to apply.
	 */
	public void addStatus(boolean addedStatus)
	{
		this.addedStatus = addedStatus;
	}
	
	/**
	 * Bans a player from the game world for a specific period.<br>
	 * This method updates the local state and saves the ban to the database.<br>
	 * It schedules an automatic unban if the duration is greater than {@code 0}.
	 * @param by The name of the administrator who issued the ban.
	 * @param reason The explanation for why the player was banned.
	 * @param duration The length of time in milliseconds that the ban will last.
	 * @return {@code true} if the ban was successfully applied, or {@code false} otherwise.
	 */
	public boolean banFromWorld(String by, String reason, long duration)
	{
		if (isBannedFromWorld())
		{
			return false;
		}
		
		bannedFromWorld = true;
		bannedFromWorldDate = Calendar.getInstance().getTime();
		bannedFromWorldDuring = duration;
		bannedFromWorldBy = by;
		bannedFromWorldReason = reason;
		final PlayerWorldBanDAO dao = DAOManager.getDAO(PlayerWorldBanDAO.class);
		if (!dao.addWorldBan(getObjectId(), by, bannedFromWorldDuring, bannedFromWorldDate, bannedFromWorldReason))
		{
			return false;
		}
		
		if (bannedFromWorldDuring > 0)
		{
			scheduleUnbanFromWorld();
		}
		
		return true;
	}
	
	/**
	 * Retrieves the specific chat command associated with a channel ID.<br>
	 * This method maps internal IDs to their corresponding string commands.
	 * @param chanId The unique identifier for the chat channel.
	 * @return A {@code String} representing the command, or an empty string if not found.
	 */
	public static String getChanCommand(int chanId)
	{
		switch (chanId)
		{
			case CHAT_FIXED_ON_ASMOS:
				return "." + "asmo";
			case CHAT_FIXED_ON_ELYOS:
				return "." + "ely";
			case CHAT_FIXED_ON_WORLD:
				return "." + "tg";
			case CHAT_FIXED_ON_BOTH:
				return "." + "2race";
		}
		
		return "";
	}
	
	/**
	 * Checks if the player is currently banned from the world.<br>
	 * This method returns {@code true} if a ban exists.
	 * @return {@code true} if the player is banned, {@code false} otherwise.
	 */
	public boolean isBannedFromWorld()
	{
		return bannedFromWorld;
	}
	
	/**
	 * Removes the world ban for this player.<br>
	 * This method updates the {@code bannedFromWorld} status to {@code false}.<br>
	 * It also removes the record from the database and cancels any pending unban tasks.
	 * @return {@code true} if the unban was successful.
	 */
	public boolean unbanFromWorld()
	{
		bannedFromWorld = false;
		final PlayerWorldBanDAO dao = DAOManager.getDAO(PlayerWorldBanDAO.class);
		cancelUnbanFromWorld();
		dao.removeWorldBan(getObjectId());
		return true;
	}
	
	/**
	 * Schedules a task to remove the world ban for this player.<br>
	 * It checks if the player is currently banned before scheduling.<br>
	 * The unban occurs automatically after the specified duration expires.
	 */
	public void scheduleUnbanFromWorld()
	{
		if (!isBannedFromWorld())
		{
			throw new RuntimeException("scheduling unban task when not banned from " + getChanCommand(CHAT_FIX_WORLD_CHANNEL));
		}
		
		cancelUnbanFromWorld();
		final int playerObjId = getObjectId();
		final String playerName = getName();
		final String adminName = bannedFromWorldBy;
		final long time = bannedFromWorldDuring;
		if (time > 0)
		{
			final Date endDate = new Date(bannedFromWorldDate.getTime() + bannedFromWorldDuring);
			taskToUnbanFromWorld = ThreadPoolManager.getInstance().schedule(() ->
			{
				final World world = World.getInstance();
				final Player player = world.findPlayer(playerName);
				final Player admin = world.findPlayer(adminName);
				if (endDate.getTime() <= Calendar.getInstance().getTimeInMillis())
				{
					DAOManager.getDAO(PlayerWorldBanDAO.class).removeWorldBan(playerObjId);
				}
				
				if (player != null)
				{
					player.bannedFromWorld = false;
					PacketSendUtility.sendBrightYellowMessageOnCenter(player, "You're not anymore banned from chat channels");
				}
				
				if (admin != null)
				{
					PacketSendUtility.sendBrightYellowMessageOnCenter(admin, "The player " + playerName + " is no more banned from chat channels");
				}
			}, time);
		}
	}
	
	/**
	 * Cancels the scheduled task to unban a player from the world.<br>
	 * This method checks if {@code taskToUnbanFromWorld} is not {@code null}.<br>
	 * If it exists, it calls {@code cancel} with {@code false}.<br>
	 * It then sets the task reference to {@code null}.
	 */
	private void cancelUnbanFromWorld()
	{
		if (taskToUnbanFromWorld != null)
		{
			taskToUnbanFromWorld.cancel(false);
			taskToUnbanFromWorld = null;
		}
	}
	
	/**
	 * Retrieves the name of the person who issued the world ban.<br>
	 * This value is stored in the {@code bannedFromWorldBy} field.
	 * @return The name of the administrator or system that performed the ban as a {@code String}.
	 */
	public String getBannedFromWorldBy()
	{
		return bannedFromWorldBy;
	}
	
	/**
	 * Retrieves the reason why a player was banned from the world.<br>
	 * This value is stored in the {@code bannedFromWorldReason} field.
	 * @return The string containing the ban reason or {@code null} if no reason exists.
	 */
	public String getBannedFromWorldReason()
	{
		return bannedFromWorldReason;
	}
	
	/**
	 * Retrieves the remaining time for a player's world ban.<br>
	 * It calculates the difference between the ban duration and the current time.<br>
	 * If no ban is active, it returns {@code "indetermin?"}.
	 * @return A formatted string representing the remaining time.
	 */
	public String getBannedFromWorldRemainingTime()
	{
		long elapsed = 0;
		if (bannedFromWorldDuring == 0)
		{
			return "indetermin?";
		}
		
		elapsed = bannedFromWorldDuring - (Calendar.getInstance().getTimeInMillis() - bannedFromWorldDate.getTime());
		return HumanTime.approximately(elapsed - (elapsed % 1000));
	}
	
	/**
	 * Bans a player from the game world.<br>
	 * This method updates the ban status and details for the player.
	 * @param by The name of the person who issued the ban.
	 * @param reason The explanation for why the player was banned.
	 * @param duration The length of time the ban should last in milliseconds.
	 * @param date The start date and time of the ban.
	 */
	public void setBannedFromWorld(String by, String reason, long duration, Date date)
	{
		bannedFromWorld = true;
		bannedFromWorldBy = by;
		bannedFromWorldDate = date;
		bannedFromWorldDuring = duration;
		bannedFromWorldReason = reason;
	}
	
	/**
	 * Increases the current {@code wordBanTime} value.<br>
	 * This method doubles the existing ban duration for a player.
	 */
	public void increaseWordBanTime()
	{
		wordBanTime += wordBanTime;
	}
	
	/**
	 * Retrieves the remaining time for a word ban.<br>
	 * This value represents how many seconds are left until the player can use chat again.
	 * @return The number of seconds remaining in the word ban.
	 */
	public int getWordBanTime()
	{
		return wordBanTime;
	}
	
	/**
	 * Checks if the auto-group feature is enabled.<br>
	 * This value determines if the player can automatically join a group.
	 * @return The current status of the {@code useAutoGroup} setting as an {@code int}.
	 */
	public int getUseAutoGroup()
	{
		return useAutoGroup;
	}
	
	/**
	 * Sets whether the player uses auto-grouping.<br>
	 * This updates the {@code useAutoGroup} field for the current player.
	 * @param useAutoGroup The value to set for auto-grouping.
	 */
	public void setUseAutoGroup(int useAutoGroup)
	{
		this.useAutoGroup = useAutoGroup;
	}
	
	/**
	 * Checks if the player is currently using a robot.
	 * @return {@code true} if the player is using a robot, {@code false} otherwise.
	 */
	public boolean isUseRobot()
	{
		return robot;
	}
	
	/**
	 * Sets whether the player is identified as a robot.<br>
	 * This flag determines if the character behaves like an automated bot.
	 * @param robot The boolean value to set for the robot status.
	 */
	public void setUseRobot(boolean robot)
	{
		this.robot = robot;
	}
	
	/**
	 * Retrieves the unique identifier for the robot.<br>
	 * This value is used to identify specific automated entities.
	 * @return The {@code int} ID of the robot.
	 */
	public int getRobotId()
	{
		return robotId;
	}
	
	/**
	 * Sets the unique identifier for a robot.<br>
	 * This value is used to identify specific automated entities.
	 * @param robotId The {@code int} ID of the robot to assign.
	 */
	public void setRobotId(int robotId)
	{
		this.robotId = robotId;
	}
	
	/**
	 * Retrieves the previous world position of the player.<br>
	 * This method returns {@code null} if the current position is not spawned.<br>
	 * If the previous position is missing or on a different map, it initializes a new one based on the current coordinates.
	 * @return the {@link WorldPosition} of the previous location, or {@code null} if invalid.
	 */
	public WorldPosition getPrevPos()
	{
		if ((getPosition() == null) || !getPosition().isSpawned())
		{
			return null;
		}
		
		if ((prevPos == null) || (prevPos.getMapId() != getPosition().getMapId()))
		{
			prevPos = new WorldPosition(getPosition().getMapId());
			prevPos.setXYZH(getPosition().getX(), getPosition().getY(), getPosition().getZ(), getPosition().getHeading());
		}
		
		return prevPos;
	}
	
	/**
	 * Retrieves the current location of the player.<br>
	 * This method gets the position from the {@link PlayerCommonData} object.
	 * @return the {@code WorldPosition} of the player.
	 */
	@Override
	public WorldPosition getPosition()
	{
		return playerCommonData.getPosition();
	}
	
	/**
	 * Updates the current location of this object.<br>
	 * This method sets the {@code position} field to a new {@link WorldPosition}.
	 * @param position The new {@link WorldPosition} to assign to this object.
	 */
	@Override
	public void setPosition(WorldPosition position)
	{
		playerCommonData.setPosition(position);
	}
	
	/**
	 * Retrieves the absolute statistics for the player.<br>
	 * This method returns the {@code AbsoluteStatOwner} object.
	 * @return The {@code AbsoluteStatOwner} containing the stats.
	 */
	public AbsoluteStatOwner getAbsoluteStats()
	{
		return absStatsHolder;
	}
	
	/**
	 * Checks if the player is currently on the fast track.<br>
	 * This status determines if specific movement or action speeds are applied.
	 * @return {@code true} if the player is on the fast track, {@code false} otherwise.
	 */
	public boolean isOnFastTrack()
	{
		return isOnFastTrack;
	}
	
	/**
	 * Updates the fast track status for the player.<br>
	 * This method sets whether the player is currently on a fast track.
	 * @param isOnFastTrack The new status to set. Use {@code true} to enable or {@code false} to disable.
	 */
	public void setOnFastTrack(boolean isOnFastTrack)
	{
		this.isOnFastTrack = isOnFastTrack;
	}
	
	/**
	 * Checks if the player is currently in a live party.
	 * @return {@code true} if the player is in a live party, {@code false} otherwise.
	 */
	public boolean isInLiveParty()
	{
		return isInLiveParty;
	}
	
	/**
	 * Updates the status of whether the player is currently in a live party.<br>
	 * This method sets the {@code isInLiveParty} flag to either {@code true} or {@code false}.
	 * @param isInLiveParty The new status for the live party membership.
	 */
	public void setInLiveParty(boolean isInLiveParty)
	{
		this.isInLiveParty = isInLiveParty;
	}
	/*
	 * public int getLinkedSkill() { return linkedSkill; } public void setLinkedSkill(int skillId) { this.linkedSkill = skillId; }
	 */
	
	/**
	 * Resets the join request data for the current player.<br>
	 * This method clears the legion ID and state in {@code PlayerCommonData}.<br>
	 * It also calls {@code clearJoinRequest} to update the database.
	 */
	public void clearJoinRequest()
	{
		playerCommonData.setJoinRequestLegionId(0);
		playerCommonData.setJoinRequestState(LegionJoinRequestState.NONE);
		DAOManager.getDAO(PlayerDAO.class).clearJoinRequest(getObjectId());
	}
	
	/**
	 * Retrieves the arcade upgrade data for the player.<br>
	 * This method fetches information from the {@code PlayerCommonData}.
	 * @return the {@code PlayerUpgradeArcade} object associated with this player.
	 */
	public PlayerUpgradeArcade getUpgradeArcade()
	{
		return playerCommonData.getUpgradeArcade();
	}
	
	/**
	 * Retrieves the protector data for a player who has conquered territory.<br>
	 * If no data exists, it creates a new {@code PlayerConquererProtectorData} object.
	 * @return The {@code PlayerConquererProtectorData} associated with this player.
	 */
	public PlayerConquererProtectorData getConquerorProtectorData()
	{
		if (conquerorProtectorData == null)
		{
			conquerorProtectorData = new PlayerConquererProtectorData();
		}
		
		return conquerorProtectorData;
	}
	
	/**
	 * Updates the internal protector data for a player.<br>
	 * This method assigns the provided {@code PlayerConquererProtectorData} to the current object.
	 * @param conquerorDefenderData The new data to be assigned.
	 */
	public void setConquerorDefenderData(PlayerConquererProtectorData conquerorDefenderData)
	{
		conquerorProtectorData = conquerorDefenderData;
	}
	
	/**
	 * Checks if the player currently possesses an abyss bonus.<br>
	 * This is used to determine if specific rewards or effects are active.
	 * @return {@code true} if the bonus is active, {@code false} otherwise.
	 */
	public boolean hasAbyssBonus()
	{
		return hasAbyssBonus;
	}
	
	/**
	 * Updates the status of the Abyss bonus for the player.<br>
	 * This method sets whether the player currently possesses the bonus.
	 * @param hasAbyssBonus The boolean value to set for the abyss bonus.
	 */
	public void setAbyssBonus(boolean hasAbyssBonus)
	{
		this.hasAbyssBonus = hasAbyssBonus;
	}
	
	/**
	 * Retrieves the unique identifier for the current abyss.<br>
	 * This value is used to identify which specific abyss instance a player is in.
	 * @return The {@code int} ID of the abyss.
	 */
	public int getAbyssId()
	{
		return abyssId;
	}
	
	/**
	 * Sets the unique identifier for the abyss.<br>
	 * This method updates the {@code abyssId} field of the player object.
	 * @param id The new integer value to assign to the abyss ID.
	 */
	public void setAbyssId(int id)
	{
		abyssId = id;
	}
	
	/**
	 * Updates the bonus status of the player.<br>
	 * This method sets the {@code hasBonus} field to a new value.
	 * @param hasBonus The boolean value to set for the bonus status.
	 */
	public void setBonus(boolean hasBonus)
	{
		this.hasBonus = hasBonus;
	}
	
	/**
	 * Retrieves the unique identifier for the player's bonus.<br>
	 * This value is used to identify specific rewards or perks.
	 * @return The {@code int} ID of the bonus.
	 */
	public int getBonusId()
	{
		return bonusId;
	}
	
	/**
	 * Sets the unique identifier for a bonus.<br>
	 * This method updates the {@code bonusId} field of the player.
	 * @param id The new integer ID to assign to the bonus.
	 */
	public void setBonusId(int id)
	{
		bonusId = id;
	}
	
	/**
	 * Retrieves the unique identifier for the player's bonus.<br>
	 * This ID is used to track specific rewards or bonuses assigned to a {@link Player}.
	 * @return The integer ID of the player's bonus.
	 */
	public int getPlayersBonusId()
	{
		return playersBonusId;
	}
	
	/**
	 * Sets the unique identifier for the player bonus.<br>
	 * This value is used to track specific bonuses assigned to a {@code Player}.
	 * @param id The new bonus ID to assign.
	 */
	public void setPlayersBonusId(int id)
	{
		playersBonusId = id;
	}
	
	/**
	 * Updates the status of whether the player is new.<br>
	 * This method sets the {@code newPlayer} field to the provided value.
	 * @param b The boolean value to set for the new player status.
	 */
	public void setNew(boolean b)
	{
		newPlayer = b;
	}
	
	/**
	 * Checks if the player is currently classified as a new player.<br>
	 * This status is determined by the internal {@code newPlayer} flag.
	 * @return {@code true} if the player is new, {@code false} otherwise.
	 */
	public boolean isNewPlayer()
	{
		return newPlayer;
	}
	
	/**
	 * Retrieves the current bonus time for the player.<br>
	 * This value is used to track active time-based rewards.
	 * @return The {@code PlayerBonusTime} object containing the bonus details.
	 */
	public PlayerBonusTime getBonusTime()
	{
		return bonusTime;
	}
	
	/**
	 * Updates the {@code bonusTime} for the player.<br>
	 * This method assigns a new {@link PlayerBonusTime} object to the current instance.
	 * @param bonusTime The new {@code PlayerBonusTime} to set.
	 */
	public void setBonusTime(PlayerBonusTime bonusTime)
	{
		this.bonusTime = bonusTime;
	}
	
	/**
	 * Retrieves the date when the player account was first created.<br>
	 * This method fetches the timestamp from {@link PlayerCommonData}.<br>
	 * If no creation date exists, it returns {@code 0}.
	 * @return The creation time as a {@code long} value.
	 */
	public long getCreationDate()
	{
		final Timestamp creationDate = playerCommonData.getCreationDate();
		if (creationDate == null)
		{
			return 0;
		}
		
		return creationDate.getTime();
	}
	
	/**
	 * Sets the creation day for the player.<br>
	 * This updates the {@code creationDay} field with a new value.
	 * @param i The number of days since the character was created.
	 */
	public void setCreationDataDay(long i)
	{
		creationDay = i;
	}
	
	/**
	 * Retrieves the day the player character was created.
	 * @return The creation day as a {@code long}.
	 */
	public long getCreationDataDay()
	{
		return creationDay;
	}
	
	/**
	 * Updates the player's bonus time status based on their account age.<br>
	 * This method checks if the player is new or a returning user.<br>
	 * It sets the {@code PlayerBonusTimeStatus} to {@code NEW}, {@code RETURN}, or {@code NORMAL}.
	 */
	public void setBonusTimeStatus()
	{
		// Timestamp tm = getClientConnection().getAccount().getPlayerAccountData(getObjectId()).getPlayerCommonData().getLastOnline();
		// long lastOnlineTimeDay = (System.currentTimeMillis() - tm.getTime()) / 24 / 60 / 60 / 1000;
		final long t = (System.currentTimeMillis() - getCommonData().getCreationDate().getTime()) / 24 / 60 / 60 / 1000;
		final long bonus_time = getBonusTime().getTime() != null ? System.currentTimeMillis() - getBonusTime().getTime().getTime() : 0;
		final boolean bonus_comeback = System.currentTimeMillis() < bonus_time;
		setCreationDataDay(t);
		if (t <= 3L)
		{
			setNew(true);
		}
		else
		{
			setNew(false);
		}
		
		if (((getBonusTime().getStatus() == PlayerBonusTimeStatus.RETURN) && bonus_comeback) || ((getBonusTime().getStatus() == PlayerBonusTimeStatus.NEW) && (t <= 30)))
		{
			return;
		}
		
		if (t <= 30L)
		{
			getBonusTime().setStatus(PlayerBonusTimeStatus.NEW);
		}
		else
		{
			getBonusTime().setStatus(PlayerBonusTimeStatus.NORMAL);
		}
		
		if (getClientConnection().getAccount().getIsReturn() == 1)
		{
			getBonusTime().setStatus(PlayerBonusTimeStatus.RETURN);
			getBonusTime().setTime(new Timestamp(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)));
		}
	}
	
	/**
	 * Retrieves the membership status of the current account.<br>
	 * Returns {@code 0x00} if the account is null.
	 * @return The membership level as a {@code byte}.
	 */
	public byte getMembership()
	{
		if (playerAccount == null)
		{
			return 0x00;
		}
		
		return playerAccount.getMembership();
	}
	
	/**
	 * Retrieves the free-to-play status of the player.
	 * @return the {@code F2p} status object.
	 */
	public F2p getF2p()
	{
		return f2p;
	}
	
	/**
	 * Sets the {@code F2p} status for the player.<br>
	 * This method updates the internal {@code f2p} field with the provided value.
	 * @param f2p The {@code F2p} object to set.
	 */
	public void setF2p(F2p f2p)
	{
		this.f2p = f2p;
	}
	
	/**
	 * Registers a new observer to monitor hot teleport actions.<br>
	 * This method adds the provided {@code ActionObserver} to the internal list.<br>
	 * If the list is currently {@code null}, it initializes a new {@code ArrayList}.
	 * @param observer The {@link ActionObserver} to be added.
	 */
	public void setHotTeleObservers(ActionObserver observer)
	{
		if (hotTeleObservers == null)
		{
			hotTeleObservers = new ArrayList<>(3);
		}
		
		hotTeleObservers.add(observer);
	}
	
	/**
	 * Retrieves the list of observers for hot teleportation.<br>
	 * These observers handle specific logic when a player uses a hot teleport.
	 * @return a {@code List} of {@link ActionObserver} objects.
	 */
	public List<ActionObserver> getHotTeleObservers()
	{
		return hotTeleObservers;
	}
	
	/**
	 * Retrieves the unique identifier for the current transformation model.<br>
	 * This ID is used to determine which visual appearance the player currently has.
	 * @return The {@code int} value of the transformed model ID.
	 */
	public int getTransformedModelId()
	{
		return transformModelId;
	}
	
	/**
	 * Updates the unique identifier for the current transformation model.<br>
	 * This value is used to determine which visual model a player uses.
	 * @param id The new {@code int} ID for the transformed model.
	 */
	public void setTransformedModelId(int id)
	{
		transformModelId = id;
	}
	
	/**
	 * Retrieves the unique identifier for the current transformation.<br>
	 * This value is used to identify which specific item is being used.
	 * @return the {@code int} ID of the transformed item.
	 */
	public int getTransformedItemId()
	{
		return transformItemId;
	}
	
	/**
	 * Updates the current transformation item ID.<br>
	 * This method sets the {@code transformItemId} field to a new value.
	 * @param id The unique identifier for the transformation item.
	 */
	public void setTransformedItemId(int id)
	{
		transformItemId = id;
	}
	
	/**
	 * Retrieves the unique identifier for the current transformation panel.<br>
	 * This value is used to identify which specific transformation is active.
	 * @return the {@code int} ID of the transformed panel.
	 */
	public int getTransformedPanelId()
	{
		return transformPanelId;
	}
	
	/**
	 * Updates the unique identifier for the transformed panel.<br>
	 * This value is used to identify which transformation is currently active.
	 * @param id The new {@code int} ID for the transformed panel.
	 */
	public void setTransformedPanelId(int id)
	{
		transformPanelId = id;
	}
	
	/**
	 * Retrieves the unique identifier for the current transformation skill.<br>
	 * This value is used to identify which specific transformation a player has active.
	 * @return The {@code int} ID of the transformed skill.
	 */
	public int getTransformedSkillId()
	{
		return transformSkillId;
	}
	
	/**
	 * Updates the unique identifier for the player's current transformation skill.<br>
	 * This value is used to track which specific skill is active.
	 * @param id The new {@code int} ID of the transformation skill.
	 */
	public void setTransformedSkillId(int id)
	{
		transformSkillId = id;
	}
	
	/**
	 * Checks if the player is currently using an invisible transformation.
	 * @return {@code true} if the transform is invisible, {@code false} otherwise.
	 */
	public boolean isInvisibleTransform()
	{
		return invisibleTransform;
	}
	
	/**
	 * Sets whether the player is currently using an invisible transform.<br>
	 * This updates the {@code invisibleTransform} state of the player object.
	 * @param invisibleTransform The boolean value to set for invisibility status.
	 */
	public void setInvisibleTransform(boolean invisibleTransform)
	{
		this.invisibleTransform = invisibleTransform;
	}
	
	/**
	 * Retrieves the list of account transformations.<br>
	 * This method returns the {@code AccountTransformList} associated with the account.
	 * @return the current {@link AccountTransformList}
	 */
	public AccountTransformList getTransformList()
	{
		return transformList;
	}
	
	/**
	 * Retrieves the ID of the transformation most recently used by the player.<br>
	 * This value is stored in the {@code lastUsedTransformation} field.
	 * @return The integer ID of the last used transformation.
	 */
	public int getLastUsedTransformation()
	{
		return lastUsedTransformation;
	}
	
	/**
	 * Updates the ID of the transformation last used by the player.<br>
	 * This value is stored in the {@code lastUsedTransformation} field.
	 * @param lastUsedTransformation The unique identifier for the transformation.
	 */
	public void setLastUsedTransformation(int lastUsedTransformation)
	{
		this.lastUsedTransformation = lastUsedTransformation;
	}
	
	/**
	 * Retrieves the collection of transforms for this player.<br>
	 * The map uses an {@code Integer} key to identify each entry.
	 * @return A {@link Map} containing the {@link TransformCollection} objects.
	 */
	public Map<Integer, TransformCollection> getTransformCollections()
	{
		return transformCollections;
	}
	
	/**
	 * Retrieves the list of created account transformations.<br>
	 * This method returns all {@link AccountTransfo} objects that have been initialized.
	 * @return a {@code List} containing all created {@link AccountTransfo} objects.
	 */
	public List<AccountTransfo> getTransformCreated()
	{
		return transformCreated;
	}
	
	/**
	 * Updates the list of created transformations for the account.<br>
	 * This method sets the {@code transformCreated} field with the provided data.
	 * @param transformCreated The list of {@link AccountTransfo} objects to set.
	 */
	public void setTransformCreated(List<AccountTransfo> transformCreated)
	{
		this.transformCreated = transformCreated;
	}
	
	/**
	 * Retrieves the current list of items in the player's wardrobe.
	 * @return a {@code PlayerWardrobeList} containing all wardrobe items.
	 */
	public PlayerWardrobeList getWardrobe()
	{
		return wardrobe;
	}
	
	/**
	 * Updates the player's current wardrobe.<br>
	 * This method assigns a new {@code PlayerWardrobeList} to the player object.
	 * @param wardrobe The new {@code PlayerWardrobeList} to set.
	 */
	public void setWardrobe(PlayerWardrobeList wardrobe)
	{
		this.wardrobe = wardrobe;
	}
	
	/**
	 * Sets the {@code PlayerLunaShop} object for the player.<br>
	 * This method updates the shop data associated with the current user.
	 * @param pls The {@code PlayerLunaShop} instance to assign.
	 */
	public void setPlayerLunaShop(PlayerLunaShop pls)
	{
		lunaShop = pls;
	}
	
	/**
	 * Retrieves the {@code PlayerLunaShop} object for the current player.<br>
	 * This method provides access to the player's specific shop data.
	 * @return The {@code PlayerLunaShop} instance associated with this player.
	 */
	public PlayerLunaShop getPlayerLunaShop()
	{
		return lunaShop;
	}
	
	/**
	 * Updates the Luna consume point for the player.<br>
	 * This method modifies the value stored in {@code PlayerCommonData}.
	 * @param point The new integer value to set for the Luna consume point.
	 */
	public void setLunaConsumePoint(int point)
	{
		playerCommonData.setLunaConsumePoint(point);
	}
	
	/**
	 * Retrieves the current Luna Consume Point value for the player.<br>
	 * This method fetches data from the {@code PlayerCommonData} object.
	 * @return The integer value of the Luna Consume Points.
	 */
	public int getLunaConsumePoint()
	{
		return playerCommonData.getLunaConsumePoint();
	}
	
	/**
	 * Retrieves the {@code PlayerSweep} object associated with this player.<br>
	 * This is used to manage specific sweep actions for the character.
	 * @return The current {@link PlayerSweep} instance.
	 */
	public PlayerSweep getPlayerShugoSweep()
	{
		return shugoSweep;
	}
	
	/**
	 * Sets the {@code PlayerSweep} object for the player.<br>
	 * This method updates the internal {@code shugoSweep} field.
	 * @param ps The {@code PlayerSweep} data to assign.
	 */
	public void setPlayerShugoSweep(PlayerSweep ps)
	{
		shugoSweep = ps;
	}
	
	/**
	 * Updates the municipality keys for the player.<br>
	 * This method updates the {@code keys} value in the {@code PlayerCommonData}.
	 * @param keys The new integer value for the municipality keys.
	 */
	public void setMuniKeys(int keys)
	{
		playerCommonData.setMuniKeys(keys);
	}
	
	/**
	 * Retrieves the number of municipal keys held by the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The total count of municipal keys as an {@code int}.
	 */
	public int getMuniKeys()
	{
		return playerCommonData.getMuniKeys();
	}
	
	/**
	 * Updates the number of Luna items consumed by the player.<br>
	 * This method updates the value in {@code PlayerCommonData}.
	 * @param count The new consumption count to set.
	 */
	public void setLunaConsumeCount(int count)
	{
		playerCommonData.setLunaConsumeCount(count);
	}
	
	/**
	 * Retrieves the total number of Luna items consumed by the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The current count of consumed Luna items as an {@code int}.
	 */
	public int getLunaConsumeCount()
	{
		return playerCommonData.getLunaConsumeCount();
	}
	
	/**
	 * Updates the Luna value for the current player account.<br>
	 * This method sends a packet to the login server to sync the data.<br>
	 * If the communication is successful, it updates the {@link Account} object.
	 * @param luna The new Luna value to set.
	 */
	public void setLunaAccount(long luna)
	{
		if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(1, getClientConnection().getAccount().getToll(), luna, getAcountName())))
		{
			getClientConnection().getAccount().setLuna(luna);
		}
		else
		{
			PacketSendUtility.sendMessage(this, "ls communication error.");
		}
	}
	
	/**
	 * Retrieves the unique Luna identifier for the current player's account.<br>
	 * This value is fetched from the {@link com.aionemu.gameserver.model.account.Account} object.
	 * @return The {@code long} value of the Luna ID.
	 */
	public long getLunaAccount()
	{
		return getClientConnection().getAccount().getLuna();
	}
	
	/**
	 * Updates the current wardrobe slot for the player.<br>
	 * This method modifies the {@code PlayerCommonData} object.
	 * @param slot The index of the wardrobe slot to set.
	 */
	public void setWardrobeSlot(int slot)
	{
		playerCommonData.setWardrobeSlot(slot);
	}
	
	/**
	 * Retrieves the current wardrobe slot index for the player.<br>
	 * This value is fetched from the {@code PlayerCommonData}.
	 * @return The integer representing the wardrobe slot.
	 */
	public int getWardrobeSlot()
	{
		return playerCommonData.getWardrobeSlot();
	}
	
	/**
	 * Retrieves the current Luna Buff bonus for the player.<br>
	 * This value is used to calculate specific character benefits.
	 * @return the {@code LunaBuffBonus} object.
	 */
	public LunaBuffBonus getLunaBuffBonus()
	{
		return lunaBuffBonus;
	}
	
	/**
	 * Sets the {@code LunaBuffBonus} for the player.<br>
	 * This method updates the internal bonus value.
	 * @param lunaBuffBonus The new {@code LunaBuffBonus} to assign.
	 */
	public void setLunaBuffBonus(LunaBuffBonus lunaBuffBonus)
	{
		this.lunaBuffBonus = lunaBuffBonus;
	}
	
	/**
	 * Updates the current floor level for the player.<br>
	 * This method updates the value within the {@link PlayerCommonData} object.
	 * @param floor The new floor number to set.
	 */
	public void setFloor(int floor)
	{
		getCommonData().setFloor(floor);
	}
	
	/**
	 * Retrieves the current floor level of the player.<br>
	 * This method fetches the data from the {@link PlayerCommonData} object.
	 * @return The integer value representing the current floor.
	 */
	public int getFloor()
	{
		return getCommonData().getFloor();
	}
	
	/**
	 * Retrieves the current value of the {@code LunaDiceGame} variable.<br>
	 * This method returns the game state for the Luna Dice event.
	 * @return The integer value representing the {@code LunaDiceGame} status.
	 */
	public int getLunaDiceGame()
	{
		return LunaDiceGame;
	}
	
	/**
	 * Updates the {@code LunaDiceGame} value for the player.<br>
	 * If {@code reset} is {@code false}, it only updates if the new {@code dice} value is higher than the current one.<br>
	 * If {@code reset} is {@code true}, it overwrites the current value regardless of its size.
	 * @param dice The new dice value to set.
	 * @param reset A boolean flag that determines whether to overwrite the existing value.
	 */
	public void setLunaDiceGame(int dice, boolean reset)
	{
		if (!reset)
		{
			if (dice > LunaDiceGame)
			{
				LunaDiceGame = dice;
			}
			else
			{
				return;
			}
		}
		else
		{
			LunaDiceGame = dice;
		}
	}
	
	/**
	 * Retrieves the current number of attempts for the Luna Dice Game.
	 * @return The integer value of {@code LunaDiceGameTry}.
	 */
	public int getLunaDiceGameTry()
	{
		return LunaDiceGameTry;
	}
	
	/**
	 * Updates the number of attempts for the Luna Dice Game.<br>
	 * This method sets the {@code LunaDiceGameTry} value to the provided integer.
	 * @param dice The number of tries to set for the game.
	 */
	public void setLunaDiceGameTry(int dice)
	{
		LunaDiceGameTry = dice;
	}
	
	/**
	 * Retrieves the current list of equipment settings.<br>
	 * This method returns the {@code EquipmentSettingList} associated with the player.
	 * @return the {@code EquipmentSettingList} object.
	 */
	public EquipmentSettingList getEquipmentSettingList()
	{
		return equipmentSettingList;
	}
	
	/**
	 * Updates the list of equipment settings for the player.<br>
	 * This method assigns a new {@code EquipmentSettingList} to the current object.
	 * @param equipmentSettingList The new list of equipment settings to apply.
	 */
	public void setEquipmentSettingList(EquipmentSettingList equipmentSettingList)
	{
		this.equipmentSettingList = equipmentSettingList;
	}
	
	/**
	 * Retrieves the list of skill skins for the player.
	 * @return a {@code SkillSkinList} containing all active skill skins.
	 */
	public SkillSkinList getSkillSkinList()
	{
		return skillSkinList;
	}
	
	/**
	 * Sets the list of skill skins for this player.<br>
	 * This method also sets this object as the owner of the provided {@code SkillSkinList}.
	 * @param skillSkinList The {@code SkillSkinList} to associate with the player.
	 */
	public void setSkillSkinList(SkillSkinList skillSkinList)
	{
		this.skillSkinList = skillSkinList;
		skillSkinList.setOwner(this);
	}
	
	/**
	 * Retrieves the unique identifier for the player's partner.<br>
	 * This value is used to link two players together in the game world.
	 * @return The {@code int} ID of the partner.
	 */
	public int getPartnerId()
	{
		return partnerId;
	}
	
	/**
	 * Retrieves the list of monsters currently in a cubic area around the player.<br>
	 * This method is used to identify nearby enemies for combat logic.
	 * @return The {@code PlayerMCList} containing monster data.
	 */
	public PlayerMCList getMonsterCubic()
	{
		return mc;
	}
	
	/**
	 * Sets the monster cubic list for a specific player.<br>
	 * This method updates the {@code mc} field with the provided {@code PlayerMCList}.
	 * @param playerMcList The new {@code PlayerMCList} to assign to the player.
	 */
	public void setMonsterCubic(PlayerMCList playerMcList)
	{
		mc = playerMcList;
	}
	
	/**
	 * Player Ranking System
	 */
	private ArenaOfDisciplineRank disciplineRank;
	private ArenaOfCooperationRank cooperationRank;
	
	/**
	 * Retrieves the current rank of the player in a specific discipline.<br>
	 * This value is used to determine permissions or status within the game.
	 * @return The {@code ArenaOfDisciplineRank} of the player.
	 */
	public ArenaOfDisciplineRank getDisciplineRank()
	{
		return disciplineRank;
	}
	
	/**
	 * Sets the discipline rank for the player.<br>
	 * This updates the {@code disciplineRank} field with the provided value.
	 * @param dr The new {@link ArenaOfDisciplineRank} to assign.
	 */
	public void setDisciplineRank(ArenaOfDisciplineRank dr)
	{
		disciplineRank = dr;
	}
	
	/**
	 * Retrieves the current cooperation rank of the player.<br>
	 * This value represents the player's standing in cooperative activities.
	 * @return The {@code ArenaOfCooperationRank} of the player.
	 */
	public ArenaOfCooperationRank getCooperationRank()
	{
		return cooperationRank;
	}
	
	/**
	 * Sets the cooperation rank for the player.<br>
	 * This updates the {@code cooperationRank} field with the provided value.
	 * @param cr The new {@link ArenaOfCooperationRank} to assign.
	 */
	public void setCooperationRank(ArenaOfCooperationRank cr)
	{
		cooperationRank = cr;
	}
	
	/**
	 * Retrieves the list of achievements for the current player.<br>
	 * The results are mapped by their unique identification numbers.
	 * @return A {@code Map<Integer, PlayerAchievement>} containing all player achievements.
	 */
	public Map<Integer, PlayerAchievement> getPlayerAchievements()
	{
		return playerAchievements;
	}
	
	/**
	 * Updates the achievements for a specific player.<br>
	 * This method assigns a map of {@code PlayerAchievement} objects to the current player instance.
	 * @param playerAchievements A {@code Map} where the key is the achievement ID and the value is the {@code PlayerAchievement} object.
	 */
	public void setPlayerAchievements(Map<Integer, PlayerAchievement> playerAchievements)
	{
		this.playerAchievements = playerAchievements;
	}
	
	/**
	 * Retrieves the list of achievements earned by the player during events.<br>
	 * The results are mapped by their unique identifier.
	 * @return A {@code Map<Integer, PlayerAchievement>} containing all event achievements.
	 */
	public Map<Integer, PlayerAchievement> getPlayerEventAchievements()
	{
		return playerEventAchievements;
	}
	
	/**
	 * Updates the achievements for a specific player.<br>
	 * This method replaces the current list of achievements with the provided map.
	 * @param playerAchievements A {@code Map} where the key is the achievement ID and the value is the {@link PlayerAchievement}.
	 */
	public void setPlayerEventAchievements(Map<Integer, PlayerAchievement> playerAchievements)
	{
		playerEventAchievements = playerAchievements;
	}
	
	/**
	 * Retrieves the current fame values for all players.<br>
	 * The data is stored in a {@code Map} where the key is the player ID.
	 * @return A {@code Map} containing player IDs and their corresponding {@link PlayerFame} objects.
	 */
	public Map<Integer, PlayerFame> getPlayerFame()
	{
		return playerFame;
	}
	
	/**
	 * Updates the fame data for all players.<br>
	 * This method sets the {@code playerFame} field with a new map of values.
	 * @param playerFame A {@link Map} containing player IDs and their corresponding {@link PlayerFame} objects.
	 */
	public void setPlayerFame(Map<Integer, PlayerFame> playerFame)
	{
		this.playerFame = playerFame;
	}
	
	/**
	 * Retrieves the total time spent in the game world.<br>
	 * This value is stored as an integer representing seconds.
	 * @return The current {@code worldPlayTime}.
	 */
	public int getWorldPlayTime()
	{
		return worldPlayTime;
	}
	
	/**
	 * Updates the current world play time for the player.<br>
	 * This method sets the {@code worldPlayTime} variable to a new value.
	 * @param playTime The new play time value to set.
	 */
	public void setWorldPlayTime(int playTime)
	{
		worldPlayTime = playTime;
	}
	
	/**
	 * Retrieves the current Lumiel transformations for the player.<br>
	 * The map uses the transformation ID as the key.
	 * @return A {@code Map<Integer, LumielTransform>} containing all active transformations.
	 */
	public Map<Integer, LumielTransform> getPlayerLumiel()
	{
		return playerLumiel;
	}
	
	/**
	 * Updates the {@code playerLumiel} data for the current player.<br>
	 * This method stores a map of IDs to {@link LumielTransform} objects.
	 * @param playerLumiel A {@code Map} containing the transformation data.
	 */
	public void setPlayerLumiel(Map<Integer, LumielTransform> playerLumiel)
	{
		this.playerLumiel = playerLumiel;
	}
	
	/**
	 * Retrieves the collection of players associated with this object.
	 * @return the {@code PlayerCollection} containing the players.
	 */
	public PlayerCollection getPlayerCollection()
	{
		return playerCollection;
	}
	
	/**
	 * Updates the {@code playerCollection} for this object.<br>
	 * This method assigns a new {@link PlayerCollection} to the internal field.
	 * @param playerCollection The {@code PlayerCollection} to set.
	 */
	public void setPlayerCollection(PlayerCollection playerCollection)
	{
		this.playerCollection = playerCollection;
	}
}
