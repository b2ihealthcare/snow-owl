/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.core.mrcm.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.b2international.commons.StringUtils;
import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.domain.PermissionIdConstant;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.request.UserRequests;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.google.common.collect.Sets;

/**
 * OSGi command provider for MRCM import and export.
 */
public class MrcmCommandProvider implements CommandProvider {

	private String exportFormatsString = StringUtils.toString(Sets.newHashSet(MrcmExportFormat.values()));
	
	public void _mrcm(final CommandInterpreter interpreter) {
		
		try {
			final String nextArgument = interpreter.nextArgument();
			
			if ("import".equals(nextArgument)) {
				_import(interpreter);
				return;
			} else if ("export".equals(nextArgument)) {
				_export(interpreter);
				return;
			} else {
				interpreter.println(getHelp());
			}
			
		} catch (final Throwable t) {
			interpreter.println(getHelp());
		}
	}
	
	public synchronized void _import(final CommandInterpreter interpreter) {
		
		final String filePath = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(filePath)) {
			interpreter.println("MRCM import file path should be specified.");
			return;
		}
		
		final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
		
		if (!authenticator.authenticate(interpreter)) {
			return;
		}
		
		final User user = getUser(authenticator.getUsername());
		if (!user.hasPermission(PermissionIdConstant.IMPORT)) {
			interpreter.print("User is unauthorized to import MRCM rules.");
			return;
		}

		final Path file = Paths.get(filePath);
		try (final InputStream content = Files.newInputStream(file, StandardOpenOption.READ)) {
			new XMIMrcmImporter().doImport(authenticator.getUsername(), content);
		} catch (IOException e) {
			interpreter.printStackTrace(e);
		}
		
	}

	private User getUser(final String username) {
		return UserRequests.prepareGet(username).buildAsync().execute(ApplicationContext.getServiceForClass(IEventBus.class)).getSync(1, TimeUnit.MINUTES);
	}

	public synchronized void _export(final CommandInterpreter interpreter) {

		final String format = interpreter.nextArgument();
		if (StringUtils.isEmpty(format)) {
			interpreter.println("Format needs to be specified.");
			return;
		}
		
		MrcmExportFormat selectedFormat = null;
		try {
			selectedFormat = MrcmExportFormat.valueOf(format);
		} catch(IllegalArgumentException iae) {
			interpreter.println("Incorrect format: " + format + " Supported formats: " + exportFormatsString + ".");
			return;
		}
		
		final String destinationFolder = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(destinationFolder)) {
			interpreter.println("Export destination folder needs to be specified.");
			return;
		}
		
		final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
		
		if (!authenticator.authenticate(interpreter)) {
			return;
		}
		
		final User user = getUser(authenticator.getUsername());
		if (!user.hasPermission(PermissionIdConstant.EXPORT)) {
			interpreter.print("User is not authorized to export MRCM rules.");
			return;
		}
		
		final String username = User.SYSTEM.getUsername();

		interpreter.println("Exporting MRCM rules (" + selectedFormat.name() + ")...");
		
		final Path outputFolder = Paths.get(destinationFolder);
		checkOutputFolder(outputFolder);
		final Path exportPath = outputFolder.resolve("mrcm_" + Dates.now() + "." + selectedFormat.name().toLowerCase());
		
		try (final OutputStream stream = Files.newOutputStream(exportPath, StandardOpenOption.CREATE)) {
			if (selectedFormat == MrcmExportFormat.XMI) {
				new XMIMrcmExporter().doExport(username, stream);
			} else if (selectedFormat == MrcmExportFormat.CSV) {
				new CsvMrcmExporter().doExport(username, stream);
			}
			interpreter.println("Exported MRCM rules to " + exportPath + " in " 
			+ selectedFormat.name() + " format.");
		} catch (IOException e) {
			interpreter.printStackTrace(e);
		}
	}
	
	private void checkOutputFolder(Path outputFolder) {
		final File folder = outputFolder.toFile();
		if (!folder.exists() || !folder.isDirectory()) {
			throw new BadRequestException("Export destination folder cannot be found.");
		}
		if (!folder.canRead()) {
			throw new BadRequestException("Cannot read destination folder.");
		}		
	}

	@Override
	public String getHelp() {
		return new StringBuilder("--- MRCM commands ---\n")
		.append("\tmrcm import [importFileAbsolutePath] - Imports the MRCM rules from the given XMI source file.\n")
		.append("\tmrcm export ")
		.append(exportFormatsString)
		.append(" [destinationDirectoryPath] - Exports the MRCM rules in the given format to the destination folder.\n").toString();
	}

}