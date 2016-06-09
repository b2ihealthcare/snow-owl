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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.collect.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.snomed.datastore.IsAStatement;
import com.b2international.snowowl.snomed.datastore.SnomedStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementCollectionMode;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Index based statement browser implementation.
 */
public class SnomedServerStatementBrowser implements SnomedStatementBrowser {

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

	private Query getRelationshipSourceOrDestinationQuery(final String conceptId) {
		final Long longConceptId = Long.valueOf(conceptId);
		return SnomedMappings.newQuery()
				.relationshipSource(longConceptId)
				.relationshipDestination(longConceptId)
				.matchAny();
	}

}