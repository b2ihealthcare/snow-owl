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

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.scripting.services.api.IHierarchicalService;
import com.b2international.snowowl.semanticengine.simpleast.subsumption.SubsumptionTester;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Concept hierarchy service for the SNOMED CT ontology.
 * @see IHierarchicalService
 */
public class SnomedHierarchicalService implements IHierarchicalService {

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSnomedRoot()
	 */
	@Override
	public SnomedConceptDocument getSnomedRoot() {
		return getConcept(Concepts.ROOT_CONCEPT);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getRootConcepts()
	 */
	@Override
	public List<SnomedConceptDocument> getRootConcepts() {
		return Lists.newArrayList(getTerminologyBrowser().getRootConcepts());
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getConcept(long)
	 */
	@Override
	public SnomedConceptDocument getConcept(final long conceptId) {
		return getTerminologyBrowser().getConcept(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSubtypes(long)
	 */
	@Override
	public Collection<SnomedConceptDocument> getSubtypes(final long conceptId) {
		return getTerminologyBrowser().getSubTypesById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getDirectSubtypeCount(long)
	 */
	@Override
	public long getDirectSubtypeCount(final long conceptId) {
		return getTerminologyBrowser().getSubTypeCountById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSubtypeCount(long)
	 */
	@Override
	public long getAllSubtypeCount(final long conceptId) {
		return getTerminologyBrowser().getAllSubTypeCountById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSubtypes(long)
	 */
	@Override
	public Collection<SnomedConceptDocument> getAllSubtypes(final long conceptId) {
		return getTerminologyBrowser().getAllSubTypesById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSupertypes(long)
	 */
	@Override
	public Collection<SnomedConceptDocument> getSupertypes(final long conceptId) {
		return getTerminologyBrowser().getSuperTypesById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypes(long)
	 */
	@Override
	public Collection<SnomedConceptDocument> getAllSupertypes(final long conceptId) {
		return getTerminologyBrowser().getAllSuperTypesById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getDirectSupertypeCount(long)
	 */
	@Override
	public long getDirectSupertypeCount(final long conceptId) {
		return getTerminologyBrowser().getSubTypeCountById(String.valueOf(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypeCount(long)
	 */
	@Override
	public long getAllSupertypeCount(final long conceptId) {
		return getTerminologyBrowser().getAllSuperTypeCountById(String.valueOf(conceptId));
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
	public List<SnomedConceptDocument> getShortestPath(final long startingConcept, final long endConcept) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSourceConcepts(long, long)
	 */
	@Override
	public Collection<SnomedConceptDocument> getSourceConcepts(final long targetConceptId, final long relationshipTypeId) {
		
		final String conceptId = Long.toString(targetConceptId);
		final String typeId = Long.toString(relationshipTypeId);
		return getSourceConcepts(conceptId, typeId);
	}
	
	@Override
	public Collection<SnomedConceptDocument> getSourceConcepts(final String targetConceptId, final String relationshipTypeId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByDestination(targetConceptId)
				.filterByType(relationshipTypeId)
				.setExpand("sourceConcept(expand(pt()))")
				.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedRelationships, Collection<ISnomedConcept>>() {
					@Override
					public Collection<ISnomedConcept> apply(SnomedRelationships input) {
						return FluentIterable.from(input).transform(new Function<ISnomedRelationship, ISnomedConcept>() {
							@Override
							public ISnomedConcept apply(ISnomedRelationship input) {
								return input.getSourceConcept();
							}
						}).toList();
					}
				})
				.then(new Function<Collection<ISnomedConcept>, Collection<SnomedConceptDocument>>() {
					@Override
					public Collection<SnomedConceptDocument> apply(Collection<ISnomedConcept> input) {
						return SnomedConceptDocument.fromConcepts(input);
					}
				})
				.getSync();
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getTargetConcepts(java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptDocument> getTargetConcepts(String sourceConceptId, String relationshipTypeId) {
		
		List<SnomedConceptDocument> targetConceptIndexEntries = Lists.newArrayList();
		List<SnomedRelationshipIndexEntry> activeOutboundRelationships = getActiveOutboundRelationships(sourceConceptId, relationshipTypeId);
		for (SnomedRelationshipIndexEntry snomedRelationshipIndexEntry : activeOutboundRelationships) {
			String targetConceptId = snomedRelationshipIndexEntry.getDestinationId();
			SnomedConceptDocument targetConceptIndexEntry = getConcept(targetConceptId);
			targetConceptIndexEntries.add(targetConceptIndexEntry);
		}
		return targetConceptIndexEntries;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getTargetConcepts(long, long)
	 */
	@Override
	public Collection<SnomedConceptDocument> getTargetConcepts(long sourceConceptId, long relationshipTypeId) {
		final String conceptId = Long.toString(sourceConceptId);
		final String typeId = Long.toString(relationshipTypeId);
		return getTargetConcepts(conceptId, typeId);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getConcept(java.lang.String)
	 */
	@Override
	public SnomedConceptDocument getConcept(final String conceptId) {
		return getTerminologyBrowser().getConcept(conceptId);
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSubtypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptDocument> getSubtypes(final String conceptId) {
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
	public Collection<SnomedConceptDocument> getAllSubtypes(final String conceptId) {
		return getAllSubtypes(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getSupertypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptDocument> getSupertypes(final String conceptId) {
		return getSupertypes(asLong(conceptId));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getAllSupertypes(java.lang.String)
	 */
	@Override
	public Collection<SnomedConceptDocument> getAllSupertypes(final String conceptId) {
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
	public List<SnomedConceptDocument> getShortestPath(final String startingConcept, final String endConcept) {
		return getShortestPath(asLong(startingConcept), asLong(endConcept));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getOutboundRelationships(java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundRelationships(final String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterBySource(conceptId)
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedRelationships, List<SnomedRelationshipIndexEntry>>() {
					@Override
					public List<SnomedRelationshipIndexEntry> apply(SnomedRelationships input) {
						return ImmutableList.copyOf(SnomedRelationshipIndexEntry.fromRelationships(input));
					}
				})
				.getSync();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getActiveOutboundRelationships(java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterBySource(conceptId)
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedRelationships, List<SnomedRelationshipIndexEntry>>() {
					@Override
					public List<SnomedRelationshipIndexEntry> apply(SnomedRelationships input) {
						return ImmutableList.copyOf(SnomedRelationshipIndexEntry.fromRelationships(input));
					}
				})
				.getSync();
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.scripting.services.api.IHierarchicalService#getOutboundRelationships(java.lang.String, java.lang.String)
	 */
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundRelationships(final String conceptId, final String relationshipTypeId) {
		return Lists.newArrayList(Collections2.filter(getOutboundRelationships(conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return relationshipTypeId.equals(relationship.getTypeId());
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
				return relationshipTypeId.equals(relationship.getTypeId());
			}
		}));
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getInboundRelationships(final String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByDestination(conceptId)
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedRelationships, List<SnomedRelationshipIndexEntry>>() {
					@Override
					public List<SnomedRelationshipIndexEntry> apply(SnomedRelationships input) {
						return ImmutableList.copyOf(SnomedRelationshipIndexEntry.fromRelationships(input));
					}
				})
				.getSync();
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final String conceptId) {
		return SnomedRequests.prepareSearchRelationship()
				.all()
				.filterByActive(true)
				.filterByDestination(conceptId)
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedRelationships, List<SnomedRelationshipIndexEntry>>() {
					@Override
					public List<SnomedRelationshipIndexEntry> apply(SnomedRelationships input) {
						return ImmutableList.copyOf(SnomedRelationshipIndexEntry.fromRelationships(input));
					}
				})
				.getSync();
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getInboundRelationships(final String conceptId, final String relationshipTypeId) {
		return Lists.newArrayList(Collections2.filter(getActiveInboundRelationships(conceptId), new Predicate<SnomedRelationshipIndexEntry>() {
			@Override public boolean apply(final SnomedRelationshipIndexEntry relationship) {
				return relationshipTypeId.equals(relationship.getTypeId());
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
				return relationshipTypeId.equals(relationship.getTypeId());
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
	
	private long asLong(final String id) {
		return Long.parseLong(id);
	}
	
	private String toString(final long id) {
		return String.valueOf(id);
	}

}
