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
package com.b2international.snowowl.index.diff.tests.mock;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TotalHitCountCollector;
import org.eclipse.emf.cdo.common.branch.CDOBranch;

import com.b2international.snowowl.core.api.BranchPath;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOBranchPath;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.datastore.server.index.IDirectoryManager;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.datastore.server.index.RAMDirectoryManager;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;

import bak.pcj.list.IntArrayList;

/**
 * @since 4.3
 */
public class DiffIndexServerService extends IndexServerService<DiffConceptIndexEntry> {

	private static final long DEFAULT_TIMEOUT_MINUTES = 30L;

	private final LoadingCache<IBranchPath, Integer> branchPathToCdoBranchId = CacheBuilder.newBuilder().build(new CacheLoader<IBranchPath, Integer>() {
		@Override
		public Integer load(IBranchPath key) throws Exception {
			return ++lastUsedBranchId;
		}
	});
			
	private final IDirectoryManager directoryManager;
	private volatile int lastUsedBranchId = CDOBranch.MAIN_BRANCH_ID;
	
	public DiffIndexServerService() {
		super(DEFAULT_TIMEOUT_MINUTES);
		directoryManager = new RAMDirectoryManager(getRepositoryUuid(), new File(getRepositoryUuid()));
		branchPathToCdoBranchId.put(BranchPathUtils.createMainPath(), CDOBranch.MAIN_BRANCH_ID);
	}

	@Override
	public String getRepositoryUuid() {
		return DiffIndexServerService.class.getName();
	}

	@Override
	protected IDirectoryManager getDirectoryManager() {
		return directoryManager;
	}

	public void deleteDocs(final IBranchPath branchPath, final long... storageKeys) {
		for (final long storageKey : storageKeys) {
			delete(branchPath, storageKey);
		}

		commit(branchPath);
	}

	public void tag(final String tag) {
		IBranchPath versionPath = BranchPathUtils.createVersionPath(tag);
		// Will update to a newer value for the version path, if it existed previously 
		invalidateCdoId(versionPath);
		reopen(versionPath, getMostRecentPhysicalPath(versionPath));
	}

	public void invalidateCdoId(final IBranchPath versionPath) {
		branchPathToCdoBranchId.invalidate(versionPath);
	}
	
	@Override
	protected BranchPath getMostRecentPhysicalPath(IBranchPath logicalPath) {
		final IntArrayList segments = new IntArrayList();
		final Iterator<IBranchPath> branchPrefixIterator = BranchPathUtils.topToBottomIterator(logicalPath);
		while (branchPrefixIterator.hasNext()) {
			final IBranchPath currentBranchPath = branchPrefixIterator.next();
			segments.add(branchPathToCdoBranchId.getUnchecked(currentBranchPath));
		}
		
		return new CDOBranchPath(segments.toArray());
	}

	public void indexIrrelevantDocs(final IBranchPath branchPath, final String... ids) {
		indexIrrelevantDocs(branchPath, toConcepts(ids));
	}

	public void indexIrrelevantDocs(final IBranchPath branchPath, final Iterable<DiffConcept> docs) {
		indexDocs(branchPath, docs, false);
	}

	public void indexRelevantDocs(final IBranchPath branchPath, final String... ids) {
		indexRelevantDocs(branchPath, toConcepts(ids));
	}

	public void indexRelevantDocs(final IBranchPath branchPath, final Iterable<DiffConcept> docs) {
		indexDocs(branchPath, docs, true);
	}

	private Collection<DiffConcept> toConcepts(final String... ids) {
		return FluentIterable.from(Arrays.asList(ids))
				.transform(new Function<String, DiffConcept>() { @Override public DiffConcept apply(final String id) {
					return new DiffConcept(id, id + "_default_label");
				}})
				.toSet();
	}

	public void indexRelevantDocs(final IBranchPath branchPath, final Map<String, String> idLabelPairs) {
		final Iterable<DiffConcept> docs = FluentIterable.from(idLabelPairs.entrySet())
				.transform(new Function<Entry<String, String>, DiffConcept>() { @Override public DiffConcept apply(final Entry<String, String> input) {
					return new DiffConcept(input.getKey(), input.getValue());
				}});

		indexRelevantDocs(branchPath, docs);
	}

	public int getAllDocsCount(final IBranchPath branchPath) {
		return getTotalHitCount(branchPath, getAllDocsQuery());
	} 

	public Collection<Document> getAllDocs(final IBranchPath branchPath) {
		return executeReadTransaction(branchPath, new IndexRead<Collection<Document>>() {
			@Override
			public Collection<Document> execute(final IndexSearcher index) throws IOException {
				final TotalHitCountCollector hitCountCollector = new TotalHitCountCollector();
				index.search(getAllDocsQuery(), hitCountCollector);
				final int totalHits = hitCountCollector.getTotalHits();

				if (totalHits > 0) {
					final Collection<Document> docs = newArrayList();
					for (final ScoreDoc sd : index.search(getAllDocsQuery(), totalHits).scoreDocs) {
						docs.add(index.doc(sd.doc));
					}		
					return docs;
				} else {
					return Collections.emptySet();
				}
			}
		});
	}

	public Map<String, DiffConcept> getAllDocsAsMap(final IBranchPath branchPath) {
		return FluentIterable.from(getAllDocs(branchPath))
				.transform(new Function<Document, DiffConcept>() { @Override public DiffConcept apply(final Document input) {
					return new DiffConcept(Mappings.id().getValue(input), Mappings.label().getValue(input));
				}}).uniqueIndex(new Function<DiffConcept, String>() { @Override public String apply(final DiffConcept input) {
					return input.getId();
				}});
	}

	private Query getAllDocsQuery() {
		return Mappings.id().toExistsQuery();
	}

	private void indexDocs(final IBranchPath branchPath, final Iterable<DiffConcept> concepts, final boolean relevant) {
		for (final DiffConcept concept : concepts) {
			index(branchPath, new DiffConceptMappingStrategy(concept, relevant));
		}

		commit(branchPath);
	}
}
