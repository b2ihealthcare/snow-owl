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

import static com.b2international.snowowl.datastore.index.IndexUtils.longToPrefixCoded;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_RELEASED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_ATTRIBUTE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_DESTINATION_NEGATED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_INFERRED;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNION_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.lucene.search.BooleanClause.Occur.MUST;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongMapIterator;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongSet;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.index.field.ComponentIdLongField;
import com.b2international.snowowl.datastore.index.field.ComponentStorageKeyField;
import com.b2international.snowowl.datastore.index.query.IndexQueries;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexQueries;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Index based statement browser implementation.
 */
public class SnomedServerStatementBrowser extends AbstractSnomedIndexBrowser<SnomedRelationshipIndexEntry> implements SnomedStatementBrowser {

	private static final ImmutableSet<String> FIELD_NAMES_TO_LOAD = ImmutableSet.of(ComponentIdLongField.COMPONENT_ID, RELATIONSHIP_OBJECT_ID,
			RELATIONSHIP_ATTRIBUTE_ID, RELATIONSHIP_VALUE_ID, RELATIONSHIP_GROUP,
			RELATIONSHIP_UNION_GROUP, COMPONENT_RELEASED, COMPONENT_ACTIVE,
			RELATIONSHIP_INFERRED, RELATIONSHIP_UNIVERSAL, RELATIONSHIP_DESTINATION_NEGATED,
			SnomedIndexQueries.RELATIONSHIP_CHARACTERISTIC_TYPE_ID, COMPONENT_MODULE_ID, ComponentStorageKeyField.COMPONENT_STORAGE_KEY,
			RELATIONSHIP_EFFECTIVE_TIME);

	private static final Set<String> GROUP_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(RELATIONSHIP_GROUP));
	private static final Set<String> STORAGE_KEY_GROUP_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(ComponentStorageKeyField.COMPONENT_STORAGE_KEY,
			RELATIONSHIP_GROUP,
			RELATIONSHIP_UNION_GROUP));
	private static final Set<String> UNION_GROUP_FIELD_TO_LOAD = Collections.unmodifiableSet(Sets.newHashSet(RELATIONSHIP_UNION_GROUP));

	/**
	 * Class constructor.
	 * @param indexService index service for SNOMED&nbsp;CT ontology.
	 */
	public SnomedServerStatementBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}

	@Override
	protected SnomedRelationshipIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		final String id = ComponentIdLongField.getString(doc);
		final String objectId = doc.get(RELATIONSHIP_OBJECT_ID);
		final String attributeId = doc.get(RELATIONSHIP_ATTRIBUTE_ID);
		final String valueId = doc.get(RELATIONSHIP_VALUE_ID);
		final String characteristicTypeId = doc.get(SnomedIndexQueries.RELATIONSHIP_CHARACTERISTIC_TYPE_ID);
		final byte group = (byte) doc.getField(RELATIONSHIP_GROUP).numericValue().intValue();
		final byte unionGroup = (byte) doc.getField(RELATIONSHIP_UNION_GROUP).numericValue().intValue();
		final byte flags = SnomedRelationshipIndexEntry.generateFlags(IndexUtils.getBooleanValue(doc.getField(COMPONENT_RELEASED)),
				IndexUtils.getBooleanValue(doc.getField(COMPONENT_ACTIVE)),
				IndexUtils.getBooleanValue(doc.getField(RELATIONSHIP_INFERRED)),
				IndexUtils.getBooleanValue(doc.getField(RELATIONSHIP_UNIVERSAL)),
				IndexUtils.getBooleanValue(doc.getField(RELATIONSHIP_DESTINATION_NEGATED)));
		final String moduleId = doc.get(COMPONENT_MODULE_ID);
		final long storageKey = ComponentStorageKeyField.getLong(doc);
		// FIXME: remove null check
		final IndexableField effectiveTimeField = doc.getField(RELATIONSHIP_EFFECTIVE_TIME);
		final long effectiveTime = (null == effectiveTimeField) ? EffectiveTimes.UNSET_EFFECTIVE_TIME : IndexUtils.getLongValue(effectiveTimeField);
		return new SnomedRelationshipIndexEntry(id, objectId, attributeId, valueId, characteristicTypeId, storageKey, moduleId, group, unionGroup, flags, effectiveTime);
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
					SnomedIndexQueries.RELATIONSHIP_TYPE_QUERY,
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
		return getConcept(branchPath, new ComponentIdLongField(id).toQuery());
	}

	@Override
	public List<SnomedRelationshipIndexEntry> getStatementsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");
		try {
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			final TermQuery valueQuery = new TermQuery(new Term(RELATIONSHIP_VALUE_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId))));
			final TermQuery objectQuery = new TermQuery(new Term(RELATIONSHIP_OBJECT_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId))));
			final BooleanQuery query = new BooleanQuery();
			query.add(valueQuery, Occur.SHOULD);
			query.add(objectQuery, Occur.SHOULD);
			service.search(branchPath, query, collector);
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
			final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
			service.search(branchPath, new TermQuery(new Term(RELATIONSHIP_VALUE_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), collector);
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
			final BooleanQuery query = new BooleanQuery(true);
			query.add(new TermQuery(new Term(RELATIONSHIP_VALUE_ID, longToPrefixCoded(conceptId))), MUST);
			query.add(new TermQuery(new Term(RELATIONSHIP_ATTRIBUTE_ID, longToPrefixCoded(typeId))), MUST);
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
			service.search(branchPath, new TermQuery(new Term(RELATIONSHIP_OBJECT_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), collector);
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

			while (itr.next()) {

				final int group = IndexUtils.getIntValue(service.document(searcher, itr.getDocID(), GROUP_FIELD_TO_LOAD).getField(RELATIONSHIP_GROUP));
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
			while (itr.next()) {
				final int unionGroup = IndexUtils.getIntValue(service.document(searcher, itr.getDocID(), UNION_GROUP_FIELD_TO_LOAD).getField(RELATIONSHIP_UNION_GROUP));
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

	private long[] getStatementStorageKeysForGroupField(final IBranchPath branchPath, final long conceptId, final int value, final String groupField) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
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
				final Document doc = searcher.doc(itr.getDocID(), STORAGE_KEY_GROUP_FIELD_TO_LOAD);
				final int groupValue = IndexUtils.getIntValue(doc.getField(groupField));
				if (value == groupValue) {
					statementStorageKeys[i++] = ComponentStorageKeyField.getLong(doc);
				}
			}

			return Arrays.copyOf(statementStorageKeys, i);

		} catch (final IOException e) {
			throw new IndexException(MessageFormat.format(
					"Error while getting active statement storage keys for concept: {0} for value: {1}, field: {2}",
					conceptId, value, groupField), e);
		} finally {
			if (searcher != null)
				try {
					manager.release(searcher);
				} catch (final IOException e) {
					throw new IndexException(e);
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
		final int hitCount = service.getHitCount(branchPath, SnomedIndexQueries.ACTIVE_RELATIONSHIPS_QUERY, null);
		final StatementFragmentCollector collector = new StatementFragmentCollector(hitCount);
		service.search(branchPath, SnomedIndexQueries.ACTIVE_RELATIONSHIPS_QUERY, collector);
		return collector.getStatementMap();
	}

	@Override
	public long getStorageKey(final IBranchPath branchPath, final String relationshipId) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Concept ID argument cannot be null.");

		final TopDocs topDocs = service.search(branchPath, IndexQueries.queryComponentByLongId(SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER, relationshipId), 1);

		//cannot found matching label for component
		if (null == topDocs || CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return -1L;
		}

		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, ComponentStorageKeyField.FIELDS_TO_LOAD);
		return ComponentStorageKeyField.getLong(doc);
	}

	@Override
	public long getSourceIdForStatementStorageKey(final IBranchPath branchPath, final long statementStorageKey) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		final Query query = IndexQueries.and(SnomedIndexQueries.ACTIVE_RELATIONSHIPS_QUERY, new ComponentStorageKeyField(statementStorageKey).toQuery());
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
			final BooleanQuery query = new BooleanQuery(true);
			query.add(new TermQuery(new Term(RELATIONSHIP_VALUE_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), Occur.MUST);
			query.add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
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
			final BooleanQuery query = new BooleanQuery(true);
			query.add(new TermQuery(new Term(RELATIONSHIP_OBJECT_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), Occur.MUST);
			query.add(SnomedIndexQueries.ACTIVE_COMPONENT_QUERY, Occur.MUST);
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying active outbound statements of concept. ID: " + conceptId, e);
		}
	}

	@Override
	public Map<String, String> getAllStatementLabelsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");

		final StatementIdCollector collector = new StatementIdCollector();
		final BooleanQuery typeQuery = new BooleanQuery(true);
		typeQuery.add(new TermQuery(new Term(RELATIONSHIP_VALUE_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), Occur.SHOULD);
		typeQuery.add(new TermQuery(new Term(RELATIONSHIP_OBJECT_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), Occur.SHOULD);
		final BooleanQuery query = new BooleanQuery(true);
		query.add(typeQuery, Occur.MUST);
		service.search(branchPath, query, collector);

		final LongSet idsSet = collector.getIds();
		final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idsSet.size());

		if (idsSet.size() > 1000) {
			final SnomedComponentLabelCollector labelCollector = new SnomedComponentLabelCollector(idsSet);
			service.search(branchPath, SnomedIndexQueries.CONCEPT_TYPE_QUERY, labelCollector);
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

		final StatementIdCollector collector = new StatementIdCollector();
		final BooleanQuery typeQuery = new BooleanQuery(true);
		typeQuery.add(new TermQuery(new Term(RELATIONSHIP_VALUE_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), Occur.SHOULD);
		typeQuery.add(new TermQuery(new Term(RELATIONSHIP_OBJECT_ID, IndexUtils.longToPrefixCoded(Long.valueOf(conceptId)))), Occur.SHOULD);
		final BooleanQuery query = new BooleanQuery(true);
		query.add(typeQuery, Occur.MUST);
		service.search(branchPath, query, collector);

		final LongSet idsSet = collector.getIds();
		final Map<String, String> $ = Maps.newHashMapWithExpectedSize(idsSet.size());

		if (idsSet.size() > 1000) {
			final SnomedConceptIconIdCollector labelCollector = new SnomedConceptIconIdCollector(idsSet);
			service.search(branchPath, SnomedIndexQueries.CONCEPT_TYPE_QUERY, labelCollector);
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

	@Override
	public Map<String, String> getAllDestinationLabels(final IBranchPath branchPath, final Collection<String> sourceIds,
			final String typeId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(sourceIds, "sourceIds");
		checkNotNull(typeId, "typeId");
		final StatementDestinationIdCollector collector = new StatementDestinationIdCollector();

		final BooleanQuery query = new BooleanQuery(true);
		query.add(new TermQuery(new Term(RELATIONSHIP_ATTRIBUTE_ID, IndexUtils.longToPrefixCoded(Long.valueOf(typeId)))), Occur.MUST);
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
		return IndexQueries.and(SnomedIndexQueries.ACTIVE_RELATIONSHIPS_QUERY, new TermQuery(new Term(RELATIONSHIP_OBJECT_ID, IndexUtils.longToPrefixCoded(conceptId))));		
	}
	
}