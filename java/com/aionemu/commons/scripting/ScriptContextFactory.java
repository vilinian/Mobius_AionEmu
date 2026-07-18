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
package com.aionemu.commons.scripting;

import java.io.File;

import com.aionemu.commons.scripting.impl.ScriptContextImpl;

/**
 * This class serves as a factory for providing {@link com.aionemu.commons.scripting.ScriptContext} instances.<br>
 * It allows the system to switch between different implementations of the script context easily.<br>
 * Using this factory ensures that the scripting engine remains decoupled from specific implementation details.
 * @author SoulKeeper
 */
public final class ScriptContextFactory
{
	/**
	 * Creates a new {@link ScriptContext} instance.<br>
	 * It initializes the context using the provided {@code root} file.<br>
	 * If a {@code parent} is provided, it links the new context to that parent.
	 * @param root The {@code File} used as the base for the script.
	 * @param parent The existing {@link ScriptContext} to act as a parent, or {@code null}.
	 * @return A new instance of {@link ScriptContext}.
	 */
	public static ScriptContext getScriptContext(File root, ScriptContext parent)
	{
		ScriptContextImpl ctx;
		if (parent == null)
		{
			ctx = new ScriptContextImpl(root);
		}
		else
		{
			ctx = new ScriptContextImpl(root, parent);
			parent.addChildScriptContext(ctx);
		}
		
		return ctx;
	}
}
