/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.OutputStream;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;

/**
 * 4.4
 */
public class XMIMrcmExporter {

	private static final Logger LOG = LoggerFactory.getLogger(XMIMrcmExporter.class);
	
	public void doExport(String user, OutputStream stream) {
		final IBranchPath branch = BranchPathUtils.createMainPath();
		try (SnomedEditingContext context = new SnomedEditingContext(branch)) {
			LogUtils.logExportActivity(LOG, user, branch, "Exporting MRCM rules...");

			final ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			final URI fileURI = URI.createFileURI(UUID.randomUUID().toString());
			final Resource resource = resourceSet.createResource(fileURI);
			
			// Prevent container changes by copying all constraints
			final ConceptModel conceptModel = MrcmFactory.eINSTANCE.createConceptModel();
			conceptModel.getConstraints().addAll(EcoreUtil.copyAll(context.getConstraints()));
			resource.getContents().add(conceptModel);
			resource.save(stream, null);
			
			LogUtils.logExportActivity(LOG, user, branch, "MRCM rule export successfully finished.");
		} catch (final Throwable t) {
			LogUtils.logExportActivity(LOG, user, branch, "Failed to export MRCM rules.");
			throw new SnowowlRuntimeException("Failed to export MRCM rules.", t);
		}
	}

}
