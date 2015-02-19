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

	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmCommandProvider.class);
	
	public void _mrcm(final CommandInterpreter interpreter) {
		
		try {
			
			final String nextArgument = interpreter.nextArgument();
			
			if ("import".equals(nextArgument)) {
				_import(interpreter);
				return;
			} else if ("export".equals(nextArgument)) {
				_export(interpreter);
				return;
			} else if ("configure_mrcm_file_for_sct_full_import_process".equals(nextArgument)) {
				_configure(interpreter);
				return;
			} else if ("show_configured_mrcm_file_for_sct_full_import_process".equals(nextArgument)) {
				_showConfiguredFile(interpreter);
				return;
			} else {
				interpreter.println(getHelp());
			}
			
		} catch (final Throwable t) {
			interpreter.println(getHelp());
		}
		
	}
	
	private synchronized void _showConfiguredFile(final CommandInterpreter interpreter) {
		java.net.URI mrcmFileUri = MrcmFileRegistryImpl.INSTANCE.getMrcmFileUri();
		if (null == mrcmFileUri) {
			interpreter.println("No MRCM files are configured.");
		} else {
			interpreter.println(mrcmFileUri);
		}
	}

	private synchronized void _configure(final CommandInterpreter interpreter) {
		
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
		
		MrcmFileRegistryImpl.INSTANCE.configureMrcmFile(file.toURI());
		
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

		MrcmImporter.INSTANCE.doImport(authenticator.getUsername(), file, true, null);
	}

	public synchronized void _export(final CommandInterpreter interpreter) {

		final String destinationFolderPath = interpreter.nextArgument();
		
		if (StringUtils.isEmpty(destinationFolderPath)) {
			interpreter.println("Export destination folder should be specified.");
			return;
		}
		
		final File folder = new File(destinationFolderPath);
		
		if (!folder.exists() || !folder.isDirectory()) {
			interpreter.print("Export destination folder cannot be found.");
			return;
		}
		
		if (!folder.canRead()) {
			interpreter.print("Cannot read destination folder.");
			return;
		}

		final CommandLineAuthenticator authenticator = new CommandLineAuthenticator();
		final IAuthorizationService authorizationService = ApplicationContext.getInstance().getService(IAuthorizationService.class);
		if (authenticator.authenticate(interpreter) && !authorizationService.isAuthorized(authenticator.getUsername(), new Permission(PermissionIdConstant.MRCM_EXPORT))) {
			interpreter.print("User is unauthorized to export MRCM rules.");
			return;
		}

		// final String userId = authenticator.getUsername();
		final String userId = SpecialUserStore.SYSTEM_USER_NAME;

		interpreter.println("Exporting MRCM rules...");
		LogUtils.logExportActivity(LOGGER, userId, BranchPathUtils.createMainPath(), "Exporting MRCM rules...");
		
		MrcmEditingContext context = null;
		
		try {
		
			context = new MrcmEditingContext();
			
			final File exportXmi = new File(folder, "mrcm_" + Dates.formatByHostTimeZone(new Date(), DateFormats.FULL) + ".xmi");
			final URI uri = URI.createFileURI(exportXmi.getAbsolutePath());
			
			final ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			final Resource resource = resourceSet.createResource(uri);
			ConceptModel model = null; 
			model = context.getConceptModel();
			resource.getContents().add(model);
			resource.save(null);
			
			interpreter.print("MRCM rule export successfully finished.");
			LogUtils.logExportActivity(LOGGER, userId, BranchPathUtils.createMainPath(), "MRCM rule export successfully finished.");
			
		} catch (final Throwable t) {
			
			interpreter.println("Failed to export MRCM rules.");
			LogUtils.logExportActivity(LOGGER, userId, BranchPathUtils.createMainPath(), "Failed to export MRCM rules.");
			interpreter.println(t.getStackTrace());
			
		} finally {
			
			if (null != context) {
				context.close();
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		return new StringBuilder("--- MRCM commands ---\n")
		.append("\tmrcm import [importFileAbsolutePath] - Imports the MRCM rules from the given XMI source file.\n")
		.append("\tmrcm export [destinationDirectoryPath] - Exports the MRCM rules XMI file to the destination folder.\n").toString();
	}

}