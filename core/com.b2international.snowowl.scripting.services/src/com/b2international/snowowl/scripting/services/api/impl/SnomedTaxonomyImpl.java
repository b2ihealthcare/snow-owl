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
package com.b2international.snowowl.scripting.services.api.impl;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;

import java.util.Collection;

import com.b2international.snowowl.snomed.datastore.SnomedTaxonomy;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyClientService;

/**
 * Highly customized taxonomy for supplying an ephemeral
 * semantic store when evaluating ESCG expressions.
 *
 */
public class SnomedTaxonomyImpl implements SnomedTaxonomy {

	private final SnomedTaxonomyClientService delegate = getServiceForClass(SnomedTaxonomyClientService.class);

	@Override
	public boolean isActive(final String conceptId) {
		return delegate.isActive(conceptId);
	}
	
	@Override
	public String getSnomedRoot() {
		return delegate.getSnomedRoot();
	}

	@Override
	public Collection<String> getSubtypes(final String conceptId) {
		return delegate.getSubtypes(conceptId);
	}

	@Override
	public Collection<String> getAllSubtypes(final String conceptId) {
		return delegate.getAllSubtypes(conceptId);
	}

	@Override
	public int getSubtypesCount(final String conceptId) {
		return delegate.getSubtypesCount(conceptId);
	}

	@Override
	public int getAllSubtypesCount(final String conceptId) {
		return delegate.getAllSubtypesCount(conceptId);
	}

	@Override
	public Collection<String> getSupertypes(final String conceptId) {
		return delegate.getSupertypes(conceptId);
	}

	@Override
	public Collection<String> getAllSupertypes(final String conceptId) {
		return delegate.getAllSupertypes(conceptId);
	}

	@Override
	public int getSupertypesCount(final String conceptId) {
		return delegate.getSupertypesCount(conceptId);
	}

	@Override
	public int getAllSupertypesCount(final String conceptId) {
		return delegate.getAllSupertypesCount(conceptId);
	}

	@Override
	public int getOutboundConceptsCount(final String conceptId) {
		return delegate.getOutboundConceptsCount(conceptId);
	}
	
	@Override
	public Collection<String> getOutboundConcepts(final String conceptId) {
		return delegate.getOutboundConcepts(conceptId);
	}

	@Override
	public Collection<String> getOutboundConcepts(final String conceptId, final String typeId) {
		return delegate.getOutboundConcepts(conceptId, typeId);
	}

	@Override
	public Collection<String> getAllOutboundConcepts(final String conceptId) {
		return delegate.getAllOutboundConcepts(conceptId);
	}
	
	@Override
	public boolean hasOutboundRelationshipOfType(final String conceptId, final String typeId) {
		return delegate.hasOutboundRelationshipOfType(conceptId, typeId);
	}

	@Override
	public Collection<String> getInboundConcepts(final String conceptId) {
		return delegate.getInboundConcepts(conceptId);
	}

	@Override
	public Collection<String> getInboundConcepts(final String conceptId, final String typeId) {
		return delegate.getInboundConcepts(conceptId, typeId);
	}

	@Override
	public Collection<String> getAllInboundConcepts(final String conceptId) {
		return delegate.getAllInboundConcepts(conceptId);
	}
	
	@Override
	public boolean hasInboundRelationshipOfType(final String conceptId, final String typeId) {
		return delegate.hasInboundRelationshipOfType(conceptId, typeId);
	}

	@Override
	public Collection<String> getOutboundRelationshipTypes(final String conceptId) {
		return delegate.getOutboundRelationshipTypes(conceptId);
	}
	
	@Override
	public int getDepth(final String conceptId) {
		return delegate.getDepth(conceptId);
	}

	@Override
	public int getHeight(final String conceptId) {
		return delegate.getHeight(conceptId);
	}

	@Override
	public boolean isLeaf(final String conceptId) {
		return delegate.isLeaf(conceptId);
	}

	@Override
	public Collection<String> getContainerRefSetIds(final String conceptId) {
		return delegate.getContainerRefSetIds(conceptId);
	}
	
	@Override
	public Collection<String> evaluateEscg(final String expression) {
		return delegate.evaluateEscg(expression);
	}
	
}