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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.date.Dates;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;

/**
 * 4.4
 */
public class XMIMrcmExporter implements MrcmExporter {

	private static final Logger LOG = LoggerFactory.getLogger(MrcmExporter.class);
	
	@Override
	public Path doExport(String user, Path outputFolder) {
		checkOutputFolder(outputFolder);
		
		final IBranchPath branch = BranchPathUtils.createMainPath();
		LogUtils.logExportActivity(LOG, user, branch, "Exporting MRCM rules...");
		
		try (MrcmEditingContext context = new MrcmEditingContext(branch)) {
			final Path exportPath = outputFolder.resolve("mrcm_" + Dates.now() + ".xmi");

			final ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			final URI fileURI = URI.createFileURI(exportPath.toAbsolutePath().toString());
			final Resource resource = resourceSet.createResource(fileURI);
			resource.getContents().add(context.getConceptModel());
			resource.save(null);
			
			LogUtils.logExportActivity(LOG, user, branch, "MRCM rule export successfully finished.");
			return exportPath;
		} catch (final Throwable t) {
			LogUtils.logExportActivity(LOG, user, branch, "Failed to export MRCM rules.");
			throw new SnowowlRuntimeException("Failed to export MRCM rules.", t);
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

}
