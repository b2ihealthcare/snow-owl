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
package com.b2international.snowowl.datastore.server;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.TopDocs;
import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IndexException;
import com.b2international.snowowl.datastore.index.AbstractIndexService;
import com.b2international.snowowl.datastore.index.mapping.Mappings;

/**
 * Abstract implementation of the {@link IEClassProvider} interface.
 * This class is responsible to return with the {@link EClass} of an object identified 
 * by it unique storage key. (CDO ID)
 * @see IEClassProvider
 */
public abstract class EClassProvider implements IEClassProvider {

	/**
	 * The default priority.
	 */
	private static final int PRIORITY = 999;

	/**
	 * May return with {@code null}.
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	@Nullable public EClass getEClass(final IBranchPath branchPath, final long storageKey) {
		
		final ReferenceManager<IndexSearcher> manager = getServerService().getManager(branchPath);
		IndexSearcher searcher = null;
		
		try {
			searcher = manager.acquire();
			final TopDocs topDocs = searcher.search(getQuery(storageKey), 1);
			
			if (CompareUtils.isEmpty(topDocs.scoreDocs)) {
				return null;
			}
			
			final Document doc = searcher.doc(topDocs.scoreDocs[0].doc, getFieldsToLoad());
			return extractEClass(doc, storageKey);
		} catch (final IOException e) {
			throw new IndexException("Error while getting EClass of a component. Storage key: " + storageKey, e);
		} finally {
			if (searcher != null) {
				try {
					manager.release(searcher);
				} catch (IOException e) {
					throw new IndexException(e);
				}
			}
		}
		
	}
	
	@Override
	public int getPriority() {
		return PRIORITY;
	}

	/**
	 * Extracts the {@link EClass} information from the {@link Document}.
	 * @param doc the document as the outcome of an index query.
	 * @return the {@link EClass}
	 */
	protected abstract EClass extractEClass(final Document doc);
	
	protected EClass extractEClass(final Document doc, long storageKey) {
		return extractEClass(doc);
	}

	
	/**
	 * Returns with a set of {@link IndexableField} name that has to be loaded.
	 * @return a set of field names to load.
	 */
	protected Set<String> getFieldsToLoad() {
		return Mappings.fieldsToLoad().type().build();
	}
	
	/**
	 * Returns with the query that will be performed to find {@link EClass} based on unique storage key.
	 * @return the query.
	 */
	private Query getQuery(final long storageKey) {
		return Mappings.newQuery().storageKey(storageKey).matchAll();
	}

	/**
	 * Returns with the server side index service.
	 * @return the index service.
	 */
	protected abstract AbstractIndexService<?> getServerService();
	
}