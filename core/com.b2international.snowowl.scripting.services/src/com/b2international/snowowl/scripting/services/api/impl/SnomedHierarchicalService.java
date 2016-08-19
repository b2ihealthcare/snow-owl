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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.scripting.services.api.IHierarchicalService;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Concept hierarchy service for the SNOMED CT ontology.
 * @see IHierarchicalService
 */
public class SnomedHierarchicalService implements IHierarchicalService {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSnomedRoot()
	 */
	@Override
	public SnomedConceptIndexEntry getSnomedRoot() {
		return getConcept(Concepts.ROOT_CONCEPT);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getRootConcepts()
	 */
	@Override
	public List<SnomedConceptIndexEntry> getRootConcepts() {
		return Lists.newArrayList(getTerminologyBrowser().getRootConcepts());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getConcept(long)
	 */
	@Override
	public SnomedConceptIndexEntry getConcept(final long conceptId) {
		return getTerminologyBrowser().getConcept(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSubtypes(long)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getSubtypes(final long conceptId) {
		return getTerminologyBrowser().getSubTypes(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getDirectSubtypeCount(long)
	 */
	@Override
	public long getDirectSubtypeCount(final long conceptId) {
		return getTerminologyBrowser().getSubTypeCount(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSubtypeCount(long)
	 */
	@Override
	public long getAllSubtypeCount(final long conceptId) {
		return getTerminologyBrowser().getAllSubTypeCount(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSubtypes(long)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubtypes(final long conceptId) {
		return getTerminologyBrowser().getAllSubTypes(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSupertypes(long)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getSupertypes(final long conceptId) {
		return getTerminologyBrowser().getSuperTypes(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypes(long)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSupertypes(final long conceptId) {
		return getTerminologyBrowser().getAllSuperTypes(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getDirectSupertypeCount(long)
	 */
	@Override
	public long getDirectSupertypeCount(final long conceptId) {
		return getTerminologyBrowser().getSubTypeCount(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypeCount(long)
	 */
	@Override
	public long getAllSupertypeCount(final long conceptId) {
		return getTerminologyBrowser().getAllSuperTypeCount(getConcept(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#isSubsumed(long, long)
	 */
	@Override
	public boolean isSubsumed(final long parentRefsetId, final long subsumedRefsetId) {
		return new SubsumptionTester(getTerminologyBrowser()).isSubsumed(getConcept(parentRefsetId), getConcept(subsumedRefsetId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#isAncestor(long, long)
	 */
	@Override
	public boolean isAncestor(final long parentConceptId, final long childConceptId) {
		return getTerminologyBrowser().isSuperTypeOfById(toString(parentConceptId), toString(childConceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getShortestPath(long, long)
	 */
	@Override
	public List<SnomedConceptIndexEntry> getShortestPath(final long startingConcept, final long endConcept) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSourceConcepts(long, long)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getSourceConcepts(final long targetConceptId, final long relationshipTypeId) {
		
		final String conceptId = Long.toString(targetConceptId);
		final String typeId = Long.toString(relationshipTypeId);
		return getSourceConcepts(conceptId, typeId);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSourceConcepts(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getSourceConcepts(final String targetConceptId, final String relationshipTypeId) {
		
		final Set<SnomedConceptIndexEntry> $ = Sets.newHashSet();
		
		final List<SnomedRelationshipIndexEntry> inboundStatements = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class).getInboundStatementsById(targetConceptId);
		
		for (final SnomedRelationshipIndexEntry relationship : inboundStatements) {
			
			if (!relationship.isActive()) {
				continue; //inactive statement
			}
			
			if (!relationshipTypeId.equals(relationship.getAttributeId())) {
				continue; //not the specified type
			}

			final SnomedConceptIndexEntry sourceConcept = getTerminologyBrowser().getConcept(relationship.getObjectId());
			
			if (!sourceConcept.isActive()) {
				continue; //ignore inactive target concepts
			}
			$.add(sourceConcept);
		}
		//groovy loves lists.
		return Lists.newArrayList($);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getTargetConcepts(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getTargetConcepts(String sourceConceptId, String relationshipTypeId) {
		
		List<SnomedConceptIndexEntry> targetConceptIndexEntries = Lists.newArrayList();
		List<SnomedRelationshipIndexEntry> activeOutboundRelationships = getActiveOutboundRelationships(sourceConceptId, relationshipTypeId);
		for (SnomedRelationshipIndexEntry snomedRelationshipIndexEntry : activeOutboundRelationships) {
			String targetConceptId = snomedRelationshipIndexEntry.getValueId();
			SnomedConceptIndexEntry targetConceptIndexEntry = getConcept(targetConceptId);
			targetConceptIndexEntries.add(targetConceptIndexEntry);
		}
		return targetConceptIndexEntries;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getTargetConcepts(long, long)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getTargetConcepts(long sourceConceptId, long relationshipTypeId) {
		final String conceptId = Long.toString(sourceConceptId);
		final String typeId = Long.toString(relationshipTypeId);
		return getTargetConcepts(conceptId, typeId);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getConcept(java.lang.String)
	 */
	@Override
	public SnomedConceptIndexEntry getConcept(final String conceptId) {
		return getTerminologyBrowser().getConcept(conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSubtypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getSubtypes(final String conceptId) {
		return getSubtypes(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getDirectSubtypeCount(java.lang.String)
	 */
	@Override
	public long getDirectSubtypeCount(final String conceptId) {
		return getDirectSubtypeCount(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSubtypeCount(java.lang.String)
	 */
	@Override
	public long getAllSubtypeCount(final String conceptId) {
		return getAllSubtypeCount(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSubtypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSubtypes(final String conceptId) {
		return getAllSubtypes(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSupertypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getSupertypes(final String conceptId) {
		return getSupertypes(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptIndexEntry> getAllSupertypes(final String conceptId) {
		return getAllSupertypes(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getDirectSupertypeCount(java.lang.String)
	 */
	@Override
	public long getDirectSupertypeCount(final String conceptId) {
		return getDirectSupertypeCount(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypeCount(java.lang.String)
	 */
	@Override
	public long getAllSupertypeCount(final String conceptId) {
		return getAllSupertypeCount(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#isSubsumed(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isSubsumed(final String parentRefsetId, final String subsumedRefsetId) {
		return isSubsumed(asLong(parentRefsetId), asLong(subsumedRefsetId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#isAncestor(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isAncestor(final String parentConceptId, final String childConceptId) {
		return isAncestor(asLong(parentConceptId), asLong(childConceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getShortestPath(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SnomedConceptIndexEntry> getShortestPath(final String startingConcept, final String endConcept) {
		return getShortestPath(asLong(startingConcept), asLong(endConcept));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getOutboundRelationships(java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundRelationships(final String conceptId) {
		return getStatementBrowser().getOutboundStatementsById(conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveOutboundRelationships(java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final String conceptId) {
		return Lists.newArrayList(getStatementBrowser().getActiveOutboundStatementsById(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getOutboundRelationships(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundRelationships(final String conceptId, final String relationshipTypeId) {
		return Lists.newArrayList(Collections2.filter(getOutboundRelationships(conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return relationshipTypeId.equals(relationship.getAttributeId());
			}
		}));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveOutboundRelationships(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final String conceptId, final String relationshipTypeId) {
		return Lists.newArrayList(Collections2.filter(getActiveOutboundRelationships(conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return relationshipTypeId.equals(relationship.getAttributeId());
			}
		}));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getInboundRelationships(java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getInboundRelationships(final String conceptId) {
		return getStatementBrowser().getInboundStatementsById(conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveInboundRelationships(java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final String conceptId) {
		return Lists.newArrayList(getStatementBrowser().getActiveInboundStatementsById(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getInboundRelationships(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getInboundRelationships(final String conceptId, final String relationshipTypeId) {
		return Lists.newArrayList(Collections2.filter(getActiveInboundRelationships(conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return relationshipTypeId.equals(relationship.getAttributeId());
			}
		}));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveIntboundRelationships(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final String conceptId, final String relationshipTypeId) {
		return Lists.newArrayList(Collections2.filter(getActiveInboundRelationships(conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return relationshipTypeId.equals(relationship.getAttributeId());
			}
		}));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getOutboundRelationships(long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundRelationships(final long conceptId) {
		return getOutboundRelationships(toString(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveOutboundRelationships(long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final long conceptId) {
		return getActiveOutboundRelationships(toString(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getOutboundRelationships(long, long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundRelationships(final long conceptId, final long relationshipTypeId) {
		return getOutboundRelationships(toString(conceptId), toString(relationshipTypeId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveOutboundRelationships(long, long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final long conceptId, final long relationshipTypeId) {
		return getActiveOutboundRelationships(toString(conceptId), toString(relationshipTypeId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getInboundRelationships(long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getInboundRelationships(final long conceptId) {
		return getInboundRelationships(toString(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveInboundRelationships(long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final long conceptId) {
		return getActiveInboundRelationships(toString(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getInboundRelationships(long, long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getInboundRelationships(final long conceptId, final long relationshipTypeId) {
		return getInboundRelationships(toString(conceptId), toString(relationshipTypeId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveIntboundRelationships(long, long)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final long conceptId, final long relationshipTypeId) {
		return getActiveInboundRelationships(toString(conceptId), toString(relationshipTypeId));
	}

	/*returns with the SNOMED CT concept hierarchy browsing service registered to the application context*/
	private SnomedClientTerminologyBrowser getTerminologyBrowser() {
		return Preconditions.checkNotNull(
				ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class), 
				"SNOMED CT concept hierarchy browsing service was null.");
	}
	
	/*returns with the SNOMED CT concept hierarchy browsing service registered to the application context*/
	private SnomedClientStatementBrowser getStatementBrowser() {
		return Preconditions.checkNotNull(
				ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class), 
				"SNOMED CT concept statement browsing service was null.");
	}

	private long asLong(final String id) {
		return Long.parseLong(id);
	}
	
	private String toString(final long id) {
		return String.valueOf(id);
	}

}
