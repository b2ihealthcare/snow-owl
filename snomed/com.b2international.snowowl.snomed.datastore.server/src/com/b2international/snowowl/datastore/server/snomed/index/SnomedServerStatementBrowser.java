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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

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

import com.b2international.commons.CompareUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.mapping.IndexField;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import bak.pcj.map.LongKeyMap;
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
			.relationshipSource()
			.relationshipDestination()
			.relationshipGroup()
			.relationshipUnionGroup()
			.released()
			.relationshipInferred()
			.relationshipUniversal()
			.relationshipDestinationNegated()
			.build();

	/**
	 * Class constructor.
	 * @param indexService index service for SNOMED&nbsp;CT ontology.
	 */
	public SnomedServerStatementBrowser(final SnomedIndexService indexService) {
		super(indexService);
	}

	@Override
	protected SnomedRelationshipIndexEntry createResultObject(final IBranchPath branchPath, final Document doc) {
		return SnomedRelationshipIndexEntry.builder(doc).build();
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
			service.search(branchPath, getRelationshipSourceOrDestinationQuery(conceptId), collector);
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
			final Query query = SnomedMappings.newQuery().relationshipDestination(Long.valueOf(conceptId)).matchAll();
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
			final Query query = SnomedMappings.newQuery().relationshipType(typeId).relationshipDestination(conceptId).matchAll();
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
			final Query query = SnomedMappings.newQuery().relationshipSource(Long.valueOf(conceptId)).matchAll();
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
			final Set<String> fieldsToLoad = SnomedMappings.fieldsToLoad().relationshipGroup().build();
			searcher = manager.acquire();

			while (itr.next()) {
				final Document doc = service.document(searcher, itr.getDocID(), fieldsToLoad);
				final int group = SnomedMappings.relationshipGroup().getValue(doc);
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
			final Set<String> fieldsToLoad = SnomedMappings.fieldsToLoad().relationshipUnionGroup().build();
			searcher = manager.acquire();

			while (itr.next()) {
				final Document doc = service.document(searcher, itr.getDocID(), fieldsToLoad);
				final int unionGroup = SnomedMappings.relationshipUnionGroup().getValue(doc);
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
		return getStatementStorageKeysForGroupField(branchPath, conceptId, group, SnomedMappings.relationshipGroup().fieldName());
	}

	@Override
	public long[] getStatementStorageKeysForUnionGroup(final IBranchPath branchPath, final long conceptId, final int unionGroup) {
		return getStatementStorageKeysForGroupField(branchPath, conceptId, unionGroup, SnomedMappings.relationshipUnionGroup().fieldName());
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
			final Query query = SnomedMappings.newQuery().active().relationshipDestination(Long.valueOf(conceptId)).matchAll();
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
			final Query query = SnomedMappings.newQuery().active().relationshipSource(Long.valueOf(conceptId)).matchAll();
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
			final Query query = SnomedMappings.newQuery().active().relationshipType(relationshipTypeId).relationshipSource(Long.valueOf(conceptId)).matchAll();
			service.search(branchPath, query, collector);
			return createResultObjects(branchPath, collector.getDocIDs().iterator());
		} catch (final IOException e) {
			throw new RuntimeException("Error when querying active outbound statements with type for concept. ID: " + conceptId, e);
		}
	}
	
	@Override
	public Map<String, String> getAllStatementImageIdsById(final IBranchPath branchPath, final String conceptId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(conceptId, "SNOMED CT concept ID cannot be null.");

		final BooleanQuery query = new BooleanQuery(true);
		query.add(getRelationshipSourceOrDestinationQuery(conceptId), Occur.MUST);
		
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

	private Query getRelationshipSourceOrDestinationQuery(final String conceptId) {
		final Long longConceptId = Long.valueOf(conceptId);
		return SnomedMappings.newQuery()
				.relationshipSource(longConceptId)
				.relationshipDestination(longConceptId)
				.matchAny();
	}

	@Override
	public Map<String, String> getAllDestinationLabels(final IBranchPath branchPath, final Collection<String> sourceIds, final String typeId) {
		checkNotNull(branchPath, "Branch path argument cannot be null.");
		checkNotNull(sourceIds, "sourceIds");
		checkNotNull(typeId, "typeId");
		
		return SnomedRequests.prepareSearchRelationship()
			.all()
			.filterBySource(sourceIds)
			.filterByType(typeId)
			.filterByActive(true)
			.setExpand("destination(expand(pt()))")
			.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
			.build(branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(new Function<SnomedRelationships, Map<String, String>>() {
				@Override
				public Map<String, String> apply(SnomedRelationships input) {
					Map<String, String> result = newHashMap();
					for (ISnomedRelationship relationship : input) {
						result.put(relationship.getSourceId(), relationship.getDestinationConcept().getPt() == null
								? relationship.getDestinationId() : relationship.getDestinationConcept().getPt().getTerm());
					}
					return result;
				}
			})
			.getSync();
	}
	
	private Query queryActiveRelationshipsWhereObjectId(long conceptId) {
		return SnomedMappings.newQuery().active().relationship().relationshipSource(conceptId).matchAll();		
	}
	
}