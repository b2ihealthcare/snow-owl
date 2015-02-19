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
package com.b2international.snowowl.datastore.cdo;

import javax.annotation.Nonnull;

import com.b2international.snowowl.datastore.CDOEditingContext;
import com.b2international.snowowl.datastore.exception.MergeFailedException;

/**
 * Representation of {@link CDOEditingContext editing context} merger.
 * @see NullMerger
 */
public interface ICDOEditingContextMerger<E extends CDOEditingContext> {
	
	/**
	 * Merges all the changes made on the <b>dirtyEditingContext</b> argument to a brand new 
	 * editing context specified as the <b>newEditingContext</b> argument.
	 * @param dirtyEditingContext the dirty editing context where the changes have been made. Cannot be {@code null}.
	 * @param newEditingContext the brand new editing context instance. Cannot be {@code null}. Cannot be {@link CDOEditingContext#isDirty() dirty}.
	 * @throws MergeFailedException if the merge failed due to stale references.
	 */
	void mergeEditingContext(@Nonnull final E dirtyEditingContext, @Nonnull final E newEditingContext) throws MergeFailedException;
	
	/**
	 * Null implementation of the {@link ICDOEditingContextMerger}. 
	 * @see ICDOEditingContextMerger
	 * @see #INSTANCE
	 */
	static final class NullMerger implements ICDOEditingContextMerger<CDOEditingContext> {

		/**
		 * The singleton *NULL* merger implementation. This implementation does nothing but disposes the old editing context.
		 * @return *NULL* implementation.
		 */
		@SuppressWarnings("unchecked")
		public static <E extends CDOEditingContext> ICDOEditingContextMerger<E> getInstance() {
			return (ICDOEditingContextMerger<E>) INSTANCE;
		}
		
		private NullMerger() { /*suppress instantiation*/ }
		
		private static final NullMerger INSTANCE = new NullMerger();
		
		/**
		 * This implementation does nothing but disposes the specified <b>dirtyEditingContext</b> argument.
		 * <p> 
		 * {@inheritDoc}
		 * @param dirtyEditingContext the dirty editing context will be disposed. Cannot be {@code null}.
		 * @param newEditingContext the new editing context. Will be ignored for this implementation. 
		 */
		@Override
		public void mergeEditingContext(final CDOEditingContext dirtyEditingContext, final CDOEditingContext newEditingContext) {
			dirtyEditingContext.close();
		}
		
	}
	
}