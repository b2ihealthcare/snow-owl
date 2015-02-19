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

import static com.b2international.snowowl.datastore.server.internal.lucene.store.LoggingLockFactory.log;

import java.io.IOException;

import org.apache.lucene.store.Lock;

/**
 * A {@link Lock} implementation which logs all invoked operations.
 * 
 */
public class LoggingLock extends DelegatingLock {

	public LoggingLock(final Lock delegate) {
		super(delegate);
	}

	@Override
	public boolean obtain() throws IOException {
		final boolean result = super.obtain();
		
		if (result) {
			log("Lock obtained: %s", getDelegate());
		} else {
			log("Lock not obtained: %s", getDelegate());
		}
		
		return result;
	}

	@Override
	public boolean isLocked() throws IOException {
		final boolean result = super.isLocked();
		log("Is locked check returns with %b: %s", result, getDelegate());
		return result;
	}
	
}