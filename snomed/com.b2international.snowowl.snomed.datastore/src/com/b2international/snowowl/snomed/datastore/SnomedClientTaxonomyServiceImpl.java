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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.eclipse.emf.ecore.EPackage;

import com.b2international.snowowl.core.annotations.Client;
import com.b2international.snowowl.datastore.ActiveBranchPathAwareService;
import com.b2international.snowowl.snomed.SnomedPackage;

/**
 * {@link SnomedTaxonomyClientService} implementation that delegates to the {@link SnomedTaxonomyService}
 * based on the current branch configuration of the SNOMED&nbsp;CT ontology.
 */
@Client
public class SnomedClientTaxonomyServiceImpl extends ActiveBranchPathAwareService implements SnomedTaxonomyClientService {

	private final SnomedTaxonomyService delegate;

	public SnomedClientTaxonomyServiceImpl(final SnomedTaxonomyService delegate) {
		this.delegate = checkNotNull(delegate, "delegate");
	}
	
	@Override
	public boolean isActive(final String conceptId) {
		return delegate.isActive(getBranchPath(), conceptId);
	}
	
	@Override
	public String getSnomedRoot() {
		return delegate.getSnomedRoot(getBranchPath());
	}

	@Override
	public Collection<String> getSubtypes(final String conceptId) {
		return delegate.getSubtypes(getBranchPath(), conceptId);
	}

	@Override
	public Collection<String> getAllSubtypes(final String conceptId) {
		return delegate.getAllSubtypes(getBranchPath(), conceptId);
	}

	@Override
	public int getSubtypesCount(final String conceptId) {
		return delegate.getSubtypesCount(getBranchPath(), conceptId);
	}

	@Override
	public int getAllSubtypesCount(final String conceptId) {
		return delegate.getAllSubtypesCount(getBranchPath(), conceptId);
	}

	@Override
	public Collection<String> getSupertypes(final String conceptId) {
		return delegate.getSupertypes(getBranchPath(), conceptId);
	}

	@Override
	public Collection<String> getAllSupertypes(final String conceptId) {
		return delegate.getAllSupertypes(getBranchPath(), conceptId);
	}

	@Override
	public int getSupertypesCount(final String conceptId) {
		return delegate.getSupertypesCount(getBranchPath(), conceptId);
	}

	@Override
	public int getAllSupertypesCount(final String conceptId) {
		return delegate.getAllSupertypesCount(getBranchPath(), conceptId);
	}

	@Override
	public int getOutboundConceptsCount(final String conceptId) {
		return delegate.getOutboundConceptsCount(getBranchPath(), conceptId);
	}
	
	@Override
	public Collection<String> getOutboundConcepts(final String conceptId) {
		return delegate.getOutboundConcepts(getBranchPath(), conceptId);
	}

	@Override
	public Collection<String> getOutboundConcepts(final String conceptId, final String typeId) {
		return delegate.getOutboundConcepts(getBranchPath(), conceptId, typeId);
	}
	
	@Override
	public Collection<String> getAllOutboundConcepts(final String conceptId) {
		return delegate.getAllOutboundConcepts(getBranchPath(), conceptId);
	}

	@Override
	public boolean hasOutboundRelationshipOfType(final String conceptId, final String typeId) {
		return delegate.hasOutboundRelationshipOfType(getBranchPath(), conceptId, typeId);
	}

	@Override
	public Collection<String> getOutboundRelationshipTypes(final String conceptId) {
		return delegate.getOutboundRelationshipTypes(getBranchPath(), conceptId);
	}
	
	@Override
	public Collection<String> getInboundConcepts(final String conceptId) {
		return delegate.getInboundConcepts(getBranchPath(), conceptId);
	}

	@Override
	public Collection<String> getInboundConcepts(final String conceptId, final String typeId) {
		return delegate.getInboundConcepts(getBranchPath(), conceptId, typeId);
	}
	
	@Override
	public Collection<String> getAllInboundConcepts(final String conceptId) {
		return delegate.getAllInboundConcepts(getBranchPath(), conceptId);
	}

	@Override
	public boolean hasInboundRelationshipOfType(final String conceptId, final String typeId) {
		return delegate.hasInboundRelationshipOfType(getBranchPath(), conceptId, typeId);
	}

	@Override
	public int getDepth(final String conceptId) {
		return delegate.getDepth(getBranchPath(), conceptId);
	}

	@Override
	public int getHeight(final String conceptId) {
		return delegate.getHeight(getBranchPath(), conceptId);
	}

	@Override
	public boolean isLeaf(final String conceptId) {
		return delegate.isLeaf(getBranchPath(), conceptId);
	}

	@Override
	public Collection<String> getContainerRefSetIds(final String conceptId) {
		return delegate.getContainerRefSetIds(getBranchPath(), conceptId);
	}
	
	@Override
	public Collection<String> evaluateEscg(final String expression) {
		return delegate.evaluateEscg(getBranchPath(), expression);
	}

	@Override
	protected EPackage getEPackage() {
		return SnomedPackage.eINSTANCE;
	}
}