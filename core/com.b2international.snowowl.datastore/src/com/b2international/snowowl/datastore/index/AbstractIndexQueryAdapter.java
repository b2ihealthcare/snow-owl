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
package com.b2international.snowowl.datastore.index;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.core.api.index.IIndexService;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;

public abstract class AbstractIndexQueryAdapter<E extends IIndexEntry> implements IIndexQueryAdapter<E> {

	private static final long serialVersionUID = -512253235971783678L;

	/** Special flag indicating that search flags are not supported */
	public static final int SEARCH_DEFAULT = 0;
	
	/** Special flag indicating that all options should be used when searching */
	public static final int SEARCH_EVERYTHING = Integer.MAX_VALUE;
	
	protected final String searchString;
	
	protected final int searchFlags;

	/**
	 * Utility method for constraining an arbitrary search flag value to a set of allowed bits.
	 * 
	 * @param searchFlag
	 * @param allowedValues
	 * @return
	 */
	protected static final int checkFlags(final int searchFlag, final int... allowedValues) {
		int mask = 0;
		
		for (final int allowedValue : allowedValues) {
			mask |= allowedValue;
		}
		
		return searchFlag & mask;
	}

	protected AbstractIndexQueryAdapter(final String searchString, final int searchFlags) {
		this.searchString = Strings.nullToEmpty(searchString);
		this.searchFlags = searchFlags;
	}

	/**
	 * @param mask the bitmask to check against
	 * @return {@code true} if any bit of the mask is set in {@link #searchFlags}, {@code false} otherwise
	 */
	protected final boolean anyFlagSet(final int mask) {
		return (searchFlags & mask) != 0;
	}

	/**
	 * @param mask the bitmask to check against
	 * @return {@code true} if all bits of the mask are set in {@link #searchFlags}, {@code false} otherwise
	 */
	protected final boolean allFlagsSet(final int mask) {
		return (searchFlags & mask) == mask;
	}

	/**
	 * Builds the result from the returning document.
	 * 
	 * @param doc the indexed document; cannot be {@code null}
	 * @param branchPath branch path where the document was looked up.
	 * @param score the score for the document
	 * 
	 * @return the converted {@link IIndexEntry}
	 */
	public abstract E buildSearchResult(final Document doc, final IBranchPath branchPath, float score);

	private final class EntryConverterFunction implements Function<DocumentWithScore, E> {
		
		private final IBranchPath branchPath;
		
		public EntryConverterFunction(final IBranchPath branchPath) {
			this.branchPath = branchPath;
		}

		@Override 
		public E apply(@Nullable final DocumentWithScore input) {
			return buildSearchResult(input.getDocument(), branchPath, input.getScore());
		}
	}

	@Override
	public final List<E> search(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		return search(indexService, branchPath, Integer.MAX_VALUE);
	}

	@Override
	public final List<E> search(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int limit) {
		final List<DocumentWithScore> documents = doSearch(indexService, branchPath, limit);
		return newArrayList(Collections2.transform(documents, new EntryConverterFunction(branchPath)));
	}
	
	@Override
	public final List<E> search(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int offset, final int limit) {
		final List<DocumentWithScore> documents = doSearch(indexService, branchPath, offset, limit);
		return newArrayList(Collections2.transform(documents, new EntryConverterFunction(branchPath)));
	}

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#searchIds(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath, int)
	 */
	@Override
	public List<String> searchIds(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int limit) {
		
		Preconditions.checkNotNull(indexService, "Index service argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		return doSearchIds(indexService, branchPath, limit);
	}
	
	@Override
	public Collection<E> searchUnsorted(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		
		Preconditions.checkNotNull(indexService, "Index service argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		
		return FluentIterable.from(doSearchUnsorted(indexService, branchPath))
				.transform(new EntryConverterFunction(branchPath))
				.toList();
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.core.api.index.IIndexQueryAdapter#searchUnsortedIds(com.b2international.snowowl.core.api.index.IIndexService, com.b2international.snowowl.core.api.IBranchPath)
	 */
	@Override
	public Collection<String> searchUnsortedIds(final IIndexService<? super E> indexService, final IBranchPath branchPath) {
		Preconditions.checkNotNull(indexService, "Index service argument cannot be null.");
		Preconditions.checkNotNull(branchPath, "Branch path argument cannot be null.");
		return doSearchUnsortedIds(indexService, branchPath);
	}
	
	protected abstract List<DocumentWithScore> doSearch(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int limit);
	
	protected abstract List<DocumentWithScore> doSearch(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int offset, final int limit);
	
	protected abstract List<String> doSearchIds(final IIndexService<? super E> indexService, final IBranchPath branchPath, final int limit);
	
	protected abstract Collection<DocumentWithScore> doSearchUnsorted(final IIndexService<? super E> indexService, final IBranchPath branchPath);
	
	protected abstract Collection<String> doSearchUnsortedIds(final IIndexService<? super E> indexService, final IBranchPath branchPath);
}