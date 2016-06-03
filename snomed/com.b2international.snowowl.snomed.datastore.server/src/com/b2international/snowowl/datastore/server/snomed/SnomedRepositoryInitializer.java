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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;
import static com.b2international.snowowl.snomed.datastore.SnomedCDORootResourceNameProvider.GENERATOR_RESOURCE_NAME;
import static com.b2international.snowowl.snomed.datastore.SnomedCDORootResourceNameProvider.ROOT_RESOURCE_NAME;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;
import static org.eclipse.emf.cdo.common.id.CDOID.NULL;
import static org.eclipse.emf.ecore.InternalEObject.EStore.NO_INDEX;
import static org.eclipse.net4j.util.lifecycle.LifecycleUtil.checkActive;

import java.util.Collection;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.datastore.server.RepositoryInitializer;
import com.b2international.snowowl.snomed.SnomedPackage;

/**
 * SNOMED&nbsp;CT specific repository initializer.
 * 
 *
 */
public class SnomedRepositoryInitializer extends RepositoryInitializer {

	private static final Collection<String> RELEVANT_ROOT_RESOURCE_NAMES = unmodifiableCollection(newHashSet(
			ROOT_RESOURCE_NAME, 
			GENERATOR_RESOURCE_NAME
	));
	
	@Override
	protected boolean shouldCreateDbIndexes() {
		return false;
	}
	
	@Override
	protected Collection<InternalCDORevision> createAdditionalRevisionsForResource(
			final InternalCDORevision resourceRevision, final boolean metaRoot, final InternalRepository repository) {
		
		checkNotNull(resourceRevision, "resourceRevision");
		checkNotNull(repository, "repository");
		checkActive(repository);
		final EClass eClass = resourceRevision.getEClass();
		checkNotNull(eClass, "EClass was null on revision: " + resourceRevision);
		checkArgument(isCdoResource(eClass), "Expected " + CDOResource.class + " got: " + eClass);

		if (metaRoot) {
			return emptySet();
		}

		final String resourceName = getResourceName(resourceRevision);
		if (!RELEVANT_ROOT_RESOURCE_NAMES.contains(resourceName)) {
			return emptySet();
		}

		final InternalCDORevision newConceptsRevision = createConceptsRevision(repository);
		newConceptsRevision.setBranchPoint(resourceRevision.getBranch().getHead());
		newConceptsRevision.setContainerID(NULL);
		newConceptsRevision.setContainingFeatureID(0);
		final CDOID newConceptsCdoId = getCdoIdForNewRevision(repository, newConceptsRevision);
		newConceptsRevision.setID(newConceptsCdoId);
		newConceptsRevision.setResourceID(resourceRevision.getID());
		
		final CDOList rootContentsList = resourceRevision.getList(EresourcePackage.eINSTANCE.getCDOResource_Contents());
		rootContentsList.add(newConceptsCdoId);
		
		return singleton(newConceptsRevision);
	}

	private String getResourceName(final InternalCDORevision resourceRevision) {
		return valueOfOrEmptyString(resourceRevision.get(
				EresourcePackage.eINSTANCE.getCDOResourceNode_Name(), 
				NO_INDEX));
	}

	private boolean isCdoResource(final EClass eClass) {
		return EresourcePackage.eINSTANCE.getCDOResource().equals(eClass);
	}
	
	private InternalCDORevision createConceptsRevision(final InternalRepository repository) {
		final InternalCDORevisionManager revisionManager = repository.getRevisionManager();
		final CDORevisionFactory factory = revisionManager.getFactory();
		return (InternalCDORevision) factory.createRevision(SnomedPackage.eINSTANCE.getConcepts());
	}
	
}