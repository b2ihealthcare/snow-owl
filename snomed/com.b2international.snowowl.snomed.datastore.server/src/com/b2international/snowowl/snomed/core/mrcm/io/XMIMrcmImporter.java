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

import java.io.InputStream;
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
import com.b2international.snowowl.datastore.server.CDOServerUtils;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;

/**
 * @since 4.4
 */
public class XMIMrcmImporter implements MrcmImporter {

	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmImporter.class);

	@Override
	public void doImport(final String userName, final InputStream content) {
		final IBranchPath branch = BranchPathUtils.createMainPath();
		LogUtils.logImportActivity(LOGGER, userName, branch, "Importing MRCM rules...");
		final URI uri = URI.createFileURI(UUID.randomUUID().toString());
		try (SnomedEditingContext context = new SnomedEditingContext(branch)) {
			
			final ResourceSet resourceSet = new ResourceSetImpl();
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
			final Resource resource = resourceSet.createResource(uri);
			resource.load(content, null);
			
			final ConceptModel model = (ConceptModel) resource.getContents().get(0);
			context.getConstraints().clear();
			// Prevent container changes by copying all constraints
			context.getConstraints().addAll(EcoreUtil.copyAll(model.getConstraints()));

			LogUtils.logImportActivity(LOGGER, userName, branch, "MRCM rule import to {} successfully finished.", branch.getPath());
			CDOServerUtils.commit(context, userName, "Imported MRCM rules", null);
		} catch (final Throwable t) {
			LogUtils.logImportActivity(LOGGER, userName, branch, "Failed to import MRCM rules to {}", branch, t);
			throw new SnowowlRuntimeException(t);
		}
	}

}