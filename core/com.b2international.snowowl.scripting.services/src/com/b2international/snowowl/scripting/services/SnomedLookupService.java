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
package com.b2international.snowowl.scripting.services;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.services.ISnomedLookupService;

/**
 * Delegate {@link ISnomedLookupService lookup service} for SNOMED&nbsp;CT.
 * @see ISnomedLookupService
 */
public class SnomedLookupService implements ISnomedLookupService {

	private final ISnomedLookupService delegate;
	
	public SnomedLookupService() {
		delegate = ApplicationContext.getInstance().getService(com.b2international.snowowl.snomed.datastore.services.SnomedLookupService.class);
	}

	@Override
	public boolean hasRelationship(final long sourceConceptId, final long destinationConceptId, final long relationshipTypeId) {
		return delegate.hasRelationship(sourceConceptId, destinationConceptId, relationshipTypeId);
	}

	@Override
	public boolean hasRelationship(final String sourceConceptId, final String destinationConceptId, final String relationshipTypeId) {
		return delegate.hasRelationship(sourceConceptId, destinationConceptId, relationshipTypeId);
	}

	@Override
	public String getConceptId(final String descriptionId) {
		return delegate.getConceptId(descriptionId);
	}

	@Override
	public String[] getDescriptionTerms(final String conceptId) {
		return delegate.getDescriptionTerms(conceptId);
	}

	@Override
	public String[] getDescriptionTerms(final long conceptId, final long descriptionTypeConceptId) {
		return delegate.getDescriptionTerms(conceptId, descriptionTypeConceptId);
	}

	@Override
	public String getPreferredTerm(final String conceptId) {
		return delegate.getPreferredTerm(conceptId);
	}

	@Override
	public String getFullySpecifiedName(final long conceptId) {
		return delegate.getFullySpecifiedName(conceptId);
	}

	@Override
	public Description getFsnDescription(final long conceptId) {
		return delegate.getFsnDescription(conceptId);
	}

	@Override
	public String[] getSynonyms(final long conceptId) {
		return delegate.getSynonyms(conceptId);
	}

	@Override
	public boolean isDescriptionExist(final String conceptId, final String termToMatch, final CaseSignificance caseSensitivity) {
		return delegate.isDescriptionExist(conceptId, termToMatch, caseSensitivity);
	}

	@Override
	public boolean isDescriptionExist(final long conceptId, final String termToMatch, final CaseSignificance caseSensitivity, final long descriptionTypeConceptId) {
		return delegate.isDescriptionExist(conceptId, termToMatch, caseSensitivity, descriptionTypeConceptId);
	}

	@Override
	public boolean isDescriptionExist(final String conceptId, final String termToMatch, final CaseSignificance caseSensitivity, final String descriptionTypeConceptId) {
		return delegate.isDescriptionExist(conceptId, termToMatch, caseSensitivity, descriptionTypeConceptId);
	}

	@Override
	public boolean descriptionTermMatches(final String descriptionTerm, final String termToMatch, final CaseSignificance caseSensitivity) {
		return delegate.descriptionTermMatches(descriptionTerm, termToMatch, caseSensitivity);
	}

	@Override
	public Concept getConcept(final long conceptId) {
		return delegate.getConcept(conceptId);
	}

	@Override
	public Concept getConceptById(final String conceptId) {
		return delegate.getConceptById(conceptId);
	}

	@Override
	public boolean isConceptExist(final long conceptId) {
		return delegate.isConceptExist(conceptId);
	}

	@Override
	public boolean isConceptExist(final String conceptId) {
		return delegate.isConceptExist(conceptId);
	}
	
	@Override
	public String generateNewConceptId() {
		return delegate.generateNewConceptId();
	}

	@Override
	public String generateNewDescriptionId() {
		return delegate.generateNewDescriptionId();
	}
	
	@Override
	public String generateNewRelationshipId() {
		return delegate.generateNewRelationshipId();
	}

}