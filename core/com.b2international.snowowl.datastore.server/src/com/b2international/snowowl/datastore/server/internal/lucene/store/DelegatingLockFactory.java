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
package com.b2international.snowowl.datastore.server.internal.lucene.store;

import java.io.IOException;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;

/**
 * A {@link LockFactory} implementation which delegates actual work to another lock.
 * 
 */
public class DelegatingLockFactory extends LockFactory {

	private final LockFactory delegate;

	public DelegatingLockFactory(final LockFactory delegate) {
		if (null == delegate) {
			throw new NullPointerException("Lock factory delegate may not be null.");
		}
		
		this.delegate = delegate;
	}
	
	protected LockFactory getDelegate() {
		return delegate;
	}

	@Override
	public Lock makeLock(String lockName) {
		return delegate.makeLock(lockName);
	}

	@Override
	public void clearLock(String lockName) throws IOException {
		delegate.clearLock(lockName);
	}
}