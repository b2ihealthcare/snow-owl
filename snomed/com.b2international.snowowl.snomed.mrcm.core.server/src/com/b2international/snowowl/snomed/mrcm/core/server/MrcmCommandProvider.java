/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.mrcm.core.server;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.date.DateFormats;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.users.IAuthorizationService;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.PermissionIdConstant;
import com.b2international.snowowl.core.users.SpecialUserStore;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.server.console.CommandLineAuthenticator;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;

/**
 * OSGi command provider for MRCM import and export.
 */

public class MrcmCommandProvider implements CommandProvider {

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
		
		final File file = new File(filePath);
		
		if (!file.exists() || !file.isFile()) {
			interpreter.print("MRCM import file cannot be found.");
			return;
		}
		
		if (!file.canRead()) {
			interpreter.print("Cannot read MRCM import file content.");
			return;
		}
		
		final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
		
		if (!authenticator.authenticate(interpreter)) {
			return;
		}
		
		final IAuthorizationService authorizationService = ApplicationContext.getInstance().getService(IAuthorizationService.class);
		final boolean isAuthorized = authorizationService.isAuthorized(authenticator.getUsername(), new Permission(PermissionIdConstant.MRCM_IMPORT));
		if (!isAuthorized) {
			interpreter.print("User is unauthorized to import MRCM rules.");
			return;
		}

		new XMIMrcmImporter().doImport(authenticator.getUsername(), file);
	}

	public synchronized void _export(final CommandInterpreter interpreter) {

		final String destinationFolder = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(destinationFolder)) {
			interpreter.println("Export destination folder should be specified.");
			return;
		}
		
		final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
		final IAuthorizationService authorizationService = ApplicationContext.getInstance().getService(IAuthorizationService.class);
		if (authenticator.authenticate(interpreter) && !authorizationService.isAuthorized(authenticator.getUsername(), new Permission(PermissionIdConstant.MRCM_EXPORT))) {
			interpreter.print("User is unauthorized to export MRCM rules.");
			return;
		}
		
		// final String userId = authenticator.getUsername();
		final String user = SpecialUserStore.SYSTEM_USER_NAME;

		interpreter.println("Exporting MRCM rules...");
		final Path exportedFile = new XMIMrcmExporter().doExport(user, Paths.get(destinationFolder));
		interpreter.println("Exported MRCM rules to " + exportedFile);
	}

	@Override
	public String getHelp() {
		return new StringBuilder("--- MRCM commands ---\n")
		.append("\tmrcm import [importFileAbsolutePath] - Imports the MRCM rules from the given XMI source file.\n")
		.append("\tmrcm export [destinationDirectoryPath] - Exports the MRCM rules XMI file to the destination folder.\n").toString();
	}

}