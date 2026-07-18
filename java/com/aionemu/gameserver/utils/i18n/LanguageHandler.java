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
package com.aionemu.gameserver.utils.i18n;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.scripting.classlistener.AggregatedClassListener;
import com.aionemu.commons.scripting.classlistener.OnClassLoadUnloadListener;
import com.aionemu.commons.scripting.classlistener.ScheduledTaskClassListener;
import com.aionemu.commons.scripting.scriptmanager.ScriptManager;
import com.aionemu.gameserver.GameServerError;
import com.aionemu.gameserver.configs.main.GSConfig;

/**
 * This class manages the internationalization (i18n) system for the game server.<br>
 * It handles loading and retrieving localized text strings based on the selected language. It provides a centralized way to manage multi-language support across the application.
 * @author Fennek
 */
public class LanguageHandler
{
	private static final File LANGUAGE_DESCRIPTOR_FILE = new File("./data/scripts/system/languages.xml");
	private static Logger log = LoggerFactory.getLogger(Language.class);
	private final Map<String, Language> languages = new HashMap<>();
	private Language language;
	private static final LanguageHandler instance = new LanguageHandler();
	private final ScriptManager sm = new ScriptManager();
	
	/**
	 * Provides the global singleton instance of {@link LanguageHandler}.<br>
	 * This method initializes the language system and loads necessary files.<br>
	 * Use this to access translation features throughout the application.
	 * @return The active {@code LanguageHandler} instance.
	 */
	public static LanguageHandler getInstance()
	{
		final AggregatedClassListener acl = new AggregatedClassListener();
		acl.addClassListener(new OnClassLoadUnloadListener());
		acl.addClassListener(new ScheduledTaskClassListener());
		acl.addClassListener(new LanguagesLoader(instance));
		instance.sm.setGlobalClassListener(acl);
		
		try
		{
			instance.sm.load(LANGUAGE_DESCRIPTOR_FILE);
		}
		catch (Exception e)
		{
			throw new GameServerError("Cannot load languages", e);
		}
		
		instance.language = instance.getLanguage(GSConfig.LANG);
		return instance;
	}
	
	/**
	 * Private constructor for the {@link LanguageHandler} class.<br>
	 * This prevents other classes from creating new instances.<br>
	 * Use {@code getInstance} to access the singleton instance.
	 */
	private LanguageHandler()
	{
	}
	
	/**
	 * Translates a message based on a unique identifier.<br>
	 * This method uses the current language settings to format the text.<br>
	 * It allows for dynamic values using variable parameters.
	 * @param id The {@code CustomMessageId} used to look up the translation.
	 * @param params A variable number of objects to fill in placeholders within the message.
	 * @return The translated string as a {@code String}.
	 */
	public static String translate(CustomMessageId id, Object... params)
	{
		return instance.language.translate(id, params);
	}
	
	/**
	 * Adds a new {@link Language} to the system.<br>
	 * This method registers all supported languages from the provided object.<br>
	 * If a language already exists, it will be overwritten by this new entry.
	 * @param language The {@code Language} object to register.
	 */
	public void registerLanguage(Language language)
	{
		if (language == null)
		{
			throw new NullPointerException("Cannot register null Language");
		}
		
		final List<String> langs = language.getSupportedLanguages();
		
		for (String lang : langs)
		{
			if (languages.containsKey(lang))
			{
				log.warn("Overriding language " + lang + " with class " + language.getClass().getName());
			}
			
			languages.put(lang, language);
		}
	}
	
	/**
	 * Retrieves a {@link Language} object based on the provided name.<br>
	 * If the language is not found, it returns a new instance of {@code Language}.
	 * @param language The name of the language to look up.
	 * @return The requested {@code Language} object or a new one if missing.
	 */
	public Language getLanguage(String language)
	{
		if (!languages.containsKey(language))
		{
			return new Language();
		}
		
		return languages.get(language);
	}
	
	/**
	 * Removes all registered languages from the internal map.<br>
	 * The {@code size} will return 0 after this call is finished.
	 */
	public void clear()
	{
		languages.clear();
	}
	
	/**
	 * Returns the total number of registered languages.<br>
	 * This method retrieves the count from the internal {@code languages} map.
	 * @return The number of languages currently loaded in the system.
	 */
	public int size()
	{
		return languages.size();
	}
}
