/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Set;

import com.b2international.snowowl.datastore.exception.RepositoryLockException;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

/**
 * Filtering error logging strategy. May suspend error logging or may delegate into {@link OmErrorLoggingStrategy}.
 */
public enum FilteringErrorLoggingStrategy implements IErrorLoggingStrategy {

	/**Shared instance*/
	INSTANCE;
	
	private static final String CONCURRENT_REVISION_MODIFICATION_EX_MSG = "Attempt by";

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.server.IErrorLoggingStrategy#logError(java.lang.Throwable)
	 */
	@Override
	public void logError(final Throwable t) {
		if (isIgnoredException(t)) {
			OmErrorLoggingStrategy.INSTANCE.logError(t.getMessage());
		} else {
			OmErrorLoggingStrategy.INSTANCE.logError(t);
		}
	}

	private boolean isIgnoredException(final Throwable t) {
		return null != t && Predicates.or(ignoredExceptionsCache.get()).apply(t);
	}

	// TODO extract this from extension points.
	private final Supplier<Set<Predicate<Throwable>>> ignoredExceptionsCache = Suppliers.memoize(new Supplier<Set<Predicate<Throwable>>>() {
		@Override public Set<Predicate<Throwable>> get() {
			final Set<Predicate<Throwable>> predicates = Sets.newHashSet();
			predicates.add(new Predicate<Throwable>() {	@Override public boolean apply(Throwable input) {
				return input instanceof RepositoryLockException;
			}});
			predicates.add(new Predicate<Throwable>() {	@Override public boolean apply(Throwable input) {
				return input instanceof ConcurrentModificationException && input.getMessage() != null && input.getMessage().startsWith(CONCURRENT_REVISION_MODIFICATION_EX_MSG);
			}});
			return Collections.unmodifiableSet(predicates);
		}
	});
	
}