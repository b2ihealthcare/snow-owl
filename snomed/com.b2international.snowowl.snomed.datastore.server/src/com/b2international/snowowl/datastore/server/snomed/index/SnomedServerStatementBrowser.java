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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongMapIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongSet;

/**
 * Index based statement browser implementation.
 */
public class SnomedServerStatementBrowser extends AbstractSnomedIndexBrowser<SnomedRelationshipIndexEntry> implements SnomedStatementBrowser {

	private static final Set<String> FIELD_NAMES_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.active()
			.module()
			.storageKey()
			.relationshipType()
			.relationshipCharacteristicType()
			.effectiveTime()
			.field(RELATIONSHIP_OBJECT_ID)
			.field(RELATIONSHIP_VALUE_ID)
			.field(RELATIONSHIP_GROUP)
			.field(RELATIONSHIP_UNION_GROUP)
			.field(COMPONENT_RELEASED)
			.field(RELATIONSHIP_INFERRED)
			.field(RELATIONSHIP_UNIVERSAL)
			.field(RELATIONSHIP_DESTINATION_NEGATED).build();

	/**
	 * Class constructor.
	 * @param indexService index service for SNOMED&nbsp;CT ontology.
	 */
	public SnomedServerStatementBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}

	@Override
	protected SnomedRelationshipIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		
		final String id = SnomedMappings.id().getValueAsString(doc);
		final String objectId = doc.get(RELATIONSHIP_OBJECT_ID);
		final String attributeId = SnomedMappings.relationshipType().getValueAsString(doc);
		final String valueId = doc.get(RELATIONSHIP_VALUE_ID);
		final String characteristicTypeId = SnomedMappings.relationshipCharacteristicType().getValueAsString(doc);
		byte group = (byte) doc.getField(RELATIONSHIP_GROUP).numericValue().intValue();
		byte unionGroup = (byte) doc.getField(RELATIONSHIP_UNION_GROUP).numericValue().intValue();
		final boolean active = BooleanUtils.valueOf(SnomedMappings.active().getValue(doc));
		final boolean released = BooleanUtils.valueOf(SnomedMappings.released().getValue(doc)); 
		final boolean universal = BooleanUtils.valueOf(Mappings.intField(RELATIONSHIP_UNIVERSAL).getValue(doc)); 
		final boolean destinationNegated = BooleanUtils.valueOf(Mappings.intField(RELATIONSHIP_DESTINATION_NEGATED).getValue(doc)); 
		final String moduleId = SnomedMappings.module().getValueAsString(doc);
		final long storageKey = Mappings.storageKey().getValue(doc);
		final long effectiveTime = SnomedMappings.effectiveTime().getValue(doc);
		
		return SnomedRelationshipIndexEntry.builder()
				.id(id)
				.sourceId(objectId)
				.typeId(attributeId)
				.destinationId(valueId)
				.characteristicTypeId(characteristicTypeId)
				.group(group)
				.unionGroup(unionGroup)
				.active(active)
				.released(released)
				.modifierId(universal ? Concepts.UNIVERSAL_RESTRICTION_MODIFIER : Concepts.EXISTENTIAL_RESTRICTION_MODIFIER)
				.destinationNegated(destinationNegated)
				.moduleId(moduleId)
				.storageKey(storageKey)
				.effectiveTimeLong(effectiveTime)
				.build();
	}

	@Override
	protected Set<String> getFieldNamesToLoad() {
		return FIELD_NAMES_TO_LOAD;
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getInboundStatements(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getInboundStatementsById(branchPath, concept.getId());
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundStatements(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getOutboundStatementsById(branchPath, concept.getId());
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getStatements(final IBranchPath branchPath, final SnomedConceptIndexEntry concept) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(concept, "Concept must not be null.");
		return getStatementsById(branchPath, concept.getId());
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getAllStatements(final IBranchPath branchPath) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		try {
			service.search(branchPath,
					SnomedMappings.newQuery().relationship().matchAll(),
					collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying all statements.", e);
		}
	}

	@Override
	public SnomedRelationshipIndexEntry getStatement(final IBranchPath branchPath, final String id) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(id, "SNOMED CT relationship ID cannot be null.");
		return getConcept(branchPath, SnomedMappings.newQuery().relationship().id(id).matchAll());
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getStatementsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, getMatchRelationshipTypeOrValueQuery(conceptId), collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying inbound statements of concept. ID: " + conceptId, e);
		}
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getInboundStatementsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		try {
			final Query query = SnomedMappings.newQuery().field(RELATIONSHIP_VALUE_ID, Long.valueOf(conceptId)).matchAll();
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying inbound statements of concept. ID: " + conceptId, e);
		}
	}

	@Override
	public Collection<SnomedRelationshipIndexEntry> getInboundStatementsById(final IBranchPath branchPath, final long conceptId, final long typeId) {
		checkNotNull(branchPath, "branchPath");
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final Query query = SnomedMappings.newQuery().relationshipType(typeId).field(RELATIONSHIP_VALUE_ID, conceptId).matchAll();
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying inbound statements of concept. ID: " + conceptId + " with type ID: " + typeId + ".", e);
		}
	}
	
	@Override
	public List<SnomedRelationshipIndexEntry> getOutboundStatementsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final Query query = SnomedMappings.newQuery().field(RELATIONSHIP_OBJECT_ID, Long.valueOf(conceptId)).matchAll();
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying outbound statements of concept. ID: " + conceptId, e);
		}
	}
	
	@Override
	public int getHighestGroup(final IBranchPath branchPath, final long conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;

		try {
			int maxGroup = 0;

			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));

			service.search(branchPath, queryActiveRelationshipsWhereObjectId(conceptId), collector);

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			searcher = manager.acquire();

			final IndexField<Integer> groupField = Mappings.intField(RELATIONSHIP_GROUP);
			while (itr.next()) {
				final Document doc = service.document(searcher, itr.getDocID(), Mappings.fieldsToLoad().field(groupField).build());
				final int group = groupField.getValue(doc);
				if (group > maxGroup) {
					maxGroup = group;
				}
			}

			return maxGroup;
		} catch (final IOException e) {
			throw new IndexException("Error while getting highest relationship group for concept: " + conceptId, e);
		} finally {
			if (searcher != null) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}

	@Override
	public int getHighestUnionGroup(final IBranchPath branchPath, final long conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			int maxUnionGroup = 0;
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, queryActiveRelationshipsWhereObjectId(conceptId), collector);

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			searcher = manager.acquire();
			final IndexField<Integer> unionGroupField = Mappings.intField(RELATIONSHIP_UNION_GROUP);
			while (itr.next()) {
				final Document doc = service.document(searcher, itr.getDocID(), Mappings.fieldsToLoad().field(unionGroupField).build());
				final int unionGroup = unionGroupField.getValue(doc);
				if (unionGroup > maxUnionGroup) {
					maxUnionGroup = unionGroup;
				}
			}
			return maxUnionGroup;
		} catch (final IOException e) {
			throw new IndexException("Error while getting highest relationship union group for concept: " + conceptId, e);
		} finally {
			if (searcher != null) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}

	@Override
	public long[] getStatementStorageKeysForGroup(final IBranchPath branchPath, final long conceptId, final int group) {
		return getStatementStorageKeysForGroupField(branchPath, conceptId, group, RELATIONSHIP_GROUP);
	}

	@Override
	public long[] getStatementStorageKeysForUnionGroup(final IBranchPath branchPath, final long conceptId, final int unionGroup) {
		return getStatementStorageKeysForGroupField(branchPath, conceptId, unionGroup, RELATIONSHIP_UNION_GROUP);
	}

	private long[] getStatementStorageKeysForGroupField(final IBranchPath branchPath, final long conceptId, final int value, final String groupFieldName) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		final IndexField<Integer> groupField = Mappings.intField(groupFieldName);
		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		IndexSearcher searcher = null;

		try {
			// cannot search for group, as it is not indexed
			searcher = manager.acquire();
			final IndexReader reader = searcher.getIndexReader();

			final int size = reader.maxDoc();
			final DocIdCollector collector = DocIdCollector.create(size);
			service.search(branchPath, queryActiveRelationshipsWhereObjectId(conceptId), collector);

			final DocIdsIterator itr = collector.getDocIDs().iterator();

			final long[] statementStorageKeys = new long[size];

			int i = 0;
			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), SnomedMappings.fieldsToLoad().storageKey().field(groupField).build());
				final int groupValue = groupField.getValue(doc);
				if (value == groupValue) {
					statementStorageKeys[i++] = Mappings.storageKey().getValue(doc);
				}
			}

			return Arrays.copyOf(statementStorageKeys, i);

		} catch (final IOException e) {
			throw new IndexException(MessageFormat.format(
					"Error while getting active statement storage keys for concept: {0} for value: {1}, field: {2}",
					conceptId, value, groupField.fieldName()), e);
		} finally {
			if (searcher != null) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends IsAStatement> T[] getActiveStatements(final IBranchPath branchPath, final StatementCollectionMode mode) {

		checkNotNull(branchPath, "Branch path argument cannot be null.");

		final ReferenceManager<IndexSearcher> manager = service.getManager(branchPath);
		final IndexSearcher searcher = null;

		try {

			final int hitCount = service.getTotalHitCount(branchPath, mode.getQuery());
			final StatementCollector collector = new StatementCollector(hitCount, mode);
			
			service.search(branchPath, mode.getQuery(), collector);

			return (T[]) collector.getStatements();

		} finally {
			if (null != searcher) {
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
				}
			}
		}
	}

	@Override
	public LongKeyMap getAllActiveStatements(final IBranchPath branchPath) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		final Query query = SnomedMappings.newQuery().relationship().active().matchAll();
		final int hitCount = service.getHitCount(branchPath, query, null);
		final StatementFragmentCollector collector = new StatementFragmentCollector(hitCount);
		service.search(branchPath, query, collector);
		return collector.getStatementMap();
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String relationshipId) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Concept ID argument cannot be null.");

		final TopDocs topDocs = service.search(branchPath, SnomedMappings.newQuery().relationship().id(relationshipId).matchAll(), 1);

		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1L;
		}

		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, SnomedMappings.fieldsToLoad().storageKey().build());
		return Mappings.storageKey().getValue(doc);
	}

	@Override
	public long getSourceIdForStatementStorageKey(final IBranchPath branchPath, final long statementStorageKey) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		final Query query = SnomedMappings.newQuery().relationship().active().storageKey(statementStorageKey).matchAll();
		final StatementSourceIdCollector collector = new StatementSourceIdCollector(statementStorageKey);
		service.search(branchPath, query, collector);
		return collector.getSourceId();
	}

	@Override
	public Collection<SnomedRelationshipIndexEntry> getActiveInboundStatementsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final Query query = SnomedMappings.newQuery().active().field(RELATIONSHIP_VALUE_ID, Long.valueOf(conceptId)).matchAll();
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying active outbound statements of concept. ID: " + conceptId, e);
		}
	}

	@Override
	public Collection<SnomedRelationshipIndexEntry> getActiveOutboundStatementsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final Query query = SnomedMappings.newQuery().active().field(RELATIONSHIP_OBJECT_ID, Long.valueOf(conceptId)).matchAll();
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying active outbound statements of concept. ID: " + conceptId, e);
		}
	}

	@Override
	public Collection<SnomedRelationshipIndexEntry> getActiveOutboundStatementsById(final IBranchPath branchPath, final String conceptId, final String relationshipTypeId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		checkNotNull(conceptId, "Relationship type ID cannot be null.");
		
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final Query query = SnomedMappings.newQuery().active().relationshipType(relationshipTypeId).field(RELATIONSHIP_OBJECT_ID, Long.valueOf(conceptId)).matchAll();
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying active outbound statements with type for concept. ID: " + conceptId, e);
		}
	}
	
	@Override
	public Map<String, String> getAllStatementLabelsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");

		final BooleanQuery query = new BooleanQuery(true);
		query.add(getMatchRelationshipTypeOrValueQuery(conceptId), Occur.MUST);
		
		final StatementIdCollector collector = new StatementIdCollector();
		service.search(branchPath, query, collector);

		final LongSet idsSet = collector.getIds();
		final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idsSet.size());

		if (idsSet.size() > 1000) {
			final SnomedComponentLabelCollector labelCollector = new SnomedComponentLabelCollector(idsSet);
			service.search(branchPath, SnomedMappings.newQuery().concept().matchAll(), labelCollector);
			final LongKeyMap idLabelMapping = labelCollector.getIdLabelMapping();

			for (final LongKeyMapIterator itr = idLabelMapping.entries(); itr.hasNext(); /**/) {
				itr.next();
				$.put(
						Long.toString(itr.getKey()), //ID
						String.valueOf(itr.getValue())); //label
			}
		} else {
			final String[] ids = LongSets.toStringArray(collector.getIds());
			final String[] labels = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getLabels(branchPath, ids);

			for (int i = 0; i < ids.length; i++) {
				$.put(ids[i], labels[i]);
			}
		}

		return $;
	}

	@Override
	public Map<String, String> getAllStatementImageIdsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");

		final BooleanQuery query = new BooleanQuery(true);
		query.add(getMatchRelationshipTypeOrValueQuery(conceptId), Occur.MUST);
		
		final StatementIdCollector collector = new StatementIdCollector();
		service.search(branchPath, query, collector);

		final LongSet idsSet = collector.getIds();
		final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idsSet.size());

		if (idsSet.size() > 1000) {
			final SnomedConceptIconIdCollector labelCollector = new SnomedConceptIconIdCollector(idsSet);
			service.search(branchPath, SnomedMappings.newQuery().concept().matchAll(), labelCollector);
			return labelCollector.getIdIconIdStringMapping();
		} else {
			final String[] ids = LongSets.toStringArray(collector.getIds());
			final String[] iconIds = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getIconId(branchPath, ids);

			for (int i = 0; i < ids.length; i++) {
				$.put(ids[i], iconIds[i]);
			}
			return $;
		}
	}

	private Query getMatchRelationshipTypeOrValueQuery(final String conceptId) {
		final Long longConceptId = Long.valueOf(conceptId);
		return SnomedMappings.newQuery()
				.field(RELATIONSHIP_VALUE_ID, longConceptId)
				.field(RELATIONSHIP_OBJECT_ID, longConceptId)
				.matchAny();
	}

	@Override
	public Map<String, String> getAllDestinationLabels(final IBranchPath branchPath, final Collection<String> sourceIds,
			final String typeId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(sourceIds, "sourceIds");
		checkNotNull(typeId, "typeId");
		final StatementDestinationIdCollector collector = new StatementDestinationIdCollector();

		final Query query = SnomedMappings.newQuery().relationshipType(typeId).matchAll();
		service.search(branchPath, query, collector);

		final LongKeyLongMap idsSet = collector.getIds();
		final LongKeyLongMapIterator iter = idsSet.entries();
		while (iter.hasNext()) {
			iter.next();
			if (!sourceIds.contains(String.valueOf(iter.getKey()))) {
				iter.remove();
			}
		}
		final Map<String, String> result = Maps.newHashMapWithExpectedSize(idsSet.size());
		final String[] ids = LongSets.toStringArray(idsSet.keySet());
		final String[] values = LongSets.toStringArray(idsSet.values());
		final String[] labels = ApplicationContext.getInstance().getService(ISnomedComponentService.class).getLabels(branchPath, values);
		for (int i = 0; i < ids.length; i++) {
			result.put(ids[i], labels[i]);
		}
		return result;
	}
	
	private Query queryActiveRelationshipsWhereObjectId(long conceptId) {
		return SnomedMappings.newQuery().active().relationship().field(RELATIONSHIP_OBJECT_ID, conceptId).matchAll();		
	}
	
}