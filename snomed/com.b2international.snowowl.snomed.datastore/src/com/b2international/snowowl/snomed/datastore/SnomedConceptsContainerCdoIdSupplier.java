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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator.GENERATOR_RESOURCE_NAME;
import static org.eclipse.emf.cdo.common.id.CDOID.NULL;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;

import com.b2international.snowowl.snomed.Concepts;

/**
 * Supplies the unique {@link CDOID} of the container for collecting default SNOMED&nbsp;CT concepts
 * and generated drug concepts.
 * May return with {@link CDOID#NULL NULL ID} if the repository for SNOMED&nbsp;CT has never been 
 * initialized yet.
 * 
 *
 */
public enum SnomedConceptsContainerCdoIdSupplier {

	/**The shared concept container CDO ID supplier.*/
	INSTANCE;

	private CDOID defaultContainerCdoId = NULL;
	private CDOID generatedDrugContainerCdoId = NULL;
	private final Object mutex = new Object();
	
	/**
	 * Returns with the {@link CDOID} of the container of all default concepts.
	 * <p>May return with {@link CDOID#NULL NULL ID} if the repository has never been started 
	 * for SNOMED&nbsp;CT.
	 * @return the CDO ID of the default concept container.
	 */
	public CDOID getDefaultContainerCdoId() {
		if (NULL.equals(defaultContainerCdoId)) {
			synchronized (mutex) {
				if (NULL.equals(defaultContainerCdoId)) {
					try (final SnomedEditingContext context = new SnomedEditingContext(createMainPath())) {
						final CDOResource rootResource = context.getEditingContextRootResource();
						final Concepts concepts = (Concepts) rootResource.getContents().get(0);
						defaultContainerCdoId = concepts.cdoID();
					}
				}
			}
		}
		return defaultContainerCdoId;
	}

	/**
	 * Returns with the {@link CDOID} of the container of all generated drug concepts.
	 * <p>May return with {@link CDOID#NULL NULL ID} if the repository has never been started 
	 * for SNOMED&nbsp;CT.
	 * @return the CDO ID of the default concept container.
	 */
	public CDOID getGeneratedDrugContainerCdoId() {
		if (NULL.equals(generatedDrugContainerCdoId)) {
			synchronized (mutex) {
				if (NULL.equals(generatedDrugContainerCdoId)) {
					try (final SnomedEditingContext context = new SnomedEditingContext(createMainPath())) {
						final CDOResource rootResource = context.getTransaction().getOrCreateResource(GENERATOR_RESOURCE_NAME);
						final Concepts concepts = (Concepts) rootResource.getContents().get(0);
						generatedDrugContainerCdoId = concepts.cdoID();
					}
				}
			}
		}
		return generatedDrugContainerCdoId;
	}

}