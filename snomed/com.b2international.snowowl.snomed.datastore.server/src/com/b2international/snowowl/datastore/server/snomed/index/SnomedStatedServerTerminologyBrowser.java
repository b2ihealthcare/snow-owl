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
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.CompareUtils;
import com.b2international.commons.graph.GraphUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.SnomedStatedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * Server-side terminology browser implementation for SNOMED CT which uses only the STATED relationships when computing parent-child relationships.
 */
public class SnomedStatedServerTerminologyBrowser extends SnomedServerTerminologyBrowser implements SnomedStatedTerminologyBrowser {

	public SnomedStatedServerTerminologyBrowser(SnomedIndexService indexService) {
		super(indexService);
	}

	@Override
	protected Query getSubTypesQuery(String id) {
		return SnomedMappings.newQuery()
				.statedParent(id)
				.and(getTerminologyComponentTypeQuery()).matchAll();
	}
	
	@Override
	protected Query getAllSubTypesQuery(String id) {
		return SnomedMappings.newQuery()
				.concept()
				.and(SnomedMappings.newQuery()
						.statedParent(id)
						.statedAncestor(id)
						.matchAny())
				.matchAll();
	}
	
	@Override
	protected Query getRootConceptsQuery() {
		return SnomedMappings.newQuery().statedParent(SnomedMappings.ROOT_ID).active().matchAll();
	}
	
	@Override
	protected Set<String> getFieldNamesToLoad() {
		return SnomedMappings.fieldsToLoad().fields(super.getFieldNamesToLoad()).statedParent().build();
	}
	
	@Override
	public Collection<String> getSuperTypeIds(IBranchPath branchPath, String componentId) {
		checkNotNull(componentId, "Component ID argument cannot be null.");
		
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(componentId), 1); // concept
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return Collections.emptyList();
		}
		
		final Set<String> fieldsToLoad = SnomedMappings.fieldsToLoad().statedParent().build();
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, fieldsToLoad); // direct parents
		
		return SnomedMappings.statedParent().getValuesAsStringList(doc);
	}
	
	@Override
	public LongSet getSuperTypeIds(IBranchPath branchPath, long conceptId) {
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(String.valueOf(conceptId)), 1); // concept
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final Set<String> fieldsToLoad = SnomedMappings.fieldsToLoad().statedParent().build();
		final Document document = service.document(branchPath, topDocs.scoreDocs[0].doc, fieldsToLoad); // direct parents
		
		return SnomedMappings.statedParent().getValueAsLongSet(document);
	}
	
	@Override
	public LongSet getAllSuperTypeIds(IBranchPath branchPath, long conceptId) {
		final TopDocs topDocs = service.search(branchPath, getConceptByIdQueryBuilder(String.valueOf(conceptId)), 1); // concept
		
		if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
			return PrimitiveSets.newLongOpenHashSet();
		}
		
		final Set<String> fieldsToLoad = SnomedMappings.fieldsToLoad().statedParent().statedAncestor().build(); // all parents
		final Document doc = service.document(branchPath, topDocs.scoreDocs[0].doc, fieldsToLoad);
		
		final LongSet parents = SnomedMappings.statedParent().getValueAsLongSet(doc);
		final LongSet ancestors = SnomedMappings.statedAncestor().getValueAsLongSet(doc);
		if (parents.isEmpty() && ancestors.isEmpty()) {
			return LongCollections.emptySet();
		} else {
			final LongSet ids = PrimitiveSets.newLongOpenHashSetWithExpectedSize(parents.size() + ancestors.size());
			ids.addAll(parents);
			ids.addAll(ancestors);
			return ids;
		}
	}
	
	@Override
	public int getHeight(IBranchPath branchPath, String conceptId) {
		checkNotNull(conceptId, "conceptId");
		
		final Query query = getAllSubTypesQuery(conceptId);

		final AtomicReference<IndexSearcher> searcher = new AtomicReference<IndexSearcher>();
		final DocIdCollector collector = DocIdCollector.create(service.maxDoc(branchPath));
		service.search(branchPath, query, collector);

		ReferenceManager<IndexSearcher> manager = null;

		try {

			final Multimap<Long, Long> parentageMap = Multimaps.synchronizedMultimap(HashMultimap.<Long, Long>create());
			manager = service.getManager(branchPath);
			searcher.set(manager.acquire());
			IndexUtils.parallelForEachDocId(collector.getDocIDs(), new IndexUtils.DocIdProcedure() {
				@Override
				public void apply(final int docId) throws IOException {
					final Document doc = searcher.get().doc(docId, SnomedMappings.fieldsToLoad().id().statedParent().build());
					final long id = SnomedMappings.id().getValue(doc);
					parentageMap.putAll(id, SnomedMappings.statedParent().getValues(doc));
				}
			});
			
			return GraphUtils.getLongestPath(parentageMap).size();
			
		} catch (final IOException e) {
			throw new IndexException("Error while getting height of node '" + conceptId + "'.", e);
		} finally {
			if (null != manager && null != searcher.get()) {
				try {
					manager.release(searcher.get());
				} catch (final IOException e) {
					try {
						manager.release(searcher.get());
					} catch (final IOException e1) {
						e.addSuppressed(e1);
					}
					throw new IndexException("Error while releasing index searcher.", e);
				}
			}
		}
	}
}
