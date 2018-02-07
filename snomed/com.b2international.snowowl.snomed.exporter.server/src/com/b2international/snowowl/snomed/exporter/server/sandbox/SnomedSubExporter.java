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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.collect.Iterables.isEmpty;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.SegmentReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCacheDocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.util.Bits;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.server.index.IndexServerService;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;

/**
 *
 */
public class SnomedSubExporter extends AbstractIterator<String> implements Iterator<String>, AutoCloseable {

	private final Supplier<DocIdsIterator> itr;
	private final Supplier<ReferenceManager<IndexSearcher>> manager;
	private final Supplier<IndexSearcher> searcher;
	private final SnomedIndexExporter exporter;
	private final IBranchPath branchPath;
	private final Collection<String> ignoredSegmentNames;

	protected SnomedSubExporter(final IBranchPath branchPath, final SnomedIndexExporter exporter, final Collection<String> ignoredSegmentNames) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.exporter = checkNotNull(exporter, "exporter");
		itr = createDocIdIterator();
		manager = createReferenceManager();
		searcher = createIndexSearcher();
		this.ignoredSegmentNames = isEmpty(ignoredSegmentNames) 
			? Collections.<String>emptySet() 
			: copyOf(ignoredSegmentNames);
	}

	@Override
	public void close() throws IOException {
		manager.get().release(searcher.get());
	}

	@Override
	protected String computeNext() {
		while (itr.get().next()) {
			final int docId = itr.get().getDocID();
			final Document doc = tryGetDocument(docId);
			return exporter.transform(doc);
		}
		return endOfData();
	}


	/*returns with the server side index service.*/
	@SuppressWarnings("rawtypes")
	private final IndexServerService getIndexServerService() {
		return (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

	private Supplier<DocIdsIterator> createDocIdIterator() {
		return memoize(new Supplier<DocIdsIterator>() {
			@Override public DocIdsIterator get() {
				final int maxDoc = getIndexServerService().maxDoc(branchPath);
				final DocIdCollector collector = DocIdCollector.create(maxDoc);
				getIndexServerService().search(branchPath, exporter.getExportQuery(branchPath), createFilter(), collector);
				try {
					return collector.getDocIDs().iterator();
				} catch (final IOException e) {
					throw new SnowowlRuntimeException("Failed to create iterator.", e);
				}
			}

			private Filter createFilter() {
				return new Filter() {
					
					@Override
					public DocIdSet getDocIdSet(final AtomicReaderContext context, final Bits acceptDocs) throws IOException {
						
						final SegmentReader segmentReader = (SegmentReader) context.reader();
						if (ignoredSegmentNames.contains(segmentReader.getSegmentInfo().info.name)) {
							return null;
						}
						
						return new FieldCacheDocIdSet(context.reader().maxDoc(), acceptDocs) {
							protected final boolean matchDoc(int doc) {
								return null == acceptDocs ? true : acceptDocs.get(doc);
							}
						};
						
					}
					
				};
			}
		});
	}

	private Supplier<IndexSearcher> createIndexSearcher() {
		return memoize(new Supplier<IndexSearcher>() {
			@Override public IndexSearcher get() {
				try {
					return manager.get().acquire();
				} catch (final IOException e) {
					throw new SnowowlRuntimeException("Failed to acquire index searcher.", e);
				}
			}
		});
	}

	private Supplier<ReferenceManager<IndexSearcher>> createReferenceManager() {
		return memoize(new Supplier<ReferenceManager<IndexSearcher>>() {
			@SuppressWarnings("unchecked")
			@Override public ReferenceManager<IndexSearcher> get() {
				return getIndexServerService().getManager(branchPath);
			}
		});
	}
	
	private Document tryGetDocument(final int docId) {
		try {
			return searcher.get().doc(docId, exporter.getFieldsToLoad());
		} catch (final IOException e) {
			throw new SnowowlRuntimeException("Failed to load document.", e);
		}
	}

}