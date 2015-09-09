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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.DocumentWithScore;
import com.b2international.snowowl.datastore.index.IDocumentUpdater;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.datastore.server.index.FSIndexServerService;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexEntry;
import com.google.common.collect.Iterables;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.map.LongKeyOpenHashMap;

/**
 * Server-side index service for SNOMED&nbsp;CT ontology.
 */
public class SnomedIndexServerService extends FSIndexServerService<SnomedIndexEntry> implements SnomedIndexUpdater {

	/**
	 * Map for caching SNOMED&nbsp;CT concept index mapping strategies by unique concept IDs for branch aware document updates.
	 */
	private final Map<IBranchPath, LongKeyMap> documentCache;
	
	public SnomedIndexServerService(File indexPath) throws SnowowlServiceException {
		super(checkNotNull(indexPath, "indexPath"));
		documentCache = new HashMap<IBranchPath, LongKeyMap>();
	}

	@Override
	public String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}
	
	/**
	 * (non-API)
	 * 
	 * @param branchPath
	 * @param documentUpdater
	 */
	public void updateConcept(final IBranchPath branchPath, final long conceptId, final IDocumentUpdater documentUpdater) {
		
		if (null == documentCache.get(branchPath)) {
			
			final LongKeyOpenHashMap branchAwareCache = new LongKeyOpenHashMap();
			documentCache.put(branchPath, branchAwareCache);
			
		}
		
		AbstractIndexMappingStrategy mappingStrategy = null;
		
		//try to get from cache. can happen if already looked up and modified
		mappingStrategy = (AbstractIndexMappingStrategy) documentCache.get(branchPath).get(conceptId);
		
		if (null == mappingStrategy) { //if first attempt, retrieve from index
			
			final Term conceptIdTerm = new Term(CommonIndexConstants.COMPONENT_ID, IndexUtils.longToPrefixCoded(conceptId));
			final Term conceptTypeTerm  = new Term(
					CommonIndexConstants.COMPONENT_TYPE, 
					IndexUtils.intToPrefixCoded(SnomedTerminologyComponentConstants.CONCEPT_NUMBER));
			
			final BooleanQuery query = new BooleanQuery(true);
			query.add(new TermQuery(conceptTypeTerm), Occur.MUST);
			query.add(new TermQuery(conceptIdTerm), Occur.MUST);
			
			final Collection<DocumentWithScore> documents = search(branchPath, query, null, null, 2);
			
			if (documents.size() > 1) {
				throw new IndexException("Cannot update document. Reason more than one documents were found for unique concept ID: '" + conceptId + "'.");
			}
			
			if (CompareUtils.isEmpty(documents)) {
				return; //nothing to do
			}
			
			//create first instance
			mappingStrategy = (AbstractIndexMappingStrategy) documentUpdater.updateDocument(Iterables.getOnlyElement(documents).getDocument());
			
			
		} else {
			//lookup cached one, apply update, cache new instance
			mappingStrategy = (AbstractIndexMappingStrategy) documentUpdater.updateDocument(mappingStrategy.createDocument());
			
		}
		
		//cache new mapping strategy instance
		documentCache.get(branchPath).put(conceptId, mappingStrategy);
		
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexUpdater#commit(com.b2international.snowowl.core.api.IBranchPath)
	 */
	@OverridingMethodsMustInvokeSuper
	@Override
	public void commit(final IBranchPath branchPath) {
		
		final LongKeyMap branchAwareDocumentCache = documentCache.get(branchPath);
		if (null != branchAwareDocumentCache) {
			
			
			final int nThreads = Runtime.getRuntime().availableProcessors();
			ExecutorService service = null;
			
			try {
				
				service = Executors.newFixedThreadPool(nThreads);
				@SuppressWarnings("unchecked") final Future<Void>[] futures = new Future[branchAwareDocumentCache.size()];
				
				int i = 0;
				for (final LongKeyMapIterator itr = branchAwareDocumentCache.entries(); itr.hasNext(); /*nothing*/) {
					
					itr.next();
					final AbstractIndexMappingStrategy strategy = (AbstractIndexMappingStrategy) itr.getValue();
					
					futures[i++] = service.submit(new Callable<Void>() {
						@Override public Void call() throws Exception {
							index(branchPath, strategy);
							return null; //void instance
						}
					});
					
				}
				
				if (null != service) {
					
					service.shutdown();
					service = null;
					
				}
				
				for (final Future<Void> future : futures) {
					future.get();
				}
				
				branchAwareDocumentCache.clear(); //clear up cache before commit.
				
			} catch (final InterruptedException e) {
					
					Thread.interrupted();
					throw new IndexException("Interrupted while committing changes to index.", e);
				
			} catch (final ExecutionException e) {
				
				throw new IndexException("Failed to commit changes to index.", e);
				
			} finally {
				
				if (null != service) {
					
					service.shutdown();
					service = null;
					
				}
				
			}
			
			
		}
		
		//perform the index commit
		super.commit(branchPath);
		
	}
	
}