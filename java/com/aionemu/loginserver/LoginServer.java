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
package com.aionemu.loginserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.AEInfos;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.loginserver.configs.Config;
import com.aionemu.loginserver.controller.BannedIpController;
import com.aionemu.loginserver.controller.PremiumController;
import com.aionemu.loginserver.dao.BannedMacDAO;
import com.aionemu.loginserver.network.NetConnector;
import com.aionemu.loginserver.network.ncrypt.KeyGen;
import com.aionemu.loginserver.service.PlayerTransferService;
import com.aionemu.loginserver.taskmanager.TaskFromDBManager;
import com.aionemu.loginserver.utils.DeadLockDetector;
import com.aionemu.loginserver.utils.ThreadPoolManager;
import com.aionemu.loginserver.utils.Util;
import com.aionemu.loginserver.utils.cron.ThreadPoolManagerRunnableRunner;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * This class serves as the main entry point for the {@link com.aionemu.loginserver.LoginServer} application.<br>
 * It initializes the server components and manages the core execution flow of the login service.
 * @author -Nemesiss-
 */
public class LoginServer
{
	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(LoginServer.class);
	
	/**
	 * Prepares the logging system for the application.<br>
	 * This method creates a backup of existing log files before starting.<br>
	 * It then configures {@code slf4j-logback.xml} to set up the logger context.
	 */
	private static void initalizeLoggger()
	{
		new File("./log/backup/").mkdirs();
		final File[] files = new File("log").listFiles((FilenameFilter) (dir, name) -> name.endsWith(".log"));
		
		if ((files != null) && (files.length > 0))
		{
			final byte[] buf = new byte[1024];
			try
			{
				final String outFilename = "./log/backup/" + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".zip";
				final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
				out.setMethod(ZipOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_COMPRESSION);
				
				for (File logFile : files)
				{
					final FileInputStream in = new FileInputStream(logFile);
					out.putNextEntry(new ZipEntry(logFile.getName()));
					int len;
					while ((len = in.read(buf)) > 0)
					{
						out.write(buf, 0, len);
					}
					
					out.closeEntry();
					in.close();
					logFile.delete();
				}
				
				out.close();
			}
			catch (IOException e)
			{
			}
		}
		
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try
		{
			final JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("config/slf4j-logback.xml");
		}
		catch (JoranException je)
		{
			throw new RuntimeException("Failed to configure loggers, shutting down...", je);
		}
	}
	
	/**
	 * This is the main entry point for the game server application.<br>
	 * It initializes all necessary services, configurations, and engines.<br>
	 * The method handles the startup sequence for the entire game world.
	 * @param args Command line arguments passed to the application.
	 */
	public static void main(String[] args)
	{
		final long start = System.currentTimeMillis();
		
		initalizeLoggger();
		CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);
		
		// write a timestamp that can be used by TruncateToZipFileAppender
		log.info("\f" + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis())) + "\f");
		Config.load();
		Util.printCredits();
		DatabaseFactory.init();
		DAOManager.init();
		
		/**
		 * Start deadlock detector that will restart server if deadlock happened
		 */
		new DeadLockDetector(60, DeadLockDetector.RESTART).start();
		ThreadPoolManager.getInstance();
		
		/**
		 * Initialize Key Generator
		 */
		try
		{
			KeyGen.init();
		}
		catch (Exception e)
		{
			log.error("Failed initializing Key Generator. Reason: " + e.getMessage(), e);
			System.exit(ExitCode.CODE_ERROR);
		}
		
		GameServerTable.load();
		BannedIpController.start();
		DAOManager.getDAO(BannedMacDAO.class).cleanExpiredBans();
		
		NetConnector.getInstance().connect();
		PlayerTransferService.getInstance();
		TaskFromDBManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		
		AEInfos.printAllInfos();
		
		PremiumController.getController();
		log.info("AL Login Server started in " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
	}
}
