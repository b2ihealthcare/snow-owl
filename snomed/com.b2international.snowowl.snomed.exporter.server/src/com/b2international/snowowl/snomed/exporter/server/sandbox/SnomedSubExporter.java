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

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedSubExporter.class);
	
	private final Supplier<DocIdsIterator> itr;
	private final Supplier<ReferenceManager<IndexSearcher>> manager;
	private final Supplier<IndexSearcher> searcher;
	private final SnomedIndexExporter exporter;
	private final IBranchPath branchPath;

	protected SnomedSubExporter(final IBranchPath branchPath, final SnomedIndexExporter exporter) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
		this.exporter = checkNotNull(exporter, "exporter");
		manager = createReferenceManager();
		searcher = createIndexSearcher();
		itr = createDocIdIterator();
		LOGGER.info("Initialized {} for branch path {}", exporter.getClass().getSimpleName(), branchPath.getPath());
	}

	@Override
	public void close() throws IOException {
		manager.get().release(searcher.get());
		LOGGER.info("Released {} for branch path: {}", exporter.getClass().getSimpleName(), branchPath.getPath());
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

	public IBranchPath getBranchPath() {
		return branchPath;
	}

	/*returns with the server side index service.*/
	@SuppressWarnings("rawtypes")
	private final IndexServerService getIndexServerService() {
		return (IndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

	private Supplier<DocIdsIterator> createDocIdIterator() {
		return memoize(new Supplier<DocIdsIterator>() {
			@Override public DocIdsIterator get() {
				try {
					final DocIdCollector collector = DocIdCollector.create(searcher.get().getIndexReader().maxDoc());
					searcher.get().search(exporter.getExportQuery(branchPath), collector);
					return collector.getDocIDs().iterator();
				} catch (final IOException e) {
					throw new SnowowlRuntimeException("Failed to create iterator.", e);
				}
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