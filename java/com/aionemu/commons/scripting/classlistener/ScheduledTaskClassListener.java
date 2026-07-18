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
package com.aionemu.commons.scripting.classlistener;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.quartz.JobDetail;

import com.aionemu.commons.scripting.metadata.Scheduled;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.ClassUtils;

/**
 * This class listens for classes annotated with {@link Scheduled}.<br>
 * It registers these classes as tasks within the {@link CronService} system.<br>
 * It helps automate the discovery and execution of scheduled background jobs.
 */
public class ScheduledTaskClassListener implements ClassListener
{
	/**
	 * Processes the loaded classes after they are initialized.<br>
	 * This method checks each class in the {@code classes} array.<br>
	 * If a class is valid, it calls {@code scheduleClass} to register it.
	 * @param classes An array of {@code Class} objects to be processed.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void postLoad(Class<?>[] classes)
	{
		for (Class<?> clazz : classes)
		{
			if (isValidClass(clazz))
			{
				scheduleClass((Class<? extends Runnable>) clazz);
			}
		}
	}
	
	/**
	 * Prepares the system to unload specific classes.<br>
	 * This method identifies valid classes and removes them from the schedule.<br>
	 * It ensures that no tasks remain active for the provided types.
	 * @param classes The array of {@code Class<?>} objects to be unloaded.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void preUnload(Class<?>[] classes)
	{
		for (Class<?> clazz : classes)
		{
			if (isValidClass(clazz))
			{
				unScheduleClass((Class<? extends Runnable>) clazz);
			}
		}
	}
	
	/**
	 * Checks if a class is valid for scheduling.<br>
	 * It verifies that the class implements {@link Runnable}.<br>
	 * The class must be public and not abstract or an interface.<br>
	 * It also checks for the presence of the {@link Scheduled} annotation.
	 * @param clazz The class to validate.
	 * @return {@code true} if the class meets all requirements, otherwise {@code false}.
	 */
	public boolean isValidClass(Class<?> clazz)
	{
		if (!ClassUtils.isSubclass(clazz, Runnable.class))
		{
			return false;
		}
		
		final int modifiers = clazz.getModifiers();
		
		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers) || !Modifier.isPublic(modifiers) || !clazz.isAnnotationPresent(Scheduled.class))
		{
			return false;
		}
		
		final Scheduled scheduled = clazz.getAnnotation(Scheduled.class);
		if (scheduled.disabled() || (scheduled.value().length == 0))
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Schedules a class for execution based on its {@link Scheduled} annotation.<br>
	 * This method handles both single-instance and per-cron-expression tasks.<br>
	 * It uses the {@link CronService} to register the task with the system.
	 * @param clazz The class of the {@code Runnable} to be scheduled.
	 */
	protected void scheduleClass(Class<? extends Runnable> clazz)
	{
		final Scheduled metadata = clazz.getAnnotation(Scheduled.class);
		
		try
		{
			if (metadata.instancePerCronExpression())
			{
				for (String s : metadata.value())
				{
					getCronService().schedule(clazz.getDeclaredConstructor().newInstance(), s, metadata.longRunningTask());
				}
			}
			else
			{
				final Runnable r = clazz.getDeclaredConstructor().newInstance();
				for (String s : metadata.value())
				{
					getCronService().schedule(r, s, metadata.longRunningTask());
				}
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to schedule runnable " + clazz.getName(), e);
		}
	}
	
	/**
	 * Removes a class from the scheduled tasks.<br>
	 * This method finds all {@code JobDetail} instances associated with the given {@code clazz}.<br>
	 * It then cancels those jobs using the {@link CronService}.
	 * @param clazz The class of the {@code Runnable} to be unscheduled.
	 */
	protected void unScheduleClass(Class<? extends Runnable> clazz)
	{
		final Map<Runnable, JobDetail> map = getCronService().getRunnables();
		for (Map.Entry<Runnable, JobDetail> entry : map.entrySet())
		{
			if (entry.getKey().getClass() == clazz)
			{
				getCronService().cancel(entry.getValue());
			}
		}
	}
	
	/**
	 * Retrieves the singleton instance of the {@link CronService}.<br>
	 * This method ensures that the service has been properly initialized.
	 * @return The active {@code CronService} instance.
	 */
	protected CronService getCronService()
	{
		if (CronService.getInstance() == null)
		{
			throw new RuntimeException("CronService is not initialized");
		}
		
		return CronService.getInstance();
	}
}
