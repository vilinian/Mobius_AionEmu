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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class manages the internationalization (i18n) settings for the game server.<br>
 * It provides a way to handle different supported languages and their associated text resources.
 * @author blakawk
 */
public class Language
{
	private final List<String> supportedLanguages = new ArrayList<>();
	private final Map<CustomMessageId, String> translatedMessages = new HashMap<>();
	
	/**
	 * Creates a new instance of the {@link Language} class.<br>
	 * This constructor initializes an empty language manager.
	 */
	public Language()
	{
	}
	
	/**
	 * Creates a new {@code Language} instance with a specific language.<br>
	 * This constructor adds the provided string to the list of supported languages.
	 * @param language The name of the language to add.
	 */
	protected Language(String language)
	{
		supportedLanguages.add(language);
	}
	
	/**
	 * Adds a new language to the list of supported languages.<br>
	 * This method updates the internal collection used by {@code getSupportedLanguages}.
	 * @param language The name of the language to add.
	 */
	protected void addSupportedLanguage(String language)
	{
		supportedLanguages.add(language);
	}
	
	/**
	 * Retrieves the list of languages that are currently supported.<br>
	 * This method returns all language codes registered in this {@link Language} instance.
	 * @return a {@code List<String>} containing all supported language names.
	 */
	public List<String> getSupportedLanguages()
	{
		return supportedLanguages;
	}
	
	/**
	 * Translates a message based on its unique identifier.<br>
	 * It uses the provided parameters to fill in any placeholders.<br>
	 * If no specific translation exists, it uses the fallback message from {@code CustomMessageId}.
	 * @param id The unique identifier for the message to translate.
	 * @param params The values to replace placeholders within the message string.
	 * @return The formatted translation as a {@code String}.
	 */
	public String translate(CustomMessageId id, Object... params)
	{
		if (translatedMessages.containsKey(id))
		{
			return String.format(translatedMessages.get(id), params);
		}
		
		return String.format(id.getFallbackMessage(), params);
	}
	
	/*
	 * public String translateRU(CustomMessageIdRU id, Object... params) { if (translatedMessages.containsKey(id)) { return String.format(translatedMessages.get(id), params); } return String.format(id.getFallbackMessage(), params); }
	 */
	/**
	 * Adds a new translated string to the internal map.<br>
	 * This method links a {@code CustomMessageId} with its corresponding text.<br>
	 * It is used to populate the translations for the {@link Language} class.
	 * @param id The unique identifier for the message.
	 * @param message The translated text string to store.
	 */
	protected void addTranslatedMessage(CustomMessageId id, String message)
	{
		translatedMessages.put(id, message);
	}
}
