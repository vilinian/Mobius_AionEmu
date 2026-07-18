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
package com.aionemu.commons.scripting.impl.javacompiler;

import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a {@code DiagnosticListener} for the Java compiler.<br>
 * It captures compilation errors and forwards them to the logger.<br>
 * It helps developers identify issues during the script compilation process.
 * @author SoulKeeper
 */
public class ErrorListener implements DiagnosticListener<JavaFileObject>
{
	private static final Logger log = LoggerFactory.getLogger(ErrorListener.class);
	
	/**
	 * Logs a compilation diagnostic message to the logger.<br>
	 * This method extracts details like the error kind, message, and source location.<br>
	 * It formats these details into a readable string for debugging purposes.
	 * @param diagnostic The {@code Diagnostic} object containing compiler information.
	 */
	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic)
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("Java Compiler ");
		sb.append(diagnostic.getKind());
		sb.append(": ");
		sb.append(diagnostic.getMessage(Locale.ENGLISH));
		if (diagnostic.getSource() != null)
		{
			sb.append("\n");
			sb.append("Source: ");
			sb.append(diagnostic.getSource().getName());
			sb.append("\n");
			sb.append("Line: ");
			sb.append(diagnostic.getLineNumber());
			sb.append("\n");
			sb.append("Column: ");
			sb.append(diagnostic.getColumnNumber());
		}
		
		log.error(sb.toString());
	}
}
