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

/**
 * A {@link Lock} implementation which delegates actual work to another lock.
 * 
 */
public class DelegatingLock extends Lock {

	private final Lock delegate;

	public DelegatingLock(final Lock delegate) {
		if (null == delegate) {
			throw new NullPointerException("Lock delegate may not be null.");
		}
		
		this.delegate = delegate;
	}
	
	protected Lock getDelegate() {
		return delegate;
	}

	@Override
	public boolean obtain() throws IOException {
		return delegate.obtain();
	}
	
	@Override
	public boolean isLocked() throws IOException {
		return delegate.isLocked();
	}
	
	@Override
	public void close() throws IOException {
		delegate.close();
	}
}