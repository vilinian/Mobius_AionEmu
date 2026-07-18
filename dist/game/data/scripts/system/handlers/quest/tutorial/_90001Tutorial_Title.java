package system.handlers.quest.tutorial;

import com.aionemu.gameserver.model.DialogAction;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.questEngine.handlers.QuestHandler;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.questEngine.model.QuestState;
import com.aionemu.gameserver.questEngine.model.QuestStatus;

/**
 * This class handles the logic for the title sequence of tutorial quest {@code 90001}.<br>
 * It manages how the game displays and processes the initial introductory text for new players.
 * @author Falke_34
 */
public class _90001Tutorial_Title extends QuestHandler
{
	private final static int questId = 90001;
	
	/**
	 * Initializes a new instance of the {@link _90001Tutorial_Title} class.<br>
	 * This constructor sets up the quest handler for ID {@code 90001}.<br>
	 * It calls the superclass constructor to register the quest ID.
	 */
	public _90001Tutorial_Title()
	{
		super(questId);
	}
	
	/**
	 * Registers the quest with the level up event.<br>
	 * This method links {@code questId} to the level up system.<br>
	 * It ensures the quest triggers when a player gains a level.
	 */
	@Override
	public void register()
	{
		qe.registerOnLevelUp(questId);
	}
	
	/**
	 * This method handles the logic when a player levels up.<br>
	 * It checks if the level up triggers specific quest progress.<br>
	 * It calls {@code defaultOnLvlUpEvent} to process the event.
	 * @param env The current quest environment context.
	 * @return {@code true} if the event was handled successfully, otherwise {@code false}.
	 */
	@Override
	public boolean onLvlUpEvent(QuestEnv env)
	{
		return defaultOnLvlUpEvent(env);
	}
	
	/**
	 * Handles dialog events for the quest.<br>
	 * This method checks the current {@link QuestState} and {@code targetId}.<br>
	 * It determines which dialog to send based on the {@link DialogAction}.
	 * @param env The environment containing player data and current quest context.
	 * @return {@code true} if the event was handled, otherwise {@code false}.
	 */
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
		{
			return false;
		}
		
		final DialogAction action = env.getDialog();
		
		if ((qs.getStatus() == QuestStatus.START) && (qs.getQuestVarById(0) == 0))
		{
			switch (action)
			{
				case QUEST_AUTO_REWARD:
					qs.setQuestVar(0);
					qs.setStatus(QuestStatus.COMPLETE);
					updateQuestStatus(env);
					player.getTitleList().addTitle(379, true, 0);
					return closeDialogWindow(env);
				default:
					break;
			}
		}
		
		return false;
	}
	
}
