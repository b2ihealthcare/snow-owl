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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.IDisposableService;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexUpdater;
import com.b2international.snowowl.datastore.index.DocIdCollector;
import com.b2international.snowowl.datastore.index.DocIdCollector.DocIdsIterator;
import com.b2international.snowowl.datastore.index.DocumentUpdater;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.FakeQueryAdapter;
import com.b2international.snowowl.datastore.index.IndexRead;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderBase;
import com.b2international.snowowl.datastore.index.mapping.DocumentBuilderFactory;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * Terminology independent server side Lucene specific index service responsible for managing indexed documents.
 * 
 * 
 * @see AbstractIndexUpdater
 */
public abstract class IndexServerService<E extends IIndexEntry> extends AbstractIndexUpdater<E> implements IDisposableService, IBranchIndexServiceProvider {

	private final class StateCacheLoader extends CacheLoader<IBranchPath, IndexBranchService> {
		@Override public IndexBranchService load(final IBranchPath key) throws Exception {
			final IndexBranchService branchService = new IndexBranchService(
					key, //branch path 
					getDirectoryManager(), //the directory manager 
					BranchPathUtils.isMain(key) ? null : getBranchService(key.getParent()));  //master service (if any)
			
			if (branchService.isFirstStartupAtMain()) {
				getDirectoryManager().fireFirstStartup(branchService);
			}
			
			return branchService;
		}
	}

	private final class StateRemovalListener implements RemovalListener<IBranchPath, IndexBranchService> {
		@Override public void onRemoval(final RemovalNotification<IBranchPath, IndexBranchService> notification) {
			notification.getValue().close();
		}
	}
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(IndexServerService.class);
	
	protected volatile boolean disposed;

	private final LoadingCache<IBranchPath, IndexBranchService> branchServices;
	
	/**
	 * Initializes a new index index service instance. 
	 */
	protected IndexServerService() {
		this.branchServices = CacheBuilder.newBuilder()
				.removalListener(new StateRemovalListener())
				.build(new StateCacheLoader());
	}

	protected abstract IDirectoryManager getDirectoryManager();
	
	@Override
	public void commit(final IBranchPath branchPath) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotDisposed();
		
		final IndexBranchService branchService = getBranchService(branchPath);

		try {
			branchService.commit();
		} catch (final IndexException e) {
			throw e;
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	@Override
	public void delete(final IBranchPath branchPath, final Term term) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotDisposed();
		
		final IndexBranchService branchService = getBranchService(branchPath);
		
		try {
			branchService.deleteDocuments(term);
		} catch (final IOException e) {
			throw new IndexException(e);
		}
		
	}
	
	@Override
	public void delete(final IBranchPath branchPath, final long storageKey) {
		checkNotNull(branchPath, "branchPath");
		checkNotDisposed();
		final IndexBranchService branchService = getBranchService(branchPath);
		try {
			branchService.deleteDocuments(toTerm(storageKey));
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	private Term toTerm(final long storageKey) {
		return Mappings.storageKey().toTerm(storageKey);
	}
	
	private Query toQuery(final long storageKey) {
		return Mappings.storageKey().toQuery(storageKey);
	}

	@Override
	public void rollback(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		checkNotDisposed();
		final IndexBranchService branchService = getBranchService(branchPath);
		try {
			branchService.rollback();
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	@Override
	public void deleteAll(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		checkNotDisposed();
		final IndexBranchService branchService = getBranchService(branchPath);
		try {
			branchService.deleteAll();
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	@Override
	public synchronized void reopen(final IBranchPath branchPath, final int[] cdoBranchPath, final long baseTimestamp) {
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkState(!BranchPathUtils.isMain(branchPath), "Branch path cannot be MAIN.");
		final IndexBranchService baseBranchService = getBranchService(branchPath.getParent());
		
		try {
			baseBranchService.createIndexCommit(branchPath, cdoBranchPath, baseTimestamp);
			inactiveClose(branchPath);
			prepare(branchPath);
		} catch (final IOException e) {
			throw new IndexException("Failed to update snapshot for '" + branchPath + "'. [" + baseTimestamp + "]", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void prepare(final IBranchPath branchPath) {
		getHitCount(branchPath, (IIndexQueryAdapter<E>) FakeQueryAdapter.INSTANCE);
	}
	
	/**
	 * Returns with the cached index reader for the specified {@link IBranchPath branch path}. If the index reader does not exist
	 * in the cache. The reader will be instantiated and cached.
	 * @param branchPath the branch path.
	 * @return the index reader.
	 */
	@Override
	public ReferenceManager<IndexSearcher> getManager(final IBranchPath branchPath) {
		return getBranchService(branchPath).getManager();
	}

	@Override
	public synchronized void inactiveClose(final IBranchPath branchPath) {
		
		// XXX: Don't check if this service is disposed here; we can still remove directories on shutdown without it running.
		final IndexBranchService branchService = branchServices.asMap().remove(branchPath);

		if (null != branchService) {
			branchService.close();
		}
	}
	
	@Override
	public void dispose() {
		if (!disposed) {
			branchServices.invalidateAll();
		}
		
		disposed = true;
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void index(final IBranchPath branchPath, final Document document, final Term id) {
		checkNotNull(document, "document");
		checkNotNull(id, "id");
		checkNotDisposed();

		final IndexBranchService branchService = getBranchService(branchPath);

		try {
			branchService.updateDocument(id, document);
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}
	
	@Override
	public void index(IBranchPath branchPath, Document document, long storageKey) {
		index(branchPath, document, toTerm(storageKey));
	}
	
	@Override
	public <D extends DocumentBuilderBase<D>> void update(IBranchPath branchPath, long storageKey, DocumentUpdater<D> documentUpdater,
			DocumentBuilderFactory<D> builderFactory) {
		upsert(branchPath, toQuery(storageKey), documentUpdater, builderFactory);		
	}
	
	@Override
	public <D extends DocumentBuilderBase<D>> void upsert(IBranchPath branchPath, Query query, DocumentUpdater<D> documentUpdater, DocumentBuilderFactory<D> builderFactory) {
		checkNotDisposed();
		try {
			getBranchService(branchPath).upsert(query, documentUpdater, builderFactory);
		} catch (IOException e) {
			throw new IndexException(e);
		}
	}

	@Override
	public List<DocumentWithScore> search(final IBranchPath branchPath, final Query query, final @Nullable Filter filter, final @Nullable Sort sort, 
			final int limit) {

		checkNotNull(branchPath, "branchPath");
		checkNotNull(query, "query");
		checkArgument(limit > 0, "limit must be positive");
		checkNotDisposed();

		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();
			final TopDocs topDocs;
			
			if (null != sort) {
				topDocs = searcher.search(query, filter, limit, sort, true, false);
			} else {
				topDocs = searcher.search(query, filter, limit);
			}
			
			final List<DocumentWithScore> result = Lists.newArrayListWithExpectedSize(topDocs.totalHits);

			for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
				result.add(new DocumentWithScore(searcher.doc(scoreDoc.doc), branchPath, scoreDoc.score));
			}

			return result;
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public List<DocumentWithScore> search(final IBranchPath branchPath, final Query query, final @Nullable Filter filter, @Nullable Sort sort, 
			final int offset, final int limit) {

		checkNotNull(branchPath, "branchPath");
		checkNotNull(query, "query");
		checkArgument(offset >= 0, "offset may not be negative");
		checkArgument(limit > 0, "limit must be positive");
		checkNotDisposed();

		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();

			// XXX: for consistent ScoreDoc gathering, if there's no sort condition, use the index order explicitly.
			if (null == sort) {
				sort = Sort.INDEXORDER;
			}

			final TopDocs topDocs = searcher.search(query, filter, offset + limit, sort, true, false);
			final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			final List<DocumentWithScore> result = Lists.newArrayListWithExpectedSize(limit);

			for (int i = offset; i < offset + limit && i < scoreDocs.length; i++) {
				result.add(new DocumentWithScore(searcher.doc(scoreDocs[i].doc), branchPath, scoreDocs[i].score));
			}

			return result;
		} catch (final IOException e) {
			throw new IndexException(e);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexService#searchIds(com.b2international.snowowl.core.api.IBranchPath, org.apache.lucene.search.Query, org.apache.lucene.search.Filter, org.apache.lucene.search.Sort, int)
	 */
	@Override
	public List<String> searchIds(final IBranchPath branchPath, final Query query, @Nullable final Filter filter, final @Nullable Sort sort, final int limit) {

		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(query, "Query argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			
			searcher = manager.acquire();

			final TopDocs topDocs;
			
			if (null != sort) {
				topDocs = searcher.search(query, filter, limit, sort, true, false);
			} else {
				topDocs = searcher.search(query, filter, limit);
			}

			if (null == topDocs) {
				return Collections.emptyList();
			}
			
			final String[] ids = new String[topDocs.totalHits];

			int size = 0;
			
			for (final ScoreDoc scoreDoc : topDocs.scoreDocs) {
				final Document doc = searcher.doc(scoreDoc.doc, Mappings.fieldsToLoad().id().build());
				ids[size++] = Mappings.id().getValue(doc);
			}
			
			return Arrays.asList(Arrays.copyOf(ids, size));
			
		} catch (final IOException e) {
			
			throw new IndexException(e);
			
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
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexService#searchUnsorted(com.b2international.snowowl.core.api.IBranchPath, com.b2international.snowowl.core.api.index.IIndexQueryAdapter)
	 */
	@Override
	public Collection<DocumentWithScore> searchUnordered(final IBranchPath branchPath, final Query query, @Nullable final Filter filter) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(query, "Query argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final int expectedSize = searcher.getIndexReader().maxDoc();
			final DocIdCollector collector = DocIdCollector.create(expectedSize);

			if (null != filter) {
				search(branchPath, query, filter, collector);
			} else {
				search(branchPath, query, collector);
			}

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			final DocumentWithScore[] documents = new DocumentWithScore[expectedSize];

			int size = 0;
			while (itr.next()) {
				documents[size++] = new DocumentWithScore(searcher.doc(itr.getDocID()), branchPath);
			}
			
			return Arrays.asList(Arrays.copyOf(documents, size));
			
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public <T> Multimap<T, String> searchUnorderedIdGroups(final IBranchPath branchPath, final Query query, @Nullable final Filter filter, 
			final String groupField, 
			final Set<String> valueFields,
			final Function<BytesRef, T> groupFieldConverter) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(query, "Query argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final int expectedSize = searcher.getIndexReader().maxDoc();

			final GroupingSearch groupingSearch = new GroupingSearch(groupField);
			groupingSearch.setAllGroups(false);
			groupingSearch.setAllGroupHeads(false);
			groupingSearch.setIncludeScores(false);
			groupingSearch.setIncludeMaxScore(false);
			groupingSearch.setSortWithinGroup(Sort.INDEXORDER);
			groupingSearch.setGroupSort(Sort.INDEXORDER);
			groupingSearch.setGroupDocsLimit(expectedSize);
			
			final TopGroups<BytesRef> topGroups = groupingSearch.search(searcher, filter, query, 0, expectedSize);
			final Multimap<T, String> results = HashMultimap.create();
			
			for (final GroupDocs<BytesRef> groupDocs : topGroups.groups) {
				if (groupDocs.groupValue != null) {
					final ScoreDoc[] groupScoreDocs = groupDocs.scoreDocs;
					final T groupValue = groupFieldConverter.apply(groupDocs.groupValue);
					
					for (int i = 0; i < groupScoreDocs.length; i++) {
						final Document document = searcher.doc(groupScoreDocs[i].doc, valueFields);
						for (final String valueField : valueFields) {
							results.putAll(groupValue, ImmutableSet.copyOf(document.getValues(valueField)));
						}
					}
				}
			}
			
			return results;
			
		} catch (final IOException e) {
			throw new IndexException(e);
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
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexService#searchUnorderedIds(com.b2international.snowowl.core.api.IBranchPath, org.apache.lucene.search.Query, org.apache.lucene.search.Filter)
	 */
	@Override
	public Collection<String> searchUnorderedIds(final IBranchPath branchPath, final Query query, @Nullable final Filter filter) {
		
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		Preconditions.checkNotNull(query, "Query argument cannot be null.");
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			searcher = manager.acquire();
			final int expectedSize = searcher.getIndexReader().maxDoc();
			final DocIdCollector collector = DocIdCollector.create(expectedSize);

			if (null != filter) {
				search(branchPath, query, filter, collector);
			} else {
				search(branchPath, query, collector);
			}

			final DocIdsIterator itr = collector.getDocIDs().iterator();
			final String[] ids = new String[expectedSize];

			int size = 0;
			while (itr.next()) {
				final Document doc = searcher.doc(itr.getDocID(), Mappings.fieldsToLoad().id().build());
				ids[size++] = Mappings.id().getValue(doc);
			}
			
			return Arrays.asList(Arrays.copyOf(ids, size));
			
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public int getHitCount(final IBranchPath branchPath, final Query query, final @Nullable Filter filter) {
		
		checkNotNull(branchPath, "branchPath");
		checkNotNull(query, "query");
		checkNotDisposed();

		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;		
		try {
			searcher = manager.acquire();
			final int expectedSize = searcher.getIndexReader().maxDoc();
			final DocIdCollector collector = DocIdCollector.create(expectedSize);

			if (null != filter) {
				search(branchPath, query, filter, collector);
			} else {
				search(branchPath, query, collector);
			}

			int totalHits = 0;
			final DocIdsIterator itr = collector.getDocIDs().iterator();
			while (itr.next()) {
				totalHits++;
			}
			
			return totalHits;
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public TopDocs search(final IBranchPath branchPath, final Query query, final int limit) {
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;	
		try {
			searcher = manager.acquire();
			return searcher.search(Preconditions.checkNotNull(query, "Query argument cannot be null."), limit);
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public int getTotalHitCount(final IBranchPath branchPath, final Query query) {
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;	
		
		try {
			searcher = manager.acquire();
			final TotalHitCountCollector totalHitCountCollector = new TotalHitCountCollector();
			searcher.search(Preconditions.checkNotNull(query, "Query argument cannot be null."), totalHitCountCollector);
			
			return totalHitCountCollector.getTotalHits();
			
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public Document document(final IBranchPath branchPath, final int docId, final @Nullable Set<String> fieldsToLoad) {
		
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;	
		
		try {
			searcher = manager.acquire();
			return searcher.doc(docId, fieldsToLoad);
		} catch (final IOException e) {
			throw new IndexException(e);
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
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexService#document(org.apache.lucene.search.IndexSearcher, int, java.util.Set)
	 */
	@Override
	public Document document(final IndexSearcher searcher, final int docId, final Set<String> fieldsToLoad) {
		Preconditions.checkNotNull(searcher, "Index searcher argument cannot be null.");
		Preconditions.checkNotNull(fieldsToLoad, "Fields to load argument cannot be null.");
		Preconditions.checkState(docId >= 0, "Document ID must be a positive integer.");
		try {
			return searcher.doc(docId, fieldsToLoad);
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}
	
	@Override
	public int maxDoc(final IBranchPath branchPath) {
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;
		try {
			
			searcher = manager.acquire();
			final IndexReader reader = searcher.getIndexReader();
			return reader.maxDoc();
			
		} catch (final IOException e) {

			LOGGER.error("Error while getting index searcher from NRT manager.", e);
			
			throw new IndexException(e);
			
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
	public Collector search(final IBranchPath branchPath, final Query query, final Collector collector) {
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;	
		
		try {
			searcher = manager.acquire();
			searcher.search(Preconditions.checkNotNull(query, "Query argument cannot be null."), Preconditions.checkNotNull(collector, "Collector argument cannot be null."));
			return collector;
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public Collector search(final IBranchPath branchPath, final Query query, final Filter filter, final Collector collector) {
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;	
		
		try {
			searcher = manager.acquire();
			searcher.search(Preconditions.checkNotNull(query, "Query argument cannot be null."), 
					Preconditions.checkNotNull(filter, "Filter argument cannot be null."), 
					Preconditions.checkNotNull(collector, "Collector argument cannot be null."));
			
			return collector;
		} catch (final IOException e) {
			throw new IndexException(e);
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
	public void addDocument(final IBranchPath branchPath, final Document document) {
		checkNotNull(branchPath, "branchPath");
		checkNotNull(document, "document");
		checkNotDisposed();

		try {
			getBranchService(branchPath).addDocument(document);
		} catch (final IOException e) {
			throw new IndexException(e);
		}		
	}
	
	@Override
	public IndexBranchService getBranchService(final IBranchPath branchPath) {
		
		// Record usage
		if (!BranchPathUtils.isMain(branchPath)) {
			getIndexAccessUpdater().registerAccessAndRecordUsage(branchPath);
	
			//to ensure recursive index structure revive
			for (final Iterator<IBranchPath> itr = BranchPathUtils.topToBottomIterator(branchPath); itr.hasNext(); /**/) {
				final IBranchPath currentBranchPath = itr.next();
				if (!currentBranchPath.equals(branchPath) && !BranchPathUtils.isMain(currentBranchPath)) {
					getBranchService(currentBranchPath);
				}
			}
		}
		
		/*
		 * XXX: getIfPresent in combination with the synchronized block works effectively like the double-checked locking pattern. If two
		 * threads find the branch-specific service unpopulated, they will attempt to get the lock and initialize it sequentially. The first
		 * thread entering the synchronized block will initialize the IndexBranchService for the branch, while the second will find an
		 * already initialized instance. Both of them will call postProcess on it, but because of the branch-specific service's
		 * AtomicBoolean switch, the second invocation will have no effect at all.
		 * 
		 * Calling postProcess the first time on the IndexBranchService, however, will result in a recursive getBranchService() call from
		 * other (change processing job) threads. These will already get the snapshotted, but not yet processed instance from
		 * getIfPresent, will not enter the synchronized block, and will use the instance to apply changes from the branch on it, as
		 * expected.
		 */
		IndexBranchService branchService =  branchServices.getIfPresent(branchPath);
		
		if (null == branchService) {
			synchronized (this) {
				try {
					branchService = branchServices.get(branchPath);
					postProcess(branchService);
				} catch (final ExecutionException e) {
					throw IndexException.wrap(e.getCause());
				} catch (final UncheckedExecutionException e) {
					throw IndexException.wrap(e.getCause());
				}
			}
		}
	
		return branchService;
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexUpdater#listFiles(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public List<String> listFiles(final IBranchPath branchPath) {
		try {
			checkNotDisposed();
			return getDirectoryManager().listFiles(checkNotNull(branchPath, "branchPath"));
		} catch (final IOException e) {
			throw new IndexException(e);
		}		
	}
	
	protected abstract IIndexPostProcessor getIndexPostProcessor();
	
	protected ICommitTimeProvider getCommitTimeProvider() {
		return new CommitTimeProvider(getRepositoryUuid());
	}
	
	protected IIndexAccessUpdater getIndexAccessUpdater() {
		return new IndexAccessUpdater();
	}
	
	public void postProcess(final IndexBranchService branchService) {
		final IIndexPostProcessingConfiguration configuration = branchService.getPostProcessingConfiguration();
		getIndexPostProcessor().postProcess(configuration);
	}

	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @return
	 */
	public boolean hasDocuments(final IBranchPath branchPath) {
		return hasDocumentsInternal(branchPath);
	}

	private void checkNotDisposed() {
		if (disposed) {
			throw new IndexException("IndexServerService is already disposed.");
		}
	}

	private boolean hasDocumentsInternal(final IBranchPath branchPath) {
		checkNotNull(branchPath, "branchPath");
		checkNotDisposed();
		
		try {
			return getBranchService(branchPath).hasDocuments();
		} catch (final IOException e) {
			throw new IndexException(e);
		}
	}

	public <T> T executeReadTransaction(IBranchPath branchPath, IndexRead<T> read) {
		final ReferenceManager<IndexSearcher> manager = getManager(branchPath);
		IndexSearcher searcher = null;	
		try {
			searcher = manager.acquire();
			return read.execute(searcher);
		} catch (final IOException e) {
			throw new IndexException(e);
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
}