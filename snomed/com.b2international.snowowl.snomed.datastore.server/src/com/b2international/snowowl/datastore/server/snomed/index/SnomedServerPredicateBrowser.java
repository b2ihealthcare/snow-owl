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
package com.b2international.snowowl.datastore.server.snomed.index;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.index.IndexUtils.intToPrefixCoded;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.index.AbstractIndexBrowser;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.DataTypeUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedTaxonomyService;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Lucene based predicate browser implementation.
 * 
 */
public class SnomedServerPredicateBrowser extends AbstractIndexBrowser<PredicateIndexEntry> implements SnomedPredicateBrowser {

	private static final Query PREDICATE_TYPE_QUERY = new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, intToPrefixCoded(PredicateUtils.PREDICATE_TYPE_ID)));

	private static final Set<String> PREDICATE_FIELDS = ImmutableSet.of(SnomedIndexBrowserConstants.PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION,
			SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_LABEL,	SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_NAME,
			SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_TYPE, SnomedIndexBrowserConstants.PREDICATE_DESCRIPTION_TYPE_ID,
			SnomedIndexBrowserConstants.PREDICATE_GROUP_RULE, SnomedIndexBrowserConstants.PREDICATE_MULTIPLE,
			SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_TYPE_EXPRESSION,
			SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_VALUE_EXPRESSION, SnomedIndexBrowserConstants.PREDICATE_REQUIRED,
			SnomedIndexBrowserConstants.PREDICATE_TYPE, SnomedIndexBrowserConstants.PREDICATE_UUID);
	

	private static final Set<String> REFERRING_PREDICATE_FIELDS = ImmutableSet.of(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE);

	public SnomedServerPredicateBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}

	@Override
	public Collection<PredicateIndexEntry> getAllPredicates(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, PREDICATE_TYPE_QUERY, collector);
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			return createResultObjects(branchPath, itr);
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Error while loading all MRCM predicates on '" + branchPath + "' branch.");
		}
	}
	
	@Override
	public Collection<PredicateIndexEntry> getPredicate(final IBranchPath branchPath, final String... uuids) {
		final BooleanQuery query = new BooleanQuery(true) ;
		for (final String uuid : uuids) {
			final TermQuery termQuery = new TermQuery(new Term(SnomedIndexBrowserConstants.PREDICATE_UUID, uuid));
			query.add(termQuery, Occur.SHOULD);
		}
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error whey retrieving predicates by UUIDs: " + uuids, e);
		}
	}

	@Override
	public Collection<PredicateIndexEntry> getPredicates(final IBranchPath branchPath, final String conceptId, final @Nullable String ruleRefSetId) {
		checkNotNull(conceptId, "Concept ID must not be null.");

		final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates = getServiceForClass(ISnomedComponentService.class).getPredicates(branchPath);
		final HashSet<PredicateIndexEntry> newPredicates = newHashSet();

		addPredicatesForFocus(conceptId, HierarchyInclusionType.SELF, predicates, newPredicates);
		addPredicatesForFocus(conceptId, HierarchyInclusionType.SELF_OR_DESCENDANT, predicates, newPredicates);
		
		final Collection<String> ancestorIds = getServiceForClass(SnomedTaxonomyService.class).getAllSupertypes(branchPath, conceptId);
		addDescendantPredicatesForAncestors(ancestorIds, predicates, newPredicates);
		
		final Collection<String> containerRefSetIds = newHashSet(); 
		containerRefSetIds.addAll(getServiceForClass(SnomedTaxonomyService.class).getContainerRefSetIds(branchPath, conceptId));
		if (null != ruleRefSetId) {
			containerRefSetIds.add(ruleRefSetId);
		}
		
		addRefSetPredicates(containerRefSetIds, predicates, newPredicates);
		addRelationshipPredicates(branchPath, conceptId, predicates, newPredicates);
		
		return newPredicates;
	}

	@Override
	public Collection<PredicateIndexEntry> getPredicates(final IBranchPath branchPath, final Iterable<String> ruleParentIds, final @Nullable String ruleRefSetId) {
		checkNotNull(ruleParentIds, "Parent IDs iterable must not be null.");
		checkArgument(!Iterables.isEmpty(ruleParentIds), "Parent IDs iterable must not be empty.");
		
		final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates = getServiceForClass(ISnomedComponentService.class).getPredicates(branchPath);
		final HashSet<PredicateIndexEntry> newPredicates = newHashSet();
		
		for (final String ruleParentId : ruleParentIds) {
			// XXX: for a direct child of ruleParentId, ruleParentId itself should be treated as an ancestor, so include it 
			final ImmutableList.Builder<String> ancestorIds = ImmutableList.builder();
			ancestorIds.add(ruleParentId);
			ancestorIds.addAll(getServiceForClass(SnomedTaxonomyService.class).getAllSupertypes(branchPath, ruleParentId));
			
			addDescendantPredicatesForAncestors(ancestorIds.build(), predicates, newPredicates);
		}
		
		if (null != ruleRefSetId) {
			final Collection<String> containerRefSetIds = ImmutableList.of(ruleRefSetId);
			addRefSetPredicates(containerRefSetIds, predicates, newPredicates);	
		}
		
		return newPredicates;
	}
	
	private void addPredicatesForFocus(final String conceptId,
			final HierarchyInclusionType inclusionType,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final HashSet<PredicateIndexEntry> newPredicates) {

		newPredicates.addAll(predicates.get(inclusionType).get(conceptId));
	}

	private void addDescendantPredicatesForAncestors(final Iterable<String> ancestorIds,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final HashSet<PredicateIndexEntry> newPredicates) {

		for (final String ancestorId : ancestorIds) {
			addPredicatesForFocus(ancestorId, HierarchyInclusionType.SELF_OR_DESCENDANT, predicates, newPredicates);
			addPredicatesForFocus(ancestorId, HierarchyInclusionType.DESCENDANT, predicates, newPredicates);
		}
	}

	private void addRefSetPredicates(final Collection<String> containerRefSetIds,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final HashSet<PredicateIndexEntry> newPredicates) {
		
		for (final String containerRefSetId : containerRefSetIds) {
			// FIXME: We do not distinguish between a concept rule on the refset identifier concept, and a refset rule
			addPredicatesForFocus(containerRefSetId, HierarchyInclusionType.SELF, predicates, newPredicates);	
		}
	}

	private void addRelationshipPredicates(final IBranchPath branchPath, final String conceptId,
			final Map<HierarchyInclusionType, Multimap<String, PredicateIndexEntry>> predicates,
			final HashSet<PredicateIndexEntry> newPredicates) {
	
		for (final String typeId : getServiceForClass(SnomedTaxonomyService.class).getOutboundRelationshipTypes(branchPath, conceptId)) {
			for (final String outboundId: getServiceForClass(SnomedTaxonomyService.class).getOutboundConcepts(branchPath, conceptId, typeId)) {
				newPredicates.addAll(predicates.get(HierarchyInclusionType.SELF).get(typeId + "#" + outboundId));	
			}
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
		
		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.REFSET_NUMBER))), Occur.MUST);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(identifierId))), Occur.MUST);
		
		final TopDocs topDocs = service.search(branchPath, query, 1);
		
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptySet();
		}
		
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, REFERRING_PREDICATE_FIELDS);
		
		final IndexableField[] fields = doc.getFields(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE);
		
		final String[] $ = new String[fields.length];
		
		for (int i = 0; i < fields.length; i++) {
			
			$[i] = fields[i].stringValue();
			
		}
		
		return Arrays.asList($);
	}
	
	@Override
	public Set<String> getPredicateKeys(final IBranchPath branchPath, final String conceptId) {
		
		final BooleanQuery query = new BooleanQuery();
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_TYPE, IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER))), Occur.MUST);
		query.add(new TermQuery(new Term(CommonIndexConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(conceptId))), Occur.MUST);
		
		final DocIdCollector docCollector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, docCollector);
		
		try {
			final DocIdsIterator docIdsIterator = docCollector.getDocIDs().iterator();
			if (docIdsIterator.next()) {
				final int docID = docIdsIterator.getDocID();
				final Document doc = service.document(branchPath, docID, ImmutableSet.of(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE));
				return ImmutableSet.copyOf(doc.getValues(SnomedIndexBrowserConstants.COMPONENT_REFERRING_PREDICATE));
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
		final String predicateTypeString = doc.get(SnomedIndexBrowserConstants.PREDICATE_TYPE);
		final PredicateType predicateType = PredicateType.valueOf(predicateTypeString);
		final String uuid = doc.get(SnomedIndexBrowserConstants.PREDICATE_UUID);
		final String queryExpression = doc.get(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION);
		final boolean required = IndexUtils.getBooleanValue(doc.getField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED));
		final boolean multiple = IndexUtils.getBooleanValue(doc.getField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE));
		final byte flags = PredicateIndexEntry.createFlags(required, multiple);
		switch (predicateType) {
		case DATATYPE:
			final DataType dataType = DataType.valueOf(doc.get(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_TYPE));
			final String dataTypeName = doc.get(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_NAME);
			final String dataTypeLabel = doc.get(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_LABEL);
			return PredicateIndexEntry.createDataTypeTypePredicate(uuid, queryExpression, dataType, dataTypeName, dataTypeLabel, flags);
		case DESCRIPTION:
			final String descriptionTypeId = doc.get(SnomedIndexBrowserConstants.PREDICATE_DESCRIPTION_TYPE_ID);
			return PredicateIndexEntry.createDescriptionTypePredicate(uuid, queryExpression, Long.valueOf(descriptionTypeId), flags);
		case RELATIONSHIP:
			final String relationshipTypeExpression = doc.get(SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_TYPE_EXPRESSION);
			final String relationshipValueExpression = doc.get(SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_VALUE_EXPRESSION);
			final String characteristicTypeExpression = doc.get(SnomedIndexBrowserConstants.PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION);
			final GroupRule groupRule = GroupRule.valueOf(doc.get(SnomedIndexBrowserConstants.PREDICATE_GROUP_RULE));
			// TODO: is it OK not to specify the required services (see other factory method)?
			return PredicateIndexEntry.createRelationshipTypePredicate(uuid, queryExpression, relationshipTypeExpression, 
					relationshipValueExpression, characteristicTypeExpression, groupRule, flags);
		default:
			throw new IllegalArgumentException("Unexpected predicate type: " + predicateType);
		}
	}
}