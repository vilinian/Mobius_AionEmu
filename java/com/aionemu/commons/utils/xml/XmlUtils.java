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
package com.aionemu.commons.utils.xml;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Provides utility methods for handling {@code XML} parsing and transformation.<br>
 * This class uses aggressive synchronization to ensure thread safety with {@code JAXP}.<br>
 * It simplifies common tasks like converting strings to {@link Document} objects.
 */
public abstract class XmlUtils
{
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private static final TransformerFactory tf = TransformerFactory.newInstance();
	static
	{
		dbf.setNamespaceAware(true);
	}
	
	/**
	 * Converts an XML string into a {@code Document} object.<br>
	 * This method handles the parsing logic internally.<br>
	 * It returns {@code null} if the input is {@code null}.
	 * @param xmlSource The raw XML string to be parsed.
	 * @return A {@code Document} object representing the XML content.
	 */
	public static Document getDocument(String xmlSource)
	{
		synchronized (XmlUtils.class)
		{
			Document document = null;
			if (xmlSource != null)
			{
				try
				{
					final Reader stream = new StringReader(xmlSource);
					final DocumentBuilder db = dbf.newDocumentBuilder();
					document = db.parse(new InputSource(stream));
				}
				catch (Exception e)
				{
					throw new RuntimeException("Error converting string to document", e);
				}
			}
			
			return document;
		}
	}
	
	/**
	 * Converts a {@code Document} object into its string representation.<br>
	 * This method uses a {@link Transformer} to perform the conversion.<br>
	 * It handles synchronization internally to ensure thread safety.
	 * @param document The {@code Document} to convert.
	 * @return The resulting string content of the {@code Document}.
	 */
	public static String getString(Document document)
	{
		synchronized (XmlUtils.class)
		{
			try
			{
				final DOMSource domSource = new DOMSource(document);
				final StringWriter writer = new StringWriter();
				final StreamResult result = new StreamResult(writer);
				final Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
				return writer.toString();
			}
			catch (TransformerException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * Creates a {@link Schema} object from a provided XML string.<br>
	 * This method uses the W3C XML Schema namespace to parse the input.<br>
	 * It returns {@code null} if the input string is {@code null}.
	 * @param schemaString The XML string representing the schema.
	 * @return The generated {@link Schema} object or {@code null}.
	 */
	public static Schema getSchema(String schemaString)
	{
		Schema schema = null;
		try
		{
			if (schemaString != null)
			{
				final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				final StreamSource ss = new StreamSource();
				ss.setReader(new StringReader(schemaString));
				schema = sf.newSchema(ss);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to create schema from string: " + schemaString, e);
		}
		
		return schema;
	}
	
	/**
	 * Creates a {@link Schema} object from a provided web address.<br>
	 * This method uses the standard W3C XML Schema namespace.<br>
	 * It returns {@code null} if the input URL is {@code null}.
	 * @param schemaURL The {@link URL} pointing to the schema file.
	 * @return The created {@link Schema} object or {@code null}.
	 */
	public static Schema getSchema(URL schemaURL)
	{
		Schema schema = null;
		try
		{
			if (schemaURL != null)
			{
				final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				schema = sf.newSchema(schemaURL);
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to create schema from URL " + schemaURL, e);
		}
		
		return schema;
	}
	
	/**
	 * Checks if a {@code Document} follows the rules of a specific {@link Schema}.<br>
	 * This method will throw a {@code RuntimeException} if the validation fails.
	 * @param schema The {@link Schema} used to define the rules.
	 * @param document The {@code Document} that needs to be checked.
	 */
	public static void validate(Schema schema, Document document)
	{
		final Validator validator = schema.newValidator();
		try
		{
			validator.validate(new DOMSource(document));
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to validate document", e);
		}
	}
}
