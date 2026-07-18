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
package com.aionemu.commons.utils.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * This class allows us to read ResourceBundles with custom encodings, so we don't have write \\uxxxx symbols and use utilities like native2ascii to convert files.
 * <p/>
 * <br>
 * Usage: For instance we want to load resource bundle "test" from current deirectory and use english locale. If locale not found, we will use default file (and ignore default locale).
 * 
 * <pre>
 * URLClassLoader loader = new URLClassLoader(new URL[]
 * {
 * 	new File(&quot;.&quot;).toURI().toURL()
 * });
 * 
 * ResourceBundle rb = ResourceBundle.getBundle(&quot;test&quot;, Locale.ENGLISH, loader, new ResourceBundleControl(&quot;UTF-8&quot;));
 * 
 * // English locale not found, use default
 * if (!rb.getLocale().equals(Locale.ENGLISH))
 * {
 * 	rb = ResourceBundle.getBundle(&quot;test&quot;, Locale.ROOT, loader, new ResourceBundleControl(&quot;UTF-8&quot;));
 * }
 * 
 * System.out.println(rb.getString(&quot;test&quot;));
 * </pre>
 * 
 * @author SoulKeeper
 */
public class ResourceBundleControl extends ResourceBundle.Control
{
	/**
	 * Encoding which will be used to read resource bundle, by defaults it's 8859_1
	 */
	private String encoding = "UTF-8";
	
	/**
	 * Creates a new instance of {@link ResourceBundleControl}.<br>
	 * This constructor initializes the control with default settings.<br>
	 * The default encoding used is {@code UTF-8}.
	 */
	public ResourceBundleControl()
	{
	}
	
	/**
	 * Creates a new {@link ResourceBundleControl} with a specific character encoding.<br>
	 * This allows the application to read resource bundles using custom formats.
	 * @param encoding The character set name to use for reading files.
	 */
	public ResourceBundleControl(String encoding)
	{
		this.encoding = encoding;
	}
	
	/**
	 * Creates a new {@link ResourceBundle} using the specified configuration.<br>
	 * This method handles both {@code java.class} and {@code java.properties} formats.<br>
	 * It uses the custom encoding defined in this control class.
	 * @param baseName The base name of the resource bundle.
	 * @param locale The locale to use for the bundle.
	 * @param format The format of the bundle, such as {@code java.properties}.
	 * @param loader The {@code ClassLoader} to use to load the resources.
	 * @param reload Whether to bypass the cache and reload the resource from the source.
	 * @return A new {@link ResourceBundle} instance.
	 * @throws InstantiationException If the bundle class cannot be instantiated.
	 * @throws IOException If an error occurs while reading the resource file.
	 */
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws InstantiationException, IOException
	{
		final String bundleName = toBundleName(baseName, locale);
		ResourceBundle bundle = null;
		if (format.equals("java.class"))
		{
			try
			{
				@SuppressWarnings(
				{
					"unchecked"
				})
				final Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader.loadClass(bundleName);
				
				// If the class isn't a ResourceBundle subclass, throw a ClassCastException.
				if (ResourceBundle.class.isAssignableFrom(bundleClass))
				{
					try
					{
						bundle = bundleClass.getDeclaredConstructor().newInstance();
					}
					catch (ReflectiveOperationException e)
					{
						throw new InstantiationException(e.toString());
					}
				}
				else
				{
					throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
				}
			}
			catch (ClassNotFoundException ignored)
			{
			}
		}
		else if (format.equals("java.properties"))
		{
			final String resourceName = toResourceName(bundleName, "properties");
			InputStreamReader isr = null;
			InputStream stream = null;
			if (reload)
			{
				final URL url = loader.getResource(resourceName);
				if (url != null)
				{
					final URLConnection connection = url.openConnection();
					if (connection != null)
					{
						// Disable caches to get fresh data for reloading.
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			}
			else
			{
				stream = loader.getResourceAsStream(resourceName);
			}
			
			if (stream != null)
			{
				isr = new InputStreamReader(stream, encoding);
			}
			
			if (isr != null)
			{
				try
				{
					bundle = new PropertyResourceBundle(isr);
				}
				finally
				{
					isr.close();
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("unknown format: " + format);
		}
		
		return bundle;
	}
	
	/**
	 * Retrieves the character encoding used for reading resource bundles.<br>
	 * This value is used by {@code Locale, String, ClassLoader, boolean)}.
	 * @return The current encoding as a {@code String}.
	 */
	public String getEncoding()
	{
		return encoding;
	}
	
	/**
	 * Sets the character encoding used to read resource bundles.<br>
	 * This replaces the default value of {@code UTF-8}.
	 * @param encoding The name of the encoding to use, such as {@code UTF-8}.
	 */
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
}
