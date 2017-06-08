/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIds;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.index.AbstractIndexBrowser;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedQueryBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Longs;

/**
 * Lucene based predicate browser implementation.
 * 
 */
public class SnomedServerPredicateBrowser extends AbstractIndexBrowser<PredicateIndexEntry> implements SnomedPredicateBrowser {

	private static final Set<String> PREDICATE_FIELDS = SnomedMappings.fieldsToLoad()
			.storageKey()
			.predicateCharacteristicTypeExpression()
			.predicateDataTypeLabel()
			.predicateDataType()
			.predicateDataTypeName()
			.predicateDescriptionTypeId()
			.predicateGroupRule()
			.predicateMultiple()
			.predicateRequired()
			.predicateQueryExpression()
			.predicateRelationshipTypeExpression()
			.predicateRelationshipValueExpression()
			.predicateType()
			.build();
	

	private static final Set<String> REFERRING_PREDICATE_FIELDS = SnomedMappings.fieldsToLoad().componentReferringPredicate().build();

	public SnomedServerPredicateBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}

	@Override
	public Set<ConstraintDomain> getConstraintDomains(final IBranchPath branchPath, final long storageKey) {
		return service.executeReadTransaction(branchPath, new IndexRead<Set<ConstraintDomain>>() {
			@Override
			public Set<ConstraintDomain> execute(IndexSearcher index) throws IOException {
				final String predicateKeyPrefix = String.format("%s%s", storageKey, PredicateUtils.PREDICATE_SEPARATOR);
				final Query query = SnomedMappings.newQuery()
						.concept()
						.and(new PrefixQuery(SnomedMappings.componentReferringPredicate().toTerm(predicateKeyPrefix)))
						.matchAll();
				
				final DocIdCollector collector = DocIdCollector.create(index.getIndexReader().maxDoc());
				index.search(query, collector);
				final DocIds docs = collector.getDocIDs();
				if (docs.size() > 0) {
					final Set<ConstraintDomain> result = newHashSet();
					final DocIdsIterator iterator = docs.iterator();
					while (iterator.next()) {
						final Document doc = index.doc(iterator.getDocID(), SnomedMappings.fieldsToLoad().id().componentReferringPredicate().build());
						for (final Iterator<IndexableField> itr = doc.iterator(); itr.hasNext();) {
							final IndexableField indexableField = itr.next();
							if (indexableField.name().equals(SnomedMappings.componentReferringPredicate().fieldName()) && !indexableField.stringValue().startsWith(predicateKeyPrefix)) {
								itr.remove();
							}
						}
						result.addAll(ConstraintDomain.of(doc));
					}
					return result;
				}
				return Collections.emptySet();
			}
		});
	}
	
	@Override
	public Set<ConstraintDomain> getAllConstraintDomains(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		return service.executeReadTransaction(branchPath, new IndexRead<Set<ConstraintDomain>>() {
			@Override
			public Set<ConstraintDomain> execute(final IndexSearcher index) throws IOException {
				final Query query = SnomedMappings.newQuery()
						.concept()
						.and(SnomedMappings.componentReferringPredicate().toExistsQuery())
						.matchAll();
				
				final DocIdCollector collector = DocIdCollector.create(index.getIndexReader().maxDoc());
				index.search(query, collector);
				final DocIds docs = collector.getDocIDs();
				if (docs.size() > 0) {
					final Set<ConstraintDomain> result = newHashSet();
					final DocIdsIterator iterator = docs.iterator();
					while (iterator.next()) {
						final Document doc = index.doc(iterator.getDocID(), SnomedMappings.fieldsToLoad().id().componentReferringPredicate().build());
						result.addAll(ConstraintDomain.of(doc));
					}
					return result;
				}
				return Collections.emptySet();
			}
		});
	}
	
	@Override
	public Collection<PredicateIndexEntry> getAllPredicates(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, SnomedMappings.newQuery().predicate().matchAll(), collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			return createResultObjects(branchPath, itr);
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Error while loading all MRCM predicates on '" + branchPath + "' branch.");
		}
	}
	
	@Override
	public Collection<PredicateIndexEntry> getPredicate(final IBranchPath branchPath, final long...storageKeys) {
		final SnomedQueryBuilder query = SnomedMappings.newQuery();
		for (final long storageKey : storageKeys) {
			query.storageKey(storageKey);
		}
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath, query.matchAny(), collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error whey retrieving predicates by storageKeys: " + storageKeys, e);
		}
	}

	@Override
	public Collection<PredicateIndexEntry> getPredicates(final IBranchPath branchPath, final String conceptId, final @Nullable String ruleRefSetId) {
		checkNotNull(conceptId, "Concept ID must not be null.");

		final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates = getServiceForClass(ISnomedComponentService.class).getPredicates(branchPath);
		final Set<PredicateIndexEntry> newPredicates = newHashSet();

		addPredicatesForFocus(conceptId, HierarchyInclusionType.SELF, predicates, newPredicates);
		addPredicatesForFocus(conceptId, HierarchyInclusionType.SELF_OR_DESCENDANT, predicates, newPredicates);
		
		final ISnomedConcept concept = SnomedRequests.prepareGetConcept()
			.setComponentId(conceptId)
			.setExpand("members(),relationships()")
			.build(branchPath.getPath())
			.execute(getServiceForClass(IEventBus.class))
			.getSync();

		// XXX use both the stated and inferred parent/ancestor IDs to get all possible/applicable MRCM rules
		final ImmutableSet.Builder<Long> longAncestorIds = ImmutableSet.builder();
		
		if (concept.getParentIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getParentIds()));
		}
		if (concept.getAncestorIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getAncestorIds()));
		}
		if (concept.getStatedParentIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getStatedParentIds()));
		}
		if (concept.getStatedAncestorIds() != null) {
			longAncestorIds.addAll(Longs.asList(concept.getStatedAncestorIds()));
		}
		
		final ImmutableSet<String> stringAncestorIds = FluentIterable.from(longAncestorIds.build())
			.transform(Functions.toStringFunction())
			.toSet();
		
		addDescendantPredicatesForAncestors(stringAncestorIds, predicates, newPredicates);
		
		final ImmutableSet.Builder<String> containerRefSetIds = ImmutableSet.builder();
		
		for (SnomedReferenceSetMember referencingMember : concept.getMembers()) {
			containerRefSetIds.add(referencingMember.getReferenceSetId());
		}
		
		if (null != ruleRefSetId) {
			containerRefSetIds.add(ruleRefSetId);
		}
		
		addRefSetPredicates(containerRefSetIds.build(), predicates, newPredicates);
		
		List<ISnomedRelationship> inferredNonIsARelationships = FluentIterable.from(concept.getRelationships())
			.filter(new Predicate<ISnomedRelationship>() { @Override public boolean apply(ISnomedRelationship input) {
				return input.isActive()
						&& CharacteristicType.INFERRED_RELATIONSHIP.equals(input.getCharacteristicType())
						&& !Concepts.IS_A.equals(input.getTypeId());
			}})
			.toList();
		
		addRelationshipPredicates(inferredNonIsARelationships, predicates, newPredicates);

		return newPredicates;
	}

	@Override
	public Collection<PredicateIndexEntry> getPredicates(final IBranchPath branchPath, final Iterable<String> ruleParentIds, final @Nullable String ruleRefSetId) {
		checkNotNull(ruleParentIds, "Parent IDs iterable must not be null.");
		checkArgument(!Iterables.isEmpty(ruleParentIds), "Parent IDs iterable must not be empty.");
		
		final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates = getServiceForClass(ISnomedComponentService.class).getPredicates(branchPath);
		final Set<String> uniqueRuleParentIds = ImmutableSet.copyOf(ruleParentIds);
		
		final ImmutableList.Builder<String> ruleParentAndAncestorIds = ImmutableList.builder();
		final SnomedConcepts ruleConcepts = SnomedRequests.prepareSearchConcept()
			.setComponentIds(uniqueRuleParentIds)
			.setLimit(uniqueRuleParentIds.size())
			.build(branchPath.getPath())
			.execute(getServiceForClass(IEventBus.class))
			.getSync();
		
		// FIXME: Do we need the stated IDs as above? 
		final ImmutableSet.Builder<Long> longAncestorIds = ImmutableSet.builder();
		
		for (ISnomedConcept ruleConcept : ruleConcepts) {
			if (ruleConcept.getParentIds() != null) {
				longAncestorIds.addAll(Longs.asList(ruleConcept.getParentIds()));
			}
			if (ruleConcept.getAncestorIds() != null) {
				longAncestorIds.addAll(Longs.asList(ruleConcept.getAncestorIds()));
			}
//			if (parentConcept.getStatedParentIds() != null) {
//				longAncestorIds.addAll(Longs.asList(parentConcept.getStatedParentIds()));
//			}
//			if (parentConcept.getStatedAncestorIds() != null) {
//				longAncestorIds.addAll(Longs.asList(parentConcept.getStatedAncestorIds()));
//			}
		}

		ruleParentAndAncestorIds.addAll(uniqueRuleParentIds);
		
		final ImmutableSet<String> stringAncestorIds = FluentIterable.from(longAncestorIds.build())
				.transform(Functions.toStringFunction())
				.toSet();
		
		ruleParentAndAncestorIds.addAll(stringAncestorIds);
		
		final Set<PredicateIndexEntry> newPredicates = newHashSet();
		addDescendantPredicatesForAncestors(ruleParentAndAncestorIds.build(), predicates, newPredicates);
		
		if (null != ruleRefSetId) {
			final Collection<String> containerRefSetIds = ImmutableList.of(ruleRefSetId);
			addRefSetPredicates(containerRefSetIds, predicates, newPredicates);	
		}
		
		return newPredicates;
	}
	
	private void addPredicatesForFocus(final String conceptId,
			final HierarchyInclusionType inclusionType,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final Set<PredicateIndexEntry> newPredicates) {

		newPredicates.addAll(predicates.get(inclusionType).get(conceptId));
	}

	private void addDescendantPredicatesForAncestors(final Iterable<String> ancestorIds,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final Set<PredicateIndexEntry> newPredicates) {

		for (final String ancestorId : ancestorIds) {
			addPredicatesForFocus(ancestorId, HierarchyInclusionType.SELF_OR_DESCENDANT, predicates, newPredicates);
			addPredicatesForFocus(ancestorId, HierarchyInclusionType.DESCENDANT, predicates, newPredicates);
		}
	}

	private void addRefSetPredicates(final Iterable<String> containerRefSetIds,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final Set<PredicateIndexEntry> newPredicates) {
		
		for (final String containerRefSetId : containerRefSetIds) {
			// FIXME: We do not distinguish between a concept rule on the refset identifier concept, and a refset rule
			addPredicatesForFocus(containerRefSetId, HierarchyInclusionType.SELF, predicates, newPredicates);	
		}
	}

	private void addRelationshipPredicates(final Iterable<ISnomedRelationship> inferredNonIsARelationships,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final Set<PredicateIndexEntry> newPredicates) {
	
		for (final ISnomedRelationship relationship : inferredNonIsARelationships) {
			final String key = relationship.getTypeId() + PredicateUtils.PREDICATE_SEPARATOR + relationship.getDestinationId();
			addPredicatesForFocus(key, HierarchyInclusionType.SELF, predicates, newPredicates);
		}
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IPredicateBrowser#getDataTypePredicateLabel(com.b2international.snowowl.core.api.IBranchPath, java.lang.String)
	 */
	@Override
	public String getDataTypePredicateLabel(final IBranchPath branchPath, final String dataTypeName) {
		return DataTypeUtils.getDefaultDataTypeLabel(dataTypeName);
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.browser.IPredicateBrowser#getDataTypePredicateLabel(com.b2international.snowowl.core.api.IBranchPath, java.lang.String, java.lang.String)
	 */
	@Override
	public String getDataTypePredicateLabel(final IBranchPath branchPath, final String dataTypeName, final String conceptId) {
		return DataTypeUtils.getDataTypePredicateLabel(dataTypeName, getPredicates(branchPath, conceptId, null));
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser#getRefSetPredicateKeys(java.lang.String)
	 */
	@Override
	public Collection<String> getRefSetPredicateKeys(final IBranchPath branchPath, final String identifierId) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(identifierId, "Reference set identifier concept ID argument cannot be null.");
		
		final TopDocs topDocs = service.search(branchPath, SnomedMappings.newQuery().refSet().id(identifierId).matchAll(), 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptySet();
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, REFERRING_PREDICATE_FIELDS);
		return SnomedMappings.componentReferringPredicate().getValues(doc);
	}
	
	@Override
	public Set<String> getPredicateKeys(final IBranchPath branchPath, final String conceptId) {
		final DocIdCollector docCollector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, SnomedMappings.newQuery().concept().id(conceptId).matchAll(), docCollector);
		
		try {
			final DocIdsIterator docIdsIterator = docCollector.getDocIDs().iterator();
			if (docIdsIterator.next()) {
				final int docID = docIdsIterator.getDocID();
				final Document doc = service.document(branchPath, docID, SnomedMappings.fieldsToLoad().componentReferringPredicate().build());
				return ImmutableSet.copyOf(SnomedMappings.componentReferringPredicate().getValues(doc));
			}		
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying predicate keys.", e);
		}
		
		return ImmutableSet.of();
	}

	@Override
	protected Set<String> getFieldNamesToLoad() {
		return PREDICATE_FIELDS;
	}

	@Override
	protected PredicateIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		final long storageKey = Mappings.storageKey().getValue(doc);
		final String predicateTypeString = SnomedMappings.predicateType().getValue(doc);
		final PredicateType predicateType = PredicateType.valueOf(predicateTypeString);
		final String queryExpression = SnomedMappings.predicateQueryExpression().getValue(doc);
		final boolean required = BooleanUtils.valueOf(SnomedMappings.predicateRequired().getValue(doc));
		final boolean multiple = BooleanUtils.valueOf(SnomedMappings.predicateMultiple().getValue(doc));
		final byte flags = PredicateIndexEntry.createFlags(required, multiple);
		switch (predicateType) {
		case DATATYPE:
			final DataType dataType = DataType.valueOf(SnomedMappings.predicateDataType().getValue(doc));
			final String dataTypeName = SnomedMappings.predicateDataTypeName().getValue(doc);
			final String dataTypeLabel = SnomedMappings.predicateDataTypeLabel().getValue(doc);
			return PredicateIndexEntry.createDataTypeTypePredicate(storageKey, queryExpression, dataType, dataTypeName, dataTypeLabel, flags);
		case DESCRIPTION:
			final Long descriptionTypeId = SnomedMappings.predicateDescriptionTypeId().getValue(doc);
			return PredicateIndexEntry.createDescriptionTypePredicate(storageKey, queryExpression, descriptionTypeId, flags);
		case RELATIONSHIP:
			final String relationshipTypeExpression = SnomedMappings.predicateRelationshipTypeExpression().getValue(doc);
			final String relationshipValueExpression = SnomedMappings.predicateRelationshipValueExpression().getValue(doc);
			final String characteristicTypeExpression = SnomedMappings.predicateCharacteristicTypeExpression().getValue(doc);
			final GroupRule groupRule = GroupRule.valueOf(SnomedMappings.predicateGroupRule().getValue(doc));
			// TODO: is it OK not to specify the required services (see other factory method)?
			return PredicateIndexEntry.createRelationshipTypePredicate(storageKey, queryExpression, relationshipTypeExpression, 
					relationshipValueExpression, characteristicTypeExpression, groupRule, flags);
		default:
			throw new IllegalArgumentException("Unexpected predicate type: " + predicateType);
		}
	}
}